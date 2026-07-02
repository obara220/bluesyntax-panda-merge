package com.panda.merge.rocketmq.consumer;


import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.annotation.ConsumerSwitch;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.MatchAssociationMessage;
import com.panda.merge.rocketmq.processor.ThirdMatchRefreshCacheProcessor;
import com.panda.merge.rocketmq.producer.DataCenterProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

import static com.panda.merge.constant.ConstantSystem.CONSUME_NONREALTIME_GROUP;
import static com.panda.merge.constant.ConstantSystem.MATCH_ASSOCIATION_ROUTER;

/**
 * 赛程项目操作【三方赛事标记相反 或者 取消标记相反】通知刷新缓存
 * @author :  tell
 * @since 2020年12月1日16:05:11
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = MATCH_ASSOCIATION_ROUTER,
        consumerGroup = CONSUME_NONREALTIME_GROUP + MATCH_ASSOCIATION_ROUTER,
        consumeThreadMax = 128,
        consumeTimeout = 10000L
)
@DependsOn("nonrealtimeAdminApplication")
public class ThirdMatchOppositeRefreshConsumer implements RocketMQListener<List<MatchAssociationMessage>> {

    @Autowired
    private ThirdMatchRefreshCacheProcessor thirdMatchRefreshCacheProcessor;
    @NacosValue(value = "${consumer.switch.realtime_nonrealtime:true}", autoRefreshed = true)
    private boolean realtimeSwitch;
    @Resource
    private DataCenterProducer<List<MatchAssociationMessage>> dataCenterProducer;

    @Override
    public void onMessage(List<MatchAssociationMessage> refreshCacheList) {
        if (!realtimeSwitch) {
            dataCenterProducer.send(refreshCacheList,MATCH_ASSOCIATION_ROUTER,"ASSOCIATION","ASSOCIATION");
            return;
        }
        if(!CollectionUtils.isEmpty(refreshCacheList)){
            Request<List<MatchAssociationMessage>> request = new Request<>();
            request.setLinkId(String.valueOf(UUIdUtils.getId()));
            request.setData(refreshCacheList);
            log.info("::{}::"+MATCH_ASSOCIATION_ROUTER+"【赛程项目操作】【三方赛事标记相反 或者 取消标记相反】通知刷新缓存，传入参数：{}",request.getLinkId(), JSON.toJSONString(refreshCacheList));
            thirdMatchRefreshCacheProcessor.matchListRefreshCache(request);
            log.info("::{}::"+MATCH_ASSOCIATION_ROUTER+"【赛程项目操作】【三方赛事标记相反 或者 取消标记相反】通知刷新缓存结束",request.getLinkId());
        }

    }
}
