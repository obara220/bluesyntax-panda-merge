package com.panda.merge.rocketmq.spare;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.rocketmq.processor.MatchEventInfoProcessor;
import com.panda.merge.rocketmq.producer.DataCenterProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.panda.merge.constant.ConstantSystem.CONSUME_REALTIME_GROUP;
import static com.panda.merge.constant.ConstantSystem.THIRD_MATCH_EVENT_INFO_API;

/**
 * 备用单条事件MQ消费
 *
 * @author tell
 * @since 2025年02月01日17:45:05
 */
@Slf4j
@Component
public class SpareThirdMatchEventInfoConsumer {

    @Value("${slaveNamesrvAddr}")
    private String nameServers;

    @Value("${rocketmq.producer.group}")
    private String groupName;

    private DefaultMQPushConsumer consumer;

    @Autowired
    private MatchEventInfoProcessor matchEventInfoProcessor;
    @NacosValue(value = "${consumer.switch.realtime_nonrealtime:true}", autoRefreshed = true)
    private boolean realtimeSwitch;
    @NacosValue(value = "${consumer.switch.realtime_nonrealtime_event:true}", autoRefreshed = true)
    private boolean realtimeEventSwitch;
    @Resource
    private DataCenterProducer dataCenterProducer;

    @PostConstruct
    public void initConsumer() throws MQClientException {
        log.info("SpareThirdMatchEventInfoConsumer:initConsumer");
        // 创建消费者并设置配置
        consumer = new DefaultMQPushConsumer(CONSUME_REALTIME_GROUP + THIRD_MATCH_EVENT_INFO_API);
        consumer.setInstanceName("SpareThirdMatchEventInfoConsumer");
        // Set name servers
        consumer.setNamesrvAddr(nameServers);
        // 设置最大并发线程数
        consumer.setConsumeThreadMax(128);
        // 设置最小并发线程数
        consumer.setConsumeThreadMin(128);
        // 从最后一个偏移量开始消费
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        // 一次处理一条消息
        consumer.setConsumeMessageBatchMaxSize(1);
        // 订阅备用-MQ主题
        log.info("SpareThirdMatchEventInfoConsumer 订阅主题,单条三方赛事事件={}", THIRD_MATCH_EVENT_INFO_API);
        consumer.subscribe(THIRD_MATCH_EVENT_INFO_API, "*");
        // 注册消息监听器
        consumer.registerMessageListener(this::processMessages);
        // 启动消费者
        consumer.start();
        log.info("SpareThirdMatchEventInfoConsumer started successfully");
    }

    /**
     * 消费panda事件数据发送到SK-MQ
     */
    private ConsumeConcurrentlyStatus processMessages(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        for (MessageExt messageExt : msgs) {
            try {
                if (!realtimeSwitch && !realtimeEventSwitch) {
                    log.info("linkId={},topic={}--备用MQ向数据中心转发",messageExt.getKeys(),THIRD_MATCH_EVENT_INFO_API);
                    dataCenterProducer.send(messageExt,THIRD_MATCH_EVENT_INFO_API);
                    continue;
                }
                matchEventInfoProcessor.putMatchEventInfo(messageExt,true);
            } catch (Exception ex) {
                log.error("处理消息时发生异常, 消息内容: {}", new String(messageExt.getBody(), StandardCharsets.UTF_8), ex);
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    @PreDestroy
    public void shutdownConsumer() {
        if (consumer != null) {
            try {
                consumer.shutdown();
                log.info("SpareThirdMatchEventInfoConsumer,RocketMQ消费者已关闭");
            } catch (Exception e) {
                log.error("SpareThirdMatchEventInfoConsumer,关闭消费者时发生异常", e);
            }
        }
    }






}



