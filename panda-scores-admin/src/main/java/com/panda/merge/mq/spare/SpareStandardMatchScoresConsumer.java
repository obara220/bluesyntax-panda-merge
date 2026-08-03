package com.panda.merge.mq.spare;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.dto.MatchStatisticsInfoDTO;
import com.panda.merge.dto.Request;
import com.panda.merge.model.MatchEventInfo;
import com.panda.merge.mq.consumer.LiveDataScoresNewConsumer;
import com.panda.merge.mq.consumer.StandardMatchScoresConsumer;
import com.panda.merge.mq.message.CommonStandardScoresDto;
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
 * 消费标准比分MQ，触发角球、罚牌Tmax比分
 *
 * @author fymen
 * @since 2025/03/11 14:11:16
 */
@Slf4j
@Component
public class SpareStandardMatchScoresConsumer {
    private static final String TOPIC ="STANDARD_MATCH_SCORES";
    private static final String CONSUMER_GROUP="scores-group-STANDARD_MATCH_SCORES2";

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

    private DefaultMQPushConsumer consumer;
    @NacosValue(value = "${datacenter.scores.switch:false}", autoRefreshed = true)
    private Boolean datacenterMergeSwitch;

    @PostConstruct
    public void initConsumer() {
        if (datacenterMergeSwitch) {
            return;
        }
        if (StringUtils.isBlank(nameServers)) {
            log.warn("SpareStandardMatchScoresConsumer: slaveNamesrvAddr配置为空，备用MQ消费者不启动");
            return;
        }
        try {
            log.info("SpareStandardMatchScoresConsumer:initConsumer");
            // 创建消费者并设置配置
            consumer = new DefaultMQPushConsumer(CONSUMER_GROUP);
            consumer.setInstanceName("SpareStandardMatchScoresConsumer");
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
            log.info("SpareStandardMatchScoresConsumer 订阅主题,多条赛事比分={}", TOPIC);
            consumer.subscribe(TOPIC, "*");
            // 注册消息监听器
            consumer.registerMessageListener(this::processMessages);
            // 启动消费者
            consumer.start();
            log.info("SpareStandardMatchScoresConsumer started successfully");
        } catch (Exception e) {
            log.error("SpareStandardMatchScoresConsumer 启动失败，备用MQ消费者不启动: {}", e.getMessage(), e);
            // 不抛出异常，避免影响应用启动
        }
    }

    /**
     * 消费panda比分数据发送到SK-MQ
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
                log.info("SpareStandardMatchScoresConsumer,RocketMQ消费者已关闭");
            } catch (Exception e) {
                log.error("SpareStandardMatchScoresConsumer,关闭消费者时发生异常", e);
            }
        }
    }

    @Autowired
    private StandardMatchScoresConsumer standardMatchScoresConsumer;


    /**
     * 单条比分信息处理
     */
    public void processMatchEvent(MessageExt ext) {
        String topic = null;
        String linkId = null;
        try {
            topic = ext.getTopic();
            linkId = ext.getProperties().get("KEYS");
            log.info("linkId=【" + linkId + "】【备用-MQ," + topic + "】开始处理比分数据");
            String message = new String(ext.getBody(), StandardCharsets.UTF_8);
            if (StringUtils.isBlank(message)) {
                log.info("linkId=【" + linkId + "】【备用-MQ," + topic + "】接收到比分数据为空！");
            } else {
                // 解析消息
                JSONObject jSONObject = JSONObject.parseObject(message);
                CommonStandardScoresDto commonStandardScoresDto = jSONObject.getObject("data", CommonStandardScoresDto.class);
                String matchId = commonStandardScoresDto.getStandardMatchId()+"";

                // 调用数据处理逻辑
                Request<CommonStandardScoresDto> request = new Request<>();
                request.setSpareMq(true);
                request.setLinkId(linkId);
                request.setTag(matchId);
                request.setDataType(topic);
                request.setData(commonStandardScoresDto);
                standardMatchScoresConsumer.processMessage(request);
            }
        } catch (Exception e) {
            log.error("linkId=【" + linkId + "】【备用-MQ," + topic + "】比分数据处理异常,Exception:", e);
        } finally {
            log.info("linkId=【" + linkId + "】【备用-MQ," + topic + "】比分数据处理结束");
        }
    }
}
