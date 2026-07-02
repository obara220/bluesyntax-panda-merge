package com.panda.merge.rocketmq.consumer;

import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdMatchResultDTO;
import com.panda.merge.rocketmq.processor.ThirdMarketResultProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import static com.panda.merge.constant.ConstantSystem.THIRD_MARKET_RESULT_API;

/**
 * 消费数据源赛果信息
 * @author    Aison
 * @since     2020年11月18日16:55:03
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = THIRD_MARKET_RESULT_API,
        consumerGroup = "odds-group-"+THIRD_MARKET_RESULT_API,
        consumeThreadMax = 256,
        consumeTimeout = 10000L
)
@DependsOn("oddsAdminApplication")
public class ThirdMarketResultConsumer implements RocketMQListener<Request<ThirdMatchResultDTO>> {

    @Autowired
    private ThirdMarketResultProcessor thirdMarketResultProcessor;

    @Override
    public void onMessage(Request<ThirdMatchResultDTO> soldMessageRequest) {
        thirdMarketResultProcessor.thirdMarketResultApi(soldMessageRequest);
    }
}
