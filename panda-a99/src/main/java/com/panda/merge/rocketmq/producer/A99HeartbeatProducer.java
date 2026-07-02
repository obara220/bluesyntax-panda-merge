package com.panda.merge.rocketmq.producer;

import com.panda.merge.dto.HeartMessage;
import com.panda.merge.dto.Request;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import static com.panda.merge.constant.ConstantSystem.STANDARD_HEARTBEAT;

@Slf4j
@Component
public class A99HeartbeatProducer {

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    public void sendA99Heartbeat(Request<HeartMessage> request) {
        MessageBuilder<Request<HeartMessage>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, request.getLinkId());
        rocketMqTemplate.send(STANDARD_HEARTBEAT, builder.build());
    }

}
