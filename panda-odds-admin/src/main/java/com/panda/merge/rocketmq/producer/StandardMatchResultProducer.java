package com.panda.merge.rocketmq.producer;

import com.alibaba.fastjson.JSON;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdMarketResultDTO;
import com.panda.merge.dto.message.StandardMarketOddsResultMessage;
import com.panda.merge.dto.message.StandardMarketResultMessage;
import com.panda.merge.model.StandardSportMarket;
import com.panda.merge.model.StandardSportMarketOdds;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.rocketmq.RocketMQDelegate;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/30 <br>
 * @see com.panda.merge.rocketmq.producer <br>
 */
@Slf4j
@Component
public class StandardMatchResultProducer {

    @Autowired
    private RocketMQDelegate rocketMqTemplate;

    /**
     * 推送赛事赛果给下游
     *  @param linkId
     * @param matchType
     * @param standardSettledList
     * @param standardSportMarket
     * @param thirdMatchInfo
     */
    public void pushStandardMatchResultInfo(String linkId, Integer matchType, List<StandardSportMarketOdds> standardSettledList, StandardSportMarket standardSportMarket, ThirdMatchInfo thirdMatchInfo, ThirdMarketResultDTO thirdMarketResultDTO,Long matchId) {
        log.info("::{}::start send STANDARD_MATCH_RESULT mq", linkId);
        Request<StandardMarketResultMessage> request = getStandardMarketResultMsgReq(linkId, matchType, standardSettledList, standardSportMarket, thirdMatchInfo,thirdMarketResultDTO,matchId);
        MessageBuilder<Request<StandardMarketResultMessage>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        //第一个参数表示topic:tag
        rocketMqTemplate.send("STANDARD_MATCH_RESULT:" + request.getData().getStandardMarketId(), builder.build(),
                              standardSportMarket.getStandardMatchInfoId());
        log.info("::{}::开始组装赛果并下发,topic:STANDARD_MATCH_RESULT,标准盘口ID：{},request:{}", linkId, request.getData().getStandardMarketId(), JSON.toJSONString(request));
    }

    /**
     * 生成下发至下游的消息体
     *
     * @param linkId
     * @param matchType
     * @param standardSettledList
     * @param standardSportMarket
     * @param thirdMatchInfo
     * @return
     */
    private Request<StandardMarketResultMessage> getStandardMarketResultMsgReq(String linkId, Integer matchType, List<StandardSportMarketOdds> standardSettledList, StandardSportMarket standardSportMarket, ThirdMatchInfo thirdMatchInfo, ThirdMarketResultDTO thirdMarketResultDTO,Long matchId) {
        Request<StandardMarketResultMessage> request = new Request<>();
        request.setLinkId(linkId);
        List<StandardMarketOddsResultMessage> standardMarketOddsResultMessageList = new ArrayList<>();
        standardSettledList.forEach(standardSportMarketOdds -> {
            StandardMarketOddsResultMessage standardMarketOddsResultMessage = new StandardMarketOddsResultMessage();
            standardMarketOddsResultMessage.setId(standardSportMarketOdds.getRelationMarketOddsId());
            standardMarketOddsResultMessage.setBetSettlementCertainty(standardSportMarketOdds.getBetSettlementCertainty());
            standardMarketOddsResultMessage.setSettlementResult(standardSportMarketOdds.getSettlementResult());
            standardMarketOddsResultMessage.setMarketOddsId(standardSportMarketOdds.getThirdOddsFieldSourceId());
            standardMarketOddsResultMessageList.add(standardMarketOddsResultMessage);
        });
        StandardMarketResultMessage standardMarketResultMessage = new StandardMarketResultMessage();
        standardMarketResultMessage.setDataSourceCode(thirdMatchInfo.getDataSourceCode());
        standardMarketResultMessage.setSportId(thirdMatchInfo.getSportId());
        standardMarketResultMessage.setStandardMarketId(standardSportMarket.getRelationMarketId() + "");
        standardMarketResultMessage.setStandardMatchId(matchId+"");
        standardMarketResultMessage.setThirdMatchId(thirdMatchInfo.getId() + "");
        standardMarketResultMessage.setThirdMarketId(standardSportMarket.getThirdMarketSourceId());
        standardMarketResultMessage.setMarketOddsResultList(standardMarketOddsResultMessageList);
        standardMarketResultMessage.setReasonId(thirdMarketResultDTO.getReasonId());
        standardMarketResultMessage.setMatchType(matchType);
        request.setData(standardMarketResultMessage);
        return request;
    }

}
