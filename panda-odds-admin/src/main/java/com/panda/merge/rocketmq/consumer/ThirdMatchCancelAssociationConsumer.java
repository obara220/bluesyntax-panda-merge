package com.panda.merge.rocketmq.consumer;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.ConstantSystem;
import com.panda.merge.dto.Request;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;


/**
 * 三方赛事取消关联
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = ConstantSystem.UNBIND_AOMATCH_DATA,
        consumerGroup = "odds-group-" + ConstantSystem.UNBIND_AOMATCH_DATA,
        consumeThreadMax = 20, consumeTimeout = 10000L)
@DependsOn("oddsAdminApplication")

public class ThirdMatchCancelAssociationConsumer implements RocketMQListener<Request<JSONObject>> {

    @Autowired
    private RedisService redisService;

    @Override
    public void onMessage(Request<JSONObject> request) {
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
