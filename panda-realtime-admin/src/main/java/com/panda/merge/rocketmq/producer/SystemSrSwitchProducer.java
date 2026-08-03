package com.panda.merge.rocketmq.producer;

import com.panda.merge.dto.message.CashOutSwitchInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * SR系统级别提前结算开关
 */
@Slf4j
@Component
public class SystemSrSwitchProducer {

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    public void sendSystemSrSwitchMessage(String linkId, Integer num) {
        CashOutSwitchInfo cashOutSwitchInfo = new CashOutSwitchInfo();
        cashOutSwitchInfo.setSwitchKey(num);
        cashOutSwitchInfo.setLinkId(linkId);
        MessageBuilder<CashOutSwitchInfo> requestMessageBuilder = MessageBuilder.withPayload(cashOutSwitchInfo);
        rocketMqTemplate.send("SYSTEM_SR_SWITCH", requestMessageBuilder.build());
    }

}
