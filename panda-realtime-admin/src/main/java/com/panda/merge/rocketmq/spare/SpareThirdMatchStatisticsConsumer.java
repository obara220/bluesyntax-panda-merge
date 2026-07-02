package com.panda.merge.rocketmq.spare;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.dto.MatchStatisticsInfoDTO;
import com.panda.merge.dto.Request;
import com.panda.merge.rocketmq.processor.MatchStatisticsInfoProcessor;
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

import static com.panda.merge.constant.ConstantSystem.CONSUME_REALTIME_GROUP;
import static com.panda.merge.constant.ConstantSystem.MATCH_STATISTICS_INFO_API;

/**
 * 备用三方赛事统计MQ消费
 *
 * @author tell
 * @since 2025年02月07日17:45:05
 */
@Slf4j
@Component
public class SpareThirdMatchStatisticsConsumer {

    @Value("${slaveNamesrvAddr}")
    private String nameServers;

    @Value("${rocketmq.producer.group}")
    private String groupName;

    private DefaultMQPushConsumer consumer;

    @PostConstruct
    public void initConsumer() throws MQClientException {
        log.info("SpareThirdMatchStatisticsConsumer:initConsumer");
        // 创建消费者并设置配置
        consumer = new DefaultMQPushConsumer(CONSUME_REALTIME_GROUP + MATCH_STATISTICS_INFO_API);
        consumer.setInstanceName("SpareThirdMatchStatisticsConsumer");
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
        log.info("SpareThirdMatchEventListInfoConsumer 订阅主题,实时赛事统计={}", MATCH_STATISTICS_INFO_API);
        consumer.subscribe(MATCH_STATISTICS_INFO_API, "*");
        // 注册消息监听器
        consumer.registerMessageListener(this::processMessages);
        // 启动消费者
        consumer.start();
        log.info("SpareThirdMatchStatisticsConsumer started successfully");
    }

    /**
     * 消费panda事件数据发送到SK-MQ
     */
    private ConsumeConcurrentlyStatus processMessages(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        for (MessageExt messageExt : msgs) {
            try {
                processMatchStatisticsInfo(messageExt);
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
                log.info("SpareThirdMatchStatisticsConsumer,RocketMQ消费者已关闭");
            } catch (Exception e) {
                log.error("SpareThirdMatchStatisticsConsumer,关闭消费者时发生异常", e);
            }
        }
    }

    @Autowired
    private MatchStatisticsInfoProcessor matchStatisticsInfoProcessor;

    /**
     * 三方统计信息处理
     */
    public void processMatchStatisticsInfo(MessageExt ext) {
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
                MatchStatisticsInfoDTO matchStatisticsInfoDTO = jSONObject.getObject("data", MatchStatisticsInfoDTO.class);
                String thirdMatchSourceId = matchStatisticsInfoDTO.getThirdMatchSourceId();

                // 调用数据处理逻辑
                Request<MatchStatisticsInfoDTO> request = new Request<>();
                request.setSpareMq(true);
                request.setLinkId(linkId);
                request.setTag(thirdMatchSourceId);
                request.setDataType(topic);
                request.setData(matchStatisticsInfoDTO);
                matchStatisticsInfoProcessor.putMatchStatisticsInfo(request);
            }
        } catch (Exception e) {
            log.error("linkId=【" + linkId + "】【备用-MQ," + topic + "】事件列表数据处理异常,Exception:", e);
        } finally {
            log.info("linkId=【" + linkId + "】【备用-MQ," + topic + "】事件列表数据处理结束");
        }
    }


}



