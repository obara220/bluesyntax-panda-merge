package com.panda.merge.mq.consumer;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.common.utils.IdWorker;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ScoreCodeDto;
import com.panda.merge.mapper.MatchSettleResultMapper;
import com.panda.merge.model.MatchSettleEvent;
import com.panda.merge.model.MatchSettleResult;
import com.panda.merge.model.MatchSettleResultExample;
import com.panda.merge.mq.producer.CommonProducer;
import com.panda.merge.mq.producer.ScoreSettleResultProducer;
import com.panda.merge.utils.SettleNumToScoreCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 结算2.0事件下发比分中心
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = "MATCH_SETTLE_EVENT",
        consumerGroup = "scores-group-MATCH_SETTLE_EVENT-NEW",
        consumeThreadMax = 20,
        consumeTimeout = 10000L
)
@DependsOn("scoresAdminApplication")
public class MatchSettleNewEventConsumer implements RocketMQListener<Request<MatchSettleEvent>> {

    @Autowired
    ScoreSettleResultProducer scoreSettleResultProducer;


    @Autowired
    RedisService redisService;

    @Autowired
    MatchSettleResultMapper matchSettleResultMapper;

    //结算同步比分中心开关key
    private static final String SETTLE_SYNC_SCORES_KEY = "settle_sync_scores";
    @NacosValue(value = "${datacenter.scores.switch:false}", autoRefreshed = true)
    private Boolean datacenterMergeSwitch;
    @Autowired
    CommonProducer commonProducer;
    @Override
    public void onMessage(Request<MatchSettleEvent> request) {
        log.info("MatchSettleNewEventConsumer MQ消费数据开始...{}",datacenterMergeSwitch);
        if (datacenterMergeSwitch && commonProducer.getDatacenterMatchIds(request.getData().getStandardMatchId().toString())) {
            //MQ消息转发给数据中心
            commonProducer.asyncSend(request, "datacenter-MATCH_SETTLE_EVENT",request.getLinkId());
            return;
        }
        String linkId = request.getLinkId();
        Long start = System.currentTimeMillis();
        log.info("{} MatchSettleEventConsumer事件比分中心处理开始：{}", linkId, start);
        if (request == null || request.getData() == null) {
            return;
        }

        //判断同步比分中心开关是否打开
//        Object syncFlag = redisService.get(SETTLE_SYNC_SCORES_KEY);
//        if(syncFlag == null || !(boolean)syncFlag){
//            log.warn("结算同步比分中心总开关为关闭状态,{}",linkId);
//            return;
//        }
        //1、过滤
        if (!check(request.getData())) {
            log.info("{} MatchSettleEventConsumer事件过滤:{}", linkId, request.getData());
            return;
        }
        //2、处理同步比分中心的逻辑
        handleData(request);

        log.info("{} MatchSettleEventConsumer事件比分中心处理结束耗时：{}", linkId, System.currentTimeMillis()-start);
    }

    /**
     * 检查mq消息是否合法
     * @param data
     * @return
     */
    private boolean check(MatchSettleEvent data){
        //非足球不对接
        if (!data.getSportId().equals(1l)) {
            return false;
        }
        //非已结算不对接
        if (data.getStatus()==null || !data.getStatus().equals(3)) {
            return false;
        }
        //非进球比分事件不对接
        if(data.getEventType()==null ||  !data.getEventType().equals(1) || !data.getEventType().equals(3)){
            return false;
        }
        return true;
    }


