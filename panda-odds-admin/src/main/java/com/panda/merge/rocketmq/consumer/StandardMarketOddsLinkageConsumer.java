package com.panda.merge.rocketmq.consumer;

import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.MarketOddsLinkageConfigMessage;
import com.panda.merge.rocketmq.processor.StandardMarketOddsLinkageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.panda.merge.constant.ConstantSystem.RCS_MARKET_ODDS_LINKAGE_CONFIG;

/**
 * 赔率联动配置
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = RCS_MARKET_ODDS_LINKAGE_CONFIG,
        consumerGroup = "odds-group-" + RCS_MARKET_ODDS_LINKAGE_CONFIG,
        consumeThreadMax = 20,
        consumeTimeout = 10000L)
@DependsOn("oddsAdminApplication")
public class StandardMarketOddsLinkageConsumer implements RocketMQListener<Request<List<MarketOddsLinkageConfigMessage>>> {

    @Autowired
    private StandardMarketOddsLinkageProcessor standardMarketOddsLinkageProcessor;

    @Override
    public void onMessage(Request<List<MarketOddsLinkageConfigMessage>> request) {
        standardMarketOddsLinkageProcessor.process(request);
    }
}
