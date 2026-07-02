package com.panda.merge.common;

import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.RateLimiterDTO;
import com.panda.merge.dto.RateLimiterThirdMatchDTO;
import com.panda.merge.dto.Request;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.service.ThirdMatchInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.apache.rocketmq.spring.autoconfigure.RocketMQProperties;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.support.MessageBuilder;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.panda.merge.config.RedisConfig.REDIS_FIVE_MINS_TIME;
import static com.panda.merge.config.RedisConfig.REDIS_KEY_DATABASE;
import static com.panda.merge.constant.ConstantSystem.FLOW_CONTROL_NOTIFICATION;
import static com.panda.merge.constant.ConstantSystem.FLOW_CONTROL_NOTIFICATION_FORWARD;

@Slf4j
public class RateLimiterHandler {
    /** 终止限流,恢复正常 */
    private static final Integer RATE_LIMITER_DISABLE = 1;
    private static final Integer RATE_LIMITER_ENABLE = 0;
    private static final String CACHE_KEY = REDIS_KEY_DATABASE + ":RateLimiter:matchNotIn";
    private static final String CACHE_LINK = REDIS_KEY_DATABASE + ":RateLimiter:link";
    private static final String LOCK_KEY = REDIS_KEY_DATABASE + ":RateLimiter:lock";
    private static final String LOCK_ONCE_KEY = REDIS_KEY_DATABASE + ":RateLimiter:lockOnce_%s:%s";

    /** 不需要下发赛事id集合 */
    private final ConcurrentHashSet<RateLimiterThirdMatchDTO> thirdSourceMatchSet = new ConcurrentHashSet<>();

    private RocketMQProperties rocketMQProperties;

    private RocketMQTemplate rocketMQTemplate;

    private RedisService redisService;

    private ThirdMatchInfoService thirdMatchInfoService;

    @NacosValue(value = "${panda.data.realtime.config.rateLimiter.enable:false}", autoRefreshed = true)
    private boolean rateLimiterEnable;

    public RateLimiterHandler(RocketMQProperties rocketMQProperties, RocketMQTemplate rocketMQTemplate, RedisService redisService, ThirdMatchInfoService thirdMatchInfoService) {
        this.rocketMQProperties = rocketMQProperties;
        this.rocketMQTemplate = rocketMQTemplate;
        this.redisService = redisService;
        this.thirdMatchInfoService  = thirdMatchInfoService;
    }

    public void initCache(){
        try {
            boolean flag = redisService.tryLock(LOCK_KEY, LOCK_KEY, 5, 2);
            try {
                Set<Object> objects = redisService.sMembers(CACHE_KEY);
                if (CollectionUtils.isNotEmpty(objects)) {
                    thirdSourceMatchSet.addAll(objects.stream().map(o -> (RateLimiterThirdMatchDTO) o).collect(Collectors.toSet()));
                    log.info("初始化不需要下发赛事赛事id集合,thirdSourceMatchSet={}",JSONUtil.toJsonStr(thirdSourceMatchSet));
                }
            } finally {
                if (flag) {
                    redisService.unLock(LOCK_KEY, LOCK_KEY);
                }
            }
        } catch (Exception e) {
            log.error("init thirdSourceMatchSet Exception",e);
        }
    }

