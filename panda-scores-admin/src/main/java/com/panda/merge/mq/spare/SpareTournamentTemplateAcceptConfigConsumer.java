package com.panda.merge.mq.spare;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.mq.consumer.TournamentTemplateAcceptConfigConsumer;
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

/**
 * 备用玩法集tMax开关配置MQ消费
 * 风控下发
 *
 */
@Slf4j
@Component
public class SpareTournamentTemplateAcceptConfigConsumer {
    private static final String TOPIC = "Tournament_Template_Accept_Config_Score";
    private static final String CONSUMER_GROUP = "scores-group-Tournament_Template_Accept_Config_Score2";

    @Value("${slaveNamesrvAddr}")
    private String nameServers;

    @Value("${slaveProducerGroup}")
    private String groupName;

    /**
     * 消费者需要配置多少线程，范围0-1000
     */
    public static final String NACOS_MQ_CONSUMER_THREAD_NUMBER_KEY = "mq.tournament-template-accept-config.consumer.thread";

    @NacosValue(value = "${" + NACOS_MQ_CONSUMER_THREAD_NUMBER_KEY + ":256}", autoRefreshed = true)
    @Getter
    @Setter
    private Integer consumerThreadNumber;

    @NacosValue(value = "${datacenter.scores.switch:false}", autoRefreshed = true)
    private Boolean datacenterMergeSwitch;

    private DefaultMQPushConsumer consumer;

    @Autowired
    private TournamentTemplateAcceptConfigConsumer tournamentTemplateAcceptConfigConsumer;

    @PostConstruct
    public void initConsumer() {
        if (datacenterMergeSwitch) {
            return;
        }
        if (StringUtils.isBlank(nameServers)) {
            log.warn("SpareTournamentTemplateAcceptConfigConsumer: slaveNamesrvAddr配置为空，备用MQ消费者不启动");
            return;
        }
        try {
            log.info("SpareTournamentTemplateAcceptConfigConsumer:initConsumer");
            // 创建消费者并设置配置
            consumer = new DefaultMQPushConsumer(CONSUMER_GROUP);
            consumer.setInstanceName("SpareTournamentTemplateAcceptConfigConsumer");
            // Set name servers
            consumer.setNamesrvAddr(nameServers);
            // 设置最大并发线程数
            consumer.setConsumeThreadMax(256);
            // 设置最小并发线程数
            consumer.setConsumeThreadMin(256);
            // 从最后一个偏移量开始消费
            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
            // 一次处理消息数量
            consumer.setConsumeMessageBatchMaxSize(consumerThreadNumber);
            // 订阅备用-MQ主题
            log.info("SpareTournamentTemplateAcceptConfigConsumer 订阅主题,玩法集tMax开关配置={}", TOPIC);
            consumer.subscribe(TOPIC, "*");
            // 注册消息监听器
            consumer.registerMessageListener(this::processMessages);
            // 启动消费者
            consumer.start();
            log.info("SpareTournamentTemplateAcceptConfigConsumer started successfully");
        } catch (Exception e) {
            log.error("SpareTournamentTemplateAcceptConfigConsumer 启动失败，备用MQ消费者不启动: {}", e.getMessage(), e);
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
        try {
            String messageBody = new String(messageExt.getBody(), StandardCharsets.UTF_8);
            log.info("SpareTournamentTemplateAcceptConfigConsumer 收到消息: {}", messageBody);
            
            if (StringUtils.isBlank(messageBody)) {
                log.warn("SpareTournamentTemplateAcceptConfigConsumer 消息体为空，跳过处理");
                return;
            }
            
            // 直接调用主消费者的onMessage方法
            tournamentTemplateAcceptConfigConsumer.onMessage(messageBody);
            
        } catch (Exception e) {
            log.error("SpareTournamentTemplateAcceptConfigConsumer 处理消息异常: {}", e.getMessage(), e);
            throw new RuntimeException("处理消息失败", e);
        }
    }

    @PreDestroy
    public void destroy() {
        if (consumer != null) {
            consumer.shutdown();
            log.info("SpareTournamentTemplateAcceptConfigConsumer shutdown");
        }
    }
}

