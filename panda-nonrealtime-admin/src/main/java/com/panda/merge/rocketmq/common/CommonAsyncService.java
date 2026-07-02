package com.panda.merge.rocketmq.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.panda.merge.constant.ConstantSystem;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.MarginCategoryConfig;
import com.panda.merge.dto.message.ThirdSportMarketMessage;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.model.ThirdSportMarket;
import com.panda.merge.model.ThirdSportMarketOdds;
import com.panda.merge.rocketmq.producer.ThirdSportMarketMergeProducer;
import com.panda.merge.service.ThirdMatchInfoService;
import com.panda.merge.service.ThirdSportMarketOddsService;
import com.panda.merge.service.ThirdSportMarketService;

import lombok.extern.slf4j.Slf4j;

/**
 * 赔率服务异步处理服务公共类
 */
@Component
@Slf4j
public class CommonAsyncService {

    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;
    @Autowired
    private ThirdSportMarketService thirdSportMarketService;
    @Autowired
    private ThirdSportMarketOddsService thirdSportMarketOddsService;
    @Autowired
    public ThirdSportMarketMergeProducer thirdSportMarketMergeProducer;
    @Autowired
    private RedisService redisService;
    @Autowired
    private BaseProcessor baseProcessor;

    /**
     * 根据标准盘口的开售信息找出其他数据源的三方盘口集合
     * @param linkId
     * @param standardMatchInfo
     */
    public void getAllThirdSportMarketList(String linkId, StandardMatchInfo standardMatchInfo,Integer marketType,Map<Long,String> longStringHashMap)
    {
        log.info("::{}::开始获取三方盘口数据，标准赛事id：{}，盘口类型：{}，标准赛事玩法开售详细信息：{}", linkId, standardMatchInfo.getId(), marketType,longStringHashMap);
        List<ThirdMatchInfo>  thirdMatchInfos = thirdMatchInfoService.getItems(standardMatchInfo.getId());
        if (CollectionUtils.isEmpty(thirdMatchInfos))
        {
            log.info("::{}::开始获取三方盘口数据，标准赛事id：{}，标准赛事玩法开售详细信息：{}，三方赛事信息集合为空，直接返回", linkId, standardMatchInfo.getId(), longStringHashMap);
            return;
        }
        List<ThirdSportMarket> thirdSportMarketList = new ArrayList<>();
        longStringHashMap.forEach((k,v)->{
            List<ThirdMatchInfo> thirdMatchInfoList = getThirdMatchInfoList(thirdMatchInfos,v);
            for (ThirdMatchInfo thirdMatchInfo : thirdMatchInfoList)
            {
                List<ThirdSportMarket>  thirdSportMarkets = thirdSportMarketService.getItemList(thirdMatchInfo.getId(),thirdMatchInfo.getDataSourceCode(),k,marketType);
                if (!CollectionUtils.isEmpty(thirdSportMarkets))
                {
                    thirdSportMarketList.addAll(thirdSportMarkets);
                }
            }
        });
        List<ThirdSportMarketMessage> thirdSportMarketMessages = new ArrayList<>();
        if (!CollectionUtils.isEmpty(thirdSportMarketList))
        {
            thirdSportMarketList.forEach(e->{
                ThirdSportMarketMessage thirdSportMarketMessage = new ThirdSportMarketMessage();
                BeanUtils.copyProperties(e, thirdSportMarketMessage);
                List<ThirdSportMarketOdds> thirdSportMarketOddsList = thirdSportMarketOddsService.getItemList(e.getDataSourceCode(),thirdSportMarketMessage.getId());
                thirdSportMarketMessage.setThirdSportMarketOddsList(new ArrayList<>());
                if(!CollectionUtils.isEmpty(thirdSportMarketOddsList)){
                    thirdSportMarketMessage.getThirdSportMarketOddsList().addAll(thirdSportMarketOddsList);
                }
                thirdSportMarketMessages.add(thirdSportMarketMessage);
            });
        }
        //sendMessageToRisk(linkId,standardMatchInfo,thirdSportMarketMessages);
    }
    private List<ThirdMatchInfo> getThirdMatchInfoList(List<ThirdMatchInfo>  thirdMatchInfos,String dataSourceCode)
    {
        List<ThirdMatchInfo> thirdMatchInfoList = new ArrayList<>();
        thirdMatchInfos.forEach(e->{
            if (!e.getDataSourceCode().equalsIgnoreCase(dataSourceCode))
            {
                thirdMatchInfoList.add(e);
            }
        });
        return thirdMatchInfoList;
    }
    public void sendMessageToRisk(String linkId,StandardMatchInfo standardMatchInfo,List<ThirdSportMarketMessage> thirdSportMarketMessages,Long modifyTime)
    {
        if (!CollectionUtils.isEmpty(thirdSportMarketMessages))
        {
        	String key = Constant.REDIS_KEY.RONGHE_AO_MARKET_ORIGINAL_ODDS+standardMatchInfo.getId();
            Map<String,Integer> oddsMap = redisService.hGetAllBasedBucket(key, ConstantSystem.BUCKET_QUANTITY_EIGHT);
        	if(oddsMap == null) {
        		oddsMap = new HashMap<String,Integer>();
        	}
            for (ThirdSportMarketMessage thirdSportMarketMessage : thirdSportMarketMessages)
            {
                thirdSportMarketMessage.setRelationMarketId(thirdSportMarketService.getRelationMarketId(linkId,standardMatchInfo.getId(),thirdSportMarketMessage.getMarketCategoryId(),
                        thirdSportMarketMessage.getAddition1(),thirdSportMarketMessage.getAddition2(),thirdSportMarketMessage.getAddition3(),thirdSportMarketMessage.getAddition4(),thirdSportMarketMessage.getAddition5(),
                        thirdSportMarketMessage.getMarketType(),thirdSportMarketMessage.getThirdMarketSourceId()));
                thirdSportMarketMessage.setReferenceId(standardMatchInfo.getId());
                if (!CollectionUtils.isEmpty(thirdSportMarketMessage.getThirdSportMarketOddsList()))
                {
                    for(ThirdSportMarketOdds e:thirdSportMarketMessage.getThirdSportMarketOddsList()){
                        e.setMarketId(thirdSportMarketMessage.getRelationMarketId());
                        e.setId(thirdSportMarketOddsService.getRelationMarketOddsId(thirdSportMarketMessage.getRelationMarketId(),e.getOddsType(),e.getThirdOddsFieldSourceId(),e.getAddition1(),thirdSportMarketMessage.getMarketCategoryId()));
                        //缓存 AO原始赔率
                        if(DataSourceCodeEnum.AO.code.equals(thirdSportMarketMessage.getDataSourceCode()) && 
                        		MarginCategoryConfig.FootBall_MAIN_CATEGORY.contains(thirdSportMarketMessage.getMarketCategoryId())) {
                        	oddsMap.put(e.getId().toString(), e.getOriginalOddsValue());
                        }
                    }
                }
            }
            redisService.hSetAllBasedBucket(key, ConstantSystem.BUCKET_QUANTITY_EIGHT, oddsMap, baseProcessor.marketCacheTime(standardMatchInfo.getBeginTime()));
            thirdSportMarketMergeProducer.sendThirdSportMarketMessageToMQ(linkId,standardMatchInfo,thirdSportMarketMessages,modifyTime);
        }
    }
}
