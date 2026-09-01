package com.panda.merge.mq.consumer;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.calculation.impl.FootballCalculationServiceImpl;
import com.panda.merge.dto.scores.EditScoreResultStatusRequest;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.enums.EventCodeEnum;
import com.panda.merge.common.enums.MatchStatusEnum;
import com.panda.merge.common.enums.OperateLogTypeEnum;
import com.panda.merge.config.RedisService;
import com.panda.merge.config.mq.ConsumerConfigDetail;
import com.panda.merge.config.mq.MqConsumerConfig;
import com.panda.merge.constant.*;
import com.panda.merge.dto.*;
import com.panda.merge.dto.scores.StandardScoreCenter;
import com.panda.merge.dto.scores.StandardScoreCenterDTO;
import com.panda.merge.dto.sourceSwitch.BasketballSwitch;
import com.panda.merge.dto.sourceSwitch.FootballSwitch;
import com.panda.merge.dto.sourceSwitch.TennisSwitch;
import com.panda.merge.mapper.MatchScoresCenterLogMapper;
import com.panda.merge.mapper.MatchScoresSourceTypeMapper;
import com.panda.merge.mapper.StandardSportMarketSellMapper;
import com.panda.merge.model.*;
import com.panda.merge.mq.message.CommonStandardScoresDto;
import com.panda.merge.mq.producer.CommonProducer;
import com.panda.merge.mq.producer.ScoresProducer;
import com.panda.merge.repository.*;
import com.panda.merge.service.ILiveDataScoresService;
import com.panda.merge.service.IScoresService;
import com.panda.merge.utils.MessageBuilderUtils;
import com.panda.merge.utils.MessageGZIP;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.panda.merge.config.RedisConfig.REDIS_HOUR_TIME;
import static com.panda.merge.constant.ConstantSystem.ZERO;
import static com.panda.merge.constant.DataSourceConstant.DATA_SOURCES;
import static com.panda.merge.constant.RepositoryConstant.MATCH_TIME_INFO;

/**
 * 绑定标准赛的三方事件处理 MATCH_EVENT_INFO_TO_RISK（事件比分，实时服务下发）
 * */
@Slf4j
@Component
@DependsOn("scoresAdminApplication")
public class LiveDataScoresNewConsumer extends AbstractSingleMessageMQConsumer<Request<List<MatchEventInfo>>>{
    /**
     * 未处理完的事件存入redis的key + 三方赛事ID
     * */
    private static final String MATCH_EVENT_DELAY_LIST ="MATCH_EVENT_DELAY_LIST:";
    private static final String TOPIC="MATCH_EVENT_INFO_TO_RISK";
    private static final String CONSUMER_GROUP="scores-group-MATCH_EVENT_INFO_TO_RISK";

    @Autowired
    FootballCalculationServiceImpl footballCalculationService;

//    @Autowired
//    MatchScoresInfoMapper matchScoresInfoMapper;

    @Autowired
    MatchScoresSourceTypeMapper matchScoresSourceTypeMapper;

    @Autowired
    RedisService redisService;

    @Autowired
    IScoresService scoresService;

    @Autowired
    ILiveDataScoresService liveDataScoresService;

    @Autowired
    ScoresProducer scoresProducer;

//    @Autowired
//    IMatchScoreSearchService matchScoreSearchService;

    @Autowired
    MatchScoreInfoRepository matchScoreInfoRepository;
//
    @Autowired
    ThirdMatchInfoRepository thirdMatchInfoRepository;

    @Autowired
    MatchScoresSourceTypeRepository matchScoresSourceTypeRepository;

    @Autowired
    StandardSportMarketSellRepository standardSportMarketSellRepository;

    @Autowired
    StandardMatchInfoRepository standardMatchInfoRepository;
    @Autowired
    MatchTimeInfoRepository matchTimeInfoRepository;

    @Autowired
    MessageBuilderUtils messageBuilderUtils;
    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    @Autowired
    private ScoresRedisHelp scoresRedisHelp;

    @Autowired
    private StandardSportMarketSellMapper standardSportMarketSellMapper;

    @Autowired
    private MatchScoresCenterLogMapper matchScoresCenterLogMapper;


    /**
     * 消费者需要配置多少线程，范围0-1000
     */
    public static final String NACOS_MQ_CONSUMER_THREAD_NUMBER_KEY ="mq.live-data-score.consumer.thread";

    @NacosValue(value = "${"+ NACOS_MQ_CONSUMER_THREAD_NUMBER_KEY +":20}",autoRefreshed = true)
    private Integer consumerThreadNumber;


    @NacosValue(value = "${datacenter.scores.switch:false}", autoRefreshed = true)
    private Boolean datacenterMergeSwitch;
    @Autowired
    CommonProducer commonProducer;

    //延迟消费数据下发MQ 1分钟条数
    @NacosValue(value = "${delay.event.count:5}", autoRefreshed = true)
    private int DELAY_EVENT_COUNT;
    //延迟消费数据下发MQ开关 0关 1开
    @NacosValue(value = "${delay.event.sendmq.switch:0}", autoRefreshed = true)
    private int DELAY_EVENT_SENDMQ_SWITCH;

    /**
     * 点球事件
     * 获得点球、点球重提、点球未进、点球取消
     */
    private static List<String> PenaltyEventCode = new ArrayList<>(Arrays.asList("penalty_awarded","retake_pen","penalty_missed","canceled_penalty","canceled_goal","canceled_var_penalty"));

    /** 暂停，开始相关事件编码 */
    private static List<String> timeEventCode = new ArrayList<>(Arrays.asList("time_start","time_start_stop","time_startstop"));

    /**
     * init consumer
     */

    @Override
    MqConsumerConfig buildConfig() {
        ConsumerConfigDetail consumerConfigDetail = ConsumerConfigDetail.builder()
                .threadNumber(consumerThreadNumber).pullBatchSize(64).build();
        return new MqConsumerConfig(TOPIC, CONSUMER_GROUP
                , new TypeReference<Request<List<MatchEventInfo>>>() {},consumerConfigDetail);
    }


