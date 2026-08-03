package com.panda.merge.rocketmq.consumer;


import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.OddsCalcCategoryGroupUpdateMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.DependsOn;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

import static com.panda.merge.cache.CacheConstant.CACHE_SPORT_CATEGORY_GROUP;
import static com.panda.merge.constant.ConstantSystem.DATACENTER;
import static com.panda.merge.constant.ConstantSystem.PAND_ODDS_GROUP;
import static com.panda.merge.odds.constants.CacheConstant.ODDS_CALCULATION_CATEGORY_GROUP_UPDATE;


/**
 * @name: OddsCalcCategoryGroupUpdateConsumer
 * @description: 赔率计算玩法分组更新
 * @date: 1/12/2025
 **/

@Slf4j
@Component
@RocketMQMessageListener(topic = ODDS_CALCULATION_CATEGORY_GROUP_UPDATE,
        consumerGroup = PAND_ODDS_GROUP + ODDS_CALCULATION_CATEGORY_GROUP_UPDATE,
        messageModel = MessageModel.BROADCASTING)
@DependsOn("oddsAdminApplication")
public class OddsCalcCategoryGroupUpdateConsumer
        implements RocketMQListener<Request<OddsCalcCategoryGroupUpdateMessage>> {

    @Resource(name = "localCacheManager")
    private CacheManager cacheManager;
    /**
     * 数据中心赔率状态开关 1开 0关
     */
    @NacosValue(value = "${datacenter.odds.status:1}", autoRefreshed = true)
    private Integer datacenterOddsStatus;

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    @Override
    public void onMessage(Request<OddsCalcCategoryGroupUpdateMessage> request) {
        //数据中心赔率状态开关 1开 0关
        if (datacenterOddsStatus == 1) {
            // 转发消息到数据中心
            log.info("收到 ::{}:: Topic的消息：{}", ODDS_CALCULATION_CATEGORY_GROUP_UPDATE, request.getData());
            String toTopic = ODDS_CALCULATION_CATEGORY_GROUP_UPDATE + DATACENTER;
            String destination = !StringUtils.isEmpty(request.getTag()) ? toTopic + ":" + request.getTag() : toTopic;
            // 发送到 数据中心Topic
            MessageBuilder<Request<OddsCalcCategoryGroupUpdateMessage>> builder = MessageBuilder.withPayload(request)
                    .setHeader(MessageConst.PROPERTY_KEYS, request.getLinkId());
            rocketMqTemplate.send(destination, builder.build());
            log.info("::{}::消息已转发到数据中心 Topic:{},request:{}", request.getLinkId(), toTopic, JSON.toJSONString(request));
            return;
        }
        OddsCalcCategoryGroupUpdateMessage data = request.getData();
        log.info("{}: OddsCalcCategoryGroupUpdateMessage:{}", request.getLinkId(), data);
        Long sportId = data.getSportId();
        if (Objects.isNull(sportId)) {
            log.warn("{}: invalid OddsCalcCategoryGroupUpdateMessage empty sportId", request.getLinkId());
            return;
        }

        Cache sportCategoryGroup = cacheManager.getCache(CACHE_SPORT_CATEGORY_GROUP);
        if (Objects.nonNull(sportCategoryGroup)) {
            Map<Long, Integer> cache = sportCategoryGroup.get(sportId, Map.class);
            sportCategoryGroup.evict(sportId);
            log.info("{} sportCategoryGroup evict sportId:{},content: {}", request.getLinkId(), sportId, cache);
        }
    }
}
