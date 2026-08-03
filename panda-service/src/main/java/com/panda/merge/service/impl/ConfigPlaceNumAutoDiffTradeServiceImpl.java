package com.panda.merge.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.TradePlaceNumAutoDiffConfigItemDTO;
import com.panda.merge.mapper.ConfigPlacenumAutoDiffTradeMapper;
import com.panda.merge.model.ConfigCategoryAutoDiffTrade;
import com.panda.merge.model.ConfigPlacenumAutoDiffTrade;
import com.panda.merge.model.ConfigPlacenumAutoDiffTradeExample;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.service.ConfigPlaceNumAutoDiffTradeService;
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
public class ConfigPlaceNumAutoDiffTradeServiceImpl implements ConfigPlaceNumAutoDiffTradeService {
    @Autowired
    ConfigPlacenumAutoDiffTradeMapper configPlacenumAutoDiffTradeMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private ConfigPlaceNumAutoDiffTradeService configPlaceNumAutoDiffTradeService;

    @Autowired
    private BaseProcessor baseProcessor;

    @Autowired
    private StandardMatchInfoService standardMatchInfoService;

    private static String REDIS_KEY_PLACENUM = "ODDS_DIFF_PLACENUM:";

    private static String STR_JOIN = ":";
//    private static Integer REDIS_EXPIR_TIME = 86400;

    @Override
    public ConfigPlacenumAutoDiffTrade getItem(String linkId,Long matchId,Long categoryId, Long childCategoryId, Integer placeNum) {
        if (ObjectUtil.isEmpty(placeNum))
        {
            placeNum = 1;
        }
        StopWatch swCalculate = new StopWatch(UUID.randomUUID().toString());
        swCalculate.start("数据库查询坑位水差耗时");
        Map<String, ConfigPlacenumAutoDiffTrade> configDiffTradeMap =(Map<String, ConfigPlacenumAutoDiffTrade>) redisService.hGet(REDIS_KEY_PLACENUM + matchId, categoryId +"");
        if(CollectionUtils.isEmpty(configDiffTradeMap)){
            ConfigPlacenumAutoDiffTradeExample example = new ConfigPlacenumAutoDiffTradeExample();
            example.createCriteria()
                .andStandardMatchIdEqualTo(matchId)
                .andStandardCategoryIdEqualTo(categoryId)
                .andChildStandardCategoryIdEqualTo(childCategoryId)
                .andPlaceNumEqualTo(placeNum);
            List<ConfigPlacenumAutoDiffTrade> list = configPlacenumAutoDiffTradeMapper.selectByExample(example);
            if(CollectionUtils.isEmpty(list)){
                return null;
            }
            ConfigPlacenumAutoDiffTrade placeNumAutoDiffTrade = list.get(0);
            Map<String, ConfigPlacenumAutoDiffTrade> newConfigDiffMap = new HashMap<>();
            newConfigDiffMap.put(childCategoryId + STR_JOIN + placeNum, placeNumAutoDiffTrade);
            redisService.hSet(REDIS_KEY_PLACENUM + matchId, categoryId + "", newConfigDiffMap, RedisConfig.REDIS_MY_TIME);
            swCalculate.stop();
            log.info("::{}::Redis缓存为空，从DB查询坑位水差耗时{}ms,标准玩法id={},子玩法id={},placeNum={},diffValue={}" , linkId, swCalculate.getTotalTimeMillis(), categoryId, childCategoryId, placeNum, placeNumAutoDiffTrade.getDiffValue());
            return placeNumAutoDiffTrade;
        }
        ConfigPlacenumAutoDiffTrade marketAutoDiffTrade = configDiffTradeMap.get(childCategoryId + STR_JOIN + placeNum);
        if(marketAutoDiffTrade == null){
            ConfigPlacenumAutoDiffTradeExample example = new ConfigPlacenumAutoDiffTradeExample();
            example.createCriteria()
                .andStandardMatchIdEqualTo(matchId)
                .andStandardCategoryIdEqualTo(categoryId)
                .andChildStandardCategoryIdEqualTo(childCategoryId)
                .andPlaceNumEqualTo(placeNum);
            List<ConfigPlacenumAutoDiffTrade> list = configPlacenumAutoDiffTradeMapper.selectByExample(example);
            if(!CollectionUtils.isEmpty(list)){
                marketAutoDiffTrade = list.get(0);
                configDiffTradeMap.put(childCategoryId + STR_JOIN + placeNum, marketAutoDiffTrade);
                redisService.hSet(REDIS_KEY_PLACENUM + matchId, categoryId + "", configDiffTradeMap, RedisConfig.REDIS_MY_TIME);
            }
        }
        swCalculate.stop();
        log.info("::{}::数据库查询坑位水差耗时{}ms,标准玩法id={},子玩法id={},placeNum={},diffValue={}", linkId, swCalculate.getTotalTimeMillis(), categoryId, childCategoryId, placeNum, marketAutoDiffTrade != null ? marketAutoDiffTrade.getDiffValue() : "null");
        return marketAutoDiffTrade;
    }


