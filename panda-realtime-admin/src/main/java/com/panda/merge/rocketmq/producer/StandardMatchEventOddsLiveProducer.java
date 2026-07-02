package com.panda.merge.rocketmq.producer;

import com.alibaba.fastjson.JSON;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.StandardOddsLiveStatusMessage;
import com.panda.merge.model.StandardMatchInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * PD事件触发赔率live
 */
@Slf4j
@Component
public class StandardMatchEventOddsLiveProducer extends BaseProcessor {

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    /**
     * 非提前开赛
     * */
    public void sendStandardOddsLiveStatus(String linkId, StandardMatchInfo standardMatchInfo, String dataSourceCode) {
        if (0 == isOddsLive(standardMatchInfo.getId())) {
            return;
        }
        sendStandardOddsLiveStatus(linkId,standardMatchInfo,dataSourceCode,0);
    }

    /**
     * 事件提前开赛
     * */
    public void sendStandardOddsLiveStatus(String linkId, StandardMatchInfo standardMatchInfo, String dataSourceCode,Integer advance) {
        if (0 == isOddsLive(standardMatchInfo.getId())) {
            return;
        }
        StandardOddsLiveStatusMessage standardOddsLiveStatusMessage = new StandardOddsLiveStatusMessage();
        standardOddsLiveStatusMessage.setStandardMatchId(standardMatchInfo.getId());
        standardOddsLiveStatusMessage.setSportId(standardMatchInfo.getSportId());
        standardOddsLiveStatusMessage.setDataSourceCode(dataSourceCode);
        standardOddsLiveStatusMessage.setAdvance(advance);
        Request<StandardOddsLiveStatusMessage> request = new Request<>();
        request.setLinkId(linkId);
        request.setData(standardOddsLiveStatusMessage);

        MessageBuilder<Request<StandardOddsLiveStatusMessage>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, standardMatchInfo.getId());
        log.info("linkId=【{}】开始组装事件触发滚球标识下发,topic=STANDARD_ODDS_LIVE_STATUS,request:{}", linkId, JSON.toJSONString(request));
        //第一个参数表示topic=tag
        rocketMqTemplate.asyncSend("STANDARD_ODDS_LIVE_STATUS:" + linkId, builder.build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("linkId=【{}】,send successful", linkId);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("linkId=【{}】TOPIC={}，send fail; ", linkId, "STANDARD_ODDS_LIVE_STATUS", throwable);
            }
        });
    }
}
