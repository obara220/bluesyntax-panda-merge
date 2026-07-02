package com.panda.merge.service.impl;

import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.dao.StandardMarketCategoryFieldDao;
import com.panda.merge.dto.StandardMarketCategoryFieldDetail;
import com.panda.merge.mapper.StandardMarketCategoryFieldMapper;
import com.panda.merge.model.StandardMarketCategoryField;
import com.panda.merge.model.StandardMarketCategoryFieldExample;
import com.panda.merge.service.StandardMarketCategoryFieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * 标准玩法信息 <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/14 <br>
 */
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class StandardMarketCategoryFieldServiceImpl implements StandardMarketCategoryFieldService {

    @Autowired
    private StandardMarketCategoryFieldMapper standardMarketCategoryFieldMapper;
    @Autowired
    private StandardMarketCategoryFieldDao standardMarketCategoryFieldDao;

    @Autowired
    private RedisService redisService;

    @Override
    @Cacheable(key = "'StandardMarketCategoryFieldList:'+ #marketCategoryId ", unless = "#result == null || #result.size() == 0")
    public List<StandardMarketCategoryFieldDetail> getItems(Long marketCategoryId) {
        return standardMarketCategoryFieldDao.getItemsByMarketCategoryId(marketCategoryId);
    }

    @Override
    public List<StandardMarketCategoryFieldDetail> getItems(List<Long> marketCategoryIds) {
        if (CollectionUtils.isEmpty(marketCategoryIds)) {
            return new LinkedList<>();
        }
        return standardMarketCategoryFieldDao.getItemsByMarketCategoryIds(marketCategoryIds);
    }

    @Override
    public List<StandardMarketCategoryField> getItemList() {
        StandardMarketCategoryFieldExample example = new StandardMarketCategoryFieldExample();
        return standardMarketCategoryFieldMapper.selectByExample(example);
    }

    public int delRedisByAll(){
        //查询全部数据，并清理redis中缓存
        List<StandardMarketCategoryField> resList = standardMarketCategoryFieldMapper.selectByExample(new StandardMarketCategoryFieldExample());
        for (StandardMarketCategoryField item : resList) {
            redisService.del(RedisConfig.REDIS_KEY_DATABASE + "::StandardMarketCategoryFieldList:" + item.getMarketCategoryId());
        }
        return resList.size();
    }

}
