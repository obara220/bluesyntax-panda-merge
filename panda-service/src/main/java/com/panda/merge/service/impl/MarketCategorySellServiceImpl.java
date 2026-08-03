package com.panda.merge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.bo.MarketCategorySellBO;
import com.panda.merge.common.RedisHelper;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.SaleMatchSellStausEnum;
import com.panda.merge.dao.MarketCategorySellDao;
import com.panda.merge.mapper.MarketCategorySellMapper;
import com.panda.merge.model.MarketCategorySell;
import com.panda.merge.model.MarketCategorySellExample;
import com.panda.merge.service.MarketCategorySellService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

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
 * @createDate 2020/8/18 <br>
 */
@Slf4j
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class MarketCategorySellServiceImpl implements MarketCategorySellService {

    @Autowired
    private MarketCategorySellMapper marketCategorySellMapper;

    @Autowired
    private MarketCategorySellDao marketCategorySellDao;

    @Resource
    private RedisService redisService;

    @Resource
    private RedisHelper redisHelper;

    @Override
    @Cacheable(key = "'MarketCategorySell:' + #matchId+ '-' + #marketCategoryId + '-' +#marketType ",unless = "#result == null ")
    public MarketCategorySell getItem(String linkId, Long matchId, Integer marketType, Long marketCategoryId) {
        StopWatch swCalculate = new StopWatch(UUID.randomUUID().toString());
        swCalculate.start("数据库玩法开售表耗时");
        MarketCategorySellExample marketCategorySellExample = new MarketCategorySellExample();
        marketCategorySellExample.createCriteria().andMatchIdEqualTo(matchId).
                andMarketCategoryIdEqualTo(marketCategoryId).
                andMarketTypeEqualTo(String.valueOf(marketType));
        List<MarketCategorySell> marketCategorySells = marketCategorySellMapper.selectByExample(marketCategorySellExample);
        swCalculate.stop();
        log.info("::{}::数据库玩法开售表耗时{}ms,标准玩法id={},类型：{}", linkId, swCalculate.getTotalTimeMillis(), marketCategoryId, marketType);
        if(CollectionUtils.isEmpty(marketCategorySells)){
            return null;
        }
        return marketCategorySells.get(0);
    }

    /**
     *
     * @param marketSellkeys 其中每个元素都是由matchId-marketCategoryId-marketType拼接起来的.
     * @return
     */
    @Override
    public List<MarketCategorySell> getItems(List<String> marketSellkeys) {
        if(CollectionUtils.isEmpty(marketSellkeys)) {
            return Collections.EMPTY_LIST;
        }
        List<MarketCategorySell> result = new ArrayList<>();
        List<String> requiredCallItems = new ArrayList<>();

        // Obtaining data from redis
        List<String> keys = marketSellkeys.stream().map(t-> RedisConfig.REDIS_KEY_DATABASE + "::MarketCategorySell:" + t).collect(Collectors.toList());
        List<Object> objectList= redisService.mGet(keys);
        redisHelper.postProcMget(marketSellkeys, objectList, result, requiredCallItems);
        if(CollectionUtils.isEmpty(requiredCallItems)){
            return result;
        }
        log.info("2724,查询标准玩法开售数据库：{}", requiredCallItems);
        MarketCategorySellExample example = new MarketCategorySellExample();
        for (String category : requiredCallItems) {
            String[] array = category.split("-");         // dataSourceCode: array   thirdMarketCategorySourceId: arr[1]
            if (array.length != 3) {
                throw new RuntimeException("[ThirdMarketCategoryServiceImpl] getItems parameter marketSellkeys's split array length is not equal to 3!");
            }
            example.or().andMatchIdEqualTo(Long.valueOf(array[0])).andMarketCategoryIdEqualTo(Long.valueOf(array[1])).andMarketTypeEqualTo(array[2]);
        }
        List<MarketCategorySell> marketCategorySells = marketCategorySellMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(marketCategorySells)){
            return result;
        }
        log.info("查询玩法开始表：{}", JSONObject.toJSONString(requiredCallItems));
        Map<String, MarketCategorySell> marketCategorySellsMap = marketCategorySells.stream().collect(Collectors.toMap(
                t->t.getMatchId()+"-"+t.getMarketCategoryId()+"-"+t.getMarketType(), Function.identity(), (v1, v2)->v1));
        List<MarketCategorySell> filteredMarketCategorySells =marketCategorySellsMap.values().stream().collect(Collectors.toList());

        result.addAll(filteredMarketCategorySells);
        // Storing the remained data into redis
        Map<String, Object> redisVal = filteredMarketCategorySells.stream().collect(Collectors.toMap(t->
                RedisConfig.REDIS_KEY_DATABASE + "::MarketCategorySell:" + t.getMatchId()+"-"+t.getMarketCategoryId()+"-"+t.getMarketType(), Function.identity(), (v1, v2) -> v1));
        redisService.mSet(redisVal);
        return result;
    }

    @Override
    public String getKey(Long matchId, Long marketCategoryId, Integer marketType) {
        return matchId + "-" + marketCategoryId + "-" + marketType;
    }

    @Override
    public List<MarketCategorySell> getItem(Long matchId, String marketType) {
        MarketCategorySellExample marketCategorySellExample = new MarketCategorySellExample();
        marketCategorySellExample.createCriteria().andMatchIdEqualTo(matchId).
                andMarketTypeEqualTo(marketType);
        return marketCategorySellMapper.selectByExample(marketCategorySellExample);
    }

    @Override
    @CachePut(key = "'MarketCategorySell:' + #marketCategorySell.matchId+ '-' + #marketCategorySell.marketCategoryId + '-' + #marketCategorySell.marketType")
    public MarketCategorySell update(MarketCategorySell marketCategorySell) {
        marketCategorySell.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        marketCategorySellMapper.updateByPrimaryKeySelective(marketCategorySell);
        if (marketCategorySell.getMarketType().equals("0")) {
            List<String> keys = new ArrayList<>();
            if (null != marketCategorySell.getAutoCloseMarket()) {
                keys.add(RedisConfig.REDIS_KEY_DATABASE + "::MarketCategorySellCacheAutoClose:" + marketCategorySell.getMatchId() + '-' + marketCategorySell.getAutoCloseMarket());
            }
            if (null != marketCategorySell.getAutoOpenMarket()) {
                keys.add(RedisConfig.REDIS_KEY_DATABASE + "::MarketCategorySellCacheAutoOpen:" + marketCategorySell.getMatchId() + '-' + marketCategorySell.getAutoOpenMarket());
            }
            if (!CollectionUtils.isEmpty(keys)) {
                redisService.del(keys);
            }
        }
        return marketCategorySell;
    }

    /**
     * 自动关盘查询盘口使用方法
     * @param matchId
     * @param periodId
     * @return
     */
    @Override
    public List<MarketCategorySell> getItemByPrimary(Long matchId, Long periodId) {
        MarketCategorySellExample marketCategorySellExample = new MarketCategorySellExample();
        //只有滚球才有自动关盘
        marketCategorySellExample.createCriteria().andMatchIdEqualTo(matchId).
                andAutoCloseMarketEqualTo(periodId.intValue()).andMarketTypeEqualTo("0");
        List<MarketCategorySell> marketCategorySells = marketCategorySellMapper.selectByExample(marketCategorySellExample);
        if (CollectionUtils.isEmpty(marketCategorySells)) {
            return null;
        }
        return marketCategorySells;
    }

    @Override
    @CachePut(key = "'MarketCategorySellCacheAutoClose:' + #matchId+ '-' + #periodId", unless = "#result == null ")
    public List<MarketCategorySellBO> getItemByPrimaryCache(Long matchId, Long periodId) {
        MarketCategorySellExample marketCategorySellExample = new MarketCategorySellExample();
        //只有滚球才有自动关盘
        marketCategorySellExample.createCriteria().andMatchIdEqualTo(matchId).
                andAutoCloseMarketEqualTo(periodId.intValue()).andMarketTypeEqualTo("0");
        List<MarketCategorySell> marketCategorySells = marketCategorySellMapper.selectByExample(marketCategorySellExample);
        if (CollectionUtils.isEmpty(marketCategorySells)) {
            return null;
        }
        List<MarketCategorySellBO> marketCategorySellBOs = new ArrayList<>();
        marketCategorySells.forEach(s->{
            MarketCategorySellBO marketCategorySellBO = new MarketCategorySellBO();
            BeanUtils.copyProperties(s, marketCategorySellBO);
            marketCategorySellBOs.add(marketCategorySellBO);
        });
        return marketCategorySellBOs;
    }

    /**
     * 自动开盘查询盘口使用方法
     * @param matchId
     * @param periodId
     * @return
     */
    @Override
    public List<MarketCategorySell> getItemByPrimaryOpen(Long matchId, Long periodId) {
        MarketCategorySellExample marketCategorySellExample = new MarketCategorySellExample();
        //只有滚球才有自动开盘
        marketCategorySellExample.createCriteria().andMatchIdEqualTo(matchId).
                andAutoOpenMarketEqualTo(periodId.intValue()).andMarketTypeEqualTo("0");
        List<MarketCategorySell> marketCategorySells = marketCategorySellMapper.selectByExample(marketCategorySellExample);
        if (CollectionUtils.isEmpty(marketCategorySells)) {
            return null;
        }
        return marketCategorySells;
    }

    /**
     * 自动开盘查询盘口使用方法
     * @param matchId
     * @param periodId
     * @return
     */
    @Override
    @CachePut(key = "'MarketCategorySellCacheAutoOpen:' + #matchId+ '-' + #periodId", unless = "#result == null ")
    public List<MarketCategorySellBO> getItemByPrimaryOpenCache(Long matchId, Long periodId) {
        MarketCategorySellExample marketCategorySellExample = new MarketCategorySellExample();
        //只有滚球才有自动开盘
        marketCategorySellExample.createCriteria().andMatchIdEqualTo(matchId).
                andAutoOpenMarketEqualTo(periodId.intValue()).andMarketTypeEqualTo("0");
        List<MarketCategorySell> marketCategorySells = marketCategorySellMapper.selectByExample(marketCategorySellExample);
        if (CollectionUtils.isEmpty(marketCategorySells)) {
            return null;
        }
        List<MarketCategorySellBO> marketCategorySellBOs = new ArrayList<>();
        marketCategorySells.forEach(s->{
            MarketCategorySellBO marketCategorySellBO = new MarketCategorySellBO();
            BeanUtils.copyProperties(s, marketCategorySellBO);
            marketCategorySellBOs.add(marketCategorySellBO);
        });
        return marketCategorySellBOs;
    }

    @Override
    public void saveBatch(Long standardMatchId, Integer marketType, List<MarketCategorySell> categorySellConfigurations) {
        marketCategorySellDao.saveBatch(categorySellConfigurations);
        List<String> redisKey = new ArrayList<>();
        Set<String> keys = new HashSet<>();
        categorySellConfigurations.forEach(sell -> {
            String key = RedisConfig.REDIS_KEY_DATABASE + "::MarketCategorySell:" + standardMatchId + "-" + sell.getMarketCategoryId() + "-" + sell.getMarketType();
            redisKey.add(key);
            if (sell.getMarketType().equals("0")) {
                if (null != sell.getAutoCloseMarket()) {
                    keys.add(RedisConfig.REDIS_KEY_DATABASE + "::MarketCategorySellCacheAutoClose:" + sell.getMatchId() + '-' + sell.getAutoCloseMarket());
                }
                if (null != sell.getAutoOpenMarket()) {
                    keys.add(RedisConfig.REDIS_KEY_DATABASE + "::MarketCategorySellCacheAutoOpen:" + sell.getMatchId() + '-' + sell.getAutoOpenMarket());
                }
            }
        });
        redisService.del(redisKey);
        if (!CollectionUtils.isEmpty(keys)) {
            redisService.delete(keys);
        }

    }

    @Override
    public void batchUpdate(Long standardMatchId, Integer marketType, List<MarketCategorySell> categorySellConfigurations) {
        marketCategorySellDao.batchUpdate(categorySellConfigurations);
        List<String> redisKey = new ArrayList<>();
        categorySellConfigurations.forEach(sell -> {
            String key = RedisConfig.REDIS_KEY_DATABASE + "::MarketCategorySell:" + standardMatchId + "-" + sell.getMarketCategoryId() + "-" + sell.getMarketType();
            redisKey.add(key);
        });
        redisService.del(redisKey);
    }

    @Override
    public void batchUpdateById(Long standardMatchId, Integer marketType, List<MarketCategorySell> categorySellConfigurations) {
        marketCategorySellDao.batchUpdateById(categorySellConfigurations);
        List<String> redisKey = new ArrayList<>();
        categorySellConfigurations.forEach(sell -> {
            String key = RedisConfig.REDIS_KEY_DATABASE + "::MarketCategorySell:" + standardMatchId + "-" + sell.getMarketCategoryId() + "-" + sell.getMarketType();
            redisKey.add(key);
        });
        redisService.del(redisKey);
    }

    @Override
    @CachePut(key = "'MarketCategorySell:' + #marketCategorySell.matchId+ '-' + #marketCategorySell.marketCategoryId + '-' + #marketCategorySell.marketType")
    public MarketCategorySell updateByItem(MarketCategorySell marketCategorySell) {
        MarketCategorySell marketCategorySell1 = getItem(marketCategorySell.getLinkId(), marketCategorySell.getMatchId(), Integer.valueOf(marketCategorySell.getMarketType()), marketCategorySell.getMarketCategoryId());
        BeanUtil.copyProperties(marketCategorySell,marketCategorySell1, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
        marketCategorySellDao.updateByItem(marketCategorySell);
        return marketCategorySell1;
    }

    @Override
    @CacheEvict(key = "'MarketCategorySell:' + #standardMatchId+ '-' +#marketCategoryId + '-' + #marketType")
    public void removeCache(Long standardMatchId, Integer marketType, Long marketCategoryId) {
    }

    @Override
    public void removeCashes(Long standardMatchId, Integer marketType, Collection<Long> marketCategoryIds) {
        if (CollectionUtils.isEmpty(marketCategoryIds)) {
            return;
        }
        redisService.delete(marketCategoryIds
                                    .stream()
                                    .map(id -> RedisConfig.REDIS_KEY_DATABASE + "::MarketCategorySell:" + getKey(standardMatchId, id, marketType))
                                    .collect(Collectors.toSet()));
    }

    @Override
    public List<MarketCategorySell> getItemByMatchId(Long matchId) {
        MarketCategorySellExample marketCategorySellExample = new MarketCategorySellExample();
        marketCategorySellExample.createCriteria().andMatchIdEqualTo(matchId);
        List<MarketCategorySell> marketCategorySells = marketCategorySellMapper.selectByExample(marketCategorySellExample);
        if (CollectionUtils.isEmpty(marketCategorySells)) {
            return null;
        }
        return marketCategorySells;
    }

    @Override
    public List<MarketCategorySell> getItem(Long matchId, List<Long> marketCategoryIds) {
        MarketCategorySellExample marketCategorySellExample = new MarketCategorySellExample();
        marketCategorySellExample.createCriteria()
                .andMatchIdEqualTo(matchId).andSellStatusEqualTo(SaleMatchSellStausEnum.Sold.name())
                .andDataSourceCodeIsNotNull().andMarketCategoryIdIn(marketCategoryIds);
        List<MarketCategorySell> marketCategorySells = marketCategorySellMapper.selectByExample(marketCategorySellExample);
        if (CollectionUtils.isEmpty(marketCategorySells)) {
            return null;
        }
        return marketCategorySells;
    }
    @Override
    public List<MarketCategorySell> getItemByDataSourceCodeAndMarketType(Long matchId, String dataSourceCode, String marketType) {
        MarketCategorySellExample marketCategorySellExample = new MarketCategorySellExample();
        marketCategorySellExample.createCriteria()
                .andMatchIdEqualTo(matchId).andSellStatusEqualTo(SaleMatchSellStausEnum.Sold.name())
                .andDataSourceCodeEqualTo(dataSourceCode).andMarketTypeEqualTo(marketType);
        List<MarketCategorySell> marketCategorySells = marketCategorySellMapper.selectByExample(marketCategorySellExample);
        if (CollectionUtils.isEmpty(marketCategorySells)) {
            return null;
        }
        return marketCategorySells;
    }

    @Override
    public List<MarketCategorySell> getItemByMarketType(Long matchId,  String marketType) {
        MarketCategorySellExample marketCategorySellExample = new MarketCategorySellExample();
        marketCategorySellExample.createCriteria()
                .andMatchIdEqualTo(matchId).andSellStatusEqualTo(SaleMatchSellStausEnum.Sold.name()).andMarketTypeEqualTo(marketType);
        List<MarketCategorySell> marketCategorySells = marketCategorySellMapper.selectByExample(marketCategorySellExample);
        if (CollectionUtils.isEmpty(marketCategorySells)) {
            return null;
        }
        return marketCategorySells;
    }

}
