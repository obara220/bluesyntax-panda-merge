package com.panda.merge.rocketmq.producer;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.PreSoldFirstOddsMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * PreSoldReportMessageProducer
 *
 * @description: 预售未开售首次三方盘口告警信息生产者
 * @date: 1/25/2025
 **/
@Slf4j
@Component
public class PreSoldReportMessageProducer {

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    /**
     * 紧急事件告警
     *
     * @param linkId
     * @param aoMatchId
     * @param matchId
     */

    public void send(PreSoldFirstOddsMessage message) {
        JSONObject object = new JSONObject();
        String linkId = "pre_sold_first_odds_" + message.getMatchInfoId();
        Request<JSONArray> request = new Request<>();
        request.setDataSourceCode(message.getSourceCode());
        request.setLinkId(linkId);
        JSONArray datas = new JSONArray();
        JSONObject data = new JSONObject();
        data.put("matchId", message.getMatchInfoId());
        data.put("status", 1);
        data.put("dataSourceCode", message.getSourceCode());
        data.put("eventType", 4);
        datas.add(data);
        request.setData(datas);
        MessageBuilder<Request<JSONArray>> builder = MessageBuilder.withPayload(request)
                .setHeader(MessageConst.PROPERTY_KEYS, message.getMatchInfoId());
        rocketMqTemplate.asyncSend("RCS_MATCH_EVENT_INFO_WARN_NOTICE" + ":" + message.getMatchInfoId(), builder.build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("::{}::, send successful to RCS_MATCH_EVENT_INFO_WARN_NOTICE ", linkId);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::RCS_MATCH_EVENT_INFO_WARN_NOTICE TOPIC={}，send fail; ", linkId, throwable);
            }
        });
    }
}
