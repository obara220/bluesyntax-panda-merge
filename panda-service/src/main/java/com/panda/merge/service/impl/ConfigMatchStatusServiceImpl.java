package com.panda.merge.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.cache.CommonItem;
import com.panda.merge.cache.FootballCacheScores;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.config.ThreadPoolConfig;
import com.panda.merge.constant.MarginCategoryConfig;
import com.panda.merge.dto.ThirdMarketDTO;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.dto.message.StandardMarketMessage;
import com.panda.merge.mapper.ConfigMatchStatusMapper;
import com.panda.merge.model.ConfigMatchStatus;
import com.panda.merge.model.ConfigMatchStatusExample;
import com.panda.merge.model.StandardSportMarket;
import com.panda.merge.service.ConfigMatchStatusService;
import com.panda.merge.service.StandardSportMarketService;
import com.panda.merge.service.ThirdSportMarketOddsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class ConfigMatchStatusServiceImpl implements ConfigMatchStatusService {
    @Autowired
    ConfigMatchStatusMapper configMatchStatusMapper;
    @Autowired
    private RedisService redisService;
    @Autowired
    private ThirdSportMarketOddsService thirdSportMarketOddsService;
    @Autowired
    private ThreadPoolConfig threadPoolConfig;
    @Autowired
    private BaseProcessor baseProcessor;
    @Autowired
    private StandardSportMarketService standardSportMarketService;

    @Override
    public ConfigMatchStatus getItem(String linkId, Long matchId, Integer marketType) {
        ConfigMatchStatusExample configMatchStatusExample = new ConfigMatchStatusExample();
        configMatchStatusExample.createCriteria().andStandardMatchInfoIdEqualTo(matchId).andMarketTypeEqualTo(marketType);
        List<ConfigMatchStatus> list = configMatchStatusMapper.selectByExample(configMatchStatusExample);
        if (CollectionUtils.isEmpty(list))
        {
            return null;
        }
        return list.get(0);
    }

    @Override
    public ConfigMatchStatus create(String linkId, ConfigMatchStatus configMatchStatus) {
        configMatchStatusMapper.insert(configMatchStatus);
        saveRedisData(configMatchStatus.getStandardMatchInfoId());
        return configMatchStatus;
    }

    @Override
    public ConfigMatchStatus update(String linkId, ConfigMatchStatus configMatchStatus) {
        configMatchStatusMapper.updateByPrimaryKeySelective(configMatchStatus);
        if(Constant.CONFIG_MATCH_STATUS.OPEN.equals(configMatchStatus.getStatus())) {
        	saveRedisData(configMatchStatus.getStandardMatchInfoId());
        }else {
        	deleteRedisData(linkId,configMatchStatus.getStandardMatchInfoId());
        }
        return configMatchStatus;
    }

    /**
     * 保存开盘盘口最新投注项赔率到redis
     * @param linkId
     * @param matchId
     * @param marketCategoryId
     * @param standardMarketMessageList
     * @param beginTime
     */
    public void saveMatchMarketLastActiveDeaOddsOfRedis(String linkId,Long matchId, Long marketCategoryId,List<StandardMarketMessage> standardMarketMessageList,Long beginTime,String dataSourceCode) {
    	if(matchId == null || marketCategoryId == null || CollectionUtils.isEmpty(standardMarketMessageList)) {
    		return;
    	}
    	TaskExecutor processTradeSystemThreadPool = threadPoolConfig.getProcessTradeSystemThreadPool();
        processTradeSystemThreadPool.execute(new Runnable() {
            @Override
            public void run() {
            	String lockValue = UUIdUtils.getId()+"_"+linkId;
            	String redisLocKey = Constant.REDIS_KEY.RONGHE_MATCH_MARKET_LOCK + matchId+"_"+marketCategoryId;
                log.info("::{}::processConfigMatchStatus保存开盘盘口最新投注项赔率到redis加锁,redisLocKey:{},准备获取分布式锁,lockValue:{}", linkId,redisLocKey, lockValue);
                boolean isLock = redisService.tryLock(redisLocKey, lockValue, 3, 3);
                log.info("::{}::processConfigMatchStatus保存开盘盘口最新投注项赔率到redis加锁,redisLocKey:{},获取到分布式锁,lockValue:{}", linkId,redisLocKey, lockValue);
                try {
	            	Map<String,StandardMarketMessage> newActive = standardMarketMessageList.stream().
	            			filter(e->Constant.SPORT_MARKET.STATUS.ACTIVE.equals(e.getStatus()) && e.getOldThirdMarketSourceStatus() == null).
	            			collect(Collectors.toMap(e -> e.getId().toString()+"_"+e.getDataSourceCode(), e -> e,(oldValue,newValue)->newValue));
	            	if(!CollectionUtils.isEmpty(newActive)) {
	            		StandardMarketMessage oneData = newActive.values().iterator().next();
	            		//存储开盘数据
		            	String redisLastActiveOddsKey = Constant.REDIS_KEY.RONGHE_MATCH_MARKET_LAST_ACTIVE_ODDS+matchId;
		            	Object activeObj = redisService.hGet(redisLastActiveOddsKey, String.valueOf(marketCategoryId));
		            	log.info("::{}::processConfigMatchStatus开始缓存下发的最新开盘盘口赔率数据，赛事ID：{}，玩法ID：{}，开盘盘口：{}。",linkId,matchId,marketCategoryId,newActive);
		            	if (ObjectUtil.isNotEmpty(activeObj))
		                {
		            		Map<String,StandardMarketMessage> map = (Map<String,StandardMarketMessage>) activeObj;
		            		StandardMarketMessage obj;
		                	for(String k:newActive.keySet()) {
		                		obj = newActive.get(k);
		                		if(!CollectionUtils.isEmpty(obj.getMarketOddsList())) {
		                			map.put(k, newActive.get(k));
		                		}else {
		                			log.info("::{}::盘口没有投注项，不存入缓存，赛事ID：{}，玩法ID：{}，开盘盘口：{}。",linkId,matchId,marketCategoryId,obj);
		                		}
		                	}
		                    redisService.hDel(redisLastActiveOddsKey,String.valueOf(marketCategoryId));
		                    log.info("::{}::processConfigMatchStatus执行存入开盘数据，赛事ID：{}，玩法ID：{}，开盘盘口：{}。",linkId,matchId,marketCategoryId,map);
		                    redisService.hSet(redisLastActiveOddsKey, String.valueOf(marketCategoryId), map, baseProcessor.marketCacheTime(beginTime));
		                }else {
		                	log.info("::{}::processConfigMatchStatus执行存入开盘数据，赛事ID：{}，玩法ID：{}，开盘盘口：{}。",linkId,matchId,marketCategoryId,newActive);
		                	redisService.hSet(redisLastActiveOddsKey, String.valueOf(marketCategoryId), newActive, baseProcessor.marketCacheTime(beginTime));
		                }
	            	}

	            	//存储关盘数据
	            	String redisDeaOddsKey = Constant.REDIS_KEY.RONGHE_MATCH_MARKET_DEA_ODDS+matchId;
	            	Object deaObj = redisService.hGet(redisDeaOddsKey, String.valueOf(marketCategoryId));
	            	Set<String> newDea = standardMarketMessageList.stream().
	            			filter(e->Constant.SPORT_MARKET.STATUS.DEACTIVATED <= e.getStatus()).map(e->e.getId()+"_"+e.getDataSourceCode())
	            			.collect(Collectors.toSet());
	            	Set<String> newsup = standardMarketMessageList.stream().
	            			filter(e->Constant.SPORT_MARKET.STATUS.SUSPENDED.equals(e.getStatus())).map(e->e.getId()+"_"+e.getDataSourceCode())
	            			.collect(Collectors.toSet());
	            	Set<String> oldDeaSet;
	            	if(CollectionUtils.isEmpty(newDea)) {
	            		return;
	            	}
	            	log.info("::{}::processConfigMatchStatus开始缓存关盘盘口赔率数据，赛事ID：{}，玩法ID：{}，关盘盘口：{}。",linkId,matchId,marketCategoryId,newDea);
	            	if (ObjectUtil.isNotEmpty(deaObj))
	                {
	            		oldDeaSet = (Set<String>) deaObj;
	            		if(!CollectionUtils.isEmpty(newActive)) {//删除重新开盘的数据
	            			Set<String> acIds = newActive.values().stream().map(e->e.getId()+"_"+e.getDataSourceCode()).collect(Collectors.toSet());
	            			oldDeaSet = oldDeaSet.stream().filter(e->!acIds.contains(e)).collect(Collectors.toSet());
	            		}
	            		if(!CollectionUtils.isEmpty(newsup)) {//删除重新封盘的数据
	            			oldDeaSet = oldDeaSet.stream().filter(e->!newsup.contains(e)).collect(Collectors.toSet());
	            		}
	            		oldDeaSet.addAll(newDea);
	                    redisService.hDel(redisDeaOddsKey,String.valueOf(marketCategoryId));
	                    log.info("::{}::processConfigMatchStatus执行存入关盘数据，赛事ID：{}，玩法ID：{}，关盘盘口：{}。",linkId,matchId,marketCategoryId,oldDeaSet);
	                    redisService.hSet(redisDeaOddsKey, String.valueOf(marketCategoryId), oldDeaSet, baseProcessor.marketCacheTime(beginTime));
	                }else {
	                	log.info("::{}::processConfigMatchStatus执行存入关盘数据，赛事ID：{}，玩法ID：{}，关盘盘口：{}。",linkId,matchId,marketCategoryId,newDea);
	                	redisService.hSet(redisDeaOddsKey, String.valueOf(marketCategoryId), newDea, baseProcessor.marketCacheTime(beginTime));
	                }
                }finally {
                	if (isLock)
                    {
                        redisService.unLock(redisLocKey,lockValue);
                        log.info("::{}::processConfigMatchStatus保存开盘盘口最新投注项赔率到redis加锁,redisLocKey:{},释放分布式锁,lockValue:{}", linkId,redisLocKey, lockValue);
                    }
				}
            }
        });
    }

    private void deleteDeaMarketOfRedis(String linkId,Long matchId, Set<Long> marketCategoryIds,Set<StandardMarketDataMessage> markets, Long dataSourceTime) {
    	if(CollectionUtils.isEmpty(marketCategoryIds) || CollectionUtils.isEmpty(markets)) {
    		return ;
    	}
    	for(Long marketCategoryId:marketCategoryIds) {
    		String redisDateKey = Constant.REDIS_KEY.RONGHE_MATCH_MARKET_DEA_ODDS_DATE + matchId+"_"+marketCategoryId;
            Object oldTime = redisService.get(redisDateKey);
            if (oldTime != null && ((Long)oldTime) > dataSourceTime)
            {
            	log.info("::{}::processConfigMatchStatus,不是最新的赔率数据，不清理历史关闭数据Id：{},玩法ID:{},oldTime:{},dataSourceTime:{}。",
                        linkId,matchId,marketCategoryId,oldTime,dataSourceTime);
            	continue;
            }
	    	//存储关盘数据
	    	String redisDeaOddsKey = Constant.REDIS_KEY.RONGHE_MATCH_MARKET_DEA_ODDS+matchId;
	    	Object deaObj = redisService.hGet(redisDeaOddsKey, String.valueOf(marketCategoryId));
	    	if(deaObj == null) {
	    		return;
	    	}
	    	String lockValue = UUIdUtils.getId()+"_"+linkId;
	    	String redisLocKey = Constant.REDIS_KEY.RONGHE_MATCH_MARKET_LOCK + matchId+"_"+marketCategoryId;
	        log.info("::{}::processConfigMatchStatus清除玩法关盘数据redis加锁,redisLocKey:{},准备获取分布式锁,lockValue:{}", linkId,redisLocKey, lockValue);
	        boolean isLock = redisService.tryLock(redisLocKey, lockValue, 2, 2);
	        log.info("::{}::processConfigMatchStatus清除玩法关盘数据redis加锁,redisLocKey:{},获取到分布式锁,lockValue:{}", linkId,redisLocKey, lockValue);
	        try {
	        	Set<String> deaMarket = (Set<String>) deaObj;
	        	markets.forEach(e->{
	        		String key = e.getRelationMarketId()+"_"+e.getDataSourceCode();
	        		if(deaMarket.contains(key)) {
	        			log.info("::{}::processConfigMatchStatus 关盘盘口重新下发开盘，清理缓存,lockValue:{}", linkId,key);
	        			deaMarket.remove(key);
	        		}
	        		if(DataSourceCodeEnum.TX.code.equals(e.getDataSourceCode()) && !StringUtils.isEmpty(e.getSendData())){
						String keyTx = e.getSendData()+"_"+e.getDataSourceCode();
						if(deaMarket.contains(keyTx)) {
							log.info("::{}::processConfigMatchStatus 关盘盘口重新下发开盘，清理缓存,lockValue:{}", linkId,keyTx);
							deaMarket.remove(keyTx);
						}
					}
	        	});
	        	//清理历史关盘数据
	            redisService.hSet(redisDeaOddsKey,String.valueOf(marketCategoryId),deaMarket);
	            redisService.set(redisDateKey,dataSourceTime, RedisConfig.REDIS_MY_TIME);
	        }finally {
	        	if (isLock)
	            {
	                redisService.unLock(redisLocKey,lockValue);
	                log.info("::{}::processConfigMatchStatus清除玩法关盘数据redis加锁,redisLocKey:{},释放分布式锁,lockValue:{}", linkId,redisLocKey, lockValue);
	            }
			}
    	}
    	log.info("::{}::processConfigMatchStatus清除玩法关盘数据，赛事ID：{}，盘口集合：{}。",linkId,matchId,markets);
    }

    /**
     * 缓存2.0 配置信息
     * @param matchId
     */
    private void saveRedisData(Long matchId) {
    	String key = Constant.REDIS_KEY.RONGHE_CONFIG_MATCH_STATUS_DATA;
    	Object obj = redisService.get(key);
    	if(obj == null) {
    		obj = new HashSet<Long>();
    	}
    	Set<Long> set = (Set<Long>) obj;
    	if(set.contains(matchId)) {
    		return;
    	}
    	set.add(matchId);
    	redisService.del(key);
    	redisService.set(key, set);
    }

    /**
     * 清除缓存2.0 配置信息
     */
    public void deleteRedisData(String linkId,Long matchId) {
    	String key = Constant.REDIS_KEY.RONGHE_CONFIG_MATCH_STATUS_DATA;
    	Object obj = redisService.get(key);
    	if(obj == null) {
    		return;
    	}
    	Set<Long> set = (Set<Long>) obj;
    	if(!set.contains(matchId)) {
    		return;
    	}
    	log.info("::{}::deleteRedisData操盘2.0 缓存2.0 配置信息,赛事id:{},盘口信息:{}", linkId,  matchId);
    	set.remove(matchId);
    	redisService.del(key);
    	redisService.set(key, set);
    }

    /**
     * 足球增加开盘时间-封、关盘/接拒
     */
	@Override
    public void processConfigMatchStatus(String linkId, Map<String, StandardMarketDataMessage> standardMarketMessageMap,
                                         String dataSourceCode, Long matchId, Long sportId, Integer marketType, Long beginTime, Long dataSourceTime, Set<Long> marketCategoryIdSet, Long matchPeriodId) {
		if (CollectionUtils.isEmpty(marketCategoryIdSet)) {
			return;
		}
		Set<Long> newMarketCategoryId = new HashSet<>();
		MarginCategoryConfig.NO_CLOS_CATEGORY.forEach(c -> {
			if (marketCategoryIdSet.contains(c)) {
				newMarketCategoryId.add(c);
			}
		});
		if (CollectionUtils.isEmpty(newMarketCategoryId)) {
			return;
		}
		log.info("::{}::processConfigMatchStatus操盘2.0忽略第三方关盘状态,赛种ID:{},赛事id:{},玩法集合:{}", linkId, sportId, matchId, newMarketCategoryId);
		ConfigMatchStatusExample configMatchStatusExample = new ConfigMatchStatusExample();
		configMatchStatusExample.createCriteria().andStandardMatchInfoIdEqualTo(matchId).
				andStatusEqualTo(Constant.CONFIG_MATCH_STATUS.OPEN).andMarketTypeEqualTo(marketType);
		List<ConfigMatchStatus> list = configMatchStatusMapper.selectByExample(configMatchStatusExample);
		if (CollectionUtils.isEmpty(list) || CollectionUtils.isEmpty(standardMarketMessageMap)) {
			log.info("::{}::processConfigMatchStatus 配置或盘口为空 或 赛种不支持 或 数据源不支持. 赛种:{},盘口类别:{},数据源:{},配置信息:{}", linkId, sportId, marketType, dataSourceCode, list);
			//删除RONGHE_CONFIG_MATCH_STATUS_DATA reids中的 matchId
			deleteRedisData(linkId, matchId);
			return;
		}
		//解决关盘数据后发先制，导致100s关盘数据被清理
		String match1852Lock = Constant.REDIS_KEY.RONGHE_MATCH_1852_TIMESTAMP + matchId;
		Long lockTime = (Long) redisService.get(match1852Lock);
		if (lockTime != null && lockTime > dataSourceTime) {
			log.info("::{}::processConfigMatchStatus 时间戳校验不通过。不予处理逻辑. 赛事ID:{},数据源:{},历史时间:{},当前数据处理时间:{}", linkId, matchId, dataSourceCode, lockTime, dataSourceTime);
			return;
		}
		redisService.set(match1852Lock, dataSourceTime);

		ConfigMatchStatus c = list.get(0);
		//过滤历史已关盘的盘口数据
		List<StandardMarketDataMessage> markets = standardMarketMessageMap.values().stream().
				filter(map -> newMarketCategoryId.contains(map.getMarketCategoryId()) && map.getModifyTime() > c.getModifyTime()).collect(Collectors.toList());
		if (CollectionUtils.isEmpty(markets)) {
			log.info("::{}::processConfigMatchStatus操盘2.0忽略第三方关盘状态, 当前玩法盘口没有符合的数据. 赛种:{},配置信息:{}", linkId, sportId, list);
			return;
		}
		log.info("::{}::processConfigMatchStatus操盘2.0忽略第三方关盘状态, 处理的盘口信息:{}", linkId, markets);
		Set<Long> categoryActive = new HashSet<Long>();
		Set<StandardMarketDataMessage> marketActive = new HashSet<StandardMarketDataMessage>();
		Set<Long> categorySuspended = new HashSet<Long>();
		Map<Long, List<StandardMarketDataMessage>> categoryOddsMaps = new HashMap<Long, List<StandardMarketDataMessage>>();
		List<StandardMarketDataMessage> l;
		Set<Long> logActive = new HashSet<Long>();
		for (StandardMarketDataMessage s : markets) {
			if (categoryOddsMaps.containsKey(s.getMarketCategoryId())) {
				categoryOddsMaps.get(s.getMarketCategoryId()).add(s);
			} else {
				l = new ArrayList<StandardMarketDataMessage>();
				l.add(s);
				categoryOddsMaps.put(s.getMarketCategoryId(), l);
			}
			if (Constant.SPORT_MARKET.STATUS.ACTIVE.equals(s.getThirdMarketSourceStatus()) &&
					s.getOldThirdMarketSourceStatus() == null) {
				log.info("::{}::processConfigMatchStatus操盘2.0忽略第三方关盘状态, 存在开盘玩法:{}, 开盘盘口ID:{}", linkId, s.getMarketCategoryId(), s.getRelationMarketId() + "_" + s.getThirdMarketSourceId());
				categoryActive.add(s.getMarketCategoryId());
				logActive.add(s.getRelationMarketId());
				marketActive.add(s);
			} else if (Constant.SPORT_MARKET.STATUS.ACTIVE.equals(s.getThirdMarketSourceStatus()) &&
					s.getOldThirdMarketSourceStatus() != null) {
				s.setThirdMarketSourceStatus(s.getOldThirdMarketSourceStatus());
			} else if (Constant.SPORT_MARKET.STATUS.SUSPENDED.equals(s.getThirdMarketSourceStatus()) &&
					!categorySuspended.contains(s.getMarketCategoryId())) {
				categorySuspended.add(s.getMarketCategoryId());
			}
		}

		//重新开盘的盘口数据，清除掉100s 盘口记时
		thirdSportMarketOddsService.deleteMatchMarketOddsByActive(linkId, matchId, markets, dataSourceTime);
		//重新开盘的盘口数据，清除掉历史关盘记录
		deleteDeaMarketOfRedis(linkId, matchId, categoryActive, marketActive, dataSourceTime);
		//获取历史关盘数据
		String redisKey = Constant.REDIS_KEY.RONGHE_MATCH_MARKET_DEA_ODDS + matchId;
		Map<String, Set<String>> deaMarket = redisService.hGetAll(redisKey);
		List<StandardMarketDataMessage> insertUpdateTime = new ArrayList<StandardMarketDataMessage>();
		Set<String> newDeaMarket;
		Object autoClos;
		//不存在开盘盘口的玩法
		Set<Long> categorys = new HashSet<Long>();
		categoryOddsMaps.keySet().forEach(e -> {
			if (!categoryActive.contains(e)) {
				categorys.add(e);
			}
		});
		log.info("::{}::processConfigMatchStatus操盘2.0忽略第三方关盘状态, 盘口总数据内容:{}", linkId, categorys);
		if (CollectionUtils.isEmpty(categorys)) {
			return;
		}
		for (Long categoryId : categorys) {
			List<StandardMarketDataMessage> sd = categoryOddsMaps.get(categoryId);
			//判断出所有盘口状态为封盘的玩法，执行操盘2.0操作
			List<StandardMarketDataMessage> sdf = sd.stream().filter(x -> Constant.SPORT_MARKET.STATUS.SUSPENDED.equals(x.getThirdMarketSourceStatus()) || Constant.SPORT_MARKET.STATUS.DEACTIVATED.equals(x.getThirdMarketSourceStatus())).collect(Collectors.toList());
			if (!CollectionUtils.isEmpty(sdf)) {
                log.info("::{}::processConfigMatchStatus操盘2.0忽略第三方关盘状态,阶段:{} 执行操作数据:{}", linkId, matchPeriodId, sdf);
				Set<String> deaSet = deaMarket.get(categoryId.toString());
				newDeaMarket = new HashSet<String>();
				//执行操盘2.0操作，当玩法下的盘口全部为封盘时，将盘口开启。
				for (StandardMarketDataMessage e : sdf) {
					//因关封规则 或 之前已关盘，当前盘口跳过拒关/封规则
					if (Constant.SPORT_MARKET.STATUS.DEACTIVATED <= e.getThirdMarketSourceStatus() && (categorySuspended.contains(categoryId)
							|| (!CollectionUtils.isEmpty(deaSet) && (deaSet.contains(e.getRelationMarketId() + "_" + e.getDataSourceCode()))))) {
						newDeaMarket.add(e.getRelationMarketId() + "_" + e.getDataSourceCode());
						log.info("::{}::processConfigMatchStatus 因关封规则 或 之前已关盘，当前盘口跳过拒关/封规则。玩法Id:{},盘口Id:{},历史关盘状态:{}", linkId, categoryId, e.getRelationMarketId() + "_" + e.getThirdMarketSourceId(), e.getStatus());
						continue;
					}
					if (Constant.SPORT_MARKET.STATUS.DEACTIVATED <= e.getThirdMarketSourceStatus() && (categorySuspended.contains(categoryId)
							|| (!CollectionUtils.isEmpty(deaSet) && DataSourceCodeEnum.TX.code.equals(e.getDataSourceCode()) && deaSet.contains(e.getSendData() + "_" + e.getDataSourceCode())))) {
						newDeaMarket.add(e.getRelationMarketId() + "_" + e.getDataSourceCode());
						log.info("::{}::processConfigMatchStatus 因关封规则 或 之前已关盘，当前盘口跳过拒关/封规则。玩法Id:{},盘口Id:{},历史关盘状态1:{}", linkId, categoryId, e.getRelationMarketId() + "_" + e.getThirdMarketSourceId(), e.getStatus());
						continue;
					}
					//校验比分兜底
					if (scoreValidate(linkId, e, matchId, categoryId)) {
						e.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
						newDeaMarket.add(e.getRelationMarketId() + "_" + e.getDataSourceCode());
						continue;
					}
					//上半场44分钟后，全场89分钟后，不强转
					if(eventTimeCheck(linkId,matchId,e.getMarketCategoryId())){
						e.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
						newDeaMarket.add(e.getRelationMarketId() + "_" + e.getDataSourceCode());
						continue;
					}
                    if (periodCheck(linkId, matchId, e.getMarketCategoryId(), matchPeriodId)) {
                        Integer status = e.getThirdMarketSourceStatus().equals(Constant.SPORT_MARKET.STATUS.ACTIVE) ? Constant.SPORT_MARKET.STATUS.DEACTIVATED : e.getThirdMarketSourceStatus();
						e.setThirdMarketSourceStatus(status);
						newDeaMarket.add(e.getRelationMarketId() + "_" + e.getDataSourceCode());
						continue;
					}
					e.setOldThirdMarketSourceStatus(e.getThirdMarketSourceStatus());
					e.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.ACTIVE);
					log.info("::{}::processConfigMatchStatus 执行操作数据,强转开盘盘口:{},", linkId, e);

					String key = Constant.REDIS_KEY.RONGHE_MATCH_MARKET_ODDS_UPDATETIME + matchId;
					autoClos = redisService.hGet(key, e.getMarketCategoryId() + "_" + e.getRelationMarketId() + "_" + e.getDataSourceCode() + "_" + e.getOldThirdMarketSourceStatus());
					//重置100s计时
					if (e.getOldThirdMarketSourceStatus() != null && Constant.SPORT_MARKET.STATUS.SUSPENDED <= e.getOldThirdMarketSourceStatus() &&
							autoClos == null) {
						//将markets 中的盘口 添加到 reids RONGHE_MATCH_MARKET_ODDS_UPDATETIME key中 并且更新时间  100s自动下发关盘
						log.info("::{}::processConfigMatchStatus 把规则调整过的盘口，加入100s计时器。盘口:{}", linkId, e);
						insertUpdateTime.add(e);
					}
				}
				//记录历史关盘数据
				if (!CollectionUtils.isEmpty(newDeaMarket)) {
					saveDeaMarketOfRedis(linkId, matchId, categoryId, newDeaMarket, beginTime);
				}
			}
		}
		if (!CollectionUtils.isEmpty(insertUpdateTime)) {
			thirdSportMarketOddsService.insertMatchMarketOddsOfRedis(linkId, matchId, insertUpdateTime, beginTime, dataSourceTime);
		}
	}

	@Override
	public void saveDeaMarketOfRedis(String linkId, Long matchId, Set<StandardMarketDataMessage> newDea, Long beginTime) {
		if(matchId == null || CollectionUtils.isEmpty(newDea)) {
			return;
		}
		String redisDeaOddsKey = Constant.REDIS_KEY.RONGHE_MATCH_MARKET_DEA_ODDS+matchId;
		Set<String> oldDeaSet;
		//存储关盘数据
		for(StandardMarketDataMessage sm:newDea) {
			Object deaObj = redisService.hGet(redisDeaOddsKey, String.valueOf(sm.getMarketCategoryId()));
			if(CollectionUtils.isEmpty(newDea)) {
	    		return;
	    	}
			log.info("::{}::processConfigMatchStatus开始缓存关盘盘口赔率数据，赛事ID：{}，玩法ID：{}，关盘盘口：{}。",linkId,matchId,sm.getMarketCategoryId(),newDea);
	    	if (ObjectUtil.isNotEmpty(deaObj))
	        {
	    		oldDeaSet = (Set<String>) deaObj;
	    		oldDeaSet.add(sm.getRelationMarketId()+"_"+sm.getDataSourceCode());
	            log.info("::{}::processConfigMatchStatus执行存入关盘数据，赛事ID：{}，玩法ID：{}，关盘盘口：{}。",linkId,matchId,sm.getMarketCategoryId(),oldDeaSet);
	            redisService.hSet(redisDeaOddsKey, String.valueOf(sm.getMarketCategoryId()), oldDeaSet, baseProcessor.marketCacheTime(beginTime));
	        }else {
	        	log.info("::{}::processConfigMatchStatus执行存入关盘数据，赛事ID：{}，玩法ID：{}，关盘盘口：{}。",linkId,matchId,sm.getMarketCategoryId(),newDea);
	        	oldDeaSet = new HashSet<String>();
	        	oldDeaSet.add(sm.getRelationMarketId()+"_"+sm.getDataSourceCode());
	        	redisService.hSet(redisDeaOddsKey, String.valueOf(sm.getMarketCategoryId()), oldDeaSet, baseProcessor.marketCacheTime(beginTime));
	        }
	    	if(DataSourceCodeEnum.TX.code.equals(sm.getDataSourceCode()) && !StringUtils.isEmpty(sm.getSendData())) {
	    		oldDeaSet.add(sm.getSendData()+"_"+sm.getDataSourceCode());
	        	redisService.hSet(redisDeaOddsKey, String.valueOf(sm.getMarketCategoryId()), oldDeaSet, baseProcessor.marketCacheTime(beginTime));
			}
		}
	}

	private void saveDeaMarketOfRedis(String linkId,Long matchId, Long marketCategoryId,Set<String> newDea,Long beginTime) {
    	//存储关盘数据
    	String redisDeaOddsKey = Constant.REDIS_KEY.RONGHE_MATCH_MARKET_DEA_ODDS+matchId;
    	Object deaObj = redisService.hGet(redisDeaOddsKey, String.valueOf(marketCategoryId));
    	Set<String> oldDeaSet;
    	if(CollectionUtils.isEmpty(newDea)) {
    		return;
    	}
    	log.info("::{}::processConfigMatchStatus开始缓存关盘盘口赔率数据，赛事ID：{}，玩法ID：{}，关盘盘口：{}。",linkId,matchId,marketCategoryId,newDea);
    	if (ObjectUtil.isNotEmpty(deaObj))
        {
    		oldDeaSet = (Set<String>) deaObj;
    		oldDeaSet.addAll(newDea);
            redisService.hDel(redisDeaOddsKey,String.valueOf(marketCategoryId));
            log.info("::{}::processConfigMatchStatus执行存入关盘数据，赛事ID：{}，玩法ID：{}，关盘盘口：{}。",linkId,matchId,marketCategoryId,oldDeaSet);
            redisService.hSet(redisDeaOddsKey, String.valueOf(marketCategoryId), oldDeaSet, baseProcessor.marketCacheTime(beginTime));
        }else {
        	log.info("::{}::processConfigMatchStatus执行存入关盘数据，赛事ID：{}，玩法ID：{}，关盘盘口：{}。",linkId,matchId,marketCategoryId,newDea);
        	redisService.hSet(redisDeaOddsKey, String.valueOf(marketCategoryId), newDea, baseProcessor.marketCacheTime(beginTime));
        }
    }

	private boolean scoreValidate(String linkId, StandardMarketDataMessage market, Long matchId, Long categoryId) {
		try {
			log.info("::{}::processConfigMatchStatus 比分兜底校验。赛事ID:{},玩法ID:{},盘口信息:{}", linkId,matchId,categoryId,market);
			Object scoreObj = redisService.get(DigestUtil.md5Hex(Constant.REDIS_KEY.STANDARD_MATCH_SCORES + matchId));
			FootballCacheScores scores;
			if (Objects.isNull(scoreObj)) {
				return false;
			}
			scores = JSONObject.parseObject(scoreObj.toString(), FootballCacheScores.class);
	        log.info("::{}::processConfigMatchStatus 比分兜底校验。比分信息:{},盘口信息:{}", linkId,scoreObj,market);
            //盘口值验证
            if (MarginCategoryConfig.SCORE_CHECK_GOAL.contains(categoryId)) {
                if (scores.getGoal() == null) {
                    return false;
                }
                return category2Validate(linkId, market, scores.getGoal());
            } else if (MarginCategoryConfig.SCORE_CHECK_CORNER.contains(categoryId)) {
                if (scores.getCorner() == null) {
                    return false;
                }
                return category2Validate(linkId, market, scores.getCorner());
            } else if (MarginCategoryConfig.SCORE_CHECK_FACARD.contains(categoryId)) {
                if (scores.getRedCard() == null && scores.getYellowCard() == null) {
                    return false;
                }
                if (scores.getRedCard() == null) {
                    scores.setRedCard(new CommonItem(0, 0));
                }
                if (scores.getYellowCard() == null) {
                    scores.setYellowCard(new CommonItem(0, 0));
                }
                Integer home = scores.getRedCard().getHome() * 2 + scores.getYellowCard().getHome();
                Integer away = scores.getRedCard().getAway() * 2 + scores.getYellowCard().getAway();
                scores.setFaCard(new CommonItem(home, away));
                return category2Validate(linkId, market, scores.getFaCard());
            }
            //比分验证
            if (MarginCategoryConfig.HANDICAP_VALUE_CHECK_GOAL.contains(categoryId)) {
                if (scores.getGoal() == null) {
                    return false;
                }
                return category4Validate(linkId, market, scores.getGoal());
            } else if (MarginCategoryConfig.HANDICAP_VALUE_CHECK_CORNER.contains(categoryId)) {
                if (scores.getCorner() == null) {
                    return false;
                }
                return category4Validate(linkId, market, scores.getCorner());
            } else if (MarginCategoryConfig.HANDICAP_VALUE_CHECK_FACARD.contains(categoryId)) {
                if (scores.getRedCard() == null && scores.getYellowCard() == null) {
                    return false;
                }
                if (scores.getRedCard() == null) {
                    scores.setRedCard(new CommonItem(0, 0));
                }
                if (scores.getYellowCard() == null) {
                    scores.setYellowCard(new CommonItem(0, 0));
                }
                Integer home = scores.getRedCard().getHome() * 2 + scores.getYellowCard().getHome();
                Integer away = scores.getRedCard().getAway() * 2 + scores.getYellowCard().getAway();
                scores.setFaCard(new CommonItem(home, away));
                return category4Validate(linkId, market, scores.getFaCard());
            }
		}catch(Exception e) {
			log.info("::{}::processConfigMatchStatus 比分兜底校验,盘口ID:{}, error:{}",linkId,market.getRelationMarketId(),e);
			return false;
		}
		return false;
	}

	private boolean category2Validate(String linkId, StandardMarketDataMessage market, CommonItem goal) {
		double a1 = Double.valueOf(market.getAddition1()).doubleValue();
		double goalTotal = Double.valueOf(goal.getHome()+goal.getAway()).doubleValue() + 0.25;
		log.info("::{}::processConfigMatchStatus 比分兜底校验。条件Addition1信息:{}, 比分+25:{}", linkId,a1,goalTotal);
		return a1 < goalTotal;
	}

	private boolean category4Validate(String linkId, StandardMarketDataMessage market, CommonItem goal) {
		Integer cornerHome = goal.getHome();
        Integer cornerAway = goal.getAway();
        String marketHome = market.getAddition3();
        String marketAway = market.getAddition4();
		log.info("::{}::processConfigMatchStatus 基准分兜底校验。条件Addition3信息:{}, 条件Addition4信息:{},比分home:{},比分Away:{}", linkId,marketHome,marketAway,cornerHome,cornerAway);
		if(cornerHome != null && marketHome != null && !marketHome.equals(cornerHome.toString())) {
			log.info("::{}::processConfigMatchStatus 基准分兜底校验。主队比分不匹配。条件Addition3信息:{}, 比分home:{}", linkId,marketHome,cornerHome);
			return true;
		}
		if(cornerAway != null && marketAway != null && !marketAway.equals(cornerAway.toString())) {
			log.info("::{}::processConfigMatchStatus 基准分兜底校验。客队比分不匹配。条件Addition3信息:{}, 比分home:{}", linkId,marketAway,cornerAway);
			return true;
		}
		return false;
	}

	/**
	 * TX盘口消费 并发 丢失关盘赔率数据兜底
	 * @param thirdMarketDTO
	 * @param matchId
	 * @param dataSourceCode
	 * @param beginTime
	 */
	@Async("ProcessOddsByPandaThreadPool")
	public void processTXTimestamps(String linkId,ThirdMarketDTO thirdMarketDTO,Long matchId,String dataSourceCode, Long beginTime) {
		if(thirdMarketDTO == null || thirdMarketDTO.getStatus() < Constant.SPORT_MARKET.STATUS.DEACTIVATED) {
			return;
		}
		log.info("::{}::processConfigMatchStatus TX盘口消费 并发 丢失关盘赔率数据兜底。数据源:{},赛事Id:{},三方盘口数据:{}", linkId,dataSourceCode,matchId,thirdMarketDTO);
		String redisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + matchId + "_" + dataSourceCode+"_"+thirdMarketDTO.getMarketCategoryId());
        StandardSportMarket standardSportMarket = standardSportMarketService.getItem(thirdMarketDTO.getDataSourceCode(), thirdMarketDTO.getThirdMarketSourceId(), matchId);
        if(standardSportMarket == null || standardSportMarket.getRelationMarketId() == null) {
        	log.info("::{}::processConfigMatchStatus 没有标准盘口数据。赛事Id:{},三方盘口Id:{}", linkId,matchId,thirdMarketDTO.getThirdMarketSourceId());
        	return;
        }
        StandardMarketDataMessage standardMarketMessage = (StandardMarketDataMessage) redisService.hGet(redisKey,standardSportMarket.getRelationMarketId().toString());
		if(standardMarketMessage == null || standardMarketMessage.getStatus() >= Constant.SPORT_MARKET.STATUS.DEACTIVATED) {
			log.info("::{}::processConfigMatchStatus 缓存没有标准盘口数据。赛事Id:{},标准盘口Id:{},标准盘口数据:{}", linkId,matchId,standardSportMarket.getRelationMarketId().toString(), JSONObject.toJSONString(standardMarketMessage));
			return;
		}
		standardMarketMessage.setStatus(thirdMarketDTO.getStatus());
		standardMarketMessage.setThirdMarketSourceStatus(thirdMarketDTO.getStatus());
