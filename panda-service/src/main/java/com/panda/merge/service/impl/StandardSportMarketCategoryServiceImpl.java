package com.panda.merge.service.impl;

import com.panda.merge.common.RedisHelper;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.dao.StandardSportMarketCategoryDao;
import com.panda.merge.dto.StandardSportMarketCategoryDetail;
import com.panda.merge.mapper.StandardSportMarketCategoryMapper;
import com.panda.merge.model.StandardSportMarketCategory;
import com.panda.merge.model.StandardSportMarketCategoryExample;
import com.panda.merge.service.StandardSportMarketCategoryService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 运动种类对应标准玩法玩数据
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/14 <br>
 */
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class StandardSportMarketCategoryServiceImpl implements StandardSportMarketCategoryService {

    @Resource
    private StandardSportMarketCategoryMapper standardSportMarketCategoryMapper;
    @Resource
    private StandardSportMarketCategoryDao standardSportMarketCategoryDao;

    @Resource
    private RedisService redisService;

    @Resource
    private RedisHelper redisHelper;

    @Override
    @Cacheable(key = "'StandardSportMarketCategory:'+ #marketCategoryId + '-' +#sportId",unless = "#result == null ")
    public StandardSportMarketCategory getItem(Long marketCategoryId, Long sportId) {
        StandardSportMarketCategoryExample standardSportMarketCategoryExample = new StandardSportMarketCategoryExample();
        standardSportMarketCategoryExample.createCriteria().andMarketCategoryIdEqualTo(marketCategoryId)
                .andSportIdEqualTo(sportId);
        List<StandardSportMarketCategory> standardSportMarketCategories = standardSportMarketCategoryMapper.selectByExample(standardSportMarketCategoryExample);
        if(CollectionUtils.isEmpty(standardSportMarketCategories)){
            return null;
        }
        return standardSportMarketCategories.get(0);
    }

    /**
     *
     * @param standardCategories left: marketCategoryId right: sportId
     * @return
     */
    @Override
    public List<StandardSportMarketCategory> getItemsByStandardCategories(List<Pair<Long, Long>> standardCategories) {
        if(CollectionUtils.isEmpty(standardCategories)) {
            return Collections.EMPTY_LIST;
        }
        List<StandardSportMarketCategory> result = new ArrayList<>();
        List<Pair<Long, Long>> requiredCallItems = new ArrayList<>();

        // Obtaining data from redis
        List<String> keys = standardCategories.stream().map(t-> RedisConfig.REDIS_KEY_DATABASE + "::StandardSportMarketCategory:" + t.getLeft()+"-"+t.getRight()).collect(Collectors.toList());
        List<Object> objectList= redisService.mGet(keys);
        redisHelper.postProcMget(standardCategories, objectList, result, requiredCallItems);
        if(CollectionUtils.isEmpty(requiredCallItems)){
            return result;
        }
        StandardSportMarketCategoryExample example = new StandardSportMarketCategoryExample();
        for (Pair<Long, Long> item : requiredCallItems) {
            example.or().andMarketCategoryIdEqualTo(item.getLeft()).andSportIdEqualTo(Long.valueOf(item.getRight()));
        }
        List<StandardSportMarketCategory> marketCategories = standardSportMarketCategoryMapper.selectByExample(example);
        Map<String, StandardSportMarketCategory> marketCategorySellsMap = marketCategories.stream().collect(Collectors.toMap(
                t->t.getMarketCategoryId()+"-"+t.getSportId(), Function.identity(), (v1, v2)->v1));
        List<StandardSportMarketCategory> filteredMarketCategorySells =marketCategorySellsMap.values().stream().collect(Collectors.toList());

        result.addAll(filteredMarketCategorySells);
        // Storing the remained data into redis
        Map<String, Object> redisVal = filteredMarketCategorySells.stream().collect(Collectors.toMap(t->
                RedisConfig.REDIS_KEY_DATABASE + "::StandardSportMarketCategory:" + t.getMarketCategoryId()+"-"+t.getSportId(), Function.identity(), (v1, v2) -> v1));
        redisService.mSet(redisVal);
        return result;
    }

    @Deprecated
    @Override
    public List<StandardSportMarketCategory> getItemsByMarketCategoryIds(List<Long> marketCategoryIds){
        if(CollectionUtils.isEmpty(marketCategoryIds)){
            return Collections.EMPTY_LIST;
        }
        StandardSportMarketCategoryExample standardSportMarketCategoryExample = new StandardSportMarketCategoryExample();
        standardSportMarketCategoryExample.createCriteria().andMarketCategoryIdIn(marketCategoryIds);
        return standardSportMarketCategoryMapper.selectByExample(standardSportMarketCategoryExample);
    }

    @Override
    public StandardSportMarketCategory getByCategoryIdAndSportId(Long marketCategoryId, Long sportId) {
    	StandardSportMarketCategoryExample standardSportMarketCategoryExample = new StandardSportMarketCategoryExample();
        standardSportMarketCategoryExample.createCriteria().andMarketCategoryIdEqualTo(marketCategoryId)
                .andSportIdEqualTo(sportId);
        List<StandardSportMarketCategory> standardSportMarketCategories = standardSportMarketCategoryMapper.selectByExample(standardSportMarketCategoryExample);
        if(CollectionUtils.isEmpty(standardSportMarketCategories)){
            return null;
        }
        return standardSportMarketCategories.get(0);
    }
    
    @Override
    public List<StandardSportMarketCategory> selectByCategoryId(Long marketCategoryId) {
        StandardSportMarketCategoryExample standardSportMarketCategoryExample = new StandardSportMarketCategoryExample();
        standardSportMarketCategoryExample.createCriteria().andMarketCategoryIdEqualTo(marketCategoryId);
        List<StandardSportMarketCategory> standardSportMarketCategories = standardSportMarketCategoryMapper.selectByExample(standardSportMarketCategoryExample);
        if(CollectionUtils.isEmpty(standardSportMarketCategories)){
            return null;
        }
        return standardSportMarketCategories;
    }

    @Override
    @Cacheable(key = "'StandardSportMarketCategory:'+ #marketCategoryId",unless = "#result == null || #result.size() == 0")
    public List<StandardSportMarketCategoryDetail> getItems(Long marketCategoryId){
        return standardSportMarketCategoryDao.getItemsByMarketCategoryId(marketCategoryId);
    }

    @Override
    public List<StandardSportMarketCategoryDetail> getItems(List<Long> marketCategoryIds){
        if(CollectionUtils.isEmpty(marketCategoryIds)){
            return new LinkedList<>();
        }
        return standardSportMarketCategoryDao.getItemsByMarketCategoryIds(marketCategoryIds);
    }

    @Override
    public int delRedisByAll(){
        //查询全部数据，并清理redis中缓存
        List<StandardSportMarketCategory> resList = standardSportMarketCategoryMapper.selectByExample(new StandardSportMarketCategoryExample());
        for (StandardSportMarketCategory item : resList) {
            redisService.del(RedisConfig.REDIS_KEY_DATABASE + "::StandardSportMarketCategory:" + item.getMarketCategoryId());
            redisService.del(RedisConfig.REDIS_KEY_DATABASE + "::StandardSportMarketCategory:" + item.getMarketCategoryId() + "-" + item.getSportId());
        }
        return resList.size();
    }

}
