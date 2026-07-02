package com.panda.merge.mq;

import com.panda.merge.dto.Request;
import com.panda.merge.dto.response.StandardSettleEventPushDto;
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
@RocketMQMessageListener(topic = "MATCH_SETTLE_EVENT_PUSH", consumerGroup = "ws-group-StandardSettleEventConsumer",consumeThreadMax = 10,
        consumeTimeout = 10000L,
        messageModel = MessageModel.BROADCASTING)
@DependsOn("mergeWebSocketApplication")
public class StandardSettleEventConsumer implements RocketMQListener<Request<StandardSettleEventPushDto>> {

    @Autowired
    PDSubcribe pdSubcribe;

    @Override
    public void onMessage(Request<StandardSettleEventPushDto> request) {
//        log.info("MATCH_SETTLE_EVENT_PUSH:{}",request.getData());
        pdSubcribe.sendStandardSettleEvent(request.getData());
    }
}
