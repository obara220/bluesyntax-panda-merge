package com.panda.merge.mq.producer;


import com.alibaba.fastjson.JSON;
import com.panda.merge.constant.CommonConstant;
import com.panda.merge.dto.Request;
import com.panda.merge.model.MatchScoresInfo;
import com.panda.merge.model.MatchScoresSourceType;
import lombok.extern.slf4j.Slf4j;
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
public class MatchScoreSourceTypeProducer {
//    @Autowired
//    private RocketMQTemplate rocketMqTemplate;
    @Resource(name = "secondTemplate")
    private RocketMQTemplate secondTemplate;

    /**
     * 结算阶段MQ下发
     * @param
     */
    public void updateSourceTypeByMq( MatchScoresSourceType sourceType) {

        Request<List<MatchScoresSourceType>> reqMessage = new Request<>();
        reqMessage.setLinkId(sourceType.getId().toString());
        reqMessage.setData(Collections.singletonList(sourceType));
        MessageBuilder<Request<List<MatchScoresSourceType>>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, reqMessage.getLinkId())
                .setHeader(RocketMQHeaders.TAGS, CommonConstant.SCORE_CENTER_MATCH_SCORES_SOURCE_TYPE_TABLE)
                .setHeader(CommonConstant.TAG, CommonConstant.SCORE_CENTER_MATCH_SCORES_SOURCE_TYPE_TABLE)
                .setHeader(CommonConstant.IS_INSERT, false);
        secondTemplate.syncSendOrderly(CommonConstant.SCORE_CENTER_SLAVE_DB_TOPIC, builder.build(), CommonConstant.SCORE_CENTER_MATCH_SCORES_SOURCE_TYPE_TABLE);
//        rocketMqTemplate.send("MATCH_SCORES_SOURCE_TYPE_UPDATE:" + sourceType.getThirdMatchId(), builder.build());
        log.info("::{}::开始组装结算比分异步更新,topic:MATCH_SCORES_SOURCE_TYPE_UPDATE,request={}", reqMessage.getLinkId(), JSON.toJSONString(reqMessage));
    }

}
