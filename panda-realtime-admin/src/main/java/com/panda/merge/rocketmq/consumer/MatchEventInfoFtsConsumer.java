package com.panda.merge.rocketmq.consumer;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.annotation.ConsumerSwitch;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.dto.Request;
import com.panda.merge.model.MatchEventInfo;
import com.panda.merge.rocketmq.producer.DataCenterProducer;
import com.panda.merge.rocketmq.producer.MatchEventInfoProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.List;

import static com.panda.merge.constant.ConstantSystem.CONSUME_REALTIME_GROUP;
import static com.panda.merge.constant.ConstantSystem.MATCH_EVENT_INFO_FTS;

/**
 * FTS赛事事件逻辑处理
 *
 * @author tell
 * @since 2025年02月17日11:04:05
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = MATCH_EVENT_INFO_FTS,
        consumerGroup = CONSUME_REALTIME_GROUP + MATCH_EVENT_INFO_FTS,
        consumeThreadMax = 128,
        consumeTimeout = 10000L)
@DependsOn("realtimeAdminApplication")
public class MatchEventInfoFtsConsumer implements RocketMQListener<Request<List<MatchEventInfo>>> {

    @Autowired
    private MatchEventInfoProducer matchEventInfoProducer;
    @NacosValue(value = "${consumer.switch.realtime_nonrealtime:true}", autoRefreshed = true)
    private boolean realtimeSwitch;
    @NacosValue(value = "${consumer.switch.realtime_nonrealtime_event:true}", autoRefreshed = true)
    private boolean realtimeEventSwitch;
    @Resource
    private DataCenterProducer<List<MatchEventInfo>> dataCenterProducer;

//    @ConsumerSwitch("realtime")
    @Override
    public void onMessage(Request<List<MatchEventInfo>> request) {
        if (!realtimeSwitch && !realtimeEventSwitch) {
            dataCenterProducer.send(request,MATCH_EVENT_INFO_FTS);
            return;
        }
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        //84632单：FTS事件linkId和原始事件区分开，避免下游认为是同一条数据过滤掉
        request.setLinkId(DataSourceCodeEnum.FTS.code+"_"+request.getLinkId());
        log.info("linkId=【{}】FTS事件消费处理开始,标准赛事ID={}", request.getLinkId(), request.getTag());
        try {
            matchEventInfoProducer.handleFtsMatchEventInfo(request.getLinkId(), request.getData(), Long.valueOf(request.getTag()));
        } catch (Exception e) {
            log.error("linkId=【" + request.getLinkId() + "】FTS事件消费处理异常,Exception:", e);
        } finally {
            stopWatch.stop();
            log.info("linkId=【{}】FTS事件消费处理结束,耗时={}", request.getLinkId(), stopWatch.getTotalTimeMillis());
        }
    }
}
