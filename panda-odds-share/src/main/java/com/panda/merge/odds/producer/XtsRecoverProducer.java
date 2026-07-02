package com.panda.merge.odds.producer;

import com.alibaba.fastjson.JSON;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.Request;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import static com.panda.merge.common.enums.Constant.REDIS_KEY.RONGHE_XTS_MATCH_AUTO_SWITCH;

/**
 * XtsRecoverProducer
 *
 * @description:
 * @date: 7/13/2025
 **/
@Slf4j
@Component
public class XtsRecoverProducer {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class XtsRecover implements java.io.Serializable {

        private Long matchId;

        private Integer matchType;

    }

    @Autowired
    private RocketMQTemplate rocketMqTemplate;
    @Autowired
    private RedisService redisService;

    public void send(Long uuid, Long matchId, Integer matchType) {
        Object xtsMatchAutoSwitchObj = redisService.get(RONGHE_XTS_MATCH_AUTO_SWITCH + matchId + "_" + matchType);
        if (null == xtsMatchAutoSwitchObj || (int) xtsMatchAutoSwitchObj == 0) {
            log.info("::{}::玩法数据源变更下发风控,xts切换非自动不下发:{}", uuid, xtsMatchAutoSwitchObj);
            return;
        }
        Request<XtsRecover> request = new Request<>();
        request.setLinkId(uuid.toString());

        request.setData(new XtsRecover(matchId, matchType));
        {
            MessageBuilder<String> stringMessageBuilder = MessageBuilder
                    .withPayload(JSON.toJSONString(request))
                    .setHeader(MessageConst.PROPERTY_KEYS, uuid)
                    .setHeader(MessageConst.PROPERTY_TAGS, matchId);
            rocketMqTemplate.send("RCS_XTS_RECOVER", stringMessageBuilder.build());
            log.info("::{}::玩法数据源变更下发风控:{}", request.getLinkId(), JSON.toJSONString(request));
        }
    }


}
