package com.panda.merge.proxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.model.StandardSportMarket;
import com.panda.merge.model.StandardSportMarketOdds;
import com.panda.merge.model.ThirdSportMarket;

import lombok.extern.slf4j.Slf4j;

/**
 * 处理赔率数据 在数据没有变动的情况下不需要修改 的业务逻辑
 * @author damian
 *
 */
@Component
@Slf4j
public class UpdateOperateProxy {
	@Autowired
    private StandardSportMarketNewService standardSportMarketService;
	
	@Autowired
    private StandardSportMarketOddsNewService standardSportMarketOddsService;
	
	@Autowired
    private ThirdSportMarketNewService thirdSportMarketService;
	
	private static Map<String,Object> dataSources = null;
	static {
		dataSources = new HashMap<String,Object>();
		dataSources.put("AO", "AO");
	}
	
	/**
	 * 修改标准盘口信息代理逻辑处理
	 * @param standardSportMarket
	 * @return
	 */
	public StandardSportMarket updateStandardSportMarket(StandardSportMarket standardSportMarket,String linkId) {
		if(standardSportMarket == null || standardSportMarket.getStandardMatchInfoId() == null || standardSportMarket.getStandardMatchInfoId().longValue() == 0||
				StringUtils.isEmpty(standardSportMarket.getDataSourceCode()) || StringUtils.isEmpty(standardSportMarket.getThirdMarketSourceId())) {
			log.info("::{}::修改标准盘口信息数据验证不通过，StandardMatchInfoId={}, DataSourceCode={}, ThirdMarketSourceId={}, 时间:{}", linkId, standardSportMarket.getStandardMatchInfoId(), standardSportMarket.getDataSourceCode(), standardSportMarket.getThirdMarketSourceId(),TimeUtils.millsSecondsEast8ZoneGmt());
			return standardSportMarket;
		}
		if(dataSources.containsKey(standardSportMarket.getDataSourceCode())) {
			StandardSportMarket old = standardSportMarketService.getItem(standardSportMarket.getDataSourceCode(), standardSportMarket.getThirdMarketSourceId(), standardSportMarket.getStandardMatchInfoId());
			if(old != null && equalsStandardSportMarket(old, standardSportMarket,linkId)) {
				log.info("::{}::当前修改标准盘口数据已为最新数据，跳过数据库修改操作，StandardMatchInfoId={}, DataSourceCode={}, ThirdMarketSourceId={}, 时间:{}", linkId, standardSportMarket.getStandardMatchInfoId(), standardSportMarket.getDataSourceCode(), standardSportMarket.getThirdMarketSourceId(),TimeUtils.millsSecondsEast8ZoneGmt());
				return standardSportMarket;
			}
		}
		log.info("::{}::执行 修改标准盘口信息 数据库操作，StandardMatchInfoId={}, DataSourceCode={}, ThirdMarketSourceId={}, 时间:{}", linkId, standardSportMarket.getStandardMatchInfoId(), standardSportMarket.getDataSourceCode(), standardSportMarket.getThirdMarketSourceId(),TimeUtils.millsSecondsEast8ZoneGmt());
		try {
			return standardSportMarketService.updateByPrimaryKeySelective(standardSportMarket);
		}catch (Exception e){
			log.info("::{}::执行 修改标准盘口信息出现异常：{}", linkId, standardSportMarket.getThirdMarketSourceId());
			return standardSportMarket;
		}
	}
	/**
	 * 自定义比较标准盘口信息代理逻辑处理
	 * @param old
	 * @param news
	 * @return
	 */
	private boolean equalsStandardSportMarket(StandardSportMarket old,StandardSportMarket news,String linkId) {
		if((old.getThirdMarketSourceStatus() == null && news.getThirdMarketSourceStatus() != null) || (old.getThirdMarketSourceStatus() != null && news.getThirdMarketSourceStatus() == null)) {
        	log.info("::{}:: StandardSportMarket新旧其中有一个为空，需要执行数据库调整操作 oldThirdMarketSourceStatus={}, newsThirdMarketSourceStatus={}, 时间:{}", linkId, old.getThirdMarketSourceStatus(), news.getThirdMarketSourceStatus(),TimeUtils.millsSecondsEast8ZoneGmt());
        	return false;
        }
        if(old.getThirdMarketSourceStatus() != null && news.getThirdMarketSourceStatus() != null && !old.getThirdMarketSourceStatus().equals(news.getThirdMarketSourceStatus())) {
        	log.info("::{}::StandardSportMarket新旧数据内容不一致，需要执行数据库调整操作 oldThirdMarketSourceStatus={}, newsThirdMarketSourceStatus={}, 时间:{}", linkId, old.getThirdMarketSourceStatus(), news.getThirdMarketSourceStatus(),TimeUtils.millsSecondsEast8ZoneGmt());
        	return false;
        }
        if((old.getStatus() == null && news.getStatus() != null) || (old.getStatus() != null && news.getStatus() == null)) {
        	log.info("::{}:: StandardSportMarket新旧其中有一个为空，需要执行数据库调整操作 oldStatus={}, newsStatus={}, 时间:{}", linkId, old.getStatus(), news.getStatus(),TimeUtils.millsSecondsEast8ZoneGmt());
        	return false;
        }
        if(old.getStatus() != null && news.getStatus() != null && !old.getStatus().equals(news.getStatus())) {
        	log.info("::{}::StandardSportMarket新旧数据内容不一致，需要执行数据库调整操作 oldStatus={}, newsStatus={}, 时间:{}", linkId, old.getStatus(), news.getStatus(),TimeUtils.millsSecondsEast8ZoneGmt());
        	return false;
        }
        if((old.getMarketType() == null && news.getMarketType() != null) || (old.getMarketType() != null && news.getMarketType() == null)) {
        	log.info("::{}:: StandardSportMarket新旧其中有一个为空，需要执行数据库调整操作 oldMarketType={}, newsMarketType={}, 时间:{}", linkId, old.getMarketType(), news.getMarketType(),TimeUtils.millsSecondsEast8ZoneGmt());
        	return false;
        }
        if(old.getMarketType() != null && news.getMarketType() != null && !old.getMarketType().equals(news.getMarketType())) {
        	log.info("::{}:: StandardSportMarket新旧数据内容不一致，需要执行数据库调整操作 oldMarketType={}, newsMarketType={}, 时间:{}", linkId, old.getMarketType(), news.getMarketType(),TimeUtils.millsSecondsEast8ZoneGmt());
        	return false;
        }
        if((old.getOddsName() == null && news.getOddsName() != null) || (old.getOddsName() != null && news.getOddsName() == null)) {
        	log.info("::{}:: StandardSportMarket新旧其中有一个为空，需要执行数据库调整操作 oldOddsName={}, newsOddsName={}, 时间:{}", linkId, old.getOddsName(), news.getOddsName(),TimeUtils.millsSecondsEast8ZoneGmt());
        	return false;
        }
        if(old.getOddsName() != null && news.getOddsName() != null && !old.getOddsName().equals(news.getOddsName())) {
        	log.info("::{}:: StandardSportMarket新旧数据内容不一致，需要执行数据库调整操作 oldOddsName={}, newsOddsName={}, 时间:{}", linkId, old.getOddsName(), news.getOddsName(),TimeUtils.millsSecondsEast8ZoneGmt());
        	return false;
        }
        if((old.getAddition1() == null && news.getAddition1() != null) || (old.getAddition1() != null && news.getAddition1() == null)) {
        	log.info("::{}:: StandardSportMarket新旧其中有一个为空，需要执行数据库调整操作 oldAddition1={}, newsAddition1={}, 时间:{}", linkId, old.getAddition1(), news.getAddition1(),TimeUtils.millsSecondsEast8ZoneGmt());
        	return false;
        }
        if(old.getAddition1() != null && news.getAddition1() != null && !old.getAddition1().equals(news.getAddition1())) {
        	log.info("::{}:: StandardSportMarket新旧数据内容不一致，需要执行数据库调整操作 oldAddition1={}, newsAddition1={}, 时间:{}", linkId, old.getAddition1(), news.getAddition1(),TimeUtils.millsSecondsEast8ZoneGmt());
        	return false;
        }
        if((old.getAddition2() == null && news.getAddition2() != null) || (old.getAddition2() != null && news.getAddition2() == null)) {
        	log.info("::{}:: StandardSportMarket新旧其中有一个为空，需要执行数据库调整操作 oldAddition2={}, newsAddition2={}, 时间:{}", linkId, old.getAddition2(), news.getAddition2(),TimeUtils.millsSecondsEast8ZoneGmt());
        	return false;
        }
        if(old.getAddition2() != null && news.getAddition2() != null && !old.getAddition2().equals(news.getAddition2())) {
        	log.info("::{}:: StandardSportMarket新旧数据内容不一致，需要执行数据库调整操作 oldAddition2={}, newsAddition2={}, 时间:{}", linkId, old.getAddition2(), news.getAddition2(),TimeUtils.millsSecondsEast8ZoneGmt());
        	return false;
        }
        if((old.getAddition3() == null && news.getAddition3() != null) || (old.getAddition3() != null && news.getAddition3() == null)) {
        	log.info("::{}:: StandardSportMarket新旧其中有一个为空，需要执行数据库调整操作 oldAddition3={}, newsAddition3={}, 时间:{}", linkId, old.getAddition3(), news.getAddition3(),TimeUtils.millsSecondsEast8ZoneGmt());
        	return false;
        }
        if(old.getAddition3() != null && news.getAddition3() != null && !old.getAddition3().equals(news.getAddition3())) {
        	log.info("::{}:: StandardSportMarket新旧数据内容不一致，需要执行数据库调整操作 oldAddition3={}, newsAddition3={}, 时间:{}", linkId, old.getAddition3(), news.getAddition3(),TimeUtils.millsSecondsEast8ZoneGmt());
        	return false;
        }
        if((old.getAddition4() == null && news.getAddition4() != null) || (old.getAddition4() != null && news.getAddition4() == null)) {
        	log.info("::{}:: StandardSportMarket新旧其中有一个为空，需要执行数据库调整操作 oldAddition4={}, newsAddition4={}, 时间:{}", linkId, old.getAddition4(), news.getAddition4(),TimeUtils.millsSecondsEast8ZoneGmt());
        	return false;
        }
        if(old.getAddition4() != null && news.getAddition4() != null && !old.getAddition4().equals(news.getAddition4())) {
        	log.info("::{}:: StandardSportMarket新旧数据内容不一致，需要执行数据库调整操作 oldAddition4={}, newsAddition4={}, 时间:{}", linkId, old.getAddition4(), news.getAddition4(),TimeUtils.millsSecondsEast8ZoneGmt());
        	return false;
        }
        if((old.getNumberOfWinners() == null && news.getNumberOfWinners() != null) || (old.getNumberOfWinners() != null && news.getNumberOfWinners() == null)) {
        	log.info("::{}:: StandardSportMarket新旧其中有一个为空，需要执行数据库调整操作 oldNumberOfWinners={}, newsNumberOfWinners={}, 时间:{}", linkId, old.getNumberOfWinners(), news.getNumberOfWinners(),TimeUtils.millsSecondsEast8ZoneGmt());
        	return false;
        }
        if(old.getNumberOfWinners() != null && news.getNumberOfWinners() != null && !old.getNumberOfWinners().equals(news.getNumberOfWinners())) {
        	log.info("::{}:: StandardSportMarket新旧数据内容不一致，需要执行数据库调整操作 oldNumberOfWinners={}, newsNumberOfWinners={}, 时间:{}", linkId, old.getNumberOfWinners(), news.getNumberOfWinners(),TimeUtils.millsSecondsEast8ZoneGmt());
        	return false;
        }
		return true;
	}
	
