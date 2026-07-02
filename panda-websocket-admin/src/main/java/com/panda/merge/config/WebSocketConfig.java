package com.panda.merge.config;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.panda.merge.service.IScoresCenterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.yeauty.standard.ServerEndpointExporter;
//import org.springframework.web.socket.server.standard.ServerEndpointExporter;
//import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @project Name :  panda_data_service
 * @package Name :  com.panda.sports.manager.realtime.config
 * @description :  TODO
 * --------  ---------  --------------------------
 */

@Configuration
@Slf4j
public class WebSocketConfig {
    /**
     * 连接空闲时长
     */
    private static Long MAX_TIME_OUT = 30 * 60 * 1000L;
    @Autowired
    IScoresCenterService scoresCenterService;
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

//    @Bean
//    public ServletServerContainerFactoryBean createServletServerContainerFactoryBean() {
//        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
//        container.setMaxTextMessageBufferSize(65536);
//        container.setMaxSessionIdleTimeout (MAX_TIME_OUT);
//
//        return container;
//    }

    /**
     * 必须要指定这个Bean，refreshAfterWrite=5s这个配置属性才生效
     *
     * @return
     */
    @Bean
    public CacheLoader<Object, Object> cacheLoader() {
        CacheLoader<Object, Object> cacheLoader = new CacheLoader<Object, Object>() {

            @Override
            public Object load(Object key) throws Exception {
                log.info("cacheLoader:xxx");
                return null;
            }
        };
        return cacheLoader;
    }

    @Bean
    public CacheManager cacheManager( ) {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        List<CaffeineCache> list = new ArrayList<>();
        try{
            list.add(new CaffeineCache("GET:SCORE",
                    Caffeine.newBuilder()
                            .initialCapacity(3000)
                            .maximumSize(20000)
                            .expireAfterWrite(5, TimeUnit.SECONDS)
                            .softValues()
                            .build()));

            cacheManager.setCaches(list);
        }catch (Exception e){
            e.getStackTrace();
            log.error("cacheManager:",e);
        }

        return cacheManager;
    }
}
