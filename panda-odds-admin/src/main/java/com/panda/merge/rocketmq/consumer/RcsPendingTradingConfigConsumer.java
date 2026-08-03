package com.panda.merge.rocketmq.consumer;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;

import static com.panda.merge.common.enums.Constant.REDIS_KEY.RONGHE_TRAD_CONFIG;
import static com.panda.merge.config.RedisConfig.REDIS_YEAR_TIME;
import static com.panda.merge.constant.ConstantSystem.DATACENTER;
import static com.panda.merge.constant.ConstantSystem.RCS_PENDING_TRADING_CONFIG;


/**
 * GTS RTS CTS OTS MTS
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = RCS_PENDING_TRADING_CONFIG,
        consumerGroup = "odds-group-" + RCS_PENDING_TRADING_CONFIG,
        consumeThreadMax = 20,
        consumeTimeout = 10000L
)
@DependsOn("oddsAdminApplication")
public class RcsPendingTradingConfigConsumer implements RocketMQListener<Map<String, Integer>> {

    @Lazy
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
    public void onMessage(Map<String, Integer> map) {
        //数据中心赔率状态开关 1开 0关
        if (datacenterOddsStatus == 1) {
            Long linkId = UUIdUtils.getId();
            // 转发消息到数据中心
            log.info("收到 ::{}:: Topic的消息：{}", RCS_PENDING_TRADING_CONFIG, map);
            String toTopic = RCS_PENDING_TRADING_CONFIG + DATACENTER;
            // 发送到 数据中心Topic
            MessageBuilder<Map<String, Integer>> builder = MessageBuilder.withPayload(map)
                    .setHeader(MessageConst.PROPERTY_KEYS, linkId);
            rocketMqTemplate.send(toTopic, builder.build());
            log.info("::{}::消息已转发到数据中心 Topic:{},map:{}", linkId, toTopic, map);
            return;
        }
        log.info("::rcsPendingTradingConfigConsumer::接收到消息：：{}", map);
        if (!CollectionUtils.isEmpty(map)) {
            redisService.set(RONGHE_TRAD_CONFIG, map, REDIS_YEAR_TIME);
            log.info("::rcsPendingTradingConfigConsumer::缓存刷新成功：：{}", map);
        }
    }
}
