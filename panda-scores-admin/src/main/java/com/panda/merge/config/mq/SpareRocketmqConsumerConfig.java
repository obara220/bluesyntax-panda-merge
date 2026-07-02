package com.panda.merge.config.mq;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.constant.CommonConstant;
import com.panda.merge.mq.spare.SpareDBTableConsumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author warren
 * @since 2025/06/14 02:53:20
 */
@Slf4j
@Configuration
public class SpareRocketmqConsumerConfig {
    @NacosValue(value = "${mq.live-data-score.consumer.thread:128}", autoRefreshed = true)
    private Integer threadNum;
    @NacosValue(value = "${slaveNamesrvAddr:}", autoRefreshed = true)
    private String slaveNamesrvAddr;
    private static final String CONSUMER_GROUP = "score-center-group-SLAVE-DB-STORAGE";

    private DefaultMQPushConsumer RocketMQConsumer;
    @Autowired
    private SpareDBTableConsumer spareDBTableConsumer;
    @NacosValue(value = "${datacenter.scores.switch:false}", autoRefreshed = true)
    private Boolean datacenterMergeSwitch;
    @PostConstruct
    public void initConsumer() throws MQClientException {
        log.info("rocketMQTemplate:initConsumer");
        if(StringUtils.isEmpty(slaveNamesrvAddr)){
            return;
        }
        // 创建消费者并设置配置
        RocketMQConsumer = new DefaultMQPushConsumer(CONSUMER_GROUP);
        RocketMQConsumer.setInstanceName("SpareRocketmqConsumerConfig");
        // Set name servers
        RocketMQConsumer.setNamesrvAddr(slaveNamesrvAddr);
        // 设置最大并发线程数
        RocketMQConsumer.setConsumeThreadMax(threadNum);
        // 设置最小并发线程数
        RocketMQConsumer.setConsumeThreadMin(threadNum);
        // 从最后一个偏移量开始消费
        RocketMQConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        // 一次处理消息条数
        RocketMQConsumer.setConsumeMessageBatchMaxSize(32);
        // 订阅备用-MQ主题
        log.info("SpareRocketmqConsumerConfig 订阅主题,多条赛事事件={}", CommonConstant.SCORE_CENTER_SLAVE_DB_TOPIC);
        RocketMQConsumer.subscribe(CommonConstant.SCORE_CENTER_SLAVE_DB_TOPIC, "*");
        // 注册消息监听器
        RocketMQConsumer.registerMessageListener(spareDBTableConsumer::processMessages);
        // 启动消费者
        RocketMQConsumer.start();
        log.info("SpareRocketmqConsumerConfig started successfully");
    }

    public DefaultMQPushConsumer getConsumer() {
        return RocketMQConsumer;
    }
}
