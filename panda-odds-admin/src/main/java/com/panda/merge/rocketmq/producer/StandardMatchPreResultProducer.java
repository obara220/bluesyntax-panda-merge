package com.panda.merge.rocketmq.producer;

import cn.hutool.core.util.ObjectUtil;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.StandardMatchMarketPreMessage;
import com.panda.merge.dto.message.StandardMatchMarketPreResultMessage;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.rocketmq.RocketMQDelegate;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 下发提前结算盘口信息
 */
@Slf4j
@Component
public class StandardMatchPreResultProducer extends BaseProcessor {

    @Autowired
    private RocketMQDelegate rocketMqTemplate;

    public static List<String> EVENT_CODE = Arrays.asList("possible_video_assistant_referee", "video_assistant_referee", "possible_var", "var_reason", "var_reviewing");

    /**
     * 下发提前结算盘口信息
     *
     * @param linkId
     */
    public void sendStandardMatchPreResult(String linkId, StandardMatchInfo standardMatchInfo, Long sportId,
                                           List<StandardMatchMarketPreResultMessage> marketPreResultMessageList, Integer matchPreStatus, Long dataSourceTime) {
        StandardMatchMarketPreMessage matchMarketPreMessage = new StandardMatchMarketPreMessage();
        matchMarketPreMessage.setStandardMatchInfoId(standardMatchInfo.getId());
        matchMarketPreMessage.setSportId(sportId);
        aoMatchPreIconStatus(linkId, standardMatchInfo.getId(), marketPreResultMessageList);
        matchMarketPreMessage.setMatchPreStatusRisk(marketPreResultMessageList.get(0).getMatchPreStatusRisk());
        matchMarketPreMessage.setMatchPreStatus(marketPreResultMessageList.get(0).getMatchPreStatus());
        matchMarketPreMessage.setMarketPreResultMessages(marketPreResultMessageList);

        String eventKey = Constant.REDIS_KEY.STANDARD_EVENT_VR_CODE + standardMatchInfo.getId();
        Object o = redisService.get(eventKey);
        log.info("::{}::提前结算VR事件:{}", linkId, o);
        if (ObjectUtil.isNotEmpty(o)) {
            String eventCode = (String) o;
            if (EVENT_CODE.contains(eventCode)) {
                marketPreResultMessageList.forEach(m -> {
                    m.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                });
                matchMarketPreMessage.setMarketPreResultMessages(marketPreResultMessageList);
            }
        }
        Request<StandardMatchMarketPreMessage> request = new Request<>();
        request.setLinkId(linkId);
        request.setData(matchMarketPreMessage);
        request.setDataSourceTime(dataSourceTime);

        MessageBuilder<Request<StandardMatchMarketPreMessage>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        log.info("::{}::开始组装提前结算盘口消息并下发,topic:STANDARD_MARKET_PRE_RESULT,事件:{}", linkId, o);
        //第一个参数表示topic:tag
        rocketMqTemplate.asyncSend("STANDARD_MARKET_PRE_RESULT:" + standardMatchInfo.getId(), builder.build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("::{}::,send successful", linkId);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::TOPIC={}，send fail; ", linkId, "STANDARD_MARKET_PRE_RESULT", throwable);
            }
        }, standardMatchInfo.getId());
    }
}