	/**
	 * 修改标准盘口赔率信息代理逻辑处理
	 * @param standardSportMarketOdds
	 * @return
	 */
	public StandardSportMarketOdds updateStandardSportMarketOdds(StandardSportMarketOdds standardSportMarketOdds, String linkId) {
		if(standardSportMarketOdds == null || standardSportMarketOdds.getMarketId() == null || standardSportMarketOdds.getMarketId().longValue() == 0 ||
				StringUtils.isEmpty(standardSportMarketOdds.getDataSourceCode()) || StringUtils.isEmpty(standardSportMarketOdds.getThirdOddsFieldSourceId())) {
			log.info("::{}:: 修改标准盘口赔率信息数据验证不通过，MarketId={}, DataSourceCode={}, ThirdOddsFieldSourceId={}, 时间:{}", linkId, standardSportMarketOdds.getId(), standardSportMarketOdds.getDataSourceCode(), standardSportMarketOdds.getThirdOddsFieldSourceId(),TimeUtils.millsSecondsEast8ZoneGmt());
			return standardSportMarketOdds;
		}
		if(dataSources.containsKey(standardSportMarketOdds.getDataSourceCode())) {
			StandardSportMarketOdds old = standardSportMarketOddsService.getItem(standardSportMarketOdds.getDataSourceCode(), standardSportMarketOdds.getThirdOddsFieldSourceId(), standardSportMarketOdds.getMarketId());
			if(old != null && equalsStandardSportMarketOdds(old, standardSportMarketOdds,linkId)) {
				log.info("::{}::当前修改标准盘口赔率数据已为最新数据，跳过数据库修改操作，MarketId={}, DataSourceCode={}, ThirdOddsFieldSourceId={}, 时间:{}", linkId, standardSportMarketOdds.getMarketId(), standardSportMarketOdds.getDataSourceCode(), standardSportMarketOdds.getThirdOddsFieldSourceId(),TimeUtils.millsSecondsEast8ZoneGmt());
				return standardSportMarketOdds;
			}
		}
		log.info("::{}::执行 修改标准盘口赔率信息 数据库操作，MarketId={}, DataSourceCode={}, ThirdOddsFieldSourceId={}, 时间:{}", linkId, standardSportMarketOdds.getMarketId(), standardSportMarketOdds.getDataSourceCode(), standardSportMarketOdds.getThirdOddsFieldSourceId(),TimeUtils.millsSecondsEast8ZoneGmt());
		try {
			return standardSportMarketOddsService.updateByPrimaryKeySelective(standardSportMarketOdds);
		} catch (Exception e) {
			log.info("::{}::执行 修改标准盘口赔率信息 出现异常：{}", linkId, standardSportMarketOdds.getThirdOddsFieldSourceId());
			return standardSportMarketOdds;
		}
	}
	
