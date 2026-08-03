package com.panda.merge.rocketmq.consumer;


import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.constant.ConstantSystem;
import com.panda.merge.dto.Request;
import com.panda.merge.service.ConfigMarketLevelService;
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

import static com.panda.merge.constant.ConstantSystem.CATEGORY_MARKET_LEVEL;
import static com.panda.merge.constant.ConstantSystem.DATACENTER;
import static com.panda.merge.odds.MQConstant.TOPIC_DATA_SOURCE_SWITCH_CONFIG;

@Slf4j
@Component
@RocketMQMessageListener(
        topic = CATEGORY_MARKET_LEVEL,
        consumerGroup = "odds-group-CONFIG_MARKET_LEVEL",
        consumeThreadMax = 10
)
@DependsOn("oddsAdminApplication")
public class ConfigMarketLevelConsumer implements RocketMQListener<Request<List<Long>>> {

    @Autowired
    private ConfigMarketLevelService configMarketLevelService;
    /**
     * 数据中心赔率状态开关 1开 0关
     */
    @NacosValue(value = "${datacenter.odds.status:1}", autoRefreshed = true)
    private Integer datacenterOddsStatus;

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    @Override
    public void onMessage(Request<List<Long>> request) {
        //数据中心赔率状态开关 1开 0关
        if (datacenterOddsStatus == 1) {
            // 转发消息到数据中心
            log.info("收到 ::{}:: Topic的消息：{}", CATEGORY_MARKET_LEVEL, request.getData());
            String toTopic = CATEGORY_MARKET_LEVEL + DATACENTER;
            String destination = !StringUtils.isEmpty(request.getTag()) ? toTopic + ":" + request.getTag() : toTopic;
            // 发送到 数据中心Topic
            MessageBuilder<Request<List<Long>>> builder = MessageBuilder.withPayload(request)
                    .setHeader(MessageConst.PROPERTY_KEYS, request.getLinkId());
            rocketMqTemplate.send(destination, builder.build());
            log.info("::{}::消息已转发到数据中心 Topic:{},request:{}", request.getLinkId(), toTopic, JSON.toJSONString(request));
            return;
        }
        String linkId = request.getLinkId();
        configMarketLevelService.deleteCacheByIdList(request.getData());
        log.info("::{}::ConfigMarketLevelConsumer,删除缓存成功", linkId);

    }
}
