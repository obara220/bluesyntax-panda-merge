package com.panda.merge.rocketmq.consumer;

import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.StandardOddsLiveStatusMessage;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.StandardSportMarketSell;
import com.panda.merge.rocketmq.processor.ThirdMatchMarketProcessor;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.service.StandardSportMarketSellService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * 下发滚球标识
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = "STANDARD_ODDS_LIVE_STATUS", consumerGroup = "odds-group-STANDARD_ODDS_LIVE_STATUS")
@DependsOn("oddsAdminApplication")
public class StandardOddsLiveStatusConsumer implements RocketMQListener<Request<StandardOddsLiveStatusMessage>> {
    @Lazy
    @Autowired
    private ThirdMatchMarketProcessor thirdMatchMarketProcessor;
    @Autowired
    private StandardSportMarketSellService standardSportMarketSellService;
    @Autowired
    private StandardMatchInfoService standardMatchInfoService;
    @Override
    public void onMessage(Request<StandardOddsLiveStatusMessage> request) {
        String linkId = request.getLinkId();
        StandardOddsLiveStatusMessage standardOddsLiveStatusMessage = request.getData();
        Long standardMatchId = standardOddsLiveStatusMessage.getStandardMatchId();
        Long sportId = standardOddsLiveStatusMessage.getSportId();
        String dataSourceCode = standardOddsLiveStatusMessage.getDataSourceCode();
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardMatchId);
        StandardSportMarketSell standardSportMarketSell = standardSportMarketSellService.getItem(standardMatchId);
        thirdMatchMarketProcessor.newClosePreMarkets(linkId, standardSportMarketSell, 0, standardMatchInfo, request.getDataSourceTime(),
                Boolean.TRUE, new ArrayList<>(), standardOddsLiveStatusMessage.getAdvance());
    }
}
