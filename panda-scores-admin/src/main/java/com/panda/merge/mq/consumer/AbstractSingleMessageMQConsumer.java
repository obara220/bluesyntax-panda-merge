package com.panda.merge.mq.consumer;

import com.panda.merge.dto.Request;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

/**
 * @author Hunta
 * @since 9/11/2023
 *
 * 每次一条一条消息消费的消费者
 */


@Slf4j
public abstract class AbstractSingleMessageMQConsumer<T extends Request<?>> extends AbstractMQConsumer<T>{

    @Override
    protected MessageListenerConcurrently getBizLogic() {
        return new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> messageExtList, ConsumeConcurrentlyContext context) {
                for (MessageExt msg : messageExtList){
                    try {
                        processMessage(extractRequest(msg));
                    }catch (Exception e){
                        log.error("consume message error",e);
                        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                    }
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        };
    }
    /**
     * 主要业务逻辑
     * @param t
     */
    abstract void processMessage(T t);
}
