package com.panda.merge.mq.consumer;


import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.mysql.cj.util.StringUtils;
import com.panda.merge.common.enums.FootballBallPeroidEnum;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.CommonConstant;
import com.panda.merge.constant.FootballPeriodValidateEnum;
import com.panda.merge.dto.CommonThirdScoresDto;
import com.panda.merge.dto.Request;
import com.panda.merge.mapper.MatchEventInfoMapper;
import com.panda.merge.mapper.MatchSettleInfoMapper;
import com.panda.merge.model.MatchEventInfo;
import com.panda.merge.mq.producer.CommonProducer;
import com.panda.merge.v2.check.processor.BasketBallScoreProcessor;
import com.panda.merge.v2.check.IMatchScoresTransSettleService;
import com.panda.merge.service.IWsPushService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.Set;


@Slf4j
@Component
@RocketMQMessageListener(topic = "THIRD_MATCH_SCORES", consumerGroup = "settle-group-STANDARD_MATCH_SCORE", consumeTimeout = 10000L)
@DependsOn("settleScoresAdminApplication")
public class StandardMatchScoreConsumer implements RocketMQListener<Request<CommonThirdScoresDto>>, RocketMQPushConsumerLifecycleListener {

    @NacosValue(value = "${settle.score.consume.thread.num:128}", autoRefreshed = true)
    private Integer threadNum;

    @Autowired
    MatchSettleInfoMapper matchSettleInfoMapper;
    @Autowired
    IMatchScoresTransSettleService matchScoresTransSettleService;
    @Autowired
    MatchEventInfoMapper matchEventInfoMapper;
    @Autowired
    RedisService redisService;
    @Autowired
    IWsPushService wsPushService;
    @Autowired
    BasketBallScoreProcessor bascketBallScoreProcessor;

    @Autowired
    FlowControlConsumer flowControlConsumer;

    @Autowired
    CommonProducer commonProducer;

    //是否使用数据中心接口
    @NacosValue(value = "${datacenter.settle.switch}", autoRefreshed = true)
    private Boolean datacenterSettleSwitch;
    @NacosValue(value = "${datacenter.settle.id}", autoRefreshed = true)
    private String datacenterSettleId;
    private final String prefixSettleScoreRedis = "settle_scores_linkId:";

//    static HashedWheelTimer timer = new HashedWheelTimer(50l, TimeUnit.MILLISECONDS,512);

