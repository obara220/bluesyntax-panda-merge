package com.panda.merge.rocketmq.consumer;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.annotation.ConsumerSwitch;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.thirdmatch.ThirdMatchTeamSkillStatisticsDTO;
import com.panda.merge.rocketmq.processor.ThirdMatchTeamSkillStatisticsProcessor;
import com.panda.merge.rocketmq.producer.DataCenterProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import static com.panda.merge.constant.ConstantSystem.CONSUME_REALTIME_GROUP;
import static com.panda.merge.constant.ConstantSystem.THIRD_MATCH_TEAM_SKILL_STATISTICS_API;

/**
 * 赛事球队技术统计
 * @author     tell
 * @since      2025年6月10日9:42:31
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = THIRD_MATCH_TEAM_SKILL_STATISTICS_API,
        consumerGroup = CONSUME_REALTIME_GROUP + THIRD_MATCH_TEAM_SKILL_STATISTICS_API,
        consumeThreadMax = 128,
        consumeTimeout = 10000L)
@DependsOn("realtimeAdminApplication")
public class ThirdMatchTeamSkillStatisticsConsumer implements RocketMQListener<Request<ThirdMatchTeamSkillStatisticsDTO>> {

    @Autowired
    private ThirdMatchTeamSkillStatisticsProcessor thisProcessor;
    @NacosValue(value = "${consumer.switch.realtime_nonrealtime:true}", autoRefreshed = true)
    private boolean realtimeSwitch;
    @Resource
    private DataCenterProducer<ThirdMatchTeamSkillStatisticsDTO> dataCenterProducer;

    @Override
    public void onMessage(Request<ThirdMatchTeamSkillStatisticsDTO> request) {
        if (!realtimeSwitch) {
            dataCenterProducer.send(request,THIRD_MATCH_TEAM_SKILL_STATISTICS_API);
            return;
        }
        thisProcessor.processData(request);
    }
}