    /**
     * 处理同步比分中心的逻辑
     * @param request
     */
    private void handleData(Request<MatchSettleEvent> request) {
        MatchSettleEvent matchSettleEvent = request.getData();
        //有code就是 点球
        String scoreCode = SettleNumToScoreCodeUtils.getScoreCodeBySettleEvent(matchSettleEvent);
        if(scoreCode!=null){
            updateAndSentEventMsg(matchSettleEvent,scoreCode,matchSettleEvent.getT1(),matchSettleEvent.getT2());
        }else {
            //红牌比分
            ScoreCodeDto redCodeDto = SettleNumToScoreCodeUtils.countSettleRedCardEvent(matchSettleEvent);
            if(redCodeDto!=null){
                updateAndSentEventMsg(matchSettleEvent,redCodeDto.getScoreCode(),redCodeDto.getT1(),redCodeDto.getT2());
                //如果是下半场没有罚牌则同时计算下半场红牌比分
                if(redCodeDto.getScoreCode().equals("S11001")){
                    this.countAndSend2HTRedCard(matchSettleEvent,redCodeDto);
                }
            }
            //黄牌比分
            ScoreCodeDto yellowCodeDto = SettleNumToScoreCodeUtils.countSettleYellowCardEvent(matchSettleEvent);
            if(yellowCodeDto!=null){
                updateAndSentEventMsg(matchSettleEvent,yellowCodeDto.getScoreCode(),yellowCodeDto.getT1(),yellowCodeDto.getT2());
                //如果是下半场没有罚牌则同时计算下半场黄牌比分
                if(yellowCodeDto.getScoreCode().equals("S12001")){
                    this.countAndSend2HTYellowCard(matchSettleEvent,yellowCodeDto);
                }
            }
        }

    }
    // 查询 和更新 下半场黄牌比分
    private void countAndSend2HTYellowCard(MatchSettleEvent matchSettleEvent, ScoreCodeDto redCodeDto) {
        try {
            MatchSettleResultExample matchSettleResultExample = new MatchSettleResultExample();
            //查询上半场黄牌
            matchSettleResultExample.createCriteria().andStandardMatchIdEqualTo(matchSettleEvent.getStandardMatchId()).andScoreCodeEqualTo("S14");
            List<MatchSettleResult> list = matchSettleResultMapper.selectByExample(matchSettleResultExample);
            if (list.size() == 0) {
                return;
            }
            MatchSettleResult matchSettleResult = list.get(0);
            Integer red2H_T1 = redCodeDto.getT1()-matchSettleResult.getT1();
            Integer red2H_T2 = redCodeDto.getT2()-matchSettleResult.getT2();
            if(red2H_T1<0){
                red2H_T1 = 0;
            }
            if(red2H_T2<0){
                red2H_T2 = 0;
            }
            //更新下半场黄牌
            updateAndSentEventMsgById(matchSettleEvent.getStandardMatchId(),"S1402", red2H_T1, red2H_T2);
        }catch (Exception e){
            log.error(":处理数据发生异常:", e);
        }
    }
    // 查询 和更新 下半场红牌比分
    private void countAndSend2HTRedCard(MatchSettleEvent matchSettleEvent, ScoreCodeDto redCodeDto) {
        try {
            MatchSettleResultExample matchSettleResultExample = new MatchSettleResultExample();
            //查询上半场红牌
            matchSettleResultExample.createCriteria().andStandardMatchIdEqualTo(matchSettleEvent.getStandardMatchId()).andScoreCodeEqualTo("S13");
            List<MatchSettleResult> list = matchSettleResultMapper.selectByExample(matchSettleResultExample);
            if (list.size() == 0) {
                return;
            }
            MatchSettleResult matchSettleResult = list.get(0);
            Integer red2H_T2 = redCodeDto.getT2()-matchSettleResult.getT2();
            Integer red2H_T1 = redCodeDto.getT1()-matchSettleResult.getT1();
            if(red2H_T1<0){
                red2H_T1 = 0;
            }
            if(red2H_T2<0){
                red2H_T2 = 0;
            }
            //更新下半场红牌
            updateAndSentEventMsgById(matchSettleEvent.getStandardMatchId(),"S1302", red2H_T1, red2H_T2);
        }catch (Exception e){
            log.error(":处理数据发生异常:", e);
        }

    }

