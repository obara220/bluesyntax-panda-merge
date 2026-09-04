package com.panda.merge.mq.producer;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.dto.Request;
import com.panda.merge.model.StandardMatchScores;
import com.panda.merge.mq.message.CommonStandardScoresDto;
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
    @NacosValue(value = "${datacenter.scores.switch:false}", autoRefreshed = true)
    private Boolean datacenterMergeSwitch;
    @NacosValue(value = "${datacenter.scores.matchId}", autoRefreshed = true)
    private String datacenterScoresMatchId;

    /**
     * 同步推送
     */
    public void send(Request<T> request, String topic) {
        String linkId = request.getLinkId();
        if ( StringUtils.isEmpty(linkId) ) {
            linkId = sdf.format(new Date()) + "_" + UUID.randomUUID();
        }
        MessageBuilder<Request<T>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        rocketMqTemplate.send(topic, builder.build());
    }

//    /**
//     * 异步推送
//     */
//    public void asyncSend(Object request, String topic) {
////        log.info("::{}::数据中心MQ消息转发：msg={}，send start; ", topic,request);
//
////        if(request!=null){
////            String objStr = JSONObject.toJSONString( request);
////            JSONObject jsonObj = JSONObject.parseObject(objStr);
////            if(jsonObj!=null){
////                Object linkStr = jsonObj.get("linkId");
////                if(linkStr!=null){
////                    String link = jsonObj.getString("linkId");
////                    if(StringUtils.isNotEmpty(link)){
////                        linkId = link;
////                        log.info("::{}::数据中心MQ消息转发：获取linkId:{},msg={}，send start; ", topic,linkId,request);
////                    }
////                }
////            }
////        }
//        String linkId = sdf.format(new Date()) + "_" + UUID.randomUUID();
//        MessageBuilder<Object> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
//        String finalLinkId = linkId;
//        rocketMqTemplate.asyncSend(topic, builder.build(), new SendCallback() {
//            @Override
//            public void onSuccess(SendResult sendResult) {
//                log.info("数据中心MQ消息转发成功：topic={},linkId={}，send successful; ", topic,finalLinkId);
//            }
//            @Override
//            public void onException(Throwable throwable) {
//                log.error("::{}::TOPIC={}，数据中心MQ消息转发失败 send fail; ", finalLinkId, topic, throwable);
//            }
//        });
//    }

    public void asyncSend(Object request, String topic, String linkId) {
        MessageBuilder<Object> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        rocketMqTemplate.asyncSend(topic, builder.build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("数据中心MQ消息转发成功：topic={},linkId={}，send successful; ", topic,linkId);
            }
            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::TOPIC={}，数据中心MQ消息转发失败 send fail; ", linkId, topic, throwable);
            }
        });
    }

    /**
     * 赛事ID判断是否转发MQ
     * @param matchId
     * @return
     */
    public boolean getDatacenterMatchIds(String matchId) {
        log.info("数据中心消息转发：获取赛事ID：{}",matchId);
        // 快速失败：不满足基本条件直接返回
        if (!datacenterMergeSwitch || StringUtils.isBlank(matchId)) {
            log.info("数据中心消息转发1：赛事ID：{} false",matchId);
            return false;
        }

        if (StringUtils.isBlank(datacenterScoresMatchId)) {
            log.info("数据中心消息转发2：赛事ID：{} true",matchId);
            return true;
        }
        // 转换为set集合
        Set<String> spareMatchIds = new HashSet<>(
                Arrays.asList(datacenterScoresMatchId.split(","))
        );
        log.info("数据中心消息转发3：赛事ID：{} {}",matchId,spareMatchIds.contains(matchId));
        return spareMatchIds.contains(matchId) ;
    }


    public void asyncSendList(List<Request<Object>> list, String topic, String linkId) {
        if(!list.isEmpty()){
            for (Object request : list) {
                MessageBuilder<Object> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
                rocketMqTemplate.asyncSend(topic, builder.build(), new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        log.info("数据中心MQ消息转发成功：topic={},linkId={}，send successful; ", topic,linkId);
                    }
                    @Override
                    public void onException(Throwable throwable) {
                        log.error("::{}::TOPIC={}，数据中心MQ消息转发失败 send fail; ", linkId, topic, throwable);
                    }
                });
            }
        }
    }
}
