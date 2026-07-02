package com.panda.merge.rocketmq.consumer;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.config.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

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

    @Override
    public void onMessage(JSONObject message) {
        log.info("接收篮球独赢独赢原始赔率限制：{}", message.toJSONString());
        redisService.setLongTime(Constant.REDIS_KEY.RCS_BASKETBALL_ORIGINAL_ODDS_LIMIT, message.getDouble("originalOdds"));
        log.info("接收篮球独赢独赢原始赔率限制，完成：{}", message.toJSONString());

    }
}
