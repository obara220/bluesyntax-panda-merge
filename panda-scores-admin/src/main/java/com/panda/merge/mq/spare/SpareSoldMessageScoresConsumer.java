package com.panda.merge.mq.spare;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.SaleUpdateLiveBusinessEventMessage;
import com.panda.merge.mq.consumer.SoldMessageScoresConsumer;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.panda.merge.constant.ConstantSystem.LIVE_BUSINESS_EVENT_UPDATE_MESSAGE;

/**
 * 备用切换数据源后补发比分MQ消费（风控后台触发）
 *

 */
@Slf4j
@Component
public class SpareSoldMessageScoresConsumer {
    private static final String TOPIC = LIVE_BUSINESS_EVENT_UPDATE_MESSAGE;
    private static final String CONSUMER_GROUP = "scores-group-" + LIVE_BUSINESS_EVENT_UPDATE_MESSAGE+"2";

    @Value("${slaveNamesrvAddr}")
    private String nameServers;

    @Value("${slaveProducerGroup}")
    private String groupName;

    /**
     * 消费者需要配置多少线程，范围0-1000
     */
    public static final String NACOS_MQ_CONSUMER_THREAD_NUMBER_KEY = "mq.sold-message-scores.consumer.thread";

    @NacosValue(value = "${" + NACOS_MQ_CONSUMER_THREAD_NUMBER_KEY + ":2}", autoRefreshed = true)
    @Getter
    @Setter
    private Integer consumerThreadNumber;

    @NacosValue(value = "${datacenter.scores.switch:false}", autoRefreshed = true)
    private Boolean datacenterMergeSwitch;

    private DefaultMQPushConsumer consumer;

    @Autowired
    private SoldMessageScoresConsumer soldMessageScoresConsumer;

    @PostConstruct
    public void initConsumer() {
        if (datacenterMergeSwitch) {
            return;
        }
        if (StringUtils.isBlank(nameServers)) {
            log.warn("SpareSoldMessageScoresConsumer: slaveNamesrvAddr配置为空，备用MQ消费者不启动");
            return;
        }
        try {
            log.info("SpareSoldMessageScoresConsumer:initConsumer");
            // 创建消费者并设置配置
            consumer = new DefaultMQPushConsumer(CONSUMER_GROUP);
            consumer.setInstanceName("SpareSoldMessageScoresConsumer");
            // Set name servers
            consumer.setNamesrvAddr(nameServers);
            // 设置最大并发线程数
            consumer.setConsumeThreadMax(2);
            // 设置最小并发线程数
            consumer.setConsumeThreadMin(2);
            // 从最后一个偏移量开始消费
            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
            // 一次处理消息数量
            consumer.setConsumeMessageBatchMaxSize(consumerThreadNumber);
            // 订阅备用-MQ主题
            log.info("SpareSoldMessageScoresConsumer 订阅主题,切换数据源后补发比分={}", TOPIC);
            consumer.subscribe(TOPIC, "*");
            // 注册消息监听器
            consumer.registerMessageListener(this::processMessages);
            // 启动消费者
            consumer.start();
            log.info("SpareSoldMessageScoresConsumer started successfully");
        } catch (Exception e) {
            log.error("SpareSoldMessageScoresConsumer 启动失败，备用MQ消费者不启动: {}", e.getMessage(), e);
            // 不抛出异常，避免影响应用启动
        }
    }

    /**
     * 消费消息
     */
    private ConsumeConcurrentlyStatus processMessages(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        for (MessageExt messageExt : msgs) {
            try {
                processMessage(messageExt);
            } catch (Exception ex) {
                log.error("处理消息时发生异常, 消息内容: {}", new String(messageExt.getBody(), StandardCharsets.UTF_8), ex);
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    /**
     * 处理单条消息
     */
    private void processMessage(MessageExt messageExt) {
        String topic = null;
        String linkId = null;
        try {
            topic = messageExt.getTopic();
            linkId = messageExt.getProperties().get("KEYS");
            log.info("linkId=【{}】【备用-MQ,{}】开始处理切换数据源后补发比分数据", linkId, topic);
            String messageBody = new String(messageExt.getBody(), StandardCharsets.UTF_8);
            
            if (StringUtils.isBlank(messageBody)) {
                log.warn("linkId=【{}】【备用-MQ,{}】消息体为空，跳过处理", linkId, topic);
                return;
            }
            
            // 解析消息
            JSONObject jsonObject = JSONObject.parseObject(messageBody);
            SaleUpdateLiveBusinessEventMessage data = jsonObject.getObject("data", SaleUpdateLiveBusinessEventMessage.class);
            
            if (data == null) {
                log.warn("linkId=【{}】【备用-MQ,{}】解析后的数据为空", linkId, topic);
                return;
            }
            
            // 构建Request对象
            Request<SaleUpdateLiveBusinessEventMessage> request = new Request<>();
            request.setSpareMq(true);
            request.setLinkId(linkId != null ? linkId : jsonObject.getString("linkId"));
            request.setTag(jsonObject.getString("tag"));
            request.setDataType(topic);
            request.setData(data);
            
            // 调用主消费者的onMessage方法
            soldMessageScoresConsumer.onMessage(request);
            
        } catch (Exception e) {
            log.error("linkId=【{}】【备用-MQ,{}】切换数据源后补发比分数据处理异常: {}", linkId, topic, e.getMessage(), e);
            throw new RuntimeException("处理消息失败", e);
        }
    }

    @PreDestroy
    public void destroy() {
        if (consumer != null) {
            consumer.shutdown();
            log.info("SpareSoldMessageScoresConsumer shutdown");
        }
    }
}

