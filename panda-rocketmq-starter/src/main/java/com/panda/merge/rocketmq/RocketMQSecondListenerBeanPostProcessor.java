package com.panda.merge.rocketmq;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.AccessChannel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.autoconfigure.RocketMQProperties;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQReplyListener;
import org.apache.rocketmq.spring.support.DefaultRocketMQListenerContainer;
import org.apache.rocketmq.spring.support.RocketMQMessageConverter;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;

/**
 * RocketMQSecondClusterBeanPostProcessor
 *
 * @description:
 * @date: 2/9/2025
 **/
@Slf4j
@Component
public class RocketMQSecondListenerBeanPostProcessor
        implements BeanPostProcessor, ApplicationContextAware, EnvironmentAware {

    private ApplicationContext applicationContext;

    private Environment environment;


    private AtomicLong counter = new AtomicLong(0);

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = AopUtils.getTargetClass(bean);
        RocketMQMessageListener ann = targetClass.getAnnotation(RocketMQMessageListener.class);

        EnableSecondRocketMQCluster secAnn = targetClass.getAnnotation(EnableSecondRocketMQCluster.class);
        if (ann != null && secAnn != null) {
            try {
                registerContainer(beanName, bean);
            } catch (Exception e) {
                log.error("Failed to register second listener for bean: {}", beanName, e);
            }
        }
        return bean;
    }

    private void registerContainer(String beanName, Object bean) {

        Class<?> clazz = AopProxyUtils.ultimateTargetClass(bean);
        RocketMQMessageListener annotation = clazz.getAnnotation(RocketMQMessageListener.class);
        String consumerGroup = this.environment.resolvePlaceholders(annotation.consumerGroup());
        String topic = this.environment.resolvePlaceholders(annotation.topic());
        RocketMQProperties rocketMQProperties = applicationContext.getBean(RocketMQProperties.class);
        boolean listenerEnabled = (boolean) rocketMQProperties
                .getConsumer()
                .getListeners()
                .getOrDefault(consumerGroup, Collections.EMPTY_MAP)
                .getOrDefault(topic, true);

        if (!listenerEnabled) {
            log.debug(
                    "Consumer Listener (group:{},topic:{}) is not enabled by configuration, will ignore initialization.",
                    consumerGroup,
                    topic);
            return;
        }

        String containerBeanName = String.format("%s_second_%s",
                                                 DefaultRocketMQListenerContainer.class.getName(),
                                                 counter.incrementAndGet());
        GenericApplicationContext genericApplicationContext = (GenericApplicationContext) applicationContext;
        genericApplicationContext.registerBean(containerBeanName,
                                               DefaultRocketMQListenerContainer.class,
                                               () -> createRocketMQListenerContainer(containerBeanName,
                                                                                     bean,
                                                                                     annotation));
        DefaultRocketMQListenerContainer container =
                genericApplicationContext.getBean(containerBeanName, DefaultRocketMQListenerContainer.class);
        if (!container.isRunning()) {
            try {
                container.start();
            } catch (Exception e) {
                log.error("Started second container failed. {}", container, e);
                throw new RuntimeException(e);
            }
        }

        log.info("Register the second listener to container, listenerBeanName:{}, containerBeanName:{}",
                 beanName,
                 containerBeanName);
    }

    private DefaultRocketMQListenerContainer createRocketMQListenerContainer(String name,
                                                                             Object bean,
                                                                             RocketMQMessageListener annotation) {
        DefaultRocketMQListenerContainer container = new DefaultRocketMQListenerContainer();

        container.setRocketMQMessageListener(annotation);
        RocketMQSecondConfig secondConfig = applicationContext.getBean(RocketMQSecondConfig.class);
        String nameServer = secondConfig.getSlaveNamesrvAddr();
        if (StringUtils.isBlank(nameServer)) {
            throw new IllegalArgumentException("name server can not be null");
        }
        container.setNameServer(nameServer);
        String accessChannel = environment.resolvePlaceholders(annotation.accessChannel());
        if (!StringUtils.isEmpty(accessChannel)) {
            container.setAccessChannel(AccessChannel.valueOf(accessChannel));
        }
        container.setTopic(environment.resolvePlaceholders(annotation.topic()));
        String tags = environment.resolvePlaceholders(annotation.selectorExpression());
        if (!StringUtils.isEmpty(tags)) {
            container.setSelectorExpression(tags);
        }
        container.setConsumerGroup(environment.resolvePlaceholders(annotation.consumerGroup())+"_second");
        if (RocketMQListener.class.isAssignableFrom(bean.getClass())) {
            container.setRocketMQListener((RocketMQListener) bean);
        } else if (RocketMQReplyListener.class.isAssignableFrom(bean.getClass())) {
            container.setRocketMQReplyListener((RocketMQReplyListener) bean);
        }
        RocketMQMessageConverter rocketMQMessageConverter = applicationContext.getBean(RocketMQMessageConverter.class);
        container.setMessageConverter(rocketMQMessageConverter.getMessageConverter());
        container.setName(name);

        return container;
    }

}
