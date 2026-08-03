package com.panda.merge.rocketmq.consumer;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.ConstantSystem;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.panda.merge.constant.ConstantSystem.AUTO_OPEN_DATA_SOURCE_CODE_BEFORE;


/**
 * 风控通知融合切换前的数据源
 */
@Slf4j
@Component
        @RocketMQMessageListener(topic = AUTO_OPEN_DATA_SOURCE_CODE_BEFORE,
        consumerGroup = "odds-group-" + AUTO_OPEN_DATA_SOURCE_CODE_BEFORE,
        consumeThreadMax = 50)
@DependsOn("oddsAdminApplication")
public class AutoOpenDataSourceCodeBeforeConsumer implements RocketMQListener<String> {

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
    public void onMessage(String str) {
        //数据中心赔率状态开关 1开 0关
        if (datacenterOddsStatus == 1) {
            Long linkId = UUIdUtils.getId();
            // 转发消息到数据中心
            log.info("收到 ::{}:: Topic的消息：{}", AUTO_OPEN_DATA_SOURCE_CODE_BEFORE, str);
            String toTopic = AUTO_OPEN_DATA_SOURCE_CODE_BEFORE + "_DATACENTER";
            // 发送到 数据中心Topic
            MessageBuilder<String> builder = MessageBuilder.withPayload(str)
                    .setHeader(MessageConst.PROPERTY_KEYS, linkId);
            rocketMqTemplate.send(toTopic, builder.build());
            log.info("::{}::消息已转发到数据中心 Topic:{},request:{}", linkId, toTopic, str);
            return;
        }
        log.info("::{}::autoOpen风控通知融合切换前的数据源", str);
        JSONObject object = JSONObject.parseObject(str);
        Long standardMatchId = object.getLong("standardMatchId");
        JSONObject oldDataSourceCodeObj = object.getJSONObject("oldDataSourceCodeMap");
        Map<String, Object> oldDataSourceCodeObjMap = oldDataSourceCodeObj.getInnerMap();
        redisService.hSetAll(Constant.REDIS_KEY.AUTO_OPEN_DATA_SOURCE_CODE + standardMatchId, oldDataSourceCodeObjMap, RedisConfig.REDIS_HOUR_TIME*5L);
        redisService.hSet(Constant.REDIS_KEY.AUTO_OPEN_DATA_SOURCE_CODE_MATCH, standardMatchId + "", System.currentTimeMillis() + ((RedisConfig.REDIS_HOUR_TIME*5L) * 1000L));

    }
}
