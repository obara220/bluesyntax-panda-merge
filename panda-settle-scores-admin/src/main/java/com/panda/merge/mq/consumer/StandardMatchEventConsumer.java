package com.panda.merge.mq.consumer;


import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.mysql.cj.util.StringUtils;
import com.panda.merge.api.ISettleCenterApi;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.CommonConstant;
import com.panda.merge.constant.EffectScoresCode;
import com.panda.merge.dto.Request;
import com.panda.merge.filter.football.impl.MatchScoresSettleInitChainFilter;
import com.panda.merge.mapper.MatchSettleInfoMapper;
import com.panda.merge.model.*;
import com.panda.merge.mq.producer.CommonProducer;
import com.panda.merge.respository.MatchEventInfoRepository;
import com.panda.merge.v2.check.IMatchSettleBatchCheckService;
import com.panda.merge.v2.check.impl.MatchSettleBatchCheckServiceHelper;
import com.panda.merge.v2.check.processor.BasketBallEventProcessor;
import com.panda.merge.v2.check.IMatchScoresTransSettleService;
import com.panda.merge.service.IMatchSettleService;
import com.panda.merge.service.IWsPushService;
import com.panda.merge.v2.controllerv2.MatchSettleCenterController;
import com.panda.merge.v2.repository.*;
import com.panda.merge.v2.service.IMatchSettleGoalStatusService;
import com.panda.merge.v2.service.helper.MatchSettleInfoHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RocketMQMessageListener(topic = "MATCH_EVENT_INFO_TO_RISK", consumerGroup = "settle-group-MATCH_EVENT_INFO", consumeTimeout = 10000L)
@DependsOn("settleScoresAdminApplication")
public class StandardMatchEventConsumer implements RocketMQListener<Request<List<MatchEventInfo>>>, RocketMQPushConsumerLifecycleListener {

    @NacosValue(value = "${settle.event.consume.thread.num:128}", autoRefreshed = true)
    private Integer threadNum;

    @Autowired
    MatchSettleInfoMapper matchSettleInfoMapper;
    @Autowired
    IMatchScoresTransSettleService matchScoresTransSettleService;
    @Autowired
    IWsPushService wsPushService;
    @Autowired
    IMatchSettleService matchSettleService;
    @Autowired
    IMatchSettleBatchCheckService matchSettleBatchCheckService;
    @Autowired
    ISettleCenterApi settleCenterApi;
    @Autowired
    RedisService redisService;
    @Autowired
    BasketBallEventProcessor bascketBallEventProcessor;
    @Autowired
    MatchSettleInfoRepository matchSettleInfoRepository;
    @Autowired
    IMatchSettleGoalStatusService matchSettleGoalStatusService;
    @Autowired
    private MatchScoresSettleInitChainFilter matchScoresSettleInitChainFilter;
    @Autowired
    private MatchSettleScoreV2Repository matchSettleScoreRepository;
    @Autowired
    private MatchSettleEventV2Repository matchSettleEventRepository;
    @Autowired
    MatchEventInfoRepository matchEventInfoRepository;
    @Autowired
    private MatchSettleCheckInfoRepository matchSettleCheckInfoRepository;
    @Autowired
    private MatchSettleThirdEventRepository matchSettleThirdEventRepository;
    @Autowired
    private MatchSettleBatchCheckServiceHelper matchSettleBatchCheckServiceHelper;

    @Autowired
    FlowControlConsumer flowControlConsumer;
    
    @Autowired
    private com.panda.merge.service.IDataSourceHeartbeatService dataSourceHeartbeatService;

    @Value("${rocketmq.event.timeout:10}")
    private Integer mqEventTimeout;
    //是否使用数据中心接口
    @NacosValue(value = "${datacenter.settle.switch}", autoRefreshed = true)
    private Boolean datacenterSettleSwitch;
    @NacosValue(value = "${datacenter.settle.id}", autoRefreshed = true)
    private String datacenterSettleId;
    @Autowired
    CommonProducer commonProducer;
    @Autowired
    private MatchSettleCenterController matchSettleCenterController;
    @Autowired
    private MatchSettleInfoHelper matchSettleInfoHelper;



