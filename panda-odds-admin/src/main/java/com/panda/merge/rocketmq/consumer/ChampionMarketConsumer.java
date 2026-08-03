package com.panda.merge.rocketmq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.StandardMatchMarketDTO;
import com.panda.merge.rocketmq.processor.ChampionMarketProcessor;
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

/**
 * 时间变更、新增盘口
 */

@Slf4j
@Component
@RocketMQMessageListener(
        topic = "CHAMPION_MARKET_SETTING",
        consumerGroup = "odds-group-CHAMPION_MARKET_SETTING",
        consumeThreadMax = 20,
        consumeTimeout = 10000L
)
@DependsOn("oddsAdminApplication")
public class ChampionMarketConsumer implements RocketMQListener<Request<StandardMatchMarketDTO>> {

    @Autowired
    private ChampionMarketProcessor championMarketProcessor;
    /**
     * 数据中心赔率状态开关 1开 0关
     */
    @NacosValue(value = "${datacenter.odds.status:1}", autoRefreshed = true)
    private Integer datacenterOddsStatus;

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    @Override
    public void onMessage(Request<StandardMatchMarketDTO> request) {
        //数据中心赔率状态开关 1开 0关
        if (datacenterOddsStatus == 1) {
            // 转发消息到数据中心
            String fromTopic = "CHAMPION_MARKET_SETTING";
            log.info("收到 ::{}:: Topic的消息：{}", fromTopic, request.getData());
            String toTopic = fromTopic + DATACENTER;
            String destination = !StringUtils.isEmpty(request.getTag()) ? toTopic + ":" + request.getTag() : toTopic;
            // 发送到 数据中心Topic
            MessageBuilder<Request<StandardMatchMarketDTO>> builder = MessageBuilder.withPayload(request)
                    .setHeader(MessageConst.PROPERTY_KEYS, request.getLinkId());
            rocketMqTemplate.send(destination, builder.build());
            log.info("::{}::消息已转发到数据中心 Topic:{},request:{}", request.getLinkId(), toTopic, JSON.toJSONString(request));
            return;
        }
        championMarketProcessor.processChampionMarketSetting(request);
    }
}
