package com.panda.merge.mq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.CommonConstant;
import com.panda.merge.dto.Request;
import com.panda.merge.mq.dto.FlowControlDto;
import com.panda.merge.mq.producer.CommonProducer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


@Slf4j
@Component
@RocketMQMessageListener(
        topic = "FLOW_CONTROL_NOTIFICATION",
        consumerGroup = "settle-group-FLOW_CONTROL_NOTIFICATION",
        consumeTimeout = 10000L
)
@DependsOn("settleScoresAdminApplication")
public class FlowControlConsumer implements RocketMQListener<Request<FlowControlDto>> {

    @Autowired
    RedisService redisService;

    //是否使用数据中心接口
    @NacosValue(value = "${datacenter.settle.switch}", autoRefreshed = true)
    private Boolean datacenterSettleSwitch;


    private static Set<Long> limitedMatchIds = new HashSet();

    private static boolean FLAG = true;

    @Autowired
    CommonProducer commonProducer;

    @SneakyThrows
    @Override
    public void onMessage(Request<FlowControlDto> request) {
        if(datacenterSettleSwitch){
            commonProducer.asyncSend(request, "datacenter-FLOW_CONTROL_NOTIFICATION");
            return;
        }
        String linkId = request.getLinkId();
        log.info("::{}::FlowControlConsumer start", linkId);
        String lockerKey = "settle.flow.control.match.ids.locker";
        try{
            //0 开 1 关
            if(request.getData()==null || request.getData().getFlowControlNotificationStatus() == 1 ||
                    CollectionUtils.isEmpty(request.getData().getFlowControlNotificationMatchNotInIds())){
                return;
            }

            //redis锁 防止里面 查询后插入的问题
            if(redisService.tryLock(lockerKey,lockerKey,20,20)) {
                Object rawValue = redisService.get(CommonConstant.SETTLE_FLOW_CONTROL_MATCH_IDS);

                if (rawValue == null) {
                    limitedMatchIds = new HashSet<>();
                } else {
                    limitedMatchIds = JSON.parseObject(rawValue.toString(), Set.class);
                }
                limitedMatchIds.addAll(request.getData().getFlowControlNotificationMatchNotInIds());
                redisService.set(CommonConstant.SETTLE_FLOW_CONTROL_MATCH_IDS, limitedMatchIds, RedisConfig.REDIS_DEFAULT_TIME);
            }else {
                log.info("linkId::{}::三方比分获取redis锁失败",linkId);
                throw new Exception("linkId::{}::三方比分获取redis锁失败");
            }
        } finally {
            redisService.unLock(lockerKey,lockerKey);
        }
        log.info("::{}::FlowControlConsumer end", linkId);
    }

    public Set<Long> getLimitedMatchIds(){
        if (limitedMatchIds.size() == 0) {
            Object rawValue = redisService.get(CommonConstant.SETTLE_FLOW_CONTROL_MATCH_IDS);
            if (rawValue != null) {
                limitedMatchIds = JSON.parseObject(rawValue.toString(), Set.class);
            } else {
                limitedMatchIds = Collections.emptySet();
            }
        }

        return limitedMatchIds;
    }
}
