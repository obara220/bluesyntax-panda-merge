package com.panda.merge.rocketmq.consumer;

import com.panda.merge.component.UUIdUtils;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.MarketDBMessage;
import com.panda.merge.model.StandardSportMarketOdds;
import com.panda.merge.proxy.StandardSportMarketAndOddsBatchUpdateProxy;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.panda.merge.constant.ConstantSystem.STANDARD_SPORT_MARKET_ODDS_INSERT;

@Slf4j
@Component
@RocketMQMessageListener(topic = STANDARD_SPORT_MARKET_ODDS_INSERT,
        consumerGroup = "odds-group-" + STANDARD_SPORT_MARKET_ODDS_INSERT,
        consumeThreadMax = 256,
        consumeTimeout = 10000L)
@DependsOn("oddsAdminApplication")
public class StandardSportMarketOddsInsertMultipleConsumer  implements RocketMQListener<Request<MarketDBMessage>> {

    @Autowired
    StandardSportMarketAndOddsBatchUpdateProxy standardSportMarketAndOddsBatchUpdateProxy;

    @Override
    public void onMessage(Request<MarketDBMessage> request) {
        String linkIdNew = request.getLinkId();
        String linkId = UUIdUtils.getId() + "_standard_odds_batchInsert";
        MarketDBMessage marketDBMessage = request.getData();
        List<StandardSportMarketOdds> standardSportMarketOdds = marketDBMessage.getStandardSportMarketOdds();
        log.info("::{}::{}::标准盘口赔率新增,批量接收数据: {}", linkId, linkIdNew, standardSportMarketOdds.size());
        standardSportMarketAndOddsBatchUpdateProxy.batchStandardOddsInsert(linkId, standardSportMarketOdds);
    }
}