//		standardMarketMessageMap.setOldThirdMarketSourceStatus(null);
		redisService.hSet(redisKey,standardSportMarket.getRelationMarketId().toString(),standardMarketMessage,baseProcessor.marketCacheTime(beginTime));
		StandardMarketDataMessage standardMarketMessage1 = (StandardMarketDataMessage) redisService.hGet(redisKey,standardSportMarket.getSendData());
		if(standardMarketMessage1 != null) {
			standardMarketMessage1.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
			standardMarketMessage1.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
			redisService.hSet(redisKey,standardSportMarket.getSendData(),standardMarketMessage1,baseProcessor.marketCacheTime(beginTime));
		}
		//获取历史关盘数据
		String deaRedisKey = Constant.REDIS_KEY.RONGHE_MATCH_MARKET_DEA_ODDS + matchId;
		Set<String> deaMarket = (Set<String>) redisService.hGet(deaRedisKey,standardMarketMessage.getMarketCategoryId().toString());
		if(deaMarket != null && deaMarket.contains(standardMarketMessage.getSendData()+"_"+standardMarketMessage.getDataSourceCode())) {
			log.info("::{}::processConfigMatchStatus 历史关闭盘口数据中，已存在此盘口。赛事Id:{},标准盘口Id:{}", linkId,matchId,standardSportMarket.getRelationMarketId().toString());
			return;
		}
		if (null == deaMarket) {
			deaMarket = new HashSet<>();
		}
		deaMarket.add(standardMarketMessage.getSendData()+"_"+standardMarketMessage.getDataSourceCode());
		redisService.hSet(deaRedisKey,standardMarketMessage.getMarketCategoryId().toString(),deaMarket,baseProcessor.marketCacheTime(beginTime));
	}

	/**
	 * 存在重新开盘盘口数据的历史数据清理
	 * @param categoryActive
	 * @param categoryOddsMaps
	 */
	private void againOpenMarketClean(String linkId,Long matchId, Set<Long> categoryActive,Map<Long, List<StandardMarketDataMessage>> categoryOddsMaps,Long beginTime) {
		List<StandardMarketDataMessage> list;
		Set<String> newDea;
		for(Long categoryId:categoryActive) {
			list = categoryOddsMaps.get(categoryId);
			if(CollectionUtils.isEmpty(list)) {
				continue;
			}
			newDea = new HashSet<String>();
			for(StandardMarketDataMessage e:list) {
				if(e.getOldThirdMarketSourceStatus() != null && e.getThirdMarketSourceStatus() <= Constant.SPORT_MARKET.STATUS.SUSPENDED) {
					log.info("::{}::processConfigMatchStatus 盘口存在开盘数据，历史数据调整为关盘。标准盘口Id:{},标准盘口状态:{}", linkId,e.getRelationMarketId().toString(),e.getThirdMarketSourceStatus());
					e.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
//					e.setOldThirdMarketSourceStatus(null);
					newDea.add(e.getRelationMarketId()+"_"+e.getDataSourceCode());
				}
				if(e.getThirdMarketSourceStatus() >= Constant.SPORT_MARKET.STATUS.DEACTIVATED ||
						(e.getOldThirdMarketSourceStatus() != null &&
						e.getOldThirdMarketSourceStatus() >= Constant.SPORT_MARKET.STATUS.DEACTIVATED)) {
					log.info("::{}::processConfigMatchStatus 盘口存在开盘数据，历史数据调整为关盘。标准盘口Id:{},标准盘口状态:{}", linkId,e.getRelationMarketId().toString(),e.getThirdMarketSourceStatus());
					newDea.add(e.getRelationMarketId()+"_"+e.getDataSourceCode());
				}
			};
			if(!CollectionUtils.isEmpty(newDea)) {
				saveDeaMarketOfRedis(linkId, matchId, categoryId, newDea, beginTime);
			}
		}
	}


	private void txNoCloseMarket(String linkId, String dataSourceCode,List <StandardMarketDataMessage> markets, Long beginTime) {
		if(!DataSourceCodeEnum.TX.code.equals(dataSourceCode)) {
			return;
		}
		List <StandardMarketDataMessage> marketsTX = markets.stream().
			filter(e->e.getThirdMarketSourceStatus() <= Constant.SPORT_MARKET.STATUS.SUSPENDED).collect(Collectors.toList());
		Map<String,StandardMarketDataMessage> map = new HashMap<String,StandardMarketDataMessage>();
		if(CollectionUtils.isEmpty(marketsTX)) {
			return;
		}
		log.info("::{}::processConfigMatchStatus Tx未关盘的盘口数据。未关盘的盘口数据集合:{}", linkId,marketsTX);
		StandardMarketDataMessage sm;
		Set<String> newDea;
		for(StandardMarketDataMessage s : marketsTX) {
			if(s.getMarketCategoryId() == null || s.getPlaceNum() == null) {
				continue;
			}
			sm = map.get(s.getMarketCategoryId()+"_"+s.getPlaceNum());
			if(sm == null) {
				map.put(s.getMarketCategoryId()+"_"+s.getPlaceNum(), s);
			}else if(sm != null && s.getModifyTime() > sm.getModifyTime()) {
				log.info("::{}::processConfigMatchStatus Tx数据历史盘口处理为关盘。标准盘口Id:{},处理前盘口状态:{}", linkId,sm.getRelationMarketId(),sm.getThirdMarketSourceStatus());
				sm.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
				map.put(s.getMarketCategoryId()+"_"+s.getPlaceNum(), s);
				newDea = new HashSet<String>();
				newDea.add(sm.getRelationMarketId()+"_"+sm.getDataSourceCode());
				saveDeaMarketOfRedis(linkId, sm.getStandardMatchInfoId(),sm.getMarketCategoryId(), newDea, beginTime);
			}else if(sm != null && s.getModifyTime() < sm.getModifyTime()){
				log.info("::{}::processConfigMatchStatus Tx数据历史盘口处理为关盘。标准盘口Id:{},处理前盘口状态:{}", linkId,s.getRelationMarketId(),s.getThirdMarketSourceStatus());
				s.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
				newDea = new HashSet<String>();
				newDea.add(s.getRelationMarketId()+"_"+s.getDataSourceCode());
				saveDeaMarketOfRedis(linkId, s.getStandardMatchInfoId(),s.getMarketCategoryId(), newDea, beginTime);
			}
		}

		List <StandardMarketDataMessage> deaMarketsTX = markets.stream().
				filter(e->e.getThirdMarketSourceStatus() >= Constant.SPORT_MARKET.STATUS.DEACTIVATED).collect(Collectors.toList());
		for(StandardMarketDataMessage ds : deaMarketsTX) {
			newDea = new HashSet<String>();
			newDea.add(ds.getRelationMarketId()+"_"+ds.getDataSourceCode());
			saveDeaMarketOfRedis(linkId, ds.getStandardMatchInfoId(), ds.getMarketCategoryId(), newDea, beginTime);
			log.info("::{}::processConfigMatchStatus 全量刷新历史关盘数据。标准盘口Id:{}", linkId,ds.getRelationMarketId());
		}

	}

	/**
	 * 时间阶段时间校验
	 *上半场玩法 44分钟后 停止强开逻辑 全场玩法 44分钟后 停止强开逻辑
	 * @param standardMatchId
	 * @param marketCategoryId
	 * @return TRUE:终止强转逻辑，FALSE:继续强转
	 */
	private Boolean eventTimeCheck(String linkId, Long standardMatchId, Long marketCategoryId) {
		String cacheStatusKey = "STANDARD_MATCH_STATUS_2070:" + standardMatchId;
		Map<String, Long> matchPeriodId2Time = redisService.hGetAll(cacheStatusKey);
		if (null == matchPeriodId2Time) {
			return Boolean.FALSE;
		}
		Long periodId = MarginCategoryConfig.NO_CLOS_CATEGORY_HT.contains(marketCategoryId) ? 6L : 7L;
        Long time =periodId == 6L ? 44 * 60 * 1000L : 46 * 60 * 1000L;
		//阶段开始时间
		Long startTime = matchPeriodId2Time.get(periodId.toString());
		if (null == startTime) {
			return Boolean.FALSE;
		}
		Long nowTime = System.currentTimeMillis();
        Boolean isTrue = nowTime >= startTime + time;
		log.info("::{}::processConfigMatchStatus 时间阶段时间校验。缓存阶段时间:{},玩法ID对应阶段:{}_{},系统时间:{},最终结果:{}",
				linkId, matchPeriodId2Time, marketCategoryId, periodId, nowTime, isTrue);
		if (isTrue) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

    /**
     * 阶段为上半场：全场强开的时候，上半场玩法符合条件才强开
     * *
     *
     * @param standardMatchId
     * @param marketCategoryId
     * @return TRUE:终止强转逻辑，FALSE:继续强转
     */
    private Boolean periodCheck(String linkId, Long standardMatchId, Long marketCategoryId, Long matchPeriodId) {
        //只处理上半场阶段，和上半场玩法
        if (matchPeriodId == 6 && MarginCategoryConfig.MATCH_PERIOD_CATEGORY_OPEN.containsValue(marketCategoryId)) {
            Map<Long, Long> categoryMap = MarginCategoryConfig.MATCH_PERIOD_CATEGORY_OPEN.entrySet().stream().collect(Collectors.toMap(category -> category.getValue(), category -> category.getKey()));
            //全场玩法
            Long categoryId = categoryMap.get(marketCategoryId);
            String tagKey = Constant.REDIS_KEY.RONGHE_MATCH_CATEGORY_TAG + standardMatchId;
            Object obj = redisService.hGet(tagKey, categoryId.toString());
            log.info("::{}::processConfigMatchStatus,赛事ID:{},玩法ID:{}-{},periodCheck全场强开标识数据:{}"
                    , linkId, standardMatchId, categoryId, marketCategoryId, obj);
            if (obj == null) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }
        return Boolean.FALSE;
    }

}

