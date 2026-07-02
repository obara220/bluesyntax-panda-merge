package com.panda.merge.mq.producer;


import com.alibaba.fastjson.JSON;
import com.panda.merge.dto.Request;
import com.panda.merge.model.MatchScoresInfo;
import com.panda.merge.model.MatchScoresSourceType;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MatchScoreSourceTypeProducer {
    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    /**
     * 结算阶段MQ下发
     * @param
     */
    public void updateSourceTypeByMq( MatchScoresSourceType sourceType) {

        Request<MatchScoresSourceType> reqMessage = new Request<>();
        reqMessage.setLinkId(sourceType.getId().toString());
        reqMessage.setData(sourceType);
        MessageBuilder<Request<MatchScoresSourceType>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, reqMessage.getLinkId());
        rocketMqTemplate.send("MATCH_SCORES_SOURCE_TYPE_UPDATE:" + sourceType.getThirdMatchId(), builder.build());
        log.info("::{}::开始组装结算比分异步更新,topic:MATCH_SCORES_SOURCE_TYPE_UPDATE,request={}", reqMessage.getLinkId(), JSON.toJSONString(reqMessage));
    }

}
