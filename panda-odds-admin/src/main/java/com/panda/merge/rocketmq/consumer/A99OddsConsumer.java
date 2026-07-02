package com.panda.merge.rocketmq.consumer;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.ConstantSystem;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.rocketmq.processor.A99OddsProcessor;
import com.panda.merge.service.ThirdMatchInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * a01 延长开售
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = ConstantSystem.A99_STANDARD_ODDS_API,
        consumerGroup = "odds-group-" + ConstantSystem.A99_STANDARD_ODDS_API,
        consumeThreadMax = 50)
@DependsOn("oddsAdminApplication")
public class A99OddsConsumer implements RocketMQListener<Request<List<StandardMarketDataMessage>>> {

    @Autowired
    private A99OddsProcessor a99OddsProcessor;

    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;

    @Override
    public void onMessage(Request<List<StandardMarketDataMessage>> request) {
        a99OddsProcessor.execute(request);
    }
}
