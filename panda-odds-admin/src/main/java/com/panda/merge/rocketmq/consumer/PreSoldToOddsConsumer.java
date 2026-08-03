package com.panda.merge.rocketmq.consumer;


import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.dto.Request;
import com.panda.merge.model.StandardSportMarketSell;
import com.panda.merge.rocketmq.processor.PreSoldMessageToOddsProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import static com.panda.merge.constant.ConstantSystem.*;

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
    /**
     * 数据中心赔率状态开关 1开 0关
     */
    @NacosValue(value = "${datacenter.odds.status:1}", autoRefreshed = true)
    private Integer datacenterOddsStatus;

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    @Override
    public void onMessage(Request<StandardSportMarketSell> request) {
        //数据中心赔率状态开关 1开 0关
        if (datacenterOddsStatus == 1) {
            // 转发消息到数据中心
            log.info("收到 ::{}:: Topic的消息：{}", MATCH_ADVANCE_SALE, request.getData());
            String toTopic = MATCH_ADVANCE_SALE + DATACENTER;
            String destination = !StringUtils.isEmpty(request.getTag()) ? toTopic + ":" + request.getTag() : toTopic;
            // 发送到 数据中心Topic
            MessageBuilder<Request<StandardSportMarketSell>> builder = MessageBuilder.withPayload(request)
                    .setHeader(MessageConst.PROPERTY_KEYS, request.getLinkId());
            rocketMqTemplate.send(destination, builder.build());
            log.info("::{}::消息已转发到数据中心 Topic:{},request:{}", request.getLinkId(), toTopic, JSON.toJSONString(request));
            return;
        }
        log.info("{} pre sold message {}", request.getLinkId(), request);
        processor.process(request);
    }
}
