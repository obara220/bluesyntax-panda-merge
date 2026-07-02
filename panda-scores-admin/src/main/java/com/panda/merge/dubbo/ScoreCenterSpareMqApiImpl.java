package com.panda.merge.dubbo;

import com.panda.merge.api.ScoreCenterSpareMqApi;
import com.panda.merge.config.mq.SpareRocketmqConsumerConfig;
import com.panda.merge.mq.spare.SpareLiveDataScoresNewConsumer;
import com.panda.merge.mq.spare.SpareStandardMatchScoresConsumer;
import com.panda.merge.mq.spare.SpareUOFScoresConsumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.panda.merge.constant.ConstantSystem.ONE;

/**
 * @author warren
 * @since 2025/06/14 02:41:54
 */
@Service
@DubboService
@Slf4j
public class ScoreCenterSpareMqApiImpl implements ScoreCenterSpareMqApi {
    @Autowired
    private SpareRocketmqConsumerConfig spareRocketmqConsumerConfig;

    @Override
    public void slaveRocketMqStopResume(Integer isStop) {
        DefaultMQPushConsumer consumer = spareRocketmqConsumerConfig.getConsumer();
        if (consumer != null) {
            if(ONE.equals(isStop)){
                consumer.suspend();
                log.info("【slaveRocketMqStopResume 手动触发MQ暂停消费（部分topic）:{}】 已暂停消费!", System.currentTimeMillis());
            }else{
                consumer.resume();
                log.info("【slaveRocketMqStopResume 手动触发MQ暂停消费（部分topic）:{}】 已恢复消费!", System.currentTimeMillis());
            }
        }else{
            log.info("【slaveRocketMqStopResume 手动触发MQ暂停消费（部分topic）:{}】 consumer为空,无法处理!", System.currentTimeMillis());
        }
    }
}
