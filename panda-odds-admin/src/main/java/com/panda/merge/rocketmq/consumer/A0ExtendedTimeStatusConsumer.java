package com.panda.merge.rocketmq.consumer;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.ConstantSystem;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.service.ThirdMatchInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;


/**
 * a01 延长开售
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = ConstantSystem.A01_EXTENDED_TIME_STATUS,
        consumerGroup = "odds-group-" + ConstantSystem.A01_EXTENDED_TIME_STATUS,
        consumeThreadMax = 50)
@DependsOn("oddsAdminApplication")
public class A0ExtendedTimeStatusConsumer implements RocketMQListener<JSONObject> {

    @Autowired
    private RedisService redisService;

    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;

    @Override
    public void onMessage(JSONObject object) {
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
