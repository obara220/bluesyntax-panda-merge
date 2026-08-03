package com.panda.merge.rocketmq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.Request;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

import static com.panda.merge.constant.ConstantSystem.DATACENTER;
import static com.panda.merge.constant.ConstantSystem.UNBIND_AOMATCH_DATA;


/**
 * 三方赛事取消关联
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = UNBIND_AOMATCH_DATA,
        consumerGroup = "odds-group-" + UNBIND_AOMATCH_DATA,
        consumeThreadMax = 20, consumeTimeout = 10000L)
@DependsOn("oddsAdminApplication")

public class ThirdMatchCancelAssociationConsumer implements RocketMQListener<Request<JSONObject>> {

    @Autowired
    private RedisService redisService;
    /**
     * 数据中心赔率状态开关 1开 0关
     */
    @NacosValue(value = "${datacenter.odds.status:1}", autoRefreshed = true)
    private Integer datacenterOddsStatus;

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    @Override
    public void onMessage(Request<JSONObject> request) {
        //数据中心赔率状态开关 1开 0关
        if (datacenterOddsStatus == 1) {
            // 转发消息到数据中心
            log.info("收到 ::{}:: Topic的消息：{}", UNBIND_AOMATCH_DATA, request.getData());
            String toTopic = UNBIND_AOMATCH_DATA + DATACENTER;
            String destination = !StringUtils.isEmpty(request.getTag()) ? toTopic + ":" + request.getTag() : toTopic;
            // 发送到 数据中心Topic
            MessageBuilder<Request<JSONObject>> builder = MessageBuilder.withPayload(request)
                    .setHeader(MessageConst.PROPERTY_KEYS, request.getLinkId());
            rocketMqTemplate.send(destination, builder.build());
            log.info("::{}::消息已转发到数据中心 Topic:{},request:{}", request.getLinkId(), toTopic, JSON.toJSONString(request));
            return;
        }
        String linkId = UUID.randomUUID() + "_unbind_aomatch_data";
        JSONObject data = request.getData();
        //解绑数据源
        String dataSourceCode = data.getString("unbindDataSourceCode");
        //标准赛事ID
        String unbindTargetMatchId = data.getString("unbindTargetMatchId");
        List<String> delKey = new ArrayList<>();
        Set<String> dataSourceCodes = new HashSet<>();
        List<String> internalCodes = Constant.DATA_SOURCE_CODE_INTERNAL.get(dataSourceCode);
        if (CollectionUtils.isEmpty(internalCodes)) {
            delKey.add(Constant.REDIS_KEY.THIRD_ALL_MARKET_HEAD + unbindTargetMatchId + "_" + dataSourceCode);
        } else {
            internalCodes.forEach(internalCode -> {
                delKey.add(Constant.REDIS_KEY.THIRD_ALL_MARKET_HEAD + unbindTargetMatchId + "_" + internalCode);
            });
        }
        redisService.del(delKey);
        log.info("::{}::赛事解除绑定,AO赛事ID：{}，解绑数据源:{}，删除key :{} , ", linkId, unbindTargetMatchId, dataSourceCode, delKey);
    }
}
