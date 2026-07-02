package com.panda.merge.mq;


import com.panda.merge.dto.Request;
import com.panda.merge.handler.PDSubcribe;
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
@RocketMQMessageListener(topic = "SCORE_CENTER_MATCH_SCORES", consumerGroup = "scores-group-SCORE_CENTER_MATCH_SCORES",consumeThreadMax = 2,
        consumeTimeout = 10000L,
        messageModel = MessageModel.BROADCASTING)
@DependsOn("mergeWebSocketApplication")
public class StandardMatchScoresConsumer implements RocketMQListener<Request<String>> {

    @Autowired
    PDSubcribe pdSubcribe;

    @Override
    public void onMessage(Request<String> request) {
        log.info("SCORE_CENTER_MATCH_SCORES消费数据:{}",request);
        pdSubcribe.sendMatchScore(request.getData());
    }

}
