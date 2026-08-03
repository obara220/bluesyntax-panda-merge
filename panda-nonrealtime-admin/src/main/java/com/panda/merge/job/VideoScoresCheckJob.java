package com.panda.merge.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.enums.EventCodeEnum;
import com.panda.merge.common.enums.MatchStatusEnum;
import com.panda.merge.common.utils.MessageGZIP;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.SourceTypeEnum;
import com.panda.merge.dto.Request;
import com.panda.merge.mapper.MatchScoresInfoMapper;
import com.panda.merge.mapper.StandardMatchInfoMapper;
import com.panda.merge.mapper.StandardSportMarketSellMapper;
import com.panda.merge.mapper.ThirdMatchInfoMapper;
import com.panda.merge.model.*;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.panda.merge.config.RedisConfig.REDIS_FIVE_MINS_TIME;
import static com.panda.merge.constant.ConstantSystem.*;

/**
 * @author tell
 * @since 2023年8月8日
 * 需求：http://lan-zentao.sportxxxr1pub.com/story-view-2584.html
 * 1：定时每10s查询数据库中含V02动画滚球标准赛事,
 * 2：对比当前标准事件比分和动画源比分，不一致则缓存到redis计数  key ： CHECK_SCORE_CLOSE_VIDEO:标准赛事ID  val: 次数
 * 3：超过2次就通知赛程自动关闭V02动画源
 *
 * 需求：http://lan-zentao.dbsports.online/bug-view-61841.html
 * 对动画进行上、下架操作
 * 已上架动画：60秒1次，连续2次比分不致时，则自动下架
 * 已下架动画：30秒1次，只要判定比分一致后，则自动上架
 *
 */
@Slf4j
@Component
@JobHandler(value = "VideoScoresCheckJob")
public class VideoScoresCheckJob extends IJobHandler {

    @Autowired
    private StandardSportMarketSellMapper standardSportMarketSellMapper;

    @Autowired
    private StandardMatchInfoMapper standardMatchInfoMapper;

    @Autowired
    private ThirdMatchInfoMapper thirdMatchInfoMapper;

    @Autowired
    private MatchScoresInfoMapper matchScoresInfoMapper;

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    @Autowired
    public RedisService redisService;

    //    @Scheduled(cron = "2/10 * * * * ? ")
    public void test() {
        execute("{\"sportId\":\"1\"}");
    }

    /**
     * @param param 入参 ： {"sportId":"1"}  多个赛种,号分割
     *
     * 新入参：{"sportId":"1","type":"0"}
     * portId：赛种，多个赛种以逗号进行做分割;
     * type：用于判断调用该方法job，1：60S执行一次，0：30S执行一次
     */
    @Override
    public ReturnT<String> execute(String param) {
        long currentTime = System.currentTimeMillis();
        //log.info("【VideoScoresCheck 定时查询数据库中含动画滚球赛事校验比分:{}】 处理开始,入参: {}", currentTime, param);
        XxlJobLogger.log("【VideoScoresCheck 定时查询数据库中含动画滚球赛事校验比分:{}】 处理开始,入参: {}", currentTime, param);
        try {
            Map<String, String> parMap = JSON.parseObject(param, Map.class);
            //获取传入赛种
            List<Long> sportIds = new HashSet<>(Splitter.on(",").splitToList(parMap.get("sportId"))).stream().map(sportId -> Long.valueOf(sportId)).collect(Collectors.toList());
            //查询指定赛种的滚球赛事
            StandardMatchInfoExample matchExample = new StandardMatchInfoExample();
            matchExample.createCriteria().andMatchStatusEqualTo(MatchStatusEnum.Live.value).andMatchOverEqualTo(0).andSportIdIn(sportIds);
            List<StandardMatchInfo> standardMatchInfoList = standardMatchInfoMapper.selectByExample(matchExample);
            if (!CollectionUtils.isEmpty(standardMatchInfoList)) {
                getThirdMatchInfoList(standardMatchInfoList, currentTime, parMap.get("type"));
            } else {
                //log.info("【VideoScoresCheck 定时查询数据库中含动画滚球赛事校验比分:{}】 当前赛种{}滚球中的标准赛事为空!", currentTime, sportIds);
                XxlJobLogger.log("【VideoScoresCheck 定时查询数据库中含动画滚球赛事校验比分:{}】 当前赛种{}滚球中的标准赛事为空!", currentTime, sportIds);
            }

        } catch (Exception e) {
            log.error("【VideoScoresCheck 定时查询数据库中含动画滚球赛事校验比分执行异常:" + currentTime + "】 Exception:", e);
            XxlJobLogger.log("【VideoScoresCheck 定时查询数据库中含动画滚球赛事校验比分执行异常:" + currentTime + "】 Exception:" + e.getMessage());
        }
        //log.info("【VideoScoresCheck 定时查询数据库中含动画滚球赛事校验比分:{}】 处理结束", currentTime);
        XxlJobLogger.log("【VideoScoresCheck 定时查询数据库中含动画滚球赛事校验比分:{}】 处理结束", currentTime);
        return ReturnT.SUCCESS;
    }

