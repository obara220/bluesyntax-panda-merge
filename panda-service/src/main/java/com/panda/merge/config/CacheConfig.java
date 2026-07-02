package com.panda.merge.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.util.concurrent.TimeUnit;

/**
 * @name: CacheConfig
 * @description: 缓存配置
 * @date: 1/11/2025
 **/
@Configuration
public class CacheConfig {

    @Primary
    @Bean
    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory,
                                          RedisCacheConfiguration cacheConfiguration) {
        return RedisCacheManager.builder(redisConnectionFactory)
                                .cacheDefaults(cacheConfiguration)
                                .build();
    }

    @Bean(name = "localCacheManager")
    public CacheManager localCacheManager() {
        return new ConcurrentMapCacheManager();
    }

    @Bean(name = "localCacheExpireManager")
    public CacheManager localCacheExpireManager() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setCaffeine(Caffeine.newBuilder().expireAfterAccess(1, TimeUnit.DAYS));
        return caffeineCacheManager;
    }

}
