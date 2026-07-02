package com.panda.merge.rocketmq.consumer;


import com.panda.merge.constant.ConstantSystem;
import com.panda.merge.dto.Request;
import com.panda.merge.service.ConfigMarketLevelService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RocketMQMessageListener(
        topic = ConstantSystem.CATEGORY_MARKET_LEVEL,
        consumerGroup = "odds-group-CONFIG_MARKET_LEVEL",
        consumeThreadMax = 10
)
@DependsOn("oddsAdminApplication")
public class ConfigMarketLevelConsumer implements RocketMQListener<Request<List<Long>>> {

    @Autowired
    private ConfigMarketLevelService configMarketLevelService;

    @Override
    public void onMessage(Request<List<Long>> request) {
        String linkId = request.getLinkId();
        configMarketLevelService.deleteCacheByIdList(request.getData());
        log.info("::{}::ConfigMarketLevelConsumer,删除缓存成功", linkId);

    }
}
