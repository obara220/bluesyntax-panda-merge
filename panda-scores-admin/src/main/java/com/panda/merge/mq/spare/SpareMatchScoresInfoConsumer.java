package com.panda.merge.mq.spare;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.dto.Request;
import com.panda.merge.model.MatchScoresInfo;
import com.panda.merge.mq.consumer.MatchScoresInfoConsumer;
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
import java.util.ArrayList;
import java.util.List;

/**
 * 备用比分信息更新MQ消费
 *

 */
@Slf4j
@Component
public class SpareMatchScoresInfoConsumer {
    private static final String TOPIC = "MATCH_SCORES_INFO_UPDATE";
    private static final String CONSUMER_GROUP = "scores-group-MATCH_SCORES_INFO_UPDATE";

    @Value("${slaveNamesrvAddr}")
    private String nameServers;

    @Value("${slaveProducerGroup}")
    private String groupName;

    /**
     * 消费者需要配置多少线程，范围0-1000
     */
    public static final String NACOS_MQ_CONSUMER_THREAD_NUMBER_KEY = "mq.uof-score.consumer.thread";

    @NacosValue(value = "${" + NACOS_MQ_CONSUMER_THREAD_NUMBER_KEY + ":20}", autoRefreshed = true)
    @Getter
    @Setter
    private Integer consumerThreadNumber;

    @NacosValue(value = "${datacenter.scores.switch:false}", autoRefreshed = true)
    private Boolean datacenterMergeSwitch;

    private DefaultMQPushConsumer consumer;

    @Autowired
    private MatchScoresInfoConsumer matchScoresInfoConsumer;

    @PostConstruct
    public void initConsumer() {
        if (datacenterMergeSwitch) {
            return;
        }
        if (StringUtils.isBlank(nameServers)) {
            log.warn("SpareMatchScoresInfoConsumer: slaveNamesrvAddr配置为空，备用MQ消费者不启动");
            return;
        }
        try {
            log.info("SpareMatchScoresInfoConsumer:initConsumer");
            // 创建消费者并设置配置
            consumer = new DefaultMQPushConsumer(CONSUMER_GROUP);
            consumer.setInstanceName("SpareMatchScoresInfoConsumer");
            // Set name servers
            consumer.setNamesrvAddr(nameServers);
            // 设置最大并发线程数
            consumer.setConsumeThreadMax(64);
            // 设置最小并发线程数
            consumer.setConsumeThreadMin(64);
            // 从最后一个偏移量开始消费
            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
            // 一次处理消息数量
            consumer.setConsumeMessageBatchMaxSize(consumerThreadNumber);
            // 订阅备用-MQ主题
            log.info("SpareMatchScoresInfoConsumer 订阅主题,比分信息更新={}", TOPIC);
            consumer.subscribe(TOPIC, "*");
            // 注册消息监听器
            consumer.registerMessageListener(this::processMessages);
            // 启动消费者
            consumer.start();
            log.info("SpareMatchScoresInfoConsumer started successfully");
        } catch (Exception e) {
            log.error("SpareMatchScoresInfoConsumer 启动失败，备用MQ消费者不启动: {}", e.getMessage(), e);
            // 不抛出异常，避免影响应用启动
        }
    }

    /**
     * 消费消息
     */
    private ConsumeConcurrentlyStatus processMessages(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        for (MessageExt messageExt : msgs) {
            try {
                processMessageList(messageExt);
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
                log.info("SpareMatchScoresInfoConsumer,RocketMQ消费者已关闭");
            } catch (Exception e) {
                log.error("SpareMatchScoresInfoConsumer,关闭消费者时发生异常", e);
            }
        }
    }

    /**
     * 处理批量消息
     */
    public void processMessageList(MessageExt ext) {
        String topic = null;
        String linkId = null;
        try {
            topic = ext.getTopic();
            linkId = ext.getProperties().get("KEYS");
            log.info("linkId=【{}】【备用-MQ,{}】开始处理比分信息更新数据", linkId, topic);
            String message = new String(ext.getBody(), StandardCharsets.UTF_8);
            if (StringUtils.isBlank(message)) {
                log.info("linkId=【{}】【备用-MQ,{}】接收到比分信息更新数据为空！", linkId, topic);
                return;
            }
            // 解析消息
            JSONObject jsonObject = JSONObject.parseObject(message);
            // 解析为批量消息列表
            List<MatchScoresInfo> dataList = JSONObject.parseObject(jsonObject.getString("data"), new TypeReference<List<MatchScoresInfo>>() {
            });

            if (dataList == null || dataList.isEmpty()) {
                log.info("linkId=【{}】【备用-MQ,{}】解析后的数据列表为空", linkId, topic);
                return;
            }

            // 转换为Request列表
            List<Request<MatchScoresInfo>> requestList = new ArrayList<>();
            for (MatchScoresInfo data : dataList) {
                Request<MatchScoresInfo> request = new Request<>();
                request.setSpareMq(true);
                request.setLinkId(linkId);
                request.setTag(data.getId() != null ? data.getId().toString() : linkId);
                request.setDataType(topic);
                request.setData(data);
                requestList.add(request);
            }

            // 调用主消费者的批量处理方法
            matchScoresInfoConsumer.processMessageList(requestList);
        } catch (Exception e) {
            log.error("linkId=【{}】【备用-MQ,{}】比分信息更新数据处理异常,Exception:", linkId, topic, e);
        } finally {
            log.info("linkId=【{}】【备用-MQ,{}】比分信息更新数据处理结束", linkId, topic);
        }
    }
}

