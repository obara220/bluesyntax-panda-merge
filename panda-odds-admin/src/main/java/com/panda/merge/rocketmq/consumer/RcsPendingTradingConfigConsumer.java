package com.panda.merge.rocketmq.consumer;

import com.panda.merge.config.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;

import static com.panda.merge.common.enums.Constant.REDIS_KEY.RONGHE_TRAD_CONFIG;
import static com.panda.merge.config.RedisConfig.REDIS_YEAR_TIME;
import static com.panda.merge.constant.ConstantSystem.RCS_PENDING_TRADING_CONFIG;


/**
 * GTS RTS CTS OTS MTS
 *
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = RCS_PENDING_TRADING_CONFIG,
        consumerGroup = "odds-group-"+RCS_PENDING_TRADING_CONFIG,
        consumeThreadMax = 20,
        consumeTimeout = 10000L
)
@DependsOn("oddsAdminApplication")
public class RcsPendingTradingConfigConsumer  implements RocketMQListener<Map<String,Integer>> {

    @Lazy
    @Autowired
    private RedisService redisService;



    @Override
    public void onMessage(Map<String,Integer> map) {
        log.info("::rcsPendingTradingConfigConsumer::接收到消息：：{}",map);
        if(!CollectionUtils.isEmpty(map)){
            redisService.set(RONGHE_TRAD_CONFIG,map,REDIS_YEAR_TIME);
            log.info("::rcsPendingTradingConfigConsumer::缓存刷新成功：：{}",map);
        }
    }
}
