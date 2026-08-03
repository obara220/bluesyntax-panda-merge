package com.panda.merge.rocketmq.producer;

import com.alibaba.fastjson.JSON;
import com.panda.merge.dto.message.MatchOddsWarningMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * @author : Bevan
 * @project Name : panda-merge
 * @package Name : com.panda.merge.rocketmq.producer
 * @description : TODO
 * @date: 2021-02-04 14:29
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */
@Slf4j
@Component
public class MatchOddWarningProducer {

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    /**
     * 通知风控赔率告警
     */
    public void sendMatchOddsWarningRisk(String linkId, Long standardMatchInfoId, Long marketCategoryId, boolean sign) {
        MatchOddsWarningMessage matchOddsWarningMessage = new MatchOddsWarningMessage();
        matchOddsWarningMessage.setStandardMatchId(standardMatchInfoId);
        matchOddsWarningMessage.setMarketCategoryId(marketCategoryId);
        matchOddsWarningMessage.setSign(sign);
        matchOddsWarningMessage.setLinkId(linkId);
        MessageBuilder<MatchOddsWarningMessage> builder = MessageBuilder.withPayload(matchOddsWarningMessage).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        rocketMqTemplate.send("MATCH_ODDS_WARNING_RISK:" + standardMatchInfoId + ":" + marketCategoryId, builder.build());
        log.info("::{}::通知风控赔率告警,topic=MATCH_ODDS_WARNING_RISK,消息体:{}", linkId, JSON.toJSONString(matchOddsWarningMessage));
    }
}