    /**
     * 获取标准事件源和动画源的三方赛事ID关系
     *
     * @param standardMatchInfoList 正在进行中的标准赛事列表
     * @param currentTime           当前时间
     * @param type                  执行任务JOB类型，1：60S执行一次，0：30S执行一次
     */
    private void getThirdMatchInfoList(List<StandardMatchInfo> standardMatchInfoList, Long currentTime, String type) {
        Map<Long, Long> eventMatchId2animationMatchId = new HashMap<>();
        List<Long> standardMatchIds = standardMatchInfoList.stream().map(obj -> obj.getId()).collect(Collectors.toList());
        //查询赛事的开售信息
        StandardSportMarketSellExample marketSellExample = new StandardSportMarketSellExample();
        marketSellExample.createCriteria().andMatchInfoIdIn(standardMatchIds);
        List<StandardSportMarketSell> marketSells = standardSportMarketSellMapper.selectByExample(marketSellExample);
        if (!CollectionUtils.isEmpty(marketSells)) {
            //事件源三方赛事ID和标准赛事ID
            Map<Long, Long> thirdMatchId2StandaId = new HashMap<>();
            //查询三方赛事信息
            ThirdMatchInfoExample thirdmatchExample = new ThirdMatchInfoExample();
            thirdmatchExample.createCriteria().andReferenceIdIn(standardMatchIds);
            List<ThirdMatchInfo> thirdMatchList = thirdMatchInfoMapper.selectByExample(thirdmatchExample);
            Map<Long, List<ThirdMatchInfo>> matchId2thirdMatchList = new HashMap<>();
            if (!CollectionUtils.isEmpty(thirdMatchList)) {
                matchId2thirdMatchList = thirdMatchList.stream().collect(Collectors.groupingBy(obj -> obj.getReferenceId()));
            }
            //标准赛事ID对应的动画源
            Map<Long, String> sid2DataSourceCode = new HashMap<>();
            //三方赛事对应的事件源
            Map<Long, String> thirdMatchDataSourceCode = new HashMap<>();
            for (StandardSportMarketSell item : marketSells) {
                Long standardMatchId = item.getMatchInfoId();
                //动画源
                String animationCode = item.getAnimationCode();
                String animationCodeOld = (String) redisService.get("ANIMATION_DOWN:" + standardMatchId);
                if(StringUtils.isNotBlank(animationCodeOld)){
                    //log.info("【VideoScoresCheck 定时查询数据库中含动画滚球赛事校验比分: 当前动画源:{}, 原始动画源:{}", animationCode, animationCodeOld);
                    animationCode = animationCodeOld;
                }
                //事件源
                String businessEvent = item.getBusinessEvent();
                //log.info("【VideoScoresCheck 定时查询数据库中含动画滚球赛事校验比分:{}】 开售的标准赛事:{},事件源{},动画源{}", currentTime, standardMatchId, businessEvent, animationCode);
                XxlJobLogger.log("【VideoScoresCheck 定时查询数据库中含动画滚球赛事校验比分:{}】 开售的标准赛事:{},事件源{},动画源{}", currentTime, standardMatchId, businessEvent, animationCode);
                try {
                    Map<String, ThirdMatchInfo> dataSourceCode2ThirdMatchInfo = new HashMap<>();
                    List<ThirdMatchInfo> thirdMatchInfos = matchId2thirdMatchList.get(item.getMatchInfoId());
                    if (!CollectionUtils.isEmpty(thirdMatchInfos)) {
                        dataSourceCode2ThirdMatchInfo = thirdMatchInfos.stream().collect(Collectors.toMap(ThirdMatchInfo::getDataSourceCode, thi -> thi));
                    }
                    ThirdMatchInfo animationItem = dataSourceCode2ThirdMatchInfo.get(animationCode);
                    ThirdMatchInfo eventItem = dataSourceCode2ThirdMatchInfo.get(businessEvent);
                    if (null == animationItem || null == eventItem) {
                        //log.info("【VideoScoresCheck 定时查询数据库中含动画滚球赛事校验比分:{}】 标准赛事:{}对应的事件源{}或者动画源{}的三方赛事为空!", currentTime, standardMatchId, businessEvent, animationCode);
                        XxlJobLogger.log("【VideoScoresCheck 定时查询数据库中含动画滚球赛事校验比分:{}】 标准赛事:{}对应的事件源{}或者动画源{}的三方赛事为空!", currentTime, standardMatchId, businessEvent, animationCode);
                    } else {
                        eventMatchId2animationMatchId.put(eventItem.getId(), animationItem.getId());
                        thirdMatchId2StandaId.put(eventItem.getId(), standardMatchId);
                        sid2DataSourceCode.put(standardMatchId, animationCode);
                        thirdMatchDataSourceCode.put(eventItem.getId(), businessEvent);
                    }
                } catch (Exception e) {
                    log.error("【VideoScoresCheck 定时查询数据库中含动画滚球赛事校验比分:" + currentTime + "】 遍历赛事" + standardMatchId + "开售信息执行异常,Exception:", e);
                    XxlJobLogger.log("【VideoScoresCheck 定时查询数据库中含动画滚球赛事校验比分:" + currentTime + "】 遍历赛事" + standardMatchId + "开售信息执行异常,Exception:", e);
                }
            }
            checkMatchScores(eventMatchId2animationMatchId, thirdMatchId2StandaId, sid2DataSourceCode, currentTime, thirdMatchDataSourceCode, type);
        } else {
            //log.info("【VideoScoresCheck 定时查询数据库中含动画滚球赛事校验比分:{}】 标准赛事列表:{}，开售状态：{} 对应的开售信息为空!", currentTime, standardMatchIds, Constant.STANDARD_MATCH_SELL.SELL_STATUS.SOLD);
            XxlJobLogger.log("【VideoScoresCheck 定时查询数据库中含动画滚球赛事校验比分:{}】 标准赛事列表:{}，开售状态：{} 对应的开售信息为空!", currentTime, standardMatchIds, Constant.STANDARD_MATCH_SELL.SELL_STATUS.SOLD);
        }
    }

