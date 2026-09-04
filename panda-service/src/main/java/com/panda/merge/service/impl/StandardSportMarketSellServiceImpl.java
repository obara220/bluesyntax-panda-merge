package com.panda.merge.service.impl;

import com.panda.merge.common.RedisHelper;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.dao.StandardSportMarketSellDao;
import com.panda.merge.mapper.StandardSportMarketSellMapper;
import com.panda.merge.model.StandardSportMarketSell;
import com.panda.merge.model.StandardSportMarketSellExample;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.service.StandardSportMarketSellService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/14 <br>
 */
@Slf4j
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class StandardSportMarketSellServiceImpl extends BaseServiceImpl<StandardSportMarketSell> implements StandardSportMarketSellService {


    @Autowired
    private StandardSportMarketSellMapper standardSportMarketSellMapper;

    @Autowired
    private StandardSportMarketSellDao standardSportMarketSellDao;

    @Autowired
    private StandardMatchInfoService standardMatchInfoService;

    @Resource
    private RedisHelper redisHelper;

    @Override
    @Cacheable(key = "'StandardSportMarketSell:' + #standardMatchId",unless = "#result == null ")
    public StandardSportMarketSell getItem(Long standardMatchId) {
        return getStandardSportMarketSell(standardMatchId);
    }

    @Override
    public List<StandardSportMarketSell> getItems(List<Long> standardMatchIds) {
        if(CollectionUtils.isEmpty(standardMatchIds)) {
            return Collections.EMPTY_LIST;
        }
        List<StandardSportMarketSell> result = new ArrayList<>();
        List<Long> requiredCallItems = new ArrayList<>();

        // Obtaining data from redis
        List<String> keys = standardMatchIds.stream().map(t-> RedisConfig.REDIS_KEY_DATABASE + "::StandardSportMarketSell:" + t).collect(Collectors.toList());
        List<Object> objectList= redisService.mGet(keys);
        redisHelper.postProcMget(standardMatchIds, objectList, result, requiredCallItems);
        if(CollectionUtils.isEmpty(requiredCallItems)){
            return result;
        }

        // Obtaining remained data from mysql
        StandardSportMarketSellExample example = new StandardSportMarketSellExample();
        example.createCriteria().andMatchInfoIdIn(requiredCallItems);
        List<StandardSportMarketSell> standardSportMarketSells = standardSportMarketSellMapper.selectByExample(example);
        Map<Long, StandardSportMarketSell> sportMarketSellsMap = standardSportMarketSells.stream()
                .collect(Collectors.toMap(StandardSportMarketSell::getMatchInfoId, Function.identity(), (v1, v2)->v1));
        List<StandardSportMarketSell> sportMarketSells = sportMarketSellsMap.values().stream().collect(Collectors.toList());
        result.addAll(sportMarketSells);

        // Storing the remained data into redis
        Map<String, Object> redisVal = sportMarketSells.stream().collect(Collectors.toMap(t->RedisConfig.REDIS_KEY_DATABASE + "::StandardSportMarketSell:" + t.getId(), Function.identity(), (v1, v2) -> v1));
        redisService.mSet(redisVal);
        return result;
    }

    @Override
    @CacheEvict(key = "'StandardSportMarketSell:' + #item.matchInfoId")
    public StandardSportMarketSell update(StandardSportMarketSell item) {
        item.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        standardSportMarketSellMapper.updateByPrimaryKeySelective(item);
        return item;
    }

    @Override
//    @CachePut(key = "'StandardSportMarketSell:' + #standardMatchId",unless = "#result == null ")
    public StandardSportMarketSell refreshCache(Long standardMatchId) {
        return refreshCache(getStandardSportMarketSell(standardMatchId));
    }

    @Override
    public StandardSportMarketSell refreshCache(StandardSportMarketSell item){
        if(null != item){
            redisService.set(RedisConfig.REDIS_KEY_DATABASE + "::StandardSportMarketSell:" + item.getMatchInfoId(), item, RedisConfig.REDIS_MY_TIME);
        }
        return item;
    }

    @Override
    public void evictCache(Long standardMatchId) {
        if (standardMatchId == null) {
            log.warn("standard sport market sell  evict cache invalid match id");
            return;
        }
        redisService.del(RedisConfig.REDIS_KEY_DATABASE + "::StandardSportMarketSell:" + standardMatchId);
    }


    private StandardSportMarketSell getStandardSportMarketSell(Long standardMatchId) {
        if(standardMatchId == null){
            return null;
        }
        //刷新标准赛事缓存
        standardMatchInfoService.getItemByPrimaryKey(standardMatchId);
        //刷新开售信息缓存
        StandardSportMarketSellExample standardSportMarketSellExample = new StandardSportMarketSellExample();
        standardSportMarketSellExample.createCriteria().andMatchInfoIdEqualTo(standardMatchId);
        List<StandardSportMarketSell> standardSportMarketSells = standardSportMarketSellMapper.selectByExample(standardSportMarketSellExample);
        if(CollectionUtils.isEmpty(standardSportMarketSells)){
            return null;
        }
        return standardSportMarketSells.get(0);
    }

    @Override
    public List<Long> getMatchSellMatchIdByExample() {
        return standardSportMarketSellDao.getMatchSellMatchIdByExample();
    }
}
