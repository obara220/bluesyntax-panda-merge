package com.panda.merge.mq.producer;


import com.alibaba.fastjson.JSON;
import com.panda.merge.constant.CommonConstant;
import com.panda.merge.dto.Request;
import com.panda.merge.model.MatchScoresInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class MatchScoresInfoProducer {
//    @Autowired
//    private RocketMQTemplate rocketMqTemplate;
    @Resource(name = "secondTemplate")
    private RocketMQTemplate secondTemplate;

    /**
     * 结算阶段MQ下发
     * @param
     */
    public void updateScoresInfoByMq( MatchScoresInfo matchScoresInfo) {

        Request<List<MatchScoresInfo>> reqMessage = new Request<>();
        reqMessage.setLinkId(matchScoresInfo.getThirdMatchId()+"_"+matchScoresInfo.getDataSourceType());
        reqMessage.setData(Collections.singletonList(matchScoresInfo));
        MessageBuilder<Request<List<MatchScoresInfo>>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, reqMessage.getLinkId())
                .setHeader(RocketMQHeaders.TAGS, CommonConstant.SCORE_CENTER_MATCH_SCORES_INFO_TABLE)
                .setHeader(CommonConstant.TAG, CommonConstant.SCORE_CENTER_MATCH_SCORES_INFO_TABLE)
                .setHeader(CommonConstant.IS_INSERT, false);
        secondTemplate.syncSendOrderly(CommonConstant.SCORE_CENTER_SLAVE_DB_TOPIC, builder.build(), CommonConstant.SCORE_CENTER_MATCH_SCORES_INFO_TABLE);
//        rocketMqTemplate.asyncSend("MATCH_SCORES_INFO_UPDATE:" + matchScoresInfo.getThirdMatchId(), builder.build(), new SendCallback() {
//            @Override
//            public void onSuccess(SendResult sendResult) {
//                log.info("::{}::,MATCH_SCORES_INFO_UPDATE send successful", reqMessage.getLinkId());
//            }
//
//            @Override
//            public void onException(Throwable throwable) {
//                log.error("::{}::TOPIC={}，send fail; ", reqMessage, "MATCH_SCORES_INFO_UPDATE", throwable);
//            }
//        });
        log.info("::{}::开始组装结算比分异步更新,topic:scores_center_slave_db_storage,request={}", reqMessage.getLinkId(), JSON.toJSONString(reqMessage));
    }

}
