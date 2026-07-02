package com.panda.merge.mq.producer;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.dto.Request;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Kepa
 */
@Slf4j
@Component
public class CommonProducer<T> {

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    //是否使用数据中心接口
    @NacosValue(value = "${datacenter.settle.switch}", autoRefreshed = true)
    private Boolean datacenterSettleSwitch;

    @NacosValue(value = "${datacenter.settle.id}", autoRefreshed = true)
    private String datacenterSettleId;

    /**
     * 同步推送
     */
    public void send( Request<T> request, String topic) {
        String linkId = request.getLinkId();
        if ( StringUtils.isEmpty(linkId) ) {
            linkId = sdf.format(new Date()) + "_" + UUID.randomUUID();
        }
        MessageBuilder<Request<T>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        rocketMqTemplate.send(topic, builder.build());
    }

    /**
     * 异步推送
     */
    public void asyncSend(Request<T> request, String topic) {
        String linkId = request.getLinkId();
        if ( StringUtils.isEmpty(linkId) ) {
            linkId = sdf.format(new Date()) + "_" + UUID.randomUUID();
        }
        MessageBuilder<Request<T>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        String finalLinkId = linkId;
        rocketMqTemplate.asyncSend(topic, builder.build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("::{}::,send successful", finalLinkId);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::TOPIC={}，send fail; ", finalLinkId, topic, throwable);
            }
        });
    }

    public boolean getDatacenterMatchIds(String matchId) {
        log.info("结算2.0数据中心消息转发：获取赛事ID：{}",matchId);
        // 快速失败：不满足基本条件直接返回
        if (!datacenterSettleSwitch || StringUtils.isBlank(matchId)) {
            log.info("结算2.0数据中心消息转发1：赛事ID：{} false",matchId);
            return false;
        }

        if (StringUtils.isBlank(datacenterSettleId)) {
            log.info("结算2.0数据中心消息转发2：赛事ID：{} true",matchId);
            return true;
        }
        // 转换为set集合
        Set<String> spareMatchIds = new HashSet<>(
                Arrays.asList(datacenterSettleId.split(","))
        );
        log.info("结算2.0数据中心消息转发3：赛事ID：{} {}",matchId,spareMatchIds.contains(matchId));
        return spareMatchIds.contains(matchId) ;
    }
}