    /**
     * 校验事件源和动画源对应的赛事比分(比分数据超过30s不一致的)
     *
     * @param eventMatchId2animationMatchId 事件源三方赛事ID和动画源三方赛事ID
     * @param thirdMatchId2StandaId         事件源三方赛事ID和标准赛事ID
     * @param sid2DataSourceCode            标准赛事ID对应的动画源
     * @param type                          执行任务JOB类型，1：60S执行一次，0：30S执行一次
     */
    private void checkMatchScores(Map<Long, Long> eventMatchId2animationMatchId, Map<Long, Long> thirdMatchId2StandaId,
                                  Map<Long, String> sid2DataSourceCode, Long currentTime, Map<Long, String> thirdMatchDataSourceCode, String type) {
        if (!CollectionUtils.isEmpty(eventMatchId2animationMatchId)) {
            List<Long> eventMatchIds = eventMatchId2animationMatchId.keySet().stream().collect(Collectors.toList());
            List<Long> animationMatchIds = eventMatchId2animationMatchId.values().stream().collect(Collectors.toList());
            eventMatchIds.addAll(animationMatchIds);
            List<MatchScoresInfo> matchScoresInfoList = new ArrayList<>();
            for (Long matchId : eventMatchIds) {
                //获取三方事件源编码，如果事件源编码为PD时，直接取数据库中得分；反之先获取缓存数据，不存在则查数据库
                String dataSourceCode = thirdMatchDataSourceCode.get(matchId);
                MatchScoresInfo matchScoresInfo = null;
                if(DataSourceCodeEnum.PD.getCode().equals(dataSourceCode)){
                    MatchScoresInfoExample example = new MatchScoresInfoExample();
                    example.createCriteria().andThirdMatchIdEqualTo(matchId).andDataSourceTypeEqualTo(SourceTypeEnum.LIVE_DATA.getCode().toString());
                    List<MatchScoresInfo> list = matchScoresInfoMapper.selectByExample(example);
                    if (!list.isEmpty()) {
                        matchScoresInfo = list.get(0);
                    }
                }else {
                    //比分服务入库较慢，所以需要先查询缓存，在查询数据库
                    matchScoresInfo = matchScoresInfoSelectByExample(matchId, SourceTypeEnum.LIVE_DATA.getCode());
                }
                if (null != matchScoresInfo) {
                    matchScoresInfoList.add(matchScoresInfo);
                }
            }
            Map<Long, MatchScoresInfo> thirdMatchId2MatchScores = new HashMap<>();
            if (!CollectionUtils.isEmpty(matchScoresInfoList)) {
                thirdMatchId2MatchScores = matchScoresInfoList.stream().collect(Collectors.toMap(MatchScoresInfo::getThirdMatchId, thi -> thi));
            }
            //需要关闭动画的标准赛事ID和动画源编码
            Map<String, JSONArray> dataSourceCode2Sids = new HashMap<>();
            //需要开启动画的标准赛事ID
            Map<String, JSONArray> dataSourceCode2OpenSids = new HashMap<>();
            //校验比分数据是否一致
            for (Long eventMatchId : eventMatchId2animationMatchId.keySet()) {
                //动画源三方赛事ID
                Long animationMatchId = eventMatchId2animationMatchId.get(eventMatchId);
                //事件源标准赛事ID
                Long standardMatchId = thirdMatchId2StandaId.get(eventMatchId);
                //动画数据源编码
                String dataSourceCode = sid2DataSourceCode.get(standardMatchId);
                //事件源比分
                MatchScoresInfo eventMatchScores = thirdMatchId2MatchScores.get(eventMatchId);
                //动画源比分
                MatchScoresInfo animationScores = thirdMatchId2MatchScores.get(animationMatchId);
                if (null == eventMatchScores || null == animationScores) {
                    //log.info("【VideoScoresCheck 定时查询数据库中含" + dataSourceCode + "动画滚球赛事校验比分:{}】 标准赛事ID:{},动画源赛事ID{},事件源赛事ID{},比分事件为空!", currentTime, standardMatchId, animationMatchId, eventMatchId);
                    XxlJobLogger.log("【VideoScoresCheck 定时查询数据库中含" + dataSourceCode + "动画滚球赛事校验比分:{}】 标准赛事ID:{},动画源赛事ID{},事件源赛事ID{},比分事件为空!", currentTime, standardMatchId, animationMatchId, eventMatchId);
                } else {
                    JSONObject eventJson = parseJsonObject(eventMatchScores.getScoresJson());
                    JSONObject animationJson = parseJsonObject(animationScores.getScoresJson());
                    List<String> eventCodes = EventCodeEnum.getVideoScoresEventCodes();
                    //对应事件比分是否不一致标识,默认一致
                    boolean flag = false;
                    for (String eventCode : eventCodes) {
                        JSONObject obj1 = eventJson.getJSONObject(eventCode);
                        JSONObject obj2 = animationJson.getJSONObject(eventCode);
                        if (!compareJsonObjects(obj1, obj2)) {
                            flag = true;
                            //log.info("【VideoScoresCheck 定时查询数据库中含" + dataSourceCode + "动画滚球赛事校验比分:{}】 compareJsonObjects 标准赛事ID:{},事件编码：{}比分不一致,标准比分：{},动画源比分:{}", currentTime, standardMatchId, eventCode, obj1, obj2);
                            XxlJobLogger.log("【VideoScoresCheck 定时查询数据库中含" + dataSourceCode + "动画滚球赛事校验比分:{}】 compareJsonObjects 标准赛事ID:{},事件编码：{}比分不一致,标准比分：{},动画源比分:{}", currentTime, standardMatchId, eventCode, obj1, obj2);
                        } else {
                            //log.info("【VideoScoresCheck 定时查询数据库中含" + dataSourceCode + "动画滚球赛事校验比分:{}】 标准赛事ID:{},事件编码：{}比分一致,标准比分：{},动画源比分:{}", currentTime, standardMatchId, eventCode, obj1, obj2);
                            XxlJobLogger.log("【VideoScoresCheck 定时查询数据库中含" + dataSourceCode + "动画滚球赛事校验比分:{}】 标准赛事ID:{},事件编码：{}比分一致,标准比分：{},动画源比分:{}", currentTime, standardMatchId, eventCode, obj1, obj2);
                        }
                    }
                    //下架动画
                    String closeKey = "CHECK_SCORE_CLOSE_VIDEO:" + standardMatchId;
                    //上架动画
                    String openKey = "CHECK_SCORE_OPEN_VIDEO:" + standardMatchId;
                    //处理动画上、下架数据
                    Object keyValObj = getObject(type, flag, openKey, dataSourceCode2OpenSids, dataSourceCode, standardMatchId, closeKey, dataSourceCode2Sids);
                    //log.info("【VideoScoresCheck 定时查询数据库中含" + dataSourceCode + "动画滚球赛事校验比分:{}】 标准赛事ID:{},比分是否一致：{},缓存中次数：{},需要下架的数据条数:{},需要上架的数据条数:{}",
//                            currentTime, standardMatchId, flag,keyValObj,dataSourceCode2Sids.keySet(),dataSourceCode2OpenSids.keySet());
                    XxlJobLogger.log("【VideoScoresCheck 定时查询数据库中含" + dataSourceCode + "动画滚球赛事校验比分:{}】 标准赛事ID:{},比分是否不一致：{},缓存中次数：{},需要下架的数据条数:{},需要上架的数据条数:{}",
                            currentTime, standardMatchId, flag,keyValObj,dataSourceCode2Sids.keySet(),dataSourceCode2OpenSids.keySet());
                }
            }
            //log.info("【VideoScoresCheck 定时查询数据库中含动画滚球赛事校验比分:{}】 需要下架的数据条数:{},需要上架的数据条数:{}", currentTime, dataSourceCode2Sids.values().size(),dataSourceCode2OpenSids.values().size());
            XxlJobLogger.log("【VideoScoresCheck 定时查询数据库中含动画滚球赛事校验比分:{}】 需要下架的数据条数:{},需要上架的数据条数:{}", currentTime, dataSourceCode2Sids.values().size(),dataSourceCode2OpenSids.values().size());
            //需要自动下架的动画
            for (String dataSourceCode : dataSourceCode2Sids.keySet()) {
                pushCheckMatchScores(dataSourceCode2Sids.get(dataSourceCode), currentTime, dataSourceCode, ZERO);
                //记录标准赛事下架动画对应的动画源(重新上架时，以此动画源为准)
                JSONArray obj = dataSourceCode2Sids.get(dataSourceCode);
                List<String> standardMatchIds = obj.toJavaList(String.class);
                for(String standardMatchId : standardMatchIds){
                    String animationKey = "ANIMATION_DOWN:" + standardMatchId;
                    redisService.set(animationKey, dataSourceCode, 2 * 60 * 60);
                }
            }
            //需要自动上架的动画
            for (String dataSourceCode : dataSourceCode2OpenSids.keySet()) {
                pushCheckMatchScores(dataSourceCode2OpenSids.get(dataSourceCode), currentTime, dataSourceCode, ONE);
            }
        } else {
            //log.info("【VideoScoresCheck 定时查询数据库中含动画滚球赛事校验比分:{}】 传入事件源三方赛事ID和动画源三方赛事ID关系为空!", currentTime);
            XxlJobLogger.log("【VideoScoresCheck 定时查询数据库中含动画滚球赛事校验比分:{}】 传入事件源三方赛事ID和动画源三方赛事ID关系为空!", currentTime);
        }
    }

