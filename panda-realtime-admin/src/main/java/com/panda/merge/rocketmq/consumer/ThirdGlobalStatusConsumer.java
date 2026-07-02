package com.panda.merge.rocketmq.consumer;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.annotation.ConsumerSwitch;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdGlobalStatusDTO;
import com.panda.merge.rocketmq.processor.ThirdGlobalStatusProcessor;
import com.panda.merge.rocketmq.producer.DataCenterProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.panda.merge.constant.ConstantSystem.CONSUME_REALTIME_GROUP;
import static com.panda.merge.constant.ConstantSystem.THIRD_GLOBAL_STATUS_API;

/**
 * 数据商服务状态处理<br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/27 <br>
 * @see com.panda.merge.rocketmq.consumer <br>
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = THIRD_GLOBAL_STATUS_API,
        consumerGroup = CONSUME_REALTIME_GROUP + THIRD_GLOBAL_STATUS_API,
        consumeThreadMax = 128,
        consumeTimeout = 10000L
)
@DependsOn("realtimeAdminApplication")
public class ThirdGlobalStatusConsumer implements RocketMQListener<Request<ThirdGlobalStatusDTO>> {

    @Autowired
    ThirdGlobalStatusProcessor thirdGlobalStatusProcessor;
    @NacosValue(value = "${consumer.switch.realtime_nonrealtime:true}", autoRefreshed = true)
    private boolean realtimeSwitch;
    @Resource
    private DataCenterProducer<ThirdGlobalStatusDTO> dataCenterProducer;

    @Override
    public void onMessage(Request<ThirdGlobalStatusDTO> thirdGlobalStatusDTORequest) {
        if (!realtimeSwitch) {
            dataCenterProducer.send(thirdGlobalStatusDTORequest,THIRD_GLOBAL_STATUS_API);
            return;
        }
        thirdGlobalStatusProcessor.putGlobalStatus(thirdGlobalStatusDTORequest);
    }
}
