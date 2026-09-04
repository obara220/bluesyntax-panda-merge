package com.panda.merge.service.impl;

import com.panda.merge.config.RedisConfig;
import com.panda.merge.dao.ThirdSportMarketCategoryDao;
import com.panda.merge.mapper.ThirdSportMarketCategoryMapper;
import com.panda.merge.model.ThirdMarketCategory;
import com.panda.merge.model.ThirdMarketCategoryExample;
import com.panda.merge.model.ThirdSportMarketCategory;
import com.panda.merge.model.ThirdSportMarketCategoryExample;
import com.panda.merge.service.ThirdSportMarketCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author : bevan
 * @project Name : panda-merge
 * @package Name : com.panda.merge.service.impl
 * @date: 2020-09-11 9:39
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */
@Service
public class ThirdSportMarketCategoryServiceImpl implements ThirdSportMarketCategoryService {

    @Autowired
    private ThirdSportMarketCategoryMapper thirdSportMarketCategoryMapper;

    @Autowired
    private ThirdSportMarketCategoryDao thirdSportMarketCategoryDao;

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

    public int delRedisByAll(){
        return 0;
    }

    @Override
    public List<ThirdMarketCategory> queryThirdMarketCategoryList(List<Long> referenceIds, List<Long> sportIds) {
        return thirdSportMarketCategoryMapper.queryThirdMarketCategoryList(referenceIds, sportIds);
    }
}
