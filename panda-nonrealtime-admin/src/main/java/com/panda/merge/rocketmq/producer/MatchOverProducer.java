package com.panda.merge.rocketmq.producer;

import com.alibaba.fastjson.JSON;
import com.panda.merge.bo.StandardMatchOverBO;
import com.panda.merge.bo.ThirdMatchOverBO;
import com.panda.merge.dto.Request;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

import static com.panda.merge.constant.ConstantSystem.STANDARD_MATCH_OVER_PLS;
import static com.panda.merge.constant.ConstantSystem.THIRD_MATCH_OVER_PLS;

/**
 * 完赛通知
 */
@Slf4j
@Component
public class MatchOverProducer {

    @Resource
    private RocketMQTemplate rocketMqTemplate;

    /**
     * 标准赛事完赛通知下游(比分网)
     * */
    public void sendStandardMatchOverPls(String linkId, List<StandardMatchOverBO> standardMatchOverBOs, Long dataSourceTime) {
        Request<List<StandardMatchOverBO>> requestMsg = new Request<>();
        requestMsg.setData(standardMatchOverBOs);
        requestMsg.setLinkId(linkId);
        requestMsg.setDataSourceTime(dataSourceTime);
        MessageBuilder<Request<List<StandardMatchOverBO>>> requestMessageBuilder = MessageBuilder.withPayload(requestMsg).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        rocketMqTemplate.send(STANDARD_MATCH_OVER_PLS , requestMessageBuilder.build());
        log.info("linkId=【{}】标准赛事完赛下发完成,topic=STANDARD_MATCH_OVER_PLS,request:{}", linkId, JSON.toJSONString(requestMsg));
    }

    /**
     * 三方赛事完赛通知下游(比分网)
     * */
    public void sendThirdMatchOverPls(String linkId, List<ThirdMatchOverBO> thirdMatchOverBOs, Long dataSourceTime) {
        Request<List<ThirdMatchOverBO>> requestMsg = new Request<>();
        requestMsg.setData(thirdMatchOverBOs);
        requestMsg.setLinkId(linkId);
        requestMsg.setDataSourceTime(dataSourceTime);
        MessageBuilder<Request<List<ThirdMatchOverBO>>> requestMessageBuilder = MessageBuilder.withPayload(requestMsg).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        rocketMqTemplate.send(THIRD_MATCH_OVER_PLS , requestMessageBuilder.build());
        log.info("linkId=【{}】三方赛事完赛下发完成,topic=THIRD_MATCH_OVER_PLS,request:{}", linkId, JSON.toJSONString(requestMsg));
    }
}
