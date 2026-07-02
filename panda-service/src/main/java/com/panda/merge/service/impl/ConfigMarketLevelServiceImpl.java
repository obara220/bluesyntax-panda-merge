package com.panda.merge.service.impl;

import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.mapper.ConfigMarketLevelMapper;
import com.panda.merge.model.ConfigMarketLevel;
import com.panda.merge.model.ConfigMarketLevelExample;
import com.panda.merge.service.ConfigMarketLevelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class ConfigMarketLevelServiceImpl implements ConfigMarketLevelService {
    @Autowired
    ConfigMarketLevelMapper configMarketLevelMapper;

    @Autowired
    private RedisService redisService;

    @Cacheable(key = "'ConfigMarketLevel:'+ #sportId + '-' +#level",unless = "#result == null || #result.size() == 0")
    @Override
    public List<ConfigMarketLevel> getItemLevel(Long sportId,Integer level) {
        ConfigMarketLevelExample configMarketLevelExample = new ConfigMarketLevelExample();
        configMarketLevelExample.createCriteria().andSportIdEqualTo(sportId).andTournamentLevelEqualTo(level);
        List<ConfigMarketLevel> configMarketLevels = configMarketLevelMapper.selectByExample(configMarketLevelExample);
        return configMarketLevels;
    }

    @Override
    public void deleteCacheByIdList(List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            log.error("deleteCacheByIdList请求参数为空");
            return;
        }
        StopWatch timeWatch = new StopWatch(UUID.randomUUID().toString());
        ConfigMarketLevelExample configMarketLevelExample = new ConfigMarketLevelExample();
        configMarketLevelExample.createCriteria().andIdIn(idList);
        timeWatch.start("deleteCacheByIdList数据库查询耗时");
        List<ConfigMarketLevel> configMarketLevels = configMarketLevelMapper.selectByExample(configMarketLevelExample);
        if (CollectionUtils.isEmpty(configMarketLevels)) {
            log.error("deleteCacheByIdList查询数据为空,id集合：{}", idList);
            return;
        }
        timeWatch.stop();
        log.info("deleteCacheByIdList查询数据库耗时:{}", timeWatch.getTotalTimeMillis());
        Set<String> keySet = configMarketLevels.stream()
                .filter(e -> Objects.nonNull(e.getSportId()) && Objects.nonNull(e.getTournamentLevel()))
                .map(e -> RedisConfig.REDIS_KEY_DATABASE + "::ConfigMarketLevel:" + e.getSportId() + "-" + e.getTournamentLevel())
                .collect(Collectors.toSet());
        timeWatch.start("deleteCacheByIdList删除缓存耗时");
        // 批量删除key
        redisService.del(new ArrayList<>(keySet));
        timeWatch.stop();
        log.info("deleteCacheByIdList删除缓存耗时:{}",timeWatch.getTotalTimeMillis());
    }
}
