package com.panda.merge.rocketmq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.constant.ConstantSystem;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.OutrightMarketOrderMessage;
import com.panda.merge.rocketmq.processor.OutrightMarketOrderProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import static com.panda.merge.constant.ConstantSystem.*;

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
        consumeThreadMax = 20,
        consumeTimeout = 10000L
)
@DependsOn("oddsAdminApplication")
public class OutrightMarketOrderConsumer implements RocketMQListener<Request<OutrightMarketOrderMessage>> {

    @Autowired
    private OutrightMarketOrderProcessor outrightMarketOrderProcessor;
    /**
     * 数据中心赔率状态开关 1开 0关
     */
    @NacosValue(value = "${datacenter.odds.status:1}", autoRefreshed = true)
    private Integer datacenterOddsStatus;

    @Autowired
    private RocketMQTemplate rocketMqTemplate;
    @Override
    public void onMessage(Request<OutrightMarketOrderMessage> request) {
        //数据中心赔率状态开关 1开 0关
        if (datacenterOddsStatus == 1) {
            // 转发消息到数据中心
            log.info("收到 ::{}:: Topic的消息：{}", OUTRIGHT_MARKET_ORDER_MESSAGE, request.getData());
            String toTopic = OUTRIGHT_MARKET_ORDER_MESSAGE + DATACENTER;
            String destination = !StringUtils.isEmpty(request.getTag()) ? toTopic + ":" + request.getTag() : toTopic;
            // 发送到 数据中心Topic
            MessageBuilder<Request<OutrightMarketOrderMessage>> builder = MessageBuilder.withPayload(request)
                    .setHeader(MessageConst.PROPERTY_KEYS, request.getLinkId());
            rocketMqTemplate.send(destination + "_DATACENTER",builder.build());
            log.info("::{}::消息已转发到数据中心 Topic:{},request:{}", request.getLinkId(), toTopic, JSON.toJSONString(request));
            return;
        }
        outrightMarketOrderProcessor.processOutrightMarketOrder(request);
    }
}
