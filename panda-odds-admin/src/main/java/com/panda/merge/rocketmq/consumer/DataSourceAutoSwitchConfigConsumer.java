package com.panda.merge.rocketmq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.odds.DataSourceAutoSwitchConfig;
import com.panda.merge.odds.AutoSwitchConfigService;
import com.panda.merge.validator.ValidatorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.validation.Validator;

import static com.panda.merge.constant.ConstantSystem.DATACENTER;
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

    /**
     * 数据中心赔率状态开关 1开 0关
     */
    @NacosValue(value = "${datacenter.odds.status:1}", autoRefreshed = true)
    private Integer datacenterOddsStatus;

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    @Override
    public void onMessage(Request<DataSourceAutoSwitchConfig> request) {
        //数据中心赔率状态开关 1开 0关
        if (datacenterOddsStatus == 1) {
            // 转发消息到数据中心
            log.info("收到 ::{}:: Topic的消息：{}", TOPIC_DATA_SOURCE_SWITCH_CONFIG, request.getData());
            String toTopic = TOPIC_DATA_SOURCE_SWITCH_CONFIG + DATACENTER;
            String destination = !StringUtils.isEmpty(request.getTag()) ? toTopic + ":" + request.getTag() : toTopic;
            // 发送到 数据中心Topic
            MessageBuilder<Request<DataSourceAutoSwitchConfig>> builder = MessageBuilder.withPayload(request)
                    .setHeader(MessageConst.PROPERTY_KEYS, request.getLinkId());
            rocketMqTemplate.send(destination, builder.build());
            log.info("::{}::消息已转发到数据中心 Topic:{},request:{}", request.getLinkId(), toTopic, JSON.toJSONString(request));
            return;
        }
        log.info("DataSourceAutoSwitchConfigConsumer request:{}", request);
        ValidatorUtils.validate(validator, request);
        autoSwitchConfigService.update(request.getData(), request.getLinkId());
    }
}