	/**
	 * 自定义比较标准盘口赔率信息代理逻辑处理
	 * @param old
	 * @param news
	 * @return
	 */
	private boolean equalsStandardSportMarketOdds(StandardSportMarketOdds old,StandardSportMarketOdds news,String linkId) {
        if(old.getI18nNames() != news.getI18nNames() && old.getI18nNames() != null && news.getI18nNames() != null && old.getI18nNames().size() != news.getI18nNames().size()) {
        	log.info("::{}:: StandardSportMarketOdds新旧数据长度不一致，需要执行数据库调整操作 oldI18nName={}, newsI18nName={}, 时间:{}", linkId, old.getI18nNames(), news.getI18nNames(),TimeUtils.millsSecondsEast8ZoneGmt());
        	return false;
        }
        if((old.getI18nNames() == null && news.getI18nNames() != null) || (old.getI18nNames() != null && news.getI18nNames() == null)) {
        	log.info("::{}:: StandardSportMarketOdds新旧其中有一个为空，需要执行数据库调整操作 oldI18nName={}, newsI18nName={}, 时间:{}", linkId, old.getI18nNames(), news.getI18nNames(),TimeUtils.millsSecondsEast8ZoneGmt());
        	return false;
        }
        if(old.getI18nNames() != null && news.getI18nNames() != null) {
        	List<Object> oldNameCodes=old.getI18nNames().stream().map(obj->obj.getNameCode()).collect(Collectors.toList());
            List<Object> newsNameCodes=news.getI18nNames().stream().map(obj->obj.getNameCode()).collect(Collectors.toList());
            oldNameCodes.retainAll(newsNameCodes);
            if(oldNameCodes.size() != newsNameCodes.size()) {
            	log.info("::{}:: StandardSportMarketOdds新旧数据内容不一致，需要执行数据库调整操作 oldI18nName={}, newsI18nName={}, 时间:{}", linkId, old.getI18nNames(), news.getI18nNames(),TimeUtils.millsSecondsEast8ZoneGmt());
            	return false;
            }
        }
        if((old.getActive() == null && news.getActive() != null) || (old.getActive() != null && news.getActive() == null)) {
        	log.info("::{}:: StandardSportMarketOdds新旧其中有一个为空，需要执行数据库调整操作 oldActive={}, newsActive={}, 时间:{}", linkId, old.getActive(), news.getActive(),TimeUtils.millsSecondsEast8ZoneGmt());
        	return false;
        }
        if(old.getActive() != null && news.getActive() != null && !old.getActive().equals(news.getActive())) {
        	log.info("::{}:: StandardSportMarketOdds新旧数据内容不一致，需要执行数据库调整操作 oldActive={}, newsActive={}, 时间:{}", linkId, old.getActive(), news.getActive(),TimeUtils.millsSecondsEast8ZoneGmt());
        	return false;
        }
        if((old.getOddsValue() == null && news.getOddsValue() != null) || (old.getOddsValue() != null && news.getOddsValue() == null)) {
        	log.info("::{}:: StandardSportMarketOdds新旧其中有一个为空，需要执行数据库调整操作 oldOddsValue={}, newsOddsValue={}, 时间:{}", linkId, old.getOddsValue(), news.getOddsValue(),TimeUtils.millsSecondsEast8ZoneGmt());
        	return false;
        }
        if(old.getOddsValue() != null && news.getOddsValue() != null && !old.getOddsValue().equals(news.getOddsValue())) {
        	log.info("::{}:: StandardSportMarketOdds新旧数据内容不一致，需要执行数据库调整操作 oldOddsValue={}, newsOddsValue={}, 时间:{}", linkId, old.getOddsValue(), news.getOddsValue(),TimeUtils.millsSecondsEast8ZoneGmt());
        	return false;
        }
        if((old.getOriginalOddsValue() == null && news.getOriginalOddsValue() != null) || (old.getOriginalOddsValue() != null && news.getOriginalOddsValue() == null)) {
        	log.info("::{}:: StandardSportMarketOdds新旧其中有一个为空，需要执行数据库调整操作 oldOriginalOddsValue={}, newsOriginalOddsValue={}, 时间:{}", linkId, old.getOriginalOddsValue(), news.getOriginalOddsValue(),TimeUtils.millsSecondsEast8ZoneGmt());
        	return false;
        }
        if(old.getOriginalOddsValue() != null && news.getOriginalOddsValue() != null && !old.getOriginalOddsValue().equals(news.getOriginalOddsValue())) {
        	log.info("::{}:: StandardSportMarketOdds新旧数据内容不一致，需要执行数据库调整操作 oldOriginalOddsValue={}, newsOriginalOddsValue={}, 时间:{}", linkId, old.getOriginalOddsValue(), news.getOriginalOddsValue(),TimeUtils.millsSecondsEast8ZoneGmt());
        	return false;
        }
        if((old.getOddsType() == null && news.getOddsType() != null) || (old.getOddsType() != null && news.getOddsType() == null)) {
        	log.info("::{}:: StandardSportMarketOdds新旧其中有一个为空，需要执行数据库调整操作 oldOddsType={}, newsOddsType={}, 时间:{}", linkId, old.getOddsType(), news.getOddsType(),TimeUtils.millsSecondsEast8ZoneGmt());
        	return false;
        }
        if(old.getOddsType() != null && news.getOddsType() != null && !old.getOddsType().equals(news.getOddsType())) {
        	log.info("::{}:: StandardSportMarketOdds新旧数据内容不一致，需要执行数据库调整操作 oldOddsType={}, newsOddsType={}, 时间:{}", linkId, old.getOddsType(), news.getOddsType(),TimeUtils.millsSecondsEast8ZoneGmt());
        	return false;
        }
        if((old.getAddition1() == null && news.getAddition1() != null) || (old.getAddition1() != null && news.getAddition1() == null)) {
        	log.info("::{}:: StandardSportMarketOdds新旧其中有一个为空，需要执行数据库调整操作 oldAddition1={}, newsAddition1={}, 时间:{}", linkId, old.getAddition1(), news.getAddition1(),TimeUtils.millsSecondsEast8ZoneGmt());
        	return false;
        }
        if(old.getAddition1() != null && news.getAddition1() != null && !old.getAddition1().equals(news.getAddition1())) {
        	log.info("::{}:: StandardSportMarketOdds新旧数据内容不一致，需要执行数据库调整操作 oldAddition1={}, newsAddition1={}, 时间:{}", linkId, old.getAddition1(), news.getAddition1(),TimeUtils.millsSecondsEast8ZoneGmt());
        	return false;
        }
        if((old.getNameCode() == null && news.getNameCode() != null) || (old.getNameCode() != null && news.getNameCode() == null)) {
        	log.info("::{}:: StandardSportMarketOdds新旧其中有一个为空，需要执行数据库调整操作 oldNameCode={}, newsNameCode={}, 时间:{}", linkId, old.getNameCode(), news.getNameCode(),TimeUtils.millsSecondsEast8ZoneGmt());
        	return false;
        }
        if(old.getNameCode() != null && news.getNameCode() != null && !old.getNameCode().equals(news.getNameCode())) {
        	log.info("::{}:: StandardSportMarketOdds新旧数据内容不一致，需要执行数据库调整操作 oldNameCode={}, newsNameCode={}, 时间:{}", linkId, old.getNameCode(), news.getNameCode(),TimeUtils.millsSecondsEast8ZoneGmt());
        	return false;
        }
		return true;
	}
	
