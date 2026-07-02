package com.panda.merge.rocketmq.consumer;

import com.alibaba.fastjson.JSON;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.ConstantSystem;
import com.panda.merge.dto.ModifyMarketCache;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.model.StandardSportMarket;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import static com.panda.merge.constant.ConstantSystem.CLEAR_OUTRIGHT_MARKET;

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
        consumeThreadMax = 256,
        consumeTimeout = 10000L
)
@DependsOn("oddsAdminApplication")
public class MergeAndManagerConsumer implements RocketMQListener<Request<ModifyMarketCache>> {

    @Autowired
    public RedisService redisService;

    @Override
    public void onMessage(Request<ModifyMarketCache> request) {
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

