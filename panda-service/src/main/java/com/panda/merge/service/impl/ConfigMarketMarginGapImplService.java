package com.panda.merge.service.impl;

import com.panda.merge.common.BaseProcessor;
import com.panda.merge.config.RedisService;
import com.panda.merge.model.ConfigMarketMarginGap;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.service.ConfigMarketMarginGapService;
import com.panda.merge.service.StandardMatchInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import java.util.*;

@Slf4j
@Service
public class ConfigMarketMarginGapImplService implements ConfigMarketMarginGapService {
    @Autowired
    private RedisService redisService;
    @Autowired
    private BaseProcessor baseProcessor;
    @Autowired
    private StandardMatchInfoService standardMatchInfoService;
    private static String REDIS_KEY_MARGIN_GAP = "ODDS_DIFF_MARGIN_GAP:";
    @Override
    public List<ConfigMarketMarginGap> getItemList(Long standardMatchId, Long marketCategoryId,Long childMarketCategoryId, Integer placeNum) {
        List<ConfigMarketMarginGap> configMarketMarginGaps = new ArrayList<>();
        Map<String, ConfigMarketMarginGap> configDiffMap  = (Map<String, ConfigMarketMarginGap>) redisService.hGet(REDIS_KEY_MARGIN_GAP+standardMatchId,marketCategoryId+"");
        if(!CollectionUtils.isEmpty(configDiffMap)){
            for (ConfigMarketMarginGap marginGapEntry : configDiffMap.values()) {
                if(marginGapEntry.getChildStandardCategoryId().equals(childMarketCategoryId) && marginGapEntry.getPlaceNum() == placeNum){
                    configMarketMarginGaps.add(marginGapEntry);
                }
            }
        }
        if (CollectionUtils.isEmpty(configMarketMarginGaps) && configMarketMarginGaps.size() == 0) {
            return null;
        }
        return configMarketMarginGaps;
    }

    @Override
    public ConfigMarketMarginGap getItem(Long standardMatchId, Long marketCategoryId, Long childMarketCategoryId,String oddsType, Integer placeNum) {
        Map<String, ConfigMarketMarginGap> configDiffMap  = (Map<String, ConfigMarketMarginGap>) redisService.hGet(REDIS_KEY_MARGIN_GAP+standardMatchId,marketCategoryId+"");
        String configKey = childMarketCategoryId + '-' + oddsType + '-' + placeNum;
        if(!CollectionUtils.isEmpty(configDiffMap) && configDiffMap.containsKey(configKey)){
            return configDiffMap.get(configKey);
        }
        return null;
    }

    @Override
    public void insertList(String linkId, Long standardMatchInfoId, List<ConfigMarketMarginGap> configMarketMarginGaps) {
        log.info("::{}::ConfigMarketMarginGapImplService-批量添加数据:{},标准赛事ID:{}", linkId, configMarketMarginGaps.size(), standardMatchInfoId);
        String hKey = REDIS_KEY_MARGIN_GAP + standardMatchInfoId;
        StandardMatchInfo standMatchInfo = standardMatchInfoService.getItem(standardMatchInfoId);
        Long expireTime = baseProcessor.marketCacheTime(standMatchInfo.getBeginTime());
        for(ConfigMarketMarginGap marginGap : configMarketMarginGaps){
            Map<String, ConfigMarketMarginGap> configDiffMap = new HashMap<>();
            if(redisService.hHasKey(hKey,marginGap.getMarketCategoryId()+"")){
                configDiffMap = (Map<String, ConfigMarketMarginGap>) redisService.hGet(hKey, marginGap.getMarketCategoryId() + "");
            }
            //保证key唯一  mapKey = 子玩法id + 投注项类型  + 坑位
            configDiffMap.put(marginGap.getChildStandardCategoryId() + '-' + marginGap.getOddsType() + '-' + marginGap.getPlaceNum(),marginGap);
            redisService.hSet(hKey,marginGap.getMarketCategoryId()+"",configDiffMap,expireTime);
        }
    }


