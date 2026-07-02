package com.panda.merge.rocketmq.consumer;

import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.ChangeSoldMessage;
import com.panda.merge.rocketmq.processor.ChangeSoldMessageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RocketMQMessageListener(
        topic = "CHANGE_SOLD_MESSAGE",
        consumerGroup = "odds-group-CHANGE_SOLD_MESSAGE",
        consumeThreadMax = 256,
        consumeTimeout = 10000L
)
@DependsOn("oddsAdminApplication")
public class ChangeSoldMessageConsumer implements RocketMQListener<Request<ChangeSoldMessage>> {
    @Lazy
    @Autowired
    ChangeSoldMessageProcessor changeSoldMessageProcessor;
    @Override
    public void onMessage(Request<ChangeSoldMessage> changeSoleMessageRequest) {
        changeSoldMessageProcessor.changeSoldMessage(changeSoleMessageRequest);
    }
}
