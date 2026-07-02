package com.panda.merge.rocketmq.consumer;

import com.panda.merge.dto.Request;
import com.panda.merge.rocketmq.processor.RcsHisDataCleanProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.panda.merge.constant.ConstantSystem.CONSUME_NONREALTIME_GROUP;
import static com.panda.merge.constant.ConstantSystem.THIRD_MATCH_OVER_DAY_CLEAN;

@Slf4j
@Component
@RocketMQMessageListener(
        topic = THIRD_MATCH_OVER_DAY_CLEAN,
        consumerGroup = CONSUME_NONREALTIME_GROUP + THIRD_MATCH_OVER_DAY_CLEAN,
        consumeThreadMax = 128,
        consumeTimeout = 10000L
)
@DependsOn("nonrealtimeAdminApplication")
public class RcsHisDataCleanThirdConsumer  implements RocketMQListener<Request<Map<Long, String>>> {
    @Autowired
    RcsHisDataCleanProcessor rcsHisDataCleanProcessor;
    @Override
    public void onMessage(Request<Map<Long, String>> mapRequest) {
        rcsHisDataCleanProcessor.cleanThirdData(mapRequest);
    }
}