    @Override
    public void updateList(String linkId, Long standardMatchInfoId, List<ConfigMarketMarginGap> configMarketMarginGaps) {
        log.info("::{}::ConfigMarketMarginGapImplService-批量修改数据:{},标准赛事ID:{}", linkId, configMarketMarginGaps.size(), standardMatchInfoId);
        insertList(linkId,standardMatchInfoId,configMarketMarginGaps);
    }

    @Override
    public void updateByMatchIdAndCategoryList(String linkId, Long standardMatchId, List<Long> categoryList) {
        StandardMatchInfo standMatchInfo = standardMatchInfoService.getItem(standardMatchId);
        Long expireTime = baseProcessor.marketCacheTime(standMatchInfo.getBeginTime());
        Map<String,  Map<String, ConfigMarketMarginGap>> categoryMap = redisService.hGetAll(REDIS_KEY_MARGIN_GAP + standardMatchId);
        if(CollectionUtils.isEmpty(categoryMap)){
            return;
        }
        for(Long categoryId : categoryList){
            if(categoryMap.containsKey(categoryId+"")) {
                Map<String, ConfigMarketMarginGap> marginGapMap = categoryMap.get(categoryId + "");
                for (ConfigMarketMarginGap marginGapEntry : marginGapMap.values()) {
                    marginGapEntry.setDiffValue(0D);
                    marginGapEntry.setProbability(0D);
                }
                redisService.hSet(REDIS_KEY_MARGIN_GAP + standardMatchId,categoryId+"",marginGapMap,expireTime);
            }
        }
        log.info("::{}::切换数据源标识或切换玩法数据源清除水差概率差,标准赛事ID:{},玩法集合:{}", linkId, standardMatchId, categoryList);
    }

    @Override
    public void updateByMatchId(String linkId, Long standardMatchId) {
        StopWatch sw1 = new StopWatch(UUID.randomUUID().toString());
        sw1.start();
        String hKey = REDIS_KEY_MARGIN_GAP + standardMatchId;
        Map<String,  Map<String, ConfigMarketMarginGap>> categoryMap = redisService.hGetAll(hKey);
        sw1.stop();
        log.info("::{}::赛前切滚球或切换数据源关闭旧数据源赛事级别清除水差概率差,查询耗时:{}ms,标准赛事ID={}", linkId, sw1.getTotalTimeMillis(), standardMatchId);
        if (CollectionUtils.isEmpty(categoryMap)) {
            return;
        }
        StopWatch sw2 = new StopWatch(UUID.randomUUID().toString());
        sw2.start();
        StandardMatchInfo standMatchInfo = standardMatchInfoService.getItem(standardMatchId);
        Long expireTime = baseProcessor.marketCacheTime(standMatchInfo.getBeginTime());
        Integer updateNumber = 0;
        //批量更新有改变字段
        for (Map.Entry<String, Map<String, ConfigMarketMarginGap>> entry : categoryMap.entrySet()) {
            for (ConfigMarketMarginGap marginGapEntry : entry.getValue().values()) {
                marginGapEntry.setDiffValue(0D);
                marginGapEntry.setProbability(0D);
                updateNumber++;
            }
            redisService.hSet(hKey, entry.getKey(), entry.getValue(),expireTime);
        }
        sw2.stop();
        log.info("::{}::赛前切滚球或切换数据源关闭旧数据源赛事级别清除水差概率差,修改耗时:{}ms,标准赛事ID:{},修改条数:{}", linkId, sw2.getTotalTimeMillis(), standardMatchId, updateNumber);
        //刷新环境
    }