    private void updateAndSentEventMsg(MatchSettleEvent matchSettleEvent, String scoreCode, Integer t1, Integer t2){
        MatchSettleResultExample matchSettleResultExample=new MatchSettleResultExample();
        matchSettleResultExample.createCriteria().andStandardMatchIdEqualTo(matchSettleEvent.getStandardMatchId()).andScoreCodeEqualTo(scoreCode);

        List<MatchSettleResult>  list = matchSettleResultMapper.selectByExample(matchSettleResultExample);
        MatchSettleResult matchSettleResult= null;
        if(list.size()==0){
            matchSettleResult=this.createMatchSettleResult(matchSettleEvent,scoreCode,t1,t2);
        }else {
            matchSettleResult=list.get(0);
            matchSettleResult.setT1(t1);
            matchSettleResult.setT2(t2);
            matchSettleResult.setSettleTime(matchSettleEvent.getModifyTime());
            matchSettleResultMapper.updateByPrimaryKey(matchSettleResult);
        }
        log.info("更新结算比分到表:{} ",matchSettleResult);
        //发送MQ到下游
        scoreSettleResultProducer.sendResult(matchSettleResult);
    }

    private void updateAndSentEventMsgById(Long standardMatchId, String scoreCode, Integer t1, Integer t2){
        MatchSettleResultExample matchSettleResultExample=new MatchSettleResultExample();
        matchSettleResultExample.createCriteria().andStandardMatchIdEqualTo(standardMatchId).andScoreCodeEqualTo(scoreCode);

        List<MatchSettleResult>  list = matchSettleResultMapper.selectByExample(matchSettleResultExample);
        MatchSettleResult matchSettleResult= null;
        if(list.size()==0){
            matchSettleResult=this.createMatchSettleResultById(standardMatchId,scoreCode,t1,t2);
        }else {
            matchSettleResult=list.get(0);
            matchSettleResult.setT1(t1);
            matchSettleResult.setT2(t2);
            matchSettleResult.setSettleTime(System.currentTimeMillis());
            matchSettleResultMapper.updateByPrimaryKey(matchSettleResult);
        }
        log.info("更新结算比分到表:{} ",matchSettleResult);
        //发送MQ到下游
        scoreSettleResultProducer.sendResult(matchSettleResult);
    }

    private MatchSettleResult createMatchSettleResultById(Long standardMatchId, String scoreCode,Integer t1, Integer t2) {
        MatchSettleResult matchSettleResult =new MatchSettleResult();
        matchSettleResult.setId(IdWorker.getId());
        matchSettleResult.setScoreCode(scoreCode);
        matchSettleResult.setModifyTime(System.currentTimeMillis());
        matchSettleResult.setCreateTime(System.currentTimeMillis());
        matchSettleResult.setSportId(1L);
        matchSettleResult.setSettleTime(System.currentTimeMillis());
        matchSettleResult.setStandardMatchId(standardMatchId);
        matchSettleResult.setT1(t1);
        matchSettleResult.setT2(t2);
        //1 比分 2 事件
        matchSettleResult.setScoresType(1);
        matchSettleResult.setSettleScoreEventId(standardMatchId);
        matchSettleResult.setLinkId(standardMatchId+"_"+scoreCode);
        matchSettleResultMapper.insert(matchSettleResult);
        return matchSettleResult;
    }

    private MatchSettleResult createMatchSettleResult(MatchSettleEvent matchSettleEvent, String scoreCode, Integer t1, Integer t2) {
        MatchSettleResult matchSettleResult =new MatchSettleResult();
        matchSettleResult.setId(IdWorker.getId());
        matchSettleResult.setScoreCode(scoreCode);
        matchSettleResult.setT2( t2);
        matchSettleResult.setT1(t1);

        matchSettleResult.setModifyTime(System.currentTimeMillis());
        matchSettleResult.setCreateTime(System.currentTimeMillis());
        matchSettleResult.setSportId(matchSettleEvent.getSportId());
        matchSettleResult.setSettleTime(matchSettleEvent.getModifyTime());
        matchSettleResult.setStandardMatchId(matchSettleEvent.getStandardMatchId());
        //1 比分 2 事件
        matchSettleResult.setScoresType(1);
        matchSettleResult.setSettleScoreEventId(matchSettleEvent.getId());
        matchSettleResult.setLinkId(matchSettleEvent.getId().toString());
        matchSettleResultMapper.insert(matchSettleResult);
        return matchSettleResult;
    }


}
