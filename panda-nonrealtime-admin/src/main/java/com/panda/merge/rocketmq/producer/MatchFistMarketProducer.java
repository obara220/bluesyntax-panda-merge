package com.panda.merge.rocketmq.producer;

import com.alibaba.fastjson.JSON;
import com.panda.aocollect.dto.Request;
import com.panda.aocollect.model.MatchOddsHistory;
import com.panda.aocollect.model.MatchOddsHistoryBasketball;
import com.panda.aocollect.model.MatchStoppagetimeHistory;
import com.panda.merge.dto.ThirdMarketDTO;
import com.panda.merge.dto.message.StandardMatchMarketAoMessage;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.ThirdMatchInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class MatchFistMarketProducer {
    @Autowired
    private RocketMQTemplate rocketMqTemplate;
    /**
     * 下发足球球头初盘数据
     *
     * @param linkId
     * @param matchOddsHistories
     */
    public void sendFistMarketFootBall(String linkId, Long standardMatchInfoId, Long sportId ,List<MatchOddsHistory> matchOddsHistories) {
        Request<List<MatchOddsHistory>> request = new Request<>();
        request.setLinkId(linkId);
        request.setSportId(sportId);
        request.setData(matchOddsHistories);
        MessageBuilder<Request<List<MatchOddsHistory>>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, standardMatchInfoId);
        log.info("::{}::开始组装通知下游足球球头初盘数据,topic:MARKET_ODDS_HISTORY", linkId);
        //第一个参数表示topic:tag
        rocketMqTemplate.asyncSend("MARKET_ODDS_HISTORY:" + standardMatchInfoId, builder.build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("::{}::,send successful", linkId);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::TOPIC={}，send fail; ", linkId, "MARKET_ODDS_HISTORY", throwable);
            }
        });
    }
    /**
     * 下发篮球球头初盘数据
     *
     * @param linkId
     * @param matchOddsHistories
     */
    public void sendFistMarketBasketball(String linkId, Long standardMatchInfoId, Long sportId ,List<MatchOddsHistoryBasketball> matchOddsHistories) {
        Request<List<MatchOddsHistoryBasketball>> request = new Request<>();
        request.setLinkId(linkId);
        request.setSportId(sportId);
        request.setData(matchOddsHistories);
        MessageBuilder<Request<List<MatchOddsHistoryBasketball>>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, standardMatchInfoId);
        log.info("::{}::开始组装通知下游篮球球头初盘数据,topic:MARKET_ODDS_HISTORY,request:{}", linkId);
        //第一个参数表示topic:tag
        rocketMqTemplate.asyncSend("MARKET_ODDS_HISTORY:" + standardMatchInfoId, builder.build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("::{}::,send successful", linkId);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::TOPIC={}，send fail; ", linkId, "MARKET_ODDS_HISTORY", throwable);
            }
        });
    }

    /**
     * 下发补时
     *
     * @param linkId
     * @param matchOddsHistories
     */
    public void sendApplyInjTime(String linkId, MatchStoppagetimeHistory matchOddsHistories) {
        Request<MatchStoppagetimeHistory> request = new Request<>();
        request.setLinkId(linkId);
        request.setData(matchOddsHistories);
        MessageBuilder<Request<MatchStoppagetimeHistory>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, matchOddsHistories.getMatchId());
        log.info("::{}::开始组装通知下游下发补时数据,topic:MATCH_STOPPAGETIME_HISTORY,request:{}", linkId, JSON.toJSONString(request));
        //第一个参数表示topic:tag
        rocketMqTemplate.asyncSend("MATCH_STOPPAGETIME_HISTORY:" + matchOddsHistories.getMatchId(), builder.build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("::{}::,send successful", linkId);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::TOPIC={}，send fail; ", linkId, "MATCH_STOPPAGETIME_HISTORY", throwable);
            }
        });
    }


    /**
     * 下发A0球头三方赔率
     *
     * @param linkId
     * @param thirdMatchInfo
     * @param standardMatchInfo
     * @param marketBallHeadMap
     * @param dataSourceCode
     * @param dataSourceTime
     * @return
     */
    public void sendThirdBallHeadMarketAoAsync(String linkId, ThirdMatchInfo thirdMatchInfo, StandardMatchInfo standardMatchInfo, Map<Long, ThirdMarketDTO> marketBallHeadMap, String dataSourceCode, Long dataSourceTime) {
        StandardMatchMarketAoMessage standardMatchMarketMessage = new StandardMatchMarketAoMessage();
        standardMatchMarketMessage.setStandardMatchInfoId(standardMatchInfo.getId());
        standardMatchMarketMessage.setThirdMatchInfoId(thirdMatchInfo.getThirdMatchSourceId());
        standardMatchMarketMessage.setDataSourceCode(dataSourceCode);
        standardMatchMarketMessage.setThirdMarketBallHeadMap(marketBallHeadMap);
        standardMatchMarketMessage.setSportId(standardMatchInfo.getSportId());

        com.panda.merge.dto.Request<StandardMatchMarketAoMessage> request = new com.panda.merge.dto.Request<>();
        request.setData(standardMatchMarketMessage);
        request.setLinkId(linkId);
        request.setDataSourceTime(dataSourceTime);
        MessageBuilder<com.panda.merge.dto.Request<StandardMatchMarketAoMessage>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        log.info("::{}::开始组装三方AO球头赔率消息并下发,topic:THIRD_MARKET_BALL_HEAD,request:{}", linkId, JSON.toJSONString(request));
        //第一个参数表示topic:tag
        rocketMqTemplate.asyncSend("THIRD_MARKET_BALL_HEAD:" + standardMatchMarketMessage.getStandardMatchInfoId(), builder.build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("::{}::,send successful", linkId);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::TOPIC={}，send fail; ", linkId, "THIRD_MARKET_BALL_HEAD", throwable);
            }
        });
    }

    /**
     * 下发A0篮球 三方球头
     *
     * @param linkId
     * @param thirdMatchInfo
     * @param standardMatchInfo
     * @param dataSourceTime
     * @return
     */
    public void sendBasketballThirdBallHeadMarketAoAsync(String linkId, ThirdMatchInfo thirdMatchInfo, StandardMatchInfo standardMatchInfo, Map<Long, List<ThirdMarketDTO>> marketBallHeadMap, String dataSourceCode, Long dataSourceTime) {
        StandardMatchMarketAoMessage standardMatchMarketMessage = new StandardMatchMarketAoMessage();
        standardMatchMarketMessage.setStandardMatchInfoId(standardMatchInfo.getId());
        standardMatchMarketMessage.setThirdMatchInfoId(thirdMatchInfo.getThirdMatchSourceId());
        standardMatchMarketMessage.setDataSourceCode(dataSourceCode);
        standardMatchMarketMessage.setThirdBasketballMarketBallHeadMap(marketBallHeadMap);
        standardMatchMarketMessage.setSportId(standardMatchInfo.getSportId());

        com.panda.merge.dto.Request<StandardMatchMarketAoMessage> request = new com.panda.merge.dto.Request<>();
        request.setData(standardMatchMarketMessage);
        request.setLinkId(linkId);
        request.setDataSourceTime(dataSourceTime);
        MessageBuilder<com.panda.merge.dto.Request<StandardMatchMarketAoMessage>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        log.info("::{}::开始组装三方AO球头赔率消息并下发,topic:THIRD_MARKET_BASKETBALL_HEAD,request:{}", linkId, JSON.toJSONString(request));
        //第一个参数表示topic:tag
        rocketMqTemplate.asyncSend("THIRD_MARKET_BASKETBALL_HEAD:" + standardMatchMarketMessage.getStandardMatchInfoId(), builder.build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("::{}::,send successful", linkId);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::TOPIC={}，send fail; ", linkId, "THIRD_MARKET_BALL_HEAD", throwable);
            }
        });
    }
}
