package com.panda.merge.odds.cache;

import com.panda.merge.dto.message.LocalCacheRefreshMessage;
import com.panda.merge.odds.constants.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * InternalCacheRefreshProducer
 *
 * @description: 缓存刷新事件生产者
 */
@Slf4j
@Component
public class LocalCacheRefreshProducer {

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    /**
     * 缓存刷新事件发送
     *
     * @param message 缓存刷新信息
     */
    public void send(LocalCacheRefreshMessage message) {
        send(message, CacheConstant.TOPIC_LOCAL_CACHE_UPDATE);
    }

    public void send(LocalCacheRefreshMessage message, String topic) {
        MessageBuilder<LocalCacheRefreshMessage> builder =
                MessageBuilder.withPayload(message).setHeader(MessageConst.PROPERTY_KEYS, message.getLinkId());
        rocketMqTemplate.send(topic + ":" + message.getLinkId(), builder.build());
        log.info("linkId:{},sendCacheRefreshMessage:{},topic:{}", message.getLinkId(), message,
                 topic);
    }
}
