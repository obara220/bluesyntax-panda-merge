package com.panda.merge.mq.producer;


import com.alibaba.fastjson.JSON;
import com.panda.merge.dto.Request;
import com.panda.merge.model.MatchScoresInfo;
import com.panda.merge.model.StandardMatchScores;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StandardMatchScoresProducer {
    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    /**
     * 结算阶段MQ下发
     * @param
     */
    public void updateStandardMatchScoresByMq( StandardMatchScores standardMatchScores) {

        Request<StandardMatchScores> reqMessage = new Request<>();
        reqMessage.setLinkId(standardMatchScores.getMatchId()+"");
        reqMessage.setData(standardMatchScores);
        MessageBuilder<Request<StandardMatchScores>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, reqMessage.getLinkId());
        rocketMqTemplate.send("STANDARD_MATCH_SCORES_UPDATE:" + standardMatchScores.getMatchId(), builder.build());
        log.info("::{}::开始组装标准比分异步更新,topic:STANDARD_MATCH_SCORES_UPDATE,request={}", reqMessage.getLinkId(), JSON.toJSONString(reqMessage));
    }

}
