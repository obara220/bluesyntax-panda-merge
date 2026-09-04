package com.panda.merge.rocketmq.consumer;

import com.panda.merge.component.UUIdUtils;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.MarketDBMessage;
import com.panda.merge.model.ThirdSportMarketOdds;
import com.panda.merge.proxy.ThirdSportMarketAndOddsBatchUpdateProxy;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.panda.merge.constant.ConstantSystem.THIRD_SPORT_MARKET_ODDS_INSERT;

@Slf4j
@Component
@RocketMQMessageListener(topic = THIRD_SPORT_MARKET_ODDS_INSERT,
        consumerGroup = "odds-group-" + THIRD_SPORT_MARKET_ODDS_INSERT,
        consumeThreadMax = 256,
        consumeTimeout = 10000L)
@DependsOn("oddsAdminApplication")
public class ThirdSportMarketOddsInsertMultipleConsumer implements RocketMQListener<Request<MarketDBMessage>> {

    @Autowired
    ThirdSportMarketAndOddsBatchUpdateProxy thirdSportMarketAndOddsBatchUpdateProxy;

    @Override
    public void onMessage(Request<MarketDBMessage> request) {
        String linkId = request.getLinkId();
        String linkIdNew = UUIdUtils.getId() + "_third_odds_batchInsert";
        MarketDBMessage marketDBMessage = request.getData();
        List<ThirdSportMarketOdds> thirdSportMarketOdds = marketDBMessage.getThirdSportMarketOdds();
        log.info("::{}::{}::三方盘口赔率新增,批量接收数据: {}", linkId, linkIdNew, thirdSportMarketOdds.size());
        thirdSportMarketAndOddsBatchUpdateProxy.batchOddsInsert(linkId, thirdSportMarketOdds);
    }
}
