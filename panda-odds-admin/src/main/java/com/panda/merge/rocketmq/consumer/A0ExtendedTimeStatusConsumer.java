package com.panda.merge.rocketmq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.service.ThirdMatchInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.panda.merge.constant.ConstantSystem.A01_EXTENDED_TIME_STATUS;
import static com.panda.merge.constant.ConstantSystem.DATACENTER;


/**
 * a01 延长开售
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = A01_EXTENDED_TIME_STATUS,
        consumerGroup = "odds-group-" + A01_EXTENDED_TIME_STATUS,
        consumeThreadMax = 50)
@DependsOn("oddsAdminApplication")
public class A0ExtendedTimeStatusConsumer implements RocketMQListener<JSONObject> {

    @Autowired
    private RedisService redisService;

    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;

    /**
     * 数据中心赔率状态开关 1开 0关
     */
    @NacosValue(value = "${datacenter.odds.status:1}", autoRefreshed = true)
    private Integer datacenterOddsStatus;

    @Resource
    private RocketMQTemplate rocketMqTemplate;

    @Override
    public void onMessage(JSONObject object) {
        //数据中心赔率状态开关 1开 0关
        if (datacenterOddsStatus == 1) {
            Long linkId = UUIdUtils.getId();
            // 转发消息到数据中心
            log.info("收到 ::{}:: Topic的消息：{}", A01_EXTENDED_TIME_STATUS, object);
            String toTopic = A01_EXTENDED_TIME_STATUS + DATACENTER;
            // 发送到 数据中心Topic
            MessageBuilder<JSONObject> builder = MessageBuilder.withPayload(object)
                    .setHeader(MessageConst.PROPERTY_KEYS, linkId);
            rocketMqTemplate.send(toTopic, builder.build());
            log.info("::{}::消息已转发到数据中心 Topic:{},request:{}", linkId, toTopic, JSON.toJSONString(object));
            return;
        }
        log.info("::{}::a01延长开售", object);
        Long aoMatchId = object.getLong("aoMatchId");
        Integer extendedTimeStatus = object.getInteger("extendedTimeStatus");
        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem(aoMatchId);
        if (null == thirdMatchInfo || null == thirdMatchInfo.getReferenceId() && thirdMatchInfo.getReferenceId() == 0) {
            return;
        }
        // 延迟开售 1 开  0关
        redisService.set(Constant.REDIS_KEY.A01_EXTENDED_TIME_STATUS_KEY + thirdMatchInfo.getReferenceId(), extendedTimeStatus, RedisConfig.REDIS_DEFAULT_TIME);
    }
}
