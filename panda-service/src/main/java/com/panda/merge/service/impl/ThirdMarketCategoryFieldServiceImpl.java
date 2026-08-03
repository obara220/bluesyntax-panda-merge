package com.panda.merge.service.impl;

import com.panda.merge.common.RedisHelper;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.dao.ThirdMarketCategoryFieldDao;
import com.panda.merge.dto.ThirdMarketCategoryFieldDetail;
import com.panda.merge.mapper.ThirdMarketCategoryFieldMapper;
import com.panda.merge.model.*;
import com.panda.merge.service.ThirdMarketCategoryFieldService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
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
public class ThirdMarketCategoryFieldServiceImpl implements ThirdMarketCategoryFieldService {

    @Autowired
    private ThirdMarketCategoryFieldMapper thirdMarketCategoryFieldMapper;

    @Autowired
    private ThirdMarketCategoryFieldDao thirdMarketCategoryFieldDao;

    @Autowired
    private RedisService redisService;

    @Resource
    private RedisHelper redisHelper;

    @Override
    @Cacheable(key = "'ThirdMarketCategoryField:' + #thirdMarketCategoryId+ '-' + #thirdTemplateSourceId", unless = "#result == null ")
    public ThirdMarketCategoryField getItem(String dataSourceCode, String thirdTemplateSourceId, Long thirdMarketCategoryId) {
        if ("none".equalsIgnoreCase(thirdTemplateSourceId.toLowerCase())) {
            //表示无投注项模板
            ThirdMarketCategoryField template = new ThirdMarketCategoryField();
            template.setThirdSourceId("None");
            template.setId(0L);
            template.setReferenceId(0L);
            return template;
        }
        ThirdMarketCategoryFieldExample thirdMarketCategoryFieldExample = new ThirdMarketCategoryFieldExample();
        thirdMarketCategoryFieldExample.createCriteria().andMarketCategoryIdEqualTo(thirdMarketCategoryId)
                .andThirdSourceIdEqualTo(thirdTemplateSourceId);
        List<ThirdMarketCategoryField> thirdMarketCategoryFields = thirdMarketCategoryFieldMapper.selectByExample(thirdMarketCategoryFieldExample);
        if (CollectionUtils.isEmpty(thirdMarketCategoryFields)) {
            return null;
        }
        return thirdMarketCategoryFields.get(0);
    }

    @Override
    @Cacheable(key = "'ThirdMarketCategoryField:' + #id", unless = "#result == null ")
    public ThirdMarketCategoryField getItem(Long id, String thirdTemplateSourceId) {
        if(null == thirdTemplateSourceId){
            return null;
        }
        if ("none".equalsIgnoreCase(thirdTemplateSourceId.toLowerCase())) {
            //表示无投注项模板
            ThirdMarketCategoryField template = new ThirdMarketCategoryField();
            template.setThirdSourceId("None");
            template.setId(0L);
            template.setReferenceId(0L);
            return template;
        }
        return thirdMarketCategoryFieldMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<ThirdMarketCategoryField> queryThirdSportOddsFieldsList(Set<String> thirdTempletSourceIdSet) {
        if(CollectionUtils.isEmpty(thirdTempletSourceIdSet)) {
            return Collections.EMPTY_LIST;
        }
        List<String> thirdTempletSourceIdList = new ArrayList<>();
        List<ThirdMarketCategoryField> result = new ArrayList<>();
        List<String> requiredCallItems = new ArrayList<>();

        // Obtaining data from redis
        List<String> keys = new ArrayList<>();
        for (String item : thirdTempletSourceIdSet) {
            String[] array = item.split("-");         // dataSourceCode: array   thirdMarketCategorySourceId: arr[1]
            if (array.length != 2) {
                throw new RuntimeException("[ThirdMarketCategoryFieldServiceImpl] queryThirdSportOddsFieldsList parameter marketSellkeys's split array length is not equal to 2!");
            }
            if ("none".equals(array[0])) {
                //表示无投注项模板
                ThirdMarketCategoryField template = new ThirdMarketCategoryField();
                template.setThirdSourceId("none");
                template.setId(0L);
                template.setReferenceId(0L);
                template.setMarketCategoryId(Long.parseLong(array[1]));
                result.add(template);
                continue;
            }
            if ("None".equals(array[0])) {
                //表示无投注项模板
                ThirdMarketCategoryField template = new ThirdMarketCategoryField();
                template.setThirdSourceId("None");
                template.setId(0L);
                template.setReferenceId(0L);
                template.setMarketCategoryId(Long.parseLong(array[1]));
                result.add(template);
                continue;
            }
            thirdTempletSourceIdList.add(item);
            keys.add(RedisConfig.REDIS_KEY_DATABASE + "::ThirdMarketCategoryField:" + array[1] +"-"+array[0]);
        }
        List<Object> objectList= redisService.mGet(keys);
        redisHelper.postProcMget(thirdTempletSourceIdList, objectList, result, requiredCallItems);
        if(CollectionUtils.isEmpty(requiredCallItems)){
            return result;
        }
        log.info("2724,查询三方投注项模版数据库：{}", requiredCallItems);
        // Obtaining remained data from mysql
        ThirdMarketCategoryFieldExample example = new ThirdMarketCategoryFieldExample();
        for(String item : requiredCallItems) {
            String[] array = item.split("-");
            example.or().andMarketCategoryIdEqualTo(Long.parseLong(array[1]))
                    .andThirdSourceIdEqualTo(array[0]);
        }
        List<ThirdMarketCategoryField> thirdMarketCategoryFields = thirdMarketCategoryFieldMapper.selectByExample(example);
        result.addAll(thirdMarketCategoryFields);
        // Storing the remained data into redis
        Map<String, Object> redisVal = thirdMarketCategoryFields.stream().collect(Collectors.toMap(t->RedisConfig.REDIS_KEY_DATABASE + "::ThirdMarketCategoryField:"
                + t.getMarketCategoryId()+"-"+t.getThirdSourceId(), Function.identity(), (v1, v2) -> v1));
        redisService.mSet(redisVal);
        return result;
    }

    @Override
    public List<ThirdMarketCategoryField> queryThirdSportOddsFieldsLists(Set<String> thirdTempletSourceIdSet) {
        ThirdMarketCategoryFieldExample example = new ThirdMarketCategoryFieldExample();
        example.createCriteria().andThirdSourceIdIn(new ArrayList<>(thirdTempletSourceIdSet));
        List<ThirdMarketCategoryField> thirdMarketCategoryFields = thirdMarketCategoryFieldMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(thirdMarketCategoryFields)) {
            return new LinkedList<>();
        }
        return thirdMarketCategoryFields;
    }

