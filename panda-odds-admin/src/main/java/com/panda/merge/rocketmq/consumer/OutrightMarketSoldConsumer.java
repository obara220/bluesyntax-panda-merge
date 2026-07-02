package com.panda.merge.rocketmq.consumer;

import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.OutrightMarketSoldMessage;
import com.panda.merge.rocketmq.processor.OutrightMarketSoldProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import static com.panda.merge.constant.ConstantSystem.OUTRIGHT_MARKET_SOLD_MESSAGE;

/**
 * 冠军盘口开售处理
 * @author    aison
 * @since     2020年11月18日16:58:31
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = OUTRIGHT_MARKET_SOLD_MESSAGE,
        consumerGroup = "odds-group-"+OUTRIGHT_MARKET_SOLD_MESSAGE,
        consumeThreadMax = 256,
        consumeTimeout = 10000L
)
@DependsOn("oddsAdminApplication")
public class OutrightMarketSoldConsumer implements RocketMQListener<Request<OutrightMarketSoldMessage>> {

    @Autowired
    private OutrightMarketSoldProcessor outrightMarketSoldProcessor;

    @Override
    public void onMessage(Request<OutrightMarketSoldMessage> outrightMarketSoldMessageRequest) {
        outrightMarketSoldProcessor.processOutrightMarketSold(outrightMarketSoldMessageRequest);
    }
}
