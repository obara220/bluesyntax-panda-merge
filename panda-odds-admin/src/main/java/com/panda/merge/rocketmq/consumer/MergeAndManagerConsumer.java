package com.panda.merge.rocketmq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.ConstantSystem;
import com.panda.merge.dto.ModifyMarketCache;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.model.StandardSportMarket;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import static com.panda.merge.constant.ConstantSystem.CLEAR_OUTRIGHT_MARKET;
import static com.panda.merge.constant.ConstantSystem.DATACENTER;
import static com.panda.merge.odds.constants.CacheConstant.ODDS_CALCULATION_CATEGORY_GROUP_UPDATE;

/**
 * @Author Kepa
 * @Date 2021/7/18 18:03
 * @Version 1.0
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = CLEAR_OUTRIGHT_MARKET,
        consumerGroup = "odds-group-"+CLEAR_OUTRIGHT_MARKET,
        consumeThreadMax = 20,
        consumeTimeout = 10000L
)
@DependsOn("oddsAdminApplication")
public class MergeAndManagerConsumer implements RocketMQListener<Request<ModifyMarketCache>> {

    @Autowired
    public RedisService redisService;
    /**
     * 数据中心赔率状态开关 1开 0关
     */
    @NacosValue(value = "${datacenter.odds.status:1}", autoRefreshed = true)
    private Integer datacenterOddsStatus;

    @Autowired
    private RocketMQTemplate rocketMqTemplate;
    @Override
    public void onMessage(Request<ModifyMarketCache> request) {
        //数据中心赔率状态开关 1开 0关
        if (datacenterOddsStatus == 1) {
            // 转发消息到数据中心
            log.info("收到 ::{}:: Topic的消息：{}", CLEAR_OUTRIGHT_MARKET, request.getData());
            String toTopic = CLEAR_OUTRIGHT_MARKET + DATACENTER;
            String destination = !StringUtils.isEmpty(request.getTag()) ? toTopic + ":" + request.getTag() : toTopic;
            // 发送到 数据中心Topic
            MessageBuilder<Request<ModifyMarketCache>> builder = MessageBuilder.withPayload(request)
                    .setHeader(MessageConst.PROPERTY_KEYS, request.getLinkId());
            rocketMqTemplate.send(destination,builder.build());
            log.info("::{}::消息已转发到数据中心 Topic:{},request:{}", request.getLinkId(), toTopic, JSON.toJSONString(request));
            return;
        }
        log.info("融合清理盘口数据 params={}", JSON.toJSON(request));
        String linkId = request.getLinkId();
        ModifyMarketCache modifyMarketCache = request.getData();
        String key = modifyMarketCache.getKey();
        Long timeout = modifyMarketCache.getTimeout();
        StandardSportMarket standardSportMarket = modifyMarketCache.getStandardSportMarket();

        String modifyKey = ConstantSystem.CHAMPION_CACHE + standardSportMarket.getRelationMarketId();
        log.info("::{}:: 记录修改缓存的参数: {}",linkId, modifyKey);
        redisService.set(modifyKey, standardSportMarket, RedisConfig.REDIS_YEAR_TIME);
        log.info("::{}:: 记录修改缓存完成 redisKey: {}",linkId, modifyKey);
        //刷新盘口信息
        String marketKey = Constant.REDIS_KEY.RONGHE_STANDARD_MARKET + standardSportMarket.getStandardMatchInfoId() + "_" + standardSportMarket.getDataSourceCode();
        log.info("::{}:: processOutrightMarketOrder redisKey={} ", linkId, marketKey);
        Object obj = redisService.hGet(marketKey,standardSportMarket.getRelationMarketId().toString());
        if (null != obj)
        {
            StandardMarketDataMessage standardMarketDataMessage = (StandardMarketDataMessage)obj;
            standardMarketDataMessage.setAddition2(standardSportMarket.getAddition2());
            standardMarketDataMessage.setAddition3(standardSportMarket.getAddition3());
            redisService.hSet(marketKey,standardSportMarket.getRelationMarketId().toString(),standardMarketDataMessage, RedisConfig.REDIS_YEAR_TIME);
        }
    }

}

