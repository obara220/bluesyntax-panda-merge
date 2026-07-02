package com.panda.merge.rocketmq.producer;

import com.alibaba.fastjson.JSON;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.settle.DataSourceConnectionStatusDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * 数据商连接状态推送Producer
 */
@Slf4j
@Component
public class DataSourceConnectionStatusProducer {

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    /**
     * 推送数据商连接状态
     * @param dto 连接状态DTO
     */
    public void pushDataSourceConnectionStatus(DataSourceConnectionStatusDto dto) {
        try {
            Request<DataSourceConnectionStatusDto> reqMessage = new Request<>();
            reqMessage.setLinkId(dto.getStandardMatchId().toString());
            reqMessage.setData(dto);
            MessageBuilder<Request<DataSourceConnectionStatusDto>> builder = MessageBuilder.withPayload(reqMessage)
                    .setHeader(MessageConst.PROPERTY_KEYS, dto.getStandardMatchId());
            rocketMqTemplate.send("DATASOURCE_CONNECTION_STATUS_PUSH:" + dto.getStandardMatchId(), builder.build());
            log.info("::{}::开始推送数据商连接状态,topic:DATASOURCE_CONNECTION_STATUS_PUSH,standardMatchId:{},datasourceCount:{},request={}", 
                    dto.getStandardMatchId(), dto.getStandardMatchId(), 
                    dto.getDatasourceStatusMap() != null ? dto.getDatasourceStatusMap().size() : 0, 
                    JSON.toJSONString(reqMessage));
        } catch (Exception e) {
            log.error("推送数据商连接状态失败, standardMatchId:{}", 
                    dto.getStandardMatchId(), e);
        }
    }
}

