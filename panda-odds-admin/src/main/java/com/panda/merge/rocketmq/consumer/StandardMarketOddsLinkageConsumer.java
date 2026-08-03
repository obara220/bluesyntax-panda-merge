package com.panda.merge.rocketmq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.MarketOddsLinkageConfigMessage;
import com.panda.merge.rocketmq.processor.StandardMarketOddsLinkageProcessor;
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

import java.util.List;

import static com.panda.merge.constant.ConstantSystem.DATACENTER;
import static com.panda.merge.constant.ConstantSystem.RCS_MARKET_ODDS_LINKAGE_CONFIG;

/**
 * 赔率联动配置
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = RCS_MARKET_ODDS_LINKAGE_CONFIG,
        consumerGroup = "odds-group-" + RCS_MARKET_ODDS_LINKAGE_CONFIG,
        consumeThreadMax = 20,
        consumeTimeout = 10000L)
@DependsOn("oddsAdminApplication")
public class StandardMarketOddsLinkageConsumer implements RocketMQListener<Request<List<MarketOddsLinkageConfigMessage>>> {

    @Autowired
    private StandardMarketOddsLinkageProcessor standardMarketOddsLinkageProcessor;
    /**
     * 数据中心赔率状态开关 1开 0关
     */
    @NacosValue(value = "${datacenter.odds.status:1}", autoRefreshed = true)
    private Integer datacenterOddsStatus;

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    @Override
    public void onMessage(Request<List<MarketOddsLinkageConfigMessage>> request) {
        //数据中心赔率状态开关 1开 0关
        if (datacenterOddsStatus == 1) {
            // 转发消息到数据中心
            log.info("收到 ::{}:: Topic的消息：{}", RCS_MARKET_ODDS_LINKAGE_CONFIG, request.getData());
            String toTopic = RCS_MARKET_ODDS_LINKAGE_CONFIG + DATACENTER;
            String destination = !StringUtils.isEmpty(request.getTag()) ? toTopic + ":" + request.getTag() : toTopic;
            // 发送到 数据中心Topic
            MessageBuilder<Request<List<MarketOddsLinkageConfigMessage>>> builder = MessageBuilder.withPayload(request)
                    .setHeader(MessageConst.PROPERTY_KEYS, request.getLinkId());
            rocketMqTemplate.send(destination, builder.build());
            log.info("::{}::消息已转发到数据中心 Topic:{},request:{}", request.getLinkId(), toTopic, JSON.toJSONString(request));
            return;
        }
        standardMarketOddsLinkageProcessor.process(request);
    }
}