    /**
     * 处理动画上、下架数据
     *
     * @param type                    用于判断调用该方法job，1：60S执行一次，0：30S执行一次
     * @param flag                    判断比分对象是否相等 true不相等，false相等
     * @param openKey                 上架动画Key
     * @param dataSourceCode2OpenSids 需要上架动画的标准赛事ID集合
     * @param dataSourceCode          数据源编码
     * @param standardMatchId         标准赛事ID
     * @param closeKey                下架动画Key
     * @param dataSourceCode2Sids     需要下架动画的标准赛事ID集合
     * @return keyValObj 被缓存次数
     */
    private Object getObject(String type, boolean flag, String openKey, Map<String, JSONArray> dataSourceCode2OpenSids,
                             String dataSourceCode, Long standardMatchId, String closeKey, Map<String, JSONArray> dataSourceCode2Sids) {
        Object keyValObj = null;
        if ("0".equals(type)) {
            if (!flag) {
                //处理动画上架
                keyValObj = setDataSourceCode2OpenSidsValue(dataSourceCode2OpenSids, dataSourceCode, standardMatchId);
            }
        } else {
            if (flag) {
                //处理动画下架
                keyValObj = redisService.get(closeKey);
                matchScoresLogic(closeKey, dataSourceCode2Sids, standardMatchId, dataSourceCode, keyValObj);
                //如果openKey有值 则清理缓存
                if (redisService.hasKey(openKey)) {
                    redisService.del(openKey);
                }
            } else {
                //处理动画上架
                keyValObj = setDataSourceCode2OpenSidsValue(dataSourceCode2OpenSids, dataSourceCode, standardMatchId);
            }
        }
        return keyValObj;
    }

