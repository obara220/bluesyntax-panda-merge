package com.panda.merge.config;

import com.panda.merge.common.RateLimiterHandler;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.service.ThirdMatchInfoService;
import org.apache.rocketmq.spring.autoconfigure.RocketMQProperties;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

import static com.panda.merge.constant.ConstantSystem.CONSUME_NONREALTIME_GROUP;
import static com.panda.merge.constant.ConstantSystem.CONSUME_REALTIME_GROUP;

@Configuration
public class RateLimiterHandlerConfig {

    @Resource
    private RocketMQProperties rocketMQProperties;
    @Resource
    private RocketMQTemplate rocketMQTemplate;
    @Resource
    private RedisService redisService;
    @Resource
    private ThirdMatchInfoService thirdMatchInfoService;

    @Bean
    public RateLimiterHandler rateLimiterHandler(){
        RateLimiterHandler rateLimiterHandler = new RateLimiterHandler(rocketMQProperties, rocketMQTemplate,redisService,thirdMatchInfoService);
        rateLimiterHandler.initCache();
        rateLimiterHandler.initForwardConsumer(CONSUME_REALTIME_GROUP);
        rateLimiterHandler.initConsumer(CONSUME_REALTIME_GROUP);
        return rateLimiterHandler;
    }
}
