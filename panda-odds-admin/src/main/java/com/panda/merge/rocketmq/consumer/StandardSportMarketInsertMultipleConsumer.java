package com.panda.merge.rocketmq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.MarketDBMessage;
import com.panda.merge.model.StandardSportMarket;
import com.panda.merge.proxy.StandardSportMarketAndOddsBatchUpdateProxy;
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

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * 标准盘口新增
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = STANDARD_SPORT_MARKET_INSERT,
        consumerGroup = "odds-group-" + STANDARD_SPORT_MARKET_INSERT,
        consumeThreadMax = 256,
        consumeTimeout = 10000L)
@DependsOn("oddsAdminApplication")
public class StandardSportMarketInsertMultipleConsumer implements RocketMQListener<Request<MarketDBMessage>> {

    @Autowired
    StandardSportMarketAndOddsBatchUpdateProxy standardSportMarketAndOddsBatchUpdateProxy;
    /**
     * 数据中心赔率状态开关 1开 0关
     */
    @NacosValue(value = "${datacenter.odds.status:1}", autoRefreshed = true)
    private Integer datacenterOddsStatus;

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    @Override
    public void onMessage(Request<MarketDBMessage> request) {
        //数据中心赔率状态开关 1开 0关
        if (datacenterOddsStatus == 1) {
            // 转发消息到数据中心
            log.info("收到 ::{}:: Topic的消息：{}", STANDARD_SPORT_MARKET_INSERT, request.getData());
            String toTopic = STANDARD_SPORT_MARKET_INSERT + DATACENTER;
            String destination = !StringUtils.isEmpty(request.getTag()) ? toTopic + ":" + request.getTag() : toTopic;
            // 发送到 数据中心Topic
            MessageBuilder<Request<MarketDBMessage>> builder = MessageBuilder.withPayload(request)
                    .setHeader(MessageConst.PROPERTY_KEYS, request.getLinkId());
            rocketMqTemplate.send(destination, builder.build());
            log.info("::{}::消息已转发到数据中心 Topic:{},request:{}", request.getLinkId(), toTopic, JSON.toJSONString(request));
            return;
        }
        String linkIdNew = request.getLinkId();
        String linkId = UUIdUtils.getId() + "_standard_market_batchInsert";
        MarketDBMessage marketDBMessage = request.getData();
        List<StandardSportMarket> standardSportMarkets = marketDBMessage.getStandardSportMarkets();
        log.info("::{}::{}::标准盘口新增,批量接收数据: {}", linkId, linkIdNew, standardSportMarkets.size());
        standardSportMarketAndOddsBatchUpdateProxy.batchStandardMarketInsert(linkId, standardSportMarkets);
    }
}
