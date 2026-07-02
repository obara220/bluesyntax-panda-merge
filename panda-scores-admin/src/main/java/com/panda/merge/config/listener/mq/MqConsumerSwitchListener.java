package com.panda.merge.config.listener.mq;

import com.google.common.base.Splitter;
import com.panda.merge.config.listener.base.NacosChangeEvent;
import com.panda.merge.config.listener.base.NacosListener;
import com.panda.merge.mq.consumer.AbstractMQConsumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Hunta
 * @since 9/12/2023
 */

@Component
@Slf4j
public class MqConsumerSwitchListener implements NacosListener {

    // nacos里配置的rocketMQ consumer开关，默认允许consumer消费. tag结合生效
    // 例子1: rocketmq.consumer.switch ={blue:on}
    // 例子2: rocketmq.consumer.switch ={blue:off,green:on,yellow:off}
    public static final String CONSUMER_SWITCH_KEY ="rocketmq.consumer.tag.switch";

    //指定机器环境的，机器启动的时候的jvm参数的key
    public static final String MQ_JVM_ARGUMENT_KEY ="mqEnvironmentTag";

    public static final String CONSUMER_ON ="on";
    public static final String CONSUMER_OFF ="off";


    @Autowired
    List<AbstractMQConsumer> consumerHolderList;

    @Override
    public String nacosKeyToListen() {
        return CONSUMER_SWITCH_KEY;
    }

    @Override
    public void onChange(NacosChangeEvent nacosChangeEvent) {
        System.out.println("---------MqConsumerSwitchListener activated!----------------");
        if(isSwitchOn(nacosChangeEvent)){
            log.info("MqConsumerSwitchListener nacos change detected! Consumers restarting...");
            restartConsumers();
        } else if (isSwitchOff(nacosChangeEvent)){
            log.info("MqConsumerSwitchListener nacos change detected! Consumers switching off...");
            switchOffConsumers();
        }else{
           log.info("MqConsumerSwitchListener nacos change detected, but no action. " +
                   "current tag is:{},nacosChangeEvent:{}",getCurrentTag(),nacosChangeEvent);
        }
    }

    private void switchOffConsumers() {
        // 并行关闭线程池，避免一个一个关闭，耗时过长
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        List<Future> futureList=new ArrayList<>();

        if (CollectionUtils.isNotEmpty(consumerHolderList)) {
            consumerHolderList.forEach(consumerHolder -> {
                Map<String, String> consumerSubscription = consumerHolder.getConsumer().getSubscription();
                Future<?> future = executorService.submit(() -> {
                    try {
                        consumerHolder.getConsumer().shutdown();
                        log.info("MqConsumerSwitchListener, consumerHolder restarted successful! subscription:{}", consumerSubscription);
                    } catch (Exception e) {
                        log.error("MqConsumerSwitchListener, detected nacos change, but failed to restart consumerHolder, subscription:{}", consumerSubscription,e);
                    }
                });
                futureList.add(future);
            });
        }
        futureList.forEach(future -> {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
        log.info("MqConsumerSwitchListener, all consumers switch off successful!");
    }

    private void restartConsumers() {
        if(CollectionUtils.isNotEmpty(consumerHolderList)){
            consumerHolderList.forEach(consumerHolder->{
                Map<String, String> consumerSubscription = consumerHolder.getConsumer().getSubscription();
                try {
                    consumerHolder.restartConsumer();
                }catch (Exception e){
                    log.error("MqConsumerSwitchListener, detected nacos change, but failed to restart consumer, subscription:{}", consumerSubscription,e);
                }
                log.info("MqConsumerSwitchListener, consumer restarted successful! subscription:{}", consumerSubscription);
            });
        }
    }

    private boolean isSwitchOff(NacosChangeEvent nacosChangeEvent) {
        Pair<String, String> beforeAfterPair = extractBeforeAfterValue(nacosChangeEvent);
        // 之前是开或者没有配置， 现在是关，那么关闭
        return
                (CONSUMER_ON.equals(beforeAfterPair.getLeft()) || StringUtils.isEmpty(beforeAfterPair.getLeft()))
                && CONSUMER_OFF.equals(beforeAfterPair.getRight());
    }

    private boolean isSwitchOn(NacosChangeEvent nacosChangeEvent) {
        Pair<String, String> beforeAfterPair = extractBeforeAfterValue(nacosChangeEvent);
        return CONSUMER_OFF.equals(beforeAfterPair.getLeft()) && CONSUMER_ON.equals(beforeAfterPair.getRight());
    }

    private  Pair<String, String> extractBeforeAfterValue(NacosChangeEvent nacosChangeEvent){
        String valueBefore = nacosChangeEvent.getValueBefore();
        String valueAfter = nacosChangeEvent.getValueAfter();
        Map<String,String> valueBeforeMap=extractSwitchConfig(valueBefore);
        Map<String,String> valueAfterMap=extractSwitchConfig(valueAfter);
        String beforeStatus = valueBeforeMap.get(getCurrentTag());
        String afterStatus = valueAfterMap.get(getCurrentTag());
        return Pair.of(beforeStatus,afterStatus);
    }

    private Map<String, String> extractSwitchConfig(String string) {
        string = string.trim().replace("{","").replace("}","");
        return Splitter.on(',').withKeyValueSeparator(":").split(string);
    }

    private String getCurrentTag(){
        return System.getProperty(MQ_JVM_ARGUMENT_KEY);
    }
}
