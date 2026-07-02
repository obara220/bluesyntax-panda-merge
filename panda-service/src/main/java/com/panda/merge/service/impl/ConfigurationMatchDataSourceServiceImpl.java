package com.panda.merge.service.impl;

import com.panda.merge.config.RedisConfig;
import com.panda.merge.dao.ConfigurationMatchDataSourceDao;
import com.panda.merge.mapper.ConfigurationMatchDataSourceMapper;
import com.panda.merge.model.ConfigurationMatchDataSource;
import com.panda.merge.model.ConfigurationMatchDataSourceExample;
import com.panda.merge.service.ConfigurationMatchDataSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author :  Riben
 * @Project Name :  panda-merge
 * @Package Name :  com.panda.merge.service
 * @Description :  TODO
 * @Date: 2020-09-17 13:42
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class ConfigurationMatchDataSourceServiceImpl implements ConfigurationMatchDataSourceService {

    @Autowired
    ConfigurationMatchDataSourceMapper dataSourceMapper;

    @Autowired
    ConfigurationMatchDataSourceDao matchDataSourceDao;

    @Override
    @CachePut(key = "'ConfigurationMatchDataSource:' + #dataSource.standardMatchId+'-'+#dataSource.marketType",unless = "#result == null ")
    public ConfigurationMatchDataSource save(ConfigurationMatchDataSource dataSource) {
        dataSourceMapper.insert(dataSource);
        return dataSource;
    }

    @Override
    @CachePut(key = "'ConfigurationMatchDataSource:' + #standardMatchId+'-'+#marketType",unless = "#result == null ")
    public ConfigurationMatchDataSource getRecByMatchIdAndMarketType(Long standardMatchId, Integer marketType) {
        ConfigurationMatchDataSourceExample query = new ConfigurationMatchDataSourceExample();
        query.createCriteria().andStandardMatchIdEqualTo(standardMatchId).andMarketTypeEqualTo(marketType);
        List<ConfigurationMatchDataSource> existDataSources =  dataSourceMapper.selectByExample(query);
        if(CollectionUtils.isEmpty(existDataSources)){
            return null;
        }
        return existDataSources.get(0);
    }

    @Override
    @CachePut(key = "'ConfigurationMatchDataSource:' + #existDataSource.standardMatchId+'-'+#existDataSource.marketType")
    public ConfigurationMatchDataSource update(ConfigurationMatchDataSource existDataSource) {
        matchDataSourceDao.updateByMatchIdAndMarketType(existDataSource);
        return existDataSource;
    }


}
