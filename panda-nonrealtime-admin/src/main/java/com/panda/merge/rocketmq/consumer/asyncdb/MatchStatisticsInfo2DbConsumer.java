package com.panda.merge.rocketmq.consumer.asyncdb;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.annotation.ConsumerSwitch;
import com.panda.merge.dto.Request;
import com.panda.merge.model.MatchEventInfo;
import com.panda.merge.model.MatchStatisticsInfo;
import com.panda.merge.rocketmq.producer.DataCenterProducer;
import com.panda.merge.service.MatchStatisticsInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;

import static com.panda.merge.constant.ConstantSystem.CONSUME_NONREALTIME_GROUP;
import static com.panda.merge.constant.ConstantSystem.DATA_MATCHS_TATISTICS_INFO_DB;

/**
 * 三方赛事统计信息异步入库
 *
 * @author Tell
 * @since 2025年1月13日13:09:23
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = DATA_MATCHS_TATISTICS_INFO_DB,
        consumerGroup = CONSUME_NONREALTIME_GROUP + DATA_MATCHS_TATISTICS_INFO_DB,
        consumeThreadMax = 128,
        consumeTimeout = 10000L
)
@DependsOn("nonrealtimeAdminApplication")
public class MatchStatisticsInfo2DbConsumer implements RocketMQListener<Request<MatchStatisticsInfo>> {

    /**
     * panda数据库状态是否异常（false:否，true:是）
     * */
    @NacosValue(value = "${panda.db.error.realtime:false}", autoRefreshed = true)
    private Boolean pandaDbIsError;

    @Autowired
    private MatchStatisticsInfoService matchStatisticsInfoService;
    @NacosValue(value = "${consumer.switch.realtime_nonrealtime:true}", autoRefreshed = true)
    private boolean realtimeSwitch;
    @Resource
    private DataCenterProducer<MatchStatisticsInfo> dataCenterProducer;

    @Override
    public void onMessage(Request<MatchStatisticsInfo> request) {
//        if (!realtimeSwitch) {
//            dataCenterProducer.send(request,DATA_MATCHS_TATISTICS_INFO_DB);
//            return;
//        }
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        log.info("linkId=【{}】【DATA_MATCHS_TATISTICS_INFO_DB】消费三方赛事统计信息异步入库开始,pandaDbIsError={}",request.getLinkId(),pandaDbIsError);
        try {
            if(!pandaDbIsError){
                matchStatisticsInfoService.saveOrUpdate(request.getData(), request.getLinkId());
            }
        } catch (Exception e) {
            log.error("linkId=【"+request.getLinkId()+"】【DATA_MATCHS_TATISTICS_INFO_DB】消费三方赛事统计信息异步入库异常,Exception:", e);
        } finally {
            stopWatch.stop();
            log.info("linkId=【{}】【DATA_MATCHS_TATISTICS_INFO_DB】消费三方赛事统计信息异步入库结束,共耗时={}",request.getLinkId(),stopWatch.getTotalTimeMillis());
        }
    }
}