    private final String prefixSettleScoreRedis = "settle_event_linkId:";

//    static HashedWheelTimer timer = new HashedWheelTimer(50l, TimeUnit.MILLISECONDS,512);
    /**
     * 事件源切换的时候风控会受到list事件,可以以此作为事件处理逻辑
     * */
    @Override
    public void onMessage(Request<List<MatchEventInfo>> mq) {

        log.info("数据中心MATCH_EVENT_INFO_TO_RISK分流Id:"+datacenterSettleId);
        if(datacenterSettleSwitch|| commonProducer.getDatacenterMatchIds(mq.getData().get(0).getStandardMatchId().toString())){
            log.info("Link::{}::MATCH_EVENT_INFO_TO_RISK数据中心分流Id::{}::",mq.getLinkId(),mq.getData().get(0).getStandardMatchId());
            log.info("datacenter-settle-MATCH_EVENT_INFO_TO_RISK数据中心开关为开:"+mq.getLinkId());
            commonProducer.asyncSend(mq, "datacenter-settle-MATCH_EVENT_INFO_TO_RISK");
            return;
        }
        Long start = System.currentTimeMillis();
        String linkId = mq.getLinkId();
        log.info("linkId::{}::checkMatchThirdSettleEvent start {}", linkId, mq.getData().size());
        if(mq.getData().size()==0){
            log.info("linkId::{}::StandardMatchEventConsumer 数据为空不处理", linkId);
            return;
        }
        //过滤报球板监控事件,避免影响 结算
        if(mq.getLinkId().contains("ACTION_MONITOR")){
            return;
        }
        String prefixSettleScoreKey = prefixSettleScoreRedis+linkId;
        Object isExist = redisService.get(prefixSettleScoreKey);
        if (isExist!=null) {
            return;
        }
        redisService.set(prefixSettleScoreKey, "True",7200);
        mq.getData().forEach(t->t.setLinkId(linkId));
        MatchEventInfo eventInfo =mq.getData().get(0);
        // 109585 【生产】【产品】篮球结算2.0不接收N01,N02,N03事件与页面不展示比分
        if(eventInfo.getDataSourceCode().equals("RC")||eventInfo.getDataSourceCode().equals("TS")||eventInfo.getDataSourceCode().equals("N02")||eventInfo.getDataSourceCode().equals("N03")||eventInfo.getDataSourceCode().equals("N01")){
            log.info("linkId::{}::StandardMatchEventConsumer 数据源RC/V02/N02/N01/N03不处理", linkId);
            return;
        }
        if ("lost_connection".equals(eventInfo.getEventCode()) && (eventInfo.getDataSourceCode().equals("KO")||eventInfo.getDataSourceCode().equals("BG")
                ||eventInfo.getDataSourceCode().equals("RB")||eventInfo.getDataSourceCode().equals("SR"))) {
            matchSettleCenterController.changeMatchAutoSettleStatus(eventInfo.getStandardMatchId()+"", Boolean.FALSE, "system", "");
            redisService.set(CommonConstant.SETTLE_DATASOURCE_LOST_CONNECTION+eventInfo.getStandardMatchId(), 1, RedisConfig.REDIS_DEFAULT_TIME);
        }
        if (eventInfo.getSportId().equals(2l)) {
            log.info("linkId::{}::StandardMatchEventConsumer 开始处理篮球事件", linkId);
            bascketBallEventProcessor.processorScore(mq.getData());
            return;
        }
        if(eventInfo.getDataSourceCode().equals("LS")){
            log.info("linkId::{}::StandardMatchEventConsumer 数据源LS次序不处理", linkId);
            return;
        }
        //球种过滤
        if(!eventInfo.getSportId().equals(1l)){
            log.info("linkId::{}::StandardMatchEventConsumer 非足球不处理", linkId);
            return;
        }
        //只要live事件的
        if (eventInfo.getSourceType() != 1) {
            log.info("linkId::{}::StandardMatchEventConsumer 非live事件不处理", linkId);
            return;
        }
        //非标准赛事的三方事件不接收
        if(eventInfo.getStandardMatchId()==null||eventInfo.getStandardMatchId()==0l){
            log.info("linkId::{}::StandardMatchEventConsumer 非标准赛事三方事件不予处理",linkId);
            return;
        }
        String key = CommonConstant.MATCH_SEQUENCE_SCORE_SETTLE + eventInfo.getStandardMatchId();
        Set<Long> limitedMatchIds = flowControlConsumer.getLimitedMatchIds();
        for (MatchEventInfo matchEventInfo : mq.getData()) {
            log.info("linkId::{}::开始处理事件{}", linkId, matchEventInfo);
            // ========== 新增部分：数据商心跳处理 ==========
            // 1. 更新当前数据商以及联赛等级的时间戳到redis
            dataSourceHeartbeatService.updateDataSourceTimestamp(matchEventInfo);

            // 2. 通过redis查询数据商心跳配置时间
            // (这一步在checkAndUpdateConnectionStatus中完成)

            // 3. 判断是否断连并更新当前数据商的比赛维度连接状态
            Integer tournamentLevel = dataSourceHeartbeatService.getTournamentLevel(matchEventInfo.getStandardMatchId());
            if (tournamentLevel != null) {
                dataSourceHeartbeatService.checkAndUpdateConnectionStatus(
                        matchEventInfo.getStandardMatchId(),
                        matchEventInfo.getDataSourceCode(),
                        matchEventInfo.getSportId(),
                        tournamentLevel
                );
            }
            // ========== 新增部分结束 ==========

            // 判断是否进行后续处理（根据技术方案，这里可以基于连接状态来决定）
            // 如果断连，可以在这里决定是否继续处理

            //编码过滤 不符合则取下一个
            if (!EffectScoresCode.chargeEffectScores(matchEventInfo.getSportId(), matchEventInfo.getEventCode())
                    && !"match_status".equals(matchEventInfo.getEventCode())) {
                log.info("linkId::{}::eventId:{} 编码不符合不处理", linkId, matchEventInfo.getThirdMatchId());
                continue;
            }

            if(!eventInfo.getDataSourceCode().equals("PD")&&limitedMatchIds.contains(matchEventInfo.getStandardMatchId())) {
                log.info("linkId::{}::StandardMatchScoreConsumer 该赛事id:{}以及数据源进行限流了", linkId, eventInfo.getStandardMatchId());
                continue;
            }

            if (redisService.tryLock(key, key, 4, mqEventTimeout)) {
                try {
                    //多数据商进球确认事件处理逻辑
                    matchSettleBatchCheckService.confirmGoalDoFilter(Arrays.asList(matchEventInfo));
                    String eventCode = matchEventInfo.getEventCode();
                    if (eventCode.equals("yellow_card") || eventCode.equals("red_card") || eventCode.equals("yellow_red_card")) {
                        eventCode = "fa_card";
                    }
                    final String code = eventCode;

                    //修改事件处理逻辑 4243
                    if (eventCode.contains("time_modified")){
                        MatchSettleEvent matchSettleEvent =null;
                        //找出原有事件
                        List<MatchEventInfo> oldMatchInfos =matchEventInfoRepository.getMatchEventInfoCaseOne(eventInfo.getThirdMatchId(),eventInfo.getRemark(),eventInfo.getDataSourceCode(),eventInfo.getSportId());
                        if(oldMatchInfos.size()==0){
                            //事件未消费
                            log.error("linkid::{}::未找到原有的修改事件比分,eventId::{}::",linkId,eventInfo.getThirdMatchId());
                            continue;
                        }
                        MatchEventInfo oldEvent= oldMatchInfos.get(0);
                        //获取三方比分
                        List<MatchSettleThirdEvent> thirdEvents = matchSettleThirdEventRepository.getModelByItemsOrderBySettleNum(oldEvent.getStandardMatchId(),null,null, null,oldEvent.getId());
                        MatchSettleThirdEvent thirdEvent = thirdEvents.get(0);
                        //先试下物理删除是否有用呢
                        List<MatchSettleCheckInfo> list =  matchSettleCheckInfoRepository.getModelByThirdScoreEventIdAndMatchIdAndDataSourceCode(thirdEvent.getId(),thirdEvent.getStandardMatchId(),thirdEvent.getDataSourceCode());
                        if(list.size()==0){
                            log.info("linkId::{}::eventId:{} 没有找到被修改事件的核对记录",linkId ,oldEvent.getId());
                        }else {
                            MatchSettleCheckInfo matchSettleCheckInfo =list.get(0);
                            matchSettleEvent =matchSettleEventRepository.getById(matchSettleCheckInfo.getSettleScoreEventId());
                            matchSettleEvent.setHasDeleteEvent(1);
                            matchSettleEvent.setCurrentEventStatus(2);
                            //修改事件标记次序事件
                            matchSettleEventRepository.updateById(matchSettleEvent);
                        }


                        List<String> modifySettleNums = new ArrayList<>();
                        //修改事件标记阶段比分,标记针对修改的事件区间
                        matchScoresSettleInitChainFilter.deleteEventPeriodScorefilter(oldEvent, modifySettleNums);
                        if(modifySettleNums.size()!=0){
                            List<MatchSettleScore> matchSettleScores = matchSettleScoreRepository.getModelBySettleNumAndMatchIdIdAndStatus(modifySettleNums,matchEventInfo.getStandardMatchId(),null);
                            matchSettleScores.forEach(t->{
                                t.setHasDeleteEvent(1);
                                t.setCurrentEventStatus(2);
                            });
                            matchSettleScoreRepository.updateBatchById(matchSettleScores);
                        }
                        //进球 角球
                        Long timeFromStar = Long.parseLong(eventInfo.getExtraInfo());
                        //计算5分钟,15分钟区间
                        List<String> settleNum = matchSettleInfoHelper.getSettleNumByStarTimeAndEventCode(timeFromStar,oldEvent.getEventCode(),oldEvent.getMatchPeriodId());
                        if (null==settleNum||settleNum.isEmpty()){
                            log.info("无法判断修改后的阶段::{}",linkId);
                        }else {
                            List<MatchSettleScore> scoreList = matchSettleScoreRepository.getModelsByItemsAndSettleNums(oldEvent.getStandardMatchId(),null,null,null,settleNum);
                            scoreList.forEach(t->{
                                t.setHasDeleteEvent(1);
                                t.setCurrentEventStatus(2);

                            });
                            matchSettleScoreRepository.updateBatchById(scoreList);
                        }



                        //存储删除标记到redis
                        matchSettleBatchCheckServiceHelper.validateDeleteEvent(matchSettleEvent, modifySettleNums, matchEventInfo);
                        //修改事件标记赛事
                        MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getById(matchEventInfo.getStandardMatchId());
                        matchSettleInfo.setHasDeleteEvent(1);
                        matchSettleInfo.setCurrentEventStatus(2);
                        matchSettleInfo.setModifyTime(System.currentTimeMillis());
                        matchSettleInfoRepository.updateMatchSettleInfoToRedis(matchSettleInfo,false);
                        log.info("linkId::{}::处理修改时间事件完成,eventId::{}",linkId,matchEventInfo.getThirdMatchId());
                    }

                    //进入三方事件生成逻辑
                    if ((!matchEventInfo.getEventCode().equals("goal")) && (!matchEventInfo.getEventCode().equals("corner"))
                            && (!matchEventInfo.getEventCode().equals("yellow_card")) && (!matchEventInfo.getEventCode().equals("red_card"))
                            && (!matchEventInfo.getEventCode().equals("yellow_red_card"))) {
                        log.info("linkId::{}::eventId:{} 非进球角球罚牌事件", linkId, matchEventInfo.getThirdMatchId());
                        continue;
                    }
                    //更新当前三方赛事的进球事件状态
                    matchSettleGoalStatusService.updateMatchSettleGoalStatus(matchEventInfo);
                    //转化为结算事件
                    matchScoresTransSettleService.tansforEventSettle(matchEventInfo, false);
                    wsPushService.pushThirdSettleEvent(matchEventInfo.getStandardMatchId(), code);
                    log.info("linkId::{}::eventId:{} pushThirdSettleEvent处理结束", matchEventInfo.getLinkId(), matchEventInfo.getThirdEventId());
                    //删除事件预警 非删除事件X
                    if (matchEventInfo.getCanceled() == 0) {
                        continue;
                    }
                    //1.只处理结算2.0的删除事件预警
                    MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getModelMatchSettleInfo(matchEventInfo.getStandardMatchId());

                    if (matchSettleInfo == null) {
                        //未切换到2.0过不处理
                        log.info("linkId::{}::eventId:{} 未切换到2.0不处理", linkId, matchEventInfo.getThirdMatchId());
                        continue;
                    } else {
                        //mango预警
                        //1.查询被删除的事件
                        //2.根据被删除的事件拼凑mango预警信息 比分 主客对阵 进行时长 事件发生的球队名称
                        log.info("linkId::{}::eventId:{} 删除事件芒果预警开始", linkId, matchEventInfo.getThirdMatchId());
                        matchSettleService.manGoEarlyWarning(matchEventInfo);
                    }
                }catch (Exception e){
                    log.error("linkId::{}::eventId:{} exception: ", linkId, matchEventInfo.getThirdMatchId(), e);
                }finally {
                    redisService.unLock(key,key);
                }
            } else {
                log.info("linkId::{}::eventId:{} 事件结算处理redis锁超时", linkId, matchEventInfo.getThirdMatchId());
                throw new RuntimeException("linkId::{"+linkId+"}::eventId:{"+matchEventInfo.getThirdMatchId()+"} 事件结算处理redis锁超时");
            }
            log.info("linkId::{}::eventId:{} 完成事件", linkId, matchEventInfo.getThirdMatchId());
        }
        log.info("linkId::{}::StandardMatchEventConsumer end with cost time: {}", linkId, System.currentTimeMillis()-start);
    }

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMax(threadNum);
        defaultMQPushConsumer.setConsumeThreadMin(threadNum);
    }

}
