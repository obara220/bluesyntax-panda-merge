package com.panda.merge.rocketmq.consumer;


import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.dto.message.OddsCalcVersionSwitchMessage;
import com.panda.merge.odds.service.OddsCalcVersionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.panda.merge.constant.ConstantSystem.DATACENTER;
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

    /**
     * 数据中心赔率状态开关 1开 0关
     */
    @NacosValue(value = "${datacenter.odds.status:1}", autoRefreshed = true)
    private Integer datacenterOddsStatus;

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    @Override
    public void onMessage(OddsCalcVersionSwitchMessage message) {
        //数据中心赔率状态开关 1开 0关
        if (datacenterOddsStatus == 1) {
            Long linkId = UUIdUtils.getId();
            // 转发消息到数据中心
            log.info("收到 ::{}:: Topic的消息：{}", ODDS_CALC_VERSION_SWITCH_TOPIC, message);
            String toTopic = ODDS_CALC_VERSION_SWITCH_TOPIC + DATACENTER;
            // 发送到 数据中心Topic
            MessageBuilder<OddsCalcVersionSwitchMessage> builder = MessageBuilder.withPayload(message)
                    .setHeader(MessageConst.PROPERTY_KEYS, linkId);
            rocketMqTemplate.send(toTopic, builder.build());
            log.info("::{}::消息已转发到数据中心 Topic:{},request:{}", linkId, toTopic, JSON.toJSONString(message));
            return;
        }
        log.info("OddsCalcVersionSwitchMessage:{}", message);
        Integer status = message.getStatus();
        if (Objects.isNull(status)) {
            return;
        }
        oddsCalcVersionService.setVersion(status);
    }
}
