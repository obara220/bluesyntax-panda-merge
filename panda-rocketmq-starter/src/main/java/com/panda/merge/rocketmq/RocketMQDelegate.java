package com.panda.merge.rocketmq;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * RocketMQDelegate
 *
 * @description: rocketmq代理 根据配置属性动态选择mq集群
 * @date: 2/11/2025
 **/
@Slf4j
@Component
public class RocketMQDelegate implements InitializingBean {

    @Autowired
    private RocketMQTemplate firstTemplate;

    @Autowired(required = false)
    @Qualifier(value = "secondTemplate")
    private RocketMQTemplate secondTemplate;

    @Autowired
    private RocketMQSecondProperties secondProperties;

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("RocketMQDelegate init finished");
        log.info("secondTemplate null check :{}", Objects.isNull(secondTemplate));
        log.info("secondProperties:{}", secondProperties);
    }

    public void asyncSend(String destination, Message<?> message, SendCallback sendCallback) {
        asyncSend(destination, message, sendCallback, null);
    }

    public void send(String destination, Message<?> message) {
        send(destination, message, null);
    }

    public void asyncSend(String destination, Message<?> message, SendCallback sendCallback, Long matchId) {
        RocketMQTemplate template = getTemplate(matchId, destination, message);
        template.asyncSend(destination, message, sendCallback);
    }


    public void send(String destination, Message<?> message, Long matchId) {
        RocketMQTemplate template = getTemplate(matchId,destination,message);
        template.send(destination, message);
    }

    private RocketMQTemplate getTemplate(Long matchId, String destination, Message<?> message) {
        int cluster = getCluster(matchId);
        switch (cluster) {
            case 1:
                logDelegate(matchId, destination, message, "first");
                return firstTemplate;
            case 2:
                logDelegate(matchId, destination, message, "second");
                return secondTemplate;
            default:
                logDelegate(matchId, destination, message, "default");
                return firstTemplate;
        }
    }

    private static void logDelegate(Long matchId, String destination, Message<?> message, String cluster) {
        log.info("rocketmq delegate: matchId:{},destination:{},template:{}, message:{} ",
                 matchId,
                 destination,
                 cluster,
                 message.getHeaders());
    }

    private int getCluster(Long matchId) {
        if (Objects.isNull(secondTemplate)) {
            return 1;
        }
        Integer clusterSwitch = secondProperties.getClusterSwitch();
        if (Objects.isNull(clusterSwitch) || clusterSwitch == 1) {
            return 1;
        }
        List<Long> matchIds = secondProperties.getMatchIds();
        if (CollectionUtils.isEmpty(matchIds)) {
            return 2;
        }
        if (Objects.isNull(matchId)) {
            return 1;
        }
        return matchIds.contains(matchId) ? 2 : 1;
    }

}
