package com.panda.merge.mq;

import com.panda.merge.dto.Request;
import com.panda.merge.dto.response.StandardSettleScoresPushDto;
import com.panda.merge.handler.PDSubcribe;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Slf4j
@Component
@Service
@RocketMQMessageListener(topic = "MATCH_SETTLE_SCORES_PUSH", consumerGroup = "ws-group-StandardSettleScoresConsumer",consumeThreadMax = 10,
        consumeTimeout = 10000L,
        messageModel = MessageModel.BROADCASTING)
@DependsOn("mergeWebSocketApplication")
public class StandardSettleScoresConsumer implements RocketMQListener<Request<StandardSettleScoresPushDto>> {
    @Autowired
    PDSubcribe pdSubcribe;
    @Override
    public void onMessage(Request<StandardSettleScoresPushDto> request) {
//        log.info("MATCH_SETTLE_SCORES_PUSH:{}",request.getData());
        pdSubcribe.sendStandardSettleScores(request.getData());
    }
}
