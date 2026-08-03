package com.panda.merge.rocketmq.processor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.common.collect.Lists;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.*;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.constant.ConstantSystem;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.ResultCode;
import com.panda.merge.dto.ThirdMatchStatusDTO;
import com.panda.merge.dto.message.StandardMatchStatusMessage;
import com.panda.merge.exception.ExceptionHelper;
import com.panda.merge.mapper.StandardRelationNewStandardMapper;
import com.panda.merge.mapper.ThirdRelationNewThirdMapper;
import com.panda.merge.model.*;
import com.panda.merge.rocketmq.producer.MatchSaleOverProducer;
import com.panda.merge.rocketmq.producer.PaDataServiceLogProducer;
import com.panda.merge.rocketmq.producer.RealtimeBaseProduecr;
import com.panda.merge.rocketmq.producer.StandardMatchStatusProducer;
import com.panda.merge.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * @author : bevan
 * @project Name : panda-merge
 * @package Name : com.panda.merge.rocketmq.processor
 * @date: 2020-09-10 16:21
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */
@Component
@Slf4j
@Validated
public class ThirdMatchStatusProcessor extends BaseProcessor {

    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;
    @Autowired
    private StandardSportMarketSellService standardSportMarketSellService;
    @Autowired
    private StandardMatchInfoService standardMatchInfoService;
    @Autowired
    private MatchSaleOverProducer matchSaleOverProducer;
    @Autowired
    private StandardMatchStatusProducer standardMatchStatusProducer;
    @Autowired
    private SystemTypeDictService systemTypeDictService;
    @Autowired
    private PaDataServiceLogProducer paDataServiceLogProducer;

    @Autowired
    public RealtimeBaseProduecr realtimeBaseProduecr;
    @Autowired
    private StandardSportTournamentService standardSportTournamentService;
    @Autowired
    private MatchEventInfoService matchEventInfoService;

    /**
     * 拷贝赛事开关（false:关，true：开）
     */
    @NacosValue(value = "${copy.match.switch:false}", autoRefreshed = true)
    private boolean copyMatchSwitch;

    /**
     * 4248 【赛程】赛事中断场景优化开关（false:关，true：开）
     */
    @NacosValue(value = "${panda.interrupted.event.switch:true}", autoRefreshed = true)
    private boolean interruptedEventSwitch;

