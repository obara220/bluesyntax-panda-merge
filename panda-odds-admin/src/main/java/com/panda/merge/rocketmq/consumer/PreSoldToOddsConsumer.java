package com.panda.merge.rocketmq.consumer;


import com.panda.merge.dto.Request;
import com.panda.merge.model.StandardSportMarketSell;
import com.panda.merge.rocketmq.processor.PreSoldMessageToOddsProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Validator;

import static com.panda.merge.constant.ConstantSystem.MATCH_ADVANCE_SALE;
import static com.panda.merge.constant.ConstantSystem.PAND_ODDS_GROUP;

/**
 * PreSoldMessageConsumer
 *
 * @description: 预售消息处理
 * @date: 1/24/2025
 **/
@Slf4j
@Component
@RocketMQMessageListener(topic = MATCH_ADVANCE_SALE, consumerGroup = PAND_ODDS_GROUP + MATCH_ADVANCE_SALE)
@Validated
public class PreSoldToOddsConsumer implements RocketMQListener<Request<StandardSportMarketSell>> {

    @Autowired
    private PreSoldMessageToOddsProcessor processor;

    @Override
    public void onMessage(Request<StandardSportMarketSell> request) {

        log.info("{} pre sold message {}", request.getLinkId(), request);
        processor.process(request);
    }
}
