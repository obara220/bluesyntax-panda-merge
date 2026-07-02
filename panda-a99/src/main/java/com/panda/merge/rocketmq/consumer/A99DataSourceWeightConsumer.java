package com.panda.merge.rocketmq.consumer;

import com.panda.merge.dto.A99DataSourceWeightDTO;
import com.panda.merge.dto.Request;
import com.panda.merge.rocketmq.processor.A99DataSourceWeightProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * A99数据源权重
 */

@Slf4j
@Component
@RocketMQMessageListener(
        topic = A99_DATA_SOURCE_WEIGHT,
        consumerGroup = CONSUMER_PANDA_A99_GROUP + A99_DATA_SOURCE_WEIGHT,
        consumeThreadMax = 128,
        consumeTimeout = 10000L
)
public class A99DataSourceWeightConsumer implements RocketMQListener<A99DataSourceWeightDTO> {

    @Autowired
    private A99DataSourceWeightProcessor dataSourceWeightProcessor;

    @Override
    public void onMessage(A99DataSourceWeightDTO dto) {
        dataSourceWeightProcessor.execute(dto);
    }
}
