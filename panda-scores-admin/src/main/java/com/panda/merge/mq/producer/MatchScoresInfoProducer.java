package com.panda.merge.mq.producer;


import com.alibaba.fastjson.JSON;
import com.panda.merge.dto.Request;
import com.panda.merge.model.MatchScoresInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MatchScoresInfoProducer {
    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    /**
     * 结算阶段MQ下发
     * @param
     */
    public void updateScoresInfoByMq( MatchScoresInfo matchScoresInfo) {

        Request<MatchScoresInfo> reqMessage = new Request<>();
        reqMessage.setLinkId(matchScoresInfo.getThirdMatchId()+"_"+matchScoresInfo.getDataSourceType());
        reqMessage.setData(matchScoresInfo);
        MessageBuilder<Request<MatchScoresInfo>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, reqMessage.getLinkId());
        rocketMqTemplate.asyncSend("MATCH_SCORES_INFO_UPDATE:" + matchScoresInfo.getThirdMatchId(), builder.build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("::{}::,MATCH_SCORES_INFO_UPDATE send successful", reqMessage.getLinkId());
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::TOPIC={}，send fail; ", reqMessage, "MATCH_SCORES_INFO_UPDATE", throwable);
            }
        });
        log.info("::{}::开始组装结算比分异步更新,topic:MATCH_SCORES_INFO_UPDATE,request={}", reqMessage.getLinkId(), JSON.toJSONString(reqMessage));
    }

}
