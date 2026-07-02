package com.panda.merge.rocketmq.consumer;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.annotation.ConsumerSwitch;
import com.panda.merge.common.RateLimiterHandler;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdMatchStatusDTO;
import com.panda.merge.rocketmq.processor.ThirdMatchStatusProcessor;
import com.panda.merge.rocketmq.producer.DataCenterProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.panda.merge.constant.ConstantSystem.CONSUME_REALTIME_GROUP;
import static com.panda.merge.constant.ConstantSystem.THIRD_MATCH_STATUS_API;

/**
 * 赛事状态入口接口
 * 1.数据校验
 * 2.更新第三方赛事
 * 3.更新标准赛事（标准赛事依赖的数据源会同步更新标准赛事，其他数据源则只更新本身的数据）
 * 4.向下游推送赛事状态数据
 *
 * @author : bevan
 * @project Name : panda-merge
 * @package Name : com.panda.merge.rocketmq.consumer
 * @date: 2020-09-10 16:18
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = THIRD_MATCH_STATUS_API,
        consumerGroup = CONSUME_REALTIME_GROUP + THIRD_MATCH_STATUS_API,
        consumeThreadMax = 128,
        consumeTimeout = 10000L)
@DependsOn("realtimeAdminApplication")
public class ThirdMatchStatusConsumer implements RocketMQListener<Request<ThirdMatchStatusDTO>> {

    @Autowired
    private ThirdMatchStatusProcessor thirdMatchStatusProcessor;

    @Resource
    private RateLimiterHandler rateLimiterHandler;
    @NacosValue(value = "${consumer.switch.realtime_nonrealtime:true}", autoRefreshed = true)
    private boolean realtimeSwitch;
    @Resource
    private DataCenterProducer<ThirdMatchStatusDTO> dataCenterProducer;

    @ConsumerSwitch("realtime")
    @Override
    public void onMessage(Request<ThirdMatchStatusDTO> request) {
        if (!realtimeSwitch) {
            dataCenterProducer.send(request,THIRD_MATCH_STATUS_API);
            return;
        }
        // 3929 【融合】数据商异常下发告警&数据下发限频
        if (!rateLimiterHandler.filter(request.getData().getThirdMatchSourceId(),request.getData().getDataSourceCode())) {
            log.info("【{}】onMessage，该三方赛事状态被限流，数据不下发！源赛事ID={}", request.getLinkId(),request.getData().getThirdMatchSourceId());
            return ;
        }
        thirdMatchStatusProcessor.putMatchStatus(request);
    }
}
