package com.panda.merge.mq.spare;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.dto.Request;
import com.panda.merge.model.MatchEventInfo;
import com.panda.merge.mq.consumer.LiveDataScoresNewConsumer;
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
 * 备用多条事件MQ消费
 *
 * @author warren
 * @since 2025/02/10 14:11:16
 */
@Slf4j
@Component
public class SpareLiveDataScoresNewConsumer {
    private static final String TOPIC = "MATCH_EVENT_INFO_TO_RISK";

    private static final String CONSUMER_GROUP = "scores-group-MATCH_EVENT_INFO_TO_RISK";

    @Value("${slaveNamesrvAddr}")
    private String nameServers;

    @Value("${slaveProducerGroup}")
    private String groupName;

    /**
     * 消费者需要配置多少线程，范围0-1000
     */
    public static final String NACOS_MQ_CONSUMER_THREAD_NUMBER_KEY ="mq.live-data-score.consumer.thread";

    @NacosValue(value = "${"+ NACOS_MQ_CONSUMER_THREAD_NUMBER_KEY +":20}",autoRefreshed = true)
    @Getter
    @Setter
    private Integer consumerThreadNumber;
    @NacosValue(value = "${datacenter.scores.switch:false}", autoRefreshed = true)
    private Boolean datacenterMergeSwitch;
    private DefaultMQPushConsumer consumer;

    @PostConstruct
    public void initConsumer() {
        if (datacenterMergeSwitch) {
            return;
        }
        if (StringUtils.isBlank(nameServers)) {
            log.warn("SpareLiveDataScoresNewConsumer: slaveNamesrvAddr配置为空，备用MQ消费者不启动");
            return;
        }
        try {
            log.info("SpareLiveDataScoresNewConsumer:initConsumer");
            // 创建消费者并设置配置
            consumer = new DefaultMQPushConsumer(CONSUMER_GROUP);
            consumer.setInstanceName("SpareLiveDataScoresNewConsumer");
            // Set name servers
            consumer.setNamesrvAddr(nameServers);
            // 设置最大并发线程数
            consumer.setConsumeThreadMax(64);
            // 设置最小并发线程数
            consumer.setConsumeThreadMin(64);
            // 从最后一个偏移量开始消费
            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
            // 一次处理一条消息
            consumer.setConsumeMessageBatchMaxSize(consumerThreadNumber);
            // 订阅备用-MQ主题
            log.info("SpareLiveDataScoresNewConsumer 订阅主题,多条赛事事件={}", TOPIC);
            consumer.subscribe(TOPIC, "*");
            // 注册消息监听器
            consumer.registerMessageListener(this::processMessages);
            // 启动消费者
            consumer.start();
            log.info("SpareLiveDataScoresNewConsumer started successfully");
        } catch (Exception e) {
            log.error("SpareLiveDataScoresNewConsumer 启动失败，备用MQ消费者不启动: {}", e.getMessage(), e);
            // 不抛出异常，避免影响应用启动
        }
    }

    /**
     * 消费panda事件数据发送到SK-MQ
     */
    private ConsumeConcurrentlyStatus processMessages(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        for (MessageExt messageExt : msgs) {
            try {
                processMatchEventList(messageExt);
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
                log.info("SpareLiveDataScoresNewConsumer,RocketMQ消费者已关闭");
            } catch (Exception e) {
                log.error("SpareLiveDataScoresNewConsumer,关闭消费者时发生异常", e);
            }
        }
    }

    @Autowired
    private LiveDataScoresNewConsumer liveDataScoresNewConsumer;

    /**
     * 多条事件信息处理
     */
    public void processMatchEventList(MessageExt ext) {
        String topic = null;
        String linkId = null;
        try {
            topic = ext.getTopic();
            linkId = ext.getProperties().get("KEYS");
            log.info("linkId=【" + linkId + "】【备用-MQ," + topic + "】开始处理事件列表数据", linkId);
            String message = new String(ext.getBody(), StandardCharsets.UTF_8);
            if (StringUtils.isBlank(message)) {
                log.info("linkId=【" + linkId + "】【备用-MQ," + topic + "】接收到事件列表数据为空！");
            } else {
                // 解析消息
                JSONObject jSONObject = JSONObject.parseObject(message);
                List<MatchEventInfo> matchEventInfoList = JSONObject.parseObject(jSONObject.getString("data"), new TypeReference<List<MatchEventInfo>>() {
                });
                String thirdMatchSourceId = matchEventInfoList.get(0).getThirdMatchSourceId();

                // 调用数据处理逻辑
                Request<List<MatchEventInfo>> request = new Request<>();
                request.setSpareMq(true);
                request.setLinkId(linkId);
                request.setTag(thirdMatchSourceId);
                request.setDataType(topic);
                request.setData(matchEventInfoList);
                liveDataScoresNewConsumer.processMessage(request);
            }
        } catch (Exception e) {
            log.error("linkId=【" + linkId + "】【备用-MQ," + topic + "】事件列表数据处理异常,Exception:", e);
        } finally {
            log.info("linkId=【" + linkId + "】【备用-MQ," + topic + "】事件列表数据处理结束");
        }
    }
}
