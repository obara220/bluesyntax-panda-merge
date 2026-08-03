package com.panda.merge.rocketmq.consumer.asyncdb;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.annotation.ConsumerSwitch;
import com.panda.merge.dto.Request;
import com.panda.merge.model.MatchStatisticsInfo;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.rocketmq.producer.DataCenterProducer;
import com.panda.merge.rocketmq.producer.StandardMatchInfoProducer;
import com.panda.merge.service.StandardMatchInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * 标准赛事赛事信息异步修改入库
 *
 * @author Tell
 * @since 2025年1月13日13:09:23
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = DATA_STANDARD_MATCH_INFO_DB,
        consumerGroup = CONSUME_NONREALTIME_GROUP + DATA_STANDARD_MATCH_INFO_DB,
        consumeThreadMax = 128,
        consumeTimeout = 10000L
)
@DependsOn("nonrealtimeAdminApplication")
public class StandardMatchInfo2DbConsumer implements RocketMQListener<Request<StandardMatchInfo>> {

    @Autowired
    private StandardMatchInfoService standardMatchInfoService;
    @NacosValue(value = "${consumer.switch.realtime_nonrealtime:true}", autoRefreshed = true)
    private boolean realtimeSwitch;
    @Resource
    private DataCenterProducer<StandardMatchInfo> dataCenterProducer;

    @Resource
    private StandardMatchInfoProducer standardMatchInfoProducer;

    @Override
    public void onMessage(Request<StandardMatchInfo> request) {
        if (!realtimeSwitch) {
            dataCenterProducer.send(request,DATA_STANDARD_MATCH_INFO_DB);
            return;
        }
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        StandardMatchInfo matchInfo = request.getData();
        log.info("linkId=【{}】【DATA_STANDARD_MATCH_INFO_DB】消费标准赛事赛事信息异步修改入库开始,赛事ID={}", request.getLinkId(),matchInfo.getId());
        try {
            standardMatchInfoService.updateByPrimaryKeySelective(matchInfo,request.getLinkId());
            standardMatchInfoProducer.pushStandardMatchModify(request.getLinkId(), matchInfo);
        } catch (Exception e) {
            log.error("linkId=【" + request.getLinkId() + "】【DATA_STANDARD_MATCH_INFO_DB】消费标准赛事赛事信息异步修改入库异常,Exception:", e);
        }finally {
            stopWatch.stop();
            log.info("linkId=【{}】【DATA_STANDARD_MATCH_INFO_DB】消费标准赛事赛事信息异步修改入库结束,共耗时={}", request.getLinkId(),stopWatch.getTotalTimeMillis());
        }
    }
}