    @Override
    public List<ThirdMarketCategoryField> queryFieldsByDataSourceAndMarketCategoryIds(Set<String> dataSourceAndMarketCategoryIds) {
        ThirdMarketCategoryFieldExample example = new ThirdMarketCategoryFieldExample();
        for (String category : dataSourceAndMarketCategoryIds) {
            String[] array = category.split("-");         // dataSourceCode: array   thirdMarketCategorySourceId: arr[1]
            if (array.length != 2) {
                throw new RuntimeException("[ThirdMarketCategoryFieldServiceImpl] queryFieldsByDataSourceAndThirdSourceIds category's split array length is not equal to 2!");
            }
            example.or().andMarketCategoryIdEqualTo(Long.parseLong(array[1])).andDataSourceCodeEqualTo(array[0]);
        }
        return thirdMarketCategoryFieldMapper.selectByExample(example);
    }

    @Override
    public void saveBatch(List<ThirdMarketCategoryField> thirdMarketCategoryFieldList) {
        thirdMarketCategoryFieldDao.saveBatch(thirdMarketCategoryFieldList);
    }

    @Override
    public void updateBatchById(List<ThirdMarketCategoryField> thirdMarketCategoryFieldList) {
        //清除缓存
        List<String> keyList = new ArrayList<>();
        for (ThirdMarketCategoryField field : thirdMarketCategoryFieldList) {
            keyList.add(RedisConfig.REDIS_KEY_DATABASE + "::ThirdMarketCategoryField:" + field.getId());
            keyList.add(RedisConfig.REDIS_KEY_DATABASE + "::ThirdMarketCategoryField:" + field.getMarketCategoryId() + "-" + field.getThirdSourceId());
        }
        redisService.del(keyList);
        thirdMarketCategoryFieldDao.updateBatchById(thirdMarketCategoryFieldList);
    }

    @Override
    public List<ThirdMarketCategoryField> queryThirdMarketCategoryField(String dataSourceCode) {
        ThirdMarketCategoryFieldExample example = new ThirdMarketCategoryFieldExample();
        example.createCriteria().andDataSourceCodeEqualTo(dataSourceCode).andReferenceIdNotEqualTo(0L);
        List<ThirdMarketCategoryField> thirdMarketCategoryFields = thirdMarketCategoryFieldMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(thirdMarketCategoryFields)) {
            return new LinkedList<>();
        }
        return thirdMarketCategoryFields;
    }

    @Override
    public List<ThirdMarketCategoryFieldDetail> queryThirdMarketCategoryFieldDetail(String dataSourceCode, Long marketCategoryId) {
        List<ThirdMarketCategoryFieldDetail> thirdMarketCategoryFieldDetails = thirdMarketCategoryFieldDao.queryThirdMarketCategoryFieldDetail(dataSourceCode, marketCategoryId);
       if (CollectionUtils.isEmpty(thirdMarketCategoryFieldDetails)) {
            return new ArrayList<>();
        }
        return thirdMarketCategoryFieldDetails;
    }

    public int delRedisByAll(){
        //查询全部数据，并清理redis中缓存
        List<ThirdMarketCategoryField> resList = thirdMarketCategoryFieldMapper.selectByExample(new ThirdMarketCategoryFieldExample());
        for (ThirdMarketCategoryField item : resList) {
            redisService.del(RedisConfig.REDIS_KEY_DATABASE + "::ThirdMarketCategoryField:" + item.getId());
            redisService.del(RedisConfig.REDIS_KEY_DATABASE + "::ThirdMarketCategoryField:" + item.getMarketCategoryId() + "-" + item.getThirdSourceId());
        }
        return resList.size();
    }
}
