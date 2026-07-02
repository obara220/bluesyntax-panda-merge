package com.panda.merge.rocketmq;

import cn.hutool.core.lang.TypeReference;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Hunta
 * @since 9/9/2023
 */

@Setter
@Getter
public class MqConsumerConfig {
    private String topic;
    private String consumerGroupName;
    private TypeReference messageType;
    private ConsumerConfigDetail consumerConfigDetail;


    /**
     *
     * @param topic
     * @param consumerGroupName
     * @param messageType
     */
    public MqConsumerConfig(String topic, String consumerGroupName, TypeReference messageType) {
        this.topic = topic;
        this.consumerGroupName = consumerGroupName;
        this.messageType = messageType;
    }

    public MqConsumerConfig(String topic, String consumerGroupName, TypeReference messageType, ConsumerConfigDetail consumerConfigDetail) {
        this.topic = topic;
        this.consumerGroupName = consumerGroupName;
        this.consumerConfigDetail = consumerConfigDetail;
        this.messageType = messageType;
    }
}
