package com.panda.merge.service.impl;

import com.panda.merge.config.RedisConfig;
import com.panda.merge.dao.I18nMarketOddsDao;
import com.panda.merge.model.I18nMarketOdds;
import com.panda.merge.service.I18nMarketOddsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : raulvii
 * @project Name : panda-merge
 * @package Name : com.panda.merge.service.impl
 * @description : 投注项名称多语言
 * @date: 2020-09-11 9:52
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */
@Slf4j
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class I18nMarketOddsServiceImpl implements I18nMarketOddsService {

    @Autowired
    private I18nMarketOddsDao i18nMarketOddsDao;

    @Override
    public void saveBatch(List<I18nMarketOdds> i18nMarketOddsList) {
        i18nMarketOddsDao.saveBatch(i18nMarketOddsList);
    }

    @Override
    public void updateBatchById(List<I18nMarketOdds> i18nMarketOddsList) {

    }

}
