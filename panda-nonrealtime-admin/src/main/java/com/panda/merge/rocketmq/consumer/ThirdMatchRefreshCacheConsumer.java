package com.panda.merge.rocketmq.consumer;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.annotation.ConsumerSwitch;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.MatchAssociationMessage;
import com.panda.merge.rocketmq.processor.ThirdMatchRefreshCacheProcessor;
import com.panda.merge.rocketmq.producer.DataCenterProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * 赛程项目操作【三方赛事绑定标准赛事】通知刷新缓存
 * @author :  tell
 * @since 2024年11月04日16:05:11
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = MATCH_OPERATE_MSG,
        consumerGroup = CONSUME_NONREALTIME_GROUP + MATCH_OPERATE_MSG,
        consumeThreadMax = 128,
        consumeTimeout = 10000L
)
@DependsOn("nonrealtimeAdminApplication")
public class ThirdMatchRefreshCacheConsumer implements RocketMQListener<MessageExt> {

    @Autowired
    private ThirdMatchRefreshCacheProcessor thirdMatchRefreshCacheProcessor;
    @NacosValue(value = "${consumer.switch.realtime_nonrealtime:true}", autoRefreshed = true)
    private boolean realtimeSwitch;
    @Resource
    private DataCenterProducer dataCenterProducer;

    @Override
    public void onMessage(MessageExt ext) {
        if (!realtimeSwitch) {
            dataCenterProducer.send(ext,MATCH_OPERATE_MSG);
            return;
        }
        String linkId = null;
        try {
            linkId = ext.getProperties().get("KEYS");
            log.info("【" + MATCH_OPERATE_MSG + ",::" + linkId + "::】【赛程项目操作】【三方赛事绑定标准赛事】通知刷新缓存开始");
            /** {"operateType":1,"thirdMatchIds":[1853160573632516098],"standardMatchId":3770749}*/
            String message = new String(ext.getBody(), StandardCharsets.UTF_8);
            if (StringUtils.isBlank(message)) {
                log.info("【" + MATCH_OPERATE_MSG + ",::" + linkId + "::】接收到数据为：{}", message);
            } else {
                JSONObject jsonObject = JSON.parseObject(message);
                //标准赛事信息
                Long standardMatchId = jsonObject.getLong("standardMatchId");
                //三方赛事列表
                JSONArray thirdMatchIds = jsonObject.getJSONArray("thirdMatchIds");
                for (int i=0;i<thirdMatchIds.size();i++) {
                    thirdMatchRefreshCacheProcessor.matchRefreshCache(linkId,thirdMatchIds.getLong(i),standardMatchId);
                }
            }
        } catch (Exception e) {
            log.error("【" + MATCH_OPERATE_MSG + ",::" + linkId + "::】【赛程项目操作】【三方赛事绑定标准赛事】通知刷新缓存异常:Exception", e);
        }finally {
            log.info("【" + MATCH_OPERATE_MSG + ",::" + linkId + "::】【赛程项目操作】【三方赛事绑定标准赛事】通知刷新缓存结束");
        }
    }
}
