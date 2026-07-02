package com.panda.merge.config;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.constant.CommonConstant;
import com.panda.merge.mq.consumer.SpareDBTableConsumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Slf4j
@Configuration
public class SpareRocketmqConsumerConfig {

    @NacosValue(value = "${settle.event.consume.thread.num:128}", autoRefreshed = true)
    private Integer threadNum;
    @NacosValue(value = "${slaveNamesrvAddr:}", autoRefreshed = true)
    private String slaveNamesrvAddr;
    private static final String CONSUMER_GROUP = "settle-group-SLAVE-DB-STORAGE";

    private DefaultMQPushConsumer RocketMQConsumer2;
    @Autowired
    private SpareDBTableConsumer spareDBTableConsumer;

    @PostConstruct
    public void initConsumer() throws MQClientException {
        log.info("rocketMQTemplate2:initConsumer");
        if(StringUtils.isEmpty(slaveNamesrvAddr)){
            return;
        }
        // 创建消费者并设置配置
        RocketMQConsumer2 = new DefaultMQPushConsumer(CONSUMER_GROUP);
        RocketMQConsumer2.setInstanceName("SpareStandardMatchScoreConsumer");
        // Set name servers
        RocketMQConsumer2.setNamesrvAddr(slaveNamesrvAddr);
        // 设置最大并发线程数
        RocketMQConsumer2.setConsumeThreadMax(threadNum);
        // 设置最小并发线程数
        RocketMQConsumer2.setConsumeThreadMin(threadNum);
        // 从最后一个偏移量开始消费
        RocketMQConsumer2.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        // 一次处理一条消息
        RocketMQConsumer2.setConsumeMessageBatchMaxSize(32);
        // 订阅备用-MQ主题
        log.info("SpareStandardMatchScoreConsumer 订阅主题,多条赛事事件={}", CommonConstant.SETTLE_SLAVE_DB_TOPIC);
        RocketMQConsumer2.subscribe(CommonConstant.SETTLE_SLAVE_DB_TOPIC, "*");
        // 注册消息监听器
        RocketMQConsumer2.registerMessageListener(spareDBTableConsumer::processMessages);
        // 启动消费者
        RocketMQConsumer2.start();
        log.info("SpareStandardMatchScoreConsumer started successfully");
    }

    public DefaultMQPushConsumer getConsumer() {
        return RocketMQConsumer2;
    }

}
