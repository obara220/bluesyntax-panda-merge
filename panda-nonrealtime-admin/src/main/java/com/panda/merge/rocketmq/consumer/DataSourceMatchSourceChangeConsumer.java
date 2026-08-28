package com.panda.merge.rocketmq.consumer;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdMatchDataSourceDTO;
import com.panda.merge.dto.ThirdSportTeamDTO;
import com.panda.merge.rocketmq.processor.ThirdMatchInfoProcessor;
import com.panda.merge.rocketmq.processor.ThirdSportTeampPayerProcessor;
import com.panda.merge.rocketmq.producer.DataCenterProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * 数据商变更赛事来源时有实时更新<br>
 * @author    titan<br>
 * @since    2026年8月25日13:32:11 <br>
 */
@Slf4j
@Component
@Order
@RocketMQMessageListener(topic = DATASOURCE_MATCH_SOURCE_CHANGE,
        consumerGroup = CONSUME_NONREALTIME_GROUP + DATASOURCE_MATCH_SOURCE_CHANGE,
        consumeThreadMax = 128,
        consumeTimeout = 10000L)
@DependsOn("nonrealtimeAdminApplication")
public class DataSourceMatchSourceChangeConsumer implements RocketMQListener<Request<ThirdMatchDataSourceDTO>> {

    @Autowired
    ThirdMatchInfoProcessor thirdMatchInfoProcessor;
    @NacosValue(value = "${consumer.switch.realtime_nonrealtime:true}", autoRefreshed = true)
    private boolean realtimeSwitch;

    @Override
    public void onMessage(Request<ThirdMatchDataSourceDTO> request) {
        log.info("开始处理数据源变更赛事::{}",request.getLinkId());
//        if (!realtimeSwitch) {
//            dataCenterProducer.send(request,THIRD_SPORT_TEAM_API);
//            return;
//        }
//        ThirdMatchDataSourceDTO thirdMatchDataSourceDTO = request.getData().get(0);
        thirdMatchInfoProcessor.updateDataSourceMatchSource(request);
        log.info("结束处理数据源变更赛事::{}",request.getLinkId());
    }
}
