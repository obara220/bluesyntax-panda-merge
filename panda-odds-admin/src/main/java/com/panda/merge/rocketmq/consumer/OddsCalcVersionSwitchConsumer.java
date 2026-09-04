package com.panda.merge.rocketmq.consumer;


import com.panda.merge.dto.message.OddsCalcVersionSwitchMessage;
import com.panda.merge.odds.service.OddsCalcVersionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.panda.merge.constant.ConstantSystem.PAND_ODDS_GROUP;
import static com.panda.merge.odds.constants.CacheConstant.ODDS_CALC_VERSION_SWITCH_TOPIC;


/**
 * @name: OddsCalcVersionSwitchConsumer
 * @description: 赔率计算版本开关
 * @date: 1/12/2025
 **/


@Slf4j
@Component
@RocketMQMessageListener(topic = ODDS_CALC_VERSION_SWITCH_TOPIC,
        consumerGroup = PAND_ODDS_GROUP + ODDS_CALC_VERSION_SWITCH_TOPIC)
public class OddsCalcVersionSwitchConsumer implements RocketMQListener<OddsCalcVersionSwitchMessage> {

    @Autowired
    private OddsCalcVersionService oddsCalcVersionService;

    @Override
    public void onMessage(OddsCalcVersionSwitchMessage message) {
        log.info("OddsCalcVersionSwitchMessage:{}", message);
        Integer status = message.getStatus();
        if (Objects.isNull(status)) {
            return;
        }
        oddsCalcVersionService.setVersion(status);
    }
}
