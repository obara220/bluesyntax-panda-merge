package com.panda.merge.service.impl;

import com.panda.merge.config.RedisConfig;
import com.panda.merge.dao.I18nOutrightMarketOddsDao;
import com.panda.merge.mapper.I18nOutrightMarketOddsMapper;
import com.panda.merge.model.I18nOutrightMarketExample;
import com.panda.merge.model.I18nOutrightMarketOdds;
import com.panda.merge.model.I18nOutrightMarketOddsExample;
import com.panda.merge.service.I18nOutrightMarketOddsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
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
public class I18nOutrightMarketOddsServiceImpl implements I18nOutrightMarketOddsService {

    @Autowired
    private I18nOutrightMarketOddsDao i18nOutrightMarketOddsDao;
    @Autowired
    private I18nOutrightMarketOddsMapper i18nOutrightMarketOddsMapper;

    @Override
    public void saveBatch(List<I18nOutrightMarketOdds> i18nOutrightMarketOddsList) {
        i18nOutrightMarketOddsDao.saveBatch(i18nOutrightMarketOddsList);
    }

    @Override
    public List<I18nOutrightMarketOdds> selectI18nOutRightMarketOddsList(String dataSourceCode, Long nameCode) {
        I18nOutrightMarketOddsExample example = new I18nOutrightMarketOddsExample();
        example.createCriteria().andDataSourceCodeEqualTo(dataSourceCode).andNameCodeEqualTo(nameCode);
        return i18nOutrightMarketOddsMapper.selectByExample(example);
    }

    /**
     * pair left: dataSourceCode pair right:nameCode
     * @param i18nPairs
     * @return
     */
    @Override
    public List<I18nOutrightMarketOdds> selectI18nOutRightMarketOddsList(List<Pair<String, Long>> i18nPairs) {
        if(CollectionUtils.isEmpty(i18nPairs)) {
            return Collections.EMPTY_LIST;
        }
        I18nOutrightMarketOddsExample example = new I18nOutrightMarketOddsExample();
        for(Pair<String, Long> pair : i18nPairs) {
            example.or().andNameCodeEqualTo(pair.getRight()).andDataSourceCodeEqualTo(pair.getLeft());
        }
        return i18nOutrightMarketOddsMapper.selectByExample(example);
    }

    @Override
    public List<I18nOutrightMarketOdds> getListOutrightMarketOdds(List<Long> nameCodes, String dataSourceCode) {
        I18nOutrightMarketOddsExample example = new I18nOutrightMarketOddsExample();
        example.createCriteria().andNameCodeIn(nameCodes).andDataSourceCodeEqualTo(dataSourceCode);
        return i18nOutrightMarketOddsMapper.selectByExample(example);
    }

    @Override
    public void updateBatchByPrimaryKeys(List<I18nOutrightMarketOdds> records){
        for (I18nOutrightMarketOdds i18n : records){
            i18nOutrightMarketOddsMapper.updateByPrimaryKey(i18n);
        }
    }


}
