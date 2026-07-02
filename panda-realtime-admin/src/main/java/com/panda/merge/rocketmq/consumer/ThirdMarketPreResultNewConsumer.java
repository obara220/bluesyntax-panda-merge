package com.panda.merge.rocketmq.consumer;

import com.panda.merge.common.RateLimiterHandler;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdMatchPreResultDTO;
import com.panda.merge.rocketmq.processor.ThirdMarketPreResultNewProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.panda.merge.constant.ConstantSystem.CONSUME_REALTIME_GROUP;
import static com.panda.merge.constant.ConstantSystem.THIRD_MARKET_PRE_RESULT_NEW_API;

/**
 * 消费数据源盘口提前结算信息
 *
 * @author bevan
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = THIRD_MARKET_PRE_RESULT_NEW_API,
        consumerGroup = CONSUME_REALTIME_GROUP+ THIRD_MARKET_PRE_RESULT_NEW_API,
        consumeThreadMax = 256,
        consumeTimeout = 10000L)
@DependsOn("realtimeAdminApplication")
public class ThirdMarketPreResultNewConsumer implements RocketMQListener<Request<ThirdMatchPreResultDTO>> {

    @Lazy
    @Autowired
    private ThirdMarketPreResultNewProcessor thirdMarketPreResultNewProcessor;

    @Resource
    private RateLimiterHandler rateLimiterHandler;

    @Override
    public void onMessage(Request<ThirdMatchPreResultDTO> request) {
        // 3929 【融合】数据商异常下发告警&数据下发限频
        if (!rateLimiterHandler.filter()) {
            log.info("【{}】进入限流状态,提前结算不下发！源赛事ID={}", request.getLinkId(),request.getData().getThirdMatchId());
            return ;
        }
        thirdMarketPreResultNewProcessor.thirdMarketPreResultApi(request);
    }
}