	/**
	 * 修改第三方盘口信息代理逻辑处理
	 * @param thirdSportMarket
	 * @param linkId
	 * @return
	 */
	public ThirdSportMarket updateThirdSportMarket(ThirdSportMarket thirdSportMarket,String linkId) {
		if(thirdSportMarket == null || thirdSportMarket.getMatchId() == null || thirdSportMarket.getMatchId().longValue() == 0 ||
				StringUtils.isEmpty(thirdSportMarket.getDataSourceCode()) || StringUtils.isEmpty(thirdSportMarket.getThirdMarketSourceId())) {
			log.info("::{}:: 修改第三方盘口信息数据验证不通过，MatchId={}, DataSourceCode={}, ThirdMarketSourceId={}, 时间:{}", linkId, thirdSportMarket.getMatchId(), thirdSportMarket.getDataSourceCode(), thirdSportMarket.getThirdMarketSourceId(),TimeUtils.millsSecondsEast8ZoneGmt());
			return thirdSportMarket;
		}
		if(dataSources.containsKey(thirdSportMarket.getDataSourceCode())) {
			ThirdSportMarket old = thirdSportMarketService.getItem(thirdSportMarket.getDataSourceCode(), thirdSportMarket.getThirdMarketSourceId(), thirdSportMarket.getMatchId());
			if(old != null && equalsThirdSportMarket(old, thirdSportMarket, linkId)) {
				log.info("::{}::当前修改第三方盘口数据已为最新数据，跳过数据库修改操作，MatchId={}, DataSourceCode={}, ThirdMarketSourceId={}, 时间:{}", linkId, thirdSportMarket.getMatchId(), thirdSportMarket.getDataSourceCode(), thirdSportMarket.getThirdMarketSourceId(),TimeUtils.millsSecondsEast8ZoneGmt());
				return thirdSportMarket;
			}
		}
        log.info("::{}::执行 修改第三方盘口信息 数据库操作，MatchId={}, DataSourceCode={}, ThirdMarketSourceId={},最终修改数据:{}, 时间:{}",
                linkId, thirdSportMarket.getMatchId(), thirdSportMarket.getDataSourceCode(), thirdSportMarket.getThirdMarketSourceId(), JSONObject.toJSONString(thirdSportMarket), TimeUtils.millsSecondsEast8ZoneGmt());
		try {
			return thirdSportMarketService.updateByPrimaryKeySelective(thirdSportMarket);
		} catch (Exception e) {
			log.info("::{}::执行 修改第三方盘口信息出现异常:{}", linkId, thirdSportMarket.getThirdMarketSourceId());
			return thirdSportMarket;
		}

	}
	
