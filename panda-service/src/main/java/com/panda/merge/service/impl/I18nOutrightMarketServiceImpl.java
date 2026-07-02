package com.panda.merge.service.impl;

import com.panda.merge.config.RedisConfig;
import com.panda.merge.dao.I18nOutrightMarketDao;
import com.panda.merge.mapper.I18nOutrightMarketMapper;
import com.panda.merge.model.I18nOutrightMarket;
import com.panda.merge.model.I18nOutrightMarketExample;
import com.panda.merge.service.I18nOutrightMarketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : raulvii
 * @project Name : panda-merge
 * @package Name : com.panda.merge.service.impl
 * @description : 盘口名称多语言
 * @date: 2020-09-11 9:52
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */
@Slf4j
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class I18nOutrightMarketServiceImpl implements I18nOutrightMarketService {

    @Autowired
    private I18nOutrightMarketDao i18nOutrightMarketDao;

    @Autowired
    private I18nOutrightMarketMapper i18nOutrightMarketMapper;

    @Override
    public void saveBatch(List<I18nOutrightMarket> i18nOutrightMarketList) {
        i18nOutrightMarketDao.saveBatch(i18nOutrightMarketList);
    }

    @Override
    public void updateBatchById(List<I18nOutrightMarket> i18nOutrightMarketList) {
        i18nOutrightMarketDao.updateBatchById(i18nOutrightMarketList);
    }

    @Override
    public List<I18nOutrightMarket> selectI18nOutrightMarketList(String dataSourceCode, List<Long> nameCodeList) {
        I18nOutrightMarketExample i18nOutrightMarketExample = new I18nOutrightMarketExample();
        i18nOutrightMarketExample.createCriteria().andNameCodeIn(nameCodeList).andDataSourceCodeEqualTo(dataSourceCode).andFlagEqualTo(2);
        return i18nOutrightMarketMapper.selectByExample(i18nOutrightMarketExample);
    }

    @Override
    public List<I18nOutrightMarket> selectI18nOutrightMarketList(String dataSourceCode, Long nameCode) {
        I18nOutrightMarketExample i18nOutrightMarketExample = new I18nOutrightMarketExample();
        i18nOutrightMarketExample.createCriteria().andDataSourceCodeEqualTo(dataSourceCode).andNameCodeEqualTo(nameCode);
        return i18nOutrightMarketMapper.selectByExample(i18nOutrightMarketExample);
    }
}
