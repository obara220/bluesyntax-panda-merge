package com.panda.merge.config.listener.base;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.AbstractListener;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Hunta
 * @since 9/13/2023
 *
 * 监听nacos变化并且获取配置之前的值和现在的值
 *
 */


@Slf4j
@Component
public class NacosChangeProcessor implements InitializingBean {

    // 现有项目都是app名称作为nacosDataId
    @Value("${spring.application.name}")
    private String nacosDataId;

    @Value("${spring.profiles.active}")
    private String springProfile;

    @NacosInjected
    private ConfigService configService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private List<NacosListener> nacosListenerList;

    private ExecutorService listenerCallbackThreadPool = Executors.newCachedThreadPool();

    private static final String NAOCS_LISTENER_KEY="NACOS_LISTENER";

    @Override
    public void afterPropertiesSet() throws Exception {
        configService.addListener(nacosDataId, Constants.DEFAULT_GROUP, new AbstractListener() {
            @Override
            public void receiveConfigInfo(String fullConfig) {
                // 这里拿到的config是整个配置文件，所以需要解析成一个map
                Map<String, String> afterConfigMap = parseConfig(fullConfig);
                Map<Object, Object> beforeConfigMap = getBeforeConfigFromRedis();
                Map<String, NacosChangeEvent> nacosChangeEventMap = getChangedNacos(afterConfigMap, beforeConfigMap);
                log.info("nacos change detected,nacosChangeEventMap:{}",nacosChangeEventMap);
                triggerNacosChangeListener(nacosChangeEventMap);
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                // 把update放到最后，并且sleep一下，避免多台机器之间redis配置读写互相覆盖。
                updateConfigInRedis(afterConfigMap);
            }
        });
    }

    private void updateConfigInRedis(Map<String, String> afterConfigMap) {
        stringRedisTemplate.opsForHash().putAll(getNacosConfigKey(),afterConfigMap);
    }

    private Map<Object, Object> getBeforeConfigFromRedis() {
        return stringRedisTemplate.opsForHash().entries(getNacosConfigKey());
    }

    private String getNacosConfigKey() {
        return NAOCS_LISTENER_KEY+":"+nacosDataId+":"+springProfile;
    }

    private void triggerNacosChangeListener(Map<String, NacosChangeEvent> nacosChangeEventMap) {
        if(MapUtils.isEmpty(nacosChangeEventMap)){
            return;
        }
        if(CollectionUtils.isNotEmpty(nacosListenerList)){
            nacosListenerList.forEach(listener->{
                String key = listener.nacosKeyToListen();
                NacosChangeEvent nacosChangeEvent = nacosChangeEventMap.get(key);
                    if(nacosChangeEvent!=null){
                       listenerCallbackThreadPool.execute(() -> {
                            try {
                                listener.onChange(nacosChangeEvent);
                            }catch (Exception e){
                                log.error("NacosChangeProcessor error, nacos key:{}",listener.nacosKeyToListen(),e);
                            }
                        });
                    }
            });
        }
    }

    private static Map<String, NacosChangeEvent> getChangedNacos(Map<String, String> afterConfigMap, Map<Object, Object> beforeConfigMap) {
        Map<String,NacosChangeEvent> nacosChangeEventMap=new HashMap<>();

        MapDifference<Object, Object> differenceMap = Maps.difference(beforeConfigMap, afterConfigMap);
        //value值改变的key
        Set<Object> valueChangedKeys=  differenceMap.entriesDiffering().keySet();
        //删掉的key
        Set<Object> deletedKeys = differenceMap.entriesOnlyOnLeft().keySet();
        //新增的key
        Set<Object> addedKeys = differenceMap.entriesOnlyOnRight().keySet();

        Set<Object> allChangedKeys=new HashSet<>();
        allChangedKeys.addAll(valueChangedKeys);
        allChangedKeys.addAll(deletedKeys);
        allChangedKeys.addAll(addedKeys);

        allChangedKeys.forEach(key->{
            NacosChangeEvent nacosChangeEvent = buildNacosEvent(afterConfigMap, beforeConfigMap, key);
            nacosChangeEventMap.put(key.toString(),nacosChangeEvent);
        });
        return nacosChangeEventMap;
    }

    private static NacosChangeEvent buildNacosEvent(Map<String, String> afterConfigMap, Map<Object, Object> beforeConfigMap, Object key) {
        String beforeValue = beforeConfigMap.get(key) == null ? "" : beforeConfigMap.get(key).toString();
        String afterValue = afterConfigMap.get(key) == null ? "" : afterConfigMap.get(key).toString();
        return new NacosChangeEvent(beforeValue,afterValue);
    }

    private static  Map<String,String> parseConfig(String fullConfig) {
        Map<String,String> configMap =new HashMap<>();
        if(fullConfig !=null){
            String[] configArray = fullConfig.split(System.lineSeparator());
            for(String config:configArray){
                if(config==null ||config.startsWith("#") || config.contains("=")==false){
                    continue;
                }
                String[] keyValue = config.split("=");
                if(StringUtils.isNotBlank(keyValue[0]) && StringUtils.isNotBlank(keyValue[1])){
                    configMap.put(keyValue[0].trim(),keyValue[1].trim());
                }
            }
        }
        return configMap;
    }
}
