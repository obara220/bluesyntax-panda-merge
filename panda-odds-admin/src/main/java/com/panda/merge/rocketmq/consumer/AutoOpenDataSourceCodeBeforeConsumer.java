package com.panda.merge.rocketmq.consumer;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.ConstantSystem;
import com.panda.merge.dto.Request;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.Map;


/**
 * 风控通知融合切换前的数据源
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = ConstantSystem.AUTO_OPEN_DATA_SOURCE_CODE_BEFORE,
        consumerGroup = "odds-group-" + ConstantSystem.AUTO_OPEN_DATA_SOURCE_CODE_BEFORE,
        consumeThreadMax = 50)
@DependsOn("oddsAdminApplication")
public class AutoOpenDataSourceCodeBeforeConsumer implements RocketMQListener<String> {

    @Autowired
    private RedisService redisService;

    @Override
    public void onMessage(String str) {
        log.info("::{}::autoOpen风控通知融合切换前的数据源", str);
        JSONObject object = JSONObject.parseObject(str);
        Long standardMatchId = object.getLong("standardMatchId");
        JSONObject oldDataSourceCodeObj = object.getJSONObject("oldDataSourceCodeMap");
        Map<String, Object> oldDataSourceCodeObjMap = oldDataSourceCodeObj.getInnerMap();
        redisService.hSetAll(Constant.REDIS_KEY.AUTO_OPEN_DATA_SOURCE_CODE + standardMatchId, oldDataSourceCodeObjMap, RedisConfig.REDIS_HOUR_TIME*5L);
        redisService.hSet(Constant.REDIS_KEY.AUTO_OPEN_DATA_SOURCE_CODE_MATCH, standardMatchId + "", System.currentTimeMillis() + ((RedisConfig.REDIS_HOUR_TIME*5L) * 1000L));

    }
}
