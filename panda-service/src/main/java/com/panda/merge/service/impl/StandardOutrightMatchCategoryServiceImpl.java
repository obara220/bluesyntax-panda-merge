package com.panda.merge.service.impl;

import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.constant.SaleMatchSellStausEnum;
import com.panda.merge.mapper.StandardOutrightMatchCategoryMapper;
import com.panda.merge.model.StandardOutrightMatchCategory;
import com.panda.merge.model.StandardOutrightMatchCategoryExample;
import com.panda.merge.service.StandardOutrightMatchCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @Author: Kepa
 * @Date: 2020/9/29 20:19
 */
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class StandardOutrightMatchCategoryServiceImpl implements StandardOutrightMatchCategoryService {

    @Autowired
    private StandardOutrightMatchCategoryMapper standardOutrightMatchCategoryMapper;

    @Override
    @Cacheable(key = "'StandardOutrightMatchCategory:' + #standardMatchId +  '-' + #categoryId", unless = "#result == null ")
    public StandardOutrightMatchCategory getItem(Long standardMatchId, Long categoryId) {
        StandardOutrightMatchCategoryExample example = new StandardOutrightMatchCategoryExample();
        example.createCriteria().andStandardMatchIdEqualTo(standardMatchId).andIdEqualTo(categoryId);
        List<StandardOutrightMatchCategory> categoryList = standardOutrightMatchCategoryMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(categoryList)) {
            return null;
        }
        return categoryList.get(0);
    }

    @Override
    @CacheEvict(key = "'StandardOutrightMatchCategory:' + #standardMatchId +  '-' + #categoryId")
    public void updateStandardOutrightMatchCategory(Long standardMatchId, Long categoryId) {
        StandardOutrightMatchCategory record = new StandardOutrightMatchCategory();
        record.setCategorySellStatus(SaleMatchSellStausEnum.Sold.name());
        record.setModfiyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        StandardOutrightMatchCategoryExample example = new StandardOutrightMatchCategoryExample();
        example.createCriteria().andStandardMatchIdEqualTo(standardMatchId).andIdEqualTo(categoryId);
        standardOutrightMatchCategoryMapper.updateByExampleSelective(record, example);
    }

    @Override
    @CacheEvict(key = "'StandardOutrightMatchCategory:' + #standardMatchId +  '-' + #categoryId")
    public void removeCache(Long standardMatchId, Long categoryId) {

    }

}