    public void initConsumer(String consumerGroup){
        try {
            DefaultMQPushConsumer broadcastConsumer = new DefaultMQPushConsumer(consumerGroup+FLOW_CONTROL_NOTIFICATION_FORWARD);
            broadcastConsumer.setNamesrvAddr(rocketMQProperties.getNameServer());
            broadcastConsumer.subscribe(FLOW_CONTROL_NOTIFICATION_FORWARD,"*");
            broadcastConsumer.setMessageModel(MessageModel.BROADCASTING);
            log.info("consumer 订阅主题:{},nameServer={}", FLOW_CONTROL_NOTIFICATION_FORWARD,rocketMQProperties.getNameServer());
            broadcastConsumer.registerMessageListener(((List<MessageExt> msgs, ConsumeConcurrentlyContext context) -> {
                try {
                    for (MessageExt msg : msgs) {
                        String linkId = msg.getProperties().get("KEYS");
                        String body = new String(msg.getBody(), StandardCharsets.UTF_8);
                        Request<RateLimiterDTO> request = JSON.parseObject(body, new TypeReference<Request<RateLimiterDTO>>() {});
                        refreshThirdSourceMatchIdSet(request.getData(),linkId);
                    }
                } catch (Exception e) {
                    log.error("FLOW_CONTROL_NOTIFICATION_FORWARD Exception:", e);
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }));
            broadcastConsumer.start();
        } catch (Exception e) {
            log.error("FLOW_CONTROL_NOTIFICATION_FORWARD Exception", e);
        }
    }

    public void initForwardConsumer(String consumerGroup){
        try {
            DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerGroup+FLOW_CONTROL_NOTIFICATION);
            consumer.setNamesrvAddr(rocketMQProperties.getNameServer());
            consumer.subscribe(FLOW_CONTROL_NOTIFICATION,"*");
            log.info("consumer 订阅主题:{},nameServer={}", FLOW_CONTROL_NOTIFICATION,rocketMQProperties.getNameServer());
            consumer.registerMessageListener(((List<MessageExt> msgs, ConsumeConcurrentlyContext context) -> {
                try {
                    for (MessageExt msg : msgs) {
                        String linkId = msg.getProperties().get("KEYS");
                        log.info("【{}】 赛程限流通知透传",linkId);
                        String body = new String(msg.getBody(), StandardCharsets.UTF_8);
                        Request<RateLimiterDTO> request = JSON.parseObject(body, new TypeReference<Request<RateLimiterDTO>>() {});
                        changeLink(request);
                        if (CollectionUtils.isNotEmpty(request.getData().getFlowControlNotificationMatchNotInIds())) {
                            List<ThirdMatchInfo> thirdMatchInfos = thirdMatchInfoService.getItems(request.getData().getFlowControlNotificationMatchNotInIds(), null);
                            List<RateLimiterThirdMatchDTO> rateLimiterThirdMatchDTOs = thirdMatchInfos.stream().map(thirdMatchInfo -> {
                                RateLimiterThirdMatchDTO rateLimiterThirdMatchDTO = new RateLimiterThirdMatchDTO();
                                rateLimiterThirdMatchDTO.setThirdSourceMatchId(thirdMatchInfo.getThirdMatchSourceId());
                                rateLimiterThirdMatchDTO.setDataSourceCode(thirdMatchInfo.getDataSourceCode());
                                return rateLimiterThirdMatchDTO;
                            }).collect(Collectors.toList());
                            request.getData().setFlowControlNotificationMatchNotIns(rateLimiterThirdMatchDTOs);
                        }
                        send(request.getData(),linkId,FLOW_CONTROL_NOTIFICATION_FORWARD,linkId);
                    }
                } catch (Exception e) {
                    log.error("FLOW_CONTROL_NOTIFICATION Exception:", e);
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }));
            consumer.start();
        } catch (Exception e) {
            log.error("FLOW_CONTROL_NOTIFICATION Exception", e);
        }
    }

    private void changeLink(Request<RateLimiterDTO> request) {
        Object currentLinkId = redisService.get(CACHE_LINK);
        redisService.set(CACHE_LINK, request.getLinkId());
        if (currentLinkId == null || !request.getLinkId().equals((String)currentLinkId)) {
            request.getData().changeLink = true;
            log.info("【{}】赛程限流通知linkId发生改变,old={},new={}",request.getLinkId(),currentLinkId,request.getLinkId());
        }
    }

    public boolean filter(String thirdSourceMatchId,String dataSourceCode){
        if (!rateLimiterEnable) {
            return true;
        }
        RateLimiterThirdMatchDTO thirdMatchObject = new RateLimiterThirdMatchDTO();
        thirdMatchObject.setThirdSourceMatchId(thirdSourceMatchId);
        thirdMatchObject.setDataSourceCode(dataSourceCode);
        if (thirdSourceMatchSet.contains(thirdMatchObject)) {
            return false;
        }
        return true;
    }

    public boolean filter(){
        if (!rateLimiterEnable) {
            return true;
        }
        if (CollectionUtils.isNotEmpty(thirdSourceMatchSet)) {
            return false;
        }
        return true;
    }

    /** 更新缓存 */
    private void refreshThirdSourceMatchIdSet(RateLimiterDTO rateLimiterObject, String linkId){
        log.info("【{}】 刷新不需要下发赛事赛事id集合={}",linkId, JSONUtil.toJsonStr(rateLimiterObject));
        boolean flag = redisService.tryLock(LOCK_KEY, LOCK_KEY, 5, 2);
        try {
            if (RATE_LIMITER_DISABLE.equals(rateLimiterObject.flowControlNotificationStatus) || rateLimiterObject.changeLink) {
                thirdSourceMatchSet.clear();
                String lockKey = String.format(LOCK_ONCE_KEY, "clear", linkId);
                if(redisService.tryLockOnce(lockKey,lockKey,REDIS_FIVE_MINS_TIME)){
                    redisService.del(CACHE_KEY);
                }
            }
            if (RATE_LIMITER_ENABLE.equals(rateLimiterObject.flowControlNotificationStatus)) {
                thirdSourceMatchSet.addAll(rateLimiterObject.getFlowControlNotificationMatchNotIns());
                String lockKey = String.format(LOCK_ONCE_KEY, "addAll", rateLimiterObject.flowControlNotificationUnique);
                if(redisService.tryLockOnce(lockKey,lockKey,REDIS_FIVE_MINS_TIME)){
                    redisService.sAdd(CACHE_KEY, ArrayUtil.toArray(rateLimiterObject.getFlowControlNotificationMatchNotIns(),RateLimiterThirdMatchDTO.class));
                }
            }
        } finally {
            if (flag) {
                redisService.unLock(LOCK_KEY, LOCK_KEY);
            }
        }
        log.info("【{}】 刷新不需要下发赛事赛事id集合,thirdSourceMatchSet={}",linkId,JSONUtil.toJsonStr(thirdSourceMatchSet));
    }


    private boolean send(RateLimiterDTO data,String linkId,String topic,String tag) {
        boolean result = false;
        log.info("【"+linkId+"】【"+topic+"】数据同步推送MQ开始,tag={}",tag);
        Request<RateLimiterDTO> request = new Request<>(data, linkId);
        request.setTag(tag);
        try {
            MessageBuilder<Request<RateLimiterDTO>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, request.getLinkId());
            SendResult sendResult = rocketMQTemplate.syncSend(topic + ":" + request.getTag(), builder.build());
            if (SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
                result = true;
            }
        } catch (Exception e) {
            log.info("【"+linkId+"】【"+topic+"】数据同步推送MQ异常,Exception:", e);
        }
        log.info("【"+linkId+"】【"+topic+"】数据同步推送MQ结束,result={}",result);
        return result;
    }

}
