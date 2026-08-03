package com.panda.merge.rocketmq.common;

import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.cache.CommonItem;
import com.panda.merge.cache.FootballCacheScores;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.CategoryOppositeConfig;
import com.panda.merge.constant.ConstantSystem;
import com.panda.merge.constant.MarginCategoryConfig;
import com.panda.merge.dto.ThirdMarketCategoryFieldDetail;
import com.panda.merge.dto.message.ThirdSportMarketMessage;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.model.ThirdSportMarket;
import com.panda.merge.model.ThirdSportMarketOdds;
import com.panda.merge.rocketmq.producer.ThirdSportMarketMergeProducer;
import com.panda.merge.service.ThirdMarketCategoryFieldService;
import com.panda.merge.service.ThirdMatchInfoService;
import com.panda.merge.service.ThirdSportMarketOddsService;
import com.panda.merge.service.ThirdSportMarketService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.panda.merge.constant.ConstantSystem.ONE;

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

    @Autowired
    private ThirdMarketCategoryFieldService thirdMarketCategoryFieldService;

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

    /**
     * 生成统一盘口id
     * @param linkId
     * @param standardMatchInfo
     * @param thirdSportMarketMessages
     * @param modifyTime
     */
    public void sendMessageToRisk(String linkId,StandardMatchInfo standardMatchInfo,List<ThirdSportMarketMessage> thirdSportMarketMessages,Long modifyTime,ThirdMatchInfo thirdMatchInfo)
    {
        if (!CollectionUtils.isEmpty(thirdSportMarketMessages))
        {
            //AO初盘第一条赔率
        	String key = Constant.REDIS_KEY.RONGHE_AO_MARKET_ORIGINAL_ODDS+standardMatchInfo.getId();
            Map<String,Integer> oddsMap = redisService.hGetAllBasedBucket(key, ConstantSystem.BUCKET_QUANTITY_EIGHT);
            //生成盘口id
            for (ThirdSportMarketMessage thirdSportMarketMessage : thirdSportMarketMessages)
            {
                //37510:百家赔T01主客队相反处理
                if(StandardSportTypeEnum.FootBall.code.equals(standardMatchInfo.getSportId()) ){
                    if(  DataSourceCodeEnum.TX.code.equals(standardMatchInfo.getDataSourceCode()) ){
                        //TX让球比分处理
                        txHandicapDispose(linkId,thirdSportMarketMessage.getMarketCategoryId(),standardMatchInfo,thirdSportMarketMessage,thirdMatchInfo);
                    }
                    //主客队相反盘口、投注项相关内容处理
                    if(ONE.equals(thirdMatchInfo.getHomeAwayOpposite())){
                        //主客相反三方盘口内容替换
                        changeThirdMarketContent(linkId,thirdSportMarketMessage);
                        //主客相反改变投注项内容
                        changeThirdMarketOddsContent(linkId,thirdSportMarketMessage.getThirdSportMarketOddsList(),thirdSportMarketMessage);
                    }
                }
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


    /**
     * 主客相反三方盘口内容替换
     *
     * @param linkId
     * @param thirdSportMarket
     */
    public void changeThirdMarketContent(String linkId, ThirdSportMarketMessage thirdSportMarket) {
        log.info("::{}::changeThirdMarketContent, 标准玩法id:{}，addition1:{},addition2:{},addition3:{},addition4:{},", linkId, thirdSportMarket.getMarketCategoryId(), thirdSportMarket.getAddition1(), thirdSportMarket.getAddition2(), thirdSportMarket.getAddition3(), thirdSportMarket.getAddition4());
        if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_1.containsKey(thirdSportMarket.getMarketCategoryId())) {
            thirdSportMarket.setMarketCategoryId(CategoryOppositeConfig.FootBall.CATEGORY_TYPE_1.get(thirdSportMarket.getMarketCategoryId()));
        }
        if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_2.contains(thirdSportMarket.getMarketCategoryId())) {
            String add1 = thirdSportMarket.getAddition1().contains("-") ? thirdSportMarket.getAddition1().replace("-", "") : "-" + thirdSportMarket.getAddition1();
            thirdSportMarket.setAddition1(add1);
        }
        if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_3.contains(thirdSportMarket.getMarketCategoryId())) {
            String add2 = thirdSportMarket.getAddition2().contains("-") ? thirdSportMarket.getAddition2().replace("-", "") : "-" + thirdSportMarket.getAddition2();
            thirdSportMarket.setAddition2(add2);
        }
        if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_4.contains(thirdSportMarket.getMarketCategoryId())) {
            String add3 = thirdSportMarket.getAddition3();
            String add4 = thirdSportMarket.getAddition4();
            thirdSportMarket.setAddition3(add4);
            thirdSportMarket.setAddition4(add3);
        }
        if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_6.contains(thirdSportMarket.getMarketCategoryId())) {
            String add1 = thirdSportMarket.getAddition1();
            String add2 = thirdSportMarket.getAddition2();
            thirdSportMarket.setAddition1(add2);
            thirdSportMarket.setAddition2(add1);
        }
        if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_10.contains(thirdSportMarket.getMarketCategoryId())) {
            String add3 = thirdSportMarket.getAddition3();
            String add4 = thirdSportMarket.getAddition4();
            thirdSportMarket.setAddition3(add4);
            thirdSportMarket.setAddition4(add3);
        }
    }

    /**
     * 主客相反改变投注项内容
     *
     * @param linkId
     * @param thirdSportMarketOddsList
     * @param thirdSportMarket
     */
    public void changeThirdMarketOddsContent(String linkId, List<ThirdSportMarketOdds> thirdSportMarketOddsList, ThirdSportMarketMessage thirdSportMarket) {
        if (CollectionUtils.isEmpty(thirdSportMarketOddsList)) {
            log.info("::{}:: changeThirdMarketOddsContent fail, thirdSportMarketOddsList is Empty", linkId);
            return;
        }
        Map<String, Long> oddsFieldTemplateMap = new HashMap<>();
        Map<String, String> thirdTemplateSourceIdMap = new HashMap<>();
        Map<String, String> oddsNameMap = new HashMap<>();
        for (ThirdSportMarketOdds thirdSportMarketOdds : thirdSportMarketOddsList) {
            thirdTemplateSourceIdMap.put(thirdSportMarketOdds.getOddsType(), thirdSportMarketOdds.getThirdTemplateSourceId());
            oddsNameMap.put(thirdSportMarketOdds.getOddsType(), thirdSportMarketOdds.getName());
            oddsFieldTemplateMap.put(thirdSportMarketOdds.getOddsType(), thirdSportMarketOdds.getOddsFieldsTemplateId());
        }
        if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_1.containsKey(thirdSportMarket.getMarketCategoryId())) {
            List<ThirdMarketCategoryFieldDetail> thirdMarketCategoryFieldDetails = thirdMarketCategoryFieldService.queryThirdMarketCategoryFieldDetail(thirdSportMarket.getDataSourceCode(), thirdSportMarket.getMarketCategoryId());
            if (!CollectionUtils.isEmpty(thirdMarketCategoryFieldDetails)) {
                Map<String, Long> longMap = thirdMarketCategoryFieldDetails.stream().collect(Collectors.toMap(ThirdMarketCategoryFieldDetail::getOddsName, ThirdMarketCategoryFieldDetail::getReferenceId));
                Map<String, String> stringMap = thirdMarketCategoryFieldDetails.stream().collect(Collectors.toMap(ThirdMarketCategoryFieldDetail::getOddsName, ThirdMarketCategoryFieldDetail::getThirdSourceId));
                for (ThirdSportMarketOdds thirdSportMarketOdds : thirdSportMarketOddsList) {
                    thirdSportMarketOdds.setOddsFieldsTemplateId(longMap.get(thirdSportMarketOdds.getOddsType().toLowerCase()));
                    thirdSportMarketOdds.setThirdTemplateSourceId(stringMap.get(thirdSportMarketOdds.getOddsType().toLowerCase()));
                }
            }
        }
        for (ThirdSportMarketOdds thirdSportMarketOdds : thirdSportMarketOddsList) {
            /*log.info("::{}::changeThirdMarketContent, 标准玩法id:{}，addition1:{},addition2:{},addition3:{},addition4:{},", linkId, thirdSportMarket.getMarketCategoryId(),
                    thirdSportMarketOdds.getAddition1(),thirdSportMarketOdds.getAddition2(),thirdSportMarketOdds.getAddition3(),thirdSportMarketOdds.getAddition4());*/
            if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_7.contains(thirdSportMarket.getMarketCategoryId())) {
                String add1 = thirdSportMarketOdds.getAddition1();
                String add2 = thirdSportMarketOdds.getAddition2();
                thirdSportMarketOdds.setAddition1(add2);
                thirdSportMarketOdds.setAddition2(add1);
            }
            if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_8.contains(thirdSportMarket.getMarketCategoryId())) {
                String add3 = thirdSportMarketOdds.getAddition3();
                String add4 = thirdSportMarketOdds.getAddition4();
                thirdSportMarketOdds.setAddition3(add4);
                thirdSportMarketOdds.setAddition4(add3);
            }
            if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_5.contains(thirdSportMarket.getMarketCategoryId())) {
                if (thirdSportMarket.getMarketCategoryId() == 104L) {
                    if (CategoryOppositeConfig.FootBall.CATEGORY_ODDS_TYPE_CHANGE_104.containsKey(thirdSportMarketOdds.getOddsType())) {
                        String oddsType = thirdSportMarketOdds.getOddsType();
                        thirdSportMarketOdds.setOddsType(CategoryOppositeConfig.FootBall.CATEGORY_ODDS_TYPE_CHANGE_104.get(oddsType));
                        thirdSportMarketOdds.setThirdTemplateSourceId(thirdTemplateSourceIdMap.get(thirdSportMarketOdds.getOddsType()));
                        thirdSportMarketOdds.setOddsFieldsTemplateId(oddsFieldTemplateMap.get(thirdSportMarketOdds.getOddsType()));
                        thirdSportMarketOdds.setName(oddsNameMap.get(thirdSportMarketOdds.getOddsType()));
                    }
                } else if (thirdSportMarket.getMarketCategoryId() == 103L) {
                    String str1 = (thirdSportMarketOdds.getAddition1() == null || thirdSportMarketOdds.getAddition1().contains("+")) ? thirdSportMarketOdds.getAddition1() : thirdSportMarketOdds.getAddition1() + ":" + thirdSportMarketOdds.getAddition2();
                    String str2 = (thirdSportMarketOdds.getAddition3() == null || thirdSportMarketOdds.getAddition3().contains("+")) ? thirdSportMarketOdds.getAddition3() : thirdSportMarketOdds.getAddition3() + ":" + thirdSportMarketOdds.getAddition4();
                    thirdSportMarketOdds.setOddsType(str1 + " " + str2);
                    thirdSportMarketOdds.setName(oddsNameMap.get(thirdSportMarketOdds.getOddsType()));
                    thirdSportMarketOdds.setThirdTemplateSourceId(thirdTemplateSourceIdMap.get(thirdSportMarketOdds.getOddsType()));
                    thirdSportMarketOdds.setOddsFieldsTemplateId(oddsFieldTemplateMap.get(thirdSportMarketOdds.getOddsType()));
                } else {
                    if (CategoryOppositeConfig.FootBall.CATEGORY_ODDS_TYPE_CHANGE.containsKey(thirdSportMarketOdds.getOddsType())) {
                        String oddsType = thirdSportMarketOdds.getOddsType();
                        thirdSportMarketOdds.setOddsType(CategoryOppositeConfig.FootBall.CATEGORY_ODDS_TYPE_CHANGE.get(oddsType));
                        thirdSportMarketOdds.setThirdTemplateSourceId(thirdTemplateSourceIdMap.get(thirdSportMarketOdds.getOddsType()));
                        thirdSportMarketOdds.setOddsFieldsTemplateId(oddsFieldTemplateMap.get(thirdSportMarketOdds.getOddsType()));
                        thirdSportMarketOdds.setName(oddsNameMap.get(thirdSportMarketOdds.getOddsType()));
                    } else {
                        if (thirdSportMarketOdds.getOddsType().contains(":")) {
                            String[] strArr = thirdSportMarketOdds.getOddsType().split(":");
                            if (strArr.length == 2) {
                                thirdSportMarketOdds.setOddsType(strArr[1] + ":" + strArr[0]);
                                thirdSportMarketOdds.setName(oddsNameMap.get(thirdSportMarketOdds.getOddsType()));
                                thirdSportMarketOdds.setOddsFieldsTemplateId(oddsFieldTemplateMap.get(thirdSportMarketOdds.getOddsType()));
                                thirdSportMarketOdds.setThirdTemplateSourceId(thirdTemplateSourceIdMap.get(thirdSportMarketOdds.getOddsType()));
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * 1.TX让球比分处理 addition3:主队比分 addition4:客队比分
     * 2.计算全场盘口值 、替换三方盘口源ID
     *
     * @param linkId
     * @param marketCategoryId
     * @param standardMatchInfo
     * @param thirdMarketDTO
     * @param thirdMatchInfo
     */
    public void txHandicapDispose(String linkId, Long marketCategoryId, StandardMatchInfo standardMatchInfo, ThirdSportMarketMessage thirdMarketDTO, ThirdMatchInfo thirdMatchInfo) {
            String addition3 = thirdMarketDTO.getAddition3();
            String addition4 = thirdMarketDTO.getAddition4();
            if ("0".equals(addition3) && "0".equals(addition4)) {
                //获取比分中心提供主客队比分
                CommonItem goalObj = getFootballCacheScores(linkId, standardMatchInfo, marketCategoryId);
                if (goalObj == null) {
                    return;
                }
                Integer goalHome = goalObj.getHome();
                Integer goalAway = goalObj.getAway();
                //全场盘口值 = 基准分盘口值 - (主队比分 - 客队比分)
                String addition2 = Double.toString(Double.valueOf(thirdMarketDTO.getAddition1()) - (goalHome - goalAway)).replace(".0", "");
                //三方盘口替换
                String thirdCategoryId = thirdMarketDTO.getThirdMarketSourceId().split("_")[1];
                String calculateThirdMarketSourceId = thirdMatchInfo.getThirdMatchSourceId() + "_" + thirdCategoryId + "_" + addition2 + "_" + thirdMarketDTO.getOfferLineId();
                log.info("::{}::标准赛事ID:{},TX主客队比分更换,源主队比分:{},源客队比分:{},源三方源盘口id:{},源基准分盘口值:{},主队比分:{},客队比分:{},计算出三方源盘口id:{},计算出全场盘口值:{},标准玩法ID:{},比分:{}", linkId, standardMatchInfo.getId(), addition3, addition4, thirdMarketDTO.getThirdMarketSourceId(), thirdMarketDTO.getAddition1(), goalHome, goalAway, calculateThirdMarketSourceId, addition2, marketCategoryId, goalObj);
                thirdMarketDTO.setThirdMarketSourceId(calculateThirdMarketSourceId);
                thirdMarketDTO.setAddition2(addition2);
                thirdMarketDTO.setAddition3(String.valueOf(goalHome));
                thirdMarketDTO.setAddition4(String.valueOf(goalAway));
                //第三方投注项原始ID替换
                List<ThirdSportMarketOdds> thirdMarketOddsList = thirdMarketDTO.getThirdSportMarketOddsList();
                if (!CollectionUtils.isEmpty(thirdMarketOddsList)) {
                    thirdMarketOddsList.forEach(o -> {
                        //2048839_33_0_1_1
                        String thirdOddsFieldSourceId = o.getThirdOddsFieldSourceId();
                        if (StringUtils.isNotEmpty(thirdOddsFieldSourceId)) {
                            String[] split = thirdOddsFieldSourceId.split("_");
                            String thirdOddsFieldSourceIdStr = split[0] + "_" + split[1] + "_" + addition2 + "_" + split[3] + "_" + split[4];
                            o.setThirdOddsFieldSourceId(thirdOddsFieldSourceIdStr);
                            log.info("::{}::标准赛事ID:{},TX让球三方投注项原始ID替换,计算后三方源盘口id:{},替换前投注项原始ID:{},替换后投注项原始ID:{}", linkId, standardMatchInfo.getId(), calculateThirdMarketSourceId, thirdOddsFieldSourceId, thirdOddsFieldSourceIdStr);
                        }
                    });
                }
            }
    }

    /**
     * 足球获取缓存比分
     *
     * @param linkId
     * @param standardMatchInfo
     * @param marketCategoryId
     */
    private CommonItem getFootballCacheScores(String linkId, StandardMatchInfo standardMatchInfo, Long marketCategoryId) {
        //获取比分中心提供主客队比分
        FootballCacheScores scores = new FootballCacheScores();
        Object scoreObj = redisService.get(DigestUtil.md5Hex(Constant.REDIS_KEY.STANDARD_MATCH_SCORES+ standardMatchInfo.getId()));
        if (!Objects.isNull(scoreObj)) {
            scores = JSONObject.parseObject(scoreObj.toString(), FootballCacheScores.class);
            if (scores.getGoal() == null) {
                scores.setGoal(new CommonItem(0, 0));
            }
            if (scores.getGoalOverTime() == null) {
                scores.setGoalOverTime(new CommonItem(0, 0));
            }
            if (scores.getGoalPenalty() == null) {
                scores.setGoalPenalty(new CommonItem(0, 0));
            }
            if (scores.getRedCard() == null) {
                scores.setRedCard(new CommonItem(0, 0));
            }
            if (scores.getYellowCard() == null) {
                scores.setYellowCard(new CommonItem(0, 0));
            }
        } else {
            scores = preScoreBuild();
        }
        CommonItem goalObj = new CommonItem();
        if (MarginCategoryConfig.FOOTBALL_SCORE_CATEGORY.contains(marketCategoryId)) {
            //常规进球玩法
            goalObj = scores.getGoal();
        } else if (MarginCategoryConfig.FOOTBALL_OVERTIME_SCORE_CATEGORY.contains(marketCategoryId)) {
            //加时赛比分
            goalObj = scores.getGoalOverTime();
        } else if (MarginCategoryConfig.FOOTBALL_PENALTY_SCORE_CATEGORY.contains(marketCategoryId)) {
            //点球大战比分
            goalObj = scores.getGoalPenalty();
        } else if (MarginCategoryConfig.FOOTBALL_RAD_SCORE_CATEGORY.contains(marketCategoryId)) {
            //罚牌比分处理
            Integer home = scores.getRedCard().getHome() * 2 + scores.getYellowCard().getHome();
            Integer away = scores.getRedCard().getAway() * 2 + scores.getYellowCard().getAway();
            scores.setFaCard(new CommonItem(home, away));
            goalObj = scores.getFaCard();
        } else if (MarginCategoryConfig.FOOTBALL_YELLOW_SCORE_CATEGORY.contains(marketCategoryId)) {
            //黄牌比分处理
            goalObj = scores.getYellowCard();
        } else {
            return null;
        }
        return goalObj;
    }
    /**
     * 赛前盘比分中心没有比分 需要自己构建 0 比分
     */
    private static FootballCacheScores preScoreBuild() {
        FootballCacheScores footballCacheScores = new FootballCacheScores();
        footballCacheScores.setCorner(new CommonItem(0, 0));
        footballCacheScores.setGoal(new CommonItem(0, 0));
        footballCacheScores.setGoalOverTime(new CommonItem(0, 0));
        footballCacheScores.setGoalPenalty(new CommonItem(0, 0));
        footballCacheScores.setRedCard(new CommonItem(0, 0));
        footballCacheScores.setYellowCard(new CommonItem(0, 0));
        return footballCacheScores;
    }
}
