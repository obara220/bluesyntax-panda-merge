package com.panda.merge.rocketmq.producer;

import com.panda.merge.dto.Request;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Component
public class DataCenterProducer<T> {

    @Resource
    private RocketMQTemplate rocketMqTemplate;

    public void send(Request<T> request,String topic) {
        String forwardTopic = topic+"_DC";
        log.info("linkId={},topic={},tag={}--向数据中心转发开始",request.getLinkId(),forwardTopic,request.getTag());
        if (StringUtils.isBlank(request.getTag())) {
            request.setTag("forward_to_dc");
        }
        try {
            MessageBuilder<Request<T>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, request.getLinkId());
            rocketMqTemplate.send(forwardTopic + ":" + request.getTag(), builder.build());
        } catch (Exception e) {
            log.info("linkId={},topic={},tag={}--向数据中心转发异常",request.getLinkId(),forwardTopic,request.getTag());
            log.error("linkId={}--向数据中心转发异常",request.getLinkId(),e);
        }
        log.info("linkId={},topic={},tag={}--向数据中心转发结束",request.getLinkId(),forwardTopic,request.getTag());
    }

    public void send(T request,String topic,String linkId,String tag) {
        String forwardTopic = topic+"_DC";
        log.info("linkId={},topic={},tag={}--向数据中心转发开始",linkId,forwardTopic,tag);
        try {
            MessageBuilder<T> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
            rocketMqTemplate.send(forwardTopic + ":" + tag, builder.build());
        } catch (Exception e) {
            log.info("linkId={},topic={},tag={}--向数据中心转发异常",linkId,forwardTopic,tag);
            log.error("linkId={}--向数据中心转发异常",linkId,e);
        }
        log.info("linkId={},topic={},tag={}--向数据中心转发结束",linkId,forwardTopic,tag);
    }

    public void send(MessageExt messageExt,String topic) {
        String linkId = messageExt.getKeys();
        String tag = messageExt.getTags();
        String forwardTopic = topic+"_DC";
        log.info("linkId={},topic={},tag={}--向数据中心转发开始",linkId,forwardTopic,tag);
        try {
            Message message = new Message(forwardTopic, tag, linkId, messageExt.getBody());
            rocketMqTemplate.getProducer().send(message);
        } catch (Exception e) {
            log.info("linkId={},topic={},tag={}--向数据中心转发异常",linkId,forwardTopic,tag);
            log.error("linkId={}--向数据中心转发异常",linkId,e);
        }
        log.info("linkId={},topic={},tag={}--向数据中心转发结束",linkId,forwardTopic,tag);
    }

    /** 批量拉取后,一次消费多条,需遍历转发 */
    public void send(List<Request<T>> requests, String topic) {
        if (CollectionUtils.isEmpty(requests)) {
            return;
        }
        for (Request<T> request : requests) {
            send(request,topic);
        }
    }
}
