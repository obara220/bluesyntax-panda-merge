package com.panda.merge.rocketmq.producer;

import com.alibaba.fastjson.JSON;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.MarketCategorySellMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class MarketCategorySellProducer {
    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    public void sendStandardMarketCategorySell( Long matchId, Integer matchType, Map<String,
            List<Long>> playDataSource) {
        Request<MarketCategorySellMessage> request = new Request<>();
        String linkId = RandomStringUtils.random(32, true, true) + "_CategorySell";
        MarketCategorySellMessage marketCategorySellMessage = new MarketCategorySellMessage();
        marketCategorySellMessage.setMatchId(matchId);
        marketCategorySellMessage.setMatchType(matchType);
        marketCategorySellMessage.setPlayDataSource(playDataSource);
        request.setData(marketCategorySellMessage);
        request.setLinkId(linkId);
        {
            MessageBuilder<String> standardMarketCategorySellMessageBuilder =
                    MessageBuilder.withPayload(JSON.toJSONString(request))
                            .setHeader(MessageConst.PROPERTY_KEYS, linkId).setHeader(MessageConst.PROPERTY_TAGS,
                            "玩法数据源变更下发风控").setHeader(MessageConst.PROPERTY_TAGS,
                            marketCategorySellMessage.getMatchId());
            rocketMqTemplate.send("STANDARD_MARKET_CATEGORY_SELL", standardMarketCategorySellMessageBuilder.build());
            log.info("::{}::玩法数据源变更下发风控:{}", request.getLinkId(), JSON.toJSONString(request));
        }
    }
}
