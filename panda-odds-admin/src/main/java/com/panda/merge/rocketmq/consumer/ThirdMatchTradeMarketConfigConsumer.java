package com.panda.merge.rocketmq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.TradeMarketConfigDTO;
import com.panda.merge.rocketmq.processor.ThirdMatchTradeMarketConfigProcessor;
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

import static com.panda.merge.constant.ConstantSystem.DATACENTER;
import static com.panda.merge.constant.ConstantSystem.THIRD_MATCH_TRADE_MARKET_CONFIG_API;

@Slf4j
@Component
@RocketMQMessageListener(
        topic = THIRD_MATCH_TRADE_MARKET_CONFIG_API,
        consumerGroup = "odds-group-" + THIRD_MATCH_TRADE_MARKET_CONFIG_API,
        consumeThreadMax = 20,
        consumeTimeout = 10000L
)
@DependsOn("oddsAdminApplication")
public class ThirdMatchTradeMarketConfigConsumer implements RocketMQListener<Request<TradeMarketConfigDTO>> {

    @Autowired
    private ThirdMatchTradeMarketConfigProcessor thirdMatchTradeMarketConfigProcessor;

    /**
     * 数据中心赔率状态开关 1开 0关
     */
    @NacosValue(value = "${datacenter.odds.status:1}", autoRefreshed = true)
    private Integer datacenterOddsStatus;

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    @Override
    public void onMessage(Request<TradeMarketConfigDTO> request) {
        //数据中心赔率状态开关 1开 0关
        if (datacenterOddsStatus == 1) {
            // 转发消息到数据中心
            log.info("收到 ::{}:: Topic的消息：{}", THIRD_MATCH_TRADE_MARKET_CONFIG_API, request.getData());
            String toTopic = THIRD_MATCH_TRADE_MARKET_CONFIG_API + DATACENTER;
            String destination = !StringUtils.isEmpty(request.getTag()) ? toTopic + ":" + request.getTag() : toTopic;
            // 发送到 数据中心Topic
            MessageBuilder<Request<TradeMarketConfigDTO>> builder = MessageBuilder.withPayload(request)
                    .setHeader(MessageConst.PROPERTY_KEYS, request.getLinkId());
            rocketMqTemplate.send(destination, builder.build());
            log.info("::{}::消息已转发到 Topic:{},request:{}", request.getLinkId(), toTopic, JSON.toJSONString(request));

            // 融合服务消息转发数据中心服务成功
            log.info("::{}::融合服务开盘开关关闭，消息已经成功转发到数据中心", datacenterOddsStatus);
            return;
        }
        thirdMatchTradeMarketConfigProcessor.processThirdMatchTradeMarketConfig(request);
    }
}
