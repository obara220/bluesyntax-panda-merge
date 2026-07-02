package com.panda.merge.rocketmq.consumer;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.annotation.ConsumerSwitch;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.ConstantSystem;
import com.panda.merge.dto.Request;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.rocketmq.producer.DataCenterProducer;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.service.StandardSportMarketSellService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * 根据标准赛事刷新标准赛事相关缓存
 * @author :  darwinxi
 * @since 2025年3月10日
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = STANDARD_MATCH_BATCH_REFRESH,
        consumerGroup = CONSUME_NONREALTIME_GROUP + STANDARD_MATCH_BATCH_REFRESH,
        consumeThreadMax = 128,
        consumeTimeout = 10000L
)
@DependsOn("nonrealtimeAdminApplication")
public class StandardMatchBatchRefreshCacheConsumer implements RocketMQListener<Request<List<Long>>> {

    @Autowired
    private StandardMatchInfoService standardMatchInfoService;
    @Autowired
    private StandardSportMarketSellService standardSportMarketSellService;
    @Autowired
    public RedisService redisService;
    @NacosValue(value = "${consumer.switch.realtime_nonrealtime:true}", autoRefreshed = true)
    private boolean realtimeSwitch;
    @Resource
    private DataCenterProducer<List<Long>> dataCenterProducer;

    @Override
    public void onMessage(Request<List<Long>> request) {
        if (!realtimeSwitch) {
            dataCenterProducer.send(request,STANDARD_MATCH_BATCH_REFRESH);
            return;
        }
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ STANDARD_MATCH_BATCH_REFRESH+"】【::"+request.getLinkId()+"::】根据标准赛事ID批量刷新赛事缓存开始，传入参数：{}", JSONUtil.toJsonStr(request.getData()));
        if (CollectionUtil.isEmpty(request.getData())) {
            return;
        }
        //需要的刷新标准赛事
        List<StandardMatchInfo> standardMatchInfos = standardMatchInfoService.getItems(request.getData());
        if (CollectionUtil.isEmpty(standardMatchInfos)) {
            log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ STANDARD_MATCH_BATCH_REFRESH+"】【::"+request.getLinkId()+"::】需要的刷新标准赛事为空！");
            return;
        }
        //需要刷新的开售信息标准赛事ID
        List<Long> standardIds = new ArrayList<>();
        for (StandardMatchInfo standardMatchInfo : standardMatchInfos) {
            if (StringUtils.isBlank(standardMatchInfo.getMatchManageId())) {
                continue;
            }
            standardIds.add(standardMatchInfo.getId());
            //刷新开赛时间缓存
            String matchBeginKey = Constant.REDIS_KEY.RONGHE_THIRD_PER_MARKET;
            String updatedKey = redisService.genNewHashKey(matchBeginKey, standardMatchInfo.getId().toString(), ConstantSystem.BUCKET_QUANTITY_SIXTY_FOUR);
            redisService.hSet(updatedKey, standardMatchInfo.getId().toString(), standardMatchInfo.getBeginTime(),Integer.MAX_VALUE);
        }
        if (CollectionUtil.isEmpty(standardIds)) {
            log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ STANDARD_MATCH_BATCH_REFRESH+"】【::"+request.getLinkId()+"::】需要刷新的开售信息为空！");
            return;
        }
        //刷新开售缓存
        standardSportMarketSellService.getItems(standardIds);
        stopWatch.stop();
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ STANDARD_MATCH_BATCH_REFRESH+"】【::"+request.getLinkId()+"::】根据标准赛事ID批量刷新赛事缓存结束,耗时:{} 毫秒,刷新标准缓存{}条,刷新开售缓存{}条",stopWatch.getTotalTimeMillis(),standardMatchInfos.size(),standardIds.size());
    }
}
