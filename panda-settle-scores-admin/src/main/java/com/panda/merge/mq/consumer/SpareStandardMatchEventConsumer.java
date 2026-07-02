package com.panda.merge.mq.consumer;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.dto.Request;
import com.panda.merge.model.MatchEventInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Component
public class SpareStandardMatchEventConsumer {

    @NacosValue(value = "${settle.event.consume.thread.num:128}", autoRefreshed = true)
    private Integer threadNum;
    @NacosValue(value = "${slaveNamesrvAddr:}", autoRefreshed = true)
    private String slaveNamesrvAddr;
    private static final String TOPIC = "MATCH_EVENT_INFO_TO_RISK";
    private static final String CONSUMER_GROUP = "settle-group-MATCH_EVENT_INFO_SPARE";
    private DefaultMQPushConsumer consumer;

    @Resource
    private StandardMatchEventConsumer standardMatchEventConsumer;

    public ConsumeConcurrentlyStatus processMessages(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        MessageExt ext = msgs.get(0);
        String linkId = null;
        try {
            linkId = ext.getProperties().get("KEYS");
            log.info("linkId::{}::SpareStandardMatchEventConsumer start", linkId);
            String message = new String(ext.getBody(), StandardCharsets.UTF_8);
            if (StringUtils.isBlank(message)) {
                log.info("linkId::{}::SpareStandardMatchEventConsumer 接收到事件列表数据为空！", linkId);
            } else {
                // 解析消息
                JSONObject jSONObject = JSONObject.parseObject(message);
                List<MatchEventInfo> matchEventInfoList = JSONObject.parseObject(jSONObject.getString("data"), new TypeReference<List<MatchEventInfo>>() {
                });
                String dataSourceCode = jSONObject.getString("dataSourceCode");
                String dataSourceTime = jSONObject.getString("dataSourceTime");
                String dataType = jSONObject.getString("dataType");
                String tag = jSONObject.getString("tag");
                String operateId = jSONObject.getString("operateId");
                String isReissue = jSONObject.getString("isReissue");

                // 调用数据处理逻辑
                Request<List<MatchEventInfo>> request = new Request<>();
                request.setLinkId(linkId);
                request.setDataSourceCode(dataSourceCode);
                request.setDataSourceTime(dataSourceTime==null?null:Long.parseLong(dataSourceTime));
                request.setLinkId(linkId);
                request.setDataType(dataType);
                request.setTag(tag);
                request.setIsReissue(isReissue==null?null:Boolean.parseBoolean(isReissue));
                request.setOperaterId(operateId==null?null:Long.parseLong(operateId));
                request.setData(matchEventInfoList);
                standardMatchEventConsumer.onMessage(request);
            }
        } catch (Exception e) {
            log.error("linkId::{}::SpareStandardMatchEventConsumer 事件列表数据处理异常, Exception:", linkId, e);
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    @PostConstruct
    public void initConsumer() throws MQClientException {
        log.info("SpareStandardMatchEventConsumer:initConsumer");
        if(StringUtils.isEmpty(slaveNamesrvAddr)){
            return;
        }
        // 创建消费者并设置配置
        consumer = new DefaultMQPushConsumer(CONSUMER_GROUP);
        consumer.setInstanceName("SpareStandardMatchEventConsumer");
        // Set name servers
        consumer.setNamesrvAddr(slaveNamesrvAddr);
        // 设置最大并发线程数
        consumer.setConsumeThreadMax(threadNum);
        // 设置最小并发线程数
        consumer.setConsumeThreadMin(threadNum);
        // 从最后一个偏移量开始消费
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        // 一次处理一条消息
        consumer.setConsumeMessageBatchMaxSize(1);
        // 订阅备用-MQ主题
        log.info("SpareStandardMatchEventConsumer 订阅主题,多条赛事事件={}", TOPIC);
        consumer.subscribe(TOPIC, "*");
        // 注册消息监听器
        consumer.registerMessageListener(this::processMessages);
        // 启动消费者
        consumer.start();
        log.info("SpareStandardMatchEventConsumer started successfully");
    }

}
