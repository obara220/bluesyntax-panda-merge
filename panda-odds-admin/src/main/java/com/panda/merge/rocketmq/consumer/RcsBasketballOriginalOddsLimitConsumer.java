package com.panda.merge.rocketmq.consumer;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import static com.panda.merge.constant.ConstantSystem.DATACENTER;
import static com.panda.merge.constant.ConstantSystem.RCS_BASKETBALL_ORIGINALODDS_LIMIT;

@Slf4j
@Component
@RocketMQMessageListener(topic = RCS_BASKETBALL_ORIGINALODDS_LIMIT,
        consumerGroup = "odds-group-" + RCS_BASKETBALL_ORIGINALODDS_LIMIT,
        consumeThreadMax = 10, consumeTimeout = 10000L)
@DependsOn("oddsAdminApplication")
public class RcsBasketballOriginalOddsLimitConsumer implements RocketMQListener<JSONObject> {

    @Autowired
    private RedisService redisService;

    /**
     * 数据中心赔率状态开关 1开 0关
     */
    @NacosValue(value = "${datacenter.odds.status:1}", autoRefreshed = true)
    private Integer datacenterOddsStatus;

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    @Override
    public void onMessage(JSONObject message) {
        //数据中心赔率状态开关 1开 0关
        if (datacenterOddsStatus == 1) {
            Long linkId = UUIdUtils.getId();
            // 转发消息到数据中心
            log.info("收到 ::{}:: Topic的消息：{}", RCS_BASKETBALL_ORIGINALODDS_LIMIT, message.toJSONString());
            String toTopic = RCS_BASKETBALL_ORIGINALODDS_LIMIT + DATACENTER;
            // 发送到 数据中心Topic
            MessageBuilder<JSONObject> builder = MessageBuilder.withPayload(message)
                    .setHeader(MessageConst.PROPERTY_KEYS, linkId);
            rocketMqTemplate.send(toTopic, builder.build());
            log.info("::{}::消息已转发到数据中心 Topic:{},map:{}", linkId, toTopic, message.toJSONString());
            return;
        }

        log.info("接收篮球独赢独赢原始赔率限制：{}", message.toJSONString());
        redisService.setLongTime(Constant.REDIS_KEY.RCS_BASKETBALL_ORIGINAL_ODDS_LIMIT, message.getDouble("originalOdds"));
        log.info("接收篮球独赢独赢原始赔率限制，完成：{}", message.toJSONString());

    }
}
