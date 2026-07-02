package com.panda.merge.job;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.rocketmq.consumer.asyncdb.MatchEventInfo2DbsConsumer;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.panda.merge.constant.ConstantSystem.ONE;

/**
 * 手动触发MQ暂停消费（部分topic）
 * */
@Slf4j
@Component
@JobHandler(value = "RocketmqConsumerJob")
public class RocketmqConsumerJob extends IJobHandler {

    /**
     * 事件异步入库消费
     * */
    @Autowired
    private MatchEventInfo2DbsConsumer matchConsumer;

    @Override
    public ReturnT<String> execute(String param) {
        long currentTime = System.currentTimeMillis();
        log.info("【RocketmqConsumerJob 手动触发MQ暂停消费（部分topic）:{}】 处理开始,入参: {}", currentTime, param);
        XxlJobLogger.log("【RocketmqConsumerJob 手动触发MQ暂停消费（部分topic）:{}】 处理开始,入参: {}", currentTime, param);
        try {
            JSONObject jsonObject = JSON.parseObject(param);
            processData(jsonObject,currentTime);
        } catch (Exception e) {
            log.error("【RocketmqConsumerJob 手动触发MQ暂停消费（部分topic）执行异常:" + currentTime + "】 Exception:", e);
            XxlJobLogger.log("【RocketmqConsumerJob 手动触发MQ暂停消费（部分topic）执行异常:" + currentTime + "】 Exception:" + e.getMessage());
        }
        log.info("【RocketmqConsumerJob 手动触发MQ暂停消费（部分topic）:{}】 处理结束", currentTime);
        XxlJobLogger.log("【RocketmqConsumerJob 手动触发MQ暂停消费（部分topic）:{}】 处理结束", currentTime);
        return ReturnT.SUCCESS;
    }

    /**
     * 业务逻辑处理
     * */
    public void processData(JSONObject jsonObject,long currentTime){
        //panda数据库是否异常（0:否，1:是）
        Integer pandaDbIsError = jsonObject.getInteger("pandaDbIsError");
        DefaultMQPushConsumer consumer = matchConsumer.getConsumer();
        if (consumer != null) {
            if(ONE.equals(pandaDbIsError)){
                consumer.suspend();
                log.info("【RocketmqConsumerJob 手动触发MQ暂停消费（部分topic）:{}】 已暂停消费!", currentTime);
            }else{
                consumer.resume();
                log.info("【RocketmqConsumerJob 手动触发MQ暂停消费（部分topic）:{}】 已恢复消费!", currentTime);
            }
        }else{
            log.info("【RocketmqConsumerJob 手动触发MQ暂停消费（部分topic）:{}】 consumer为空,无法处理!", currentTime);
        }
    }
}

