package com.panda.merge.odds;

import com.panda.merge.odds.cache.CacheService;
import com.panda.merge.dto.message.LocalCacheRefreshMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import static com.panda.merge.constant.ConstantSystem.CONSUME_NONREALTIME_GROUP;
import static com.panda.merge.odds.constants.CacheConstant.TOPIC_NONREALTIME_CACHE_UPDATE;

/**
 * InternalCacheRefreshConsumer
 *
 * @description: 内部缓存刷新消费
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = TOPIC_NONREALTIME_CACHE_UPDATE, consumerGroup = CONSUME_NONREALTIME_GROUP + TOPIC_NONREALTIME_CACHE_UPDATE,
        messageModel = MessageModel.BROADCASTING)

public class LocalCacheRefreshConsumer implements RocketMQListener<LocalCacheRefreshMessage> {

    @Autowired
    private ApplicationContext applicationContext;


    @Override
    public void onMessage(LocalCacheRefreshMessage message) {
        log.info("local cache refresh message: {}", message);
        String serviceName = message.getCacheServiceName();
        if (StringUtils.isBlank(serviceName)) {
            return;
        }

        try {
            CacheService cacheService = applicationContext.getBean(serviceName, CacheService.class);
            cacheService.refresh(message.getKey());
        } catch (Exception e) {
            log.error("local cache refresh failed:  {}", serviceName, e);
        }

    }
}
