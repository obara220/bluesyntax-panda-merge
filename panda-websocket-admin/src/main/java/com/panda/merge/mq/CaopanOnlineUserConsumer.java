package com.panda.merge.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.panda.merge.cache.MyCacheService;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.RedisKeyConstant;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.request.OperatorOnlineCatchVo;
import com.panda.merge.dto.request.OperatorOnlineVo;
import com.panda.merge.dto.response.OnlineResponseVo;
import com.panda.merge.handler.PDSubcribe;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@Service
@RocketMQMessageListener(topic = "CAOPAN_ONLINE_STATUS_INFO", consumerGroup = "CAOPAN_ONLINE_STATUS_INFO_MERGE_GROUP", consumeThreadMax = 10,
        consumeTimeout = 10000L,
        messageModel = MessageModel.BROADCASTING)
@DependsOn("mergeWebSocketApplication")
public class CaopanOnlineUserConsumer implements RocketMQListener<OperatorOnlineVo> {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void onMessage(OperatorOnlineVo onlineVo) {
        log.info("operatorOnlineMessage:{}", JSON.toJSON(onlineVo));
        Integer userId = onlineVo.getUserId();
        Integer status = onlineVo.getLoginStatus();
        if (userId == null || status == null) {
            log.info("操盘手在线状态消费,参数错误:{}", JSON.toJSON(onlineVo));
            return;
        }
        //登入
        if (status == 0) {
            redisTemplate.opsForSet().add(RedisKeyConstant.CAO_PAN_ONLINE, userId);
        } else {
            if (redisTemplate.opsForSet().isMember(RedisKeyConstant.CAO_PAN_ONLINE,userId)) {
                redisTemplate.opsForSet().remove(RedisKeyConstant.CAO_PAN_ONLINE, userId);
            }
        }
        sendOperatorOnline(onlineVo);
    }

    public void sendOperatorOnline(OperatorOnlineVo onlineVo){
        for (OperatorOnlineCatchVo value : MyCacheService.sessionOperatorOnlineMap.values()) {
            log.info("::{}::, 推送value信息：{}",onlineVo.getLinkId(), JSON.toJSONString(value));
            try {
                if (value.getSession().isOpen()) {
                    if (value.getUserIds().contains(onlineVo.getUserId())) {
                        log.info("::{}::,  后台推送操盘手信息:{}", onlineVo.getLinkId(), JSONObject.toJSONString(onlineVo, SerializerFeature.WriteMapNullValue));
                        OnlineResponseVo onlineResponseVo = new OnlineResponseVo();
                        onlineResponseVo.setData(onlineVo);
                        value.getSession().sendText(JSONObject.toJSONString(onlineResponseVo));
                        log.info("::{}::, 推送操盘手信息, {}", onlineVo.getLinkId(), JSON.toJSONString(value));
                    }
                }
            } catch (Exception e){
                log.error("::{}::, sendOperatorOnline::",onlineVo.getLinkId(), e);
            }
        }
    }
}
