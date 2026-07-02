package com.panda.merge.rocketmq.producer;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.dto.CategoryDataSourceCodeDTO;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.StandardCategoryDataSourceCodeDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.panda.merge.constant.ConstantSystem.*;

@Slf4j
@Component
public class CategoryCodeProducer {
    @Autowired
    private RocketMQTemplate rocketMqTemplate;
    public void sendStandardInternalCodeToMQ(Request<StandardCategoryDataSourceCodeDTO> standardCategoryDataSourceCodeDTO)
    {
        MessageBuilder<Request<StandardCategoryDataSourceCodeDTO>> builder = MessageBuilder.withPayload(standardCategoryDataSourceCodeDTO)
                .setHeader(MessageConst.PROPERTY_KEYS, standardCategoryDataSourceCodeDTO.getLinkId());
        rocketMqTemplate.send("STANDARD_INTERNAL_DATASOURCECODE",builder.build());
        log.info("::{}::开售组装三方赛事内部数据源数据并下发,topic:STANDARD_INTERNAL_DATASOURCECODE ,request:{}", standardCategoryDataSourceCodeDTO.getLinkId(), JSON.toJSONString(standardCategoryDataSourceCodeDTO));
    }
    public void sendLCodeApiTOMQ(Request<List<CategoryDataSourceCodeDTO>> categoryDataSourceCodeDTOList){
        MessageBuilder<Request<List<CategoryDataSourceCodeDTO>>> builder = MessageBuilder.withPayload(categoryDataSourceCodeDTOList)
                .setHeader(MessageConst.PROPERTY_KEYS, categoryDataSourceCodeDTOList.getLinkId());
        rocketMqTemplate.syncSend("CATEGORY_DATASOURCECODE_L_API",builder.build(),SECOND_1 * THREE,ONE);
        log.info("::{}::开售组装L01三方赛事内部数据源数据并下发,topic:CATEGORY_DATASOURCECODE_L_API ,request:{}", categoryDataSourceCodeDTOList.getLinkId(), JSON.toJSONString(categoryDataSourceCodeDTOList));
    }
    public void sendTCodeApiTOMQ(Request<List<CategoryDataSourceCodeDTO>> categoryDataSourceCodeDTOList){
        MessageBuilder<Request<List<CategoryDataSourceCodeDTO>>> builder = MessageBuilder.withPayload(categoryDataSourceCodeDTOList)
                .setHeader(MessageConst.PROPERTY_KEYS, categoryDataSourceCodeDTOList.getLinkId());
        rocketMqTemplate.syncSend("CATEGORY_DATASOURCECODE_T_API",builder.build(),SECOND_1 * THREE,ONE);
        log.info("::{}::开售组装T01三方赛事内部数据源数据并下发,topic:CATEGORY_DATASOURCECODE_T_API ,request:{}", categoryDataSourceCodeDTOList.getLinkId(), JSON.toJSONString(categoryDataSourceCodeDTOList));
    }
    public void sendL02CodeApiTOMQ(Request<List<CategoryDataSourceCodeDTO>> categoryDataSourceCodeDTOList){
        MessageBuilder<Request<List<CategoryDataSourceCodeDTO>>> builder = MessageBuilder.withPayload(categoryDataSourceCodeDTOList)
                .setHeader(MessageConst.PROPERTY_KEYS, categoryDataSourceCodeDTOList.getLinkId());
        rocketMqTemplate.syncSend("CATEGORY_DATASOURCECODE_L02_API",builder.build(),SECOND_1 * THREE,ONE);
        log.info("::{}::开售组装L02三方赛事内部数据源数据并下发,topic:CATEGORY_DATASOURCECODE_L02_API ,request:{}", categoryDataSourceCodeDTOList.getLinkId(), JSON.toJSONString(categoryDataSourceCodeDTOList));
    }


    public void sendAutoOpenDataSourceCodeNewToMq(String linkId, Long standardMatchId, Map<String, String> newDataSourceCodeMap) {
        JSONObject obj = new JSONObject();
        obj.put("standardMatchId", standardMatchId);
        obj.put("newDataSourceCodeMap", newDataSourceCodeMap);
        obj.put("linkId", linkId);
        MessageBuilder<String> builder = MessageBuilder.withPayload(obj.toJSONString()).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        log.info("::{}::通知风控切换回数据源,topic:AUTO_OPEN_DATA_SOURCE_CODE_NEW_DATA,request:{}", linkId, JSON.toJSONString(obj));
        //第一个参数表示topic:tag
        rocketMqTemplate.asyncSend("AUTO_OPEN_DATA_SOURCE_CODE_NEW_DATA:" + standardMatchId, builder.build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("::{}::AUTO_OPEN_DATA_SOURCE_CODE_NEW_DATA,send successful", linkId);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::TOPIC={}，send fail; ", linkId, "AUTO_OPEN_DATA_SOURCE_CODE_NEW_DATA", throwable);
            }
        });
    }

}