    @Override
    public ConfigPlacenumAutoDiffTrade create(String linkId, TradePlaceNumAutoDiffConfigItemDTO tradePlaceNumAutoDiffConfigItemDTO, Long matchId, Long operaterId) {
        ConfigPlacenumAutoDiffTrade configPlacenumAutoDiffTrade = new ConfigPlacenumAutoDiffTrade();
        configPlacenumAutoDiffTrade.setId(UUIdUtils.getId());
        configPlacenumAutoDiffTrade.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
        configPlacenumAutoDiffTrade.setStandardMatchId(matchId);
        configPlacenumAutoDiffTrade.setStandardCategoryId(tradePlaceNumAutoDiffConfigItemDTO.getMarketCategoryId());
        configPlacenumAutoDiffTrade.setChildStandardCategoryId(tradePlaceNumAutoDiffConfigItemDTO.getChildStandardCategoryId());
        configPlacenumAutoDiffTrade.setPlaceNum(tradePlaceNumAutoDiffConfigItemDTO.getPlaceNum());
        configPlacenumAutoDiffTrade.setOddsType(tradePlaceNumAutoDiffConfigItemDTO.getOddType());
        configPlacenumAutoDiffTrade.setDiffValue(tradePlaceNumAutoDiffConfigItemDTO.getDiffValue());
        configPlacenumAutoDiffTrade.setLinkId(linkId);
        configPlacenumAutoDiffTrade.setOperaterId(operaterId);
        StandardMatchInfo standMatchInfo = standardMatchInfoService.getItem(matchId);
        Long expireTime = baseProcessor.marketCacheTime(standMatchInfo.getBeginTime());
        Map<String, ConfigPlacenumAutoDiffTrade> configDiffMap = new HashMap<>();
        if(redisService.hHasKey(REDIS_KEY_PLACENUM+matchId,configPlacenumAutoDiffTrade.getStandardCategoryId()+"")){
            configDiffMap = (Map<String, ConfigPlacenumAutoDiffTrade>) redisService.hGet(REDIS_KEY_PLACENUM + matchId, configPlacenumAutoDiffTrade.getStandardCategoryId() + "");
        }
        configDiffMap.put(configPlacenumAutoDiffTrade.getChildStandardCategoryId()+STR_JOIN+configPlacenumAutoDiffTrade.getPlaceNum(),configPlacenumAutoDiffTrade);
        redisService.hSet(REDIS_KEY_PLACENUM + matchId,configPlacenumAutoDiffTrade.getStandardCategoryId()+"",
                configDiffMap,expireTime);
        return configPlacenumAutoDiffTrade;
    }

    @Override
    public ConfigPlacenumAutoDiffTrade updata(ConfigPlacenumAutoDiffTrade placeNumConfig) {
        configPlacenumAutoDiffTradeMapper.updateByPrimaryKey(placeNumConfig);
        Map<String, ConfigPlacenumAutoDiffTrade> configDiffMap = new HashMap<>();
        Object cached = redisService.hGet(REDIS_KEY_PLACENUM + placeNumConfig.getStandardMatchId(), placeNumConfig.getStandardCategoryId() +"");
        if(cached != null){
            configDiffMap = (Map<String, ConfigPlacenumAutoDiffTrade>) cached;
        }
        configDiffMap.put(placeNumConfig.getChildStandardCategoryId()+STR_JOIN+placeNumConfig.getPlaceNum(),placeNumConfig);
        StandardMatchInfo standMatchInfo = standardMatchInfoService.getItem(placeNumConfig.getStandardMatchId());
        Long expireTime = baseProcessor.marketCacheTime(standMatchInfo.getBeginTime());
        redisService.hSet(REDIS_KEY_PLACENUM + placeNumConfig.getStandardMatchId(), placeNumConfig.getStandardCategoryId() +"",
                configDiffMap,expireTime);
        log.info("更新坑位水差配置成功,matchId={},categoryId={},childCategoryId={},placeNum={},diffValue={}", 
            placeNumConfig.getStandardMatchId(), placeNumConfig.getStandardCategoryId(), 
            placeNumConfig.getChildStandardCategoryId(), placeNumConfig.getPlaceNum(), placeNumConfig.getDiffValue());
        return placeNumConfig;
    }

