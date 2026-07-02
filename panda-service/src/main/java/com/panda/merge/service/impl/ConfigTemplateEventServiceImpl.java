package com.panda.merge.service.impl;

import com.panda.merge.config.RedisService;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.dao.ConfigTemplateEventDao;
import com.panda.merge.mapper.ConfigTemplateEventMapper;
import com.panda.merge.model.ConfigTemplateEvent;
import com.panda.merge.model.ConfigTemplateEventExample;
import com.panda.merge.service.ConfigTemplateEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author :  Riben
 * @Project Name :  panda-merge
 * @Package Name :  com.panda.merge.service.impl
 * @Description :  TODO
 * @Date: 2020-09-11 10:03
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class ConfigTemplateEventServiceImpl implements ConfigTemplateEventService {
    @Autowired
    private ConfigTemplateEventMapper configTemplateEventMapper;

    @Autowired
    private ConfigTemplateEventDao configTemplateEventDao;

    @Autowired
    private RedisService redisService;

    @Override
    @Cacheable(key = "'ConfigTemplateEvent:' + #templateId", unless = "#result == null ")
    public List<ConfigTemplateEvent> getEventConfigurationByTemplateId(Long templateId) {
        ConfigTemplateEventExample query = new ConfigTemplateEventExample();
        query.createCriteria().andTemplateIdEqualTo(templateId);
        List<ConfigTemplateEvent> eventConfigurations = configTemplateEventMapper.selectByExample(query);
        if (CollectionUtils.isEmpty(eventConfigurations)) {
            return null;
        }
        return eventConfigurations;
    }

    @Override
    public void saveBatch(List<ConfigTemplateEvent> addTournamentEventList) {
        configTemplateEventDao.insertList(addTournamentEventList);
    }

    @Override
    public void updateBatch(List<ConfigTemplateEvent> uptTournamentEventList) {
        List<String> keyList = new ArrayList<>();
        for (ConfigTemplateEvent list : uptTournamentEventList) {
            String key = RedisConfig.REDIS_KEY_DATABASE + "::ConfigTemplateEvent:" + list.getTemplateId();
            keyList.add(key);
        }
        redisService.del(keyList);
        configTemplateEventDao.updateBatch(uptTournamentEventList);
    }
}