    /**
     * 上架动画处理
     *
     * @param dataSourceCode2OpenSids 上架动画集合
     * @param dataSourceCode          数据编码
     * @param standardMatchId         标准赛事ID
     * @return object
     */
    private Object setDataSourceCode2OpenSidsValue(Map<String, JSONArray> dataSourceCode2OpenSids,
                                                   String dataSourceCode, Long standardMatchId) {
        Object keyValObj = 0;
        JSONArray ids = dataSourceCode2OpenSids.get(dataSourceCode);
        if (null == ids) {
            ids = new JSONArray();
        }
        ids.add(standardMatchId);
        dataSourceCode2OpenSids.put(dataSourceCode, ids);
        return keyValObj;
    }


    /**
     * 比对到最近修改事件超过120S的事件源和动画源比分不一致,自动关闭动画源(60S执行任务)
     * 比对最近事件与动画源比分一致，自动上架动画源(30S执行任务)
     * @param status  0:下架，1:上架
     */
    public void pushCheckMatchScores(JSONArray ids, Long currentTime, String dataSourceCode, Integer status) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("standardMatchIds", ids);
        jsonObject.put("status", status);

        Request<JSONObject> request = new Request<>();
        request.setLinkId(currentTime + "");
        request.setDataType("CHECK_SCORE_CLOSE_VIDEO");
        request.setDataSourceCode(dataSourceCode);
        request.setData(jsonObject);
        MessageBuilder<Request<JSONObject>> requestMessageBuilder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, request.getLinkId());
        rocketMqTemplate.send(request.getDataType() + ":" + currentTime, requestMessageBuilder.build());
        //log.info("【VideoScoresCheck 定时查询数据库中含" + dataSourceCode + "动画滚球赛事校验比分:{}】 通知赛程下架或上架动画源完成,status：{}（0:下架，1:上架）,topic:{},request:{}", currentTime,status, request.getDataType(), JSON.toJSONString(request));
        XxlJobLogger.log("VideoScoresCheck 定时查询数据库中含" + dataSourceCode + "动画滚球赛事校验比分:{}】 通知赛程下架或上架动画源完成,status：{}（0:下架，1:上架）,topic:{},request:{}", currentTime,status, request.getDataType(), JSON.toJSONString(request));
    }




    /**
     * 发生对比一致及不一致情形连续超过120秒，自动将动画数据源上下架
     *
     * @param key                 缓存锁
     * @param dataSourceCode2Sids 动画源编码2标准赛事ID
     * @param standardMatchId     事件ID
     * @param dataSourceCode      动画源编码
     * @param keyValObj    缓存中的次数
     */
    private void matchScoresLogic(String key, Map<String, JSONArray> dataSourceCode2Sids, Long standardMatchId, String dataSourceCode, Object keyValObj) {
        if (null != keyValObj) {
            Integer val = Integer.valueOf(String.valueOf(keyValObj));
            //对比超过1次则通知赛程
            if (val >= ONE) {
                JSONArray ids = dataSourceCode2Sids.get(dataSourceCode);
                if (null == ids) {
                    ids = new JSONArray();
                }
                ids.add(standardMatchId);
                dataSourceCode2Sids.put(dataSourceCode, ids);
                redisService.del(key);
            } else {
                redisService.set(key, val + ONE, REDIS_FIVE_MINS_TIME);
            }
        } else {
            redisService.set(key, ONE, REDIS_FIVE_MINS_TIME);
        }
    }


    /**
     * 比较两个时间戳是否超过30s
     */
    private static boolean isTimeDifferenceGreaterThan30s(long timestamp1, long timestamp2) {
        // 计算两个时间戳的差值，取绝对值
        long difference = Math.abs(timestamp1 - timestamp2);
        // 30秒的毫秒数
        long threshold = 30 * 1000;
        return difference > threshold;
    }

    /**
     * 获取全场比分
     */
    private JSONObject parseJsonObject(String scoresJson) {
        if (StringUtils.isBlank(scoresJson)) {
            scoresJson = STR_KH;
        }
        JSONObject resJson = JSON.parseObject(scoresJson).getJSONObject(STR_F1);
        return null == resJson ? new JSONObject() : resJson;
    }

    /**
     * 比较两个对象
     */
    private static boolean compareJsonObjects(JSONObject json1, JSONObject json2) {
        if (json1 == null && json2 == null) {
            return true; // 两个对象都为空，视为内容一致
        }
        if (json1 == null || json2 == null) {
            return false; // 一个对象为空，一个不为空，视为内容不一致
        }
        return json1.equals(json2); // 两个对象都不为空，比较内容是否一致
    }


    /**
     * 三方比分
     */
    public String MATCH_SCORES_INFO = "REPOSITORY:MATCH_SCORES_INFO:";

    public MatchScoresInfo matchScoresInfoSelectByExample(Long thirdMatchId, Integer sourceType) {
        //1.查询redis
        MatchScoresInfo matchScoresInfo = null;
        String key = MATCH_SCORES_INFO + thirdMatchId + "_" + sourceType;
        Object o = redisService.get(key);
        if (o != null) {
            //解压缩
            String str = MessageGZIP.uncompressToString((byte[]) o);
            matchScoresInfo = JSON.toJavaObject(JSONObject.parseObject(str), MatchScoresInfo.class);
            return matchScoresInfo;
        }
        //2.如果redis 没有就查库
        MatchScoresInfoExample example = new MatchScoresInfoExample();
        example.createCriteria().andThirdMatchIdEqualTo(thirdMatchId).andDataSourceTypeEqualTo(sourceType.toString());
        List<MatchScoresInfo> list = matchScoresInfoMapper.selectByExample(example);
        if (list.size() != 0) {
            matchScoresInfo = list.get(0);
        }
        return matchScoresInfo;
    }
}
