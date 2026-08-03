package com.panda.merge.constant;

import com.panda.merge.common.enums.Constant;
import com.panda.merge.dto.odds.StandardMarketModification;
import com.panda.merge.dto.odds.StandardMarketOddsModification;
import com.panda.merge.exception.ApiException;
import com.panda.merge.model.StandardSportMarket;
import com.panda.merge.model.StandardSportMarketOdds;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Arrays;

/**
 * @author :  Jimmy
 * @Project Name :  data-realtime-marketodds
 * @Package Name :  com.panda.sport.data.realtime.service.relationkey
 * @Description :  TODO
 * @Date: 2020-03-05 11:04
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
public class RelationKeyFactory {

    /**
     * 根据标准盘口信息生成标准统一盘口id key
     * @param linkId
     * @param standardSportMarket
     * @return
     */
    public static String getMarketRelationKey(String linkId, StandardSportMarket standardSportMarket) {
        return getMarketRelationKey(linkId, standardSportMarket.getStandardMatchInfoId(), standardSportMarket);
    }

    public static <T extends StandardMarketModification> String getMarketRelationKey(String linkId,
                                                                                     Long standardMatchId,
                                                                                     T standardSportMarket) {

        StringBuffer redisKey = new StringBuffer(Constant.REDIS_KEY.RONGHE_STANDARD_MARKET_RELATION_MARKET_ID);
        Long categoryId = standardSportMarket.getMarketCategoryId();
        String addition1 = standardSportMarket.getAddition1();
        String addition2 = StringUtils.isEmpty(standardSportMarket.getAddition2()) ? addition1 :
                standardSportMarket.getAddition2();
        String addition3 = standardSportMarket.getAddition3();
        String addition4 = standardSportMarket.getAddition4();
        String addition5 = standardSportMarket.getAddition5();
        Integer marketType = standardSportMarket.getMarketType();
        log.info("::{}::标准赛事Id={},三方盘口源id={},盘口类型={},玩法id={},附件值1={},附件值2={},附件值3={},附件值4={}",
                 linkId,
                 standardMatchId,
                 standardSportMarket.getThirdMarketSourceId(),
                 marketType,
                 categoryId,
                 addition1,
                 standardSportMarket.getAddition2(),
                 addition3,
                 addition4);
        //冠军盘口统一盘口id处理
        if (2 == marketType) {
            redisKey.append(standardMatchId).append("_").append(standardSportMarket.getThirdMarketSourceId());
            return redisKey.toString();
        }
        if (Arrays.asList(RelationKeyRuleConfig.CATEGORY_ADDITION1).contains(categoryId)) {
            redisKey.append(standardMatchId).append("_").append(categoryId).append("_");
            //SR准确进球类玩法根据附加字段1区分盘口,其他数据源还是按照单盘口玩法生成
            if (!StringUtils.isEmpty(addition1)) {
                redisKey.append(addition1.replace(".0", ""));
            }
        } else if (Arrays.asList(RelationKeyRuleConfig.CATEGORY_ADDITION2).contains(categoryId)) {
            redisKey
                    .append(standardMatchId)
                    .append("_")
                    .append(categoryId)
                    .append("_")
                    .append(addition2.replace(".0", ""));
        } else if (Arrays.asList(RelationKeyRuleConfig.CATEGORY_SINGLE).contains(categoryId)) {
            redisKey.append(standardMatchId).append("_").append(categoryId).append("_");
        } else if (Arrays.asList(RelationKeyRuleConfig.CATEGORY_ADDITION1_ADDITION2).contains(categoryId)) {
            redisKey
                    .append(standardMatchId)
                    .append("_")
                    .append(categoryId)
                    .append("_")
                    .append(addition1.replace(".0", ""))
                    .append(addition2.replace(".0", ""));
        } else if (Arrays.asList(RelationKeyRuleConfig.CATEGORY_ADDITION2_3).contains(categoryId)) {
            redisKey
                    .append(standardMatchId)
                    .append("_")
                    .append(categoryId)
                    .append("_")
                    .append(addition2.replace(".0", ""))
                    .append(addition3.replace(".0", ""));
        } else if (Arrays.asList(RelationKeyRuleConfig.CATEGORY_ADDITION1_2_3).contains(categoryId)) {
            redisKey
                    .append(standardMatchId)
                    .append("_")
                    .append(categoryId)
                    .append("_")
                    .append(addition1.replace(".0", ""))
                    .append(addition2.replace(".0", ""))
                    .append(addition3.replace(".0", ""));
        } else if (Arrays.asList(RelationKeyRuleConfig.CATEGORY_ADDITION1_2_3_4).contains(categoryId)) {
            redisKey
                    .append(standardMatchId)
                    .append("_")
                    .append(categoryId)
                    .append("_")
                    .append(addition1.replace(".0", ""))
                    .append(addition2.replace(".0", ""))
                    .append(addition3.replace(".0", ""))
                    .append(addition4.replace(".0", ""));
        } else if (Arrays.asList(RelationKeyRuleConfig.CATEGORY_ADDITION2_5).contains(categoryId)) {
            redisKey
                    .append(standardMatchId)
                    .append("_")
                    .append(categoryId)
                    .append("_")
                    .append(addition2.replace(".0", ""))
                    .append(addition5.replace(".0", ""));
        }else if ((categoryId >= 1109000L && categoryId <= 1109999L) ||
                (categoryId >= 3109000L && categoryId <= 3109999L)) {
            redisKey.append(standardMatchId).append("_").append(categoryId).append("_");
            if (!StringUtils.isEmpty(addition1)) {
                redisKey.append(addition1.replace(".0", ""));
            }
        } else {
            throw new ApiException("生成统一盘口id出错,玩法id:" + categoryId);
        }
        return redisKey.toString();

    }

    /**
     * 根据三方盘口信息生成标准统一盘口id key
     * @param linkId
     * @param standardMatchId
     * @param categoryId
     * @param addition1
     * @param addition2
     * @param addition3
     * @param addition4
     * @param marketType
     * @return
     */
    public static String getMarketRelationKeyByThirdInfo(String linkId, Long standardMatchId, Long categoryId,
                                                         String addition1,String addition2,String addition3,String addition4,String addition5,
                                                         Integer marketType,String thirdMarketSourceId) {
        StringBuffer redisKey = new StringBuffer(Constant.REDIS_KEY.RONGHE_STANDARD_MARKET_RELATION_MARKET_ID);
        addition2 = addition2 == null ? addition1 : addition2;
        log.info("::{}:: 根据三方盘口信息生成标准统一盘口id,标准赛事Id={},盘口类型={},玩法id={},附件值1={},附件值2={},附件值3={},附件值4={}",linkId,standardMatchId,
                marketType, categoryId,addition1,addition2,addition3,addition4);
        //冠军盘口统一盘口id处理
        if(2 == marketType){
            redisKey.append(standardMatchId)
                    .append("_").append(thirdMarketSourceId);
            return  redisKey.toString();
        }
        if (Arrays.asList(RelationKeyRuleConfig.CATEGORY_ADDITION1).contains(categoryId)) {
            redisKey.append(standardMatchId)
                    .append("_").append(categoryId)
                    .append("_");
            //SR准确进球类玩法根据附加字段1区分盘口,其他数据源还是按照单盘口玩法生成
            if (!StringUtils.isEmpty(addition1)) {
                redisKey.append(addition1.replace(".0", ""));
            }
        } else if (Arrays.asList(RelationKeyRuleConfig.CATEGORY_ADDITION2).contains(categoryId)) {
            redisKey.append(standardMatchId)
                    .append("_").append(categoryId)
                    .append("_").append(addition2.replace(".0", ""));
        } else if (Arrays.asList(RelationKeyRuleConfig.CATEGORY_SINGLE).contains(categoryId)) {
            redisKey.append(standardMatchId).append("_").append(categoryId).append("_");
        } else if (Arrays.asList(RelationKeyRuleConfig.CATEGORY_ADDITION1_ADDITION2).contains(categoryId)) {
            redisKey.append(standardMatchId)
                    .append("_").append(categoryId)
                    .append("_")
                    .append(addition1.replace(".0", ""))
                    .append(addition2.replace(".0", ""));
        } else if (Arrays.asList(RelationKeyRuleConfig.CATEGORY_ADDITION2_3).contains(categoryId)) {
            redisKey.append(standardMatchId)
                    .append("_").append(categoryId)
                    .append("_")
                    .append(addition2.replace(".0", ""))
                    .append(addition3.replace(".0", ""));
        } else if (Arrays.asList(RelationKeyRuleConfig.CATEGORY_ADDITION1_2_3).contains(categoryId)) {
            redisKey.append(standardMatchId)
                    .append("_").append(categoryId)
                    .append("_")
                    .append(addition1.replace(".0", ""))
                    .append(addition2.replace(".0", ""))
                    .append(addition3.replace(".0", ""));
        } else if(Arrays.asList(RelationKeyRuleConfig.CATEGORY_ADDITION1_2_3_4).contains(categoryId)){
            redisKey.append(standardMatchId)
                    .append("_").append(categoryId)
                    .append("_")
                    .append(addition1.replace(".0",""))
                    .append(addition2.replace(".0",""))
                    .append(addition3.replace(".0",""))
                    .append(addition4.replace(".0",""));
        }else if (Arrays.asList(RelationKeyRuleConfig.CATEGORY_ADDITION2_5).contains(categoryId))
        {
            redisKey.append(standardMatchId)
                    .append("_").append(categoryId)
                    .append("_")
                    .append(addition2.replace(".0",""))
                    .append(addition5.replace(".0", ""));
        }else if ((categoryId >=1109000L && categoryId <=1109999L) || (categoryId >=3109000L && categoryId <=3109999L)){
            redisKey.append(standardMatchId)
                    .append("_").append(categoryId)
                    .append("_");
            if (!StringUtils.isEmpty(addition1)) {
                redisKey.append(addition1.replace(".0", ""));
            }
        }else {
            throw new ApiException(linkId + ":生成统一盘口id出错,玩法id:" + categoryId);
        }
        return redisKey.toString();
    }

    /**
     * 根据标准投注项生成标准投注项统一id key
     * @param relationMarketId
     * @param odds
     * @param categoryId
     * @return
     */
    public static <T extends StandardMarketOddsModification> String getMarketOddsRelationKey(Long relationMarketId,
                                                                                             T odds,
                                                                                             Long categoryId) {
        StringBuffer redisKey =
                new StringBuffer(Constant.REDIS_KEY.RONGHE_STANDARD_MARKET_ODDS_RELATION_MARKET_ODDS_ID);
        if (MarginCategoryConfig.STANDARD_OUTRIGHT_CATEGORY.contains(categoryId)) {
            redisKey.append(relationMarketId).append("_").append(odds.getThirdOddsFieldSourceId());
        } else if (categoryId != null && RelationKeyRuleConfig.CATEGORY_ODDS_ADDITION_1.contains(categoryId)) {
            redisKey.append(relationMarketId).append("_");
            //球员玩法投注项会出现没有addition1 ，取oddsType生成
            if (!StringUtils.isEmpty(odds.getAddition1())) {
                redisKey.append(odds.getAddition1());
            } else {
                redisKey.append(odds.getOddsType());
            }
        } else {
            redisKey.append(relationMarketId).append("_").append(odds.getOddsType());
        }
        return redisKey.toString();
    }

    /**
     * 根据三方盘口投注项信息生成标准投注项统一id key
     * @param relationMarketId
     * @param oddsType
     * @param thirdOddsFieldSourceId
     * @param addition1
     * @param categoryId
     * @return
     */
    public static String getMarketOddsRelationKeyByThirdOddsInfo(Long relationMarketId, String oddsType,String thirdOddsFieldSourceId,String addition1, Long categoryId) {
        StringBuffer redisKey = new StringBuffer(Constant.REDIS_KEY.RONGHE_STANDARD_MARKET_ODDS_RELATION_MARKET_ODDS_ID);
        if (MarginCategoryConfig.STANDARD_OUTRIGHT_CATEGORY.contains(categoryId)) {
            redisKey.append(relationMarketId).append("_").append(thirdOddsFieldSourceId);
        }else if (categoryId != null && RelationKeyRuleConfig.CATEGORY_ODDS_ADDITION_1.contains(categoryId)) {
            redisKey.append(relationMarketId).append("_");
            //球员玩法投注项会出现没有addition1 ，取oddsType生成
            if (!StringUtils.isEmpty(addition1)) {
                redisKey.append(addition1);
            } else {
                redisKey.append(addition1);
            }
        } else {
            redisKey.append(relationMarketId).append("_").append(oddsType);
        }
        return redisKey.toString();
    }
}
