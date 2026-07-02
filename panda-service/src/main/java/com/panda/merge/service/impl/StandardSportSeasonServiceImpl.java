package com.panda.merge.service.impl;

import com.panda.merge.config.RedisConfig;
import com.panda.merge.mapper.StandardSportSeasonMapper;
import com.panda.merge.model.StandardSportSeason;
import com.panda.merge.model.ThirdSportSeason;
import com.panda.merge.service.StandardSportSeasonService;
import com.panda.merge.service.ThirdSportSeasonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

/**
 * @Author Kepa
 * @Date 2021/2/10 17:41
 * @Version 1.0
 */
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class StandardSportSeasonServiceImpl  extends BaseServiceImpl<StandardSportSeason> implements StandardSportSeasonService {

    @Autowired
    StandardSportSeasonMapper standardSportSeasonMapper;

    @Override
    public StandardSportSeason saveOrupdate(StandardSportSeason item) {
        return null;
    }
}
