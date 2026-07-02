package com.panda.merge.rocketmq;


import com.alibaba.nacos.api.config.annotation.NacosConfigurationProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * RocketMQSecondProperties
 *
 * @description: 第二mq集群配置属性 加载顺序优先于其他bean
 * @date: 2/8/2025
 **/
@Data
@ConfigurationProperties(prefix = "")
public class RocketMQSecondConfig {

    private String slaveNamesrvAddr;


    private String slaveProducerGroup;


    private String slaveMqInstanceName;



}
