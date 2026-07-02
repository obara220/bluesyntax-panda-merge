package com.panda.merge.dubbo;

import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.api.IStandardStatusApi;
import com.panda.merge.common.enums.*;
import com.panda.merge.common.utils.IdWorker;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.config.ThreadPoolConfig;
import com.panda.merge.dto.MatchEventInfoDTO;
import com.panda.merge.dto.MatchStatisticsInfoDTO;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.model.*;
import com.panda.merge.rocketmq.processor.MatchEventInfoProcessor;
import com.panda.merge.rocketmq.processor.MatchStatisticsInfoProcessor;
import com.panda.merge.rocketmq.processor.ThirdMatchStatusProcessor;
import com.panda.merge.rocketmq.producer.MatchSaleOverProducer;
import com.panda.merge.rocketmq.producer.StandardMatchStatusProducer;
import com.panda.merge.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static com.panda.merge.common.enums.MatchPeriodForMatchOverEnum.Ended999;
import static com.panda.merge.constant.ConstantSystem.*;

@Slf4j
@Component
@DubboService
public class ThirdMatchStatusApiImpl implements IStandardStatusApi {

    @Autowired
    public ThirdSportTypeService thirdSportTypeService;
    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;
    @Autowired
    private StandardMatchInfoService standardMatchInfoService;
    @Autowired
    ThirdMatchStatusProcessor thirdMatchStatusProcessor;
    @Autowired
    private StandardSportMarketSellService standardSportMarketSellService;
    @Autowired
    private MatchSaleOverProducer matchSaleOverProducer;
    @Autowired
    public RedisService redisService;
    @Autowired
    private StandardSportTournamentService standardSportTournamentService;
    @Autowired
    private StandardMatchStatusProducer standardMatchStatusProducer;

    @Override
    public Response updataMatchStatus(Long standardId, String matchManageId, Integer status,Long periodId) {
        return updataMatchStatusByUpdataUser(standardId, matchManageId, status, periodId, "未知操作人");
    }

    @Override
    public Response updataMatchStatusPls(Long plsStandardMatchId, String matchManageId, Integer status, Long periodId) {
        if (plsStandardMatchId == null || plsStandardMatchId == 0 ) {
            return Response.failed("请输入PLS标准赛事ID");
        }
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItemByPlsStandardMatchId(plsStandardMatchId);
        if (standardMatchInfo == null) {
            return Response.failed(String.format("PLS标准赛事id=%d,管理id=%s,标准赛事不存在", plsStandardMatchId,matchManageId));
        }
        return updataMatchStatusByUpdataUser(standardMatchInfo.getId(),matchManageId,status,periodId,"未知操作人");
    }

