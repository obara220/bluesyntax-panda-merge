package com.panda.merge.service.impl;

import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.dao.ThirdSportMarketCategoryDao;
import com.panda.merge.mapper.ThirdSportMarketCategoryMapper;
import com.panda.merge.model.ThirdMarketCategory;
import com.panda.merge.model.ThirdSportMarketCategory;
import com.panda.merge.model.ThirdSportMarketCategoryExample;
import com.panda.merge.service.ThirdSportMarketCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author : bevan
 * @project Name : panda-merge
 * @package Name : com.panda.merge.service.impl
 * @date: 2020-09-11 9:39
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */
@Slf4j
@Service
public class ThirdSportMarketCategoryServiceImpl implements ThirdSportMarketCategoryService {

    private static final String CACHE_KEY_PREFIX = "ThirdSportMarketCategory:";

    @Autowired
    private ThirdSportMarketCategoryMapper thirdSportMarketCategoryMapper;

    @Autowired
    private ThirdSportMarketCategoryDao thirdSportMarketCategoryDao;

    @Autowired
    private RedisService redisService;

    @Override
    public List<ThirdSportMarketCategory> queryThirdSportMarketCategoryList(Set<Long> categoryIdSet) {
        ThirdSportMarketCategoryExample example = new ThirdSportMarketCategoryExample();
        example.createCriteria().andMarketCategoryIdIn(new ArrayList<>(categoryIdSet));
        return thirdSportMarketCategoryMapper.selectByExample(example);
    }

    @Override
    public void saveBatch(List<ThirdSportMarketCategory> thirdSportMarketCategories) {
        thirdSportMarketCategoryDao.saveBatch(thirdSportMarketCategories);
    }

    @Override
    public int delRedisByAll() {
        Set<String> keys = redisService.keys(RedisConfig.REDIS_KEY_DATABASE + "::" + CACHE_KEY_PREFIX + "*");
        if (CollectionUtils.isEmpty(keys)) {
            return 0;
        }
        redisService.del(new ArrayList<>(keys));
        return keys.size();
    }

    @Override
    public List<ThirdMarketCategory> queryThirdMarketCategoryList(List<Long> referenceIds, List<Long> sportIds) {
        return thirdSportMarketCategoryMapper.queryThirdMarketCategoryList(referenceIds, sportIds);
    }

    @Override
    public List<ThirdMarketCategory> getItemsBySportReferenceIds(String dataSourceCode, Long sportId, List<Long> referenceIds) {
        if (CollectionUtils.isEmpty(referenceIds) || sportId == null || dataSourceCode == null) {
            return Collections.emptyList();
        }
        List<Long> distinctReferenceIds = referenceIds.stream().distinct().collect(Collectors.toList());
        List<ThirdMarketCategory> result = new ArrayList<>();
        List<Long> requiredCallItems = new ArrayList<>();

        List<String> cacheSuffixes = distinctReferenceIds.stream()
                .map(referenceId -> buildCacheSuffix(sportId, dataSourceCode, referenceId))
                .collect(Collectors.toList());
        List<String> redisKeys = cacheSuffixes.stream()
                .map(this::buildRedisKey)
                .collect(Collectors.toList());
        List<Object> cachedValues = redisService.mGet(redisKeys);
        for (int i = 0; i < cachedValues.size(); i++) {
            Object cachedValue = cachedValues.get(i);
            if (cachedValue == null) {
                requiredCallItems.add(distinctReferenceIds.get(i));
            } else {
                result.add((ThirdMarketCategory) cachedValue);
            }
        }
        if (CollectionUtils.isEmpty(requiredCallItems)) {
            return result;
        }
        log.info("查询三方赛种玩法映射数据库,sportId:{},dataSourceCode:{},referenceIds:{}", sportId, dataSourceCode, requiredCallItems);
        List<ThirdMarketCategory> dbList = thirdSportMarketCategoryMapper.queryThirdMarketCategoryList(requiredCallItems, Collections.singletonList(sportId));
        Map<Long, List<ThirdMarketCategory>> categoryByReferenceId = dbList.stream()
                .filter(category -> dataSourceCode.equalsIgnoreCase(category.getDataSourceCode()))
                .collect(Collectors.groupingBy(ThirdMarketCategory::getReferenceId));
        Map<String, Object> redisVal = new HashMap<>();
        for (Long referenceId : requiredCallItems) {
            List<ThirdMarketCategory> categories = categoryByReferenceId.get(referenceId);
            if (CollectionUtils.isEmpty(categories)) {
                continue;
            }
            result.addAll(categories);
            if (categories.size() == 1) {
                redisVal.put(buildRedisKey(buildCacheSuffix(sportId, dataSourceCode, referenceId)), categories.get(0));
            }
        }
        if (!redisVal.isEmpty()) {
            redisService.mSet(redisVal);
        }
        return result.stream().collect(Collectors.toMap(
                category -> category.getDataSourceCode() + "-" + category.getReferenceId() + "-" + category.getThirdSourceId(),
                Function.identity(),
                (v1, v2) -> v1)).values().stream().collect(Collectors.toList());
    }

    private String buildCacheSuffix(Long sportId, String dataSourceCode, Long referenceId) {
        return sportId + "-" + dataSourceCode + "-" + referenceId;
    }

    private String buildRedisKey(String cacheSuffix) {
        return RedisConfig.REDIS_KEY_DATABASE + "::" + CACHE_KEY_PREFIX + cacheSuffix;
    }
}
