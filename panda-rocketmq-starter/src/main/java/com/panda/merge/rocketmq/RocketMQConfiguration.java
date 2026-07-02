package com.panda.merge.rocketmq;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.AccessChannel;
import org.apache.rocketmq.client.MQAdmin;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.spring.autoconfigure.*;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQMessageConverter;
import org.apache.rocketmq.spring.support.RocketMQUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.util.Assert;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

/**
 * RocketMQConfiguration
 *
 * @description: mq配置类支持多cluster接入
 * @date: 2/9/2025
 **/
@Slf4j
@Configuration
@EnableConfigurationProperties(value = {RocketMQProperties.class,RocketMQSecondConfig.class})
@ConditionalOnClass({MQAdmin.class})
@ConditionalOnProperty(prefix = "rocketmq", value = "name-server", matchIfMissing = true)
@Import({MessageConverterConfiguration.class, ListenerContainerConfiguration.class,
        ExtProducerResetConfiguration.class, RocketMQSecondListenerBeanPostProcessor.class})
@AutoConfigureAfter({MessageConverterConfiguration.class})
@AutoConfigureBefore({RocketMQTransactionConfiguration.class})
public class RocketMQConfiguration  {


    private static final String SECOND_PRODUCER_INSTANCE_NAME = "default-second";

    @Bean(name = "firstProducer")
    @Primary
    @ConditionalOnProperty(prefix = "rocketmq", value = {"name-server", "producer.group"})
    public DefaultMQProducer firstProducer(RocketMQProperties rocketMQProperties)
            throws InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        log.info("RocketMQConfiguration creating firstProducer");
        DefaultMQProducer producer = createProducer(rocketMQProperties);
        log.info("RocketMQConfiguration finished creating firstProducer");
        return producer;
    }

    @Bean(name = "secondProducer")
    @ConditionalOnProperty(value = {"slaveNamesrvAddr", "slaveProducerGroup"})
    public DefaultMQProducer secondMQProducer(RocketMQProperties rocketMQProperties,
                                              RocketMQSecondConfig rocketMQSecondConfig)
            throws InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        log.info("RocketMQConfiguration creating second producer");
        RocketMQProperties copyProperties = (RocketMQProperties) BeanUtils.cloneBean(rocketMQProperties);
        RocketMQProperties.Producer producerConfig =
                (RocketMQProperties.Producer) BeanUtils.cloneBean(rocketMQProperties.getProducer());
        producerConfig.setEnableMsgTrace(false);
        producerConfig.setGroup(Optional.ofNullable(rocketMQSecondConfig.getSlaveProducerGroup()).orElse(SECOND_PRODUCER_INSTANCE_NAME) );
        copyProperties.setProducer(producerConfig);
        copyProperties.setNameServer(rocketMQSecondConfig.getSlaveNamesrvAddr());
        DefaultMQProducer secondProducer = createProducer(copyProperties);
        secondProducer.setNamesrvAddr(rocketMQSecondConfig.getSlaveNamesrvAddr());
        secondProducer.setInstanceName(
                StringUtils.isEmpty(rocketMQSecondConfig.getSlaveMqInstanceName()) ? SECOND_PRODUCER_INSTANCE_NAME:
                        rocketMQSecondConfig.getSlaveMqInstanceName());
        log.info("RocketMQConfiguration finished creating second producer");
        return secondProducer;
    }

    @Bean(name="firstTemplate",destroyMethod = "destroy")
    @Primary
    @ConditionalOnBean(value = DefaultMQProducer.class, name = "firstProducer")
    public RocketMQTemplate firstTemplate(@Qualifier("firstProducer") DefaultMQProducer mqProducer,
                                             RocketMQMessageConverter rocketMQMessageConverter) {
        log.info("RocketMQConfiguration creating firstTemplate");
        RocketMQTemplate rocketMQTemplate = new RocketMQTemplate();
        rocketMQTemplate.setProducer(mqProducer);
        rocketMQTemplate.setMessageConverter(rocketMQMessageConverter.getMessageConverter());
        log.info("RocketMQConfiguration finished creating firstTemplate");
        return rocketMQTemplate;
    }


    @Bean(name = "secondTemplate", destroyMethod = "destroy")
    @ConditionalOnBean(value = DefaultMQProducer.class, name = "secondProducer")
    public RocketMQTemplate secondTemplate(@Qualifier("secondProducer") DefaultMQProducer mqProducer,
                                           RocketMQMessageConverter rocketMQMessageConverter) {
        log.info("RocketMQConfiguration creating secondTemplate");
        RocketMQTemplate rocketMQTemplate = new RocketMQTemplate();
        rocketMQTemplate.setProducer(mqProducer);
        rocketMQTemplate.setMessageConverter(rocketMQMessageConverter.getMessageConverter());
        log.info("RocketMQConfiguration finished creating secondTemplate");
        return rocketMQTemplate;
    }


    private DefaultMQProducer createProducer(RocketMQProperties rocketMQProperties) {

        RocketMQProperties.Producer producerConfig = rocketMQProperties.getProducer();
        String nameServer = rocketMQProperties.getNameServer();
        String groupName = producerConfig.getGroup();
        Assert.hasText(nameServer, "[rocketmq.name-server] must not be null");
        Assert.hasText(groupName, "[rocketmq.producer.group] must not be null");

        String accessChannel = rocketMQProperties.getAccessChannel();

        String ak = rocketMQProperties.getProducer().getAccessKey();
        String sk = rocketMQProperties.getProducer().getSecretKey();
        boolean isEnableMsgTrace = rocketMQProperties.getProducer().isEnableMsgTrace();
        String customizedTraceTopic = rocketMQProperties.getProducer().getCustomizedTraceTopic();

        DefaultMQProducer producer =
                RocketMQUtil.createDefaultMQProducer(groupName, ak, sk, isEnableMsgTrace, customizedTraceTopic);

        producer.setNamesrvAddr(nameServer);
        if (!org.springframework.util.StringUtils.isEmpty(accessChannel)) {
            producer.setAccessChannel(AccessChannel.valueOf(accessChannel));
        }
        producer.setSendMsgTimeout(producerConfig.getSendMessageTimeout());
        producer.setRetryTimesWhenSendFailed(producerConfig.getRetryTimesWhenSendFailed());
        producer.setRetryTimesWhenSendAsyncFailed(producerConfig.getRetryTimesWhenSendAsyncFailed());
        producer.setMaxMessageSize(producerConfig.getMaxMessageSize());
        producer.setCompressMsgBodyOverHowmuch(producerConfig.getCompressMessageBodyThreshold());
        producer.setRetryAnotherBrokerWhenNotStoreOK(producerConfig.isRetryNextServer());
        return producer;
    }

}
