package com.panda.merge.mq.producer;

import com.alibaba.fastjson.JSON;

import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.CommonConstant;
import com.panda.merge.constant.SportPeriodConstant;
import com.panda.merge.dto.MatchSettleEventMessage;
import com.panda.merge.dto.MatchSettleScoreMessage;
import com.panda.merge.dto.Request;
import com.panda.merge.model.MatchSettleEvent;
import com.panda.merge.model.MatchSettleScore;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;


@Service
@Slf4j
public class MatchSettleScoresProducer {

    @Autowired
    private RocketMQTemplate rocketMqTemplate;
    @Autowired
    private RedisService redisService;

    private static List<String> list = Arrays.asList("1028", "1029", "1030");
    //结算下半场阶段修改
    private static List<String> settelNum2H =Arrays.asList("109","202","308");
    //结算加时赛下半场阶段修改
    private static List<String> settelNum2ET =Arrays.asList("1018","207","3017");

    //赛事级别重跑结算比分
    public void sendMatchSettleScores(MatchSettleScoreMessage matchSettleScore) {
        String redisKey = CommonConstant.MATCH_SETTLE_SCORE_COUNT+matchSettleScore.getId();
        if (redisService.get(redisKey) != null) {
            return;
        }
        redisService.set(redisKey, 1, RedisConfig.REDIS_FOUR_SECOND);
        if(settelNum2H.contains(matchSettleScore.getSettleNum())){
            matchSettleScore.setPeriodId(8l);
        }
        if(settelNum2ET.contains(matchSettleScore.getSettleNum())){
            matchSettleScore.setPeriodId(43l);
        }
        Request<MatchSettleScoreMessage> reqMessage = new Request<>();
        reqMessage.setLinkId(matchSettleScore.getStandardMatchId()+"_"+matchSettleScore.getSettleNum());
        reqMessage.setData(matchSettleScore);
        MessageBuilder<Request<MatchSettleScoreMessage>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, matchSettleScore.getStandardMatchId()+"_"+matchSettleScore.getSettleNum());
        rocketMqTemplate.send("MATCH_SETTLE_SCORES:" + matchSettleScore.getStandardMatchId(), builder.build());
        log.info("::{}::开始组装赛事比分信息并下发,topic:MATCH_SETTLE_SCORES,request={}", matchSettleScore.getId(), JSON.toJSONString(reqMessage));
    }
    //赛事级别重跑结算事件
    public void sendMatchSettleEvent(MatchSettleEventMessage matchSettleEvent) {
        MatchSettleEventMessage matchSettleEventMessage =new  MatchSettleEventMessage();
        BeanUtils.copyProperties(matchSettleEvent,matchSettleEventMessage);
        if (Integer.valueOf(1).equals(matchSettleEventMessage.getEventType())) {
            matchSettleEventMessage.setFiveMinSection(null);
            matchSettleEventMessage.setFifteenMinSection(null);
        }

        /**
         * 15分钟赋值:  进球由5分钟计算所得
         * 罚牌角球: 直接赋值即可
         * */
        try {
            if (matchSettleEventMessage.getSportId().equals(1L) && matchSettleEventMessage.getEventCode() != null) {
                if (matchSettleEventMessage.getEventCode().equals("corner") || matchSettleEventMessage.getEventCode().equals("fa_card")
                        || matchSettleEventMessage.getEventCode().equals("yellow_card") || matchSettleEventMessage.getEventCode().equals("red_card")) {
                    matchSettleEventMessage.setFifteenMinSection(matchSettleEventMessage.getFiveMinSection());
                } else if (matchSettleEventMessage.getEventCode().equals("goal")) {
                    Long min15 = SportPeriodConstant.FootballPeriod.get15MinPeriodBy5Min(matchSettleEventMessage.getFiveMinSection());
                    if (min15 != null) {
                        matchSettleEventMessage.setFifteenMinSection(min15.toString());
                    }
                }
                // 专为下游量身定做
                if (Integer.valueOf(3).equals(matchSettleEventMessage.getEventType())) {
                    Long periodId = matchSettleEventMessage.getPeriodId();
                    if(matchSettleEventMessage.getPeriodId() == 6) {
                        periodId = 66L;
                    } else if (matchSettleEventMessage.getPeriodId() == 7) {
                        periodId = 77L;
                    }
                    matchSettleEventMessage.setPeriodId(periodId);
                }
            }
        }catch (Exception e){
            log.error("sendMatchSettleEvent ERROR::{}::{}::{}",e.getStackTrace(),e.getMessage(),
                    matchSettleEventMessage);
        }
        Request<MatchSettleEventMessage> reqMessage = new Request<>();
        reqMessage.setLinkId(matchSettleEventMessage.getStandardMatchId()+"_"+matchSettleEventMessage.getSettleNum());
        reqMessage.setData(matchSettleEventMessage);
        MessageBuilder<Request<MatchSettleEventMessage>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS,matchSettleEventMessage.getStandardMatchId()+"_"+matchSettleEventMessage.getSettleNum());
        rocketMqTemplate.send("MATCH_SETTLE_EVENT:" + matchSettleEventMessage.getStandardMatchId(), builder.build());
        log.info("::{}::开始组装赛事比分信息并下发,topic:MATCH_SETTLE_EVENT,request={}", matchSettleEventMessage.getId(), JSON.toJSONString(reqMessage));
    }




    public void sendMatchSettleScores(MatchSettleScore matchSettleScore) {
        String redisKey = CommonConstant.MATCH_SETTLE_SCORE_COUNT+matchSettleScore.getId();
        if (redisService.get(redisKey) != null) {
            return;
        }
        redisService.set(redisKey, 1, RedisConfig.REDIS_FOUR_SECOND);
        if(settelNum2H.contains(matchSettleScore.getSettleNum())){
            matchSettleScore.setPeriodId(8l);
        }
        if(settelNum2ET.contains(matchSettleScore.getSettleNum())){
            matchSettleScore.setPeriodId(43l);
        }
        MatchSettleScoreMessage matchSettleScoreMessage = new MatchSettleScoreMessage();
        BeanUtils.copyProperties(matchSettleScore,matchSettleScoreMessage);
        Request<MatchSettleScoreMessage> reqMessage = new Request<>();
        reqMessage.setLinkId(matchSettleScoreMessage.getStandardMatchId()+"_"+matchSettleScoreMessage.getSettleNum());
        reqMessage.setData(matchSettleScoreMessage);
        MessageBuilder<Request<MatchSettleScoreMessage>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, matchSettleScoreMessage.getStandardMatchId()+"_"+matchSettleScoreMessage.getSettleNum());
        rocketMqTemplate.send("MATCH_SETTLE_SCORES:" + matchSettleScoreMessage.getStandardMatchId(), builder.build());
        log.info("::{}::开始组装赛事比分信息并下发,topic:MATCH_SETTLE_SCORES,request={}", matchSettleScoreMessage.getId(), JSON.toJSONString(reqMessage));
    }

    public void sendMatchSettleScores(MatchSettleScore matchSettleScore, int delayLevel) {
        String redisKey = CommonConstant.MATCH_SETTLE_SCORE_COUNT+matchSettleScore.getId();
        if (redisService.get(redisKey) != null) {
            return;
        }
        redisService.set(redisKey, 1, RedisConfig.REDIS_FOUR_SECOND);
        if(settelNum2H.contains(matchSettleScore.getSettleNum())){
            matchSettleScore.setPeriodId(8l);
        }
        if(settelNum2ET.contains(matchSettleScore.getSettleNum())){
            matchSettleScore.setPeriodId(43l);
        }
        MatchSettleScoreMessage matchSettleScoreMessage = new MatchSettleScoreMessage();
        BeanUtils.copyProperties(matchSettleScore,matchSettleScoreMessage);
        Request<MatchSettleScoreMessage> reqMessage = new Request<>();
        reqMessage.setLinkId(matchSettleScoreMessage.getStandardMatchId()+"_"+matchSettleScoreMessage.getSettleNum());
        reqMessage.setData(matchSettleScoreMessage);
        MessageBuilder<Request<MatchSettleScoreMessage>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, matchSettleScoreMessage.getStandardMatchId()+"_"+matchSettleScoreMessage.getSettleNum());
        rocketMqTemplate.syncSend("MATCH_SETTLE_SCORES:" + matchSettleScoreMessage.getStandardMatchId(), builder.build(), 5000, delayLevel);
        log.info("::{}::开始组装赛事比分信息并下发,topic:MATCH_SETTLE_SCORES,request={}", matchSettleScoreMessage.getId(), JSON.toJSONString(reqMessage));
    }

    public void sendMatchSettleEvent(MatchSettleEvent matchSettleEvent) {
        //点球1028,1029,1030  更改事件编码为goal-penalty

        if (list.contains(matchSettleEvent.getSettleNum()) ) {
            matchSettleEvent.setEventCode("goal-penalty");
        }

        MatchSettleEventMessage matchSettleEventMessage =new  MatchSettleEventMessage();
        BeanUtils.copyProperties(matchSettleEvent,matchSettleEventMessage);
        if(matchSettleEventMessage.getEventType() == 1) {
            matchSettleEventMessage.setFiveMinSection(null);
            matchSettleEventMessage.setFifteenMinSection(null);
        }
        /**
         * 15分钟赋值:  进球由5分钟计算所得
         * 罚牌角球: 直接赋值即可
         * */
        try {
            if (matchSettleEventMessage.getSportId().equals(1L)) {
                if (matchSettleEventMessage.getEventCode().equals("corner") || matchSettleEventMessage.getEventCode().equals("fa_card")
                        || matchSettleEventMessage.getEventCode().equals("yellow_card") || matchSettleEventMessage.getEventCode().equals("red_card")) {
                    matchSettleEventMessage.setFifteenMinSection(matchSettleEventMessage.getFiveMinSection());
                } else if (matchSettleEventMessage.getEventCode().equals("goal")) {
                    Long min15 = SportPeriodConstant.FootballPeriod.get15MinPeriodBy5Min(matchSettleEventMessage.getFiveMinSection());
                    if (min15 != null) {
                        matchSettleEventMessage.setFifteenMinSection(min15.toString());
                    }
                }
                // 专为下游量身定做
                if(matchSettleEventMessage.getEventType() == 3) {
                    Long periodId = matchSettleEventMessage.getPeriodId();
                    if(matchSettleEventMessage.getPeriodId() == 6) {
                        periodId = 66L;
                    } else if (matchSettleEventMessage.getPeriodId() == 7) {
                        periodId = 77L;
                    }
                    matchSettleEventMessage.setPeriodId(periodId);
                }
            }
        }catch (Exception e){
            log.error("sendMatchSettleEvent ERROR::"+JSON.toJSONString(matchSettleEvent)+":::",e.getMessage());
        }


        Request<MatchSettleEventMessage> reqMessage = new Request<>();
        reqMessage.setLinkId(matchSettleEventMessage.getStandardMatchId()+"_"+matchSettleEventMessage.getSettleNum());
        reqMessage.setData(matchSettleEventMessage);
        MessageBuilder<Request<MatchSettleEventMessage>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS,matchSettleEventMessage.getStandardMatchId()+"_"+matchSettleEventMessage.getSettleNum());
        rocketMqTemplate.send("MATCH_SETTLE_EVENT:" + matchSettleEventMessage.getStandardMatchId(), builder.build());
        log.info("::{}::开始组装赛事比分信息并下发,topic:MATCH_SETTLE_EVENT,request={}", matchSettleEventMessage.getId(), JSON.toJSONString(reqMessage));
    }
}