	/**
	 * 自定义比较标准盘口赔率信息代理逻辑处理
	 * @param old
	 * @param news
	 * @return
	 */
	private boolean equalsThirdSportMarket(ThirdSportMarket old,ThirdSportMarket news,String linkId) {
		if((old.getStatus() == null && news.getStatus() != null) || (old.getStatus() != null && news.getStatus() == null)) {
        	log.info("::{}:: ThirdSportMarket新旧其中有一个为空，需要执行数据库调整操作 oldStatus={}, newsStatus={}, 时间:{}", linkId, old.getStatus(), news.getStatus(),TimeUtils.millsSecondsEast8ZoneGmt());
        	return false;
        }
        if(old.getStatus() != null && news.getStatus() != null && !old.getStatus().equals(news.getStatus())) {
        	log.info("::{}:: ThirdSportMarket新旧数据内容不一致，需要执行数据库调整操作 oldStatus={}, newsStatus={}, 时间:{}", linkId, old.getStatus(), news.getStatus(),TimeUtils.millsSecondsEast8ZoneGmt());
        	return false;
        }
        if((old.getThirdMarketSourceStatus() == null && news.getThirdMarketSourceStatus() != null) || (old.getThirdMarketSourceStatus() != null && news.getThirdMarketSourceStatus() == null)) {
        	log.info("::{}:: ThirdSportMarket新旧其中有一个为空，需要执行数据库调整操作 oldThirdMarketSourceStatus={}, newsThirdMarketSourceStatus={}, 时间:{}", linkId, old.getThirdMarketSourceStatus(), news.getThirdMarketSourceStatus(),TimeUtils.millsSecondsEast8ZoneGmt());
        	return false;
        }
        if(old.getThirdMarketSourceStatus() != null && news.getThirdMarketSourceStatus() != null && !old.getThirdMarketSourceStatus().equals(news.getThirdMarketSourceStatus())) {
        	log.info("::{}:: ThirdSportMarket新旧数据内容不一致，需要执行数据库调整操作 oldThirdMarketSourceStatus={}, newsThirdMarketSourceStatus={}, 时间:{}", linkId, old.getThirdMarketSourceStatus(), news.getThirdMarketSourceStatus(),TimeUtils.millsSecondsEast8ZoneGmt());
        	return false;
        }
        if((old.getMarketType() == null && news.getMarketType() != null) || (old.getMarketType() != null && news.getMarketType() == null)) {
        	log.info("::{}:: ThirdSportMarket新旧其中有一个为空，需要执行数据库调整操作 oldMarketType={}, newsMarketType={}, 时间:{}", linkId, old.getMarketType(), news.getMarketType(),TimeUtils.millsSecondsEast8ZoneGmt());
        	return false;
        }
        if(old.getMarketType() != null && news.getMarketType() != null && !old.getMarketType().equals(news.getMarketType())) {
        	log.info("::{}:: ThirdSportMarket新旧数据内容不一致，需要执行数据库调整操作 oldMarketType={}, newsMarketType={}, 时间:{}", linkId, old.getMarketType(), news.getMarketType(),TimeUtils.millsSecondsEast8ZoneGmt());
        	return false;
        }
        if((old.getOddsName() == null && news.getOddsName() != null) || (old.getOddsName() != null && news.getOddsName() == null)) {
        	log.info("::{}:: ThirdSportMarket新旧其中有一个为空，需要执行数据库调整操作 oldOddsName={}, newsOddsName={}, 时间:{}", linkId, old.getOddsName(), news.getOddsName(),TimeUtils.millsSecondsEast8ZoneGmt());
        	return false;
        }
        if(old.getOddsName() != null && news.getOddsName() != null && !old.getOddsName().equals(news.getOddsName())) {
        	log.info("::{}:: ThirdSportMarket新旧数据内容不一致，需要执行数据库调整操作 oldOddsName={}, newsOddsName={}, 时间:{}", linkId, old.getOddsName(), news.getOddsName(),TimeUtils.millsSecondsEast8ZoneGmt());
        	return false;
        }
        if((old.getAddition1() == null && news.getAddition1() != null) || (old.getAddition1() != null && news.getAddition1() == null)) {
        	log.info("::{}:: ThirdSportMarket新旧其中有一个为空，需要执行数据库调整操作 oldAddition1={}, newsAddition1={}, 时间:{}", linkId, old.getAddition1(), news.getAddition1(),TimeUtils.millsSecondsEast8ZoneGmt());
        	return false;
        }
        if(old.getAddition1() != null && news.getAddition1() != null && !old.getAddition1().equals(news.getAddition1())) {
        	log.info("::{}:: ThirdSportMarket新旧数据内容不一致，需要执行数据库调整操作 oldAddition1={}, newsAddition1={}, 时间:{}", linkId, old.getAddition1(), news.getAddition1(),TimeUtils.millsSecondsEast8ZoneGmt());
        	return false;
        }
        if((old.getAddition2() == null && news.getAddition2() != null) || (old.getAddition2() != null && news.getAddition2() == null)) {
        	log.info("::{}:: ThirdSportMarket新旧其中有一个为空，需要执行数据库调整操作 oldAddition2={}, newsAddition2={}, 时间:{}", linkId, old.getAddition2(), news.getAddition2(),TimeUtils.millsSecondsEast8ZoneGmt());
        	return false;
        }
        if(old.getAddition2() != null && news.getAddition2() != null && !old.getAddition2().equals(news.getAddition2())) {
        	log.info("::{}:: ThirdSportMarket新旧数据内容不一致，需要执行数据库调整操作 oldAddition2={}, newsAddition2={}, 时间:{}", linkId, old.getAddition2(), news.getAddition2(),TimeUtils.millsSecondsEast8ZoneGmt());
        	return false;
        }
        if((old.getAddition3() == null && news.getAddition3() != null) || (old.getAddition3() != null && news.getAddition3() == null)) {
        	log.info("::{}:: ThirdSportMarket新旧其中有一个为空，需要执行数据库调整操作 oldAddition3={}, newsAddition3={}, 时间:{}", linkId, old.getAddition3(), news.getAddition3(),TimeUtils.millsSecondsEast8ZoneGmt());
        	return false;
        }
        if(old.getAddition3() != null && news.getAddition3() != null && !old.getAddition3().equals(news.getAddition3())) {
        	log.info("::{}:: ThirdSportMarket新旧数据内容不一致，需要执行数据库调整操作 oldAddition3={}, newsAddition3={}, 时间:{}", linkId, old.getAddition3(), news.getAddition3(),TimeUtils.millsSecondsEast8ZoneGmt());
        	return false;
        }
        if((old.getAddition4() == null && news.getAddition4() != null) || (old.getAddition4() != null && news.getAddition4() == null)) {
        	log.info("::{}:: ThirdSportMarket新旧其中有一个为空，需要执行数据库调整操作 oldAddition4={}, newsAddition4={}, 时间:{}", linkId, old.getAddition4(), news.getAddition4(),TimeUtils.millsSecondsEast8ZoneGmt());
        	return false;
        }
        if(old.getAddition4() != null && news.getAddition4() != null && !old.getAddition4().equals(news.getAddition4())) {
        	log.info("::{}:: ThirdSportMarket新旧数据内容不一致，需要执行数据库调整操作 oldAddition4={}, newsAddition4={}, 时间:{}", linkId, old.getAddition4(), news.getAddition4(),TimeUtils.millsSecondsEast8ZoneGmt());
        	return false;
        }
        if((old.getNumberOfWinners() == null && news.getNumberOfWinners() != null) || (old.getNumberOfWinners() != null && news.getNumberOfWinners() == null)) {
        	log.info("::{}:: ThirdSportMarket新旧其中有一个为空，需要执行数据库调整操作 oldNumberOfWinners={}, newsNumberOfWinners={}, 时间:{}", linkId, old.getNumberOfWinners(), news.getNumberOfWinners(),TimeUtils.millsSecondsEast8ZoneGmt());
        	return false;
        }
        if(old.getNumberOfWinners() != null && news.getNumberOfWinners() != null && !old.getNumberOfWinners().equals(news.getNumberOfWinners())) {
        	log.info("::{}:: ThirdSportMarket新旧数据内容不一致，需要执行数据库调整操作 oldNumberOfWinners={}, newsNumberOfWinners={}, 时间:{}", linkId, old.getNumberOfWinners(), news.getNumberOfWinners(),TimeUtils.millsSecondsEast8ZoneGmt());
        	return false;
        }
        if((old.getOfferLineId() == null && news.getOfferLineId() != null) || (old.getOfferLineId() != null && news.getOfferLineId() == null)) {
        	log.info("::{}:: ThirdSportMarket新旧其中有一个为空，需要执行数据库调整操作 oldOfferLineId={}, newsOfferLineId={}, 时间:{}", linkId, old.getOfferLineId(), news.getOfferLineId(),TimeUtils.millsSecondsEast8ZoneGmt());
        	return false;
        }
        if(old.getOfferLineId() != null && news.getOfferLineId() != null && !old.getOfferLineId().equals(news.getOfferLineId())) {
        	log.info("::{}:: ThirdSportMarket新旧数据内容不一致，需要执行数据库调整操作 oldOfferLineId={}, newsOfferLineId={}, 时间:{}", linkId, old.getOfferLineId(), news.getOfferLineId(),TimeUtils.millsSecondsEast8ZoneGmt());
        	return false;
        }
		if ((old.getInternalDataSourceCode() == null && news.getInternalDataSourceCode() != null) || (old.getInternalDataSourceCode() != null && news.getInternalDataSourceCode() == null)) {
			log.info("::{}:: ThirdSportMarket新旧其中有一个为空，需要执行数据库调整操作 oldInternalDataSourceCode={}, newsInternalDataSourceCode={}, 时间:{}", linkId, old.getInternalDataSourceCode(), news.getInternalDataSourceCode(), TimeUtils.millsSecondsEast8ZoneGmt());
			return false;
		}
		if (old.getInternalDataSourceCode() != null && news.getInternalDataSourceCode() != null && !old.getInternalDataSourceCode().equals(news.getInternalDataSourceCode())) {
			log.info("::{}:: ThirdSportMarket新旧数据内容不一致，需要执行数据库调整操作 oldInternalDataSourceCode={}, newsInternalDataSourceCode={}, 时间:{}", linkId, old.getInternalDataSourceCode(), news.getInternalDataSourceCode(), TimeUtils.millsSecondsEast8ZoneGmt());
			return false;
		}
		return true;
	}
}
