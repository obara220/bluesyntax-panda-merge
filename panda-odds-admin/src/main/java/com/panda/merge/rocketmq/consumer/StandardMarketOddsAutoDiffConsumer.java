package com.panda.merge.rocketmq.consumer;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.AutoDiffMarketOddsMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.panda.merge.constant.ConstantSystem.RCS_AUTO_DIFF_MARET_ODDS;

/**
 * 足球标准盘口自动水差
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = RCS_AUTO_DIFF_MARET_ODDS,
        consumerGroup = "odds-group-" + RCS_AUTO_DIFF_MARET_ODDS,
        consumeThreadMax = 20,
        consumeTimeout = 10000L)
@DependsOn("oddsAdminApplication")
public class StandardMarketOddsAutoDiffConsumer implements RocketMQListener<Request<List<AutoDiffMarketOddsMessage>>> {


    @Autowired
    RedisService redisService;

    @Override
    public void onMessage(Request<List<AutoDiffMarketOddsMessage>> request) {
        String linkId = request.getLinkId();
        List<AutoDiffMarketOddsMessage> autoDiffMarketOddsMessage = request.getData();
        log.info("::{}::自动跳分跳水：{}", linkId, JSONObject.toJSONString(autoDiffMarketOddsMessage));
        for (AutoDiffMarketOddsMessage diffMarketOddsMessage : autoDiffMarketOddsMessage) {
            redisService.set(Constant.REDIS_KEY.RONGE_MARKET_ODDS_AUTO_DIFF + diffMarketOddsMessage.getMarketId(), diffMarketOddsMessage.getOddType(), RedisConfig.REDIS_DEFAULT_TIME);
        }
    }
}