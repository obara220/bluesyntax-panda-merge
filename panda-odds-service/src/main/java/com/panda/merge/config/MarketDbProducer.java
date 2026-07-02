package com.panda.merge.config;

import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.MarketDBMessage;
import com.panda.merge.model.StandardSportMarket;
import com.panda.merge.model.StandardSportMarketOdds;
import com.panda.merge.model.ThirdSportMarket;
import com.panda.merge.model.ThirdSportMarketOdds;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.panda.merge.constant.ConstantSystem.*;

@Slf4j
@Component
public class MarketDbProducer {

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    /**
     * 发送三方盘口 批量新增数据
     *
     * @param linkId
     * @param thirdSportMarkets
     */
    public void sendThirdMarketInsertInfo(String linkId, List<ThirdSportMarket> thirdSportMarkets) {
        Request<MarketDBMessage> request = new Request<>();
        MarketDBMessage dbMessage = new MarketDBMessage();
        dbMessage.setThirdSportMarkets(thirdSportMarkets);
        request.setLinkId(linkId);
        request.setData(dbMessage);
        MessageBuilder<Request<MarketDBMessage>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        //第一个参数表示topic:tag
        rocketMqTemplate.asyncSend(THIRD_SPORT_MARKET_INSERT + ":", builder.build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("::{}::,send successful", linkId);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::TOPIC={}，send fail; ", linkId, "THIRD_SPORT_MARKET_INSERT", throwable);
            }
        });
    }


    /**
     * 发送三方盘口 批量修改数据
     *
     * @param linkId
     * @param thirdSportMarkets
     */
    public void sendThirdMarketUpdateInfo(String linkId, List<ThirdSportMarket> thirdSportMarkets) {
        Request<MarketDBMessage> request = new Request<>();
        MarketDBMessage dbMessage = new MarketDBMessage();
        dbMessage.setThirdSportMarkets(thirdSportMarkets);
        request.setLinkId(linkId);
        request.setData(dbMessage);
        MessageBuilder<Request<MarketDBMessage>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        //第一个参数表示topic:tag
        rocketMqTemplate.asyncSend(THIRD_SPORT_MARKET_UPDATE + ":"+linkId, builder.build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("::{}::,send successful", linkId);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::TOPIC={}，send fail; ", linkId, "THIRD_SPORT_MARKET_UPDATE", throwable);
            }
        });
    }

    /**
     * 发送三方盘口赔率 批量新增数据
     *
     * @param linkId
     * @param thirdSportMarketOdds
     */
    public void sendThirdMarketOddsInsertInfo(String linkId, List<ThirdSportMarketOdds> thirdSportMarketOdds) {
        Request<MarketDBMessage> request = new Request<>();
        MarketDBMessage dbMessage = new MarketDBMessage();
        dbMessage.setThirdSportMarketOdds(thirdSportMarketOdds);
        request.setLinkId(linkId);
        request.setData(dbMessage);
        MessageBuilder<Request<MarketDBMessage>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        //第一个参数表示topic:tag
        rocketMqTemplate.asyncSend(THIRD_SPORT_MARKET_ODDS_INSERT + ":", builder.build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("::{}::,send successful", linkId);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::TOPIC={}，send fail; ", linkId, "THIRD_SPORT_MARKET_ODDS_INSERT", throwable);
            }
        });
    }


    /**
     * 发送三方盘口 批量修改数据
     *
     * @param linkId
     * @param thirdSportMarketOdds
     */
    public void sendThirdMarketOddsUpdateInfo(String linkId, List<ThirdSportMarketOdds> thirdSportMarketOdds) {
        Request<MarketDBMessage> request = new Request<>();
        MarketDBMessage dbMessage = new MarketDBMessage();
        dbMessage.setThirdSportMarketOdds(thirdSportMarketOdds);
        request.setLinkId(linkId);
        request.setData(dbMessage);
        MessageBuilder<Request<MarketDBMessage>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        //第一个参数表示topic:tag
        rocketMqTemplate.asyncSend(THIRD_SPORT_MARKET_ODDS_UPDATE + ":", builder.build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("::{}::,send successful", linkId);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::TOPIC={}，send fail; ", linkId, "THIRD_SPORT_MARKET_ODDS_UPDATE", throwable);
            }
        });
    }


    /**
     * 发送标准盘口 批量新增数据
     *
     * @param linkId
     * @param standardSportMarkets
     */
    public void sendStandardMarketInsertInfo(String linkId, List<StandardSportMarket> standardSportMarkets) {
        Request<MarketDBMessage> request = new Request<>();
        MarketDBMessage dbMessage = new MarketDBMessage();
        dbMessage.setStandardSportMarkets(standardSportMarkets);
        request.setLinkId(linkId);
        request.setData(dbMessage);
        MessageBuilder<Request<MarketDBMessage>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        //第一个参数表示topic:tag
        rocketMqTemplate.asyncSend(STANDARD_SPORT_MARKET_INSERT + ":", builder.build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("::{}::,send successful", linkId);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::TOPIC={}，send fail; ", linkId, "STANDARD_SPORT_MARKET_INSERT", throwable);
            }
        });
    }


    /**
     * 发送三方盘口 批量修改数据
     *
     * @param linkId
     * @param standardSportMarkets
     */
    public void sendStandardMarketUpdateInfo(String linkId, List<StandardSportMarket> standardSportMarkets) {
        Request<MarketDBMessage> request = new Request<>();
        MarketDBMessage dbMessage = new MarketDBMessage();
        dbMessage.setStandardSportMarkets(standardSportMarkets);
        request.setLinkId(linkId);
        request.setData(dbMessage);
        MessageBuilder<Request<MarketDBMessage>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        //第一个参数表示topic:tag
        rocketMqTemplate.asyncSend(STANDARD_SPORT_MARKET_UPDATE + ":", builder.build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("::{}::,send successful", linkId);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::TOPIC={}，send fail; ", linkId, "STANDARD_SPORT_MARKET_UPDATE", throwable);
            }
        });
    }

    /**
     * 发送标准盘口赔率 批量新增数据
     *
     * @param linkId
     * @param standardSportMarketOdds
     */
    public void sendStandardMarketOddsInsertInfo(String linkId, List<StandardSportMarketOdds> standardSportMarketOdds) {
        Request<MarketDBMessage> request = new Request<>();
        MarketDBMessage dbMessage = new MarketDBMessage();
        dbMessage.setStandardSportMarketOdds(standardSportMarketOdds);
        request.setLinkId(linkId);
        request.setData(dbMessage);
        MessageBuilder<Request<MarketDBMessage>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        //第一个参数表示topic:tag
        rocketMqTemplate.asyncSend(STANDARD_SPORT_MARKET_ODDS_INSERT + ":", builder.build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("::{}::,send successful", linkId);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::TOPIC={}，send fail; ", linkId, "STANDARD_SPORT_MARKET_ODDS_INSERT", throwable);
            }
        });
    }


    /**
     * 发送标准盘口 批量修改数据
     *
     * @param standardSportMarketOdds
     */
    public void sendStandardMarketOddsUpdateInfo(String linkId, List<StandardSportMarketOdds> standardSportMarketOdds) {
        Request<MarketDBMessage> request = new Request<>();
        MarketDBMessage dbMessage = new MarketDBMessage();
        dbMessage.setStandardSportMarketOdds(standardSportMarketOdds);
        request.setData(dbMessage);
        request.setLinkId(linkId);
        MessageBuilder<Request<MarketDBMessage>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, "");
        //第一个参数表示topic:tag
        rocketMqTemplate.asyncSend(STANDARD_SPORT_MARKET_ODDS_UPDATE + ":", builder.build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("send successful");
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::TOPIC={}，send fail; ", "STANDARD_SPORT_MARKET_ODDS_UPDATE", throwable);
            }
        });
    }
}
