package com.panda.merge.service.impl;

import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.TradeCategoryAutoDiffConfigItemDTO;
import com.panda.merge.mapper.ConfigCategoryAutoDiffTradeMapper;
import com.panda.merge.model.ConfigCategoryAutoDiffTrade;
import com.panda.merge.model.ConfigCategoryAutoDiffTradeExample;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.service.ConfigCategoryAutoDiffTradeService;
import com.panda.merge.service.StandardMatchInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import java.util.*;

@Slf4j
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class ConfigCategoryAutoDiffTradeServiceImpl implements ConfigCategoryAutoDiffTradeService {

    @Autowired
    ConfigCategoryAutoDiffTradeMapper configCategoryAutoDiffTradeMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private ConfigCategoryAutoDiffTradeService configCategoryAutoDiffTradeService;

    @Autowired
    private BaseProcessor baseProcessor;

    @Autowired
    private StandardMatchInfoService standardMatchInfoService;

    private static String REDIS_KEY_GATEGORY = "ODDS_DIFF_CATEGORY:";


    @Override
    public ConfigCategoryAutoDiffTrade getItem(String linkId, Long matchId, Long categoryId,Long childCategoryId) {
        StopWatch swCalculate = new StopWatch(UUID.randomUUID().toString());
        swCalculate.start("数据库查询玩法自动水差耗时");
        Map<Long,ConfigCategoryAutoDiffTrade> configDiffTradeMap =(Map<Long, ConfigCategoryAutoDiffTrade>) redisService.hGet(REDIS_KEY_GATEGORY + matchId, categoryId +"");
        if(CollectionUtils.isEmpty(configDiffTradeMap)){
            ConfigCategoryAutoDiffTradeExample example = new ConfigCategoryAutoDiffTradeExample();
            example.createCriteria()
                .andStandardMatchIdEqualTo(matchId)
                .andStandardCategoryIdEqualTo(categoryId)
                .andChildStandardCategoryIdEqualTo(childCategoryId);
            List<ConfigCategoryAutoDiffTrade> list = configCategoryAutoDiffTradeMapper.selectByExample(example);
            if(CollectionUtils.isEmpty(list)){
                return null;
            }
            ConfigCategoryAutoDiffTrade categoryAutoDiffTrade = list.get(0);
            Map<Long,ConfigCategoryAutoDiffTrade> newConfigDiffMap = new HashMap<>();
            newConfigDiffMap.put(childCategoryId, categoryAutoDiffTrade);
            redisService.hSet(REDIS_KEY_GATEGORY + matchId, categoryId + "", newConfigDiffMap, RedisConfig.REDIS_MY_TIME);
            swCalculate.stop();
            log.info("::{}::Redis缓存为空，从DB查询玩法水差耗时{}ms,标准玩法id={},子玩法id={},diffValue={}" , linkId, swCalculate.getTotalTimeMillis(), categoryId, childCategoryId, categoryAutoDiffTrade.getDiffValue());
            return categoryAutoDiffTrade;
        }
        ConfigCategoryAutoDiffTrade categoryAutoDiffTrade = configDiffTradeMap.get(childCategoryId);
        if(categoryAutoDiffTrade == null){
            ConfigCategoryAutoDiffTradeExample example = new ConfigCategoryAutoDiffTradeExample();
            example.createCriteria()
                .andStandardMatchIdEqualTo(matchId)
                .andStandardCategoryIdEqualTo(categoryId)
                .andChildStandardCategoryIdEqualTo(childCategoryId);
            List<ConfigCategoryAutoDiffTrade> list = configCategoryAutoDiffTradeMapper.selectByExample(example);
            if(!CollectionUtils.isEmpty(list)){
                categoryAutoDiffTrade = list.get(0);
                configDiffTradeMap.put(childCategoryId, categoryAutoDiffTrade);
                redisService.hSet(REDIS_KEY_GATEGORY + matchId, categoryId + "", configDiffTradeMap, RedisConfig.REDIS_MY_TIME);
            }
        }
        swCalculate.stop();
        log.info("::{}::数据库查询玩法水差耗时{}ms,标准玩法id={},子玩法id={},diffValue={}" , linkId, swCalculate.getTotalTimeMillis(), categoryId, childCategoryId, categoryAutoDiffTrade != null ? categoryAutoDiffTrade.getDiffValue() : "null");
        return categoryAutoDiffTrade;
    }

    @Override
    public ConfigCategoryAutoDiffTrade create(String linkId, TradeCategoryAutoDiffConfigItemDTO tradeCategoryAutoDiffConfigItemDTO, Long matchId, Long operaterId) {
        ConfigCategoryAutoDiffTrade configCategoryAutoDiffTrade = new ConfigCategoryAutoDiffTrade();
        configCategoryAutoDiffTrade.setId(UUIdUtils.getId());
        configCategoryAutoDiffTrade.setStandardMatchId(matchId);
        configCategoryAutoDiffTrade.setStandardCategoryId(tradeCategoryAutoDiffConfigItemDTO.getMarketCategoryId());
        configCategoryAutoDiffTrade.setChildStandardCategoryId(tradeCategoryAutoDiffConfigItemDTO.getChildStandardCategoryId());
        configCategoryAutoDiffTrade.setOddsType(tradeCategoryAutoDiffConfigItemDTO.getOddType());
        configCategoryAutoDiffTrade.setDiffValue(tradeCategoryAutoDiffConfigItemDTO.getDiffValue());
        configCategoryAutoDiffTrade.setLinkId(linkId);
        configCategoryAutoDiffTrade.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
        configCategoryAutoDiffTrade.setOperaterId(operaterId);
        StandardMatchInfo standMatchInfo = standardMatchInfoService.getItem(matchId);
        Long expireTime = baseProcessor.marketCacheTime(standMatchInfo.getBeginTime());
        Map<Long,ConfigCategoryAutoDiffTrade> configDiffMap = new HashMap<>();
        if(redisService.hHasKey(REDIS_KEY_GATEGORY+matchId,tradeCategoryAutoDiffConfigItemDTO.getMarketCategoryId()+"")){
            configDiffMap = (Map<Long, ConfigCategoryAutoDiffTrade>) redisService.hGet(REDIS_KEY_GATEGORY + matchId, tradeCategoryAutoDiffConfigItemDTO.getMarketCategoryId() + "");
        }
        configDiffMap.put(tradeCategoryAutoDiffConfigItemDTO.getChildStandardCategoryId(),configCategoryAutoDiffTrade);
        redisService.hSet(REDIS_KEY_GATEGORY+matchId,tradeCategoryAutoDiffConfigItemDTO.getMarketCategoryId()+"",
                configDiffMap,expireTime);
        return configCategoryAutoDiffTrade;
    }

    @Override
    public ConfigCategoryAutoDiffTrade updata(ConfigCategoryAutoDiffTrade diffTrade) {
        configCategoryAutoDiffTradeMapper.updateByPrimaryKey(diffTrade);
        Map<Long,ConfigCategoryAutoDiffTrade> configDiffMap = new HashMap<>();
        Object cached = redisService.hGet(REDIS_KEY_GATEGORY+diffTrade.getStandardMatchId(),diffTrade.getStandardCategoryId()+"");
        if(cached != null){
            configDiffMap = (Map<Long, ConfigCategoryAutoDiffTrade>) cached;
        }
        configDiffMap.put(diffTrade.getChildStandardCategoryId(),diffTrade);
        StandardMatchInfo standMatchInfo = standardMatchInfoService.getItem(diffTrade.getStandardMatchId());
        Long expireTime = baseProcessor.marketCacheTime(standMatchInfo.getBeginTime());
        redisService.hSet(REDIS_KEY_GATEGORY+diffTrade.getStandardMatchId(),diffTrade.getStandardCategoryId()+"",
                configDiffMap,expireTime);
        log.info("更新玩法水差配置成功,matchId={},categoryId={},childCategoryId={},diffValue={}", 
            diffTrade.getStandardMatchId(), diffTrade.getStandardCategoryId(), 
            diffTrade.getChildStandardCategoryId(), diffTrade.getDiffValue());
        return diffTrade;
    }
    @Override
    public void delDiffByMatchInfoId(Long matchId, String linkId) {
        Map<String, Object> objectObjectMap = redisService.hGetAll(REDIS_KEY_GATEGORY + matchId);
        if(!CollectionUtils.isEmpty(objectObjectMap)){
            Set<String> allCategoryIds = objectObjectMap.keySet();
            redisService.hDel(REDIS_KEY_GATEGORY + matchId,allCategoryIds.toArray());
            //log.info("::{}::标准赛事ID:{},清除玩法水差配置成功,赛事下全清,KEY:{}", linkId, matchId, objectObjectMap);
        }
    }
    @Override
    public void delDiffByMatchIdAndCategoryList(String linkId, Long matchId, List<Long> categoryList) {
        for (Long categoryId : categoryList ) {
            Map<Long,ConfigCategoryAutoDiffTrade> configDiffTradeMap = (Map<Long, ConfigCategoryAutoDiffTrade>) redisService.hGet(REDIS_KEY_GATEGORY + matchId, categoryId + "");
            //log.info("::{}::标准赛事ID:{},categoryId:{},清除玩法水差配置成功,KEY:{}", linkId, matchId,categoryId, configDiffTradeMap);
        }
        redisService.hDel(REDIS_KEY_GATEGORY + matchId,categoryList.toArray());
    }



    //--------------------------------------------- 492187 水差优化----------------------------------------------------------------
   /* @Override
    @Cacheable(key = "'ConfigCategoryAutoDiffTrade:' + #matchId+'-'+#categoryId+'-'+#childCategoryId",unless = "#result == null ")
    public ConfigCategoryAutoDiffTrade getItem(String linkId, Long matchId, Long categoryId,Long childCategoryId) {
        StopWatch swCalculate = new StopWatch(UUID.randomUUID().toString());
        swCalculate.start("数据库查询玩法自动水差耗时");
        ConfigCategoryAutoDiffTradeExample configCategoryAutoDiffTradeExample = new ConfigCategoryAutoDiffTradeExample();
        configCategoryAutoDiffTradeExample.createCriteria()
                .andStandardMatchIdEqualTo(matchId)
                .andChildStandardCategoryIdEqualTo(childCategoryId)
                .andStandardCategoryIdEqualTo(categoryId);
        List<ConfigCategoryAutoDiffTrade> list = configCategoryAutoDiffTradeMapper.selectByExample(configCategoryAutoDiffTradeExample);
        swCalculate.stop();
        log.info("::{}::数据库查询玩法自动水差耗时{}ms,标准玩法id={},子玩法id：{}" , linkId, swCalculate.getTotalTimeMillis(),categoryId,childCategoryId);
        if (CollectionUtils.isEmpty(list))
        {
            return null;
        }
        return list.get(0);
    }

    @Override
    @CachePut(key = "'ConfigCategoryAutoDiffTrade:' + #matchId+'-'+#tradeCategoryAutoDiffConfigItemDTO.marketCategoryId+'-'+#tradeCategoryAutoDiffConfigItemDTO.childStandardCategoryId")
    public ConfigCategoryAutoDiffTrade create(String linkId, TradeCategoryAutoDiffConfigItemDTO tradeCategoryAutoDiffConfigItemDTO, Long matchId, Long operaterId) {
        ConfigCategoryAutoDiffTrade configCategoryAutoDiffTrade = new ConfigCategoryAutoDiffTrade();
        configCategoryAutoDiffTrade.setId(UUIdUtils.getId());
        configCategoryAutoDiffTrade.setStandardMatchId(matchId);
        configCategoryAutoDiffTrade.setStandardCategoryId(tradeCategoryAutoDiffConfigItemDTO.getMarketCategoryId());
        configCategoryAutoDiffTrade.setChildStandardCategoryId(tradeCategoryAutoDiffConfigItemDTO.getChildStandardCategoryId());
        configCategoryAutoDiffTrade.setOddsType(tradeCategoryAutoDiffConfigItemDTO.getOddType());
        configCategoryAutoDiffTrade.setDiffValue(tradeCategoryAutoDiffConfigItemDTO.getDiffValue());
        configCategoryAutoDiffTrade.setLinkId(linkId);
        configCategoryAutoDiffTrade.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
        configCategoryAutoDiffTrade.setOperaterId(operaterId);
        configCategoryAutoDiffTradeMapper.insert(configCategoryAutoDiffTrade);
        return configCategoryAutoDiffTrade;
    }

    @Override
    @CacheEvict(key = "'ConfigCategoryAutoDiffTrade:' + #configCategoryAutoDiffTrade.standardMatchId+'-'+#configCategoryAutoDiffTrade.standardCategoryId+'-'+#configCategoryAutoDiffTrade.childStandardCategoryId")
    public ConfigCategoryAutoDiffTrade updata(ConfigCategoryAutoDiffTrade configCategoryAutoDiffTrade) {
        configCategoryAutoDiffTradeMapper.updateByPrimaryKey(configCategoryAutoDiffTrade);
        return configCategoryAutoDiffTrade;
    }

    @Override
    @CacheEvict(key = "'ConfigCategoryAutoDiffTrade:' + #configCategoryAutoDiffTrade.standardMatchId+'-'+#configCategoryAutoDiffTrade.standardCategoryId+'-'+#configCategoryAutoDiffTrade.childStandardCategoryId")
    public void del(ConfigCategoryAutoDiffTrade configCategoryAutoDiffTrade) {
        configCategoryAutoDiffTradeMapper.deleteByPrimaryKey(configCategoryAutoDiffTrade.getId());
    }

    @Override
    public void delDiffByMatchInfoId(Long matchId, String linkId) {
        ConfigCategoryAutoDiffTradeExample configCategoryAutoDiffTradeExample = new ConfigCategoryAutoDiffTradeExample();
        configCategoryAutoDiffTradeExample.createCriteria().andStandardMatchIdEqualTo(matchId);
        List<ConfigCategoryAutoDiffTrade> list = configCategoryAutoDiffTradeMapper.selectByExample(configCategoryAutoDiffTradeExample);
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        clearCache(linkId, matchId, list);
    }

    @Override
    public void delDiffByMatchIdAndCategoryList(String linkId, Long matchId, List<Long> categoryList) {
        ConfigCategoryAutoDiffTradeExample configCategoryAutoDiffTradeExample = new ConfigCategoryAutoDiffTradeExample();
        configCategoryAutoDiffTradeExample.createCriteria().andStandardMatchIdEqualTo(matchId).andStandardCategoryIdIn(categoryList);
        List<ConfigCategoryAutoDiffTrade> list = configCategoryAutoDiffTradeMapper.selectByExample(configCategoryAutoDiffTradeExample);
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        clearCache(linkId, matchId, list);
    }

    private void clearCache(String linkId, Long matchId, List<ConfigCategoryAutoDiffTrade> list) {
        for (ConfigCategoryAutoDiffTrade autoDiffTrade : list) {
            configCategoryAutoDiffTradeService.del(autoDiffTrade);
            log.info("::{}::标准赛事ID:{},清除玩法水差配置成功,KEY:{}", linkId, matchId, JSON.toJSONString(autoDiffTrade));
        }
    }*/
}
