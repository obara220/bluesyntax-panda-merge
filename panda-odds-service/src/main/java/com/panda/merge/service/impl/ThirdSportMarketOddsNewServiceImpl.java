package com.panda.merge.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.OddsWrapper;
import com.panda.merge.common.RedisHelper;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.utils.MD5Utils;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.MarketDbProducer;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.MarginCategoryConfig;
import com.panda.merge.constant.RelationKeyFactory;
import com.panda.merge.dto.I18nItemDTO;
import com.panda.merge.dto.ThirdMarketDTO;
import com.panda.merge.dto.ThirdMarketOddsDTO;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.dao.ThirdSportMarketOddsDao;
import com.panda.merge.dto.I18nItemDTO;
import com.panda.merge.dto.ThirdMarketOddsDTO;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.mapper.ThirdSportMarketOddsMapper;
import com.panda.merge.model.*;
import com.panda.merge.service.I18nOutrightMarketOddsService;
import com.panda.merge.service.ThirdSportMarketOddsNewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class ThirdSportMarketOddsNewServiceImpl implements ThirdSportMarketOddsNewService {
    @Autowired
    private RedisService redisService;
    @Autowired
    private I18nOutrightMarketOddsService i18nOutrightMarketOddsService;
    @Autowired
    private ThirdSportMarketOddsMapper thirdSportMarketOddsMapper;
    @Autowired
    private ThirdSportMarketOddsDao thirdSportMarketOddsDao;
    @Autowired
    private BaseProcessor baseProcessor;
    @Autowired
    private MarketDbProducer marketDbProducer;
    @Autowired
    @Qualifier("pandaOddsJdbcTemplate")
    private JdbcTemplate jdbcTemplate1;

    @Resource
    private RedisHelper redisHelper;

    @Override
    @Cacheable(key = "'ThirdSportMarketOdds:' + #thirdMarketId + '-' + #thirdOddsFieldSourceId", unless = "#result == null ")
    public ThirdSportMarketOdds getItem(String dataSourceCode, String thirdOddsFieldSourceId, Long thirdMarketId) {
        String sql = "select *  from third_sport_market_odds_" + dataSourceCode.toLowerCase() + " where data_source_code=? and third_odds_field_source_id=? and market_id=?";
        List<ThirdSportMarketOdds> thirdSportMarketOdds = jdbcTemplate1.query(sql, new Object[]{dataSourceCode, thirdOddsFieldSourceId, thirdMarketId}, new BeanPropertyRowMapper<>(ThirdSportMarketOdds.class));
        if (CollectionUtils.isEmpty(thirdSportMarketOdds)) {
            return null;
        }
        return thirdSportMarketOdds.get(0);
    }

    @Override
    public List<ThirdSportMarketOdds> getItems(List<OddsWrapper<ThirdMarketDTO>> thirdMarketDTOs) {
        if(CollectionUtils.isEmpty(thirdMarketDTOs)) {
            return Collections.emptyList();
        }

        List<ThirdSportMarketOdds> result = new ArrayList<>();
        List<OddsWrapper<ThirdMarketOddsDTO>> requiredCallItems = new ArrayList<>();

        // Obtaining data from redis
        List<OddsWrapper<ThirdMarketOddsDTO>> marketOddsDTOS = thirdMarketDTOs.stream().flatMap(t->{
            if(t.getData().getMarketOddsList() == null) {
                return null;
            }
            return t.getData().getMarketOddsList().stream().map(inner->{
                OddsWrapper<ThirdMarketOddsDTO> innerWrapper = new OddsWrapper<>();
                innerWrapper.setThirdSportMarketId(t.getThirdSportMarketId());
                innerWrapper.setData(inner);
                return innerWrapper;
            });
        }).filter(Objects::nonNull).collect(Collectors.toList());
        List<String> keys = marketOddsDTOS.stream().map(inner->RedisConfig.REDIS_KEY_DATABASE + "::ThirdSportMarketOdds:" + inner.getData().getThirdOddsFieldSourceId()
                +"-"+inner.getData().getDataSourceCode()+"-"+inner.getThirdSportMarketId()).distinct().collect(Collectors.toList());
        List<Object> objectList= redisService.mGet(keys);
        redisHelper.postProcMget(marketOddsDTOS, objectList, result, requiredCallItems);
        if(CollectionUtils.isEmpty(requiredCallItems)){
            return result;
        }
        Long uuid = UUIdUtils.getId();
        log.info("::{}::2724,查询三方盘口赔率数据库：{}",uuid, requiredCallItems);
        String sql = "select *  from third_sport_market_odds_" + thirdMarketDTOs.get(0).getDataSourceCode().toLowerCase()
                + " where (third_odds_field_source_id, data_source_code, market_id) in (";
        for (OddsWrapper<ThirdMarketOddsDTO> item : requiredCallItems) {
            sql += "(\"" + item.getData().getThirdOddsFieldSourceId() + "\", \""+ item.getData().getDataSourceCode() + "\", " + item.getThirdSportMarketId() + "), ";
        }
        sql = sql.substring(0, sql.length()-2) + ")";
        log.info("::{}::2724,查询三方盘口赔率数据库sql：{}",uuid, sql);

        List<ThirdSportMarketOdds> thirdSportMarketOdds = jdbcTemplate1.query(sql, new BeanPropertyRowMapper<>(ThirdSportMarketOdds.class));
        log.info("::{}::2724,查询三方盘口赔率数据库返回：{}",uuid, thirdSportMarketOdds.size());

        result.addAll(thirdSportMarketOdds);
        // Storing the remained data into redis
        Map<String, Object> redisVal = thirdSportMarketOdds.stream().collect(Collectors.toMap(t->
                RedisConfig.REDIS_KEY_DATABASE + "::ThirdSportMarketOdds:" + t.getThirdOddsFieldSourceId() +"-"+t.getDataSourceCode()
                        +"-"+t.getMarketId(), Function.identity(), (v1, v2) -> v1));
        redisService.mSet(redisVal);
        return result;
    }

    @Override
//    @Cacheable(key = "'ThirdSportMarketOdds:' + #marketId", unless = "#result == null ")
    public List<ThirdSportMarketOdds> getItemList(String dataSourceCode, Long marketId) {
        ThirdSportMarketOddsExample thirdSportMarketOddsExample = new ThirdSportMarketOddsExample();
        thirdSportMarketOddsExample.createCriteria().andMarketIdEqualTo(marketId).andDataSourceCodeEqualTo(dataSourceCode);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("select").append(" * ").append("from ").append(" third_sport_market_odds_" + dataSourceCode.toLowerCase()).append(" where ").append(" market_id=? ").append("AND data_source_code=?");
        List<ThirdSportMarketOdds> thirdSportMarketOddsList = jdbcTemplate1.query(stringBuffer.toString(), new Object[]{marketId, dataSourceCode}, new BeanPropertyRowMapper<>(ThirdSportMarketOdds.class));
        if (CollectionUtils.isEmpty(thirdSportMarketOddsList)) {
            return null;
        }
        return thirdSportMarketOddsList;
    }

    /**
     * 创建三方盘口投注项
     *
     * @param linkId
     * @param isOutRight
     * @param thirdMarketOddsDTO
     * @param thirdSportMarket
     * @param thirdMarketCategoryFieldId
     * @return
     */
    @CachePut(key = "'ThirdSportMarketOdds:' + #thirdSportMarket.id + '-' + #thirdMarketOddsDTO.thirdOddsFieldSourceId", unless = "#result == null ")
    @Override
    public ThirdSportMarketOdds create(String dataSourceCode, String linkId, boolean isOutRight, ThirdMarketOddsDTO thirdMarketOddsDTO, ThirdSportMarket thirdSportMarket, Long thirdMarketCategoryFieldId) {
        ThirdSportMarketOdds thirdSportMarketOdds = new ThirdSportMarketOdds();
        BeanUtils.copyProperties(thirdMarketOddsDTO, thirdSportMarketOdds);
        thirdSportMarketOdds.setId(UUIdUtils.getId());
        thirdSportMarketOdds.setMarketId(thirdSportMarket.getId());
        thirdSportMarketOdds.setOddsFieldsTemplateId(thirdMarketCategoryFieldId);
        thirdSportMarketOdds.setThirdTemplateSourceId(thirdMarketOddsDTO.getThirdTempletSourceId());
        thirdSportMarketOdds.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
        thirdSportMarketOdds.setModifyTime(thirdMarketOddsDTO.getModifyTime());
        thirdSportMarketOdds.setExtraInfo(thirdMarketOddsDTO.getExtraInfo());
        if (null == thirdSportMarketOdds.getModifyTime()) {
            thirdSportMarketOdds.setModifyTime(thirdSportMarket.getModifyTime());
        }
        thirdSportMarketOdds.setThirdMatchId(thirdSportMarket.getMatchId());
        thirdSportMarketOdds.setName(StandardSportMarketOddsServiceImpl.getOddsName(thirdMarketOddsDTO.getI18nNames()));
        thirdSportMarketOdds.setNameCode(thirdSportMarketOdds.getId());
        try {
            //发送mq
            marketDbProducer.sendThirdMarketOddsInsertInfo(linkId, Arrays.asList(thirdSportMarketOdds));
            //thirdSportMarketOddsMapper.insert(thirdSportMarketOdds);
        } catch (DuplicateKeyException e) {
            //此处只打印异常，即使入库失败该盘口投注项依然需要投递给下游
            log.info("::{}::insert三方盘口投注项唯一约束冲突,尝试重新入库，盘口主键ID:{},三方投注项ID:{}", linkId, thirdSportMarket.getId(), thirdMarketOddsDTO.getThirdOddsFieldSourceId());
        }
        try {
            //处理投注项国际化
            if (isOutRight && !CollectionUtils.isEmpty(thirdMarketOddsDTO.getI18nNames())) {
                List<I18nOutrightMarketOdds> i18nMarketOddsList = new ArrayList<>();
                for (I18nItemDTO dto : thirdMarketOddsDTO.getI18nNames()) {
                    I18nOutrightMarketOdds i18nOutrightMarketOdds = new I18nOutrightMarketOdds();
                    BeanUtils.copyProperties(dto, i18nOutrightMarketOdds);
                    i18nOutrightMarketOdds.setNameCode(thirdSportMarketOdds.getNameCode());
                    i18nOutrightMarketOdds.setDataSourceCode(thirdSportMarketOdds.getDataSourceCode());
                    i18nMarketOddsList.add(i18nOutrightMarketOdds);
                }
                i18nOutrightMarketOddsService.saveBatch(i18nMarketOddsList);
            }
        } catch (DuplicateKeyException e) {
            //此处只打印异常，即使入库失败该盘口投注项依然需要投递给下游
            log.info("::{}::insert三方盘口投注项国际化唯一约束冲突，error", linkId, e);
        }
        return thirdSportMarketOdds;
    }

    @Override
    @CachePut(key = "'ThirdSportMarketOdds:' + #thirdSportMarketOdds.marketId + '-' + #thirdSportMarketOdds.thirdOddsFieldSourceId")
    @Async("ThirdSportMarketThreadPool")
    public ThirdSportMarketOdds updateByPrimaryKeySelective(String dataSourceCode, ThirdSportMarketOdds thirdSportMarketOdds) {
        //发送mq
        marketDbProducer.sendThirdMarketOddsUpdateInfo("", Arrays.asList(thirdSportMarketOdds));
//        String key = RedisConfig.REDIS_KEY_DATABASE + "::ThirdSportMarketOdds:" + thirdSportMarketOdds.getMarketId();
//        redisService.del(key);
        return thirdSportMarketOdds;
    }

    @Deprecated
    @Override
    public void upThirdOddsList(String linkId, String dataSourceCode, List<ThirdSportMarketOdds> thirdSportMarketOddsList, List<ThirdMarketOddsDTO> thirdMarketOddsDTOS) {
        try {
            //刷新LIST缓存
//            String key = RedisConfig.REDIS_KEY_DATABASE + "::ThirdSportMarketOdds:" + thirdSportMarketOddsList.get(0).getMarketId();
//            if (thirdMarketOddsDTOS.size() == thirdSportMarketOddsList.size()) {
//                redisService.set(key, thirdSportMarketOddsList);
//                log.info("::{}::upThirdOddsList缓存KEY:{}", linkId, key);
//            } else {
//                redisService.del(key);
//                log.info("::{}::upThirdOddsList删除缓存KEY:{}", linkId, key);
//            }
            //刷新单挑缓存
            thirdSportMarketOddsList.forEach(odds -> {
                String key1 = RedisConfig.REDIS_KEY_DATABASE + "::ThirdSportMarketOdds:" + odds.getMarketId() + '-' + odds.getThirdOddsFieldSourceId();
                redisService.set(key1, odds);
            });
        } catch (Exception e) {
            //此处只打印异常，即使入库失败该盘口投注项依然需要投递给下游
            log.info("::{}::upThirdOddsList三方盘口投注项唯一约束冲突，error", linkId, e);
        }
    }

    @Override
    public void upThirdOddsAsyncList(String linkId, String dataSourceCode, List<ThirdSportMarketOdds> thirdSportMarketOddsList, List<ThirdMarketOddsDTO> thirdMarketOddsDTOS) {
//        String key = RedisConfig.REDIS_KEY_DATABASE + "::ThirdSportMarketOdds:" + thirdSportMarketOddsList.get(0).getMarketId();
//        if (thirdMarketOddsDTOS.size() == thirdSportMarketOddsList.size()) {
//            redisService.set(key, thirdSportMarketOddsList);
//            log.info("::{}::upThirdOddsAsyncList缓存KEY:{}", linkId, key);
//        } else {
//            redisService.del(key);
//            log.info("::{}::upThirdOddsAsyncList删除缓存KEY:{}", linkId, key);
//        }
        //刷新单挑缓存
        thirdSportMarketOddsList.forEach(odds -> {
            String key1 = RedisConfig.REDIS_KEY_DATABASE + "::ThirdSportMarketOdds:" + odds.getMarketId() + '-' + odds.getThirdOddsFieldSourceId();
            redisService.set(key1, odds);
        });
    }

    @Override
    @Async("ProcessOddsByPandaThreadPool")
    public void upThirdOddsAsyncList(List<ThirdSportMarketOdds> upOddsList) {
        //刷新单挑缓存
        if(CollectionUtils.isEmpty(upOddsList)) {
            return;
        }
        Map<String, Object> marketOddsMap = upOddsList.stream().collect(Collectors.toMap(t->RedisConfig.REDIS_KEY_DATABASE
                + "::ThirdSportMarketOdds:" + t.getMarketId() + '-' + t.getThirdOddsFieldSourceId(), Function.identity(), (v1, v2)->v1));
        redisService.mSet(marketOddsMap);
    }


    @Override
    public Long getRelationMarketOddsId(Long relationMarketId, String oddsType, String thirdOddsFieldSourceId, String addition1, Long marketGategoryId) {
        Long relationMarketOddsId;
        //兼容冠军投注项id历史数据
        if (MarginCategoryConfig.STANDARD_OUTRIGHT_CATEGORY.contains(marketGategoryId)) {
            StringBuffer redisKey = new StringBuffer(Constant.REDIS_KEY.RONGHE_STANDARD_MARKET_ODDS_RELATION_MARKET_ODDS_ID);
            String key = redisKey.append(relationMarketId).append("_").append(oddsType).toString();
            if (redisService.get(key) != null && !StringUtils.isEmpty(redisService.get(key).toString())) {
                return Long.valueOf(redisService.get(key).toString());
            }
        }
        String redisKey = RelationKeyFactory.getMarketOddsRelationKeyByThirdOddsInfo(relationMarketId, oddsType, thirdOddsFieldSourceId, addition1, marketGategoryId);
        Object obj = redisService.get(redisKey);
        if (obj == null || StringUtils.isEmpty(obj.toString())) {
            relationMarketOddsId = MD5Utils.getLongByMD5(redisKey);//UUIdUtils.getId();
        } else {
            relationMarketOddsId = Long.valueOf(redisService.get(redisKey).toString());
        }
        return relationMarketOddsId;
    }


    @Async("ProcessOddsByPandaThreadPool")
    public void insertMatchCategoryOddsOfRedis(String linkId, Long matchId, Set<Long> marketCategoryIdSet, Long beginTime, Long dataSourceTime) {
        log.info("::{}::insertMatchCategoryOddsOfRedis 处理赛事玩法赔率最新更新时间。赛事Id:{}, 玩法集合:{}, 赛事开始时间:{}", linkId, matchId, marketCategoryIdSet, beginTime);
        if (matchId == null || CollectionUtils.isEmpty(marketCategoryIdSet)) {
            return;
        }
        String key = Constant.REDIS_KEY.RONGHE_MATCH_CATEGORY_ODDS_UPDATETIME + matchId;
        for (Long categoryId : marketCategoryIdSet) {
            String redisDateKey = Constant.REDIS_KEY.RONGHE_MATCH_CATEGORY_ODDS_UPDATETIME_DATE + matchId + "_" + categoryId;
            Object oldTime = redisService.get(redisDateKey);
            if (oldTime == null || ((Long) oldTime) <= dataSourceTime) {
                redisService.hSet(key, categoryId.toString(), System.currentTimeMillis(), baseProcessor.marketCacheTime(beginTime));
                redisService.set(redisDateKey, dataSourceTime, RedisConfig.REDIS_MY_TIME);
            } else {
                log.info("::{}::insertMatchCategoryOddsOfRedis,不是最新的赔率数据，不缓存，赛事Id：{},玩法ID:{},oldTime:{},dataSourceTime:{}。", linkId, matchId, categoryId, oldTime, dataSourceTime);
            }
        }
    }

    @Async("ProcessOddsByPandaThreadPool")
    public void insertMatchMarketOddsOfRedis(String linkId, Long matchId, List<StandardMarketDataMessage> standardMarketDataMessageList, Long beginTime, Long dataSourceTime) {
        log.info("::{}::insertMatchMarketOddsOfRedis 处理赛事盘口赔率最新更新时间。赛事Id:{}, 最新更新时间:{}, 盘口集合:{}", linkId, matchId, dataSourceTime, standardMarketDataMessageList);
        if (matchId == null || CollectionUtils.isEmpty(standardMarketDataMessageList)) {
            return;
        }
        String key = Constant.REDIS_KEY.RONGHE_MATCH_MARKET_ODDS_UPDATETIME + matchId;
        String tagKey = Constant.REDIS_KEY.RONGHE_MATCH_CATEGORY_TAG + matchId;
        for (StandardMarketDataMessage market : standardMarketDataMessageList) {
            String redisDateKey = Constant.REDIS_KEY.RONGHE_MATCH_MARKET_ODDS_UPDATETIME_DATE + matchId + "_" + market.getRelationMarketId();
            Object oldTime = redisService.get(redisDateKey);
            if (oldTime == null || ((Long) oldTime) <= dataSourceTime) {
                redisService.hDel(key, market.getMarketCategoryId() + "_" + market.getRelationMarketId() + "_" + market.getDataSourceCode() + "_" + Constant.SPORT_MARKET.STATUS.SUSPENDED);
                redisService.hDel(key, market.getMarketCategoryId() + "_" + market.getRelationMarketId() + "_" + market.getDataSourceCode() + "_" + Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                redisService.hSet(key, market.getMarketCategoryId() + "_" + market.getRelationMarketId() + "_" + market.getDataSourceCode() + "_" + market.getOldThirdMarketSourceStatus(), System.currentTimeMillis(), baseProcessor.marketCacheTime(beginTime));
                redisService.set(redisDateKey, dataSourceTime, RedisConfig.REDIS_MY_TIME);
                //记录全场强开标识
                if (MarginCategoryConfig.MATCH_PERIOD_CATEGORY_OPEN.containsKey(market.getMarketCategoryId())) {
                    redisService.hSet(tagKey, market.getMarketCategoryId().toString(), System.currentTimeMillis(), baseProcessor.marketCacheTime(beginTime));
                    log.info("::{}::insertMatchMarketOddsOfRedis 记录全场强开标识,赛事Id:{}, 玩法ID:{}", linkId, matchId, market.getMarketCategoryId());
                }
            } else {
                log.info("::{}::insertMatchMarketOddsOfRedis,不是最新的赔率数据，不缓存，赛事Id：{},玩法ID:{},oldTime:{},dataSourceTime:{}。", linkId, matchId, market.getRelationMarketId(), oldTime, dataSourceTime);
            }
        }
    }

    @Async("ProcessOddsByPandaThreadPool")
    public void deleteMatchMarketOddsByActive(String linkId, Long matchId, List<StandardMarketDataMessage> standardMarketDataMessageList, Long dataSourceTime) {
        log.info("::{}::deleteMatchMarketOddsByActive 删除被2.0关盘状态修改为开盘的赛事盘口赔率最新更新时间数据。赛事Id:{}", linkId, matchId);
        if (matchId == null || CollectionUtils.isEmpty(standardMarketDataMessageList)) {
            return;
        }
        Set<StandardMarketDataMessage> atvList = standardMarketDataMessageList.stream().filter(e -> Constant.SPORT_MARKET.STATUS.ACTIVE.equals(e.getThirdMarketSourceStatus()) && e.getOldThirdMarketSourceStatus() == null).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(atvList)) {
            return;
        }
        deleteMatchMarketOddsOfRedis(linkId, matchId, atvList, dataSourceTime);
    }

    @Async("ProcessOddsByPandaThreadPool")
    public void deleteMatchMarketOddsOfRedis(String linkId, Long matchId, Set<StandardMarketDataMessage> standardMarketDataMessageList, Long dataSourceTime) {
        log.info("::{}::deleteMatchMarketOddsOfRedis 删除被2.0关盘状态修改为开盘的赛事盘口赔率最新更新时间数据。赛事Id:{}", linkId, matchId);
        if (matchId == null || CollectionUtils.isEmpty(standardMarketDataMessageList)) {
            return;
        }
        log.info("::{}::deleteMatchMarketOddsOfRedis 删除被2.0关盘状态修改为开盘的赛事盘口赔率最新更新时间数据。需要清除缓存的盘口集合:{}", linkId, standardMarketDataMessageList);
        for (StandardMarketDataMessage market : standardMarketDataMessageList) {
            deleteMarketOddsOfRedis(linkId, matchId, market, dataSourceTime);
        }
    }

    @Async("ProcessOddsByPandaThreadPool")
    public void deleteMatchMarketOddsOfRedisByActive(String linkId, Long matchId, Set<StandardMarketDataMessage> marketActive, Long dataSourceTime) {
        log.info("::{}::deleteMatchMarketOddsOfRedisByCategory 删除被2.0关盘状态修改为开盘的赛事盘口赔率最新更新时间数据。赛事Id:{}", linkId, matchId);
        if (matchId == null || CollectionUtils.isEmpty(marketActive)) {
            return;
        }
        log.info("::{}::deleteMatchMarketOddsOfRedisByCategory 删除被2.0关盘状态修改为开盘的赛事盘口赔率最新更新时间数据。需要清除缓存的玩法Id集合:{}", linkId, marketActive);
        for (StandardMarketDataMessage sm : marketActive) {
            deleteMarketOddsOfRedis(linkId, matchId, sm, dataSourceTime);
        }
    }

    @Async("ProcessOddsByPandaThreadPool")
    public void deleteMatchMarketOddsOfRedisByCategory(String linkId, Long matchId, Set<Long> categoryIds, Long dataSourceTime) {
        log.info("::{}::deleteMatchMarketOddsOfRedisByCategory 删除被2.0关盘状态修改为开盘的赛事盘口赔率最新更新时间数据。赛事Id:{}", linkId, matchId);
        if (matchId == null || CollectionUtils.isEmpty(categoryIds)) {
            return;
        }
        log.info("::{}::deleteMatchMarketOddsOfRedisByCategory 删除被2.0关盘状态修改为开盘的赛事盘口赔率最新更新时间数据。需要清除缓存的玩法Id集合:{}", linkId, categoryIds);
        String key = Constant.REDIS_KEY.RONGHE_MATCH_MARKET_ODDS_UPDATETIME + matchId;
        Map<String, Long> markets = redisService.hGetAll(key);
        if (CollectionUtils.isEmpty(markets)) {
            return;
        }
        Set<String> keySet = markets.keySet();
        String[] mk;
        for (String marketKey : keySet) {
            mk = marketKey.split("_");
            String redisDateKey = Constant.REDIS_KEY.RONGHE_MATCH_MARKET_ODDS_UPDATETIME_DATE + matchId + "_" + mk[1];
            Object oldTime = redisService.get(redisDateKey);
            if (oldTime != null && ((Long) oldTime) > dataSourceTime) {
                log.info("::{}::deleteMatchMarketOddsOfRedisByCategory, 请求数据已过期，赛事Id：{},盘口ID:{},oldTime:{},dataSourceTime:{}。", linkId, matchId, mk[1], oldTime, dataSourceTime);
                continue;
            }
            if (categoryIds.contains(Long.valueOf(mk[0]))) {
                redisService.hDel(key, marketKey);
            }
        }
    }

    private void deleteMarketOddsOfRedis(String linkId, Long matchId, StandardMarketDataMessage market, Long dataSourceTime) {
        String key = Constant.REDIS_KEY.RONGHE_MATCH_MARKET_ODDS_UPDATETIME + matchId;
        String redisDateKey = Constant.REDIS_KEY.RONGHE_MATCH_MARKET_ODDS_UPDATETIME_DATE + matchId + "_" + market.getRelationMarketId();
        String tagKey = Constant.REDIS_KEY.RONGHE_MATCH_CATEGORY_TAG + matchId;
        Object oldTime = redisService.get(redisDateKey);
        if (oldTime == null || ((Long) oldTime) <= dataSourceTime) {
            Object obj1 = redisService.hGet(key, market.getMarketCategoryId() + "_" + market.getRelationMarketId() + "_" + market.getDataSourceCode() + "_" + Constant.SPORT_MARKET.STATUS.SUSPENDED);
            if (obj1 != null) {
                log.info("::{}::deleteMatchMarketOddsOfRedis,清理100s 缓存。key:{}", linkId, market.getMarketCategoryId() + "_" + market.getRelationMarketId() + "_" + market.getDataSourceCode() + "_" + Constant.SPORT_MARKET.STATUS.SUSPENDED);
                redisService.hDel(key, market.getMarketCategoryId() + "_" + market.getRelationMarketId() + "_" + market.getDataSourceCode() + "_" + Constant.SPORT_MARKET.STATUS.SUSPENDED);
            }
            Object obj2 = redisService.hGet(key, market.getMarketCategoryId() + "_" + market.getRelationMarketId() + "_" + market.getDataSourceCode() + "_" + Constant.SPORT_MARKET.STATUS.DEACTIVATED);
            if (obj2 != null) {
                log.info("::{}::deleteMatchMarketOddsOfRedis,清理100s 缓存。key:{}", linkId, market.getMarketCategoryId() + "_" + market.getRelationMarketId() + "_" + market.getDataSourceCode() + "_" + Constant.SPORT_MARKET.STATUS.SUSPENDED);
                redisService.hDel(key, market.getMarketCategoryId() + "_" + market.getRelationMarketId() + "_" + market.getDataSourceCode() + "_" + Constant.SPORT_MARKET.STATUS.DEACTIVATED);
            }
            if (DataSourceCodeEnum.TX.code.equals(market.getDataSourceCode()) && !StringUtils.isEmpty(market.getSendData())) {
                log.info("::{}::deleteMatchMarketOddsOfRedis,清理100s 缓存。sendDatakey:{}", linkId, market.getMarketCategoryId() + "_" + market.getSendData() + "_" + market.getDataSourceCode());
                redisService.hDel(key, market.getMarketCategoryId() + "_" + market.getSendData() + "_" + market.getDataSourceCode() + "_" + Constant.SPORT_MARKET.STATUS.SUSPENDED);
                redisService.hDel(key, market.getMarketCategoryId() + "_" + market.getSendData() + "_" + market.getDataSourceCode() + "_" + Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                String redisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + matchId + "_" + market.getDataSourceCode() + "_" + market.getMarketCategoryId());
                StandardSportMarket standardSportMarket = (StandardSportMarket) redisService.hGet(redisKey, market.getSendData());
                if (standardSportMarket != null && standardSportMarket.getOldThirdMarketSourceStatus() != null) {
                    log.info("::{}::deleteMatchMarketOddsOfRedis,重置盘口缓存。盘口状态更新为:{},更新之前三方状态:{}", linkId, standardSportMarket.getOldThirdMarketSourceStatus(), standardSportMarket.getThirdMarketSourceStatus());
                    standardSportMarket.setThirdMarketSourceStatus(standardSportMarket.getOldThirdMarketSourceStatus());
                    standardSportMarket.setStatus(standardSportMarket.getOldThirdMarketSourceStatus());
                    redisService.hSet(redisKey, market.getSendData(), standardSportMarket, RedisConfig.REDIS_MY_TIME);
                }
            }
            redisService.set(redisDateKey, dataSourceTime, RedisConfig.REDIS_MY_TIME);
            //清除全场强开标识
            redisService.hDel(tagKey, market.getMarketCategoryId().toString());
        } else {
            log.info("::{}::deleteMatchMarketOddsOfRedis,不是最新的赔率数据，不缓存，赛事Id：{},玩法ID:{},oldTime:{},dataSourceTime:{}。", linkId, matchId, market.getMarketCategoryId(), oldTime, dataSourceTime);
        }
    }
}
