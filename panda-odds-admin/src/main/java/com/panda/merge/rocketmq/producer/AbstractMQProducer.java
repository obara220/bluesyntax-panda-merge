package com.panda.merge.rocketmq.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * AbstractMQProducer
 *
 * @description:
 * @date: 4/28/2025
 **/
@Component
@Slf4j
public abstract class AbstractMQProducer {

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    public <T> void send(T payload, String topic, String tag) {
        MessageBuilder<T> builder = MessageBuilder.withPayload(payload).setHeader(MessageConst.PROPERTY_KEYS, tag);
        rocketMqTemplate.send(topic + ":" + tag, builder.build());
        log.info("mq send successfully linkId:{},topic:{},tag:{},payload:{}", tag, topic, tag, payload);
    }

}