    @Override
    public Response updataMatchStatusByUpdataUser(Long standardId, String matchManageId, Integer status, Long periodId,String updataUser){
        String linkId = String.format("%s_realtime", System.currentTimeMillis());
        log.info("linkId=【{}】updataMatchStatus,手动更新赛事状态开始, 标准赛事ID={}，赛事管理ID={}，更新状态={}，更新阶段={}，操作人={}", linkId,standardId,matchManageId,status,periodId,updataUser);
        //校验参数
        if ((standardId == null||standardId == 0 ) && !StringUtils.isNotBlank(matchManageId) ) {
            return Response.failed("请输入赛事id或者管理id");
        }
        //目前支持切换未开赛,滚球,完赛,结束,取消,放弃,中断
        List<Integer> statusList = Arrays.asList(MatchStatusEnum.Ended.value,MatchStatusEnum.Cancelled.value,
                MatchStatusEnum.Closed.value,MatchStatusEnum.Live.value,MatchStatusEnum.Not_Started.value
                ,MatchStatusEnum.Abandoned.value,MatchStatusEnum.Interrupted.value);
        if (!statusList.contains(status) ) {
            return Response.failed("赛事状态不支持切换!");
        }

        //1.查询标准赛事
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItemByPrimaryKey(standardId);
        if (null == standardMatchInfo  && StringUtils.isNotBlank(matchManageId)) {
            standardMatchInfo = standardMatchInfoService.getItemByMatchManageId(matchManageId);

        }
        if (standardMatchInfo == null) {
            return Response.failed(String.format("标准赛事id=%d,管理id=%s,标准赛事不存在", standardId,matchManageId));
        }
        //直接查询数据库中的开售信息
        StandardSportMarketSell standardSportMarketSell = standardSportMarketSellService.refreshCache(standardMatchInfo.getId());
        StandardMatchInfo upMatchInfo = new StandardMatchInfo();
        upMatchInfo.setId(standardMatchInfo.getId());
        upMatchInfo.setMatchStatus(status);
        //2.根据状态修改"是否结束"和"赛事阶段"和"预开售状态"
        if (MatchStatusEnum.Ended.value.equals(status)) {
            upMatchInfo.setMatchOver(YesNoEnum.Y.value);
            upMatchInfo.setMatchPeriodId(MatchPeriodForMatchOverEnum.Ended999.value);
            standardSportMarketSell.setStatus(PreSaleMatchStatusEnum.End.toString());
        }
        if (MatchStatusEnum.Closed.value.equals(status) || MatchStatusEnum.Cancelled.value.equals(status)) {
            upMatchInfo.setMatchOver(YesNoEnum.Y.value);
            upMatchInfo.setMatchPeriodId(Ended999.value);
            standardSportMarketSell.setStatus(PreSaleMatchStatusEnum.End.toString());
        }
        if (MatchStatusEnum.Live.value.equals(status)) {
            upMatchInfo.setMatchOver(YesNoEnum.N.value);
            //改为滚球状态 传入阶段
            if(null == periodId){
                //如果当前阶段是999,则设置为未开始，避免999不能回退问题
                if(Ended999.value.equals(standardMatchInfo.getMatchPeriodId())){
                    upMatchInfo.setMatchPeriodId(MatchPeriodForMatchOverEnum.NOT_STARTED.value);
                }
            }else{
                upMatchInfo.setMatchPeriodId(periodId);
            }
            standardSportMarketSell.setStatus(PreSaleMatchStatusEnum.Enable.toString());
        }
        if (MatchStatusEnum.Not_Started.value.equals(status)) {
            upMatchInfo.setMatchOver(YesNoEnum.N.value);
            upMatchInfo.setMatchPeriodId(MatchPeriodForMatchOverEnum.NOT_STARTED.value);
            standardSportMarketSell.setStatus(PreSaleMatchStatusEnum.Enable.toString());
        }
        //【49152bug优化】，记录手动操作标准赛事是否出现过中断或是取消状态
        if(MatchStatusEnum.Cancelled.value.equals(status) || MatchStatusEnum.Interrupted.value.equals(status)){
            upMatchInfo.setInterruptionCancellationStatus(YesNoEnum.Y.value);
        }
        //3.更新标准赛事
        standardMatchInfo = standardMatchInfoService.updateByPrimaryKeySelective(upMatchInfo);

        //4.查询三方赛事
        List<ThirdMatchInfo> thirdMatchInfo = null;
        if (standardMatchInfo.getId() != null) {
            thirdMatchInfo = thirdMatchInfoService.getItems(standardMatchInfo.getId());
        }
        if (null == thirdMatchInfo) {
            return Response.failed(String.format("::三方赛事id=%d,标准更新成功,三方赛事不存在", standardId));
        }
        List<String> keyList =new ArrayList<>();
        //5.更新三方赛事
        thirdMatchInfo.forEach(item -> {
            item.setMatchStatus(upMatchInfo.getMatchStatus());
            if(null != upMatchInfo.getMatchPeriodId()){
                item.setMatchPeriod(String.valueOf(upMatchInfo.getMatchPeriodId()));
            }
            if(null != upMatchInfo.getMatchOver()){
                item.setMatchOver(upMatchInfo.getMatchOver());
            }
            if(null != upMatchInfo.getInterruptionCancellationStatus()){
                item.setInterruptionCancellationStatus(upMatchInfo.getInterruptionCancellationStatus());
            }
            thirdMatchInfoService.updateByPrimaryKeySelective(item,linkId);
            keyList.add(RedisConfig.REDIS_KEY_DATABASE + "::ThirdMatchInfo:" + item.getId());
            keyList.add(RedisConfig.REDIS_KEY_DATABASE + "::ThirdMatchInfo:" + item.getDataSourceCode()+'-'+item.getThirdMatchSourceId());
            keyList.add(RedisConfig.REDIS_KEY_DATABASE + "::ThirdMatchInfo:" + item.getReferenceId() +  '-' +item.getDataSourceCode());
            keyList.add(RedisConfig.REDIS_KEY_DATABASE+ "::StandardMatchInfo:" + item.getReferenceId());
        });

        //6 .获取开售信息
        if (standardSportMarketSell == null) {
            return Response.failed(String.format("::标准开售id=%d,开售信息不存在", standardId));
        }
        //7.清理缓存
        redisService.del(keyList);

        //标准赛事已经完赛特殊处理逻辑
        if(YesNoEnum.Y.value.equals(upMatchInfo.getMatchOver())){
            thirdMatchStatusProcessor.cacheMatchOver( upMatchInfo.getId());
            if(null != standardSportMarketSell){
                //通知预售开售 赛事完赛消息
                matchSaleOverProducer.sendMatchSaleOverMessage(linkId, upMatchInfo,updataUser);
            }
        }
        //8. 下发赛事状态给业务
        thirdMatchStatusProcessor.pushMatchStatusInfo(linkId, standardMatchInfo, standardSportMarketSell,
                standardMatchInfo.getDataSourceCode(), Calendar.getInstance().getTimeInMillis());

        //9. 下发开售给风控
        //pushMatchSellInfo(standardSportMarketSell,linkId);

        //清除已经自动关盘的玩法
        String autoCloseRedisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_AUTO_CLOSE + standardMatchInfo.getId());
        redisService.del(autoCloseRedisKey);