    @ExceptionHelper
    public void putMatchStatus(@Valid Request<ThirdMatchStatusDTO> request) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Response response = Response.success();
        try {
            log.info("linkId=【{}】putMatchStatus,赛事状态信息={}", request.getLinkId(), JSON.toJSONString(request));
            ThirdMatchStatusDTO thirdMatchStatusDTO = request.getData();
            //获取标准运动类型
            final Long sportId = validateSportId(thirdMatchStatusDTO.getDataSourceCode(), String.valueOf(thirdMatchStatusDTO.getSportId()));
            processorMatchStatus(request, sportId);

            //拷贝的赛事向MQ投递赛事状态消息
            if (copyMatchSwitch) {
                copyStandardMatchInfoStatus2Mq(request);
            }
        } catch (Exception e) {
            response.setCode(ResultCode.FAILED.getCode());
            response.setMsg(e.getMessage());
            throw e;
        } finally {
            stopWatch.stop();
            response.setDataSourceTime(stopWatch.getTotalTimeMillis());
            //统计处理耗时
            paDataServiceLogProducer.sendPaDataServiceLog(
                    getPaDataServiceLogDTO(request.getLinkId(), realtime, THIRD_MATCH_STATUS_API, "三方赛事状态信息接入",
                            stopWatch.getTotalTimeMillis(), Integer.parseInt(String.valueOf(response.getCode())), response.getMsg())
            );
            log.info("linkId=【{}】putMatchStatus, 赛事状态处理结束，共耗时={}", request.getLinkId(), JSON.toJSONString(response));
        }
    }

    /**
     * 赛事状态处理逻辑
     */
    public void processorMatchStatus(@Valid Request<ThirdMatchStatusDTO> request, Long sportId) {
        ThirdMatchStatusDTO thirdMatchStatusDTO = request.getData();
        String thirdMatchSourceId = thirdMatchStatusDTO.getThirdMatchSourceId();
        Integer matchStatus = thirdMatchStatusDTO.getMatchStatus();
        // 检查赛事状态的有效性
        if (!checkMatchStatus(request.getLinkId(), matchStatus, sportId)) {
            log.info("linkId=【{}】putMatchStatus 检查赛事状态异常, SportId={} ,matchStatus={}", request.getLinkId(), sportId, matchStatus);
            return;
        }
        //库中三方赛事
        ThirdMatchInfo oldThirdMatchInfo = thirdMatchInfoService.getItem(thirdMatchStatusDTO.getDataSourceCode(), thirdMatchSourceId);
        if (null == oldThirdMatchInfo) {
            log.info("linkId=【{}】putMatchStatus 查询三方赛事={}信息为空!", request.getLinkId(), thirdMatchSourceId);
            return;
        }
        //兼容缓存覆盖问题
        if (oldThirdMatchInfo.getReferenceId() == null || oldThirdMatchInfo.getReferenceId() == 0) {
            oldThirdMatchInfo = thirdMatchInfoService.getItemByPrimaryKey(oldThirdMatchInfo.getId());
        }
        log.info("linkId=【{}】putMatchStatus 当前三方赛事信息={}", request.getLinkId(), JSON.toJSONString(oldThirdMatchInfo));
        //更新第三方赛事
        oldThirdMatchInfo = updateThirdMatchInfo(request.getLinkId(), matchStatus, oldThirdMatchInfo);
        //获取标准赛事ID
        Long standardMatchId = oldThirdMatchInfo.getReferenceId();
        //获取标准赛事
        StandardMatchInfo oldStandardMatchInfo = standardMatchInfoService.getItem(standardMatchId);
        if (null == oldStandardMatchInfo) {
            log.info("linkId=【{}】putMatchStatus 标准赛事不存在，标准赛事id={}", request.getLinkId(), standardMatchId);
            return;
        }
        //获取开售信息
        StandardSportMarketSell standardSportMarketSell = standardSportMarketSellService.getItem(standardMatchId);
        //预开售中赛事状态服务商
        String matchStatusSourceCode = null;
        if (null != standardSportMarketSell) {
            matchStatusSourceCode = standardSportMarketSell.getMatchStatusSourceCode();
            //已经开售且管理ID为空，查库刷新缓存
            if (StringUtils.isBlank(oldStandardMatchInfo.getMatchManageId())) {
                oldStandardMatchInfo = standardMatchInfoService.getItemByPrimaryKey(oldStandardMatchInfo.getId());
            }
        }
        //当前滚球赛事状态服务商(用于判断标准赛事信息是否更新)
        String dataSourceCode = StringUtils.isNotBlank(matchStatusSourceCode) ? matchStatusSourceCode : oldStandardMatchInfo.getDataSourceCode();
        if (!dataSourceCode.equalsIgnoreCase(thirdMatchStatusDTO.getDataSourceCode())) {
            log.info("linkId=【{}】putMatchStatus 当前数据服务商{}和赛事状态服务商{}不匹配！", request.getLinkId(), thirdMatchStatusDTO.getDataSourceCode(), dataSourceCode);
        } else {
            log.info("linkId=【{}】putMatchStatus 当前标准赛事信息={}", request.getLinkId(), JSON.toJSONString(oldStandardMatchInfo));
            //4248 【赛程】赛事中断场景优化:
            if (interruptedEventSwitch) {
                //如果缓存中key存在 并且标准赛事阶段是中断 则跳过不处理
                String interruptedKey = String.format(ConstantSystem.getInterruptedKey(), oldStandardMatchInfo.getId());
                if (Objects.equals(MatchPeriodForMatchOverEnum.Interrupted.value, oldStandardMatchInfo.getMatchPeriodId()) &&
                        redisService.hasKey(interruptedKey)) {
                    log.info("linkId=【{}】putMatchStatus 缓存中存在事件中断标识赛事状态不下发!", request.getLinkId());
                    return;
                }
            }

            //常规足球赛事
            if (StandardSportTypeEnum.FootBall.getCode().equals(oldStandardMatchInfo.getSportId()) && ONE.equals(oldStandardMatchInfo.getMatchType())) {
                /**
                 * 103497 【生产】【产品】【操盘风控】足球-常规时间状态源-异常下发结束优化
                 * 获取缓存最近一条足球标准事件，判断当前赛事是否触发完赛
                 * */
                if(MatchStatusEnum.Ended.value.equals(thirdMatchStatusDTO.getMatchStatus()) ){
                    //事件源编码
                    String businessEvent = standardSportMarketSell.getBusinessEvent();

                    if (DataSourceCodeEnum.getEventCodeList().contains(businessEvent)) {
                        MatchEventInfo matchEventInfo = standardMatchStatusProducer.getMatchEventInfo(request.getLinkId(),oldStandardMatchInfo,businessEvent);
                        if(matchEventInfo != null){
                            //赛事状态为3.当前时事件进行时间不能小于全局赛事时长
                            Long time = MatchLengthEnum.getTime(oldStandardMatchInfo.getSportId(), oldStandardMatchInfo.getMatchLength());
                            //103304 【生产】【产品】【操盘风控】足球-等待加时期间异常下发完赛临时兜底-手动完赛
                            String manuallyEndFlagKey = String.format(ConstantSystem.getStandardManuallyEndFlagKey(), oldStandardMatchInfo.getId());
                            //非PD事件源 & 缓存存在值  需要触发异常完赛
                            if(!DataSourceCodeEnum.getPdCodeList().contains(matchEventInfo.getDataSourceCode()) && redisService.hasKey(manuallyEndFlagKey)){
                                //赋值一小时毫秒数
                                time = HOUR_1;
                            }
                            //重新计算秒数
                            long second = (System.currentTimeMillis() - matchEventInfo.getEventTime()) / 1000;
                            long newSecond = matchEventInfo.getSecondsFromStart() + second;
                            if (newSecond < time) {
                                matchEventInfo.setEventCode(EventCodeEnum.MATCH_STATUS.code);
                                matchEventInfo.setSecondsFromStart(newSecond);
                                matchEventInfo.setEventTime(System.currentTimeMillis());
                                matchEventInfo.setMatchPeriodId(MatchPeriodForMatchOverEnum.Ended999.value);
                                matchEventInfo.setExtraInfo(matchEventInfo.getMatchPeriodId() + "");
                                matchEventInfo.setThirdEventId(matchEventInfo.getThirdEventId() + "_" + matchEventInfo.getMatchPeriodId());
                                matchEventInfo.setLinkId(request.getLinkId() + "_103497");
                                standardMatchStatusProducer.putMatchEventInfoMq(matchEventInfo.getLinkId(),matchEventInfo,businessEvent);
                                log.info("linkId=【{}】putMatchStatus 103497赛事状态异常完赛,赛事ID={},time对比={}-{},事件信息={}", request.getLinkId(),oldStandardMatchInfo.getId(),time,newSecond,JSON.toJSONString(matchEventInfo));
                                return;
                            }
                            log.info("linkId=【{}】putMatchStatus 103497赛事状态异常完赛,正常完赛,赛事ID={},最新事件ID={},time对比={}-{}", request.getLinkId(),oldStandardMatchInfo.getId(),matchEventInfo.getThirdEventId(),time,newSecond);
                        }
                    }

                }
            }

//            MatchEventInfo lastEvent = matchEventInfos.get(matchEventInfos.size() - 1);
//
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("eventTime",lastEvent.getEventTime());
//            jsonObject.put("secondsMatchStart",lastEvent.getSecondsFromStart());
//            redisService.get(standardEventLastKey,jsonObject,RedisConfig.REDIS_HOUR_TIME);


            //更新标准赛事（标准赛事依赖的数据源会同步更新标准赛事，其他数据源则只更新本身的数据）
            oldStandardMatchInfo = updateStandardMatchInfo(request.getLinkId(), oldStandardMatchInfo, oldThirdMatchInfo, thirdMatchStatusDTO, standardSportMarketSell);
            //向下游推送赛事状态数据
            pushMatchStatusInfo(request.getLinkId(), oldStandardMatchInfo, standardSportMarketSell, thirdMatchStatusDTO.getDataSourceCode(), request.getDataSourceTime());
        }
        log.info("linkId=【{}】putMatchStatus 向下游推送赛事状态数据,三方赛事原始id={}", request.getLinkId(), thirdMatchSourceId);
    }

    /**
     * 检查赛事状态
     */
    private boolean checkMatchStatus(String linkId, Integer matchStatus, Long sportId) {
        SystemTypeDict systemTypeDict = systemTypeDictService.getItemByCodeAndActive(Constant.MATCH_STATUS, ActiveEnum.USE.getCode());
        if (systemTypeDict == null) {
            log.info("linkId=【{}】第三方赛事对象的matchStatus对应的字典类型配置为空，请确认数据是否正确, SportId={}", linkId, sportId);
            return false;
        }
        List<SystemItemDict> systemItemDictList = systemItemDictService.getListByParentTypeId(systemTypeDict.getId());
        if (CollectionUtils.isEmpty(systemItemDictList)) {
            log.info("linkId=【{}】数据字典配置异常, SportId={}", linkId, sportId);
            return false;
        }
        Optional<SystemItemDict> dictOptional = systemItemDictList.stream().filter(dict ->
                dict.getValue().equals(String.valueOf(matchStatus))).findAny();
        if (!dictOptional.isPresent()) {
            log.info("linkId=【{}】赛事状态数据异常, sportId={}, matchStatus={}", linkId, sportId, matchStatus);
            return false;
        }
        return true;
    }


    /**
     * 更新三方赛事信息
     *
     * @param linkId            全局请求唯一追踪id号
     * @param matchStatus       传入的赛事状态
     * @param oldThirdMatchInfo 库中三方赛事信息
     */
    private ThirdMatchInfo updateThirdMatchInfo(String linkId, Integer matchStatus, ThirdMatchInfo oldThirdMatchInfo) {
        //用于本次修改的三方赛事
        ThirdMatchInfo upThirdMatchInfo = new ThirdMatchInfo();
        upThirdMatchInfo.setId(oldThirdMatchInfo.getId());
        //三方赛事状态始终保持和数据商下发的数据一致
        upThirdMatchInfo.setMatchStatus(matchStatus);
        //判断是需要手动完赛 (完赛/结束/取消)
        List<Integer> matchStatusList = Lists.newArrayList(MatchStatusEnum.Ended.value, MatchStatusEnum.Cancelled.value, MatchStatusEnum.Closed.value, MatchStatusEnum.Ended999.value);
        if (matchStatusList.contains(upThirdMatchInfo.getMatchStatus())) {
            //当前赛事是未开赛,取消,滚球状态,下发了取消状态将不会完赛
            boolean matchOverFlag = (MatchStatusEnum.Not_Started.value.equals(oldThirdMatchInfo.getMatchStatus())
                    || MatchStatusEnum.Cancelled.value.equals(oldThirdMatchInfo.getMatchStatus())
                    || MatchStatusEnum.Live.value.equals(oldThirdMatchInfo.getMatchStatus()))
                    && MatchStatusEnum.Cancelled.value.equals(matchStatus);
            if (!matchOverFlag) {
                upThirdMatchInfo.setMatchOver(YesNoEnum.Y.value);
                log.info("linkId=【{}】updateThirdMatchInfo 三方赛事满足手动完赛, id={},原赛事状态={}, BeginTime={},本次传入赛事状态={}",
                        linkId, oldThirdMatchInfo.getId(), oldThirdMatchInfo.getMatchStatus(), oldThirdMatchInfo.getBeginTime(), upThirdMatchInfo.getMatchStatus());
            }
        }
        //【49152优化】，记录三方赛事是否出现过中断或是取消状态
        if (MatchStatusEnum.Cancelled.value.equals(matchStatus) || MatchStatusEnum.Interrupted.value.equals(matchStatus)) {
            upThirdMatchInfo.setInterruptionCancellationStatus(YesNoEnum.Y.value);
        }
        log.info("linkId=【{}】updateThirdMatchInfo 需要更新的三方赛事信息{}", linkId, JSON.toJSONString(upThirdMatchInfo));
//        try{
//            BeanUtil.copyProperties(upThirdMatchInfo,oldThirdMatchInfo, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
//            realtimeBaseProduecr.send(oldThirdMatchInfo,linkId,DATA_THIRD_MATCH_INFO_DB,oldThirdMatchInfo.getThirdMatchSourceId(),oldThirdMatchInfo.getDataSourceCode());
//            return thirdMatchInfoService.updateByPrimaryKeySelective(upThirdMatchInfo, linkId);
//        }catch (Exception e){
//            log.error("linkId=【"+linkId+"】updateThirdMatchInfo 更新的三方赛事状态异常,Exception:",e);
//            BeanUtil.copyProperties(upThirdMatchInfo,oldThirdMatchInfo, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
//        }
        //赛事状态发生改变才需要修改
        if (!upThirdMatchInfo.getMatchStatus().equals(oldThirdMatchInfo.getMatchStatus())) {
            BeanUtil.copyProperties(upThirdMatchInfo, oldThirdMatchInfo, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
            realtimeBaseProduecr.send(upThirdMatchInfo, linkId, DATA_THIRD_MATCH_INFO_DB, oldThirdMatchInfo.getThirdMatchSourceId(), oldThirdMatchInfo.getDataSourceCode());

            //3803【比分网】比分网后台-联赛管理
            ThirdMatchInfo upThirdMatchInfoPls = new ThirdMatchInfo();
            BeanUtils.copyProperties(upThirdMatchInfo, upThirdMatchInfoPls);
            upThirdMatchInfoPls.setDataSourceCode(oldThirdMatchInfo.getDataSourceCode());
            upThirdMatchInfoPls.setThirdMatchSourceId(oldThirdMatchInfo.getThirdMatchSourceId());
            realtimeBaseProduecr.send(upThirdMatchInfoPls, linkId, THIRD_MATCH_INFO_STATUS_PLS, oldThirdMatchInfo.getThirdMatchSourceId(), oldThirdMatchInfo.getDataSourceCode());
        }
        return oldThirdMatchInfo;
    }


    /**
     * 更新标准赛事信息（标准赛事依赖的数据源会同步更新标准赛事，其他数据源则只更新本身的数据）
     *
     * @param linkId              全局请求唯一追踪id号
     * @param thirdMatch          最新三方赛事信息
     * @param thirdMatchStatusDTO 传入赛事状态信息
     * @return StandardMatchInfo
     */
    public StandardMatchInfo updateStandardMatchInfo(String linkId, StandardMatchInfo oldStandardMatchInfo, ThirdMatchInfo thirdMatch, ThirdMatchStatusDTO thirdMatchStatusDTO, StandardSportMarketSell standardSportMarketSell) {
        //用于本次修改的标准赛事
        StandardMatchInfo upStandardMatchInfo = new StandardMatchInfo();
        upStandardMatchInfo.setId(oldStandardMatchInfo.getId());
        //如果当前赛事为商业数据源并且为 结束，关闭状态，则延用原赛事状态(状态不可逆)
        List<Integer> mathcStatusList = Arrays.asList(MatchStatusEnum.Ended.value, MatchStatusEnum.Closed.value);
        boolean flag = (mathcStatusList.contains(oldStandardMatchInfo.getMatchStatus())) && getDataSourceCodes(DataSourceCommerceEnum.COMMERCE.getCode()).contains(thirdMatch.getDataSourceCode());
        if (flag) {
            //完赛状态可以修改关闭状态
            if (MatchStatusEnum.Ended.value.equals(oldStandardMatchInfo.getMatchStatus()) && MatchStatusEnum.Closed.value.equals(thirdMatchStatusDTO.getMatchStatus())) {
                upStandardMatchInfo.setMatchStatus(thirdMatchStatusDTO.getMatchStatus());
            } else {
                upStandardMatchInfo.setMatchStatus(oldStandardMatchInfo.getMatchStatus());
                log.info("linkId=【{}】updateStandardMatchInfo 赛事不可逆,沿用原赛事状态,原赛事状态={},传入状态={},标准id={}", linkId, oldStandardMatchInfo.getMatchStatus(), thirdMatchStatusDTO.getMatchStatus(), oldStandardMatchInfo.getId());
            }
        } else {
            upStandardMatchInfo.setMatchStatus(thirdMatchStatusDTO.getMatchStatus());
        }
        //主数据源和赛事状态源可以进行标准赛事的完赛操作
        List<String> dataSourceCodeList = Arrays.asList(oldStandardMatchInfo.getDataSourceCode(), standardSportMarketSell.getMatchStatusSourceCode());
        if (dataSourceCodeList.contains(thirdMatch.getDataSourceCode())) {
            //判断是否完赛
            List<Integer> matchStatusList = Arrays.asList(MatchStatusEnum.Ended.value, MatchStatusEnum.Cancelled.value, MatchStatusEnum.Closed.value, MatchStatusEnum.Ended999.value);
            if (matchStatusList.contains(upStandardMatchInfo.getMatchStatus())) {
                //当前赛事是未开赛,取消,滚球状态,下发了取消状态将不会完赛
                boolean matchOverFlag = (MatchStatusEnum.Not_Started.value.equals(oldStandardMatchInfo.getMatchStatus())
                        || MatchStatusEnum.Cancelled.value.equals(oldStandardMatchInfo.getMatchStatus())
                        || MatchStatusEnum.Live.value.equals(oldStandardMatchInfo.getMatchStatus()))
                        && MatchStatusEnum.Cancelled.value.equals(thirdMatchStatusDTO.getMatchStatus());
                if (!matchOverFlag) {
                    upStandardMatchInfo.setMatchOver(YesNoEnum.Y.value);
                    log.info("linkId=【{}】updateStandardMatchInfo 标准赛事满足手动完赛, id={},原赛事状态={}, BeginTime={},本次传入赛事状态={}",
                            linkId, oldStandardMatchInfo.getId(), oldStandardMatchInfo.getMatchStatus(), oldStandardMatchInfo.getBeginTime(), upStandardMatchInfo.getMatchStatus());
                }
            }
        }
        //【49152优化】，记录标准赛事是否出现过中断或是取消状态
        if (MatchStatusEnum.Cancelled.value.equals(upStandardMatchInfo.getMatchStatus()) || MatchStatusEnum.Interrupted.value.equals(upStandardMatchInfo.getMatchStatus())) {
            upStandardMatchInfo.setInterruptionCancellationStatus(YesNoEnum.Y.value);
        }
        //单号81196，标准赛事状态为关闭修改为完赛展示
        if (MatchStatusEnum.Closed.value.equals(upStandardMatchInfo.getMatchStatus())) {
            upStandardMatchInfo.setMatchStatus(MatchStatusEnum.Ended.value);
            upStandardMatchInfo.setMatchOver(YesNoEnum.Y.value);
        }
        log.info("linkId=【{}】updateStandardMatchInfo 需要更新的标准赛事信息={}", linkId, JSON.toJSONString(upStandardMatchInfo));
//        try{
//            oldStandardMatchInfo = standardMatchInfoService.updateByPrimaryKeySelective(upStandardMatchInfo);
//        }catch (Exception e){
//            log.error("linkId=【"+linkId+"】updateStandardMatchInfo 更新的标准赛事状态异常,Exception:",e);
//            BeanUtil.copyProperties(upStandardMatchInfo,oldStandardMatchInfo, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
//        }
        //赛事状态发生改变才需要修改
        if (!upStandardMatchInfo.getMatchStatus().equals(oldStandardMatchInfo.getMatchStatus())) {
            BeanUtil.copyProperties(upStandardMatchInfo, oldStandardMatchInfo, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
            realtimeBaseProduecr.send(upStandardMatchInfo, linkId, DATA_STANDARD_MATCH_INFO_DB, oldStandardMatchInfo.getId() + "", thirdMatch.getDataSourceCode());
        }
        //如果当前标准赛事已经完赛
        if (YesNoEnum.Y.value.equals(upStandardMatchInfo.getMatchOver())) {
            cacheMatchOver(upStandardMatchInfo.getId());
            if (null != standardSportMarketSell) {
                //通知预售开售 赛事完赛消息
                matchSaleOverProducer.sendMatchSaleOverMessage(linkId, oldStandardMatchInfo);
            }
            try {
                //处理关联三方赛事完赛（超过3个数据源超过完赛的自动把其他数据源的三方赛事进行完赛处理）
                thirdMatchInfoService.processThirdMatchOver(oldStandardMatchInfo.getId());
            } catch (Exception e) {
                log.error("linkId=【" + linkId + "】updateStandardMatchInfo 处理关联三方赛事完赛异常,Exception:", e);
            }
        }
        return oldStandardMatchInfo;
    }

    /**
     * 向下游推送赛事状态数据：目前是调风控Dubbo服务及发送MQ
     *
     * @param linkId                  全局请求唯一追踪id号
     * @param standardMatchInfo       标准赛事信息（非空）
     * @param standardSportMarketSell 开售信息（非空）
     */
    public void pushMatchStatusInfo(String linkId, StandardMatchInfo standardMatchInfo, StandardSportMarketSell standardSportMarketSell, String dataSourceCode, Long dataSourceTime) {
        //如果标准数据不存在则不推送
        if (null == standardMatchInfo) {
            log.info("linkId=【{}】pushMatchStatusInfo,standardMatchInfo is null. ", linkId);
            return;
        }
        if (null == standardSportMarketSell) {
            log.info("linkId=【{}】pushMatchStatusInfo,standardSportMarketSell is null. ", linkId);
            return;
        }
        //“数据源”必须与“赛事状态服务商”或“标准数据源”匹配
        List<String> dataSourceCodeList = Arrays.asList(standardMatchInfo.getDataSourceCode(), standardSportMarketSell.getMatchStatusSourceCode());
        if (!dataSourceCodeList.contains(dataSourceCode)) {
            log.info("linkId=【{}】pushMatchStatusInfo,dataSourceCode not match. :={} ", linkId, dataSourceCode);
            return;
        }
        //3803下发标准赛事状态到比分网
        if (standardMatchInfo.getPlsStandardMatchId() != null && standardMatchInfo.getPlsStandardMatchId() != 0) {
            StandardSportTournament standardSportTournament = standardSportTournamentService.getItem(standardMatchInfo.getStandardTournamentId());
            standardMatchStatusProducer.sendStandardMatchStatusPls(linkId, standardMatchInfo, dataSourceTime, standardSportTournament);
        }
        //开售状态判断：(“赛前盘开售状态”,“滚球盘开售状态”) 与 开售状态("Sold","Stop_Sold","Apply_Stop_Sold","Expected_End_Sold")至少有一个匹配
        List<String> matchSellStatus = Arrays.asList(standardSportMarketSell.getPreMatchSellStatus(), standardSportMarketSell.getLiveMatchSellStatus());
        List<String> beenSoldStatus = Arrays.asList(Constant.STANDARD_MATCH_SELL.SELL_STATUS.BEEN_SOLD_STATUS);
        log.info("linkId=【{}】pushMatchStatusInfo,当前赛事开售状态（赛前&滚球） ={} ", linkId, matchSellStatus);
        //Collections.disjoint 当两个集合中没有相同的元素的时候 返回 true 。当有相同的元素的时候返回 false.
        if (Collections.disjoint(matchSellStatus, beenSoldStatus)) {
            log.info("linkId=【{}】pushMatchStatusInfo,赛事开售状态不匹配！需要满足的开售状态有={}", linkId, beenSoldStatus);
            return;
        }
        //下发标准赛事状态
        standardMatchStatusProducer.sendStandardMatchStatus(linkId, standardMatchInfo, dataSourceTime);
        //下发标准赛事状态到V02-push
        standardMatchStatusProducer.sendStandardMatchStatusToV02(linkId, standardMatchInfo, dataSourceTime);

        //需求2550 FTS范特西赛事，FTS赛事状态推送MQ处理
        StandardMatchStatusMessage standardMatchStatusMessage = new StandardMatchStatusMessage();
        standardMatchStatusMessage.setStandardMatchId(standardMatchInfo.getId());
        standardMatchStatusMessage.setSportId(standardMatchInfo.getSportId());
        standardMatchStatusMessage.setMatchStatus(standardMatchInfo.getMatchStatus());
        standardMatchStatusMessage.setDataSourceCode("handleFtsMatchStatusInfo");
        realtimeBaseProduecr.send(standardMatchStatusMessage, linkId, STANDARD_MATCH_STATUS_FTS, standardMatchInfo.getId() + "", standardMatchStatusMessage.getDataSourceCode());

        //4248 【赛程】赛事中断场景优化: 状态源赛事中断&取消映射至赛事事件中断或取消
        if (interruptedEventSwitch) {
            //事件源编码
            //如果赛事状态取消，可能切换商业事件源，导致下发错误数据源的取消事件，所以这里延用当前的数据源
            log.info("linkId=【{}】pushMatchStatusInfo,标准赛事ID={},dataSourceCode={},Status={}", linkId, standardMatchInfo.getId(), dataSourceCode, standardMatchInfo.getMatchStatus());
            if (MatchStatusEnum.Cancelled.value.equals(standardMatchInfo.getMatchStatus()) &&
                    !Objects.equals(MatchPeriodForMatchOverEnum.Abandoned.value, standardMatchInfo.getMatchPeriodId())) {
                standardMatchStatusProducer.putMatchEventInfo(linkId, standardMatchInfo, dataSourceCode, MatchPeriodForMatchOverEnum.Abandoned.value);
            } else if (MatchStatusEnum.Interrupted.value.equals(standardMatchInfo.getMatchStatus()) &&
                    !Objects.equals(MatchPeriodForMatchOverEnum.Interrupted.value, standardMatchInfo.getMatchPeriodId())) {
                standardMatchStatusProducer.putMatchEventInfo(linkId, standardMatchInfo, dataSourceCode, MatchPeriodForMatchOverEnum.Interrupted.value);
            }
        }
    }


    /**
     * 缓存赛事结束时间 用于事件审核
     *
     * @param matchId 标准赛事id
     */
    public void cacheMatchOver(Long matchId) {
        redisService.set(String.format(MATCH_OVER_TIME, matchId), TimeUtils.millsSecondsEast8ZoneGmt(), RedisConfig.REDIS_WEEK_TIME);
    }

    /**
     * 处理范特西赛事
     */
    public void handleFtsMatchStatusInfo(String linkId, StandardMatchInfo ftsStandardMatchInfo, Integer matchStatus, long dataSourceTime, String ftsHomeAway) {
        //获取开售信息
        Long standardMatchId = ftsStandardMatchInfo.getId();
        StandardSportMarketSell standardSportMarketSell = standardSportMarketSellService.getItem(standardMatchId);
        //预开售中赛事状态服务商
        if (null != standardSportMarketSell) {
            //已经开售且管理ID为空，查库刷新缓存
            if (StringUtils.isBlank(ftsStandardMatchInfo.getMatchManageId())) {
                ftsStandardMatchInfo = standardMatchInfoService.getItemByPrimaryKey(ftsStandardMatchInfo.getId());
            }
        }
        //更新范特西标准赛事（标准赛事依赖的数据源会同步更新标准赛事，其他数据源则只更新本身的数据）
        ftsStandardMatchInfo = updateFtsStandardMatchInfo(linkId, ftsStandardMatchInfo, matchStatus, standardSportMarketSell, ftsHomeAway);

        //开售状态判断：(“赛前盘开售状态”,“滚球盘开售状态”) 与 开售状态("Sold","Stop_Sold","Apply_Stop_Sold","Expected_End_Sold")至少有一个匹配
        List<String> matchSellStatus = Arrays.asList(standardSportMarketSell.getPreMatchSellStatus(), standardSportMarketSell.getLiveMatchSellStatus());
        List<String> beenSoldStatus = Arrays.asList(Constant.STANDARD_MATCH_SELL.SELL_STATUS.BEEN_SOLD_STATUS);
        log.info("linkId=【{}】pushMatchStatusInfo,当前FTS赛事开售状态（赛前&滚球） ={} ", linkId, matchSellStatus);
        //Collections.disjoint 当两个集合中没有相同的元素的时候 返回 true 。当有相同的元素的时候返回 false.
        if (Collections.disjoint(matchSellStatus, beenSoldStatus)) {
            log.info("linkId=【{}】pushMatchStatusInfo,FTS赛事开售状态不匹配！需要满足的开售状态有={}", linkId, beenSoldStatus);
            return;
        }
        //向下游推送范特西赛事状态数据
        log.info("linkId=【{}】pushMatchStatusInfo,推送范特西赛事,赛事ID={} ", linkId, ftsStandardMatchInfo.getId());
        //下发标准赛事状态
        standardMatchStatusProducer.sendStandardMatchStatus(linkId, ftsStandardMatchInfo, dataSourceTime);
    }


    /**
     * 范特西赛事状态修改
     */
    private StandardMatchInfo updateFtsStandardMatchInfo(String linkId, StandardMatchInfo ftsStandardMatchInfo, Integer matchStatus, StandardSportMarketSell standardSportMarketSell, String ftsHomeAway) {
        //判断是否完赛
        List<Integer> matchStatusList = Arrays.asList(MatchStatusEnum.Ended.value, MatchStatusEnum.Cancelled.value,
                MatchStatusEnum.Closed.value, MatchStatusEnum.Ended999.value);
        //用于本次修改的标准赛事
        StandardMatchInfo upStandardMatchInfo = new StandardMatchInfo();
        upStandardMatchInfo.setId(ftsStandardMatchInfo.getId());
        //当范特西赛事为结束状态时，则沿用原赛事状态(状态不可逆)
        if (YesNoEnum.Y.value.equals(ftsStandardMatchInfo.getMatchOver())) {
            if (MatchStatusEnum.Live.value.equals(ftsStandardMatchInfo.getMatchStatus()) && matchStatusList.contains(matchStatus)) {
                upStandardMatchInfo.setMatchStatus(matchStatus);
            } else {
                upStandardMatchInfo.setMatchStatus(ftsStandardMatchInfo.getMatchStatus());
                log.info("updateFtsStandardMatchInfo FTS赛事不可逆,沿用原赛事状态,linkId={},原赛事状态={},传入状态={},标准id={}", linkId,
                        ftsStandardMatchInfo.getMatchStatus(), matchStatus, ftsStandardMatchInfo.getId());
            }
        } else {
            upStandardMatchInfo.setMatchStatus(matchStatus);
        }
        //当范特西赛事未完赛，则根据母赛事传入赛事状态决定范特西赛事是否结束
        if (!YesNoEnum.Y.value.equals(ftsStandardMatchInfo.getMatchOver()) && matchStatusList.contains(matchStatus)) {
            upStandardMatchInfo.setMatchOver(YesNoEnum.Y.value);
            log.info("linkId=【{}】updateFtsStandardMatchInfo 范特西标准赛事满足完赛, id={},原赛事状态={}, BeginTime={},本次传入赛事状态={}",
                    linkId, ftsStandardMatchInfo.getId(), ftsStandardMatchInfo.getMatchStatus(),
                    ftsStandardMatchInfo.getBeginTime(), upStandardMatchInfo.getMatchStatus());
        }
        //【49152优化】，记录FTS标准赛事是否出现过中断或是取消状态
        if (MatchStatusEnum.Cancelled.value.equals(matchStatus) || MatchStatusEnum.Interrupted.value.equals(matchStatus)) {
            upStandardMatchInfo.setInterruptionCancellationStatus(YesNoEnum.Y.value);
        }
        log.info("linkId=【{}】updateFtsStandardMatchInfo 需要更新的范特西标准赛事信息={}", linkId, JSON.toJSONString(upStandardMatchInfo));
        ftsStandardMatchInfo = standardMatchInfoService.updateByPrimaryKeySelective(upStandardMatchInfo);

        //94022 【日常】【生产】FTS赛事已结束并且已出赛果，结算审核页面显示赛事未开赛
        String dataSourceCodeQuery = null;
        if (TeamTypeEnum.HOME.code.equals(ftsHomeAway)) {
            dataSourceCodeQuery = "FTS";
        } else if (TeamTypeEnum.AWAY.code.equals(ftsHomeAway)) {
            dataSourceCodeQuery = "FTS1";
        }
        List<ThirdMatchInfo> thirdMatchInfos = thirdMatchInfoService.getItems(upStandardMatchInfo.getId());
        if (!CollectionUtils.isEmpty(thirdMatchInfos)) {
            thirdMatchInfos = thirdMatchInfos.stream().filter(v -> "FTS".equals(v.getDataSourceCode()) || "FTS1".equals(v.getDataSourceCode())).collect(Collectors.toList());
            for (ThirdMatchInfo matchInfo : thirdMatchInfos) {
                if (StringUtils.isNotBlank(dataSourceCodeQuery) && !dataSourceCodeQuery.equals(matchInfo.getDataSourceCode())) {
                    continue;
                }
                ThirdMatchInfo updateThirdMatchInfo = new ThirdMatchInfo();
                updateThirdMatchInfo.setId(matchInfo.getId());
                updateThirdMatchInfo.setMatchStatus(upStandardMatchInfo.getMatchStatus());
                updateThirdMatchInfo.setInterruptionCancellationStatus(upStandardMatchInfo.getInterruptionCancellationStatus());
                if (!YesNoEnum.Y.value.equals(matchInfo.getMatchOver()) && matchStatusList.contains(matchStatus)) {
                    updateThirdMatchInfo.setMatchOver(YesNoEnum.Y.value);
                    log.info("linkId=【{}】updateFtsThirdMatchInfo 范特西三方赛事满足完赛, id={},原赛事状态={}, BeginTime={},本次传入赛事状态={}",
                            linkId, matchInfo.getId(), matchInfo.getMatchStatus(),
                            matchInfo.getBeginTime(), matchStatus);
                }
                thirdMatchInfoService.updateByPrimaryKeySelective(updateThirdMatchInfo, linkId);
                log.info("linkId=【{}】updateFtsThirdMatchInfo 需要更新的范特西三方赛事信息={}", linkId, JSON.toJSONString(updateThirdMatchInfo));
            }
        }


        //如果当前标准赛事已经完赛
        if (YesNoEnum.Y.value.equals(upStandardMatchInfo.getMatchOver())) {
            cacheMatchOver(upStandardMatchInfo.getId());
            if (null != standardSportMarketSell) {
                //通知预售开售 赛事完赛消息
                matchSaleOverProducer.sendMatchSaleOverMessage(linkId, ftsStandardMatchInfo);
            }
        }
        return ftsStandardMatchInfo;
    }


    @Autowired
    private StandardRelationNewStandardMapper standardRelationNewStandardMapper;

    @Autowired
    private ThirdRelationNewThirdMapper thirdRelationNewThirdMapper;

    /**
     * 拷贝的赛事向MQ投递赛事状态消息
     */
    public void copyStandardMatchInfoStatus2Mq(Request<ThirdMatchStatusDTO> request) {
        String copyLinkId = StringUtils.join(request.getLinkId(), "_copy");
        ThirdMatchStatusDTO data = request.getData();
        log.info("linkId=【{}】copyStandardMatchInfoStatus2Mq,dataSourceCode={},thirdMatchSourceId={},copy赛事状态信息开始", copyLinkId, data.getDataSourceCode(), data.getThirdMatchSourceId());
        //库中原始三方赛事
        ThirdMatchInfo oldThirdMatchInfo = thirdMatchInfoService.getItem(data.getDataSourceCode(), data.getThirdMatchSourceId());
        if (null == oldThirdMatchInfo) {
            log.info("linkId=【{}】copyStandardMatchInfoStatus2Mq,三方赛事信息为空!", copyLinkId);
            return;
        }
        //标准赛事信息
        Long standardMatchId = oldThirdMatchInfo.getReferenceId();
        if (null != standardMatchId && !Long.valueOf(ZERO).equals(standardMatchId)) {
            //判断是否有拷贝的赛事
            StandardRelationNewStandardExample relationExample = new StandardRelationNewStandardExample();
            relationExample.createCriteria().andSourceStandardIdEqualTo(standardMatchId);
            List<StandardRelationNewStandard> copyStandards = standardRelationNewStandardMapper.selectByExample(relationExample);
            if (!CollectionUtils.isEmpty(copyStandards)) {
                int i = 1;
                for (StandardRelationNewStandard copyStandard : copyStandards) {
                    log.info("linkId=【{}】copyStandardMatchInfoStatus2Mq，copy标准赛事关系信息={}", copyLinkId, JSON.toJSONString(copyStandard));
                    //查询copy的标准赛事是否存在
                    StandardMatchInfo copyStandardMatchInfo = standardMatchInfoService.getItem(copyStandard.getNewStandardId());
                    if (null == copyStandardMatchInfo) {
                        log.info("linkId=【{}】copyStandardMatchInfoStatus2Mq，标准赛事id={}，copy标准赛事不存在!", copyLinkId, copyStandard.getNewStandardId());
                        return;
                    }
                    //拷贝的三方赛事关系
                    ThirdRelationNewThirdExample thirdRelationExample = new ThirdRelationNewThirdExample();
                    thirdRelationExample.createCriteria().andSourceThirdIdEqualTo(oldThirdMatchInfo.getId());
                    List<ThirdRelationNewThird> copyThirdList = thirdRelationNewThirdMapper.selectByExample(thirdRelationExample);
                    if (CollectionUtils.isEmpty(copyThirdList)) {
                        log.info("linkId=【{}】copyStandardMatchInfoStatus2Mq，三方赛事id={}，copy三方赛事关系不存在!", copyLinkId, oldThirdMatchInfo.getId());
                        return;
                    }
                    for (ThirdRelationNewThird copyThird : copyThirdList) {
                        ThirdMatchInfo copyThirdMatchInfo = thirdMatchInfoService.getItem(copyThird.getNewThirdId());
                        if (null == copyThirdMatchInfo) {
                            log.info("linkId=【{}】copyStandardMatchInfoStatus2Mq，copy三方赛事id={}，copy三方赛事不存在!", copyLinkId, copyThird.getNewThirdId());
                            return;
                        }
                        //重新创建参数对象
                        Request<ThirdMatchStatusDTO> newRequest = new Request<>();
                        data.setThirdMatchSourceId(copyThirdMatchInfo.getThirdMatchSourceId());
                        newRequest.setData(data);
                        newRequest.setLinkId(copyLinkId + "_" + i++);
                        newRequest.setDataSourceCode(copyThirdMatchInfo.getDataSourceCode());
                        //重新执行copy后的赛事状态逻辑
                        putMatchStatus(newRequest);
                    }
                }
            } else {
                log.info("linkId=【{}】copyStandardMatchInfoStatus2Mq,根据原始标准赛事ID={}未找到拷贝赛事信息！", copyLinkId, standardMatchId);
            }
        } else {
            log.info("linkId=【{}】copyStandardMatchInfoStatus2Mq,原始三方赛事没有对应标准赛事ID={}", copyLinkId, standardMatchId);
        }
        log.info("linkId=【{}】copyStandardMatchInfoStatus2Mq,copy赛事状态信息结束", copyLinkId);
    }
}

