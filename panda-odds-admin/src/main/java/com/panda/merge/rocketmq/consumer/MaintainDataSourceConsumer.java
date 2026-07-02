package com.panda.merge.rocketmq.consumer;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.common.utils.IdWorker;
import com.panda.merge.config.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import static com.panda.merge.common.enums.Constant.REDIS_KEY.AO_MAINTAIN_DATA_SOURCE;
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

    @Override
    public void onMessage(JSONObject data) {
        String linkId = IdWorker.getId() + "_DATA_SOURCE_MAINTENANCE";
        log.info("::{}::数据源维护消费:{}", linkId, data.toJSONString());
        String dataSourceCode = data.getString("dataSourceCode");// 预警数据源
        Integer enableSwitch = data.getInteger("enableSwitch"); //是否启用(0:禁用，1:启用)
        Long beginTime = data.getLong("beginTime");// 维护开始时间
        Long endTime = data.getLong("endTime");// 维护结束时间
        redisService.hSet(AO_MAINTAIN_DATA_SOURCE, dataSourceCode, enableSwitch + "#" + beginTime + "#" + endTime);
    }
}
