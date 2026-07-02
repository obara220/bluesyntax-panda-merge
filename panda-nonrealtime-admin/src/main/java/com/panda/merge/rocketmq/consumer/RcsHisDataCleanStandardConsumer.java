package com.panda.merge.rocketmq.consumer;

import com.panda.merge.dto.Request;
import com.panda.merge.rocketmq.processor.RcsHisDataCleanProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.panda.merge.constant.ConstantSystem.CONSUME_NONREALTIME_GROUP;
import static com.panda.merge.constant.ConstantSystem.STANDARD_MATCH_OVER_DAY_CLEAN;


@Slf4j
@Component
@RocketMQMessageListener(
    topic = STANDARD_MATCH_OVER_DAY_CLEAN,
        consumerGroup = CONSUME_NONREALTIME_GROUP + STANDARD_MATCH_OVER_DAY_CLEAN,
        consumeThreadMax = 128,
        consumeTimeout = 10000L
)
@DependsOn("nonrealtimeAdminApplication")
public class RcsHisDataCleanStandardConsumer implements RocketMQListener<Request<List<Long>>> {
    @Autowired
    RcsHisDataCleanProcessor rcsHisDataCleanProcessor;
    @Override
    public void onMessage(Request<List<Long>> listRequest) {
        rcsHisDataCleanProcessor.cleanStandardData(listRequest);
    }
}
