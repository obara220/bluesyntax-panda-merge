package com.panda.merge.rocketmq.consumer;

import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.OutrightMarketOrderMessage;
import com.panda.merge.rocketmq.processor.OutrightMarketOrderProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import static com.panda.merge.constant.ConstantSystem.OUTRIGHT_MARKET_ORDER_MESSAGE;

/**
 * 冠军盘口排序处理
 *
 * @author raulvii
 * @since 2021年01月14日1
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = OUTRIGHT_MARKET_ORDER_MESSAGE,
        consumerGroup = "odds-group-" + OUTRIGHT_MARKET_ORDER_MESSAGE,
        consumeThreadMax = 256,
        consumeTimeout = 10000L
)
@DependsOn("oddsAdminApplication")
public class OutrightMarketOrderConsumer implements RocketMQListener<Request<OutrightMarketOrderMessage>> {

    @Autowired
    private OutrightMarketOrderProcessor outrightMarketOrderProcessor;

    @Override
    public void onMessage(Request<OutrightMarketOrderMessage> request) {
        outrightMarketOrderProcessor.processOutrightMarketOrder(request);
    }
}