    @Override
    public void upProbabilityByMatchIdAndCategoryId(String linkId, Long matchId, Long marketCategoryId,Long childMarketCategoryId, List<String> changeOddsType, Integer placeNum) {
        StopWatch sw1 = new StopWatch(UUID.randomUUID().toString());
        sw1.start();
        Map<String, ConfigMarketMarginGap> configDiffMap  = (Map<String, ConfigMarketMarginGap>) redisService.hGet(REDIS_KEY_MARGIN_GAP+matchId,marketCategoryId+"");

        sw1.stop();
        log.info("::{}::大于三项数据源赔率变动清除概率差,标准赛事ID:{},玩法集合:{},坑位:{},投注项集合:{},查询耗时:{}ms", linkId, matchId, marketCategoryId, placeNum, changeOddsType, sw1.getTotalTimeMillis());
        if (CollectionUtils.isEmpty(configDiffMap)) {
            return;
        }
        StopWatch sw2 = new StopWatch(UUID.randomUUID().toString());
        sw2.start();
        StandardMatchInfo standMatchInfo = standardMatchInfoService.getItem(matchId);
        Long expireTime = baseProcessor.marketCacheTime(standMatchInfo.getBeginTime());
        Integer updateNumber = 0;
        //批量更新有改变字段
        for(String oddsType : changeOddsType){
            //根据唯一key获取对象信息  mapKey = 子玩法id + 投注项类型  + 坑位
            if(configDiffMap.containsKey(childMarketCategoryId + '-' + oddsType + '-' + placeNum)){
                ConfigMarketMarginGap configMarketMarginGap = configDiffMap.get(childMarketCategoryId + '-' + oddsType + '-' + placeNum);
                configMarketMarginGap.setProbability(0D);
                configMarketMarginGap.setLinkId(linkId);
                updateNumber++;
            }
        }
        redisService.hSet(REDIS_KEY_MARGIN_GAP+matchId, marketCategoryId+"", configDiffMap,expireTime);
        sw2.stop();
        log.info("::{}::大于三项数据源赔率变动清除概率差,标准赛事ID:{},玩法集合:{},坑位:{},投注项集合:{},修改条数:{},修改耗时:{}ms",
                linkId, matchId, marketCategoryId, placeNum, changeOddsType, updateNumber, sw2.getTotalTimeMillis());
    }

    @Override
    public void upProbabilityByMatchIdAndCategoryIdList(String linkId, Long standardMatchId, List<Long> marketCategoryId) {
        StandardMatchInfo standMatchInfo = standardMatchInfoService.getItem(standardMatchId);
        Long expireTime = baseProcessor.marketCacheTime(standMatchInfo.getBeginTime());
        Map<String,  Map<String, ConfigMarketMarginGap>> categoryMap = redisService.hGetAll(REDIS_KEY_MARGIN_GAP + standardMatchId);
        if(CollectionUtils.isEmpty(categoryMap)){
            return;
        }
        for(Long categoryId : marketCategoryId){
            if(categoryMap.containsKey(categoryId+"")) {
                Map<String, ConfigMarketMarginGap> marginGapMap = categoryMap.get(categoryId + "");
                for (ConfigMarketMarginGap marginGapEntry : marginGapMap.values()) {
                    marginGapEntry.setDiffValue(0D);
                    marginGapEntry.setProbability(0D);
                }
                redisService.hSet(REDIS_KEY_MARGIN_GAP + standardMatchId,categoryId+"",marginGapMap,expireTime);
            }
        }
        log.info("::{}::大于三项盘玩法切换数据源标识或切换玩法数据源清概率差,标准赛事ID:{},玩法集合:{}", linkId, standardMatchId, marketCategoryId);
    }

    @Override
    public void updateByCategory(String linkId, Long standardMatchInfoId, Long standardCategoryId, Double margin) {
        StandardMatchInfo standMatchInfo = standardMatchInfoService.getItem(standardMatchInfoId);
        Long expireTime = baseProcessor.marketCacheTime(standMatchInfo.getBeginTime());
        Map<String, ConfigMarketMarginGap> configDiffMap  = (Map<String, ConfigMarketMarginGap>) redisService.hGet(REDIS_KEY_MARGIN_GAP+standardMatchInfoId,standardCategoryId+"");
        if (CollectionUtils.isEmpty(configDiffMap)) {
            log.info("::{}::独赢配置分时margin总玩法子玩法为空,赛事ID:{}，玩法ID:{},margin:{}", linkId, standardMatchInfoId, standardCategoryId, margin);
            return;
        }
        Integer updateNumber = 0;
        for(ConfigMarketMarginGap marginGap : configDiffMap.values()){
            if(!marginGap.getChildStandardCategoryId().equals(standardCategoryId)){
                marginGap.setMargin(margin);
                updateNumber++;
            }
        }
        if(updateNumber == 0){
            log.info("::{}::独赢配置分时margin总玩法子玩法为空,赛事ID:{}，玩法ID:{},margin:{}", linkId, standardMatchInfoId, standardCategoryId, margin);
            return;
        }
        redisService.hSet(REDIS_KEY_MARGIN_GAP + standardMatchInfoId,standardCategoryId+"",configDiffMap,expireTime);
        log.info("::{}::独赢配置分时margin总玩法，更新子玩法margin成功,赛事ID:{}，玩法ID:{},条数:{}", linkId, standardMatchInfoId, standardCategoryId, updateNumber);
    }






