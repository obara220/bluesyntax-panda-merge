package com.panda.merge.rocketmq.consumer;

import cn.hutool.core.lang.TypeReference;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.MarketDBMessage;
import com.panda.merge.proxy.ThirdSportMarketAndOddsBatchUpdateProxy;
import com.panda.merge.rocketmq.AbstractMultipleMessageMQConsumer;
import com.panda.merge.rocketmq.ConsumerConfigDetail;
import com.panda.merge.rocketmq.MqConsumerConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.panda.merge.constant.ConstantSystem.DATACENTER;
import static com.panda.merge.constant.ConstantSystem.THIRD_SPORT_MARKET_UPDATE;

@Slf4j
@Component
@DependsOn("oddsAdminApplication")
public class ThirdSportMarketUpdateMultipleConsumer extends
        AbstractMultipleMessageMQConsumer<Request<MarketDBMessage>> {


    @Autowired
    ThirdSportMarketAndOddsBatchUpdateProxy thirdSportMarketAndOddsBatchUpdateProxy;


    private static final String CONSUMER_GROUP = "odds-group-" + THIRD_SPORT_MARKET_UPDATE;

    @Value("${odds.admin.pull.batch.size:512}")
    private Integer PULL_BATCH_SIZE;

    @Value("${odds.admin.consume.message.batch.max.size:32}")
    private Integer CONSUME_MESSAGE_BATCH_MAX_SIZE;

    @Value("${odds.admin.partition.nums.per.time:16}")
    private Integer PARTITION_NUMS_PER_TIME;

    @Value("${odds.admin.pull.interval:-1}")
    private Long PULL_INTERVAL;

    @Value("${odds.admin.consume.thread:256}")
    private Integer CONSUME_THREAD;

    /**
     * 数据中心赔率状态开关 1开 0关
     */
    @NacosValue(value = "${datacenter.odds.status:1}", autoRefreshed = true)
    private Integer datacenterOddsStatus;

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    @Override
    public MqConsumerConfig buildConfig() {
        ConsumerConfigDetail consumerConfigDetail = ConsumerConfigDetail.builder()
                .threadNumber(CONSUME_THREAD)
                .messageSize(CONSUME_MESSAGE_BATCH_MAX_SIZE)
                .pullBatchSize(PULL_BATCH_SIZE)
                .pullInterval(PULL_INTERVAL)
                .build();
        return new MqConsumerConfig(THIRD_SPORT_MARKET_UPDATE, CONSUMER_GROUP, new TypeReference<Request<MarketDBMessage>>() {
        }, consumerConfigDetail);
    }

    @Override
    public void processMessageList(List<Request<MarketDBMessage>> requests) {
        //数据中心赔率状态开关 1开 0关
        if (datacenterOddsStatus == 1) {
            requests.forEach(req -> {
                String key = req.getLinkId();
                String tag = req.getTag();
                // 转发消息到数据中心
                log.info("收到 ::{}:: Topic的消息：{}", THIRD_SPORT_MARKET_UPDATE, req);
                // 【关键优化 1】构造包含 Tag 的目标地址
                // RocketMQ 发送时，如果需要带 Tag，必须写成 "Topic:Tag" 格式
                String toTopic = THIRD_SPORT_MARKET_UPDATE + DATACENTER;
                String destination = !StringUtils.isEmpty(tag) ? toTopic + ":" + tag : toTopic;
                // 【关键优化 2】构建 Message 对象，注入 Key
                // PROPERTY_TAGS 在发送端主要是作为元数据，真正路由靠上面的 destination
                MessageBuilder<Request<MarketDBMessage>> builder = MessageBuilder.withPayload(req);
                if (!StringUtils.isEmpty(key)) {
                    builder.setHeader(MessageConst.PROPERTY_KEYS, key);
                }
                Message<Request<MarketDBMessage>> message = builder.build();
                // 执行发送
                rocketMqTemplate.send(destination, message);
                log.info("LinkID: {}消息已转发到数据中心目标topic: {}, message:{}", key, toTopic, message);
            });
            return;
        }

        ListUtils.partition(requests, PARTITION_NUMS_PER_TIME).forEach(t -> thirdSportMarketAndOddsBatchUpdateProxy.batchMarketUpdate(t));
    }
}
