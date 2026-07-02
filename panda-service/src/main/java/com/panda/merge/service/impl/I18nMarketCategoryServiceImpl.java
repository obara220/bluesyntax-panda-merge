package com.panda.merge.service.impl;

import com.panda.merge.config.RedisConfig;
import com.panda.merge.dao.I18nMarketCategoryDao;
import com.panda.merge.mapper.I18nMarketCategoryMapper;
import com.panda.merge.model.I18nMarketCategory;
import com.panda.merge.model.I18nMarketCategoryExample;
import com.panda.merge.model.ThirdMarketCategoryFieldExample;
import com.panda.merge.service.I18nMarketCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : bevan
 * @project Name : panda-merge
 * @package Name : com.panda.merge.service.impl
 * @description : TODO
 * @date: 2020-09-11 9:52
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */
@Slf4j
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class I18nMarketCategoryServiceImpl extends BaseServiceImpl<I18nMarketCategory> implements I18nMarketCategoryService {

    @Autowired
    private I18nMarketCategoryMapper i18nMarketCategoryMapper;

    @Autowired
    private I18nMarketCategoryDao i18nMarketCategoryDao;


    @Override
    //@Cacheable(key = "'I18nMarketCategoryMap:' + #nameCode", unless = "#result == null || #result.size() == 0")
    public Map<String, I18nMarketCategory> queryLanguageInternation(String dataSourceCode, Long nameCode) {
        I18nMarketCategoryExample example = new I18nMarketCategoryExample();
        if(StringUtils.isNotBlank(dataSourceCode)){
            example.createCriteria().andDataSourceCodeEqualTo(dataSourceCode).andNameCodeEqualTo(nameCode);
        }else{
            example.createCriteria().andNameCodeEqualTo(nameCode);
        }
        List<I18nMarketCategory> marketCategories = i18nMarketCategoryMapper.selectByExample(example);
        marketCategories = marketCategories.stream().sorted(Comparator.comparing(I18nMarketCategory::getId).reversed())
                .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(imc -> imc.getNameCode() + ";" + imc.getLanguageType() ))), ArrayList::new));
        return marketCategories.stream().collect(Collectors.toMap(I18nMarketCategory::getLanguageType, thi -> thi));
    }

    @Override
    public Map<Long,List<I18nMarketCategory>> getItemsByNameCodes(List<Long> nameCodes){
        Map<Long,List<I18nMarketCategory>> nameCode2Languages = new LinkedHashMap<>();
        if(!CollectionUtils.isEmpty(nameCodes)){
            I18nMarketCategoryExample example = new I18nMarketCategoryExample();
            example.createCriteria().andNameCodeIn(nameCodes);
            List<I18nMarketCategory> resList = i18nMarketCategoryMapper.selectByExample(example);
            if(!CollectionUtils.isEmpty(resList)){
                nameCode2Languages = resList.stream().collect(Collectors.groupingBy(obj->obj.getNameCode()));
            }
        }
        return nameCode2Languages;
    }

    @Override
    public List<I18nMarketCategory> getItemsByDatasourceLangNameCode(List<String> datasourceLangAndNameCode) {
        I18nMarketCategoryExample example = new I18nMarketCategoryExample();
        for (String category : datasourceLangAndNameCode) {
            String[] array = category.split("-");         // dataSourceCode: array   thirdMarketCategorySourceId: arr[1]
            if (array.length != 3) {
                throw new RuntimeException("[I18nMarketCategoryServiceImpl] getItemsByDatasourceLangNameCode category's split array length is not equal to 3!");
            }
            example.or().andDataSourceCodeEqualTo(array[0]).andLanguageTypeEqualTo(array[1]).andNameCodeEqualTo(Long.parseLong(array[2]));
        }
        return i18nMarketCategoryMapper.selectByExample(example);
    }

    @Override
    public void saveBatch(List<I18nMarketCategory> i18nMarketCategories) {
        i18nMarketCategoryDao.saveBatch(i18nMarketCategories);
        //删除执行更新操作的缓存
        Set<Long> nameCodes = i18nMarketCategories.stream().map(obj -> obj.getNameCode()).collect(Collectors.toSet());
        for (Long nameCode: nameCodes) {
            redisService.del(RedisConfig.REDIS_KEY_DATABASE + "::I18nMarketCategoryMap:"+ nameCode);
        }
    }

    @Override
    public void updateBatchById(List<I18nMarketCategory> i18nMarketCategories) {
        i18nMarketCategoryDao.updateBatchById(i18nMarketCategories);
        //更新缓存
        for (I18nMarketCategory upItem: i18nMarketCategories) {
            refreshHashCache(RedisConfig.REDIS_KEY_DATABASE + "::I18nMarketCategoryMap:"+ upItem.getNameCode(),upItem.getLanguageType(),upItem);
        }
    }

}
