package com.panda.merge.service.impl;

import com.panda.merge.config.RedisConfig;
import com.panda.merge.dao.ConfigMarketMarginGapLogDao;
import com.panda.merge.model.ConfigMarketMarginGapLog;
import com.panda.merge.service.ConfigMarketMarginGapLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class ConfigMarketMarginGapLogImplService implements ConfigMarketMarginGapLogService {

    @Autowired
    private ConfigMarketMarginGapLogDao configMarketMarginGapLogDao;

    @Override
    public void createList(List<ConfigMarketMarginGapLog> logs) {
//        configMarketMarginGapLogDao.insertList(logs);
    }
}