    @Override
    public void processMessage(Request<List<MatchEventInfo>> request) {
        log.info("LiveDataScoresConsumer MQ消费数据开始...{}",datacenterMergeSwitch);
        if (datacenterMergeSwitch && commonProducer.getDatacenterMatchIds(request.getData().get(0).getStandardMatchId().toString())) {
                //MQ消息转发给数据中心
            commonProducer.asyncSend(request, "datacenter-MATCH_EVENT_INFO_TO_RISK", request.getLinkId());
            return;
        }
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String linkId = request.getLinkId();
        try{
            log.info("linkId::{}::LiveDataScoresConsumer 事件比分中心处理开始...", linkId);
            List<MatchEventInfo> data = request.getData();
            if(CollectionUtils.isEmpty(data)){
                log.info("linkId::{}::LiveDataScoresConsumer 事件比分中心处理入参：{}", linkId, JSON.toJSONString(request));
                return;
            }
            log.info("linkId::{}::LiveDataScoresConsumer 事件比分中心处理,本次处理事件条数：{}", linkId,data.size());
            MatchEventInfo matchEventInfo1 = data.get(0);
            String dataSourceCode = matchEventInfo1.getDataSourceCode();
            //C01过滤
            if (StringUtils.isEmpty(matchEventInfo1.getDataSourceCode()) ||
                    DataSourceCodeEnum.RC.code.equals(matchEventInfo1.getDataSourceCode())) {
                return ;
            }
            //PD事件立即下发比分
            if (DataSourceCodeEnum.PD.code.equals(dataSourceCode) || DataSourceCodeEnum.PD2.code.equals(dataSourceCode)) {
                log.info("linkId::{}::LiveDataScoresConsumer PD事件比分中心处理入参：{}", linkId, JSON.toJSONString(request) );
                sendPDScores(matchEventInfo1,linkId);
                return;
            }
            //点球事件缓存编码，有效时长5分钟，用于判断下一个goal事件是普通进球，还是点球进球
            if(PenaltyEventCode.contains(matchEventInfo1.getEventCode())){
                redisService.set("PENALTY_EVENT_" + matchEventInfo1.getThirdMatchId()+"_"+matchEventInfo1.getDataSourceCode(),
                        matchEventInfo1.getEventCode(), 300);
            }
            /**
             * 缓存中的事件数据处理逻辑
             * 查询是否有事件未处理,有则拉出来后 加入本次事件处理机制
             * */
            boolean hasCacheEvent = false;
            Boolean isReissue = false;
            if(request.getIsReissue()!=null && request.getIsReissue()){
                isReissue = true;
                log.info("linkId::{}::LiveDataScoresConsumer 事件比分中心处理入参:isReissue = true", linkId);
            }
            //事件逻辑处理
            eventMessagehandle(data,linkId,isReissue);
        } finally {
            stopWatch.stop();
            log.info("linkId::{}::LiveDataScoresConsumer 事件比分中心处理结束耗时：{}", linkId, stopWatch.getTotalTimeMillis());
        }
    }

    /**
     * PD事件立即下发比分并缓存比分
     * */
    private void sendPDScores(MatchEventInfo event,String linkId) {
        boolean isEffectScores = EffectScoresCode.chargeEffectScores(event.getSportId(), event.getDataSourceCode(), event.getEventCode());
        if (!isEffectScores && !timeEventCode.contains(event.getEventCode())) {
            log.info("linkId::{}::LiveDataScoresConsumer,PD事件编码不匹配，跳过处理", linkId);
            return;
        }

        //查询三方赛事信息
        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoRepository.selectThirdMatchInfoByPrimaryKey(event.getThirdMatchId());
        MatchScoresInfo matchScoresInfo = matchScoreInfoRepository.selectByExample(event.getThirdMatchId(),event.getSourceType());
        StandardMatchInfo standardMatchInfo = standardMatchInfoRepository.selectStandardMatchPrimaryKey(thirdMatchInfo.getReferenceId());
        StandardSportMarketSell standardSportMarketSell = standardSportMarketSellRepository.selectThirdMatchInfoPrimaryKey(event.getStandardMatchId());
        //报球板存在事件时间保存失败的问题，这边兜底一下再次
//        savePdTime(event,matchScoresInfo);
        if (matchScoresInfo!=null) {
            matchScoresInfo.setScoresJsonType(event.getRemark());
            if(StringUtils.isBlank(event.getLinkId())){
                event.setLinkId(linkId);
            }
            // 足球控球率计算（PD事件也需要计算控球率，确保下发的scoresJson包含最新控球率）
            if (event.getSportId() == 1L) {
                try {
                    footballCalculationService.calcFootballEventTimePublic(event, matchScoresInfo);
                } catch (Exception e) {
                    log.error("linkId::{}::PD事件控球率计算失败", linkId, e);
                }
            }
            //主客队相反
//            scoresService.changePDHomeAwayScores(matchScoresInfo, thirdMatchInfo);
            scoresProducer.sendToMQ(thirdMatchInfo, matchScoresInfo, event);
            if(event.getSportId()==1L){
                //比分写入缓存
                footballCalculationService.save15MinScores(matchScoresInfo,thirdMatchInfo.getId());
            }
            //发送pls标准比分
            if(standardMatchInfo.getPlsStandardMatchId()!=null && standardMatchInfo.getPlsStandardMatchId()!=0) {
                scoresProducer.sendPlsScoresPD(event, matchScoresInfo, thirdMatchInfo,  standardMatchInfo);
            }
            if (standardSportMarketSell == null || thirdMatchInfo.getReferenceId() == null) {
                return ;
            }
            this.saveStandardScores(matchScoresInfo,event,thirdMatchInfo,standardMatchInfo,standardSportMarketSell);
            //标准比分通知WS服务，推送到比分中心页面，SCORE_CENTER_MATCH_SCORES
            if(DataSourceConstant.STANDARC_SCORE_SPORTIDS.contains(event.getSportId())){
                pushMatchStandScores(event.getStandardMatchId(), event.getLinkId());
            }
        }else{
            log.info("linkId::{}::LiveDataScoresConsumer,PD事件立即下发比分 无比分数据:{}",linkId,event.getThirdMatchSourceId());
        }
        log.info("linkId::{}::sendPDScores end", linkId);
    }

