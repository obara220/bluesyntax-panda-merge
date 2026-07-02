package com.panda.merge.mq.consumer;

import com.panda.merge.dto.Request;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Hunta
 * @since 9/11/2023
 *
 * 一次消费多条消息
 */

@Slf4j
public abstract class AbstractMultipleMessageMQConsumer <T extends Request<?>> extends AbstractMQConsumer<T>{

    @Override
    protected MessageListenerConcurrently getBizLogic() {
        return new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> messageExtList, ConsumeConcurrentlyContext context) {
                try {
                    List<T> entityList = messageExtList.stream()
                            .map(m -> extractRequest(m)).collect(Collectors.toList());
                    processMessageList(entityList);
                } catch (Exception e) {
                    log.error("consume message error", e);
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        };
    }
    /**
     * 主要业务逻辑
     */
    abstract void processMessageList(List<T> list);
}
