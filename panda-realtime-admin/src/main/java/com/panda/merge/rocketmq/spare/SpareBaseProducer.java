package com.panda.merge.rocketmq.spare;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.dto.Request;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;

import static com.panda.merge.constant.ConstantSystem.CONSUME_REALTIME_GROUP;

/**
 * 备用-MQ 生产者
 */
@Slf4j
@Component
public class SpareBaseProducer<T> {

    @Value("${rocketmq.producer.group}")
    private String producerGroup;

    @Value("${slaveNamesrvAddr}")
    private String namesrvAddr;

    /**
     * 生产者实例
     */
    private DefaultMQProducer spareProducer;

    @PostConstruct
    public void initializeProducer() {
        try {
            // 创建生产者实例并配置
            spareProducer = new DefaultMQProducer(producerGroup);
            spareProducer.setNamesrvAddr(namesrvAddr);
            spareProducer.setInstanceName(CONSUME_REALTIME_GROUP + "-SpareBaseProducer");

            // 启动生产者
            spareProducer.start();
            log.info("SpareBaseProducer启动成功,namesrvAddr={},producerGroup={}", namesrvAddr, producerGroup);
        } catch (Exception e) {
            log.error("SpareBaseProducer启动异常", e);
            throw new RuntimeException("SpareBaseProducer启动失败", e);  // 可能需要抛出异常或采取其他措施
        }
    }

    /**
     * 投递数据到备用-MQ
     *
     * @param request 下发对象
     **/
    public void syncSend(Request<List<T>> request) {
        log.info("linkId=【{}】【{}】数据发送到备用-MQ开始, tags={}", request.getLinkId(), request.getDataType(), request.getTag());
        try {
            Message message = new Message(request.getDataType(), request.getTag(), request.getLinkId(), JSONObject.toJSONString(request).getBytes(RemotingHelper.DEFAULT_CHARSET));
            // 发送消息（单向发送，不等待服务器返回,吞吐量最大，容易丢失消息）
//            spareProducer.sendOneway(message);
            //异步发送（注册回调）
            spareProducer.send(message, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    log.info("linkId=【{}】【{}】数据发送到备用-MQ-成功, tags={}", request.getLinkId(), request.getDataType(), request.getTag());
                }

                @Override
                public void onException(Throwable e) {
                    log.info("linkId=【{}】【{}】数据发送到备用-MQ-失败, tags={},error={}", request.getLinkId(), request.getDataType(), request.getTag(),e.getMessage());
                }
            });
        } catch (Exception e) {
            log.error("linkId=【{}】【{}】数据发送备用-MQ异常, tags={}", request.getLinkId(), request.getDataType(), request.getTag(), e);
            // 根据需要可以执行重试机制或者报警
        } finally {
            log.info("linkId=【{}】【{}】数据发送到备用-MQ结束, tags={}", request.getLinkId(), request.getDataType(), request.getTag());
        }
    }


    /**
     * 投递数据到备用-MQ-延迟下发
     *
     * @param request 下发对象
     * @param delayTimeLevel 设置延迟级别（1:1s,2:5s,3:10s）
     **/
    public void syncSend(Request<List<T>> request,int delayTimeLevel){
        log.info("linkId=【{}】【{}】数据发送到备用-MQ-延迟下发开始, tags={}, delayTimeLevel={}", request.getLinkId(), request.getDataType(), request.getTag(),delayTimeLevel);
        try {
            Message message = new Message(request.getDataType(), request.getTag(), request.getLinkId(), JSONObject.toJSONString(request).getBytes(RemotingHelper.DEFAULT_CHARSET));
            if(delayTimeLevel > 0){
                message.setDelayTimeLevel(delayTimeLevel);
            }
            // 发送消息（单向发送，不等待服务器返回,吞吐量最大，容易丢失消息）
//            spareProducer.sendOneway(message);
            //异步发送（注册回调）
            spareProducer.send(message, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    log.info("linkId=【{}】【{}】数据发送到备用-MQ-延迟下发成功, tags={}", request.getLinkId(), request.getDataType(), request.getTag());
                }

                @Override
                public void onException(Throwable e) {
                    log.info("linkId=【{}】【{}】数据发送到备用-MQ-延迟下发失败, tags={},error={}", request.getLinkId(), request.getDataType(), request.getTag(),e.getMessage());
                }
            });
        } catch (Exception e) {
            log.error("linkId=【{}】【{}】数据发送备用-MQ-延迟下发异常, tags={}", request.getLinkId(), request.getDataType(), request.getTag(), e);
            // 根据需要可以执行重试机制或者报警
        } finally {
            log.info("linkId=【{}】【{}】数据发送到备用-MQ-延迟下发结束, tags={}", request.getLinkId(), request.getDataType(), request.getTag());
        }
    }

    /**
     * 关闭生产者实例
     */
    @PreDestroy
    public void shutdownProducer() {
        if (spareProducer != null) {
            try {
                spareProducer.shutdown();
                log.info("SpareBaseProducer关闭成功.");
            } catch (Exception e) {
                log.error("SpareBaseProducer关闭异常", e);
            }
        }
    }
}
