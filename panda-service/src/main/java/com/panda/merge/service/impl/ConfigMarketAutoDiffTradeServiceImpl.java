package com.panda.merge.service.impl;

import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.TradeMarketAutoDiffConfigItemDTO;
import com.panda.merge.mapper.ConfigMarketAutoDiffTradeMapper;
import com.panda.merge.model.ConfigMarketAutoDiffTrade;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.service.ConfigMarketAutoDiffTradeService;
import com.panda.merge.service.StandardMatchInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/25 <br>
 * @see com.panda.merge.service.impl <br>
 */
@Slf4j
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class ConfigMarketAutoDiffTradeServiceImpl implements ConfigMarketAutoDiffTradeService {

    @Autowired
    private ConfigMarketAutoDiffTradeMapper configMarketAutoDiffTradeMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private ConfigMarketAutoDiffTradeService configMarketAutoDiffTradeService;

    @Autowired
    private BaseProcessor baseProcessor;

    @Autowired
    private StandardMatchInfoService standardMatchInfoService;

    private static String REDIS_KEY_MARKET = "ODDS_DIFF_MARKET:";

    @Override
    public ConfigMarketAutoDiffTrade getItem(String linkId,Long matchId, Long relationMarketId, String oddsType) {
        StopWatch swCalculate = new StopWatch(UUID.randomUUID().toString());
        swCalculate.start("数据库查询玩法自动水差耗时");
        Map<String,ConfigMarketAutoDiffTrade> configDiffTradeMap =(Map<String, ConfigMarketAutoDiffTrade>) redisService.hGet(REDIS_KEY_MARKET + matchId, relationMarketId +"");
        if(CollectionUtils.isEmpty(configDiffTradeMap)){
            return null;
        }
        ConfigMarketAutoDiffTrade marketAutoDiffTrade = configDiffTradeMap.get(oddsType);
        swCalculate.stop();
        log.info("::{}::数据库查询水差耗时{}ms,统一盘口id={}" , linkId, swCalculate.getTotalTimeMillis(),relationMarketId);
        return marketAutoDiffTrade;
    }

    @Override
    public ConfigMarketAutoDiffTrade create(String linkId, TradeMarketAutoDiffConfigItemDTO tradeMarketAutoDiffConfigItemDTO, Long matchId, Long operaterId) {
        ConfigMarketAutoDiffTrade configMarketAutoDiffTrade = new ConfigMarketAutoDiffTrade();
        configMarketAutoDiffTrade.setId(UUIdUtils.getId());
        configMarketAutoDiffTrade.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
        configMarketAutoDiffTrade.setDiffValue(tradeMarketAutoDiffConfigItemDTO.getDiffValue());
        configMarketAutoDiffTrade.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        configMarketAutoDiffTrade.setOddsType(tradeMarketAutoDiffConfigItemDTO.getOddType());
        configMarketAutoDiffTrade.setStandardMarketId(tradeMarketAutoDiffConfigItemDTO.getMarketId());
        configMarketAutoDiffTrade.setStandardCategoryId(tradeMarketAutoDiffConfigItemDTO.getMarketCategoryId());
        configMarketAutoDiffTrade.setStandardMatchId(matchId);
        configMarketAutoDiffTrade.setLinkId(linkId);
        configMarketAutoDiffTrade.setOperaterId(operaterId);
        StandardMatchInfo standMatchInfo = standardMatchInfoService.getItem(matchId);
        Long expireTime = baseProcessor.marketCacheTime(standMatchInfo.getBeginTime());
        Map<String,ConfigMarketAutoDiffTrade> configDiffMap = new HashMap<>();
        if(redisService.hHasKey(REDIS_KEY_MARKET+matchId,configMarketAutoDiffTrade.getStandardMarketId()+"")){
            configDiffMap = (Map<String, ConfigMarketAutoDiffTrade>) redisService.hGet(REDIS_KEY_MARKET + matchId, configMarketAutoDiffTrade.getStandardMarketId() + "");
        }
        configDiffMap.put(configMarketAutoDiffTrade.getOddsType(),configMarketAutoDiffTrade);
        redisService.hSet(REDIS_KEY_MARKET + matchId,configMarketAutoDiffTrade.getStandardMarketId()+"",
                configDiffMap,expireTime);
        return configMarketAutoDiffTrade;
    }


    @Override
    public ConfigMarketAutoDiffTrade updata(ConfigMarketAutoDiffTrade marketConfig) {
        marketConfig.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        Map<String,ConfigMarketAutoDiffTrade> configDiffMap = new HashMap<>();
        configDiffMap =(Map<String, ConfigMarketAutoDiffTrade>) redisService.hGet(REDIS_KEY_MARKET + marketConfig.getStandardMatchId(), marketConfig.getStandardMarketId() +"");
        configDiffMap.put(marketConfig.getOddsType(),marketConfig);
        StandardMatchInfo standMatchInfo = standardMatchInfoService.getItem(marketConfig.getStandardMatchId());
        Long expireTime = baseProcessor.marketCacheTime(standMatchInfo.getBeginTime());
        redisService.hSet(REDIS_KEY_MARKET + marketConfig.getStandardMatchId(), marketConfig.getStandardMarketId() +"",
                configDiffMap,expireTime);

        return marketConfig;
    }


    @Override
    public void delDiffByMatchInfoId(Long matchId, String linkId) {
        Map<String, Object> objectObjectMap = redisService.hGetAll(REDIS_KEY_MARKET + matchId);
        if(!CollectionUtils.isEmpty(objectObjectMap)){
            Set<String> allCategoryIds = objectObjectMap.keySet();
            redisService.hDel(REDIS_KEY_MARKET + matchId,allCategoryIds.toArray());
            log.info("::{}::标准赛事ID:{},清除盘口水差成功,赛事下全清,KEY:{}", linkId, matchId, objectObjectMap);
        }
    }

    @Override
    public void delDiffByMatchIdAndCategoryList(String linkId, Long matchId, List<Long> categoryList) {
        StandardMatchInfo standMatchInfo = standardMatchInfoService.getItem(matchId);
        Long expireTime = baseProcessor.marketCacheTime(standMatchInfo.getBeginTime());
        AtomicReference<Boolean> refresh = new AtomicReference<>(false);
        Map<String, Map<String, ConfigMarketAutoDiffTrade>> allMap = redisService.hGetAll(REDIS_KEY_MARKET + matchId);
        for (Map.Entry<String, Map<String, ConfigMarketAutoDiffTrade>> entry : allMap.entrySet()) {
            entry.getValue().forEach((oddsType, marketConfig) -> {
                if(categoryList.contains(marketConfig.getStandardCategoryId())){
                    log.info("::{}::标准赛事ID:{},清除玩法水差配置成功,KEY:{}", linkId, matchId, marketConfig);
                    entry.getValue().put(oddsType,null);
                    refresh.set(true);
                }
            });
            if(refresh.get()){
                redisService.hSet(REDIS_KEY_MARKET + matchId, entry.getKey() ,
                        entry.getValue(),expireTime);
                refresh.set(false);
            }
            }
    }



    //--------------------------------------------- 492187 水差优化----------------------------------------------------------------
    /*@Override
    @Cacheable(key = "'ConfigMarketAutoDiffTrade:' + #relationMarketId+'-'+#oddsType",unless = "#result == null ")
    public ConfigMarketAutoDiffTrade getItem(String linkId, Long relationMarketId, String oddsType) {
        ConfigMarketAutoDiffTradeExample configMarketAutoDiffTradeExample = new ConfigMarketAutoDiffTradeExample();
        configMarketAutoDiffTradeExample.createCriteria().andStandardMarketIdEqualTo(relationMarketId)
                .andOddsTypeEqualTo(oddsType);
        // 至于Id 我觉得给UUID是可行的~
        StopWatch swCalculate = new StopWatch(UUID.randomUUID().toString());
        swCalculate.start("数据库查询水差耗时");
        List<ConfigMarketAutoDiffTrade> configMarketAutoDiffTrades = configMarketAutoDiffTradeMapper.selectByExample(configMarketAutoDiffTradeExample);
        swCalculate.stop();
        log.info("::{}::数据库查询水差耗时{}ms,统一盘口id={}" , linkId, swCalculate.getTotalTimeMillis(),relationMarketId);
       if(CollectionUtils.isEmpty(configMarketAutoDiffTrades)){
            return null;
        }
        return configMarketAutoDiffTrades.get(0);
    }

    @Override
    @CachePut(key = "'ConfigMarketAutoDiffTrade:' + #tradeMarketAutoDiffConfigItemDTO.marketId+'-'+#tradeMarketAutoDiffConfigItemDTO.oddType")
    public ConfigMarketAutoDiffTrade create(String linkId, TradeMarketAutoDiffConfigItemDTO tradeMarketAutoDiffConfigItemDTO, Long matchId, Long operaterId) {
        ConfigMarketAutoDiffTrade configMarketAutoDiffTrade = new ConfigMarketAutoDiffTrade();
        configMarketAutoDiffTrade.setId(UUIdUtils.getId());
        configMarketAutoDiffTrade.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
        configMarketAutoDiffTrade.setDiffValue(tradeMarketAutoDiffConfigItemDTO.getDiffValue());
        configMarketAutoDiffTrade.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        configMarketAutoDiffTrade.setOddsType(tradeMarketAutoDiffConfigItemDTO.getOddType());
        configMarketAutoDiffTrade.setStandardMarketId(tradeMarketAutoDiffConfigItemDTO.getMarketId());
        configMarketAutoDiffTrade.setStandardCategoryId(tradeMarketAutoDiffConfigItemDTO.getMarketCategoryId());
        configMarketAutoDiffTrade.setStandardMatchId(matchId);
        configMarketAutoDiffTrade.setLinkId(linkId);
        configMarketAutoDiffTrade.setOperaterId(operaterId);
        configMarketAutoDiffTradeMapper.insert(configMarketAutoDiffTrade);
        return configMarketAutoDiffTrade;
    }

    @Override
    @CacheEvict(key = "'ConfigMarketAutoDiffTrade:' + #configMarketAutoDiffTrade.standardMarketId+'-'+#configMarketAutoDiffTrade.oddsType")
    public ConfigMarketAutoDiffTrade updata(ConfigMarketAutoDiffTrade configMarketAutoDiffTrade) {
        configMarketAutoDiffTrade.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        configMarketAutoDiffTradeMapper.updateByPrimaryKey(configMarketAutoDiffTrade);
        return configMarketAutoDiffTrade;
    }

    @Override
    @CacheEvict(key = "'ConfigMarketAutoDiffTrade:' + #diffTrade.standardMarketId+'-'+#diffTrade.oddsType")
    public void del(ConfigMarketAutoDiffTrade diffTrade) {
        configMarketAutoDiffTradeMapper.deleteByPrimaryKey(diffTrade.getId());
    }

    @Override
    public void delDiffByMatchInfoId(Long matchId, String linkId) {
        ConfigMarketAutoDiffTradeExample configMarketAutoDiffTradeExample = new ConfigMarketAutoDiffTradeExample();
        configMarketAutoDiffTradeExample.createCriteria().andStandardMatchIdEqualTo(matchId);
        List<ConfigMarketAutoDiffTrade> configMarketAutoDiffTradeList = configMarketAutoDiffTradeMapper.selectByExample(configMarketAutoDiffTradeExample);
        if (CollectionUtils.isEmpty(configMarketAutoDiffTradeList)) {
            return;
        }
        clearCache(linkId, matchId, configMarketAutoDiffTradeList);
    }

    @Override
    public void delDiffByMatchIdAndCategoryList(String linkId, Long matchId, List<Long> categoryList) {
        ConfigMarketAutoDiffTradeExample configMarketAutoDiffTradeExample = new ConfigMarketAutoDiffTradeExample();
        configMarketAutoDiffTradeExample.createCriteria().andStandardMatchIdEqualTo(matchId).andStandardCategoryIdIn(categoryList);
        List<ConfigMarketAutoDiffTrade> configMarketAutoDiffTradeList = configMarketAutoDiffTradeMapper.selectByExample(configMarketAutoDiffTradeExample);
        if (CollectionUtils.isEmpty(configMarketAutoDiffTradeList)) {
            return;
        }
        clearCache(linkId, matchId, configMarketAutoDiffTradeList);
    }

    private void clearCache(String linkId, Long matchId, List<ConfigMarketAutoDiffTrade> configMarketAutoDiffTradeList) {
        for (ConfigMarketAutoDiffTrade diffTrade : configMarketAutoDiffTradeList) {
            log.info("::{}::标准赛事ID:{},清除盘口水差成功,KEY:{}", linkId, matchId, JSON.toJSONString(diffTrade));
            configMarketAutoDiffTradeService.del(diffTrade);
        }
    }*/
}