    private void savePdTime(MatchEventInfo event,MatchScoresInfo matchScoresInfo) {
        MatchTimeInfo matchTimeInfo = matchTimeInfoRepository.selectByPrimaryKey(matchScoresInfo.getId());
        if (matchTimeInfo == null) {
            //初始化赛事比分时间
            matchTimeInfo = this.createMatchTimeInfo(event, matchScoresInfo);
        }
        matchTimeInfo.setSecondFromStart(event.getSecondsFromStart());
        String key = MATCH_TIME_INFO +matchTimeInfo.getId();
        JSONObject jsonObject = (JSONObject) JSONObject.toJSON(matchTimeInfo);
        redisService.set(key, MessageGZIP.compressToByte(jsonObject.toJSONString()), RepositoryConstant.REDIS_THREE_TIME);
        String thirdMatchIdKey = MATCH_TIME_INFO + matchTimeInfo.getThirdMatchId() + "_" + matchTimeInfo.getDataSourceType();
        redisService.set(thirdMatchIdKey, MessageGZIP.compressToByte(jsonObject.toJSONString()), RepositoryConstant.REDIS_THREE_TIME);
    }

    /**
     * 事件逻辑处理
     * @param data
     * @param linkId
     */
    private void eventMessagehandle(List<MatchEventInfo> data, String linkId, boolean isReissue) {
        //三方赛事ID
        log.info("linkId::{}::eventMessagehandle start", linkId);
        Long thirdMatchId = data.get(0).getThirdMatchId();

        /**
         * 锁的机制要变更，不要频繁for循环锁，而是要一次性锁住和释放
         * */
        String key = "MATCH_SCORES:" + thirdMatchId;
        //缓存中事件比分数据
        String matchEventListKey = MATCH_EVENT_DELAY_LIST + thirdMatchId;
        boolean flag = false;
        try {
            flag = redisService.tryLock(key, linkId, 3, 5);
            log.info("linkId::{}::LiveDataScoresConsumer,事件逻辑处理 获取redis分布式锁：{}", linkId,flag);
            if (flag) {
                addEventListCache(data, linkId, matchEventListKey);
                //查询三方赛事信息
                ThirdMatchInfo thirdMatchInfo = thirdMatchInfoRepository.selectThirdMatchInfoByPrimaryKey(data.get(0).getThirdMatchId());
                if (thirdMatchInfo == null) {
                    log.info("linkId::{}::LiveDataScoresConsumer,单条事件逻辑处理 事件对应的三方赛事不存在,三方赛事id:{}", linkId, data.get(0).getThirdMatchId());
                    return;
                }
                StandardSportMarketSell standardSportMarketSell = standardSportMarketSellRepository.selectThirdMatchInfoPrimaryKey(data.get(0).getStandardMatchId());
                if (standardSportMarketSell == null || thirdMatchInfo.getReferenceId() == null) {
                    log.info("linkId::{}::LiveDataScoresConsumer,单条事件逻辑处理 事件对应的赛开售赛事不存在,三方赛事id:{}", linkId, data.get(0).getThirdMatchId());
                    return ;
                }
                StandardMatchInfo standardMatchInfo = standardMatchInfoRepository.selectStandardMatchPrimaryKey(data.get(0).getStandardMatchId());
                if (standardMatchInfo == null) {
                    log.info("linkId::{}::LiveDataScoresConsumer,单条事件逻辑处理 事件对应的标准赛事不存在,三方赛事id:{}", linkId, data.get(0).getThirdMatchId());
                    return ;
                }
                //B02通道判断
                Boolean b02Check = true;
                if(DataSourceConstant.B02_SPORT_TYPES.contains(data.get(0).getSportId()) && DataSourceCodeEnum.BC.code.equals(data.get(0).getDataSourceCode())){
                    if (matchScoreInfoRepository.checkB02ScoresSource(data.get(0).getSportId())!=0L){
                        b02Check = false;
                    }
                }
                if(isReissue && SportTypeEnum.FOOTBALL.getValue().equals(data.get(0).getSportId())){
                    //如果是足球补发事件，过滤非比分事件，只处理进球角球红牌黄牌这些，其余过滤，减少处理时长和消费顺序错误的几率
                    List<String> eventCodeList = Arrays.asList("goal","corner","fa_card","red_card","yellow_card","match_status","injury_time",
                            "goal_time_modified","redcard_time_modified","yellowcard_time_modified","corner_time_modified");
                    List<MatchEventInfo> eventList = data.stream().filter(e -> eventCodeList.contains(e.getEventCode())).collect(Collectors.toList());
                    if(!eventList.isEmpty()){
                        log.info("linkId::{}::LiveDataScoresConsumer,补发事件过滤1，事件条数:{}", linkId,data.size());
                        data = new ArrayList<>(eventList);
                        log.info("linkId::{}::LiveDataScoresConsumer,补发事件过滤2，事件条数:{}", linkId,data.size());
                    }
                }
                Long nowTime = System.currentTimeMillis();
                for (MatchEventInfo event : data) {
                    String link = linkId;
                    if(data.size()>1){
                        link = linkId+"_"+event.getId();
                    }
                    log.info("linkId::{}::LiveDataScoresConsumer,单条事件逻辑处理开始 源事件ID:{},事件编码:{}", link,event.getThirdEventId(),event.getEventCode());
                    event.setLinkId(link);
                    Integer checkFlag = checkEventFlag(event,isReissue);
                    if(checkFlag!=0){
                        log.info("linkId::{}::LiveDataScoresConsumer,事件信息不匹配，跳过处理,flag={}", link,checkFlag);
                        continue;
                    }
                    //比分处理事件的时间与实际MQ下发的时间差超过1秒，并且非补发事件时，记录缓存下发MQ
                    if(SportTypeEnum.FOOTBALL.getValue().equals(event.getSportId()) && nowTime-event.getCreateTime()>5000 && !isReissue
                        && event.getEventCode().equals("goal")){
                        log.info("linkId::{}::LiveDataScoresConsumer,MQ消费与事件时间超出1秒，,发送MQ通知业务,开关：{}" +
                                        "事件发生时间：{}，事件下发事件：{}，比分服务处理时间：{}",
                                link,DELAY_EVENT_SENDMQ_SWITCH,event.getEventTime(),event.getCreateTime(),nowTime);
                        if(DELAY_EVENT_SENDMQ_SWITCH==1){
                            scoresProducer.sendOutTimeEvent(nowTime,event);
                        }
                    }
                    StopWatch extWatch = new StopWatch();
                    extWatch.start();
                    try {
                        //先缓存后DB
                        MatchScoresInfo matchScoresInfo = matchScoreInfoRepository.selectByExample(event.getThirdMatchId(),event.getSourceType());
                        if (matchScoresInfo == null) {
                            log.info("linkId::{}::LiveDataScoresConsumer,单条事件逻辑处理 初始化比分数据", link);
                            //初始化赛事比分信息
                            matchScoresInfo = scoresService.createMatchScoresInfo(event, thirdMatchInfo);
                        }else{
                            log.info("linkId::{}::LiveDataScoresConsumer,单条事件逻辑处理 同步校准赛事赛制和比赛进行时长，matchScoresInfo：{}", link,matchScoresInfo.getScoresJson());
                            matchScoresInfo.setMatchLength(standardMatchInfo.getMatchLength());
                            matchScoresInfo.setPeriod(event.getMatchPeriodId());
                            matchScoresInfo.setEventTime(event.getEventTime());
                            matchScoresInfo.setRemainingTime(event.getPeriodRemainingSeconds());
                            matchScoresInfo.setSecondsMatchStart(event.getSecondsFromStart());
                        }
                        log.info("linkId::{}::LiveDataScoresConsumer,单条事件逻辑处理 初始化赛事时间数据", link);
                        MatchTimeInfo matchTimeInfo = matchTimeInfoRepository.selectByPrimaryKey(matchScoresInfo.getId());
                        if (matchTimeInfo == null) {
                            //初始化赛事比分时间
                            matchTimeInfo = this.createMatchTimeInfo(event, matchScoresInfo);
                        }
                        log.info("linkId::{}::LiveDataScoresConsumer,单条事件逻辑处理 初始化数据源类型", link);
                        //数据源关系类型和缓存
                        this.updateSourceSourceTypeInfo(event, matchScoresInfo);
                        log.info("linkId::{}::LiveDataScoresConsumer,单条事件逻辑处理 保存赛事时间数据", link);
                        //更新赛事比分时间 同时更新缓存
                        this.updateMatchTimeInfo(matchScoresInfo, event,matchTimeInfo);

                        log.info("linkId::{}::LiveDataScoresConsumer,单条事件逻辑处理 开始处理事件比分", link);
                        //2.3 影响比分则 开始livedata 比分计算逻辑（下发三方比分和标准比分,标准比分不下发的赛种 5网，8乒，9排，10羽赛种）
                        this.processLivedataScores( matchScoresInfo, event,isReissue);
                        //B02通道判断
                        if(!b02Check){
                            log.info("linkId::{}::LiveDataScoresConsumer, 当前赛种B02数据通道为统计，不下发事件比分。",event.getLinkId());
                            return;
                        }
                        //推送三方比分和标准比分到下游(标准比分不下发的赛种 5网，8乒，9排，10羽赛种)
                        scoresProducer.sendToMQ(thirdMatchInfo,matchScoresInfo,event,standardMatchInfo);

                        //标准比分下发并入库，只处理5网，8乒，9排，10羽赛种
                        this.saveStandardScores(matchScoresInfo, event,thirdMatchInfo,standardMatchInfo,standardSportMarketSell);
                        //棒球滚球赛果展示开关处理（常规数据源路径）
                        handleBaseballRollingSwitch(event, link, standardMatchInfo, standardSportMarketSell);
                        //标准比分通知WS服务，推送到比分中心页面，SCORE_CENTER_MATCH_SCORES
                        if(DataSourceConstant.STANDARC_SCORE_SPORTIDS.contains(event.getSportId())){
                            pushMatchStandScores(event.getStandardMatchId(), event.getLinkId());
                        }

                        extWatch.stop();
                        log.info("linkId::{}::LiveDataScoresConsumer 已处理事件执行耗时：{}", link, extWatch.getTotalTimeMillis());
                        link = "";
                    } catch (Exception e) {
                        log.error("linkId::{}::LiveDataScoresConsumer,单条事件逻辑处理异常 Exception:",linkId, e);
                    }
                }
            }else {
                JSONArray eventArrayNew = new JSONArray();
                //没有包含有效比分事件过滤
                for (MatchEventInfo event : data) {
                    boolean isEffectScores = EffectScoresCode.chargeEffectScores(event.getSportId(), event.getDataSourceCode(), event.getEventCode());
                    if (!isEffectScores) {
                        continue;
                    }
                    eventArrayNew.add(JSON.toJSON(event));
                }
                if(CollectionUtils.isEmpty(eventArrayNew)){
                    log.info("linkId::{}::LiveDataScoresConsumer,事件逻辑处理 本次传入事件不包含比分事件,无需缓存!", linkId);
                    return;
                }
                //如果获取redis锁失败,则将失败的事件缓存一小时
                String cacheKey = MATCH_EVENT_DELAY_LIST + thirdMatchId;
                //如果获取redis锁失败，拿出来 存入 redis
                Object eventObj = redisService.get(cacheKey);
                if(eventObj != null) {
                    String eventArrayStr = eventObj.toString();
                    //转数组
                    JSONArray eventArrayOld = JSONArray.parseArray(eventArrayStr);
                    eventArrayNew.addAll(0,eventArrayOld);

                    //根据时间排序
                }
                //去重复后存入缓存
                JSONArray newJSONArray = removeDuplicatesById(eventArrayNew);
                redisService.set(cacheKey,newJSONArray.toJSONString(),REDIS_HOUR_TIME);
                log.info("linkId::{}::LiveDataScoresConsumer,事件逻辑处理 缓存事件条数:{}", linkId,newJSONArray.size());
            }
        }catch (Exception e){
            log.error("linkId::{}::LiveDataScoresConsumer,事件逻辑处理 事件逻辑处理异常,Exception:", linkId, e);
        }finally {
            if (flag){
                redisService.unLock(key, linkId);
            }
        }
        log.info("linkId::{}::eventMessagehandle end", linkId);
    }

