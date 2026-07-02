package com.panda.merge.mq.producer;

import com.alibaba.fastjson.JSON;
import com.panda.merge.dto.Request;
import com.panda.merge.model.MatchSettleResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * 结算比分MQ下发
 */
@Service
@Slf4j
public class ScoreSettleResultProducer {

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    /**
     * 结算阶段MQ下发
     * @param settleResult
     */
    public void sendResult( MatchSettleResult settleResult) {

        Request<MatchSettleResult> reqMessage = new Request<>();
        reqMessage.setLinkId(settleResult.getLinkId());
        reqMessage.setData(settleResult);
        MessageBuilder<Request<MatchSettleResult>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, reqMessage.getLinkId());
        rocketMqTemplate.send("SCORE_SETTLE_RESULT:" + settleResult.getStandardMatchId(), builder.build());
        log.info("::{}::开始组装结算比分同步业务下发,topic:SCORE_SETTLE_RESULT,request={}", reqMessage.getLinkId(), JSON.toJSONString(reqMessage));
    }


}
