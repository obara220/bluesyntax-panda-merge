package com.panda.merge.rocketmq.spare;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.panda.merge.dto.MatchEventInfoDTO;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdMatchPreResultDTO;
import com.panda.merge.dto.message.StandardMatchSwitchStatusMessage;
import com.panda.merge.rocketmq.processor.MatchEventInfoProcessor;
import com.panda.merge.rocketmq.processor.ThirdMarketPreResultNewProcessor;
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
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * 备用多条事件MQ消费
 *
 * @author tell
 * @since 2025年02月07日17:45:05
 */
@Slf4j
@Component
public class SpareThirdMarketPreResultConsumer {

    @Value("${slaveNamesrvAddr}")
    private String nameServers;

    @Value("${rocketmq.producer.group}")
    private String groupName;

    private DefaultMQPushConsumer consumer;

    @PostConstruct
    public void initConsumer() throws MQClientException {
        log.info("SpareThirdMatchEventListInfoConsumer:initConsumer");
        // 创建消费者并设置配置
        consumer = new DefaultMQPushConsumer(CONSUME_REALTIME_GROUP + THIRD_MARKET_PRE_RESULT_NEW_API);
        consumer.setInstanceName("SpareThirdMatchEventListInfoConsumer");
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
        log.info("SpareThirdMatchEventListInfoConsumer 订阅主题,多条三方赛事事件={}", THIRD_MARKET_PRE_RESULT_NEW_API);
        consumer.subscribe(THIRD_MARKET_PRE_RESULT_NEW_API, "*");
        // 注册消息监听器
        consumer.registerMessageListener(this::processMessages);
        // 启动消费者
        consumer.start();
        log.info("SpareThirdMatchEventListInfoConsumer started successfully");
    }

    /**
     * 消费panda事件数据发送到SK-MQ
     */
    private ConsumeConcurrentlyStatus processMessages(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        for (MessageExt messageExt : msgs) {
            try {
                processMarketPreEvent(messageExt);
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
                log.info("SpareThirdMatchEventListInfoConsumer,RocketMQ消费者已关闭");
            } catch (Exception e) {
                log.error("SpareThirdMatchEventListInfoConsumer,关闭消费者时发生异常", e);
            }
        }
    }


    @Autowired
    private ThirdMarketPreResultNewProcessor thirdMarketPreResultNewProcessor;

    public void processMarketPreEvent(MessageExt ext) {
        String topic = null;
        String linkId = null;
        try {
            topic = ext.getTopic();
            linkId = ext.getProperties().get("KEYS");
            log.info("linkId=【" + linkId + "】【备用-MQ," + topic + "】开始处理融合滚球标识");
            Request<ThirdMatchPreResultDTO> message = JSON.parseObject(new String(ext.getBody()), new TypeReference<Request<ThirdMatchPreResultDTO>>() {
            });
            // 解析消息
            log.info("SpareThirdMarketPreResultConsumer##{}", JSON.toJSONString(message));
            message.setLinkId(message.getLinkId() + "_mq");
            thirdMarketPreResultNewProcessor.thirdMarketPreResultApi(message);
        } catch (Exception e) {
            log.error("linkId=【" + linkId + "】【备用-MQ," + topic + "】SpareThirdMarketPreResultConsumer数据处理异常,Exception:", e);
        } finally {
            log.error("linkId=【" + linkId + "】【备用-MQ," + topic + "】SpareThirdMarketPreResultConsumer数据处理结束");
        }
    }


}



