package com.panda.merge.rocketmq.consumer;

import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.MatchMarketCategoryConfigurationMessage;
import com.panda.merge.rocketmq.processor.MatchCategoryConfigruationProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import static com.panda.merge.constant.ConstantSystem.CONSUME_REALTIME_GROUP;

@Slf4j
@Component
@RocketMQMessageListener(
        topic = "Refresh_Market_Category",
        consumerGroup = CONSUME_REALTIME_GROUP + "REFRESH_MARKET_CATEGORY",
        consumeThreadMax = 128,
        consumeTimeout = 10000L
)
@DependsOn("realtimeAdminApplication")
    public class RefreshMarketCategoryConsumer implements RocketMQListener<Request<MatchMarketCategoryConfigurationMessage>> {
        @Autowired
        MatchCategoryConfigruationProcessor processor;


        @Override
        public void onMessage(Request<MatchMarketCategoryConfigurationMessage> matchMarketCategoryConfigurationMessageRequest) {
            processor.refreshMarketCategory(matchMarketCategoryConfigurationMessageRequest);
        }

}
