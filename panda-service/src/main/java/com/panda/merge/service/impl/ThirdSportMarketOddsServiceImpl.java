package com.panda.merge.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.utils.MD5Utils;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.MarginCategoryConfig;
import com.panda.merge.constant.RelationKeyFactory;
import com.panda.merge.dao.ThirdSportMarketOddsDao;
import com.panda.merge.dto.I18nItemDTO;
import com.panda.merge.dto.ThirdMarketOddsDTO;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.mapper.ThirdSportMarketOddsMapper;
import com.panda.merge.model.*;
import com.panda.merge.service.I18nOutrightMarketOddsService;
import com.panda.merge.service.ThirdSportMarketOddsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/15 <br>
 */
@Slf4j
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class ThirdSportMarketOddsServiceImpl implements ThirdSportMarketOddsService {
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

    @Override
    @Cacheable(key = "'ThirdSportMarketOdds:' + #thirdMarketId + '-' + #thirdOddsFieldSourceId",unless = "#result == null ")
    public ThirdSportMarketOdds getItem(String dataSourceCode, String thirdOddsFieldSourceId, Long thirdMarketId) {
        ThirdSportMarketOddsExample thirdSportMarketOddsExample = new ThirdSportMarketOddsExample();
        thirdSportMarketOddsExample.createCriteria().andDataSourceCodeEqualTo(dataSourceCode)
                .andThirdOddsFieldSourceIdEqualTo(thirdOddsFieldSourceId)
                .andMarketIdEqualTo(thirdMarketId);
        List<ThirdSportMarketOdds> thirdSportMarketOdds = null;
        thirdSportMarketOdds = thirdSportMarketOddsMapper.selectByExample(thirdSportMarketOddsExample);
        if(CollectionUtils.isEmpty(thirdSportMarketOdds)){
            return null;
        }
        return thirdSportMarketOdds.get(0);
    }

    @Override
    //@Cacheable(key = "'ThirdSportMarketOdds:' + #marketId",unless = "#result == null ")
    public List<ThirdSportMarketOdds> getItemList(String dataSourceCode,Long marketId) {
        ThirdSportMarketOddsExample thirdSportMarketOddsExample = new ThirdSportMarketOddsExample();
        thirdSportMarketOddsExample.createCriteria().andMarketIdEqualTo(marketId).andDataSourceCodeEqualTo(dataSourceCode);
        List<ThirdSportMarketOdds> thirdSportMarketOddsList = thirdSportMarketOddsMapper.selectByExample(thirdSportMarketOddsExample);
        if (CollectionUtils.isEmpty(thirdSportMarketOddsList)) {
            return null;
        }
        return thirdSportMarketOddsList;
    }

    /**
     * 创建三方盘口投注项
     * @param linkId
     * @param isOutRight
     * @param thirdMarketOddsDTO
     * @param thirdSportMarket
     * @param thirdMarketCategoryFieldId
     * @return
     */
    @CachePut(key = "'ThirdSportMarketOdds:' + #thirdSportMarket.id + '-' + #thirdMarketOddsDTO.thirdOddsFieldSourceId",unless = "#result == null ")
    @Override
    public ThirdSportMarketOdds create(String dataSourceCode,String linkId, boolean isOutRight, ThirdMarketOddsDTO thirdMarketOddsDTO, ThirdSportMarket thirdSportMarket, Long thirdMarketCategoryFieldId) {
        ThirdSportMarketOdds thirdSportMarketOdds = new ThirdSportMarketOdds();
        BeanUtils.copyProperties(thirdMarketOddsDTO, thirdSportMarketOdds);
        thirdSportMarketOdds.setId(UUIdUtils.getId());
        thirdSportMarketOdds.setMarketId(thirdSportMarket.getId());
        thirdSportMarketOdds.setOddsFieldsTemplateId(thirdMarketCategoryFieldId);
        thirdSportMarketOdds.setThirdTemplateSourceId(thirdMarketOddsDTO.getThirdTempletSourceId());
        thirdSportMarketOdds.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
        thirdSportMarketOdds.setModifyTime(thirdMarketOddsDTO.getModifyTime());
        thirdSportMarketOdds.setThirdMatchId(thirdSportMarket.getMatchId());
        thirdSportMarketOdds.setName(StandardSportMarketOddsServiceImpl.getOddsName(thirdMarketOddsDTO.getI18nNames()));
        thirdSportMarketOdds.setNameCode(thirdSportMarketOdds.getId());
        try {
            thirdSportMarketOddsMapper.insert(thirdSportMarketOdds);
        } catch (DuplicateKeyException e) {
            //此处只打印异常，即使入库失败该盘口投注项依然需要投递给下游
            log.info("::{}::insert三方盘口投注项唯一约束冲突,尝试重新入库，盘口主键ID:{},三方投注项ID:{}",
                    linkId, thirdSportMarket.getId(), thirdMarketOddsDTO.getThirdOddsFieldSourceId());
            //重新入库
        	createReplenish(linkId,thirdSportMarketOdds);
//            String key = RedisConfig.REDIS_KEY_DATABASE + "::ThirdSportMarketOdds:" + thirdSportMarketOdds.getMarketId();
//            redisService.del(key);
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
    
    /**
     * 创建失败后第二次尝试
     */
    @CachePut(key = "'ThirdSportMarketOdds:' + #thirdSportMarketOdds.marketId + '-' + #thirdSportMarketOdds.thirdOddsFieldSourceId")
    public ThirdSportMarketOdds createReplenish(String linkId,ThirdSportMarketOdds thirdSportMarketOdds) {
    	try {
        	ThirdSportMarketOddsExample thirdSportMarketOddsExample = new ThirdSportMarketOddsExample();
            thirdSportMarketOddsExample.createCriteria().andMarketIdEqualTo(thirdSportMarketOdds.getMarketId())
            .andDataSourceCodeEqualTo(thirdSportMarketOdds.getDataSourceCode())
            .andThirdOddsFieldSourceIdEqualTo(thirdSportMarketOdds.getThirdOddsFieldSourceId());
            List<ThirdSportMarketOdds> list = thirdSportMarketOddsMapper.selectByExample(thirdSportMarketOddsExample);
            
            if(CollectionUtils.isEmpty(list)) {
            	//重新入库
            	thirdSportMarketOdds.setId(UUIdUtils.getId());
                thirdSportMarketOdds.setNameCode(thirdSportMarketOdds.getId());
                thirdSportMarketOddsMapper.insert(thirdSportMarketOdds);
            }else {
            	int count = updateByMarketIdAndDataSourceCodeAndFieldId(linkId,thirdSportMarketOdds);
            	if(count > 0) {
               	 	String key = RedisConfig.REDIS_KEY_DATABASE + "::ThirdSportMarketOdds:" + thirdSportMarketOdds.getMarketId()+"-"+thirdSportMarketOdds.getThirdOddsFieldSourceId();
                    redisService.del(key);
                    log.info("::{}:: update三方盘口投注项信息 成功，盘口主键ID:{},三方投注项ID:{}",
                            linkId, thirdSportMarketOdds.getMarketId(), thirdSportMarketOdds.getThirdOddsFieldSourceId());
               }else {
               		log.info("::{}:: update三方盘口投注项信息 发现此数据已经属于旧数据，盘口主键ID:{},三方投注项ID:{}",
               			linkId, thirdSportMarketOdds.getMarketId(), thirdSportMarketOdds.getThirdOddsFieldSourceId());
               		return list.get(0);
               }
            }
        } catch (DuplicateKeyException d) {
            log.info("::{}:: insert 三方盘口投注项失败2，盘口主键ID:{},三方投注项ID:{}，error",
                    linkId, thirdSportMarketOdds.getMarketId(), thirdSportMarketOdds.getThirdOddsFieldSourceId(), d);
        }
		return thirdSportMarketOdds;
    }
    
    /**
     * 根据盘口Id,数据商，投注项修改数据
     * @param linkId
     * @param thirdSportMarketOdds
     * @return
     */
    public int updateByMarketIdAndDataSourceCodeAndFieldId(String linkId,ThirdSportMarketOdds thirdSportMarketOdds) {
        ThirdSportMarketOddsExample thirdSportMarketOddsExample = new ThirdSportMarketOddsExample();
        thirdSportMarketOddsExample.createCriteria().andMarketIdEqualTo(thirdSportMarketOdds.getMarketId())
        .andDataSourceCodeEqualTo(thirdSportMarketOdds.getDataSourceCode())
        .andThirdOddsFieldSourceIdEqualTo(thirdSportMarketOdds.getThirdOddsFieldSourceId())
        .andModifyTimeLessThan(thirdSportMarketOdds.getModifyTime());
        int count = thirdSportMarketOddsMapper.updateByExampleSelective(thirdSportMarketOdds, thirdSportMarketOddsExample);
        return count;
    }

    @Override
    @CachePut(key = "'ThirdSportMarketOdds:' + #thirdSportMarketOdds.marketId + '-' + #thirdSportMarketOdds.thirdOddsFieldSourceId")
    @Async("ThirdSportMarketThreadPool")
    public ThirdSportMarketOdds updateByPrimaryKeySelective(String dataSourceCode, ThirdSportMarketOdds thirdSportMarketOdds) {
        ThirdSportMarketOddsExample thirdSportMarketOddsExample = new ThirdSportMarketOddsExample();
        thirdSportMarketOddsExample.createCriteria().andIdEqualTo(thirdSportMarketOdds.getId()).andDataSourceCodeEqualTo(dataSourceCode);
        thirdSportMarketOddsMapper.updateByExampleSelective(thirdSportMarketOdds, thirdSportMarketOddsExample);
//        String key = RedisConfig.REDIS_KEY_DATABASE + "::ThirdSportMarketOdds:" + thirdSportMarketOdds.getMarketId();
//        redisService.del(key);
        return thirdSportMarketOdds;
    }

    @Override
//    @Async("ThirdSportMarketThreadPool")
    public void upThirdOddsList(String linkId, String dataSourceCode, List<ThirdSportMarketOdds> thirdSportMarketOddsList, List<ThirdMarketOddsDTO> thirdMarketOddsDTOS) {
        try {
            //批量修改投注项
            //thirdSportMarketOddsDao.upDataList(thirdSportMarketOddsList, dataSourceCode);
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
                thirdSportMarketOddsMapper.updateByPrimaryKeySelective(odds);
                String key1 = RedisConfig.REDIS_KEY_DATABASE + "::ThirdSportMarketOdds:" + odds.getMarketId() + '-' + odds.getThirdOddsFieldSourceId();
                redisService.set(key1, odds);
            });
        } catch (Exception e) {
            //此处只打印异常，即使入库失败该盘口投注项依然需要投递给下游
            log.info("::{}::upThirdOddsList三方盘口投注项唯一约束冲突，error", linkId, e);
        }
    }

    @Override
    @Async("ThirdSportMarketThreadPool")
    public void upThirdOddsAsyncList(String linkId, String dataSourceCode, List<ThirdSportMarketOdds> thirdSportMarketOddsList, List<ThirdMarketOddsDTO> thirdMarketOddsDTOS) {
        //thirdSportMarketOddsDao.upDataList(thirdSportMarketOddsList, dataSourceCode);
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
            thirdSportMarketOddsMapper.updateByPrimaryKeySelective(odds);
            String key1 = RedisConfig.REDIS_KEY_DATABASE + "::ThirdSportMarketOdds:" + odds.getMarketId() + '-' + odds.getThirdOddsFieldSourceId();
            redisService.set(key1, odds);
        });
    }

    @Override
    public Long getRelationMarketOddsId(Long relationMarketId, String oddsType,String thirdOddsFieldSourceId,String addition1, Long marketGategoryId) {
        Long relationMarketOddsId;
        //兼容冠军投注项id历史数据
        if (MarginCategoryConfig.STANDARD_OUTRIGHT_CATEGORY.contains(marketGategoryId)) {
            StringBuffer redisKey = new StringBuffer(Constant.REDIS_KEY.RONGHE_STANDARD_MARKET_ODDS_RELATION_MARKET_ODDS_ID);
            String key = redisKey.append(relationMarketId).append("_").append(oddsType).toString();
            if (redisService.get(key) != null && !StringUtils.isEmpty(redisService.get(key).toString())) {
                return Long.valueOf(redisService.get(key).toString());
            }
        }
        String redisKey = RelationKeyFactory.getMarketOddsRelationKeyByThirdOddsInfo(relationMarketId,oddsType,thirdOddsFieldSourceId,addition1,marketGategoryId);
        Object obj = redisService.get(redisKey);
        if (obj == null || StringUtils.isEmpty(obj.toString())) {
            relationMarketOddsId = MD5Utils.getLongByMD5(redisKey);//UUIdUtils.getId();
        } else {
            relationMarketOddsId = Long.valueOf(obj.toString());
        }
        return relationMarketOddsId;
    }

    @Override
    public Integer delOdds() {
        ThirdSportMarketOddsExample thirdSportMarketOddsExample = new ThirdSportMarketOddsExample();
        return thirdSportMarketOddsMapper.deleteByExample(thirdSportMarketOddsExample);
    }

    @Override
    public List<ThirdSportMarketOdds> getItemListByParam(Long id,Long limit) {
        return thirdSportMarketOddsDao.getListByParam(id,limit);
    }

    @Override
    public void insert(List<ThirdSportMarketOdds> thirdSportMarketOdds) {
        if (CollectionUtils.isEmpty(thirdSportMarketOdds))
        {
            return;
        }
        for (ThirdSportMarketOdds thirdSportMarketOdds1 : thirdSportMarketOdds)
        {
            try{
                ThirdSportMarketOddsExample thirdSportMarketOddsExample = new ThirdSportMarketOddsExample();
                thirdSportMarketOddsExample.createCriteria().andIdEqualTo(thirdSportMarketOdds1.getId())
                        .andDataSourceCodeEqualTo(thirdSportMarketOdds1.getDataSourceCode());
                List<ThirdSportMarketOdds> list = thirdSportMarketOddsMapper.selectByExample(thirdSportMarketOddsExample);
                if (CollectionUtils.isEmpty(list))
                {
                    thirdSportMarketOddsMapper.insert(thirdSportMarketOdds1);
                }
                else
                {
                    ThirdSportMarketOddsExample thirdSportMarketOddsExample1 = new ThirdSportMarketOddsExample();
                    thirdSportMarketOddsExample1.createCriteria().andIdEqualTo(thirdSportMarketOdds1.getId()).andModifyTimeIsNotNull().
                            andModifyTimeGreaterThan(thirdSportMarketOdds1.getModifyTime()).andDataSourceCodeEqualTo(thirdSportMarketOdds1.getDataSourceCode());
                    thirdSportMarketOddsMapper.updateByExample(thirdSportMarketOdds1,thirdSportMarketOddsExample1);
                }
            }catch(Exception e)
            {
                if (thirdSportMarketOdds1.getModifyTime() != null)
                {
                    ThirdSportMarketOddsExample thirdSportMarketOddsExample = new ThirdSportMarketOddsExample();
                    thirdSportMarketOddsExample.createCriteria().andIdEqualTo(thirdSportMarketOdds1.getId()).andModifyTimeIsNotNull().
                            andModifyTimeGreaterThan(thirdSportMarketOdds1.getModifyTime()).andDataSourceCodeEqualTo(thirdSportMarketOdds1.getDataSourceCode());
                    thirdSportMarketOddsMapper.updateByExample(thirdSportMarketOdds1,thirdSportMarketOddsExample);
                }
            }
        }
    }

    @Override
    public Long getMaxId() {
        return thirdSportMarketOddsDao.getMaxId();
    }
    
    @Async("ProcessOddsByPandaThreadPool")
    public void insertMatchCategoryOddsOfRedis(String linkId, Long matchId, Set<Long> marketCategoryIdSet, Long beginTime, Long dataSourceTime) {
    	log.info("::{}::insertMatchCategoryOddsOfRedis 处理赛事玩法赔率最新更新时间。赛事Id:{}, 玩法集合:{}, 赛事开始时间:{}",linkId,matchId,marketCategoryIdSet,beginTime);
    	if(matchId == null || CollectionUtils.isEmpty(marketCategoryIdSet)) {
    		return;
    	}
    	String key = Constant.REDIS_KEY.RONGHE_MATCH_CATEGORY_ODDS_UPDATETIME+matchId;
    	for(Long categoryId:marketCategoryIdSet) {
    		String redisDateKey = Constant.REDIS_KEY.RONGHE_MATCH_CATEGORY_ODDS_UPDATETIME_DATE + matchId+"_"+categoryId;
            Object oldTime = redisService.get(redisDateKey);
            if (oldTime == null || ((Long)oldTime) <= dataSourceTime)
            {
            	redisService.hSet(key, categoryId.toString(), System.currentTimeMillis(),baseProcessor.marketCacheTime(beginTime));
            	redisService.set(redisDateKey,dataSourceTime, RedisConfig.REDIS_MY_TIME);
            }else {
            	log.info("::{}::insertMatchCategoryOddsOfRedis,不是最新的赔率数据，不缓存，赛事Id：{},玩法ID:{},oldTime:{},dataSourceTime:{}。",
                        linkId,matchId,categoryId,oldTime,dataSourceTime);
            }
    	}
    }
    
    @Async("ProcessOddsByPandaThreadPool")
    public void insertMatchMarketOddsOfRedis(String linkId, Long matchId, List<StandardMarketDataMessage> standardMarketDataMessageList, Long beginTime, Long dataSourceTime) {
    	log.info("::{}::insertMatchMarketOddsOfRedis 处理赛事盘口赔率最新更新时间。赛事Id:{}, 最新更新时间:{}, 盘口集合:{}",linkId,matchId,dataSourceTime,standardMarketDataMessageList);
    	if(matchId == null || CollectionUtils.isEmpty(standardMarketDataMessageList)) {
    		return;
    	}
    	String key = Constant.REDIS_KEY.RONGHE_MATCH_MARKET_ODDS_UPDATETIME+matchId;
    	String tagKey = Constant.REDIS_KEY.RONGHE_MATCH_CATEGORY_TAG+matchId;
    	for(StandardMarketDataMessage market:standardMarketDataMessageList) {
    		String redisDateKey = Constant.REDIS_KEY.RONGHE_MATCH_MARKET_ODDS_UPDATETIME_DATE + matchId+"_"+market.getRelationMarketId();
            Object oldTime = redisService.get(redisDateKey);
            if (oldTime == null || ((Long)oldTime) <= dataSourceTime)
            {
            	redisService.hDel(key, market.getMarketCategoryId()+"_"+market.getRelationMarketId()+"_"+market.getDataSourceCode()+"_"+Constant.SPORT_MARKET.STATUS.SUSPENDED);
            	redisService.hDel(key, market.getMarketCategoryId()+"_"+market.getRelationMarketId()+"_"+market.getDataSourceCode()+"_"+Constant.SPORT_MARKET.STATUS.DEACTIVATED);
            	redisService.hSet(key, market.getMarketCategoryId()+"_"+market.getRelationMarketId()+"_"+market.getDataSourceCode()+"_"+market.getOldThirdMarketSourceStatus(), System.currentTimeMillis(),baseProcessor.marketCacheTime(beginTime));
            	redisService.set(redisDateKey,dataSourceTime, RedisConfig.REDIS_MY_TIME);
                //记录全场强开标识
                if (MarginCategoryConfig.MATCH_PERIOD_CATEGORY_OPEN.containsKey(market.getMarketCategoryId())) {
                    redisService.hSet(tagKey, market.getMarketCategoryId().toString(), System.currentTimeMillis(), baseProcessor.marketCacheTime(beginTime));
                    log.info("::{}::insertMatchMarketOddsOfRedis 记录全场强开标识,赛事Id:{}, 玩法ID:{}", linkId, matchId, market.getMarketCategoryId());
                }
            }else {
            	log.info("::{}::insertMatchMarketOddsOfRedis,不是最新的赔率数据，不缓存，赛事Id：{},玩法ID:{},oldTime:{},dataSourceTime:{}。",
                        linkId,matchId,market.getRelationMarketId(),oldTime,dataSourceTime);
            }
    	}
    }
    
    @Async("ProcessOddsByPandaThreadPool")
    public void deleteMatchMarketOddsByActive(String linkId, Long matchId, List<StandardMarketDataMessage> standardMarketDataMessageList, Long dataSourceTime) {
    	log.info("::{}::deleteMatchMarketOddsByActive 删除被2.0关盘状态修改为开盘的赛事盘口赔率最新更新时间数据。赛事Id:{}",linkId,matchId);
    	if(matchId == null || CollectionUtils.isEmpty(standardMarketDataMessageList)) {
    		return;
    	}
        Set<StandardMarketDataMessage> atvList = standardMarketDataMessageList.stream().filter(e ->
                Constant.SPORT_MARKET.STATUS.ACTIVE.equals(e.getThirdMarketSourceStatus()) && e.getOldThirdMarketSourceStatus() == null).collect(Collectors.toSet());
    	if(CollectionUtils.isEmpty(atvList)) {
    		return;
    	}
    	deleteMatchMarketOddsOfRedis(linkId, matchId, atvList, dataSourceTime);
    }
    
    @Async("ProcessOddsByPandaThreadPool")
    public void deleteMatchMarketOddsOfRedis(String linkId, Long matchId, Set<StandardMarketDataMessage> standardMarketDataMessageList, Long dataSourceTime) {
    	log.info("::{}::deleteMatchMarketOddsOfRedis 删除被2.0关盘状态修改为开盘的赛事盘口赔率最新更新时间数据。赛事Id:{}",linkId,matchId);
    	if(matchId == null || CollectionUtils.isEmpty(standardMarketDataMessageList)) {
    		return;
    	}
    	log.info("::{}::deleteMatchMarketOddsOfRedis 删除被2.0关盘状态修改为开盘的赛事盘口赔率最新更新时间数据。需要清除缓存的盘口集合:{}",linkId,standardMarketDataMessageList);
    	for(StandardMarketDataMessage market:standardMarketDataMessageList) {
    		deleteMarketOddsOfRedis(linkId, matchId, market, dataSourceTime);
    	}
    }
    
    @Async("ProcessOddsByPandaThreadPool")
    public void deleteMatchMarketOddsOfRedisByActive(String linkId, Long matchId, Set<StandardMarketDataMessage> marketActive, Long dataSourceTime) {
    	log.info("::{}::deleteMatchMarketOddsOfRedisByCategory 删除被2.0关盘状态修改为开盘的赛事盘口赔率最新更新时间数据。赛事Id:{}",linkId,matchId);
    	if(matchId == null || CollectionUtils.isEmpty(marketActive)) {
    		return;
    	}
    	log.info("::{}::deleteMatchMarketOddsOfRedisByCategory 删除被2.0关盘状态修改为开盘的赛事盘口赔率最新更新时间数据。需要清除缓存的玩法Id集合:{}",linkId,marketActive);
    	for(StandardMarketDataMessage sm:marketActive) {
    		deleteMarketOddsOfRedis(linkId, matchId, sm, dataSourceTime);
    	}
    }

    @Async("ProcessOddsByPandaThreadPool")
	public void deleteMatchMarketOddsOfRedisByCategory(String linkId, Long matchId, Set<Long> categoryIds,
			Long dataSourceTime) {
		log.info("::{}::deleteMatchMarketOddsOfRedisByCategory 删除被2.0关盘状态修改为开盘的赛事盘口赔率最新更新时间数据。赛事Id:{}",linkId,matchId);
    	if(matchId == null || CollectionUtils.isEmpty(categoryIds)) {
    		return;
    	}
    	log.info("::{}::deleteMatchMarketOddsOfRedisByCategory 删除被2.0关盘状态修改为开盘的赛事盘口赔率最新更新时间数据。需要清除缓存的玩法Id集合:{}",linkId,categoryIds);
    	String key = Constant.REDIS_KEY.RONGHE_MATCH_MARKET_ODDS_UPDATETIME+matchId;
    	Map<String,Long> markets=redisService.hGetAll(key);
    	if(CollectionUtils.isEmpty(markets)) {
    		return;
    	}
    	Set<String> keySet = markets.keySet();
    	String[] mk;
    	for(String marketKey:keySet) {
    		mk = marketKey.split("_");
    		String redisDateKey = Constant.REDIS_KEY.RONGHE_MATCH_MARKET_ODDS_UPDATETIME_DATE + matchId+"_"+mk[1];
            Object oldTime = redisService.get(redisDateKey);
        	if (oldTime != null && ((Long)oldTime) > dataSourceTime)
            {
        		log.info("::{}::deleteMatchMarketOddsOfRedisByCategory, 请求数据已过期，赛事Id：{},盘口ID:{},oldTime:{},dataSourceTime:{}。",
                      linkId,matchId,mk[1],oldTime,dataSourceTime);
        		continue;
            }
    		if(categoryIds.contains(Long.valueOf(mk[0]))) {
    			redisService.hDel(key, marketKey);
    		}
    	}
	}
    
    private void deleteMarketOddsOfRedis(String linkId,Long matchId, StandardMarketDataMessage market, Long dataSourceTime) {
    	String key = Constant.REDIS_KEY.RONGHE_MATCH_MARKET_ODDS_UPDATETIME+matchId;
    	String redisDateKey = Constant.REDIS_KEY.RONGHE_MATCH_MARKET_ODDS_UPDATETIME_DATE + matchId+"_"+market.getRelationMarketId();
        String tagKey = Constant.REDIS_KEY.RONGHE_MATCH_CATEGORY_TAG + matchId;
        Object oldTime = redisService.get(redisDateKey);
        if (oldTime == null || ((Long)oldTime) <= dataSourceTime)
        {
        	Object obj1 = redisService.hGet(key, market.getMarketCategoryId()+"_"+market.getRelationMarketId()+"_"+market.getDataSourceCode()+"_"+Constant.SPORT_MARKET.STATUS.SUSPENDED);
        	if(obj1 != null) {
        		log.info("::{}::deleteMatchMarketOddsOfRedis,清理100s 缓存。key:{}",
                        linkId,market.getMarketCategoryId()+"_"+market.getRelationMarketId()+"_"+market.getDataSourceCode()+"_"+Constant.SPORT_MARKET.STATUS.SUSPENDED);
        		redisService.hDel(key, market.getMarketCategoryId()+"_"+market.getRelationMarketId()+"_"+market.getDataSourceCode()+"_"+Constant.SPORT_MARKET.STATUS.SUSPENDED);
        	}
        	Object obj2 = redisService.hGet(key, market.getMarketCategoryId()+"_"+market.getRelationMarketId()+"_"+market.getDataSourceCode()+"_"+Constant.SPORT_MARKET.STATUS.DEACTIVATED);
        	if(obj2 != null) {
        		log.info("::{}::deleteMatchMarketOddsOfRedis,清理100s 缓存。key:{}",
                        linkId,market.getMarketCategoryId()+"_"+market.getRelationMarketId()+"_"+market.getDataSourceCode()+"_"+Constant.SPORT_MARKET.STATUS.SUSPENDED);
        		redisService.hDel(key, market.getMarketCategoryId()+"_"+market.getRelationMarketId()+"_"+market.getDataSourceCode()+"_"+Constant.SPORT_MARKET.STATUS.DEACTIVATED);
        	}
        	if(DataSourceCodeEnum.TX.code.equals(market.getDataSourceCode()) && !StringUtils.isEmpty(market.getSendData())) {
        		log.info("::{}::deleteMatchMarketOddsOfRedis,清理100s 缓存。sendDatakey:{}",
                        linkId,market.getMarketCategoryId()+"_"+market.getSendData()+"_"+market.getDataSourceCode());
        		redisService.hDel(key, market.getMarketCategoryId()+"_"+market.getSendData()+"_"+market.getDataSourceCode()+"_"+Constant.SPORT_MARKET.STATUS.SUSPENDED);
            	redisService.hDel(key, market.getMarketCategoryId()+"_"+market.getSendData()+"_"+market.getDataSourceCode()+"_"+Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                String redisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + matchId + "_" + market.getDataSourceCode()+"_"+market.getMarketCategoryId());
            	StandardSportMarket standardSportMarket = (StandardSportMarket) redisService.hGet(redisKey,market.getSendData());
            	if(standardSportMarket != null && standardSportMarket.getOldThirdMarketSourceStatus() != null) {
            		log.info("::{}::deleteMatchMarketOddsOfRedis,重置盘口缓存。盘口状态更新为:{},更新之前三方状态:{}",
                            linkId,standardSportMarket.getOldThirdMarketSourceStatus(),standardSportMarket.getThirdMarketSourceStatus());
            		standardSportMarket.setThirdMarketSourceStatus(standardSportMarket.getOldThirdMarketSourceStatus());
            		standardSportMarket.setStatus(standardSportMarket.getOldThirdMarketSourceStatus());
            		redisService.hSet(redisKey,market.getSendData(),standardSportMarket,RedisConfig.REDIS_MY_TIME);
            	}
        	}
        	redisService.set(redisDateKey,dataSourceTime, RedisConfig.REDIS_MY_TIME);
            //清除全场强开标识
            redisService.hDel(tagKey, market.getMarketCategoryId().toString());
        }else {
        	log.info("::{}::deleteMatchMarketOddsOfRedis,不是最新的赔率数据，不缓存，赛事Id：{},玩法ID:{},oldTime:{},dataSourceTime:{}。",
                    linkId,matchId,market.getMarketCategoryId(),oldTime,dataSourceTime);
        }	
    }
}
