package com.panda.merge.job;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.api.IFootballMatchScoresSettleApi;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

/**
 * 手动触发MQ暂停消费（部分topic）
 * */
@Slf4j
@Component
@JobHandler(value = "RocketmqSettleConsumerJob")
public class RocketmqSettleConsumerJob extends IJobHandler {

    /**
     * 事件异步入库消费
     * */
    @DubboReference
    IFootballMatchScoresSettleApi footballMatchScoresSettleApi;

    @Override
    public ReturnT<String> execute(String param) {
        long currentTime = System.currentTimeMillis();
        //log.info("【RocketmqSettleConsumerJob 手动触发MQ暂停消费（部分topic）:{}】 处理开始,入参: {}", currentTime, param);
        try {
            JSONObject jsonObject = JSON.parseObject(param);
            footballMatchScoresSettleApi.slaveRocketMqStopResume(jsonObject.getInteger("pandaDbIsError"));
        } catch (Exception e) {
            log.error("【RocketmqSettleConsumerJob 手动触发MQ暂停消费（部分topic）执行异常:" + currentTime + "】 Exception:", e);
        }
        //log.info("【RocketmqSettleConsumerJob 手动触发MQ暂停消费（部分topic）:{}】 处理结束", currentTime);
        return ReturnT.SUCCESS;
    }
}

