package com.panda.merge.mq;

import com.panda.merge.dto.Request;
import com.panda.merge.dto.settle.AutoSettleDataSourceDto;
import com.panda.merge.dto.settle.MatchSettleRollBackDto;
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
@RocketMQMessageListener(topic = "MATCH_SETTLE_ROLL_BACK_STATUS_PUSH", consumerGroup = "ws-group-MatchSettleRollBackStatusConsumer",consumeThreadMax = 10,
        consumeTimeout = 10000L,
        messageModel = MessageModel.BROADCASTING)
@DependsOn("mergeWebSocketApplication")
public class MatchSettleRollBackConsumer implements RocketMQListener<Request<MatchSettleRollBackDto>> {

    @Autowired
    PDSubcribe pdSubcribe;

    @Override
    public void onMessage(Request<MatchSettleRollBackDto> request) {
//        log.info("MATCH_SETTLE_ROLL_BACK_STATUS_PUSH:{}",request.getData());
        pdSubcribe.sendMatchSettleRollBackStatus(request.getData());
    }
}
