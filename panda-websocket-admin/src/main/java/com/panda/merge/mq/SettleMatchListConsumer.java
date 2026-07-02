package com.panda.merge.mq;

import com.panda.merge.dto.Request;
import com.panda.merge.dto.response.StandardSettleEventPushDto;
import com.panda.merge.dto.settle.MatchListSettleDto;
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
@RocketMQMessageListener(topic = "MATCH_LIST_SETTLE_PUSH", consumerGroup = "ws-group-SettleMatchListConsumer",consumeThreadMax = 10,
        consumeTimeout = 10000L,
        messageModel = MessageModel.BROADCASTING)
@DependsOn("mergeWebSocketApplication")
public class SettleMatchListConsumer implements RocketMQListener<Request<MatchListSettleDto>> {

    @Autowired
    PDSubcribe pdSubcribe;

    @Override
    public void onMessage(Request<MatchListSettleDto> request) {
//        log.info("MATCH_LIST_SETTLE_PUSH:{}",request.getData());
        pdSubcribe.sendMatchListSettle(request.getData());
    }
}
