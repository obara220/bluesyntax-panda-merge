package com.panda.merge.rocketmq.consumer;

import com.panda.merge.dto.Request;
import com.panda.merge.dto.odds.DataSourceAutoSwitchConfig;
import com.panda.merge.odds.AutoSwitchConfigService;
import com.panda.merge.validator.ValidatorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Validator;

import static com.panda.merge.odds.MQConstant.ODDS_CONSUMER_GROUP;
import static com.panda.merge.odds.MQConstant.TOPIC_DATA_SOURCE_SWITCH_CONFIG;

/**
 * DataSourceAutoSwitchConfigConsumer
 *
 * @description:
 * @date: 5/4/2025
 **/

@Slf4j
@Component
@RocketMQMessageListener(topic = TOPIC_DATA_SOURCE_SWITCH_CONFIG,
        consumerGroup = ODDS_CONSUMER_GROUP + TOPIC_DATA_SOURCE_SWITCH_CONFIG)
public class DataSourceAutoSwitchConfigConsumer implements RocketMQListener<Request<DataSourceAutoSwitchConfig>> {

    @Autowired
    private AutoSwitchConfigService autoSwitchConfigService;

    @Autowired
    private Validator validator;

    @Override
    public void onMessage(Request<DataSourceAutoSwitchConfig> request) {
        log.info("DataSourceAutoSwitchConfigConsumer request:{}", request);
        ValidatorUtils.validate(validator, request);
        autoSwitchConfigService.update(request.getData(),request.getLinkId());
    }
}
