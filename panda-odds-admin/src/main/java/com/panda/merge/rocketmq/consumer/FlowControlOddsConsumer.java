package com.panda.merge.rocketmq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.dto.Request;
import com.panda.merge.odds.model.FlowControlNotificationDto;
import com.panda.merge.odds.service.FlowControlConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import static com.panda.merge.constant.ConstantSystem.DATACENTER;
import static com.panda.merge.constant.ConstantSystem.MARKET_NAME_I18N_LIST;
import static com.panda.merge.odds.MQConstant.ODDS_CONSUMER_GROUP;
import static com.panda.merge.odds.MQConstant.TOPIC_FLOW_CONTROL_NOTIFICATION;

/**
 * FlowControlOddsConsumer
 *
 * @description:
 * @date: 7/16/2025
 **/
@Slf4j
@Component
@RocketMQMessageListener(topic = TOPIC_FLOW_CONTROL_NOTIFICATION,
        consumerGroup = ODDS_CONSUMER_GROUP + TOPIC_FLOW_CONTROL_NOTIFICATION,consumeMode = ConsumeMode.ORDERLY)
public class FlowControlOddsConsumer implements RocketMQListener<Request<FlowControlNotificationDto>> {


    @Autowired
    private FlowControlConfigService configService;
    /**
     * 数据中心赔率状态开关 1开 0关
     */
    @NacosValue(value = "${datacenter.odds.status:1}", autoRefreshed = true)
    private Integer datacenterOddsStatus;

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    @Override
    public void onMessage(Request<FlowControlNotificationDto> request) {
        //数据中心赔率状态开关 1开 0关
        if (datacenterOddsStatus == 1) {
            // 转发消息到数据中心
            log.info("收到 ::{}:: Topic的消息：{}", TOPIC_FLOW_CONTROL_NOTIFICATION, request.getData());
            String toTopic = TOPIC_FLOW_CONTROL_NOTIFICATION + DATACENTER;
            String destination = !StringUtils.isEmpty(request.getTag()) ? toTopic + ":" + request.getTag() : toTopic;
            // 发送到 数据中心Topic
            MessageBuilder<Request<FlowControlNotificationDto>> builder = MessageBuilder.withPayload(request)
                    .setHeader(MessageConst.PROPERTY_KEYS, request.getLinkId());
            rocketMqTemplate.send(destination, builder.build());
            log.info("::{}::消息已转发到数据中心 Topic:{},request:{}", request.getLinkId(), toTopic, JSON.toJSONString(request));
            return;
        }
        log.info("linkId:{}, FlowControlOddsConsumer request:{}", request.getLinkId(), request.getData());
        configService.update(request);
    }

}
