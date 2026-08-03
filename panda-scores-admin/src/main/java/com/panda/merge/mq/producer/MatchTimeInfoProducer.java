package com.panda.merge.mq.producer;


import com.alibaba.fastjson.JSON;
import com.panda.merge.constant.CommonConstant;
import com.panda.merge.dto.Request;
import com.panda.merge.model.MatchTimeInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class MatchTimeInfoProducer {
    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    @Resource(name = "secondTemplate")
    private RocketMQTemplate secondTemplate;

    /**
     * 赛事时间信息异步修改通知
     * @param
     */
    public void updateMatchTimesInfoByMq(MatchTimeInfo matchTimeInfo) {

        Request<List<MatchTimeInfo>> reqMessage = new Request<>();
        reqMessage.setLinkId(matchTimeInfo.getThirdMatchId()+"");
        reqMessage.setData(Collections.singletonList(matchTimeInfo));
        MessageBuilder<Request<List<MatchTimeInfo>>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, reqMessage.getLinkId())
                .setHeader(RocketMQHeaders.TAGS, CommonConstant.SCORE_CENTER_MATCH_TIME_INFO_TABLE)
                .setHeader(CommonConstant.TAG, CommonConstant.SCORE_CENTER_MATCH_TIME_INFO_TABLE)
                .setHeader(CommonConstant.IS_INSERT, false);
        secondTemplate.syncSendOrderly(CommonConstant.SCORE_CENTER_SLAVE_DB_TOPIC, builder.build(), CommonConstant.SCORE_CENTER_MATCH_TIME_INFO_TABLE);
        rocketMqTemplate.send("MATCH_TIME_INFO_UPDATE:" + matchTimeInfo.getThirdMatchId(), builder.build());
//        log.info("::{}::开始组装赛事时间信息异步修改通知,topic:MATCH_TIME_INFO_UPDATE,request={}", reqMessage.getLinkId(), JSON.toJSONString(reqMessage));


    }

}
