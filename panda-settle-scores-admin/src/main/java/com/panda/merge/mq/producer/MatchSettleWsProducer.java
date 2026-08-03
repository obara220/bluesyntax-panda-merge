package com.panda.merge.mq.producer;

import com.alibaba.fastjson.JSON;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.settle.*;
import com.panda.merge.model.MatchSettleScore;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class MatchSettleWsProducer {

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    public void pushStandardSettleScores(List list,Long standardMatchId,String eventCode){
        StandardSettleScoresPushDto dto =new StandardSettleScoresPushDto();
        dto.setData(list);
        dto.setEventCode(eventCode);
        dto.setStandardMatchId(standardMatchId);
        Request<StandardSettleScoresPushDto> reqMessage = new Request<>();
        reqMessage.setLinkId(standardMatchId.toString());
        reqMessage.setData(dto);
        MessageBuilder<Request<StandardSettleScoresPushDto>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, standardMatchId);
        rocketMqTemplate.send("MATCH_SETTLE_SCORES_PUSH:" +standardMatchId, builder.build());
        log.info("::{}::开始组装结算比分下发,topic:MATCH_SETTLE_SCORES_PUSH,request={}", standardMatchId, JSON.toJSONString(reqMessage));
    }

    public void pushStandardSettleEvent(List list,Long standardMatchId,String eventCode){
        StandardSettleScoresPushDto dto =new StandardSettleScoresPushDto();
        dto.setStandardMatchId(standardMatchId);
        dto.setData(list);
        dto.setEventCode(eventCode);
        Request<StandardSettleScoresPushDto> reqMessage = new Request<>();
        reqMessage.setLinkId(standardMatchId.toString());
        reqMessage.setData(dto);
        MessageBuilder<Request<StandardSettleScoresPushDto>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, standardMatchId);
        rocketMqTemplate.send("MATCH_SETTLE_EVENT_PUSH:" +standardMatchId, builder.build());
        log.info("::{}::开始组装结算事件下发,topic:MATCH_SETTLE_EVENT_PUSH,request={}", standardMatchId, JSON.toJSONString(reqMessage));
    }

    public void pushThirdSettleScores(ThirdMatchSettleScoresDto dto, Long standardMatchId, String eventCode) {
        Request<ThirdMatchSettleScoresDto> reqMessage = new Request<>();
        reqMessage.setLinkId(standardMatchId.toString());
        reqMessage.setData(dto);
        MessageBuilder<Request<ThirdMatchSettleScoresDto>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, standardMatchId);
        rocketMqTemplate.send("MATCH_SETTLE_THIRD_SCORES_PUSH:" +standardMatchId, builder.build());
        log.info("::{}::开始组装三方结算比分下发,topic:MATCH_SETTLE_THIRD_SCORES_PUSH,request={}", standardMatchId, JSON.toJSONString(reqMessage));
    }

    public void pushThirdSettleEvent(ThirdMatchSettleEventDto dto, Long standardMatchId, String eventCode) {
        Request<ThirdMatchSettleEventDto> reqMessage = new Request<>();
        reqMessage.setLinkId(standardMatchId.toString());
        reqMessage.setData(dto);
        MessageBuilder<Request<ThirdMatchSettleEventDto>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, standardMatchId);
        rocketMqTemplate.send("MATCH_SETTLE_THIRD_EVENT_PUSH:" +standardMatchId, builder.build());
        log.info("::{}::开始组装三方结算比分下发,topic:MATCH_SETTLE_THIRD_EVENT_PUSH,request={}", standardMatchId, JSON.toJSONString(reqMessage));
    }

    public void pushSettleMatchList(MatchListSettleDto dto){
        Request<MatchListSettleDto> reqMessage = new Request<>();
        reqMessage.setLinkId(dto.getStandardMatchId().toString());
        reqMessage.setData(dto);
        MessageBuilder<Request<MatchListSettleDto>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, dto.getStandardMatchId());
        rocketMqTemplate.send("MATCH_LIST_SETTLE_PUSH:" +dto.getStandardMatchId(), builder.build());
        log.info("::{}::开始推送结算下发赛事列表,topic:MATCH_LIST_SETTLE_PUSH,request={}", dto.getStandardMatchId(), JSON.toJSONString(reqMessage));
    }

    public void pushGlobalAutoSettleStatus(AutoSettleDataSourceDto dto){
        Request<AutoSettleDataSourceDto> reqMessage = new Request<>();
        reqMessage.setLinkId(dto.getStandardMatchId());
        reqMessage.setData(dto);
        MessageBuilder<Request<AutoSettleDataSourceDto>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, dto.getStandardMatchId());
        rocketMqTemplate.send("GLOBAL_AUTO_SETTLE_STATUS_PUSH:" +dto.getStandardMatchId(), builder.build());
        log.info("::{}::开始推送数据商自动结算开关状态,topic:GLOBAL_AUTO_SETTLE_STATUS_PUSH,request={}", dto.getStandardMatchId(), JSON.toJSONString(reqMessage));
    }

    /**
     * 特殊玩法的结算推送
     * */
    public void pushSPSettleMatchStatus(AutoSettleDataSourceDto dto){
        Request<AutoSettleDataSourceDto> reqMessage = new Request<>();
        reqMessage.setLinkId(dto.getStandardMatchId());
        reqMessage.setData(dto);
        MessageBuilder<Request<AutoSettleDataSourceDto>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, dto.getStandardMatchId());
        rocketMqTemplate.send("SP_SETTLE_MATCH_PUSH:" +dto.getStandardMatchId(), builder.build());
        log.info("::{}::开始推送数据商自动结算开关状态,topic:SP_SETTLE_MATCH_PUSH,request={}", dto.getStandardMatchId(), JSON.toJSONString(reqMessage));
    }

    public void pushMatchSettleRollBackStatus(MatchSettleRollBackDto dto){
        Request<MatchSettleRollBackDto> reqMessage = new Request<>();
        reqMessage.setLinkId(dto.getStandardMatchId().toString());
        reqMessage.setData(dto);
        MessageBuilder<Request<MatchSettleRollBackDto>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, dto.getStandardMatchId());
        rocketMqTemplate.send("MATCH_SETTLE_ROLL_BACK_STATUS_PUSH:" +dto.getStandardMatchId(), builder.build());
        log.info("::{}::开始推送赛事回滚状态,topic:MATCH_SETTLE_ROLL_BACK_STATUS_PUSH,request={}", dto.getStandardMatchId(), JSON.toJSONString(reqMessage));
    }

}
