package com.panda.merge.service.impl;

import com.panda.merge.config.RedisConfig;
import com.panda.merge.mapper.ConfigTemplateDataSourceMapper;
import com.panda.merge.model.ConfigTemplateDataSource;
import com.panda.merge.model.ConfigTemplateDataSourceExample;
import com.panda.merge.service.ConfigTemplateDataSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author :  Riben
 * @Project Name :  panda-merge
 * @Package Name :  com.panda.merge.service.impl
 * @Description :  TODO
 * @Date: 2020-09-11 10:04
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class ConfigTemplateDataSourceServiceImpl implements ConfigTemplateDataSourceService {

    @Autowired
    private ConfigTemplateDataSourceMapper configTemplateDataSourceMapper;

    @Override
    @CachePut(key = "'ConfigTemplateDataSource:' + #templateId")
    public ConfigTemplateDataSource getDataSourceConfigurationBytemplateId(Long templateId) {
        ConfigTemplateDataSourceExample query = new  ConfigTemplateDataSourceExample();
        query.createCriteria().andTemplateIdEqualTo(templateId);
        List<ConfigTemplateDataSource> result = configTemplateDataSourceMapper.selectByExample(query);
        if(CollectionUtils.isEmpty(result)){
            return null;
        }
        return result.get(0);
    }

    @Override
    public boolean save(ConfigTemplateDataSource configTemplateDataSource) {
        int rowNum = configTemplateDataSourceMapper.insert(configTemplateDataSource);
        return rowNum > 0;
    }

    @Override
    @CacheEvict(key = "'ConfigTemplateDataSource:' + #configTemplateDataSource.templateId")
    public void updateById(ConfigTemplateDataSource configTemplateDataSource) {
        configTemplateDataSourceMapper.updateByPrimaryKeySelective(configTemplateDataSource);
    }
}
