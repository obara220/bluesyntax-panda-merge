package com.panda.merge.rocketmq.consumer;

import com.panda.merge.api.ITradeMarketConfigApi;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.TradeMarketConfigDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import static com.panda.merge.constant.ConstantSystem.THIRD_MATCH_MARKET_STATUS;

/**
 * 爬虫数据源， 赛事级关封盘  topic
 *
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = THIRD_MATCH_MARKET_STATUS,
        consumerGroup = "odds-group-"+THIRD_MATCH_MARKET_STATUS,
        consumeThreadMax = 256,
        consumeTimeout = 10000L
)
@DependsOn("oddsAdminApplication")
public class ThirdMatchTradeMarketStatusConsumer implements RocketMQListener<Request<TradeMarketConfigDTO>> {

    @Lazy
    @Autowired
    private ITradeMarketConfigApi tradeMarketConfigApi;

    @Override
    public void onMessage(Request<TradeMarketConfigDTO> request) {
        tradeMarketConfigApi.putTradeMarketConfig(request);
    }

}
