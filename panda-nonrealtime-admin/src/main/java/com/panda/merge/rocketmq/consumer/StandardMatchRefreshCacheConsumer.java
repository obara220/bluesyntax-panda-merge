package com.panda.merge.rocketmq.consumer;


import com.alibaba.fastjson.JSONObject;
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

import javax.annotation.Resource;
import static com.panda.merge.constant.ConstantSystem.CONSUME_NONREALTIME_GROUP;
import static com.panda.merge.constant.ConstantSystem.STANDARD_MATCH_REFRESH;

/**
 * 根据标准赛事刷新标准赛事相关缓存
 * @author :  tell
 * @since 2021年3月9日17:20:26
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = STANDARD_MATCH_REFRESH,
        consumerGroup = CONSUME_NONREALTIME_GROUP + STANDARD_MATCH_REFRESH,
        consumeThreadMax = 128,
        consumeTimeout = 10000L
)
@DependsOn("nonrealtimeAdminApplication")
public class StandardMatchRefreshCacheConsumer implements RocketMQListener<Request<Long>> {

    @Autowired
    private StandardMatchInfoService standardMatchInfoService;
    @Autowired
    private StandardSportMarketSellService standardSportMarketSellService;
    @Autowired
    public RedisService redisService;
    @NacosValue(value = "${consumer.switch.realtime_nonrealtime:true}", autoRefreshed = true)
    private boolean realtimeSwitch;
    @Resource
    private DataCenterProducer<Long> dataCenterProducer;

    @Override
    public void onMessage(Request<Long> request) {
        if (!realtimeSwitch) {
            dataCenterProducer.send(request,STANDARD_MATCH_REFRESH);
            return;
        }
        log.info("::{}::"+STANDARD_MATCH_REFRESH+"根据标准赛事ID{}刷新赛事缓存开始，传入参数：{}",request.getLinkId(), request.getData());
        if(null != request.getData()){
            //刷新标准赛事缓存
            StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItemByPrimaryKey(request.getData());
            log.info("::{}::" + STANDARD_MATCH_REFRESH + "根据标准赛事:{} ", request.getLinkId(), JSONObject.toJSONString(standardMatchInfo));
            if(null != standardMatchInfo && StringUtils.isNotBlank(standardMatchInfo.getMatchManageId())){
                //刷新开售缓存并返回最新开售信息
                standardSportMarketSellService.refreshCache(request.getData());
                //刷新开赛时间缓存
                String matchBeginKey = Constant.REDIS_KEY.RONGHE_THIRD_PER_MARKET;
                String updatedKey = redisService.genNewHashKey(matchBeginKey, standardMatchInfo.getId().toString(), ConstantSystem.BUCKET_QUANTITY_SIXTY_FOUR);
                redisService.hSet(updatedKey, standardMatchInfo.getId().toString(), standardMatchInfo.getBeginTime(),Integer.MAX_VALUE);
            }
            log.info("::{}::"+STANDARD_MATCH_REFRESH+"根据标准赛事ID{}刷新赛事缓存结束",request.getLinkId(), request.getData());
        }
    }
}
