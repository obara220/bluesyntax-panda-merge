package com.panda.merge.rocketmq.producer;


import com.alibaba.fastjson.JSONObject;
import com.panda.merge.constant.ConstantSystem;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.StandardCategoryIdsDiffDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ClearStandardCategoryIdsDiffProducer {

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    public void producer(String linkId, StandardCategoryIdsDiffDTO standardCategoryIdsDiffDTO) {
        Request<StandardCategoryIdsDiffDTO> request = new Request<>();
        request.setLinkId(linkId);
        request.setData(standardCategoryIdsDiffDTO);
        MessageBuilder<Request<StandardCategoryIdsDiffDTO>> builder = MessageBuilder.withPayload(request)
                .setHeader(MessageConst.PROPERTY_KEYS, linkId);
        rocketMqTemplate.send(ConstantSystem.STANDARD_CATEGORYID_CLEAR_DIFF_RISK + ":" + standardCategoryIdsDiffDTO.getStandardMatchId(), builder.build());
        log.info("::{}::,apply清除水差下发风控,topic:STANDARD_CATEGORYID_CLEAR_DIFF_RISK,标准赛事:{},通知下发数据:{}", linkId, standardCategoryIdsDiffDTO.getStandardMatchId(), JSONObject.toJSONString(standardCategoryIdsDiffDTO));
    }

}
