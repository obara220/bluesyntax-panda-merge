package com.panda.merge.util;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.dto.DiscountOddsConfigDTO;
import com.panda.merge.dto.message.StandardMarketMessage;
import com.panda.merge.dto.message.StandardMarketOddsMessage;
import com.panda.merge.dto.message.StandardMatchMarketMessage;
import com.panda.merge.enums.OddsCalcCategoryGroupEnum;
import com.panda.merge.model.ConfigMarketLevel;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class CalculateOddsUtils {
    /**
     * Map<String,StandardMatchMarketMessage>  String:原topic，StandardMatchMarketMessage：原topic对应的赔率
     * @param configs
     * @param standardMatchMarketMessage
     * @param matchTradType
     * @return
     */
    public static void calculateOddsByMatchLevel(List<ConfigMarketLevel> configs, StandardMatchMarketMessage standardMatchMarketMessage, boolean matchTradType, Map<String, DiscountOddsConfigDTO> discountOddsConfigDTOMap,Map<String, Integer> originalOddsValueMap)
    {
        Map<String,StandardMatchMarketMessage> map = new HashMap<>();
        //pa操盘
        if (!matchTradType)
        {
            //盘口类型细分：21：常规玩法，22：50/50玩法，31：1.0-2.0 32：2.01-5.0 33：5.01-10.0
            if (!ObjectUtil.isEmpty(configs) && !CollectionUtils.isEmpty(standardMatchMarketMessage.getMarketList()))
            {
                boolean isPreMarketType = standardMatchMarketMessage.getMarketList().get(0).getMarketType() == 1;
                Map<Integer, List<ConfigMarketLevel>> configsMap = configs.stream().collect(Collectors.groupingBy(ConfigMarketLevel::getLevel));
                for(Map.Entry<Integer,List<ConfigMarketLevel>> entry : configsMap.entrySet())
                {
                    List<ConfigMarketLevel> configsValue = entry.getValue();
                    if (!CollectionUtils.isEmpty(configsValue))
                    {
                        //log.info("::{}::开始组装标准赔率消息并下发,信用等级:{},联赛等级:{},运动种类：{},开始计算", linkId, entry.getKey(),level,standardMatchInfo.getSportId());
                        ConfigMarketLevel cnormal = new ConfigMarketLevel();
                        ConfigMarketLevel c50 = new ConfigMarketLevel();
                        ConfigMarketLevel cthree1 = new ConfigMarketLevel();
                        ConfigMarketLevel cthree2 = new ConfigMarketLevel();
                        ConfigMarketLevel cthree3 = new ConfigMarketLevel();
                        for (ConfigMarketLevel c : configsValue)
                        {
                            if (c.getDiffValue() == null)
                            {
                                c.setDiffValue(0.0);
                            }
                            if (c.getMarketTypeDetail() == 21)
                            {
                                cnormal = c;
                            }
                            if (c.getMarketTypeDetail() == 22)
                            {
                                c50 = c;
                            }
                            if (c.getMarketTypeDetail() == 31)
                            {
                                cthree1 = c;
                            }
                            if (c.getMarketTypeDetail() == 32)
                            {
                                cthree2 = c;
                            }
                            if (c.getMarketTypeDetail() == 33)
                            {
                                cthree3 = c;
                            }
                        }
                        ConfigMarketLevel finalCnormal = cnormal;
                        ConfigMarketLevel finalC5 = c50;
                        ConfigMarketLevel finalCthree = cthree1;
                        ConfigMarketLevel finalCthree1 = cthree2;
                        ConfigMarketLevel finalCthree2 = cthree3;
                        /*log.info("::{}::开始组装标准赔率消息并下发,联赛等级:{},运动种类：{},开始计算，finalCnormal:{},finalC5:{},finalCthree:{},finalCthree1:{},finalCthree2:{}",
                                linkId, level,standardMatchInfo.getSportId(),JSON.toJSON(finalCnormal),JSON.toJSON(finalC5),JSON.toJSON(finalCthree),JSON.toJSON(finalCthree1),JSON.toJSON(finalCthree2));*/
                        standardMatchMarketMessage.getMarketList().forEach(e-> {
                            if (!e.getStatus().equals(Constant.SPORT_MARKET.STATUS.DEACTIVATED) && !CollectionUtils.isEmpty(e.getMarketOddsList()))
                            {
                                //初始化分组盘口数据，优惠赔率分组数据
                                initOddsMap(e.getMarketOddsList(), discountOddsConfigDTOMap,isPreMarketType,originalOddsValueMap,standardMatchMarketMessage.getSportId());
                                //分组计算开始
                                if (CalculateOddsCategoryConfig.NORMAL_CATEGORY.contains(e.getMarketCategoryId()) && (CalculateOddsCategoryConfig.SOPRT_TYPE.contains(standardMatchMarketMessage.getSportId())?standardMatchMarketMessage.getSportId():-1)==(configs.get(0).getSportId()))
                                {
                                    NORMAL_CATEGORY(e.getMarketOddsList(), discountOddsConfigDTOMap, finalCnormal, entry.getKey(),isPreMarketType);
                                }
                                else if (CalculateOddsCategoryConfig.CATEGORY_50.contains(e.getMarketCategoryId()) &&  (CalculateOddsCategoryConfig.SOPRT_TYPE.contains(standardMatchMarketMessage.getSportId())?standardMatchMarketMessage.getSportId():-1)==(configs.get(0).getSportId()))
                                {
                                    CATEGORY_50(e.getMarketOddsList(), discountOddsConfigDTOMap, finalC5, entry.getKey(),isPreMarketType);
                                }
                                else if (CalculateOddsCategoryConfig.THREE_ODDS_CATEGORY.contains(e.getMarketCategoryId()) &&  (CalculateOddsCategoryConfig.SOPRT_TYPE.contains(standardMatchMarketMessage.getSportId())?standardMatchMarketMessage.getSportId():-1)==(configs.get(0).getSportId()))
                                {
                                    THREE_ODDS_CATEGORY(e.getMarketOddsList(), discountOddsConfigDTOMap, finalCthree, finalCthree1, finalCthree2, entry.getKey(),isPreMarketType);
                                }
                                else if (e.getMarketOddsList().size() == 2)
                                {
                                    NORMAL_CATEGORY(e.getMarketOddsList(), discountOddsConfigDTOMap, finalCnormal, entry.getKey(),isPreMarketType);
                                }
                                else if (e.getMarketOddsList().size() >= 3)
                                {
                                    THREE_ODDS_CATEGORY(e.getMarketOddsList(), discountOddsConfigDTOMap, finalCthree, finalCthree1, finalCthree2, entry.getKey(),isPreMarketType);
                                }
                            }
                        });
                    }
                }
            }
        }
    }

    /**
     * 赔率分组计算v1 玩法分组动态配置
     * @param configs
     * @param standardMatchMarketMessage
     * @param oddsCalcCategoryMap
     * @param matchTradType
     * @param discountOddsConfigDTOMap
     */
    public static void calculateOddsByMatchLevelV1(List<ConfigMarketLevel> configs,
                                                 StandardMatchMarketMessage standardMatchMarketMessage,
                                                 Map<Long, Integer> oddsCalcCategoryMap, boolean matchTradType,
                                                 Map<String, DiscountOddsConfigDTO> discountOddsConfigDTOMap,
                                                   Map<String, Integer> originalOddsValueMap)
    {
        Map<String,StandardMatchMarketMessage> map = new HashMap<>();
        //pa操盘
        if (!matchTradType)
        {
            //盘口类型细分：21：常规玩法，22：50/50玩法，31：1.0-2.0 32：2.01-5.0 33：5.01-10.0
            if (!ObjectUtil.isEmpty(configs) && !CollectionUtils.isEmpty(standardMatchMarketMessage.getMarketList()))
            {
                boolean isPreMarketType = standardMatchMarketMessage.getMarketList().get(0).getMarketType() == 1;
                Map<Integer, List<ConfigMarketLevel>> configsMap = configs.stream().collect(Collectors.groupingBy(ConfigMarketLevel::getLevel));
                for(Map.Entry<Integer,List<ConfigMarketLevel>> entry : configsMap.entrySet())
                {
                    List<ConfigMarketLevel> configsValue = entry.getValue();
                    if (!CollectionUtils.isEmpty(configsValue))
                    {
                        //log.info("::{}::开始组装标准赔率消息并下发,信用等级:{},联赛等级:{},运动种类：{},开始计算", linkId, entry.getKey(),level,standardMatchInfo.getSportId());
                        ConfigMarketLevel cnormal = new ConfigMarketLevel();
                        ConfigMarketLevel c50 = new ConfigMarketLevel();
                        ConfigMarketLevel cthree1 = new ConfigMarketLevel();
                        ConfigMarketLevel cthree2 = new ConfigMarketLevel();
                        ConfigMarketLevel cthree3 = new ConfigMarketLevel();
                        for (ConfigMarketLevel c : configsValue)
                        {
                            if (c.getDiffValue() == null)
                            {
                                c.setDiffValue(0.0);
                            }
                            if (c.getMarketTypeDetail() == 21)
                            {
                                cnormal = c;
                            }
                            if (c.getMarketTypeDetail() == 22)
                            {
                                c50 = c;
                            }
                            if (c.getMarketTypeDetail() == 31)
                            {
                                cthree1 = c;
                            }
                            if (c.getMarketTypeDetail() == 32)
                            {
                                cthree2 = c;
                            }
                            if (c.getMarketTypeDetail() == 33)
                            {
                                cthree3 = c;
                            }
                        }
                        ConfigMarketLevel finalCnormal = cnormal;
                        ConfigMarketLevel finalC5 = c50;
                        ConfigMarketLevel finalCthree = cthree1;
                        ConfigMarketLevel finalCthree1 = cthree2;
                        ConfigMarketLevel finalCthree2 = cthree3;
                        /*log.info("::{}::开始组装标准赔率消息并下发,联赛等级:{},运动种类：{},开始计算，finalCnormal:{},finalC5:{},finalCthree:{},finalCthree1:{},finalCthree2:{}",
                                linkId, level,standardMatchInfo.getSportId(),JSON.toJSON(finalCnormal),JSON.toJSON(finalC5),JSON.toJSON(finalCthree),JSON.toJSON(finalCthree1),JSON.toJSON(finalCthree2));*/
                        standardMatchMarketMessage.getMarketList().forEach(e-> {
                            if (!e.getStatus().equals(Constant.SPORT_MARKET.STATUS.DEACTIVATED) && !CollectionUtils.isEmpty(e.getMarketOddsList()))
                            {
                                //初始化分组盘口数据，优惠赔率分组数据
                                initOddsMap(e.getMarketOddsList(), discountOddsConfigDTOMap,isPreMarketType,originalOddsValueMap,standardMatchMarketMessage.getSportId());
                                //分组计算开始
                                switch (getCategoryGroup(oddsCalcCategoryMap, e)) {
                                    case GROUP_NORMAL:
                                        NORMAL_CATEGORY(e.getMarketOddsList(), discountOddsConfigDTOMap, finalCnormal,
                                                        entry.getKey(), isPreMarketType);
                                        break;
                                    case GROUP_50:
                                        CATEGORY_50(e.getMarketOddsList(), discountOddsConfigDTOMap, finalC5,
                                                    entry.getKey(), isPreMarketType);
                                        break;
                                    case GROUP_THREE_ODDS:
                                        THREE_ODDS_CATEGORY(e.getMarketOddsList(), discountOddsConfigDTOMap,
                                                            finalCthree, finalCthree1, finalCthree2, entry.getKey(),
                                                            isPreMarketType);
                                        break;
                                    default:
                                        break;
                                }

                            }
                        });
                    }
                }
            }
        }
    }

    /**
     * 初始化赔率map,优惠盘口数据
     * @param standardMarketOddsMessageList
     * @param discountOddsConfigDTOMap
     */
    private static void initOddsMap(List<StandardMarketOddsMessage> standardMarketOddsMessageList, Map<String, DiscountOddsConfigDTO> discountOddsConfigDTOMap,boolean isPreMarketType,Map<String, Integer> originalOddsValueMap,Long sportId){
        standardMarketOddsMessageList.forEach(y->{
            if (MapUtil.isEmpty(y.getOddsMap()))
            {
                y.setOddsMap(new HashMap<>());
            }
            if (isPreMarketType && MapUtil.isNotEmpty(discountOddsConfigDTOMap) && discountOddsConfigDTOMap.containsKey(y.getId().toString()) && y.getPaOddsValue()!=null){
                DiscountOddsConfigDTO discountOddsConfigDTO = discountOddsConfigDTOMap.get(y.getId().toString());
                y.setDiscountCreateTime(discountOddsConfigDTO.getCreateTime());
                y.setDiscountStatus(discountOddsConfigDTO.getStatus());
                y.setCard(discountOddsConfigDTO.getCard());
                if (!discountOddsConfigDTO.getStatus().equals(Constant.SPORT_MARKET.DISCOUNT_STATUS.DEACTIVATED)
                        && discountOddsConfigDTO.getScale()!=null
                        && new BigDecimal(1).compareTo(discountOddsConfigDTO.getScale()) > 0){
                    y.setDiscountOdds(processOddsValueDecimals(((new BigDecimal(y.getPaOddsValue()).divide(new BigDecimal(1).subtract(discountOddsConfigDTO.getScale()),0, RoundingMode.HALF_UP)).intValue()),originalOddsValueMap,y.getId(),sportId));
                   // y.setDiscountOdds((new BigDecimal(y.getPaOddsValue()).divide(new BigDecimal(1).subtract(discountOddsConfigDTO.getScale()),0, RoundingMode.HALF_UP)).intValue());
                }
            }
        });
    }

    public static Integer processOddsValueDecimals(Integer discountOdds,Map<String, Integer> originalOddsValueMap,Long oddsId,Long sportId) {
        //在做优化前，先判断跟A01原始赔率的大小做校验
        if (originalOddsValueMap.containsKey(oddsId.toString()) && discountOdds>= originalOddsValueMap.get(oddsId.toString()) && sportId == 1L){
            discountOdds = originalOddsValueMap.get(oddsId.toString());
        }
        //使用数据源抽水赔，走赔率优化
        try {
            if (null == discountOdds || 0 == discountOdds) {
                return discountOdds;
            }
            BigDecimal bigDecimal = new BigDecimal(discountOdds).divide(new BigDecimal(100000), 2, BigDecimal.ROUND_DOWN);
            int left = bigDecimal.intValue();
            int right = bigDecimal.subtract(new BigDecimal(left)).multiply(new BigDecimal(100)).intValue();
            Integer paOddsValue = 0;
            if (left >= 3 && left < 5) {
                if (right < 5) {
                    paOddsValue = bigDecimal.intValue() * 100000;
                } else {
                    BigDecimal divide = new BigDecimal(right).divide(new BigDecimal(5), 0, BigDecimal.ROUND_DOWN).multiply(new BigDecimal(0.05));
                    paOddsValue = new BigDecimal(left).add(divide).multiply(new BigDecimal(100000)).intValue();
                }
            } else if (left >= 5 && left < 10) {
                if (right < 10) {
                    paOddsValue = bigDecimal.intValue() * 100000;
                } else {
                    BigDecimal divide = new BigDecimal(right).divide(new BigDecimal(100), 1, BigDecimal.ROUND_DOWN);
                    paOddsValue = new BigDecimal(left).add(divide).multiply(new BigDecimal(100000)).intValue();
                }
            } else if (left >= 10 && left < 20) {
                if (right < 50) {
                    paOddsValue = bigDecimal.intValue() * 100000;
                } else {
                    paOddsValue = new BigDecimal(left).add(new BigDecimal(0.5)).multiply(new BigDecimal(100000)).intValue();
                }
            } else if (left >= 20) {
                paOddsValue = left * 100000;
            }
            BigDecimal oneHundredThousand = new BigDecimal("100000");
            if (paOddsValue != 0) {
                return new BigDecimal(paOddsValue).divide(oneHundredThousand).setScale(2, BigDecimal.ROUND_DOWN).multiply(oneHundredThousand).intValue();
            } else {
                return bigDecimal.multiply(oneHundredThousand).intValue();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return discountOdds;
    }



    private static void CATEGORY_50(List<StandardMarketOddsMessage> standardMarketOddsMessageList, Map<String, DiscountOddsConfigDTO> discountOddsConfigDTOMap,ConfigMarketLevel finalC5,Integer level,boolean isPreMarketType)  {
        standardMarketOddsMessageList.forEach(y->{
            if (y.getPaOddsValue()+(finalC5.getDiffValue()*100000) <= 100000)
            {
                CalculateOdds calculateOdds = new CalculateOdds();
                calculateOdds.setActive(Constant.SPORT_MARKET.ODDS_STATUS.DEACTIVATED);
                calculateOdds.setPaOddsValue(y.getPaOddsValue());
                calculateOdds.setMalayOddsValue(arithmeticMALAY(y.getMalayOddsValue(), finalC5.getDiffValue()));
                calculateOdds.setDisOddsValue(getDisOddsValues(y, discountOddsConfigDTOMap, finalC5,isPreMarketType));
                y.getOddsMap().put(""+level,calculateOdds);
            }
            else
            {
                Double odds  = y.getPaOddsValue()+(finalC5.getDiffValue()*100000);
                CalculateOdds calculateOdds = new CalculateOdds();
                calculateOdds.setActive(y.getActive());
                calculateOdds.setPaOddsValue(odds.intValue());
                calculateOdds.setMalayOddsValue(arithmeticMALAY(y.getMalayOddsValue(), finalC5.getDiffValue()));
                calculateOdds.setDisOddsValue(getDisOddsValues(y, discountOddsConfigDTOMap, finalC5,isPreMarketType));
                y.getOddsMap().put(""+level,calculateOdds);
            }
        });
    }

    /**
     * 计算正常赔率
     * @param standardMarketOddsMessageList
     * @param discountOddsConfigDTOMap
     * @param finalNormal
     * @param level
     */
    private static void NORMAL_CATEGORY(List<StandardMarketOddsMessage> standardMarketOddsMessageList, Map<String, DiscountOddsConfigDTO> discountOddsConfigDTOMap,ConfigMarketLevel finalNormal,Integer level,boolean isPreMarketType){
        AtomicBoolean flag = new AtomicBoolean(false);
        standardMarketOddsMessageList.forEach(y->{
            if (y.getPaOddsValue()+(finalNormal.getDiffValue()*100000) <= 100000)
            {
                flag.set(true);
            }
            else
            {
                Double odds  = y.getPaOddsValue()+(finalNormal.getDiffValue()*100000);
                CalculateOdds calculateOdds = new CalculateOdds();
                calculateOdds.setActive(y.getActive());
                calculateOdds.setPaOddsValue(odds.intValue());
                calculateOdds.setMalayOddsValue(arithmeticMALAY(y.getMalayOddsValue(), finalNormal.getDiffValue()));
                calculateOdds.setDisOddsValue(getDisOddsValues(y, discountOddsConfigDTOMap, finalNormal,isPreMarketType));
                y.getOddsMap().put(""+level,calculateOdds);
            }
        });
        if (flag.get())
        {
            standardMarketOddsMessageList.forEach(y->{
                CalculateOdds calculateOdds = new CalculateOdds();
                calculateOdds.setActive(Constant.SPORT_MARKET.ODDS_STATUS.DEACTIVATED);
                calculateOdds.setPaOddsValue(y.getPaOddsValue());
                calculateOdds.setMalayOddsValue(y.getMalayOddsValue());
                calculateOdds.setDisOddsValue(getDisOddsValues(y, discountOddsConfigDTOMap, finalNormal,isPreMarketType));
                y.getOddsMap().put(""+level,calculateOdds);
            });
        }
    }

    /**
     * 计算多项盘赔率
     * @param standardMarketOddsMessageList
     * @param discountOddsConfigDTOMap
     * @param finalCthree
     * @param finalCthree1
     * @param finalCthree2
     * @param level
     */
    private static void THREE_ODDS_CATEGORY(List<StandardMarketOddsMessage> standardMarketOddsMessageList, Map<String, DiscountOddsConfigDTO> discountOddsConfigDTOMap,ConfigMarketLevel finalCthree,ConfigMarketLevel finalCthree1,ConfigMarketLevel finalCthree2,Integer level,boolean isPreMarketType) {
        standardMarketOddsMessageList.forEach(y->{
            if (y.getPaOddsValue()>= 100000 && y.getPaOddsValue()<=200000)
            {
                if (y.getPaOddsValue()+(finalCthree.getDiffValue()*100000) <= 100000)
                {
                    CalculateOdds calculateOdds = new CalculateOdds();
                    calculateOdds.setActive(Constant.SPORT_MARKET.ODDS_STATUS.DEACTIVATED);
                    calculateOdds.setPaOddsValue(y.getPaOddsValue());
                    calculateOdds.setMalayOddsValue(arithmeticMALAY(y.getMalayOddsValue(), finalCthree.getDiffValue()));
                    calculateOdds.setDisOddsValue(getDisOddsValues(y, discountOddsConfigDTOMap, finalCthree,isPreMarketType));
                    y.getOddsMap().put(""+level,calculateOdds);
                }
                else
                {
                    Double odds  = y.getPaOddsValue()+(finalCthree.getDiffValue()*100000);
                    CalculateOdds calculateOdds = new CalculateOdds();
                    calculateOdds.setActive(y.getActive());
                    calculateOdds.setPaOddsValue(odds.intValue());
                    calculateOdds.setMalayOddsValue(arithmeticMALAY(y.getMalayOddsValue(), finalCthree.getDiffValue()));
                    calculateOdds.setDisOddsValue(getDisOddsValues(y, discountOddsConfigDTOMap, finalCthree,isPreMarketType));
                    y.getOddsMap().put(""+level,calculateOdds);
                }
            }
            else if(y.getPaOddsValue()> 200000 && y.getPaOddsValue()<=500000)
            {
                if (y.getPaOddsValue()+(finalCthree1.getDiffValue()*100000) <= 100000)
                {
                    CalculateOdds calculateOdds = new CalculateOdds();
                    calculateOdds.setActive(Constant.SPORT_MARKET.ODDS_STATUS.DEACTIVATED);
                    calculateOdds.setPaOddsValue(y.getPaOddsValue());
                    calculateOdds.setMalayOddsValue(arithmeticMALAY(y.getMalayOddsValue(), finalCthree1.getDiffValue()));
                    calculateOdds.setDisOddsValue(getDisOddsValues(y, discountOddsConfigDTOMap, finalCthree1,isPreMarketType));
                    y.getOddsMap().put(""+level,calculateOdds);
                }
                else
                {
                    Double odds  = y.getPaOddsValue()+(finalCthree1.getDiffValue()*100000);
                    CalculateOdds calculateOdds = new CalculateOdds();
                    calculateOdds.setActive(y.getActive());
                    calculateOdds.setPaOddsValue(odds.intValue());
                    calculateOdds.setMalayOddsValue(arithmeticMALAY(y.getMalayOddsValue(), finalCthree1.getDiffValue()));
                    calculateOdds.setDisOddsValue(getDisOddsValues(y, discountOddsConfigDTOMap, finalCthree1,isPreMarketType));
                    y.getOddsMap().put(""+level,calculateOdds);
                }
            }
            else if(y.getPaOddsValue()> 500000 && y.getPaOddsValue()<=1000000)
            {
                if (y.getPaOddsValue()+(finalCthree2.getDiffValue()*100000) <= 100000)
                {
                    CalculateOdds calculateOdds = new CalculateOdds();
                    calculateOdds.setActive(Constant.SPORT_MARKET.ODDS_STATUS.DEACTIVATED);
                    calculateOdds.setPaOddsValue(y.getPaOddsValue());
                    calculateOdds.setMalayOddsValue(arithmeticMALAY(y.getMalayOddsValue(), finalCthree2.getDiffValue()));
                    calculateOdds.setDisOddsValue(getDisOddsValues(y, discountOddsConfigDTOMap, finalCthree2,isPreMarketType));
                    y.getOddsMap().put(""+level,calculateOdds);
                }
                else
                {
                    Double odds  = y.getPaOddsValue()+(finalCthree2.getDiffValue()*100000);
                    CalculateOdds calculateOdds = new CalculateOdds();
                    calculateOdds.setActive(y.getActive());
                    calculateOdds.setPaOddsValue(odds.intValue());
                    calculateOdds.setMalayOddsValue(arithmeticMALAY(y.getMalayOddsValue(), finalCthree2.getDiffValue()));
                    calculateOdds.setDisOddsValue(getDisOddsValues(y, discountOddsConfigDTOMap, finalCthree2,isPreMarketType));
                    y.getOddsMap().put(""+level,calculateOdds);
                }
            }
            else
            {
                CalculateOdds calculateOdds = new CalculateOdds();
                calculateOdds.setActive(y.getActive());
                calculateOdds.setPaOddsValue(y.getPaOddsValue());
                calculateOdds.setMalayOddsValue(y.getMalayOddsValue());
                calculateOdds.setDisOddsValue(y.getDiscountOdds());
                y.getOddsMap().put(""+level,calculateOdds);
            }
        });
    }

    /**
     *
     * @param y
     * @param discountOddsConfigDTOMap
     * @param configMarketLevel
     * @param isPreMarketType
     * @return
     */
    private static Integer getDisOddsValues(StandardMarketOddsMessage y, Map<String, DiscountOddsConfigDTO> discountOddsConfigDTOMap,ConfigMarketLevel configMarketLevel,boolean isPreMarketType){
        if (isPreMarketType && MapUtil.isNotEmpty(discountOddsConfigDTOMap) && discountOddsConfigDTOMap.containsKey(y.getId().toString()) && y.getDiscountOdds()!=null){
            Double odds = y.getDiscountOdds() + (configMarketLevel.getDiffValue()* 100000);
            return odds.intValue();
        }
        return null;
    }

    /**
     * 马来赔计算规则
     *
     * @param
     * @return
     */
    private static Double arithmeticMALAY(Double malayOddsValue, Double diffValue) {
        //马来赔率
        if (null == malayOddsValue || 0 == malayOddsValue) {
            return 0D;
        }
        BigDecimal malayAndDiff = new BigDecimal(Double.toString(malayOddsValue)).add(new BigDecimal(Double.toString(diffValue)));
        //#如果正数马来赔率分组后小于等于0，令分组后的赔率为最低马来赔率0.01
        if (malayOddsValue > 0 & malayAndDiff.doubleValue() <= 0) {
            return 0.01;
        }
        //#如果负数马来赔率分组后小于等于-1，该赔率再+2
        if (malayOddsValue < 0 & malayAndDiff.doubleValue() <= -1) {
            malayAndDiff = malayAndDiff.add(new BigDecimal("2"));
        }
        return malayAndDiff.doubleValue();
    }

    private static OddsCalcCategoryGroupEnum getCategoryGroup(Map<Long, Integer> categoryGroupMap,
                                                              StandardMarketMessage marketMessage) {

        Long categoryId = marketMessage.getMarketCategoryId();
        if (categoryGroupMap.containsKey(categoryId)) {
            return OddsCalcCategoryGroupEnum.fromGroupId(categoryGroupMap.get(categoryId));
        }
        if (CalculateOddsCategoryConfig.THREE_ODDS_CATEGORY.contains(categoryId) ||
                marketMessage.getMarketOddsList().size() >= 3) {
            return OddsCalcCategoryGroupEnum.GROUP_THREE_ODDS;
        }
        if (marketMessage.getMarketOddsList().size() == 2) {
            return OddsCalcCategoryGroupEnum.GROUP_NORMAL;
        }
        return OddsCalcCategoryGroupEnum.GROUP_NONE;
    }
}
