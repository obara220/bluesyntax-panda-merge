package com.panda.merge.rocketmq.consumer;

import com.panda.merge.dto.Request;

import com.panda.merge.dto.StandardMatchMarketDTO;
import com.panda.merge.rocketmq.processor.ChampionMarketProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

/**
 * 时间变更、新增盘口
 */

@Slf4j
@Component
@RocketMQMessageListener(
        topic = "CHAMPION_MARKET_SETTING",
        consumerGroup = "odds-group-CHAMPION_MARKET_SETTING",
        consumeThreadMax = 256,
        consumeTimeout = 10000L
)
@DependsOn("oddsAdminApplication")
public class ChampionMarketConsumer implements RocketMQListener<Request<StandardMatchMarketDTO>> {

    @Autowired
    private ChampionMarketProcessor championMarketProcessor;

    @Override
    public void onMessage(Request<StandardMatchMarketDTO> marketRequest) {
        championMarketProcessor.processChampionMarketSetting(marketRequest);
    }
}