    private void addEventListCache(List<MatchEventInfo> data, String linkId, String matchEventListKey) {
        //先获取缓存，在清理缓存数据
        Object eventObj = redisService.get(matchEventListKey);
        log.info("linkId::{}::LiveDataScoresConsumer 获取redis事件缓存成功,查询到当前缓存的数据条数：{}", linkId, eventObj);
        if(eventObj != null){
            //清理缓存
            redisService.del(matchEventListKey);
            JSONArray eventArray = JSONArray.parseArray(eventObj.toString());
            if(!CollectionUtils.isEmpty(eventArray)) {
                List<MatchEventInfo> redisList = eventArray.toJavaList(MatchEventInfo.class);
                //将array 的事件 放置到 本次MQ的事件队列里面
                data.addAll(0, redisList);
                log.info("linkId::{}::LiveDataScoresConsumer 获取redis事件缓存成功,更新到事件执行列表完成,当前需要处理数据条数：{}", linkId, data.size());
            }
        }
    }

    /**
     * 根据数据源事件ID去重复
     * */
    public static JSONArray removeDuplicatesById(JSONArray jsonArray) {
        Set<String> seenIds = new HashSet<>();
        JSONArray uniqueJSONArray = new JSONArray();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String thirdEventId = jsonObject.getString("thirdEventId");
            // 如果该 id 没有出现过，则添加到 uniqueJSONArray 中
            if (!seenIds.contains(thirdEventId)) {
                seenIds.add(thirdEventId);
                uniqueJSONArray.add(jsonObject);
            }
        }
        return uniqueJSONArray;
    }

    /**
     * 校验事件数据是否正常执行
     * @param event
     * @return
     */
    private static Integer checkEventFlag(MatchEventInfo event,Boolean isReissue) {
        Integer flag = 0;
        //赛种校验
        if (!DataSourceConstant.SPORT_TYPES.contains(event.getSportId())) {
            flag =  1;
        }
        //0 阶段事件过滤
        if(Long.valueOf(ZERO).equals(event.getMatchPeriodId())){
            flag =  2;
        }
        //事件编码校验
        if (StringUtils.isBlank(event.getEventCode())) {
            flag =  3;
        }
//        //获得点球 不处理比分-
//        if("penalty_awarded".equals(event.getEventCode())){
//            return false;
//        }
        /**非商业数据源过滤*/
        if (StringUtils.isEmpty(event.getDataSourceCode()) || !DATA_SOURCES.contains(event.getDataSourceCode())) {
            flag =  4;
        }
        //uof比分不处理
        if (!SourceTypeEnum.LIVE_DATA.getCode().equals(event.getSourceType())) {
            flag =  5;
        }
        boolean isEffectScores = EffectScoresCode.chargeEffectScores(event.getSportId(), event.getDataSourceCode(), event.getEventCode());
        //处理事件时间后，校验事件编码，过滤掉非比分事件
        if (!isEffectScores && !timeEventCode.contains(event.getEventCode())) {
            flag =  6;
        }
        //中途开售或者切换后产生的补发事件，只处理关键比分数据，其余过滤，减少处理时长和消费顺序错误的几率
        if(isReissue && event.getSportId().equals(SportTypeEnum.FOOTBALL.getValue())){
           List<String> eventCodeList = Arrays.asList("goal","corner","fa_card","red_card","yellow_card","match_status","injury_time",
                   "goal_time_modified","redcard_time_modified","yellowcard_time_modified","corner_time_modified");
           if(!eventCodeList.contains(event.getEventCode())){
               flag =  7;
               return flag;
           }
        }
        return flag ;
    }


    /**
     * 更新赛事时间
     * @param data
     * @param matchScoresInfo
     */
    private MatchTimeInfo createMatchTimeInfo(MatchEventInfo data, MatchScoresInfo matchScoresInfo) {
        if(matchScoresInfo==null){
            return null;
        }
        MatchTimeInfo matchTimeInfo =new MatchTimeInfo();
        matchTimeInfo.setCreateTime(System.currentTimeMillis());
        matchTimeInfo.setModifyTime(System.currentTimeMillis());
        matchTimeInfo.setDataSourceType(matchScoresInfo.getDataSourceType());
        matchTimeInfo.setTimeGo(1);
        matchTimeInfo.setThirdMatchId(matchScoresInfo.getThirdMatchId());
        matchTimeInfo.setPeriod(data.getMatchPeriodId());
        matchTimeInfo.setSecondFromStart(data.getSecondsFromStart());
        matchTimeInfo.setId(matchScoresInfo.getId());
        matchTimeInfo.setMatchLength(matchScoresInfo.getMatchLength());
        matchTimeInfo.setRemainingTime(data.getPeriodRemainingSeconds());
        matchTimeInfo.setEventTime(data.getEventTime());
//        matchTimeInfoMapper.insert(matchTimeInfo);
        matchTimeInfoRepository.updateByPrimaryKey(matchTimeInfo);
        return matchTimeInfo;


    }
    /**
     * 革新赛事数据源类型
     * @param event
     * @param matchScoresInfo
     */
    private void updateSourceSourceTypeInfo(MatchEventInfo event, MatchScoresInfo matchScoresInfo) {
        MatchScoresSourceType sourceType = matchScoresSourceTypeRepository.selectSourceSourceTypeByThirdMatchId(matchScoresInfo.getThirdMatchId());
        log.info("linkId::{}::LiveDataScoresConsumer,事件逻辑处理 创建比分数据源关联表---{}", event.getLinkId(),sourceType);
        if (sourceType == null) {
            //1.创建比分数据源关联表
            sourceType =new MatchScoresSourceType();
            sourceType.setId(matchScoresInfo.getThirdMatchId());
            sourceType.setThirdMatchId(matchScoresInfo.getThirdMatchId());
            sourceType.setModifyTime(matchScoresInfo.getModifyTime());
            sourceType.setActiveType(1);
            sourceType.setActiveMode(1);
            sourceType.setSourceType(matchScoresInfo.getDataSourceType());
            sourceType.setCreateTime(matchScoresInfo.getCreateTime());
            sourceType.setSourceType(matchScoresInfo.getDataSourceType());
//            matchScoresSourceTypeMapper.insert(sourceType);
            //添加缓存
//            matchScoresSourceTypeRepository.updateScoresSourceType(sourceType);
            try {
                matchScoresSourceTypeRepository.insertScoresSourceType(sourceType);
                log.info("linkId::{}::LiveDataScoresConsumer,比分切换关联表插入成功,thirdMatchId={}", event.getLinkId(), matchScoresInfo.getThirdMatchId());
            } catch (Exception e) {
                log.error("linkId::{}::LiveDataScoresConsumer,比分切换关联表插入失败,thirdMatchId={},数据库插入异常信息---{}", event.getLinkId(), matchScoresInfo.getThirdMatchId(), e.getMessage(), e);
            }
        }else{
            //足篮默认ws推送事件比分 如果是 uof 当前推送的话,更新为 livedata
            boolean checkSport = SportTypeEnum.FOOTBALL.getValue().equals(event.getSportId()) ||
                    SportTypeEnum.BASKETBALL.getValue().equals(event.getSportId());
            if(SourceTypeEnum.UOF.getCode().toString().equals(sourceType.getSourceType()) && checkSport){
                sourceType.setSourceType(SourceTypeEnum.LIVE_DATA.getCode()+"");
                sourceType.setModifyTime(System.currentTimeMillis());
                matchScoresSourceTypeRepository.updateScoresSourceType(sourceType);
                log.info("linkId::{}::LiveDataScoresConsumer,比分切换关联表开始更新&插入livedata,thirdMatchId={}", event.getLinkId(), matchScoresInfo.getThirdMatchId());
            }

        }

    }

    /**
     * 标准比分下发并入库，只处理5网，8乒，9排，10羽赛种
     * @param matchScoresInfo
     * @param data
     */
    private void saveStandardScores(MatchScoresInfo matchScoresInfo, MatchEventInfo data,ThirdMatchInfo thirdMatchInfo,StandardMatchInfo standardMatchInfo,StandardSportMarketSell standardSportMarketSell ) {
        log.info("linkId::{}::saveStandardScores start", data.getLinkId());
        Long oldPeriod = data.getMatchPeriodId();
        if(!DataSourceConstant.STANDARC_SCORE_SPORTIDS.contains(data.getSportId())){
            return;
        }
        if(!standardSportMarketSell.getBusinessEvent().equals(matchScoresInfo.getDataSourceCode())){
            log.info("linkId::{}::saveStandardScores DataSourceCode不匹配！{} : {}",data.getLinkId(),data.getDataSourceCode(),standardSportMarketSell.getBusinessEvent());
            return;
        }
        log.info("linkId::{}::saveStandardScores 开始保存标准比分(网乒排羽足蓝)", data.getLinkId());

        StandardMatchScores score = scoresRedisHelp.getCatchStandScoreByMatchId(data.getStandardMatchId());
        //更新对应数据源的标准比分
        if(null == score){
            log.info("linkId::{}::saveStandardScores 标准比分数据为空！", data.getLinkId());
            score = new StandardMatchScores();
            score.setDataSourceCode(standardSportMarketSell.getBusinessEvent());
            score.setMatchId(data.getStandardMatchId());
            score.setThirdMatchId(thirdMatchInfo.getId());
            score.setCreateTime(System.currentTimeMillis());
            score.setUpdateTime(System.currentTimeMillis());
            score.setSportId(data.getSportId());
            score.setShowStatus(0);
            score.setSendSettleCount(0);
            score.setMatchManageId(standardMatchInfo.getMatchManageId());
            score.setDataSourceAccoSwitch(getSwitchsAsSportId(data.getSportId()));
            scoresRedisHelp.saveCatchStandScore(score);
            log.info("linkId::{}::saveStandardScores 标准比分数据为空，新增初始化比分{}！", data.getLinkId(),data.getStandardMatchId());
        }
        //更新比分前校准主数据源
        if(!standardSportMarketSell.getBusinessEvent().equals(score.getDataSourceCode())){
            score.setDataSourceCode(standardSportMarketSell.getBusinessEvent());
            score.setMatchManageId(standardMatchInfo.getMatchManageId());
        }
        if(SportTypeEnum.BASKETBALL.getValue().equals(standardMatchInfo.getSportId())){
            if(!Objects.equals(score.getMatchLength(), matchScoresInfo.getMatchLength())){
                log.info("linkId::{}::saveStandardScores 篮球赛制不相等！ 更新赛制,清空比分::{} : {}",data.getLinkId(),score.getMatchLength(),matchScoresInfo.getMatchLength());
                //赛制不相等时,更新赛制,并且清空标准比分,再同步
                score.setMatchLength(matchScoresInfo.getMatchLength());
                score.setUpdateTime(System.currentTimeMillis());
                score.setScoreJson(null);
                score.setDataSourceAccoSwitch(getSwitchsAsSportId(data.getSportId()));
                scoresRedisHelp.saveCatchStandScore(score);
            }
        }
        String redisLockKey = "STAND:SCORES:MATCHID:"+data.getStandardMatchId()+"_"+data.getDataSourceCode();

        try {
            if (redisService.tryLock(redisLockKey, redisLockKey, 2, 5)) {
                log.info("计算标准比分：{}",data.getLinkId());
                if(StringUtils.isEmpty(matchScoresInfo.getScoresJson())){
                    log.info("saveStandardScores保存标准比分：三方比分为空:{}",data.getLinkId());
                    return;
                }
                log.info("{}:livedata同步标准比分开始",data.getLinkId());
                //计算标准比分 STANDARD_MATCH_SCORES
                this.processStandardMatchScores(matchScoresInfo,score,data);
                data.setMatchPeriodId(oldPeriod);
                log.info("{}:livedata同步标准比分完成:",data.getLinkId());
                score.setThirdMatchId(matchScoresInfo.getThirdMatchId());
                score.setUpdateTime(System.currentTimeMillis());
                //标准比分入库
//                standardMatchScoresMapper.update(score);
                scoresRedisHelp.saveCatchStandScore(score);
                //主客队相反
//                scoresService.changePDHomeAwayScores(score, thirdMatchInfo);
                //1.发送 标准比分
                CommonStandardScoresDto commonScoresDto = messageBuilderUtils.buildStandardMatchScoreCommonScoresDto(score, data,matchScoresInfo);
//                commonScoresDto.setPeriodId(oldPeriod);
                //发送pls标准比分
                if(standardMatchInfo.getPlsStandardMatchId()!=null && standardMatchInfo.getPlsStandardMatchId()!=0){
                    log.info("linkId::{}::STANDARD_MATCH_SCORES_PLS 发送到比分网开始", data.getLinkId());
                    //1.发送 标准比分
                    CommonStandardScoresDto plsCommonScoresDto = commonScoresDto;
                    plsCommonScoresDto.setPlsStandardTournamentId(standardMatchInfo.getStandardTournamentId());
                    plsCommonScoresDto.setPlsStandardMatchId(standardMatchInfo.getPlsStandardMatchId());
                    plsCommonScoresDto.setMatchStatus(standardMatchInfo.getMatchStatus());
                    plsCommonScoresDto.setPeriodId(oldPeriod);
                    scoresProducer.sendPlsScores(plsCommonScoresDto);
                }
                log.info("linkId::{}::saveStandardScores 标准比分更新完成,发送标准比分开始", data.getLinkId());
                if (!scoresService.ifMatchSoldByThirdMatchId(thirdMatchInfo,standardMatchInfo)) {
                    log.info("linkId::{}::saveStandardScores sendToMQ 该赛事未开售！", data.getLinkId());
                    return;
                }

                scoresProducer.sendStandardMatchScores(commonScoresDto);
                //4117校验阶段比分是否完整
                scoresProducer.checkScores(matchScoresInfo.getScoresJson(),data.getLinkId(),data.getMatchPeriodId(),standardMatchInfo,data.getDataSourceCode());

            }
        }catch (Exception e){
            log.error("linkId::{}::saveStandardScores 更新标准比分事件比分异常,Exception:", data.getLinkId(), e);
        }finally {
            redisService.unLock(redisLockKey, redisLockKey);
        }
        log.info("linkId::{}::saveStandardScores end", data.getLinkId());
    }

    /**
     * 推送比分中心标准比分到WS服务
     * */
    public void pushMatchStandScores(Long standardMatchId,String linkId){
        Request<String> reqMessage = new Request<>();
        reqMessage.setLinkId(standardMatchId +"");
        StandardScoreCenter centerStand = new StandardScoreCenter();
        centerStand.setStandardMatchId(standardMatchId);
        //推送标识
        centerStand.setIndex(99);
        log.info("linkId::{}::pushMatchStandScores,livedata 推送比分中心标准比分到WS服务:{}", linkId, centerStand);
        reqMessage.setData(JSONObject.toJSONString(centerStand, SerializerFeature.DisableCircularReferenceDetect));
        MessageBuilder<Request<String>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, reqMessage.getLinkId());
        rocketMqTemplate.send("SCORE_CENTER_MATCH_SCORES" +":" +reqMessage.getLinkId(), builder.build());
        log.info("linkId::{}::pushMatchStandScores,livedata 推送比分中心标准比分到WS服务", linkId);

    }
    /**
     * 比分计算逻辑(标准比分不下发的赛种 5网，8乒，9排，10羽赛种)
     * @param matchScoresInfo 比分
     * @param data 事件
     * @throws Exception
     */
    private void processLivedataScores(MatchScoresInfo matchScoresInfo, MatchEventInfo data,Boolean isReissue) throws Exception {
        log.info("linkId::{}::processLivedataScores start",data.getLinkId());
        Long peroid = data.getMatchPeriodId();
        //1.取消事件和非取消事件  逻辑不同,分开执行
        if(data.getEventCode().equals(EffectScoresCode.DELETE_EVENT)||data.getCanceled()==1){
            //0.1取消事件
            liveDataScoresService.cancelEvent(matchScoresInfo,data,isReissue);
        }else if("canceled_goal".equals(data.getEventCode())){
            //59090：扣回上一个点球进球事件的比分
            liveDataScoresService.canceledGoal(matchScoresInfo,data);
        }else{
            //1.根据球种计算比分且入库
            liveDataScoresService.calculation(matchScoresInfo,data);
        }
        //比分入库
        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
        //2.发送比分消息给订阅者
        if("shot_on_target".equals(data.getEventCode()) || "possession".equals(data.getEventCode())){
            return;
        }
        //还原阶段ID
        data.setMatchPeriodId(peroid);
        log.info("linkId::{}::processLivedataScores end",data.getLinkId());
    }

    /**
     * 比分计算逻辑(标准比分不下发的赛种 5网，8乒，9排，10羽赛种)
     * @param matchScoresInfo 比分
     * @param standardMatchScores 事件
     * @throws Exception
     */
    private void processStandardMatchScores(MatchScoresInfo matchScoresInfo, StandardMatchScores standardMatchScores,MatchEventInfo data) throws Exception {
        //1.根据球种计算比分且入库
        liveDataScoresService.calcStandardMatchScores(matchScoresInfo,standardMatchScores,data);
    }

    /**
     * 更新赛事比分信息,比分时间
     * @param matchScoresInfo
     * @param event
     * @param matchTimeInfo
     */
    private void updateMatchTimeInfo(MatchScoresInfo matchScoresInfo, MatchEventInfo event,MatchTimeInfo matchTimeInfo ) {
        if(matchTimeInfo == null){
            log.info("linkId::{}::LiveDataScoresConsumer,updateMatchScoresInfoTime 赛事时间信息为空！", event.getLinkId());
            return;
        }
        matchTimeInfo.setEventTime(event.getEventTime());
        matchTimeInfo.setModifyTime(System.currentTimeMillis());
        matchTimeInfo.setPeriod(event.getMatchPeriodId());
        if(event.getPeriodRemainingSeconds()!=null) {
            matchTimeInfo.setRemainingTime(event.getPeriodRemainingSeconds());
        }
        if(event.getSecondsFromStart()!=null) {
            matchTimeInfo.setSecondFromStart(event.getSecondsFromStart());
        }
        //特殊3X3篮球时间交换字段
        if(matchScoresInfo.getSportId().equals(2L)&&matchScoresInfo.getMatchLength()!=null&&matchScoresInfo.getMatchLength()==3){
            if(event.getSecondsFromStart()!=null) {
                matchTimeInfo.setRemainingTime(event.getSecondsFromStart());
            }
            if(event.getPeriodRemainingSeconds()!=null) {
                matchTimeInfo.setSecondFromStart(event.getPeriodRemainingSeconds());
            }
        }
        if(event.getSportId().equals(2L)){
            if(EventCodeEnum.MATCH_STATUS.code.equals(event.getEventCode())){
                if(event.getMatchPeriodId().equals(13L)||event.getMatchPeriodId().equals(14L)||event.getMatchPeriodId().equals(15L)||
                        event.getMatchPeriodId().equals(16L)||event.getMatchPeriodId().equals(1L)||event.getMatchPeriodId().equals(2L)||
                        event.getMatchPeriodId().equals(21L)||event.getMatchPeriodId().equals(40L)){
                    matchTimeInfo.setTimeGo(1);
                }else {
                    matchTimeInfo.setTimeGo(0);
                }
            }
        }
        //判断是否暂停
        if(timeEventCode.contains(event.getEventCode())){
            if("1".equals(event.getExtraInfo())){
                matchTimeInfo.setTimeGo(1);
            }else if("0".equals(event.getExtraInfo())){
                matchTimeInfo.setTimeGo(0);
            }
        }
        matchTimeInfoRepository.updateByPrimaryKey(matchTimeInfo);
    }

    private static String getSwitchsAsSportId(Long sportId) {
        String dataSourceAccoSwitch = "";
        if(SportTypeEnum.FOOTBALL.getValue().equals(sportId)){
            FootballSwitch footballSwitch = new FootballSwitch();
            dataSourceAccoSwitch = JSONUtil.toJsonStr(footballSwitch);
        }else if(SportTypeEnum.BASKETBALL.getValue().equals(sportId)){
            BasketballSwitch basketballSwitch = new BasketballSwitch();
            dataSourceAccoSwitch = JSONUtil.toJsonStr(basketballSwitch);
        }else{
            TennisSwitch switchs = new TennisSwitch();
            dataSourceAccoSwitch = JSONUtil.toJsonStr(switchs);
        }
        return dataSourceAccoSwitch;
    }

    /**
     * 处理棒球滚球赛果展示开关
     * 等比分逻辑处理完后，判断棒球match_status事件是否为取消/延期/放弃状态
     * 如果是，则更新standard_sport_market_sell.show_result_status=0，下发SHOW_SCORE_STATUS MQ并保存操作日志
     */
    private void handleBaseballRollingSwitch(MatchEventInfo event, String linkId,
                                              StandardMatchInfo standardMatchInfo,
                                              StandardSportMarketSell standardSportMarketSell) {
        // 仅处理棒球(sportId=3)的match_status事件
        if (!SportTypeEnum.BASEBALL.getValue().equals(event.getSportId())) {
            return;
        }
        if (!"match_status".equals(event.getEventCode())) {
            return;
        }
        // extraInfo 为 null 或空时不处理
        String extraInfo = event.getExtraInfo();
        if (StringUtils.isBlank(extraInfo)) {
            return;
        }
        // 解析 matchStatus: 5=取消, 6=放弃, 7=延期
        int matchStatus;
        try {
            matchStatus = Integer.parseInt(extraInfo);
        } catch (NumberFormatException e) {
            log.warn("linkId::{}::handleBaseballRollingSwitch extraInfo格式错误:{}", linkId, extraInfo);
            return;
        }
        if (matchStatus != MatchStatusEnum.Cancelled.value
                && matchStatus != MatchStatusEnum.Abandoned.value
                && matchStatus != MatchStatusEnum.Delayed.value) {
            return;
        }
        log.info("linkId::{}::handleBaseballRollingSwitch 检测到棒球取消/延期/放弃, standardMatchId:{}, matchStatus:{}",
                linkId, event.getStandardMatchId(), matchStatus);
        int showStatus = 0;
        long now = System.currentTimeMillis();
        // 关闭开售表的开关（赛果开关与三方赛事无关）
        List<Long> matchIds = Collections.singletonList(event.getStandardMatchId());
        standardSportMarketSellMapper.updateShowResultStatusAll(matchIds, now, showStatus);

        // 下发SHOW_SCORE_STATUS MQ
        EditScoreResultStatusRequest request = new EditScoreResultStatusRequest();
        request.setType(0);
        request.setStatus(showStatus);
        request.setStandardMatchId(event.getStandardMatchId());
        request.setSportId(event.getSportId());
        scoresProducer.sendMatchShowStatus(request, event.getStandardMatchId() + "_baseballRolling");
        log.info("linkId::{}::handleBaseballRollingSwitch 下发SHOW_SCORE_STATUS完成,关闭赛果展示，standardMatchId:{}", linkId, event.getStandardMatchId());
        // 保存操作日志
        StandardScoreCenterDTO centerDto = new StandardScoreCenterDTO();
        centerDto.setStandardMatchId(event.getStandardMatchId());
        centerDto.setSportId(event.getSportId());
        centerDto.setShowStatus(showStatus);
        centerDto.setOperatorName(event.getDataSourceCode());
        centerDto.setMatchManageId(standardMatchInfo.getMatchManageId());
        editMatchResultShowStatusLog(centerDto);
        log.info("linkId::{}::handleBaseballRollingSwitch 保存操作日志完成，standardMatchId:{}", linkId, event.getStandardMatchId());
    }

    private void editMatchResultShowStatusLog(StandardScoreCenterDTO logDto) {
        log.info("保存操作日志:{}", logDto);
        Integer showStatus = logDto.getShowStatus();
        MatchScoresCenterLog matchScoresCenterLog = new MatchScoresCenterLog();
        String matchManageId = logDto.getMatchManageId();
        matchScoresCenterLog.setMatchManageId(matchManageId);
        matchScoresCenterLog.setOperateId(matchManageId);
        matchScoresCenterLog.setOperateName("");
        matchScoresCenterLog.setOperateParaName(OperateLogTypeEnum.SCORES_CENTER_MATCH_SETTING.getCode() + "");
        matchScoresCenterLog.setOperateType(OperateLogTypeEnum.SCORES_CENTER_MATCH_SETTING.getCode() + "");
        matchScoresCenterLog.setOperateRearText(String.valueOf(showStatus));
        // 取低位翻转作为操作前值：showStatus=0→before=1, showStatus=1→before=0
        matchScoresCenterLog.setOperateForwText(String.valueOf(showStatus ^ 1));
        matchScoresCenterLog.setOperateModule(OperateLogTypeEnum.SCORES_SETTLE_10038.getCode() + "");
        matchScoresCenterLog.setOperateUserName(logDto.getOperatorName() != null ? logDto.getOperatorName() : "match status auto close");
        matchScoresCenterLog.setIpAddress("");
        matchScoresCenterLog.setOperateMatchId(matchManageId);
        matchScoresCenterLog.setCreateTime(System.currentTimeMillis());
        matchScoresCenterLog.setModifyTime(System.currentTimeMillis());
        matchScoresCenterLogMapper.insert(matchScoresCenterLog);
    }
}
