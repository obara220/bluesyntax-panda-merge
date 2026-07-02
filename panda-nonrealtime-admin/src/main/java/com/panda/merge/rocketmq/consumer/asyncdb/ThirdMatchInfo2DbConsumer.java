package com.panda.merge.rocketmq.consumer.asyncdb;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.annotation.ConsumerSwitch;
import com.panda.merge.dto.Request;
import com.panda.merge.model.MatchStatisticsInfo;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.rocketmq.producer.DataCenterProducer;
import com.panda.merge.service.ThirdMatchInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import javax.annotation.Resource;
import static com.panda.merge.constant.ConstantSystem.CONSUME_NONREALTIME_GROUP;
import static com.panda.merge.constant.ConstantSystem.DATA_THIRD_MATCH_INFO_DB;

/**
 * 三方赛事信息异步修改入库
 *
 * @author Tell
 * @since 2025年1月13日13:09:23
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = DATA_THIRD_MATCH_INFO_DB,
        consumerGroup = CONSUME_NONREALTIME_GROUP + DATA_THIRD_MATCH_INFO_DB,
        consumeThreadMax = 128,
        consumeTimeout = 10000L
)
@DependsOn("nonrealtimeAdminApplication")
public class ThirdMatchInfo2DbConsumer implements RocketMQListener<Request<ThirdMatchInfo>> {

    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;
    @NacosValue(value = "${consumer.switch.realtime_nonrealtime:true}", autoRefreshed = true)
    private boolean realtimeSwitch;
    @Resource
    private DataCenterProducer<ThirdMatchInfo> dataCenterProducer;

    @Override
    public void onMessage(Request<ThirdMatchInfo> request) {
        if (!realtimeSwitch) {
            dataCenterProducer.send(request,DATA_THIRD_MATCH_INFO_DB);
            return;
        }
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        log.info("linkId=【{}】【DATA_THIRD_MATCH_INFO_DB】消费三方赛事信息异步修改入库开始", request.getLinkId());
        try {
            thirdMatchInfoService.updateByPrimaryKeySelective(request.getData(), request.getLinkId());
        } catch (Exception e) {
            log.error("linkId=【" + request.getLinkId() + "】【DATA_THIRD_MATCH_INFO_DB】消费三方赛事信息异步修改入库异常,Exception:", e);
        }finally {
            stopWatch.stop();
            log.info("linkId=【{}】【DATA_THIRD_MATCH_INFO_DB】消费三方赛事信息异步修改入库结束,共耗时={}", request.getLinkId(),stopWatch.getTotalTimeMillis());
        }
    }
}

