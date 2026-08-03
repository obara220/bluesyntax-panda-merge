package com.panda.merge.rocketmq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.dto.message.LocalCacheRefreshMessage;
import com.panda.merge.odds.cache.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import static com.panda.merge.constant.ConstantSystem.DATACENTER;
import static com.panda.merge.constant.ConstantSystem.PAND_ODDS_GROUP;
import static com.panda.merge.odds.constants.CacheConstant.TOPIC_LOCAL_CACHE_UPDATE;

/**
 * InternalCacheRefreshConsumer
 *
 * @description: 内部缓存刷新消费
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = TOPIC_LOCAL_CACHE_UPDATE, consumerGroup = PAND_ODDS_GROUP + TOPIC_LOCAL_CACHE_UPDATE,
        messageModel = MessageModel.BROADCASTING)
@DependsOn("oddsAdminApplication")
public class LocalCacheRefreshConsumer implements RocketMQListener<LocalCacheRefreshMessage> {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 数据中心赔率状态开关 1开 0关
     */
    @NacosValue(value = "${datacenter.odds.status:1}", autoRefreshed = true)
    private Integer datacenterOddsStatus;

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    @Override
    public void onMessage(LocalCacheRefreshMessage message) {
        //数据中心赔率状态开关 1开 0关
        if (datacenterOddsStatus == 1) {
            Long linkId = UUIdUtils.getId();
            // 转发消息到数据中心
            log.info("收到 ::{}:: Topic的消息：{}", TOPIC_LOCAL_CACHE_UPDATE, message);
            String toTopic = TOPIC_LOCAL_CACHE_UPDATE + DATACENTER;
            // 发送到 数据中心Topic
            MessageBuilder<LocalCacheRefreshMessage> builder = MessageBuilder.withPayload(message)
                    .setHeader(MessageConst.PROPERTY_KEYS, linkId);
            rocketMqTemplate.send(toTopic, builder.build());
            log.info("::{}::消息已转发到数据中心 Topic:{},request:{}", linkId, toTopic, JSON.toJSONString(message));
            return;
        }
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
