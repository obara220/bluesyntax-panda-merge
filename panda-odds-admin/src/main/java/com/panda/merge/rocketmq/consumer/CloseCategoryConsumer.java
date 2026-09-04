package com.panda.merge.rocketmq.consumer;

import java.util.ArrayList;
import java.util.List;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.CloseCategoryDTO;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.StandardMarketMessage;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.rocketmq.processor.ThirdMatchMarketProcessor;
import com.panda.merge.rocketmq.producer.StandardMarketOddsProducer;
import com.panda.merge.service.StandardMatchInfoService;

import cn.hutool.crypto.digest.DigestUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RocketMQMessageListener(
        topic = "CLOSE_CATEGORY_ODDS_ADMIN",
        consumerGroup = "odds-group-CLOSE_CATEGORY_ODDS_ADMIN",
        consumeThreadMax = 20
)
@DependsOn("oddsAdminApplication")
public class CloseCategoryConsumer implements RocketMQListener<Request<CloseCategoryDTO>> {
    @Lazy
    @Autowired
    private StandardMarketOddsProducer standardMarketOddsProducer;
    @Autowired
    private RedisService redisService;
    @Autowired
    private StandardMatchInfoService standardMatchInfoService;

    @Lazy
    @Autowired
    ThirdMatchMarketProcessor thirdMatchMarketProcessor;
    @NacosValue(value = "${category.waitCloseTime.Switch:true}", autoRefreshed = true)
    private boolean waitCloseTimeSwitch;

    @Override
    public void onMessage(Request<CloseCategoryDTO> message) {
        log.info("CloseCategoryConsumer,接收数据为:{}", JSON.toJSONString(message));
        if (!waitCloseTimeSwitch) {
            log.info("处理开关为关闭状态,不进行处理,CloseCategoryConsumer处理结束");
            return;
        }
        List<String> categoryList = message.getData().getCategoryList();
        Long matchId = message.getData().getMatchId();
        String linkId = message.getLinkId();
        long dataSourceTime = System.currentTimeMillis();
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(matchId);
        if (standardMatchInfo == null) {
            log.info("::{}::CloseCategoryConsumer,标准赛事未找到，标准赛事id:{}", linkId, matchId);
            return;
        }
        // 下发玩法赔率
        if (!CollectionUtils.isEmpty(categoryList)) {
            List<StandardMarketMessage> standardMarketMessages = new ArrayList<>();
            String redisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_THE_LAST_MARKETODDS + matchId);
            List<Object> cacheData = redisService.hMulGet(redisKey, categoryList);
            cacheData.forEach(o -> {
                if (null != o) {
                    List<StandardMarketMessage> marketList = (List<StandardMarketMessage>) o;
                    marketList.forEach(e -> {
                        e.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                        e.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                    });
                    standardMarketMessages.addAll(marketList);
                }
            });
            standardMarketOddsProducer.standardMarketOddsAsyncSend(linkId, standardMatchInfo, standardMarketMessages, dataSourceTime, false);
        }
    }
}