    @Override
    public void onMessage(Request<CommonThirdScoresDto> commonStandardScoresDtoRequest) {
        log.info("数据中心THIRD_MATCH_SCORES分流Id:"+datacenterSettleId);
        if(datacenterSettleSwitch||commonProducer.getDatacenterMatchIds(commonStandardScoresDtoRequest.getData().getStandardMatchId().toString())){
            log.info("Link::{}::THIRD_MATCH_SCORES数据中心分流Id::{}::",commonStandardScoresDtoRequest.getLinkId(),commonStandardScoresDtoRequest.getData().getStandardMatchId());
            log.info("datacenter-THIRD_MATCH_SCORES数据中心开关为开:"+commonStandardScoresDtoRequest.getLinkId());
            commonProducer.asyncSend(commonStandardScoresDtoRequest, "datacenter-THIRD_MATCH_SCORES");
            return;
        }

        long start =System.currentTimeMillis();
        String linkId = commonStandardScoresDtoRequest.getLinkId();
        log.info("linkId::{}::StandardMatchScoreConsumer start score {}", linkId, commonStandardScoresDtoRequest);
        if(commonStandardScoresDtoRequest.getData().getDataSourceCode().equals("N02")||commonStandardScoresDtoRequest.getData().getDataSourceCode().equals("RC")||commonStandardScoresDtoRequest.getData().getDataSourceCode().equals("TS")){
            return;
        }
        //过滤报球板监控事件,避免影响 结算
        if(commonStandardScoresDtoRequest.getLinkId().contains("ACTION_MONITOR")){
            return;
        }
        Set<Long> limitedMatchIds = flowControlConsumer.getLimitedMatchIds();
        if(!commonStandardScoresDtoRequest.getData().getDataSourceCode().equals("PD") && limitedMatchIds.contains(commonStandardScoresDtoRequest.getData().getStandardMatchId())) {
            log.info("linkId::{}::StandardMatchScoreConsumer 该赛事id:{}以及数据源进行限流了", linkId, commonStandardScoresDtoRequest.getData().getStandardMatchId());
            return;
        }
        if (commonStandardScoresDtoRequest.getData().getSportId().equals(2l)) {
            log.info("linkId::{}::StandardMatchScoreConsumer 开始处理篮球事件", linkId);
            bascketBallScoreProcessor.processorScore(commonStandardScoresDtoRequest);
            return;
        }
        String prefixSettleScoreKey = prefixSettleScoreRedis+linkId;
        Object isExist = redisService.get(prefixSettleScoreKey);
        if (isExist!=null) {
            return;
        }
        redisService.set(prefixSettleScoreKey, "True", 7200);
        // 过滤已结算的阶段组合而成的事件
        if (FootballPeriodValidateEnum.isAlreadySettleLinkId(linkId, commonStandardScoresDtoRequest.getData().getStandardMatchId())){
            return;
        }

        //非足球不对接 同时对于阶段为比赛中断80，比赛放弃90，也不处理 bug97966
        if (!commonStandardScoresDtoRequest.getData().getSportId().equals(1l) || (commonStandardScoresDtoRequest.getData().getPeriodId().equals(80l)&&!commonStandardScoresDtoRequest.getData().getDataSourceCode().equals("BFZX")) || commonStandardScoresDtoRequest.getData().getPeriodId().equals(90l)) {
            return;
        }
        //只要live事件的
        if (commonStandardScoresDtoRequest.getData().getEventSourceType() != 1&& (!commonStandardScoresDtoRequest.getData().getDataSourceCode().equals("BT"))
                && (!commonStandardScoresDtoRequest.getData().getDataSourceCode().equals("1X"))&& (!commonStandardScoresDtoRequest.getData().getDataSourceCode().equals("LS"))&& (!commonStandardScoresDtoRequest.getData().getDataSourceCode().equals("BFZX"))) {
            return;
        }
        if (commonStandardScoresDtoRequest.getData().getStandardMatchId() == null||commonStandardScoresDtoRequest.getData().getStandardMatchId().equals(0l)) {
            return;
        }
        if(commonStandardScoresDtoRequest.getData().getEventId()==null){
            log.warn("linkId::{}::事件ID为空,无法判断是否为灰色区间，拒绝处理",linkId);
//            return;
        }else {
            MatchEventInfo eventInfo =matchScoresTransSettleService.getEventFromCache(commonStandardScoresDtoRequest.getData().getEventId(),commonStandardScoresDtoRequest.getData().getDataSourceCode());
            if (eventInfo!=null){
                log.info("linkId::{}::eventId::{}",linkId,eventInfo.getId());
            }
            if(commonStandardScoresDtoRequest.getData().getDataSourceCode().equals("BT")||commonStandardScoresDtoRequest.getData().getDataSourceCode().equals("1X")){
                eventInfo=matchScoresTransSettleService.getEventFromCacheByBT(commonStandardScoresDtoRequest.getData().getLinkedId(),commonStandardScoresDtoRequest.getData().getDataSourceCode());
            }
            if(eventInfo==null&& (!commonStandardScoresDtoRequest.getData().getDataSourceCode().equals("BT"))&&(!commonStandardScoresDtoRequest.getData().getDataSourceCode().equals("1X"))
                    &&(!commonStandardScoresDtoRequest.getData().getDataSourceCode().equals("LS"))&&(!commonStandardScoresDtoRequest.getData().getDataSourceCode().equals("BFZX")&&(!commonStandardScoresDtoRequest.getData().getDataSourceCode().equals("N01")))){
                log.info("linkId::{}::事件无法查询到,无法判断是否为灰色区间，拒绝处理",linkId);
                return;
            }else {
                commonStandardScoresDtoRequest.getData().setMatchEventInfo(eventInfo);
                if(eventInfo!=null){
                    commonStandardScoresDtoRequest.getData().setSecondFromStart(eventInfo.getSecondsFromStart());
                }
            }
            if(commonStandardScoresDtoRequest.getData().getMatchEventInfo()==null&&commonStandardScoresDtoRequest.getData().getSecondFromStart()==null){
                return;
            }
        }

        log.info("linkId::{}::StandardMatchScoreConsumer:matchId{},paramData: {}", linkId,commonStandardScoresDtoRequest.getData().getStandardMatchId(), commonStandardScoresDtoRequest.getData());
        //数据转化
        String key = CommonConstant.MATCH_PHASE_SCORE_SETTLE+commonStandardScoresDtoRequest.getData().getStandardMatchId();
            try{
                //redis锁 防止里面 查询后插入的问题
                if(redisService.tryLock(key,key,2,5)) {
                    matchScoresTransSettleService.tansforScoreSettle(commonStandardScoresDtoRequest.getData(), false);
                }else {
                    log.info("linkId::{}::三方比分获取redis锁失败",linkId);
                }
                wsPushService.pushThirdSettleScores(commonStandardScoresDtoRequest.getData().getStandardMatchId(),"goal");
                wsPushService.pushThirdSettleScores(commonStandardScoresDtoRequest.getData().getStandardMatchId(),"corner");
                wsPushService.pushThirdSettleScores(commonStandardScoresDtoRequest.getData().getStandardMatchId(),"fa_card");
            }catch(Exception e){
                log.error("linkId::{}::StandardMatchScoreConsumer error:", linkId, e);
            }finally {
                redisService.unLock(key,key);
            }
        log.info("linkId::{}::StandardMatchScoreConsumer end with cost time:{}",linkId,System.currentTimeMillis()-start);
    }

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMax(threadNum);
        defaultMQPushConsumer.setConsumeThreadMin(threadNum);
    }
}
