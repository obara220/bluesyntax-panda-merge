package com.panda.merge.rocketmq.consumer;


import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.ConstantSystem;
import com.panda.merge.dto.Request;
import com.panda.merge.rocketmq.producer.DataCenterProducer;
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
 * 接收风控通知标准赛事是否需要手工完赛
 * 103304 【生产】【产品】【操盘风控】足球-等待加时期间异常下发完赛临时兜底-手动完赛
 * 兜底工具 PD事件源  PD状态源 可以正常触发完赛
 *
 * @author :  tell
 * @since 2026年01月24日
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = RCS_TRADE_MANUAL_FINISH,
        consumerGroup = CONSUME_REALTIME_GROUP + RCS_TRADE_MANUAL_FINISH,
        consumeThreadMax = 128,
        consumeTimeout = 10000L
)
@DependsOn("realtimeAdminApplication")
public class StandardMatchManuallyEndFlagConsumer implements RocketMQListener<Request<JSONObject>> {


    @Autowired
    public RedisService redisService;

    @NacosValue(value = "${consumer.switch.realtime_nonrealtime:true}", autoRefreshed = true)
    private boolean realtimeSwitch;

    @Resource
    private DataCenterProducer<JSONObject> dataCenterProducer;

    @Override
    public void onMessage(Request<JSONObject> request) {
        if (!realtimeSwitch) {
            dataCenterProducer.send(request, RCS_TRADE_MANUAL_FINISH);
            return;
        }
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        //{"data":{"manuallyEndFlag":0,"sportId ":1,"standardMatchId":11111},"linkId":"SR_0af42845202601241322010131011"} 
        JSONObject data = request.getData();
        //手工完赛标识,0:否,1:是
        Integer manuallyEndFlag = data.getInteger("manuallyEndFlag");
        Long sportId = data.getLong("sportId");
        Long standardMatchId = data.getLong("standardMatchId");
        log.info("【" + PROJECT_ID_REALTIME + " ：" + RCS_TRADE_MANUAL_FINISH + "】【::" + request.getLinkId() + "::】接收风控通知标准赛事是否需要手工完赛开始，传入参数：{}", JSONUtil.toJsonStr(data));
        //103304 【生产】【产品】【操盘风控】足球-等待加时期间异常下发完赛临时兜底-手动完赛
        String manuallyEndFlagKey = String.format(ConstantSystem.getStandardManuallyEndFlagKey(), standardMatchId);
        if (manuallyEndFlag == 1) {
            redisService.set(manuallyEndFlagKey, 1, RedisConfig.REDIS_DEFAULT_TIME);
        } else {
            redisService.del(manuallyEndFlagKey);
        }
        stopWatch.stop();
        log.info("【" + PROJECT_ID_REALTIME + " ：" + RCS_TRADE_MANUAL_FINISH + "】【::" + request.getLinkId() + "::】接收风控通知标准赛事是否需要手工完赛结束,耗时:{} 毫秒", stopWatch.getTotalTimeMillis());
    }
}
