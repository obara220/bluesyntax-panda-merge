package com.panda.merge.mq;

import com.panda.merge.dto.Request;
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
@RocketMQMessageListener(topic = "PD_FOOTBALL_EVENT", consumerGroup = "scores-group-PDEventConsumer",consumeThreadMax = 2,
        consumeTimeout = 10000L,
        messageModel = MessageModel.BROADCASTING)
@DependsOn("mergeWebSocketApplication")
public class PDEventConsumer implements RocketMQListener<Request<String>> {
    @Autowired
    PDSubcribe pdSubcribe;


    @Override
    public void onMessage(Request<String> request) {
//        log.info("PD_FOOTBALL_EVENT_GET:{}",request.getData());
        pdSubcribe.sendPdEvent(request.getData());
    }

}
