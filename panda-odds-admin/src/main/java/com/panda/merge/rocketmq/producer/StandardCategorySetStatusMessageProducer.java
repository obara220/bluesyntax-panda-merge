package com.panda.merge.rocketmq.producer;

import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.config.RedisService;
import com.panda.merge.model.StandardMatchInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 玩法集合状态
 */
@Slf4j
@Component
public class StandardCategorySetStatusMessageProducer extends BaseProcessor {

    @Autowired
    private RocketMQTemplate rocketMqTemplate;
    @Autowired
    private RedisService redisService;
    /**
     * 提前结算开关，false关，true开
     */
    @NacosValue(value = "${market.pre.switch}", autoRefreshed = true)
    private boolean marketPreSwitch;
    public void sendStandardCategorySetStatus(String linkId, StandardMatchInfo standardMatchInfo) {
        if (!marketPreSwitch) {
            log.info("::{}::提前结算NACOS关,操盘玩法集状态不处理", linkId);
            return;
        }
        String redisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_SET_STATUS + standardMatchInfo.getId());
        Map<String, Integer> categorySetStatusMap = redisService.hGetAll(redisKey);
        if (MapUtils.isEmpty(categorySetStatusMap)) {
            return;
        }
        JSONObject obj = new JSONObject();
        obj.put("linkId", linkId);
        obj.put("standardMatchId", standardMatchInfo.getId());
        obj.put("categorySetStatusMap", categorySetStatusMap);
        obj.put("dataSourceTime", System.currentTimeMillis());
        MessageBuilder<JSONObject> builder = MessageBuilder.withPayload(obj).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        log.info("::{}::开始下发提前结算操盘玩法集状态,topic:STANDARD_CATEGORY_SET_STATUS,request:{}", linkId, JSON.toJSONString(obj));
        rocketMqTemplate.asyncSend("STANDARD_CATEGORY_SET_STATUS:" + standardMatchInfo.getId(), builder.build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("::{}::,STANDARD_CATEGORY_SET_STATUS send successful", linkId);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::TOPIC={}，send fail; ", linkId, "STANDARD_CATEGORY_SET_STATUS", throwable);
            }
        });
    }

}
