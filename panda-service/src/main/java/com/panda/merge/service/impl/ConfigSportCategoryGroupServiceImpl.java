package com.panda.merge.service.impl;


import com.panda.merge.cache.CacheConstant;
import com.panda.merge.mapper.ConfigSportCategoryGroupMapper;
import com.panda.merge.model.ConfigSportCategoryGroup;
import com.panda.merge.model.ConfigSportCategoryGroupExample;
import com.panda.merge.service.ConfigSportCategoryGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @name: ConfigSportCategoryGroupServiceImpl
 * @description:
 * @date: 1/13/2025
 **/
@Slf4j
@Service
@CacheConfig(cacheNames = CacheConstant.CACHE_SPORT_CATEGORY_GROUP, cacheManager = "localCacheManager")
public class ConfigSportCategoryGroupServiceImpl implements ConfigSportCategoryGroupService {

    @Autowired
    private ConfigSportCategoryGroupMapper mapper;


    @Override
    @Cacheable(key = "#sportId")
    public Map<Long, Integer> getBySportId(Long sportId) {
        ConfigSportCategoryGroupExample example = new ConfigSportCategoryGroupExample();
        example.createCriteria().andSportIdEqualTo(sportId);
        List<ConfigSportCategoryGroup> groups = mapper.selectByExample(example);
        if (CollectionUtils.isEmpty(groups)) {
            return Collections.emptyMap();
        }
        Map<Long, Integer> categoryGroups = groups
                .stream()
                .collect(Collectors.toMap(ConfigSportCategoryGroup::getCategoryId,
                                          ConfigSportCategoryGroup::getCategoryType,
                                          (v1, v2) -> v1));
        log.info("config sport category group cache update sportId :{}: {}", sportId, categoryGroups);
        return categoryGroups;
    }
}
