package com.panda.merge.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.message.StandardMarketMessage;
import com.panda.merge.dto.message.StandardMarketOddsMessage;
import com.panda.merge.mapper.ConfigMarketOddsStatusMapper;
import com.panda.merge.model.ConfigMarketOddsStatus;
import com.panda.merge.model.ConfigMarketOddsStatusExample;
import com.panda.merge.model.ConfigMarketOddsStatusExample.Criteria;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.service.ConfigMarketOddsStatusService;

import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName ConfigMarketOddsStatusServiceImpl
 * @Description TODO
 * @Author damian
 * @Date 2022/05/14 14:08
 **/
@Slf4j
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class ConfigMarketOddsStatusServiceImpl implements ConfigMarketOddsStatusService {
    @Autowired
    private ConfigMarketOddsStatusMapper configMarketOddsStatusMapper;
    @Autowired
    private RedisService redisService;
    
    @Override
    @Cacheable(key = "'ConfigMarketOddsStatus:' + #matchId + '-' + #categoryId + '-' +#oddsType",unless = "#result == null ")
    public ConfigMarketOddsStatus getItemOne(Long matchId, Long categoryId, String oddsType) {
    	ConfigMarketOddsStatusExample configMarketOddsStatusExample = new ConfigMarketOddsStatusExample();
    	configMarketOddsStatusExample.createCriteria().andStandardMatchInfoIdEqualTo(matchId).
    	andStandardCategoryIdEqualTo(categoryId).andOddsTypeEqualTo(oddsType);
        List<ConfigMarketOddsStatus> configMarketOddsStatus = configMarketOddsStatusMapper.selectByExample(configMarketOddsStatusExample);
        if (CollectionUtils.isEmpty(configMarketOddsStatus))
        {
            return null;
        }
        return configMarketOddsStatus.get(0);
    }

    @Override
    @CachePut(key = "'ConfigMarketOddsStatus:' + #configMarketOddsStatus.standardMatchInfoId+'-'+#configMarketOddsStatus.standardCategoryId+'-'+#configMarketOddsStatus.oddsType",unless = "#result == null ")
    public ConfigMarketOddsStatus create(ConfigMarketOddsStatus configMarketOddsStatus) {
    	configMarketOddsStatus.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
    	configMarketOddsStatus.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        configMarketOddsStatusMapper.insert(configMarketOddsStatus);
        return configMarketOddsStatus;
    }

    @Override
    @CachePut(key = "'ConfigMarketOddsStatus:' + #configMarketOddsStatus.standardMatchInfoId+'-'+#configMarketOddsStatus.standardCategoryId+'-'+#configMarketOddsStatus.oddsType",unless = "#result == null ")
    public ConfigMarketOddsStatus update(ConfigMarketOddsStatus configMarketOddsStatus) {
    	configMarketOddsStatus.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
    	ConfigMarketOddsStatusExample configMarketOddsStatusExample = new ConfigMarketOddsStatusExample();
    	configMarketOddsStatusExample.createCriteria().andStandardMatchInfoIdEqualTo(configMarketOddsStatus.getStandardMatchInfoId()).
    	andStandardCategoryIdEqualTo(configMarketOddsStatus.getStandardCategoryId()).andOddsTypeEqualTo(configMarketOddsStatus.getOddsType());
        configMarketOddsStatusMapper.updateByExample(configMarketOddsStatus,configMarketOddsStatusExample);
        return configMarketOddsStatus;
    }
    
    @Override
    @CachePut(key = "'ConfigMarketOddsStatus:' + #matchId+'-'+#categoryId+'-'+#oddsType",unless = "#result == null ")
    public void delete(Long matchId, Long categoryId, String oddsType) {
    	ConfigMarketOddsStatusExample configMarketOddsStatusExample = new ConfigMarketOddsStatusExample();
    	configMarketOddsStatusExample.createCriteria().andStandardMatchInfoIdEqualTo(matchId).
    	andStandardCategoryIdEqualTo(categoryId).andOddsTypeEqualTo(oddsType);
        int count = configMarketOddsStatusMapper.deleteByExample(configMarketOddsStatusExample);
        if(count > 0) {
        	redisService.del(RedisConfig.REDIS_KEY_DATABASE+"::ConfigMarketOddsStatus:"+matchId+"-"+categoryId+"-"+oddsType);
        }
    }

    @Override
    @CachePut(key = "'ConfigMarketOddsStatus:' + #matchId",unless = "#result == null ")
    public List<ConfigMarketOddsStatus> getItemListByMatchId(Long standardMatchInfoId) {
    	ConfigMarketOddsStatusExample configMarketOddsStatusExample = new ConfigMarketOddsStatusExample();
    	configMarketOddsStatusExample.createCriteria().andStandardMatchInfoIdEqualTo(standardMatchInfoId).andStatusEqualTo(Constant.CONFIG_MARKET_ODDS_STATUS.OPEN);
        return configMarketOddsStatusMapper.selectByExample(configMarketOddsStatusExample);
    }
    
    /**
     * 投注项赔率设置处理
     */
    public void processConfigOddsValue(String linkId, List<StandardMarketMessage> StandardMarketMessageList, StandardMatchInfo standardMatchInfo) {
    	//获取风控投注项配置
    	Map<String,ConfigMarketOddsStatus> oddsStatusMap = null;
    	List<ConfigMarketOddsStatus> oddsStatusList = getItemListByMatchId(standardMatchInfo.getId());
    	if(CollectionUtils.isEmpty(oddsStatusList) || CollectionUtils.isEmpty(StandardMarketMessageList)) {
    		log.info("::{}::标准赛事下没有投注项配置,标准赛事ID:{},下发盘口集合", linkId, standardMatchInfo.getId());
    		return;
    	}
    	oddsStatusMap = oddsStatusList.stream().collect(Collectors.toMap(s -> s.getStandardCategoryId()+"_"+s.getOddsType(), s -> s));
    	log.info("::{}::执行风控投注项配置,标准赛事ID:{},投注项配置信息{}", linkId, standardMatchInfo.getId(), oddsStatusMap);
    	ConfigMarketOddsStatus oddsStatus = null;
    	List<StandardMarketOddsMessage> marketOddsList = null;
    	for(StandardMarketMessage standardMarketMessage : StandardMarketMessageList) {
	        marketOddsList = standardMarketMessage.getMarketOddsList();
	        if(CollectionUtils.isEmpty(marketOddsList)) {
	        	continue;
	        }
	    	for(StandardMarketOddsMessage s : marketOddsList) {
		    	if((oddsStatus=oddsStatusMap.get(standardMarketMessage.getMarketCategoryId()+"_"+s.getOddsType()))!=null &&
		    			oddsStatus.getOddsValue() != null) {//是否存在风控投注项配置
		    		log.info("::{}::设置投注项配置内容,标准赛事ID:{},玩法ID:{},投注项:{},配置赔率:{}",
		                    linkId, standardMatchInfo.getId(), standardMarketMessage.getMarketCategoryId(), s.getOddsType(),oddsStatus.getOddsValue());
		    		s.setPaOddsValue(oddsStatus.getOddsValue().intValue());//风控投注项配置赔率赋值
		    		s.setStatus(Constant.CONFIG_MARKET_ODDS_STATUS.OPEN);
		    	}
	    	}
    	}
    }

	@Override
	public void updateStatusByMatchId(String linkId, Long standardMatchId, Integer status) {
		log.info("::{}::updateStatusByMatchId清除赛事投注项配置,标准赛事ID:{},状态{}", linkId, standardMatchId,status);
		ConfigMarketOddsStatusExample configMarketOddsStatusExample = new ConfigMarketOddsStatusExample();
		Criteria criteria = configMarketOddsStatusExample.createCriteria().andStandardMatchInfoIdEqualTo(standardMatchId);
		if(Constant.CONFIG_MARKET_ODDS_STATUS.CLOSE.equals(status)) {
			criteria.andStatusEqualTo(Constant.CONFIG_MARKET_ODDS_STATUS.OPEN);
		}else {
			criteria.andStatusEqualTo(Constant.CONFIG_MARKET_ODDS_STATUS.CLOSE);
		}
		List<ConfigMarketOddsStatus> list = configMarketOddsStatusMapper.selectByExample(configMarketOddsStatusExample);
		log.info("::{}::updateStatusByMatchId清除赛事投注项配置内容{}", linkId, list);
    	ConfigMarketOddsStatus configMarketOddsStatus = new ConfigMarketOddsStatus();
    	configMarketOddsStatus.setStatus(status);
    	configMarketOddsStatus.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
    	int count = configMarketOddsStatusMapper.updateByExampleSelective(configMarketOddsStatus,configMarketOddsStatusExample);
    	if(count > 0) {
    		list.forEach(e->{
    			redisService.del(RedisConfig.REDIS_KEY_DATABASE+"::ConfigMarketOddsStatus:"+standardMatchId+"-"+e.getStandardCategoryId()+"-"+e.getOddsType());
    		});
    	}
	}

	@Override
	public void updateStatusByMatchIdAndCategoryIds(String linkId, Long standardMatchId, List<Long> categoryIds,
			Integer status) {
		log.info("::{}::updateStatusByMatchIdAndCategoryIds清除玩法投注项配置,标准赛事ID:{}, 玩法集合{},状态{}", linkId, standardMatchId,categoryIds,status);
		ConfigMarketOddsStatusExample configMarketOddsStatusExample = new ConfigMarketOddsStatusExample();
		Criteria criteria = configMarketOddsStatusExample.createCriteria().andStandardMatchInfoIdEqualTo(standardMatchId).andStandardCategoryIdIn(categoryIds);
		if(Constant.CONFIG_MARKET_ODDS_STATUS.CLOSE.equals(status)) {
			criteria.andStatusEqualTo(Constant.CONFIG_MARKET_ODDS_STATUS.OPEN);
		}else {
			criteria.andStatusEqualTo(Constant.CONFIG_MARKET_ODDS_STATUS.CLOSE);
		}
		List<ConfigMarketOddsStatus> list = configMarketOddsStatusMapper.selectByExample(configMarketOddsStatusExample);
		log.info("::{}::updateStatusByMatchIdAndCategoryIds清除玩法投注项配置内容{}", linkId, list);
    	ConfigMarketOddsStatus configMarketOddsStatus = new ConfigMarketOddsStatus();
    	configMarketOddsStatus.setStatus(status);
    	configMarketOddsStatus.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
    	int count = configMarketOddsStatusMapper.updateByExampleSelective(configMarketOddsStatus,configMarketOddsStatusExample);
    	if(count > 0) {
    		list.forEach(e->{
    			redisService.del(RedisConfig.REDIS_KEY_DATABASE+"::ConfigMarketOddsStatus:"+standardMatchId+"-"+e.getStandardCategoryId()+"-"+e.getOddsType());
    		});
    	}
	}
}
