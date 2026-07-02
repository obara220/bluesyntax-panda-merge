package com.panda.merge.mq.producer;

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
 * 复用XXL-Job的推送逻辑，使用相同的topic和消息格式
 */
@Slf4j
@Component
public class DataSourceConnectionStatusProducer {

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    private static final String DATA_SOURCE_N01 = "N01";

    /**
     * 推送数据商连接状态
     * @param dto 连接状态DTO
     */
    public void pushDataSourceConnectionStatus(DataSourceConnectionStatusDto dto) {
        try {
            // 过滤移除 N01 数据源的连接状态，不参与推送
            if (dto.getDatasourceStatusMap() != null && dto.getDatasourceStatusMap().remove(DATA_SOURCE_N01) != null) {
                log.debug("过滤移除数据源N01的连接状态, standardMatchId:{}", dto.getStandardMatchId());
            }
            if (dto.getDatasourceStatusMap() != null && dto.getDatasourceStatusMap().isEmpty()) {
                log.debug("过滤后无数据源连接状态，跳过推送, standardMatchId:{}", dto.getStandardMatchId());
                return;
            }
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

