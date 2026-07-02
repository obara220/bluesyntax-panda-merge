package com.panda.merge.rocketmq.producer;

import com.alibaba.fastjson.JSON;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdGlobalStatusDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/9/10 <br>
 * @see com.panda.merge.rocketmq.producer <br>
 */
@Slf4j
@Component
public class ThirdGlobalStatusProducer {
    @Autowired
    private RocketMQTemplate rocketMqTemplate;

     public void pushThirdGlobalStatus(String linkId, ThirdGlobalStatusDTO thirdGlobalStatusDTO){
         Request<ThirdGlobalStatusDTO> request = new Request<>();
         request.setData(thirdGlobalStatusDTO);
         request.setLinkId(linkId);
         MessageBuilder<Request<ThirdGlobalStatusDTO>> builder = MessageBuilder.withPayload(request)
                 .setHeader(MessageConst.PROPERTY_KEYS, linkId);
         rocketMqTemplate.send("STANDARD_GLOBAL_STATUS:" + thirdGlobalStatusDTO.getDataSourceCode(), builder.build());
         log.info("linkId=【{}】开始组装三方数据源状态并下发,topic=STANDARD_GLOBAL_STATUS,request:{}", linkId, JSON.toJSONString(request));
    }
}
