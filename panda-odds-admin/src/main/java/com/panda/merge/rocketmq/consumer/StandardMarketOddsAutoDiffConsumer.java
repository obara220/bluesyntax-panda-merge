package com.panda.merge.rocketmq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.AutoDiffMarketOddsMessage;
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
import static com.panda.merge.constant.ConstantSystem.RCS_AUTO_DIFF_MARET_ODDS;

/**
 * 足球标准盘口自动水差
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = RCS_AUTO_DIFF_MARET_ODDS,
        consumerGroup = "odds-group-" + RCS_AUTO_DIFF_MARET_ODDS,
        consumeThreadMax = 20,
        consumeTimeout = 10000L)
@DependsOn("oddsAdminApplication")
public class StandardMarketOddsAutoDiffConsumer implements RocketMQListener<Request<List<AutoDiffMarketOddsMessage>>> {


    @Autowired
    RedisService redisService;
    /**
     * 数据中心赔率状态开关 1开 0关
     */
    @NacosValue(value = "${datacenter.odds.status:1}", autoRefreshed = true)
    private Integer datacenterOddsStatus;

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    @Override
    public void onMessage(Request<List<AutoDiffMarketOddsMessage>> request) {
        //数据中心赔率状态开关 1开 0关
        if (datacenterOddsStatus == 1) {
            // 转发消息到数据中心
            log.info("收到 ::{}:: Topic的消息：{}", RCS_AUTO_DIFF_MARET_ODDS, request.getData());
            String toTopic = RCS_AUTO_DIFF_MARET_ODDS + DATACENTER;
            String destination = !StringUtils.isEmpty(request.getTag()) ? toTopic + ":" + request.getTag() : toTopic;
            // 发送到 数据中心Topic
            MessageBuilder<Request<List<AutoDiffMarketOddsMessage>>> builder = MessageBuilder.withPayload(request)
                    .setHeader(MessageConst.PROPERTY_KEYS, request.getLinkId());
            rocketMqTemplate.send(destination, builder.build());
            log.info("::{}::消息已转发到数据中心 Topic:{},request:{}", request.getLinkId(), toTopic, JSON.toJSONString(request));
            return;
        }
        String linkId = request.getLinkId();
        List<AutoDiffMarketOddsMessage> autoDiffMarketOddsMessage = request.getData();
        log.info("::{}::自动跳分跳水：{}", linkId, JSONObject.toJSONString(autoDiffMarketOddsMessage));
        for (AutoDiffMarketOddsMessage diffMarketOddsMessage : autoDiffMarketOddsMessage) {
            redisService.set(Constant.REDIS_KEY.RONGE_MARKET_ODDS_AUTO_DIFF + diffMarketOddsMessage.getMarketId(), diffMarketOddsMessage.getOddType(), RedisConfig.REDIS_DEFAULT_TIME);
        }
    }
}