package com.panda.merge.mq.spare;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.dto.Request;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
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
 *
 * @author warren
 * @since 2025/02/10 00:52:42
 */
@Slf4j
@Component
public class SpareBaseProducer<T> {

    @Value("${slaveProducerGroup}")
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
    public void send(Request<List<T>> request) {
        log.info("linkId=【{}】【{}】数据发送到备用-MQ开始, tags={}", request.getLinkId(), request.getDataType(), request.getTag());
        try {
            request.setSpareMq(true);
            Message message = new Message(request.getDataType(), request.getTag(), request.getLinkId(), JSONObject.toJSONString(request).getBytes(RemotingHelper.DEFAULT_CHARSET));
            // 发送消息（单向发送，不等待服务器返回）
            spareProducer.sendOneway(message);
        } catch (Exception e) {
            log.error("linkId=【{}】【{}】数据发送备用-MQ异常, tags={}", request.getLinkId(), request.getDataType(), request.getTag(), e);
            // 根据需要可以执行重试机制或者报警
        } finally {
            log.info("linkId=【{}】【{}】数据发送到备用-MQ结束, tags={}", request.getLinkId(), request.getDataType(), request.getTag());
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
