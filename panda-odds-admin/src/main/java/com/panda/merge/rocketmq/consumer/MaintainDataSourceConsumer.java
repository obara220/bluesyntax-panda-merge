package com.panda.merge.rocketmq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.common.utils.IdWorker;
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

import static com.panda.merge.common.enums.Constant.REDIS_KEY.AO_MAINTAIN_DATA_SOURCE;
import static com.panda.merge.constant.ConstantSystem.DATACENTER;
import static com.panda.merge.constant.ConstantSystem.DATA_SOURCE_MAINTENANCE_NOTICE;

/**
 * 4257 数据源维护
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = DATA_SOURCE_MAINTENANCE_NOTICE,
        consumerGroup = "odds-group-" + DATA_SOURCE_MAINTENANCE_NOTICE,
        consumeThreadMax = 20,
        consumeTimeout = 10000L
)
@DependsOn("oddsAdminApplication")
public class MaintainDataSourceConsumer implements RocketMQListener<JSONObject> {

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
    public void onMessage(JSONObject data) {
        //数据中心赔率状态开关 1开 0关
        if (datacenterOddsStatus == 1) {
            Long linkId = UUIdUtils.getId();
            // 转发消息到数据中心
            log.info("收到 ::{}:: Topic的消息：{}", DATA_SOURCE_MAINTENANCE_NOTICE, data);
            String toTopic = DATA_SOURCE_MAINTENANCE_NOTICE + DATACENTER;
            // 发送到 数据中心Topic
            MessageBuilder<JSONObject> builder = MessageBuilder.withPayload(data)
                    .setHeader(MessageConst.PROPERTY_KEYS, linkId);
            rocketMqTemplate.send(toTopic, builder.build());
            log.info("::{}::消息已转发到数据中心 Topic:{},request:{}", linkId, toTopic, JSON.toJSONString(data));
            return;
        }
        String linkId = IdWorker.getId() + "_DATA_SOURCE_MAINTENANCE";
        log.info("::{}::数据源维护消费:{}", linkId, data.toJSONString());
        String dataSourceCode = data.getString("dataSourceCode");// 预警数据源
        Integer enableSwitch = data.getInteger("enableSwitch"); //是否启用(0:禁用，1:启用)
        Long beginTime = data.getLong("beginTime");// 维护开始时间
        Long endTime = data.getLong("endTime");// 维护结束时间
        redisService.hSet(AO_MAINTAIN_DATA_SOURCE, dataSourceCode, enableSwitch + "#" + beginTime + "#" + endTime);
    }
}
