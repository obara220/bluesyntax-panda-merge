package com.panda.merge.mq;

import com.panda.merge.dto.Request;
import com.panda.merge.dto.settle.DataSourceConnectionStatusDto;
import com.panda.merge.handler.PDSubcribe;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Slf4j
@Component
@Service
@RocketMQMessageListener(topic = "DATASOURCE_CONNECTION_STATUS_PUSH", consumerGroup = "ws-group-DataSourceConnectionStatusConsumer",consumeThreadMax = 10,
        consumeTimeout = 10000L,
        messageModel = MessageModel.BROADCASTING)
@DependsOn("mergeWebSocketApplication")
public class DataSourceConnectionStatusConsumer implements RocketMQListener<Request<DataSourceConnectionStatusDto>> {
    @Autowired
    PDSubcribe pdSubcribe;

    @Override
    public void onMessage(Request<DataSourceConnectionStatusDto> request) {
        log.info("DATASOURCE_CONNECTION_STATUS_PUSH收到消息:{}", request.getData());
        pdSubcribe.sendDataSourceConnectionStatus(request.getData());
    }
}

