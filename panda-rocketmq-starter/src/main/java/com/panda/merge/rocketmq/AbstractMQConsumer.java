package com.panda.merge.rocketmq;

import cn.hutool.core.lang.TypeReference;
import com.alibaba.fastjson.JSON;
import com.panda.merge.dto.Request;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.autoconfigure.RocketMQProperties;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *消费者抽象类
 *
 * @param <T> 消息体的类型
 */

@Slf4j
public abstract class AbstractMQConsumer<T extends Request<?>>
        implements InitializingBean, RestartableConsumer, DisposableBean {
    private static final AtomicInteger INSTANCE_NUM = new AtomicInteger(0);

    private DefaultMQPushConsumer consumer;

    private DefaultMQPushConsumer secondConsumer;

    // mq consumer优雅关闭下线，最多等多久。注意是单个consumer的等待时间
    private final Long awaitShutDownAwaitMilli = 5000L;

    private  static final Integer consumeMessageBatchMaxSize =20;

    @Autowired
    private RocketMQProperties rocketMQProperties;

    @Autowired(required = false)
    private RocketMQSecondConfig secondProperties;

    private TypeReference<T> typeReference;

    @Override
    public void afterPropertiesSet() throws Exception {
        MqConsumerConfig mqConsumerConfig = buildConfig();
        this.typeReference = mqConsumerConfig.getMessageType();
        consumer = new DefaultMQPushConsumer();
        initConsumer(consumer, mqConsumerConfig, rocketMQProperties.getNameServer());
        initSecondConsumer();
    }

    @Override
    public synchronized void restartConsumer() throws MQClientException {
        //  consumer shutdown是幂等的，已经关闭了的话，这里不会有任何副作用
        destroy();
        consumer = new DefaultMQPushConsumer();
        initConsumer(consumer, buildConfig(), rocketMQProperties.getNameServer());
        initSecondConsumer();

    }

    protected abstract MessageListenerConcurrently getBizLogic();

    @Override
    public void destroy()  {
        consumer.shutdown();
        if (Objects.nonNull(secondConsumer)) {
            secondConsumer.shutdown();
        }
    }

    private void initSecondConsumer() throws MQClientException {
        EnableSecondRocketMQCluster ann = this.getClass().getAnnotation(EnableSecondRocketMQCluster.class);
        if (Objects.nonNull(ann) && Objects.nonNull(secondProperties) &&
                StringUtils.isNotBlank(secondProperties.getSlaveNamesrvAddr())) {
            try {

                secondConsumer = new DefaultMQPushConsumer();
                secondConsumer.setInstanceName(
                        secondConsumer.getInstanceName() + "_second_" + INSTANCE_NUM.incrementAndGet());
                MqConsumerConfig mqConsumerConfig = buildConfig();
                mqConsumerConfig.setConsumerGroupName(mqConsumerConfig.getConsumerGroupName()+"_second");
                initConsumer(secondConsumer, mqConsumerConfig, secondProperties.getSlaveNamesrvAddr());
                log.info("init second consumer success {}", this.getClass().getName());
            } catch (Exception e) {
                log.error("init second consumer error {}", this.getClass().getName(), e);
            }

        }
    }

    /**
     * 配置并且初始化消费者
     */
    private void initConsumer(DefaultMQPushConsumer consumer, MqConsumerConfig mqConsumerConfig, String nameServer)
            throws MQClientException {
        consumer.subscribe(mqConsumerConfig.getTopic(),"*");
        consumer.setConsumerGroup(mqConsumerConfig.getConsumerGroupName());
        consumer.setAwaitTerminationMillisWhenShutdown(awaitShutDownAwaitMilli);
        consumer.setConsumeMessageBatchMaxSize(consumeMessageBatchMaxSize);
        consumer.setNamesrvAddr(nameServer);
        processConsumerConfigDetail(consumer,mqConsumerConfig);
        consumer.registerMessageListener(getBizLogic());
        consumer.setAwaitTerminationMillisWhenShutdown(awaitShutDownAwaitMilli);
        consumer.start();
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
        if(consumerConfigDetail.getPullInterval() != null && consumerConfigDetail.getPullInterval() >= 0) {
            consumer.setPullInterval(consumerConfigDetail.getPullInterval());
        }
    }


    protected abstract MqConsumerConfig buildConfig();

    protected  T extractRequest(MessageExt msg) {
        return  JSON.parseObject(msg.getBody()
                , typeReference.getType());
    }

    public synchronized DefaultMQPushConsumer getConsumer() {
        return consumer;
    }
}