    @Override
    public void delDiffByMatchInfoId(Long matchId, String linkId) {
        Map<String, Object> objectObjectMap = redisService.hGetAll(REDIS_KEY_PLACENUM + matchId);
        if(!CollectionUtils.isEmpty(objectObjectMap)){
            Set<String> allCategoryIds = objectObjectMap.keySet();
            redisService.hDel(REDIS_KEY_PLACENUM + matchId,allCategoryIds.toArray());
            log.info("::{}::标准赛事ID:{},清除坑位水差REDIS成功,赛事下全清,KEY:{}", linkId, matchId, objectObjectMap);

        }
    }

    @Override
    public void delDiffByMatchIdAndCategoryList(String linkId, Long matchId, List<Long> categoryList) {
        for (Long categoryId : categoryList ) {
            Map<Long, ConfigCategoryAutoDiffTrade> configDiffTradeMap = (Map<Long, ConfigCategoryAutoDiffTrade>) redisService.hGet(REDIS_KEY_PLACENUM + matchId, categoryId + "");
            log.info("::{}::标准赛事ID:{},categoryId:{},清除坑位水差REDIS成功,KEY:{}", linkId, matchId,categoryId, configDiffTradeMap);
        }
        redisService.hDel(REDIS_KEY_PLACENUM + matchId,categoryList.toArray());
    }

