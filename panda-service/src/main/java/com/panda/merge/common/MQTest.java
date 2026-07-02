package com.panda.merge.common;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Service
public class MQTest {
    public void testMQ(String topic, Message request) throws MQBrokerException {
        if(true){
            throw new MQBrokerException(105,"the xx topic is broker");
        }
    }
}
