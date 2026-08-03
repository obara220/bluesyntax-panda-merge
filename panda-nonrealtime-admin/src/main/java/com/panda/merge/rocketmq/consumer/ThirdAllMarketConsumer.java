package com.panda.merge.rocketmq.consumer;

import cn.hutool.core.lang.TypeReference;
import com.panda.merge.common.RateLimiterHandler;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdMatchMarketDTO;
import com.panda.merge.rocketmq.AbstractMultipleMessageMQConsumer;
import com.panda.merge.rocketmq.ConsumerConfigDetail;
import com.panda.merge.rocketmq.EnableSecondRocketMQCluster;
import com.panda.merge.rocketmq.MqConsumerConfig;
import com.panda.merge.rocketmq.processor.ThirdAllBatchMarketProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

import static com.panda.merge.constant.ConstantSystem.CONSUME_NONREALTIME_GROUP;
import static com.panda.merge.constant.ConstantSystem.THIRD_TX_MATCH_MARKET_API;

@Slf4j
@Component
@DependsOn("nonrealtimeAdminApplication")
@EnableSecondRocketMQCluster
public class ThirdAllMarketConsumer extends AbstractMultipleMessageMQConsumer<Request<ThirdMatchMarketDTO>> {

    private static final String CONSUMER_GROUP = CONSUME_NONREALTIME_GROUP + THIRD_TX_MATCH_MARKET_API;


    @Value("${odds.admin.pull.batch.size:500}")
    private Integer PULL_BATCH_SIZE;

    @Value("${odds.admin.consume.message.batch.max.size:100}")
    private Integer CONSUME_MESSAGE_BATCH_MAX_SIZE;

    @Value("${odds.admin.partition.nums.per.time:10}")
    private Integer PARTITION_NUMS_PER_TIME;

    @Value("${odds.admin.pull.interval:-1}")
    private Long PULL_INTERVAL;

    @Resource
    private ThirdAllBatchMarketProcessor thirdAllBatchMarketProcessor;


    @Override
    public MqConsumerConfig buildConfig() {
        ConsumerConfigDetail consumerConfigDetail = ConsumerConfigDetail.builder().messageSize(CONSUME_MESSAGE_BATCH_MAX_SIZE).pullBatchSize(PULL_BATCH_SIZE).pullInterval(PULL_INTERVAL).build();
        return new MqConsumerConfig(THIRD_TX_MATCH_MARKET_API, CONSUMER_GROUP, new TypeReference<Request<ThirdMatchMarketDTO>>() {
        }, consumerConfigDetail);
    }

    @Override
    public void processMessageList(List<Request<ThirdMatchMarketDTO>> requests) {
        ListUtils.partition(requests, PARTITION_NUMS_PER_TIME).forEach(t -> thirdAllBatchMarketProcessor.execute(t));
    }
}