    //--------------------------------------------- 492187 水差优化----------------------------------------------------------------
   /* @Override
    @Cacheable(key = "'ConfigPlacenumAutoDiffTrade:' + #matchId+'-'+#categoryId +'-'+#childCategoryId +'-'+ #placeNum",unless = "#result == null ")
    public ConfigPlacenumAutoDiffTrade getItem(String linkId,Long matchId,Long categoryId, Long childCategoryId, Integer placeNum) {
        //存在部分独赢玩法跳过了排序查询位置水差的场景，所以这里对这些玩法默认placeNum等于1
        if (ObjectUtil.isEmpty(placeNum))
        {
            placeNum = 1;
        }
        StopWatch swCalculate = new StopWatch(UUID.randomUUID().toString());
        swCalculate.start("数据库查询坑位水差耗时");
        ConfigPlacenumAutoDiffTradeExample configPlacenumAutoDiffTradeExample = new ConfigPlacenumAutoDiffTradeExample();
        configPlacenumAutoDiffTradeExample.createCriteria()
                .andStandardMatchIdEqualTo(matchId)
                .andChildStandardCategoryIdEqualTo(childCategoryId)
                .andStandardCategoryIdEqualTo(categoryId)
                .andPlaceNumEqualTo(placeNum);
        List<ConfigPlacenumAutoDiffTrade> list =  configPlacenumAutoDiffTradeMapper.selectByExample(configPlacenumAutoDiffTradeExample);
        swCalculate.stop();
        log.info("::{}::数据库查询坑位水差耗时{}ms,标准玩法id={},子玩法id：{}", linkId, swCalculate.getTotalTimeMillis(), categoryId, childCategoryId);
        if (CollectionUtils.isEmpty(list))
        {
            return null;
        }
        return list.get(0);
    }

    @Override
    public ConfigPlacenumAutoDiffTrade getItem(String linkId, Long matchId, Long categoryId, Long childCategoryId) {
        ConfigPlacenumAutoDiffTradeExample configPlacenumAutoDiffTradeExample = new ConfigPlacenumAutoDiffTradeExample();
        configPlacenumAutoDiffTradeExample.createCriteria()
                .andStandardMatchIdEqualTo(matchId)
                .andChildStandardCategoryIdEqualTo(childCategoryId)
                .andStandardCategoryIdEqualTo(categoryId);
        List<ConfigPlacenumAutoDiffTrade> list =  configPlacenumAutoDiffTradeMapper.selectByExample(configPlacenumAutoDiffTradeExample);
        if (CollectionUtils.isEmpty(list))
        {
            return null;
        }
        return list.get(0);
    }

    @Override
    @CachePut(key = "'ConfigPlacenumAutoDiffTrade:' + #matchId+'-'+#tradePlaceNumAutoDiffConfigItemDTO.marketCategoryId +'-'+#tradePlaceNumAutoDiffConfigItemDTO.childStandardCategoryId +'-'+ #tradePlaceNumAutoDiffConfigItemDTO.placeNum")
    public ConfigPlacenumAutoDiffTrade create(String linkId, TradePlaceNumAutoDiffConfigItemDTO tradePlaceNumAutoDiffConfigItemDTO, Long matchId, Long operaterId) {
        ConfigPlacenumAutoDiffTrade configPlacenumAutoDiffTrade = new ConfigPlacenumAutoDiffTrade();
        configPlacenumAutoDiffTrade.setId(UUIdUtils.getId());
        configPlacenumAutoDiffTrade.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
        configPlacenumAutoDiffTrade.setStandardMatchId(matchId);
        configPlacenumAutoDiffTrade.setStandardCategoryId(tradePlaceNumAutoDiffConfigItemDTO.getMarketCategoryId());
        configPlacenumAutoDiffTrade.setChildStandardCategoryId(tradePlaceNumAutoDiffConfigItemDTO.getChildStandardCategoryId());
        configPlacenumAutoDiffTrade.setPlaceNum(tradePlaceNumAutoDiffConfigItemDTO.getPlaceNum());
        configPlacenumAutoDiffTrade.setOddsType(tradePlaceNumAutoDiffConfigItemDTO.getOddType());
        configPlacenumAutoDiffTrade.setDiffValue(tradePlaceNumAutoDiffConfigItemDTO.getDiffValue());
        configPlacenumAutoDiffTrade.setLinkId(linkId);
        configPlacenumAutoDiffTrade.setOperaterId(operaterId);
        configPlacenumAutoDiffTradeMapper.insert(configPlacenumAutoDiffTrade);
        return configPlacenumAutoDiffTrade;
    }

    @Override
    @CacheEvict(key = "'ConfigPlacenumAutoDiffTrade:' + #configMarketAutoDiffTrade.standardMatchId+'-'+#configMarketAutoDiffTrade.standardCategoryId +'-'+#configMarketAutoDiffTrade.childStandardCategoryId +'-'+ #configMarketAutoDiffTrade.placeNum")
    public ConfigPlacenumAutoDiffTrade updata(ConfigPlacenumAutoDiffTrade configMarketAutoDiffTrade) {
        configPlacenumAutoDiffTradeMapper.updateByPrimaryKey(configMarketAutoDiffTrade);
        return configMarketAutoDiffTrade;
    }

    @Override
    @CacheEvict(key = "'ConfigPlacenumAutoDiffTrade:' + #configMarketAutoDiffTrade.standardMatchId+'-'+#configMarketAutoDiffTrade.standardCategoryId +'-'+#configMarketAutoDiffTrade.childStandardCategoryId +'-'+ #configMarketAutoDiffTrade.placeNum")
    public void del(ConfigPlacenumAutoDiffTrade configMarketAutoDiffTrade) {
        configPlacenumAutoDiffTradeMapper.deleteByPrimaryKey(configMarketAutoDiffTrade.getId());
    }

    @Override
    public void delDiffByMatchInfoId(Long matchId, String linkId) {
        ConfigPlacenumAutoDiffTradeExample configPlacenumAutoDiffTradeExample = new ConfigPlacenumAutoDiffTradeExample();
        configPlacenumAutoDiffTradeExample.createCriteria().andStandardMatchIdEqualTo(matchId);
        List<ConfigPlacenumAutoDiffTrade> list = configPlacenumAutoDiffTradeMapper.selectByExample(configPlacenumAutoDiffTradeExample);
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        clearCache(linkId, matchId, list);
    }

    @Override
    public void delDiffByMatchIdAndCategoryList(String linkId, Long matchId, List<Long> categoryList) {
        ConfigPlacenumAutoDiffTradeExample configPlacenumAutoDiffTradeExample = new ConfigPlacenumAutoDiffTradeExample();
        configPlacenumAutoDiffTradeExample.createCriteria().andStandardMatchIdEqualTo(matchId).andStandardCategoryIdIn(categoryList);
        List<ConfigPlacenumAutoDiffTrade> list = configPlacenumAutoDiffTradeMapper.selectByExample(configPlacenumAutoDiffTradeExample);
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        clearCache(linkId, matchId, list);
    }

    private void clearCache(String linkId, Long matchId, List<ConfigPlacenumAutoDiffTrade> list) {
        for (ConfigPlacenumAutoDiffTrade diffTrade : list) {
            configPlaceNumAutoDiffTradeService.del(diffTrade);
            log.info("::{}::标准赛事ID:{},清除坑位水差REDIS成功,KEY:{}", linkId, matchId, JSON.toJSONString(diffTrade));
        }
    }*/

}
