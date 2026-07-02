package com.panda.merge.config.mq;

import org.apache.rocketmq.client.exception.MQClientException;

/**
 * @author Hunta
 * @since 9/11/2023
 */
public interface RestartableConsumer {
   void restartConsumer() throws MQClientException;
}