    //--------------------------------------------- 492187 水差优化----------------------------------------------------------------
/*
    @Override
    @Cacheable(key = "'ConfigMarketMarginGapList:' + #standardMatchId+'-'+#marketCategoryId+'-'+#childMarketCategoryId+'-'+#placeNum", unless = "#result == null ")
    public List<ConfigMarketMarginGap> getItemList(Long standardMatchId, Long marketCategoryId,Long childMarketCategoryId, Integer placeNum) {
        ConfigMarketMarginGapExample configMarketMarginExample = new ConfigMarketMarginGapExample();
        ConfigMarketMarginGapExample.Criteria criteria = configMarketMarginExample.createCriteria()
                .andMatchIdEqualTo(standardMatchId).andPlaceNumEqualTo(placeNum).andMarketCategoryIdEqualTo(marketCategoryId).andChildStandardCategoryIdEqualTo(childMarketCategoryId);
        List<ConfigMarketMarginGap> configMarketMarginGaps = configMarketMarginGapMapper.selectByExample(configMarketMarginExample);
        if (CollectionUtils.isEmpty(configMarketMarginGaps)) {
            return null;
        }
        return configMarketMarginGaps;
    }

    @Override
    @Cacheable(key = "'ConfigMarketMarginGap:' + #standardMatchId+'-'+#marketCategoryId+'-'+#childMarketCategoryId+'-'+ #oddsType+'-'+ #placeNum", unless = "#result == null ")
    public ConfigMarketMarginGap getItem(Long standardMatchId, Long marketCategoryId, Long childMarketCategoryId,String oddsType, Integer placeNum) {
        ConfigMarketMarginGapExample configMarketMarginExample = new ConfigMarketMarginGapExample();
        ConfigMarketMarginGapExample.Criteria criteria = configMarketMarginExample.createCriteria()
                .andMatchIdEqualTo(standardMatchId).andPlaceNumEqualTo(placeNum).andMarketCategoryIdEqualTo(marketCategoryId)
                .andChildStandardCategoryIdEqualTo(childMarketCategoryId)
                .andOddsTypeEqualTo(oddsType);
        List<ConfigMarketMarginGap> configMarketMarginGaps = configMarketMarginGapMapper.selectByExample(configMarketMarginExample);
        if (CollectionUtils.isEmpty(configMarketMarginGaps)) {
            return null;
        }
        return configMarketMarginGaps.get(0);
    }

    @Override
    public void insertList(String linkId, Long standardMatchInfoId, List<ConfigMarketMarginGap> configMarketMarginGaps) {
        log.info("::{}::ConfigMarketMarginGapImplService-批量添加数据:{},标准赛事ID:{}", linkId, configMarketMarginGaps.size(), standardMatchInfoId);
        configMarketMarginGapDao.insertList(configMarketMarginGaps);
        refreshCache(linkId, standardMatchInfoId, configMarketMarginGaps);
    }

    @Override
    public void updateList(String linkId, Long standardMatchInfoId, List<ConfigMarketMarginGap> configMarketMarginGaps) {
        log.info("::{}::ConfigMarketMarginGapImplService-批量修改数据:{},标准赛事ID:{}", linkId, configMarketMarginGaps.size(), standardMatchInfoId);
        configMarketMarginGapDao.updateList(configMarketMarginGaps);
        clearCache(linkId, standardMatchInfoId, configMarketMarginGaps);
    }

    @Override
    public void updateByMatchIdAndCategoryList(String linkId, Long standardMatchId, List<Long> categoryList) {
        ConfigMarketMarginGapExample configMarketMarginExample = new ConfigMarketMarginGapExample();
        configMarketMarginExample.createCriteria().andMatchIdEqualTo(standardMatchId).andMarketCategoryIdIn(categoryList);
        List<ConfigMarketMarginGap> configMarketMarginGapList = configMarketMarginGapMapper.selectByExample(configMarketMarginExample);
        if (CollectionUtils.isEmpty(configMarketMarginGapList)) {
            return;
        }
        //批量更新有改变字段
        List<ConfigMarketMarginGap> newConfigMarketMarginGapList = new ArrayList<>();
        configMarketMarginGapList.forEach(o -> {
            o.setDiffValue(0D);
            o.setProbability(0D);
            ConfigMarketMarginGap newConfigMarketMarginGap = new ConfigMarketMarginGap();
            newConfigMarketMarginGap.setId(o.getId());
            newConfigMarketMarginGap.setDiffValue(0D);
            newConfigMarketMarginGap.setProbability(0D);
            newConfigMarketMarginGapList.add(newConfigMarketMarginGap);
        });
        log.info("::{}::切换数据源标识或切换玩法数据源清除水差概率差,标准赛事ID:{},玩法集合:{}", linkId, standardMatchId, categoryList);
        configMarketMarginGapDao.updateList(newConfigMarketMarginGapList);
        //刷新缓存
        refreshCache(linkId, standardMatchId, configMarketMarginGapList);
    }

    @Override
    public void updateByMatchId(String linkId, Long standardMatchId) {
        StopWatch sw1 = new StopWatch(UUID.randomUUID().toString());
        sw1.start();
        ConfigMarketMarginGapExample configMarketMarginExample = new ConfigMarketMarginGapExample();
        configMarketMarginExample.createCriteria().andMatchIdEqualTo(standardMatchId);
        List<ConfigMarketMarginGap> configMarketMarginGapList = configMarketMarginGapMapper.selectByExample(configMarketMarginExample);
        sw1.stop();
        log.info("::{}::赛前切滚球或切换数据源关闭旧数据源赛事级别清除水差概率差,查询耗时:{}ms,标准赛事ID={}", linkId, sw1.getTotalTimeMillis(), standardMatchId);
        if (CollectionUtils.isEmpty(configMarketMarginGapList)) {
            return;
        }
        StopWatch sw2 = new StopWatch(UUID.randomUUID().toString());
        sw2.start();
        //批量更新有改变字段
        List<ConfigMarketMarginGap> newConfigMarketMarginGapList = new ArrayList<>();
        configMarketMarginGapList.forEach(o -> {
            o.setDiffValue(0D);
            o.setProbability(0D);
            ConfigMarketMarginGap newConfigMarketMarginGap = new ConfigMarketMarginGap();
            newConfigMarketMarginGap.setId(o.getId());
            newConfigMarketMarginGap.setDiffValue(0D);
            newConfigMarketMarginGap.setProbability(0D);
            newConfigMarketMarginGapList.add(newConfigMarketMarginGap);
        });
        configMarketMarginGapDao.updateList(newConfigMarketMarginGapList);
        sw2.stop();
        log.info("::{}::赛前切滚球或切换数据源关闭旧数据源赛事级别清除水差概率差,修改耗时:{}ms,标准赛事ID:{},修改条数:{}", linkId, sw2.getTotalTimeMillis(), standardMatchId, newConfigMarketMarginGapList.size());
        //刷新环境
        refreshCache(linkId, standardMatchId, configMarketMarginGapList);
    }

    @Override
    public void upProbabilityByMatchIdAndCategoryId(String linkId, Long matchId, Long marketCategoryId,Long childMarketCategoryId, List<String> changeOddsType, Integer placeNum) {
        StopWatch sw1 = new StopWatch(UUID.randomUUID().toString());
        sw1.start();
        ConfigMarketMarginGapExample configMarketMarginExample = new ConfigMarketMarginGapExample();
        configMarketMarginExample.createCriteria().andMatchIdEqualTo(matchId).andMarketCategoryIdEqualTo(marketCategoryId).andChildStandardCategoryIdEqualTo(childMarketCategoryId).andPlaceNumEqualTo(placeNum).andOddsTypeIn(changeOddsType);
        List<ConfigMarketMarginGap> configMarketMarginGapList = configMarketMarginGapMapper.selectByExample(configMarketMarginExample);
        sw1.stop();
        log.info("::{}::大于三项数据源赔率变动清除概率差,标准赛事ID:{},玩法集合:{},坑位:{},投注项集合:{},查询耗时:{}ms", linkId, matchId, marketCategoryId, placeNum, changeOddsType, sw1.getTotalTimeMillis());
        if (CollectionUtils.isEmpty(configMarketMarginGapList)) {
            return;
        }
        StopWatch sw2 = new StopWatch(UUID.randomUUID().toString());
        sw2.start();
        //批量更新有改变字段
        List<ConfigMarketMarginGap> newConfigMarketMarginGapList = new ArrayList<>();
        configMarketMarginGapList.forEach(o -> {
            ConfigMarketMarginGap newConfigMarketMarginGap = new ConfigMarketMarginGap();
            newConfigMarketMarginGap.setId(o.getId());
            newConfigMarketMarginGap.setProbability(0D);
            newConfigMarketMarginGap.setLinkId(linkId);
            newConfigMarketMarginGapList.add(newConfigMarketMarginGap);
            o.setProbability(0D);
        });
        configMarketMarginGapDao.updateList(newConfigMarketMarginGapList);
        sw2.stop();
        log.info("::{}::大于三项数据源赔率变动清除概率差,标准赛事ID:{},玩法集合:{},坑位:{},投注项集合:{},修改条数:{},修改耗时:{}ms",
                linkId, matchId, marketCategoryId, placeNum, changeOddsType, newConfigMarketMarginGapList.size(), sw2.getTotalTimeMillis());
        refreshCache(linkId, matchId, configMarketMarginGapList);
    }

    @Override
    public void upProbabilityByMatchIdAndCategoryIdList(String linkId, Long standardMatchId, List<Long> marketCategoryId) {
        ConfigMarketMarginGapExample configMarketMarginExample = new ConfigMarketMarginGapExample();
        configMarketMarginExample.createCriteria().andMatchIdEqualTo(standardMatchId).andMarketCategoryIdIn(marketCategoryId);
        List<ConfigMarketMarginGap> configMarketMarginGapList = configMarketMarginGapMapper.selectByExample(configMarketMarginExample);
        if (CollectionUtils.isEmpty(configMarketMarginGapList)) {
            return;
        }
        //批量更新有改变字段
        List<ConfigMarketMarginGap> newConfigMarketMarginGapList = new ArrayList<>();
        configMarketMarginGapList.forEach(gap -> {
            ConfigMarketMarginGap newConfigMarketMarginGap = new ConfigMarketMarginGap();
            newConfigMarketMarginGap.setId(gap.getId());
            newConfigMarketMarginGap.setProbability(0D);
            newConfigMarketMarginGap.setDiffValue(0D);
            newConfigMarketMarginGapList.add(newConfigMarketMarginGap);
            gap.setProbability(0D);
            gap.setDiffValue(0D);
        });
        log.info("::{}::大于三项盘玩法切换数据源标识或切换玩法数据源清概率差,标准赛事ID:{},玩法集合:{}", linkId, standardMatchId, marketCategoryId);
        configMarketMarginGapDao.updateList(newConfigMarketMarginGapList);
        refreshCache(linkId, standardMatchId, configMarketMarginGapList);
    }

    @Override
    public void updateByCategory(String linkId, Long standardMatchInfoId, Long standardCategoryId, Double margin) {
        ConfigMarketMarginGapExample configMarketMarginExample = new ConfigMarketMarginGapExample();
        configMarketMarginExample.createCriteria().andMatchIdEqualTo(standardMatchInfoId)
                .andMarketCategoryIdEqualTo(standardCategoryId).andChildStandardCategoryIdNotEqualTo(standardCategoryId);
        List<ConfigMarketMarginGap> configMarketMarginGapList = configMarketMarginGapMapper.selectByExample(configMarketMarginExample);
        if (CollectionUtils.isEmpty(configMarketMarginGapList)) {
            log.info("::{}::独赢配置分时margin总玩法子玩法为空,赛事ID:{}，玩法ID:{},margin:{}", linkId, standardMatchInfoId, standardCategoryId, margin);
            return;
        }
        List<ConfigMarketMarginGap> childMarketMarginGapList = new ArrayList<>();
        for (ConfigMarketMarginGap p : configMarketMarginGapList) {
            p.setMargin(margin);
            //只修改margin
            ConfigMarketMarginGap upChildCategoryMargin = new ConfigMarketMarginGap();
            upChildCategoryMargin.setId(p.getId());
            upChildCategoryMargin.setMargin(margin);
            childMarketMarginGapList.add(upChildCategoryMargin);
        }
        configMarketMarginGapDao.updateList(childMarketMarginGapList);
        //刷新缓存
        refreshCache(linkId, standardMatchInfoId, configMarketMarginGapList);
        log.info("::{}::独赢配置分时margin总玩法，更新子玩法margin成功,赛事ID:{}，玩法ID:{},条数:{}", linkId, standardMatchInfoId, standardCategoryId, childMarketMarginGapList.size());
    }

    *//**
     * 清除缓存
     *
     * @param linkId
     * @param matchId
     * @param configMarketMarginGapList
     *//*
    public void clearCache(String linkId, Long matchId, List<ConfigMarketMarginGap> configMarketMarginGapList) {
        Set<String> key = new HashSet<>();
        for (ConfigMarketMarginGap p : configMarketMarginGapList) {
            key.add(RedisConfig.REDIS_KEY_DATABASE + "::ConfigMarketMarginGapList:" + p.getMatchId() + '-' + p.getMarketCategoryId() + '-' + p.getChildStandardCategoryId() + '-' + p.getPlaceNum());
            key.add(RedisConfig.REDIS_KEY_DATABASE + "::ConfigMarketMarginGap:" + p.getMatchId() + '-' + p.getMarketCategoryId() + '-' + p.getChildStandardCategoryId() + '-' + p.getOddsType() + '-' + p.getPlaceNum());
        }
        log.info("::{}::标准赛事ID:{},清除独赢配置,KEY:{}", linkId, matchId, key);
        redisService.del(new ArrayList<>(key));
    }

    *//**
     * 刷新缓存
     *
     * @param linkId
     * @param matchId
     * @param configMarketMarginGapList
     *//*
    public void refreshCache(String linkId, Long matchId, List<ConfigMarketMarginGap> configMarketMarginGapList) {
        Set<String> keyList = new HashSet<>();
        //更新单条缓存
        for (ConfigMarketMarginGap p : configMarketMarginGapList) {
            String key = RedisConfig.REDIS_KEY_DATABASE + "::ConfigMarketMarginGap:" + p.getMatchId() + '-' + p.getMarketCategoryId() + '-' + p.getChildStandardCategoryId() + '-' + p.getOddsType() + '-' + p.getPlaceNum();
            redisService.set(key, p, RedisConfig.REDIS_DEFAULT_TIME);
            String key1 = RedisConfig.REDIS_KEY_DATABASE + "::ConfigMarketMarginGapList:" + p.getMatchId() + '-' + p.getMarketCategoryId() + '-' + p.getChildStandardCategoryId() + '-' + p.getPlaceNum();
            keyList.add(key1);
        }
        //删除list缓存
        log.info("::{}::标准赛事ID:{},删除独赢缓存配置LIST,KEY:{}", linkId, matchId, keyList);
        redisService.del(new ArrayList<>(keyList));
    }*/
}
