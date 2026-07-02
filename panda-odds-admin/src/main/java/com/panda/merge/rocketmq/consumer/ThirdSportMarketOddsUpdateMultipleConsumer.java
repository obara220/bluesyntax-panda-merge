package com.panda.merge.rocketmq.consumer;

import cn.hutool.core.lang.TypeReference;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.MarketDBMessage;
import com.panda.merge.proxy.ThirdSportMarketAndOddsBatchUpdateProxy;
import com.panda.merge.rocketmq.AbstractMultipleMessageMQConsumer;
import com.panda.merge.rocketmq.ConsumerConfigDetail;
import com.panda.merge.rocketmq.MqConsumerConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.panda.merge.constant.ConstantSystem.THIRD_MATCH_MARKET_API;
import static com.panda.merge.constant.ConstantSystem.THIRD_SPORT_MARKET_ODDS_UPDATE;

@Slf4j
@Component
@DependsOn("oddsAdminApplication")
public class ThirdSportMarketOddsUpdateMultipleConsumer extends
        AbstractMultipleMessageMQConsumer<Request<MarketDBMessage>> {

    @Autowired
    ThirdSportMarketAndOddsBatchUpdateProxy thirdSportMarketAndOddsBatchUpdateProxy;

    private static final String CONSUMER_GROUP = "odds-group-" + THIRD_SPORT_MARKET_ODDS_UPDATE;

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

    @Override
    public MqConsumerConfig buildConfig() {
        ConsumerConfigDetail consumerConfigDetail = ConsumerConfigDetail.builder()
                                                                        .threadNumber(CONSUME_THREAD)
                                                                        .messageSize(CONSUME_MESSAGE_BATCH_MAX_SIZE)
                                                                        .pullBatchSize(PULL_BATCH_SIZE)
                                                                        .pullInterval(PULL_INTERVAL)
                                                                        .build();
        return new MqConsumerConfig(THIRD_SPORT_MARKET_ODDS_UPDATE, CONSUMER_GROUP, new TypeReference<Request<MarketDBMessage>>() {
        }, consumerConfigDetail);
    }

    @Override
    public void processMessageList(List<Request<MarketDBMessage>> requests) {
        ListUtils.partition(requests, PARTITION_NUMS_PER_TIME).forEach(t -> thirdSportMarketAndOddsBatchUpdateProxy.batchOddsUpdate(t));
    }
}
