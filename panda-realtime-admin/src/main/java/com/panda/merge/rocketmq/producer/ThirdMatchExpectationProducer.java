package com.panda.merge.rocketmq.producer;


import com.panda.merge.dto.ThirdMatchExpectationDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ThirdMatchExpectationProducer {

    @Autowired
    private RocketMQTemplate rocketMqTemplate;


    public void sendThirdMatchExpectation(ThirdMatchExpectationDTO thirdMatchExpectationDTO) {
        try{
            MessageBuilder<ThirdMatchExpectationDTO> builder = MessageBuilder.withPayload(thirdMatchExpectationDTO)
                    .setHeader(MessageConst.PROPERTY_KEYS, thirdMatchExpectationDTO.getThirdMatchSourceId());
            //实时服务预期信息更新通知赛程
            rocketMqTemplate.send("THIRD_MATCH_EXPECTATION", builder.build());
            log.info("linkId=【{}】sendThirdMatchExpectation 结束,topic=THIRD_MATCH_EXPECTATION", thirdMatchExpectationDTO.getThirdMatchSourceId());
        }catch (Exception e){
            log.info("::"+thirdMatchExpectationDTO.getThirdMatchSourceId()+"::sendThirdMatchExpectation " +
                    "实时服务预期信息更新通知赛程异常,topic=THIRD_MATCH_EXPECTATION,Exception:",e);
        }
    }
}
