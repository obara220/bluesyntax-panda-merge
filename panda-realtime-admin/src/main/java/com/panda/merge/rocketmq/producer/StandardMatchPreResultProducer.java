package com.panda.merge.rocketmq.producer;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.StandardMatchMarketPreMessage;
import com.panda.merge.dto.message.StandardMatchMarketPreResultMessage;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.rocketmq.spare.SpareBaseProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 下发提前结算盘口信息
 */
@Slf4j
@Component
public class StandardMatchPreResultProducer extends BaseProcessor {

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    /** 备MQ生产者*/
    @Autowired
    private SpareBaseProducer spareBaseProducer;

    /** mq主备配置 1:主 2:备*/
    @NacosValue(value = "${panda.data.mq.gateway.event:1}", autoRefreshed = true)
    private int pandaDataMqGatewayevent = 1;

    /**
     * 下发提前结算盘口信息
     *
     * @param linkId
     */
    public void sendStandardMatchPreResult(String linkId, StandardMatchInfo standardMatchInfo, Long sportId, List<StandardMatchMarketPreResultMessage> marketPreResultMessageList, Integer matchPreStatus, Long dataSourceTime) {
        StandardMatchMarketPreMessage matchMarketPreMessage = new StandardMatchMarketPreMessage();
        matchMarketPreMessage.setStandardMatchInfoId(standardMatchInfo.getId());
        matchMarketPreMessage.setSportId(sportId);
        aoMatchPreIconStatus(linkId, standardMatchInfo.getId(), marketPreResultMessageList);
        matchMarketPreMessage.setMatchPreStatusRisk(marketPreResultMessageList.get(0).getMatchPreStatusRisk());
        matchMarketPreMessage.setMatchPreStatus(marketPreResultMessageList.get(0).getMatchPreStatus());

        matchMarketPreMessage.setMarketPreResultMessages(marketPreResultMessageList);

        Request<StandardMatchMarketPreMessage> request = new Request<>();
        request.setLinkId(linkId);
        request.setData(matchMarketPreMessage);
        request.setDataSourceTime(dataSourceTime);

        MessageBuilder<Request<StandardMatchMarketPreMessage>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        log.info("linkId=【{}】开始组装提前结算盘口消息并下发,topic=STANDARD_MARKET_PRE_RESULT,request:{}", linkId, JSON.toJSONString(request));
        //第一个参数表示topic=tag
        rocketMqTemplate.asyncSend("STANDARD_MARKET_PRE_RESULT:" + standardMatchInfo.getId(), builder.build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("linkId=【{}】,send successful", linkId);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("linkId=【{}】TOPIC={}，send fail; ", linkId, "STANDARD_MARKET_PRE_RESULT", throwable);
            }
        });
    }


    /**
     * 下发提前结算盘口信息
     *
     * @param linkId
     */
    public void sendStandardMatchPreResultNew(String linkId, StandardMatchInfo standardMatchInfo, List<StandardMatchMarketPreResultMessage> marketPreResultMessageList, Long dataSourceTime, String dataSourceCode,String requestType) {
        StandardMatchMarketPreMessage matchMarketPreMessage = new StandardMatchMarketPreMessage();
        matchMarketPreMessage.setModifyTime(dataSourceTime);
        matchMarketPreMessage.setStandardMatchInfoId(standardMatchInfo.getId());
        matchMarketPreMessage.setSportId(standardMatchInfo.getSportId());
        matchMarketPreMessage.setDataSourceCode(dataSourceCode);
        matchMarketPreMessage.setMatchPreStatus(null);
        matchMarketPreMessage.setMatchPreStatusRisk(null);
        matchMarketPreMessage.setMarketPreResultMessages(marketPreResultMessageList);
        matchMarketPreMessage.setRequestType(requestType);

        Request<StandardMatchMarketPreMessage> request = new Request<>();
        request.setLinkId(linkId);
        request.setData(matchMarketPreMessage);
        request.setDataSourceTime(dataSourceTime);

        if (pandaDataMqGatewayevent == 2){
            request.setDataType("STANDARD_MARKET_PRE_RESULT");
            request.setTag(standardMatchInfo.getId()+"");
            spareBaseProducer.syncSend(request);
        }else{
            MessageBuilder<Request<StandardMatchMarketPreMessage>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
            log.info("linkId=【{}】开始组装新提前结算盘口消息并下发,topic=STANDARD_MARKET_PRE_RESULT", linkId);
            //第一个参数表示topic=tag
            rocketMqTemplate.asyncSend("STANDARD_MARKET_PRE_RESULT:" + standardMatchInfo.getId(), builder.build(), new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    log.info("linkId=【{}】,send successful", linkId);
                }

                @Override
                public void onException(Throwable throwable) {
                    log.error("linkId=【{}】TOPIC={}，send fail; ", linkId, "STANDARD_MARKET_PRE_RESULT", throwable);
                }
            });
        }
    }
}
