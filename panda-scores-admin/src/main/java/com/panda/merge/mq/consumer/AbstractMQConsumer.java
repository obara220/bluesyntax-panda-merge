package com.panda.merge.mq.consumer;

import cn.hutool.core.lang.TypeReference;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.config.mq.ConsumerConfigDetail;
import com.panda.merge.config.mq.MqConsumerConfig;
import com.panda.merge.config.mq.RestartableConsumer;
import com.panda.merge.dto.Request;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

/**
 *消费者抽象类
 *
 * @param <T> 消息体的类型
 */

@Slf4j
public abstract class AbstractMQConsumer<T extends Request<?>> implements InitializingBean, RestartableConsumer {
    private DefaultMQPushConsumer consumer= new DefaultMQPushConsumer();

    // mq consumer优雅关闭下线，最多等多久。注意是单个consumer的等待时间
    private final Long awaitShutDownAwaitMilli = 5000L;

    private  static final Integer consumeMessageBatchMaxSize =20;

    @Value("${rocketmq.name-server}")
    private String nameServerAddr;

    // 根据环境tag来判断是否应该启动rocketMq
    @Value("${rocketmq.consumer.tag.switch:}")
    private String mqTagSwitchRule;

    private TypeReference<T> typeReference;

    @NacosValue(value = "${datacenter.scores.switch:false}", autoRefreshed = true)
    private Boolean datacenterMergeSwitch;

    @Override
    public void afterPropertiesSet() throws Exception {
        initConcurrentConsumer();
    }

    protected abstract MessageListenerConcurrently getBizLogic();

    /**
     * 配置并且初始化消费者
     */
    private void initConcurrentConsumer() throws MQClientException {
        MqConsumerConfig mqConsumerConfig = buildConfig();
        consumer.subscribe(mqConsumerConfig.getTopic(),"*");
        consumer.setConsumerGroup(mqConsumerConfig.getConsumerGroupName());
        this.typeReference=mqConsumerConfig.getMessageType();
        consumer.setAwaitTerminationMillisWhenShutdown(awaitShutDownAwaitMilli);
        consumer.setConsumeMessageBatchMaxSize(consumeMessageBatchMaxSize);
        consumer.setNamesrvAddr(nameServerAddr);


        processConsumerConfigDetail(consumer,mqConsumerConfig);
        consumer.registerMessageListener(getBizLogic());
        consumer.start();
    }


    @Override
    public synchronized void restartConsumer() throws MQClientException {
        if (datacenterMergeSwitch) {
            return;
        }
        consumer.setAwaitTerminationMillisWhenShutdown(awaitShutDownAwaitMilli);
        //  consumer shutdown是幂等的，已经关闭了的话，这里不会有任何副作用
        consumer.shutdown();
        consumer=new DefaultMQPushConsumer();
        initConcurrentConsumer();
    }

    /**
     * 消费者高级配置
     * @param consumer
     * @param mqConsumerConfig
     */
    protected void processConsumerConfigDetail(DefaultMQPushConsumer consumer, MqConsumerConfig mqConsumerConfig) {
        ConsumerConfigDetail consumerConfigDetail = mqConsumerConfig.getConsumerConfigDetail();
        if(consumerConfigDetail.getThreadNumber()!=null){
            consumer.setConsumeThreadMin(consumerConfigDetail.getThreadNumber());
            consumer.setConsumeThreadMax(consumerConfigDetail.getThreadNumber());
        }
        if( consumerConfigDetail.getMessageSize()!=null){
            consumer.setConsumeMessageBatchMaxSize(consumerConfigDetail.getMessageSize());
        }
        if( consumerConfigDetail.getPullBatchSize()!=null){
            consumer.setPullBatchSize(consumerConfigDetail.getPullBatchSize());
        }
    }


    abstract MqConsumerConfig buildConfig();

    protected  T extractRequest(MessageExt msg) {
        return  JSON.parseObject(msg.getBody()
                , typeReference.getType());
    }

    public synchronized DefaultMQPushConsumer getConsumer() {
        return consumer;
    }
}