        log.info("linkId=【{}】updataMatchStatus,手动更新赛事状态完毕,标准赛事ID={}, 更新状态={}, 更新阶段={}", linkId,upMatchInfo.getId(), upMatchInfo.getMatchStatus(),upMatchInfo.getMatchPeriodId());

        return Response.success("手动更新赛事状态完毕! linkId: "+ linkId);
    }


    /**
     * 紧急修改赛事开售状态
     *
     * @param standardId    标准赛事id
     * @param matchManageId 赛事管理id
     * @param status        需要更改的状态
     * @return
     */
    @Override
    public Response updataMatchSellStatus(Long standardId, String matchManageId, Integer status) {
        return null;
    }



    /**
     *  dubbo事件开关（false:关，true：开）
     * */
    @NacosValue(value = "${panda.dubbo.event.switch:true}", autoRefreshed = true)
    private boolean dubboEventSwitch;

    @Autowired
    private ThreadPoolConfig threadPoolConfig;

    @Autowired
    private MatchEventInfoProcessor matchEventInfoProcessor;

    /**
     * 三方赛事事件接入API
     * @return
     */
    @Override
    public Response thirdMatchEventInfoApi(Request<MatchEventInfoDTO> request){
        log.info("linkId=【{}】thirdMatchEventInfoApi，dubbo事件信息处理开始,dubboEventSwitch={}", request.getLinkId(),dubboEventSwitch);
        if(dubboEventSwitch){
            //异步通知事件
            TaskExecutor taskExecutor = threadPoolConfig.getEventInfoThreadPool();
            taskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    matchEventInfoProcessor.putMatchEventInfo(request);
                }
            });
            log.info("linkId=【{}】thirdMatchEventInfoApi，dubbo事件信息处理结束", request.getLinkId());
        }
        return Response.success();
    }

    @Autowired
    private MatchStatisticsInfoProcessor matchStatisticsInfoProcessor;

    /**
     * 三方赛事统计接入API
     * @return
     */
    @Override
    public Response thirdMatchStatisticsInfoApi(Request<MatchStatisticsInfoDTO> request){
//        return matchStatisticsInfoProcessor.putMatchStatisticsInfo(request);
        return null;
    }

    @Autowired
    private SystemTypeDictService systemTypeDictService;

    /**
     * 手动更新修改3795需求事件缓存秒数(改用 setZ01AnimationEventCacheSeconds方法)
     * */
    @Override
    @Deprecated
    public Response updataCacheSeconds3795(Request<Integer> request) {
        String linkId = request.getLinkId();
        try{
            if(StringUtils.isBlank(linkId)){
                linkId = String.format("%s_realtime", IdWorker.get32UUID().toLowerCase());
            }
            log.info("linkId=【{}】updataCacheSeconds3795,手动更新修改3795需求事件缓存秒数开始, request={}", linkId, JSON.toJSONString(request));
            SystemTypeDict item = new SystemTypeDict();
            item.setId(999L);
            item.setSportId(0l);
            item.setCode("Z01AnimationEventCacheSeconds");
            item.setValue("手动更新-自研动画Z01各赛种事件缓存秒数");
            item.setActive(1);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("seconds",request.getData());
            item.setDescription(jsonObject.toJSONString());
            systemTypeDictService.updataSystemTypeDict(item);
        }catch (Exception e){
            log.info("linkId=【"+linkId+"】updataCacheSeconds3795,手动更新修改3795需求事件缓存秒数异常,Exception:", e);
            return new Response().failed();
        }finally {
            log.info("linkId=【{}】updataCacheSeconds3795,手动更新修改3795需求事件缓存秒数结束", linkId);
        }
        return new Response().success();
    }

    /**
     * 手动更新-自研动画Z01各赛种事件缓存秒数
     * 足球：需求3795
     * 篮球：需求3833
     * */
    @Override
    public Response setZ01AnimationEventCacheSeconds(Request<String> request) {
        String linkId = request.getLinkId();
        if (StringUtils.isBlank(linkId)) {
            linkId = String.format("%s_realtime", IdWorker.get32UUID().toLowerCase());
        }
        log.info("linkId=[{}] setZ01AnimationEventCacheSeconds 开始, request={}", linkId, request);
        try {
            SystemTypeDict item = new SystemTypeDict();
            item.setId(999L);
            item.setSportId(0L);
            item.setCode("Z01AnimationEventCacheSeconds");
            item.setValue("手动更新-自动动画Z01各类事件缓存秒数");
            item.setActive(1);

            //库中现有数据
            JSONObject jsonItemDB = new JSONObject();
            SystemTypeDict itemDB = systemTypeDictService.getItemById(item.getId());
            if (itemDB != null) {
                jsonItemDB = JSON.parseObject(itemDB.getDescription());
            }
            //本次传入的数据
            JSONObject newJsonItem = new JSONObject();
            JSONObject data = JSON.parseObject(request.getData());
            if (data != null) {
                newJsonItem.putAll(data);
            }
            //合并数据
            jsonItemDB.putAll(newJsonItem);

            item.setDescription(jsonItemDB.toJSONString());
            systemTypeDictService.updataSystemTypeDict(item);
        } catch (Exception e) {
            log.error("linkId=[{}] setZ01AnimationEventCacheSeconds 异常", linkId, e);
            return new Response<>().failed();
        } finally {
            log.info("linkId=[{}] setZ01AnimationEventCacheSeconds 结束", linkId);
        }
        return new Response<>().success();
    }


    @Autowired
    private ThirdMatchHistoryStatisticsService thirdMatchHistoryStatisticsService;

    @Autowired
    private ThirdMatchLineupService thirdMatchLineupService;

    @Autowired
    private ThirdMatchSidelinedService thirdMatchSidelinedService;

    @Autowired
    private ThirdMatchExInfomationService thirdMatchExInfomationService;

    @Autowired
    private ThirdMatchFrontStatisticsService thirdMatchFrontStatisticsService;

    @Autowired
    private ThirdSportTeamRankingService thirdSportTeamRankingService;

    @Autowired
    private ThirdSportPlayerRankingService thirdSportPlayerRankingService;

    @Autowired
    private ThirdMatchHistoryExpressionService thirdMatchHistoryExpressionService;

    @Autowired
    private ThirdMatchSeasonStatisticsService thirdMatchSeasonStatisticsService;
    @Autowired
    private ThirdMatchTeamSkillStatisticsService thirdMatchTeamSkillStatisticsService;
    @Autowired
    private ThirdMatchPromotionChartService thirdMatchPromotionChartService;

    /**
     * 手动更新赛事分析相关表对应数据的修改时间，方便下游同步
     * @param request
     *    data数据结构 = {“tableName”:“表名”,“sportId”:“标准赛种ID”,thirdMatchSourceId”:“源赛事ID”,“thirdTournamentSourceId”:“源联赛ID”,“thirdSourceSeasonId”:“源赛季ID”,“dataSourceCode”:“数据源编码”}
     * */
    @Override
    public Response updataMatchAnalysisModifyTime(Request<String> request) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Response<Object> response = new Response().success();
        String linkId = request.getLinkId();
        try{
            if(StringUtils.isBlank(linkId)){
                linkId = String.format("%s_realtime", IdWorker.get32UUID().toLowerCase());
            }
            log.info("linkId=【{}】updataMatchAnalysisModifyTime,手动更新赛事分析修改时间开始, request={}", linkId, JSON.toJSONString(request));
            String data = request.getData();
            if(StringUtils.isBlank(data)){
                return new Response().failed("传入参数不能为空！");
            }
            JSONObject jsonObject = JSON.parseObject(data);
            //表名
            String tableName = jsonObject.getString("tableName");
            //标准赛种ID
            Long sportId = jsonObject.getLong("sportId");
            //源赛事ID
            String thirdMatchSourceId = jsonObject.getString("thirdMatchSourceId");
            //数据源编码
            String dataSourceCode = jsonObject.getString("dataSourceCode");
            //源联赛ID
            String thirdTournamentSourceId = jsonObject.getString("thirdTournamentSourceId");
            //源赛季ID
            String thirdSourceSeasonId = jsonObject.getString("thirdSourceSeasonId");
            switch (tableName) {
                //修改赛事历史统计分析
                case THIRD_MATCH_HISTORY_STATISTICS_API:
                    //根据联赛，赛季修改
                    if(StringUtils.isNotBlank(thirdTournamentSourceId) && StringUtils.isNotBlank(thirdSourceSeasonId) && StringUtils.isNotBlank(dataSourceCode)){
                        ThirdMatchHistoryStatisticsExample example = new ThirdMatchHistoryStatisticsExample();
                        example.createCriteria().andDataSourceCodeEqualTo(dataSourceCode).andThirdTournamentSourceIdEqualTo(thirdTournamentSourceId).andThirdSeasonSourceIdEqualTo(thirdSourceSeasonId);
                        int num = thirdMatchHistoryStatisticsService.updateModifyTimeByExampleSelective(System.currentTimeMillis(),example);
                        response.setData(num);
                    }
                    //根据赛事ID修改
                    if(StringUtils.isNotBlank(dataSourceCode) && StringUtils.isNotBlank(thirdMatchSourceId)){
                        ThirdMatchHistoryStatisticsExample example = new ThirdMatchHistoryStatisticsExample();
                        example.createCriteria().andDataSourceCodeEqualTo(dataSourceCode).andThirdMatchSourceIdEqualTo(thirdMatchSourceId);
                        int num = thirdMatchHistoryStatisticsService.updateModifyTimeByExampleSelective(System.currentTimeMillis(),example);
                        response.setData(num);
                    }
                    break;
                //修改赛事阵容数据
                case THIRD_MATCH_LINEUP_API:
                    if(StringUtils.isNotBlank(dataSourceCode) && StringUtils.isNotBlank(thirdMatchSourceId)){
                        ThirdMatchLineupExample example = new ThirdMatchLineupExample();
                        example.createCriteria().andDataSourceCodeEqualTo(dataSourceCode).andThirdMatchSourceIdEqualTo(thirdMatchSourceId);
                        int num = thirdMatchLineupService.updateModifyTimeByExampleSelective(System.currentTimeMillis(),example);
                    }
                    break;
                //修改赛事球员伤停
                case THIRD_MATCH_SIDELINED_API:
                    if(StringUtils.isNotBlank(dataSourceCode) && StringUtils.isNotBlank(thirdMatchSourceId)){
                        ThirdMatchSidelinedExample example = new ThirdMatchSidelinedExample();
                        example.createCriteria().andDataSourceCodeEqualTo(dataSourceCode).andThirdMatchSourceIdEqualTo(thirdMatchSourceId);
                        int num = thirdMatchSidelinedService.updateModifyTimeByExampleSelective(System.currentTimeMillis(),example);
                        response.setData(num);
                    }
                    break;
                //修改赛事情报综合资讯
                case THIRD_MATCH_EX_INFOMATION_API:
                    if(StringUtils.isNotBlank(dataSourceCode) && StringUtils.isNotBlank(thirdMatchSourceId)){
                        ThirdMatchExInfomationExample example = new ThirdMatchExInfomationExample();
                        example.createCriteria().andDataSourceCodeEqualTo(dataSourceCode).andThirdMatchSourceIdEqualTo(thirdMatchSourceId);
                        int num = thirdMatchExInfomationService.updateModifyTimeByExampleSelective(System.currentTimeMillis(),example);
                        response.setData(num);
                    }
                    break;
                //修改赛事正面交手统计
                case THIRD_MATCH_FRONT_STATISTICS_API:
                    if(StringUtils.isNotBlank(dataSourceCode) && StringUtils.isNotBlank(thirdMatchSourceId)){
                        ThirdMatchFrontStatisticsExample example = new ThirdMatchFrontStatisticsExample();
                        example.createCriteria().andDataSourceCodeEqualTo(dataSourceCode).andThirdMatchSourceIdEqualTo(thirdMatchSourceId);
                        int num = thirdMatchFrontStatisticsService.updateModifyTimeByExampleSelective(System.currentTimeMillis(),example);
                        response.setData(num);
                    }
                    break;
                //修改联赛球队榜单
                case THIRD_SPORT_TEAM_RANKING_API:
                    if(StringUtils.isNotBlank(thirdTournamentSourceId) && StringUtils.isNotBlank(thirdSourceSeasonId) && StringUtils.isNotBlank(dataSourceCode)){
                        ThirdSportTeamRankingExample example = new ThirdSportTeamRankingExample();
                        example.createCriteria().andDataSourceCodeEqualTo(dataSourceCode).andThirdTournamentSourceIdEqualTo(thirdTournamentSourceId)
                                .andThirdSourceSeasonIdEqualTo(thirdSourceSeasonId);
                        int num = thirdSportTeamRankingService.updateModifyTimeByExampleSelective(System.currentTimeMillis(),example);
                        response.setData(num);
                    }
                    break;
                //修改联赛球员榜单
                case THIRD_SPORT_PAYER_RANKING_API:
                    if(StringUtils.isNotBlank(thirdTournamentSourceId) && StringUtils.isNotBlank(thirdSourceSeasonId)){
                        ThirdSportPlayerRankingExample example = new ThirdSportPlayerRankingExample();
                        example.createCriteria().andThirdTournamentSourceIdEqualTo(thirdTournamentSourceId).andThirdSourceSeasonIdEqualTo(thirdSourceSeasonId);
                        int num = thirdSportPlayerRankingService.updateModifyTimeByExampleSelective(System.currentTimeMillis(),example);
                        response.setData(num);
                    }
                    break;
                //修改赛球队历史表现
                case THIRD_MATCH_HISTORY_EXPRESSION_API:
                    if(StringUtils.isNotBlank(dataSourceCode) && StringUtils.isNotBlank(thirdTournamentSourceId)){
                        ThirdMatchHistoryExpressionExample example = new ThirdMatchHistoryExpressionExample();
                        example.createCriteria().andDataSourceCodeEqualTo(dataSourceCode).andThirdTournamentSourceIdEqualTo(thirdTournamentSourceId);
                        int num = thirdMatchHistoryExpressionService.updateModifyTimeByExampleSelective(System.currentTimeMillis(),example);
                        response.setData(num);
                    }
                    break;
                //赛事球队技术统计
                case THIRD_MATCH_TEAM_SKILL_STATISTICS_API:
                    if(StringUtils.isNotBlank(dataSourceCode) && StringUtils.isNotBlank(thirdMatchSourceId)){
                        ThirdMatchTeamSkillStatisticsExample example = new ThirdMatchTeamSkillStatisticsExample();
                        example.createCriteria().andDataSourceCodeEqualTo(dataSourceCode).andMatchIdEqualTo(thirdMatchSourceId);
                        int num = thirdMatchTeamSkillStatisticsService.updateModifyTimeByExampleSelective(System.currentTimeMillis(),example);
                        response.setData(num);
                    }
                    break;
                //杯赛淘汰赛事
                case THIRD_MATCH_PROMOTION_CHART_API:
                    if(StringUtils.isNotBlank(dataSourceCode) && StringUtils.isNotBlank(thirdSourceSeasonId) && StringUtils.isNotBlank(thirdTournamentSourceId)){
                        ThirdMatchPromotionChartExample example = new ThirdMatchPromotionChartExample();
                        example.createCriteria().andDataSourceCodeEqualTo(dataSourceCode).andSeasonIdEqualTo(thirdSourceSeasonId).andTournamentIdEqualTo(thirdTournamentSourceId);
                        int num = thirdMatchPromotionChartService.updateModifyTimeByExampleSelective(System.currentTimeMillis(),example);
                        response.setData(num);
                    }
                    break;
                //修改当前赛季统计信息
                case THIRD_MATCH_SEASON_STATISTICS_API:
                    if(StringUtils.isNotBlank(dataSourceCode) && StringUtils.isNotBlank(thirdSourceSeasonId) && StringUtils.isNotBlank(thirdTournamentSourceId)){
                        ThirdMatchSeasonStatisticsExample example = new ThirdMatchSeasonStatisticsExample();
                        example.createCriteria().andDataSourceCodeEqualTo(dataSourceCode).andThirdTournamentSourceIdEqualTo(thirdTournamentSourceId)
                                .andThirdSourceSeasonIdEqualTo(thirdSourceSeasonId);
                        int num = thirdMatchSeasonStatisticsService.updateModifyTimeByExampleSelective(System.currentTimeMillis(),example);
                        response.setData(num);
                    }
                    break;
                case "RedisGet":
                    if(redisService.hasKey(dataSourceCode)){
                        response.setMsg(JSON.toJSONString(redisService.get(dataSourceCode)));
                    }
                    break;
                case "RedisGetAll":
                    if(redisService.hasKey(dataSourceCode)){
                        response.setMsg(JSON.toJSONString(redisService.hGetAll(dataSourceCode)));
                    }
                    break;
                case "RedisDelete":
                    if(redisService.hasKey(dataSourceCode)){
                        response.setMsg(redisService.del(dataSourceCode)+"");
                    }
                    break;
                default:
                    log.info("linkId=【{}】updataMatchAnalysisModifyTime,default方法,请检查入参！");
                    break;
            }
        }catch (Exception e){
            log.info("linkId=【"+linkId+"】updataMatchAnalysisModifyTime,手动更新赛事分析修改时间异常,Exception:", e);
            return new Response().failed("实时服务业务处理异常,请联系对应开发人员！");
        }finally {
            stopWatch.stop();
            log.info("linkId=【{}】updataMatchAnalysisModifyTime,手动更新赛事分析修改时间结束,共耗时=,返回结果={}{}", linkId,stopWatch.getTotalTimeMillis(),JSON.toJSONString(response));
        }
        return response;
    }

}
