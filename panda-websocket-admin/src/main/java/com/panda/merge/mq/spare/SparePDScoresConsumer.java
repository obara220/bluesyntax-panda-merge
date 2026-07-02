package com.panda.merge.mq.spare;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.handler.PDSubcribe;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author warren
 * @since 2025/02/11 00:31:17
 */
@Slf4j
@Component
public class SparePDScoresConsumer {
    private static final String TOPIC = "PD_FOOTBALL_SCORE";

    private static final String CONSUMER_GROUP = "scores-group-PDScoresConsumer";

    @Value("${slaveNamesrvAddr}")
    private String nameServers;

    @Value("${slaveProducerGroup}")
    private String groupName;

    private DefaultMQPushConsumer consumer;

    @PostConstruct
    public void initConsumer() throws MQClientException {
        log.info("SparePDScoresConsumer:initConsumer");
        // 创建消费者并设置配置
        consumer = new DefaultMQPushConsumer(CONSUMER_GROUP);
        consumer.setInstanceName("SparePDScoresConsumer");
        // Set name servers
        consumer.setNamesrvAddr(nameServers);
        // 设置最大并发线程数
        consumer.setConsumeThreadMax(64);
        // 设置最小并发线程数
        consumer.setConsumeThreadMin(64);
        // 从最后一个偏移量开始消费
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        // 一次处理一条消息
        consumer.setConsumeMessageBatchMaxSize(1);
        // 订阅备用-MQ主题
        log.info("SparePDScoresConsumer 订阅主题,单条赛事事件={}", TOPIC);
        consumer.subscribe(TOPIC, "*");
        // 广播模式
        consumer.setMessageModel(MessageModel.BROADCASTING);
        // 超时时间
        consumer.setConsumeTimeout(10000L);
        // 注册消息监听器
        consumer.registerMessageListener(this::processMessages);
        // 启动消费者
        consumer.start();
        log.info("SparePDScoresConsumer started successfully");
    }

    /**
     * 消费panda事件数据发送到SK-MQ
     */
    private ConsumeConcurrentlyStatus processMessages(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        for (MessageExt messageExt : msgs) {
            try {
                processMatchEvent(messageExt);
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
                log.info("SparePDScoresConsumer,RocketMQ消费者已关闭");
            } catch (Exception e) {
                log.error("SparePDScoresConsumer,关闭消费者时发生异常", e);
            }
        }
    }

    @Autowired
    private PDSubcribe pdSubcribe;

    /**
     * 单条事件信息处理
     */
    public void processMatchEvent(MessageExt ext) {
        String topic = null;
        String linkId = null;
        try {
            topic = ext.getTopic();
            linkId = ext.getProperties().get("KEYS");
            log.info("linkId=【" + linkId + "】【备用-MQ," + topic + "】开始处理事件数据");
            String message = new String(ext.getBody(), StandardCharsets.UTF_8);
            if (StringUtils.isBlank(message)) {
                log.info("linkId=【" + linkId + "】【备用-MQ," + topic + "】接收到事件数据为空！");
            } else {
                // 解析消息
                pdSubcribe.sendPdScore(JSONObject.parseObject(message).getString("data"));
            }
        } catch (Exception e) {
            log.error("linkId=【" + linkId + "】【备用-MQ," + topic + "】事件数据处理异常,Exception:", e);
        } finally {
            log.info("linkId=【" + linkId + "】【备用-MQ," + topic + "】事件数据处理结束");
        }
    }
}
