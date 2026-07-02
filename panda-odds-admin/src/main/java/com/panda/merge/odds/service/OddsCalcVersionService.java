package com.panda.merge.odds.service;

import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.message.LocalCacheRefreshMessage;
import com.panda.merge.odds.cache.CacheService;
import com.panda.merge.odds.cache.LocalCacheRefreshProducer;
import com.panda.merge.odds.constants.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * OddsCalcVersionService
 *
 * @description: 赔率计算功能版本切换服务
 * @date: 1/20/2025
 **/
@Slf4j
@Service
public class OddsCalcVersionService implements CacheService, BeanNameAware {

    @Autowired
    private RedisService redisService;

    @Resource(name = "localCacheManager")
    private CacheManager cacheManager;

    @Autowired
    private LocalCacheRefreshProducer localCacheRefreshProducer;

    private String beanName;

    @Override
    public void setBeanName(@NonNull String name) {
        this.beanName = name;
    }

    @Cacheable(cacheManager = "localCacheManager", cacheNames = CacheConstant.ODDS_CALC_VERSION_CACHE,
            key = "'oddsCalcVersion'")
    public int getVersion() {
        Integer version = (Integer) redisService.get(CacheConstant.ODDS_CALC_VERSION_CACHE);
        return Optional.ofNullable(version).map(v -> {
            if (v <= 0) {
                return 0;
            } else {
                return 1;
            }
        }).orElse(0);
    }

    public void setVersion(int version) {
        redisService.set(CacheConstant.ODDS_CALC_VERSION_CACHE, version, 120L, TimeUnit.DAYS);
        log.info("odds calc set version: {}", version);
        LocalCacheRefreshMessage message =
                new LocalCacheRefreshMessage(UUIdUtils.getId().toString(), beanName, null, null);
        localCacheRefreshProducer.send(message);
    }

    @Override
    public void refresh(String key) {
        Cache cache = cacheManager.getCache(CacheConstant.ODDS_CALC_VERSION_CACHE);
        log.info("odds calc cache refresh");
        if (cache != null) {
            cache.clear();
        }
    }

}
