package com.panda.merge.mq;


import com.panda.merge.dto.Request;
import com.panda.merge.handler.PDSubcribe;
import com.panda.merge.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


@Slf4j
@Component
@Service
@RocketMQMessageListener(topic = "PD_FOOTBALL_SCORE", consumerGroup = "scores-group-PDScoresConsumer",consumeThreadMax = 2,
        consumeTimeout = 10000L,
        messageModel = MessageModel.CLUSTERING)
@DependsOn("mergeWebSocketApplication")
public class PDScoresConsumer implements RocketMQListener<Request<String>> {

    @Autowired
    PDSubcribe pdSubcribe;


    @Override
    public void onMessage(Request<String> request) {
        log.info("PD_FOOTBALL_SCORE_GET:{}",request.getData());
        pdSubcribe.sendPdScore(request.getData());
        log.info("PD_FOOTBALL_SCORE_GET_END:");
    }

}
