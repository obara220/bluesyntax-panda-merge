package com.panda.merge.service.impl;

import com.panda.merge.common.RedisHelper;
import com.panda.merge.bo.ThirdSportMarketCategoryBO;
import com.panda.merge.common.RedisHelper;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.dao.ThirdMarketCategoryDao;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdCategoryDTO;
import com.panda.merge.dto.ThirdMatchMarketDTO;
import com.panda.merge.mapper.ThirdMarketCategoryMapper;
import com.panda.merge.model.ThirdMarketCategory;
import com.panda.merge.model.ThirdMarketCategoryExample;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.service.ThirdMarketCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/14 <br>
 * @see com.panda.merge.service.impl <br>
 */
@Slf4j
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class ThirdMarketCategoryServiceImpl implements ThirdMarketCategoryService {

    @Autowired
    private ThirdMarketCategoryMapper thirdMarketCategoryMapper;

    @Resource
    private ThirdMarketCategoryDao thirdMarketCategoryDao;

    @Autowired
    private RedisService redisService;

    @Resource
    private RedisHelper redisHelper;

    @Override
    @Cacheable(key = "'ThirdMarketCategory:' + #dataSourceCode+ '-' + #thirdMarketCategorySourceId", unless = "#result == null ")
    public ThirdMarketCategory getItem(String dataSourceCode, String thirdMarketCategorySourceId) {
        ThirdMarketCategoryExample thirdMarketCategoryExample = new ThirdMarketCategoryExample();
        thirdMarketCategoryExample.createCriteria().andDataSourceCodeEqualTo(dataSourceCode)
                .andThirdSourceIdEqualTo(thirdMarketCategorySourceId);
        List<ThirdMarketCategory> thirdMarketCategories = thirdMarketCategoryMapper.selectByExample(thirdMarketCategoryExample);
        if (CollectionUtils.isEmpty(thirdMarketCategories)) {
            return null;
        }
        return thirdMarketCategories.get(0);
    }

    @Override
    public List<ThirdMarketCategory> getItems(List<String> dataSourceCategory) {
        if(CollectionUtils.isEmpty(dataSourceCategory)) {
            return Collections.EMPTY_LIST;
        }
        List<ThirdMarketCategory> result = new ArrayList<>();
        List<String> requiredCallItems = new ArrayList<>();

        // Obtaining data from redis
        List<String> keys = dataSourceCategory.stream().map(t-> RedisConfig.REDIS_KEY_DATABASE + "::ThirdMarketCategory:" + t).collect(Collectors.toList());
        List<Object> objectList= redisService.mGet(keys);
        redisHelper.postProcMget(dataSourceCategory, objectList, result, requiredCallItems);
        if(CollectionUtils.isEmpty(requiredCallItems)){
            return result;
        }
        log.info("2724,查询三方盘口玩法模版数据库：{}", requiredCallItems);
        ThirdMarketCategoryExample example = new ThirdMarketCategoryExample();
        for (String category : requiredCallItems) {
            String[] array = category.split("-");         // dataSourceCode: array   thirdMarketCategorySourceId: arr[1]
            if (array.length != 2) {
                throw new RuntimeException("[ThirdMarketCategoryServiceImpl] getItems category's split array length is not equal to 2!");
            }
            example.or().andDataSourceCodeEqualTo(array[0]).andThirdSourceIdEqualTo(array[1]);
        }
        List<ThirdMarketCategory> thirdMarketCategories = thirdMarketCategoryMapper.selectByExample(example);
        Map<String, ThirdMarketCategory> thirdMarketCategoriesMap =thirdMarketCategories.stream().collect(Collectors.toMap(
                t->t.getDataSourceCode()+"-"+t.getThirdSourceId(), Function.identity(), (v1, v2)->v1));
        List<ThirdMarketCategory> filteredThirdMarketCategories =thirdMarketCategoriesMap.values().stream().collect(Collectors.toList());

        result.addAll(filteredThirdMarketCategories);
        // Storing the remained data into redis
        Map<String, Object> redisVal = filteredThirdMarketCategories.stream().collect(Collectors.toMap(t->
                RedisConfig.REDIS_KEY_DATABASE + "::ThirdMarketCategory:" + t.getDataSourceCode() +  '-' + t.getThirdSourceId(), Function.identity(), (v1, v2) -> v1));
        redisService.mSet(redisVal);
        return result;
    }

    @Override
    @Cacheable(key = "'ThirdMarketCategory:' + #dataSourceCode+ '-' + #marketCategoryId", unless = "#result == null ")
    public List<ThirdMarketCategory> getItem(String dataSourceCode, Long marketCategoryId) {
        ThirdMarketCategoryExample thirdMarketCategoryExample = new ThirdMarketCategoryExample();
        thirdMarketCategoryExample.createCriteria().andDataSourceCodeEqualTo(dataSourceCode).andReferenceIdEqualTo(marketCategoryId);
        List<ThirdMarketCategory> thirdMarketCategories = thirdMarketCategoryMapper.selectByExample(thirdMarketCategoryExample);
        if (CollectionUtils.isEmpty(thirdMarketCategories)) {
            return null;
        }
        return thirdMarketCategories;
    }

    @Override
    public List<ThirdMarketCategory> getItemsByDataSourceAndReferenceIds(List<String> dataSourceReferences) {
        ThirdMarketCategoryExample example = new ThirdMarketCategoryExample();
        for (String category : dataSourceReferences) {
            String[] array = category.split("-");         // dataSourceCode: array   thirdMarketCategorySourceId: arr[1]
            if (array.length != 2) {
                throw new RuntimeException("[ThirdMarketCategoryServiceImpl] getItems category's split array length is not equal to 2!");
            }
            example.or().andDataSourceCodeEqualTo(array[0]).andReferenceIdEqualTo(Long.parseLong(array[1]));
        }
        return thirdMarketCategoryMapper.selectByExample(example);
    }

    @Override
    @Cacheable(key = "'ThirdMarketCategory:' + #thirdMarketCategory.dataSourceCode+ '-' + #thirdMarketCategory.thirdSourceId", unless = "#result == null ")
    public ThirdMarketCategory create(ThirdMarketCategory thirdMarketCategory) {
        thirdMarketCategoryMapper.insertSelective(thirdMarketCategory);
        return thirdMarketCategory;
    }


    @Override
    public List<ThirdMarketCategory> queryThirdMarketCategoryList(Set<String> dataSourceCodeSet, Set<String> thirdSourceIdSet) {
        ThirdMarketCategoryExample thirdMarketCategoryExample = new ThirdMarketCategoryExample();
        thirdMarketCategoryExample.createCriteria().andDataSourceCodeIn(new ArrayList<>(dataSourceCodeSet))
                .andThirdSourceIdIn(new ArrayList<>(thirdSourceIdSet));
        List<ThirdMarketCategory> thirdMarketCategories = thirdMarketCategoryMapper.selectByExample(thirdMarketCategoryExample);
        if (CollectionUtils.isEmpty(thirdMarketCategories)) {
            return null;
        }
        return thirdMarketCategories;
    }

    @Override
    public List<ThirdMarketCategory> queryByThirdMarketCategorySourceIdSet(Set<String> thirdMarketCategorySourceIdSet) {
        ThirdMarketCategoryExample thirdMarketCategoryExample = new ThirdMarketCategoryExample();
        thirdMarketCategoryExample.createCriteria().andThirdSourceIdIn(new ArrayList<>(thirdMarketCategorySourceIdSet));
        List<ThirdMarketCategory> thirdMarketCategories = thirdMarketCategoryMapper.selectByExample(thirdMarketCategoryExample);
        if (CollectionUtils.isEmpty(thirdMarketCategories)) {
            return null;
        }
        return thirdMarketCategories;
    }

    @Override
    public void saveBatch(List<ThirdMarketCategory> categoryList) {
        for (ThirdMarketCategory category: categoryList) {
            thirdMarketCategoryMapper.insertSelective(category);
        }
    }

    @Override
    public void updateBatchById(List<ThirdMarketCategory> categoryList) {
        //清除缓存
        List<String> keyList = new ArrayList<>();
        for (ThirdMarketCategory thirdMarketCategory : categoryList) {
            String key = RedisConfig.REDIS_KEY_DATABASE + "::ThirdMarketCategory:" + thirdMarketCategory.getDataSourceCode() + "-" + thirdMarketCategory.getThirdSourceId();
            keyList.add(key);
        }
        redisService.del(keyList);
        thirdMarketCategoryDao.updateBatchById(categoryList);
    }

    @Override
    public List<ThirdSportMarketCategoryBO> queryThirdMarketCategory(ThirdCategoryDTO dto) {
        return thirdMarketCategoryDao.queryThirdMarketCategory(dto);
    }

    @Override
    public void updateThirdMarketCategory(Request<ThirdCategoryDTO> request) {
        ThirdCategoryDTO data = request.getData();
        ThirdMarketCategory thirdMarketCategory = new ThirdMarketCategory();
        thirdMarketCategory.setId(data.getId());
        thirdMarketCategory.setReferenceId(data.getReferenceId());
        thirdMarketCategoryMapper.updateByPrimaryKeySelective(thirdMarketCategory);
        String key = RedisConfig.REDIS_KEY_DATABASE + "::ThirdMarketCategory:" + data.getDataSourceCode() + "-" + data.getThirdSourceId();
        redisService.del(key);
    }

    public int delRedisByAll(){
        //查询全部数据，并清理redis中缓存
        List<ThirdMarketCategory> resList = thirdMarketCategoryMapper.selectByExample(new ThirdMarketCategoryExample());
        for (ThirdMarketCategory item : resList) {
            redisService.del(RedisConfig.REDIS_KEY_DATABASE + "::ThirdMarketCategory:" + item.getDataSourceCode() + "-" + item.getThirdSourceId());
            if(null != item.getReferenceId() && item.getReferenceId() > 0){
                redisService.del(RedisConfig.REDIS_KEY_DATABASE + "::ThirdMarketCategory:" + item.getDataSourceCode() + "-" + item.getReferenceId());
            }
        }
        return resList.size();
    }
}
