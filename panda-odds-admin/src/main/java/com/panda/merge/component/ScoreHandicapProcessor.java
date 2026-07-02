package com.panda.merge.component;

import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.cache.CommonItem;
import com.panda.merge.cache.FootballCacheScores;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.model.StandardMatchInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 44749
 * L01 比分兜底盘口
 */
@Slf4j
@Component
public class ScoreHandicapProcessor extends BaseProcessor {
    @Autowired
    public RedisService redisService;
    /**
     * 上半场 比分
     */
    public final static String STANDARD_MATCH_SCORES_HT = "STANDARD_MATCH_SCORES_HT:";
    /**
     * 下半场 比分
     */
    public final static String STANDARD_MATCH_SCORES_2HT = "STANDARD_MATCH_SCORES_2HT:";
    /**
     * 需要处理的玩法
     */
    public static List<Long> ALL_NEED_CATEGORY = Arrays.asList(2L, 18L, 114L, 122L, 115L, 116L, 123L, 124L, 307L, 309L);
    /**
     * 上半场
     */
    public static List<Long> NEED_CATEGORY_HT = Arrays.asList(18L, 122L, 123L, 124L, 309L);
    /**
     * 全场
     */
    public static List<Long> NEED_CATEGORY_2HT = Arrays.asList(2L, 114L, 115L, 116L, 307L);

    public void scoreMatchingMarket(String linkId, StandardMatchInfo standardMatchInfo, List<StandardMarketDataMessage> standardMarketMessages) {
        //处理足球
        if (!standardMatchInfo.getSportId().equals(StandardSportTypeEnum.FootBall.getCode())) {
            return;
        }
        //只处理滚球
        if (1 == isOddsLive(standardMatchInfo.getId())) {
            return;
        }
        try {
            //需要处理的玩法盘口
            Map<Long, List<StandardMarketDataMessage>> standardMarketMessageMap = standardMarketMessages.stream().filter(standardMarketMessage -> standardMarketMessage.getDataSourceCode().equals(DataSourceCodeEnum.LS.getCode()) && ALL_NEED_CATEGORY.contains(standardMarketMessage.getMarketCategoryId())).collect(Collectors.groupingBy(StandardMarketDataMessage::getMarketCategoryId));
            log.info("::{}::scoreHandicap,赛事id:{}", linkId, standardMatchInfo.getId());
            FootballCacheScores cacheScoresHT = getRedisScoreHt(linkId, standardMatchInfo.getId());
            log.info("::{}::scoreHandicap,赛事id:{},上半场最终比分：{}", linkId, standardMatchInfo.getId(), JSONObject.toJSONString(cacheScoresHT));
            FootballCacheScores cacheScores2HT = getRedisScore2Ht(linkId, standardMatchInfo.getId(), cacheScoresHT);
            log.info("::{}::scoreHandicap,赛事id:{},全场最终比分：{}", linkId, standardMatchInfo.getId(), JSONObject.toJSONString(cacheScores2HT));
            for (Map.Entry<Long, List<StandardMarketDataMessage>> entry : standardMarketMessageMap.entrySet()) {
                Long marketCategoryId = entry.getKey();
                List<StandardMarketDataMessage> standardMarketMessageList = entry.getValue();
                for (StandardMarketDataMessage standardMarketMessage : standardMarketMessageList) {
                    if (NEED_CATEGORY_HT.contains(marketCategoryId)) {
                        marketHtProcessor(linkId, standardMatchInfo.getId(), cacheScoresHT, standardMarketMessage);
                    } else if (NEED_CATEGORY_2HT.contains(marketCategoryId)) {
                        market2HtProcessor(linkId, standardMatchInfo.getId(), cacheScores2HT, standardMarketMessage);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("::{}::scoreHandicap,赛事id:{}，出现异常：{}", linkId, standardMatchInfo.getId(), e);
        }

    }

    /**
     * 上半场比分
     *
     * @return
     */
    public FootballCacheScores getRedisScoreHt(String linkId, Long standardMatchId) {
        //上半场比分
        Object obj = redisService.get(DigestUtil.md5Hex(STANDARD_MATCH_SCORES_HT + standardMatchId));
        FootballCacheScores footballCacheScoresHT = new FootballCacheScores();
        if (null != obj) {
            footballCacheScoresHT = JSONObject.parseObject(obj.toString(), FootballCacheScores.class);
        }
        log.info("::{}::getRedisScoreHt,赛事id:{}，缓存比分：{}", linkId, standardMatchId, footballCacheScoresHT);
        return init(footballCacheScoresHT);
    }

    public static void main(String[] args) {
        String obj = "{\"redCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":1,\"home\":4},\"corner\":{\"away\":0,\"home\":0},\"@type\":\"com.panda.merge.dto.cache.FootballCacheScores\",\"yellowCard\":{\"away\":0,\"home\":0},\"faCard\":{\"away\":0,\"home\":0}}";
        FootballCacheScores footballCacheScoresHT = JSONObject.parseObject(obj.toString(), FootballCacheScores.class);
        System.out.println(footballCacheScoresHT);
    }

    /**
     * 下半场半场比分
     *
     * @return
     */
    public FootballCacheScores getRedisScore2Ht(String linkId, Long standardMatchId, FootballCacheScores redisScoreHt) {
        //下半场比分
        FootballCacheScores footballCacheScores2HT = new FootballCacheScores();
        Object obj = redisService.get(DigestUtil.md5Hex(STANDARD_MATCH_SCORES_2HT + standardMatchId));
        if (null != obj) {
            footballCacheScores2HT = JSONObject.parseObject(obj.toString(), FootballCacheScores.class);
        }
        log.info("::{}::getRedisScore2Ht,赛事id:{}，缓存比分：{}", linkId, standardMatchId, footballCacheScores2HT);
        FootballCacheScores cacheScores = init(footballCacheScores2HT);
        //全场比分  =  上半场比分 + 下半场比分
        //上半场比分
        CommonItem goalHT = redisScoreHt.getGoal();
        CommonItem goal2HT = cacheScores.getGoal();
        cacheScores.setGoal(new CommonItem(goalHT.getHome() + goal2HT.getHome(), goalHT.getAway() + goal2HT.getAway()));
        //上半场角球
        CommonItem cornerHT = redisScoreHt.getCorner();
        CommonItem corner2HT = cacheScores.getCorner();
        cacheScores.setCorner(new CommonItem(cornerHT.getHome() + corner2HT.getHome(), cornerHT.getAway() + corner2HT.getAway()));
        //上半场罚牌
        CommonItem faCardHT = redisScoreHt.getFaCard();
        CommonItem faCard2HT = cacheScores.getFaCard();
        cacheScores.setFaCard(new CommonItem(faCardHT.getHome() + faCard2HT.getHome(), faCardHT.getAway() + faCard2HT.getAway()));
        return cacheScores;
    }

    /**
     * 上半场玩法判断处理
     *
     * @param standardMatchId
     * @return
     */
    public void marketHtProcessor(String linkId, Long standardMatchId, FootballCacheScores cacheScores, StandardMarketDataMessage standardMarketMessage) {
        Long marketCategoryId = standardMarketMessage.getMarketCategoryId();
        //上半场大小
        if (marketCategoryId == 18) {
            CommonItem goal = cacheScores.getGoal();
            if (null != goal) {
                category2Validate(linkId, standardMatchId, standardMarketMessage, goal.getHome(), goal.getAway());
            }
        }
        //上半场角球大小
        else if (marketCategoryId == 122) {
            CommonItem corner = cacheScores.getCorner();
            if (null != corner) {
                category2Validate(linkId, standardMatchId, standardMarketMessage, corner.getHome(), corner.getAway());
            }
        }
        //上半场主队角球大小
        else if (marketCategoryId == 123) {
            CommonItem corner = cacheScores.getCorner();
            if (null != corner) {
                category2ValidateHomeOrAway(linkId, standardMatchId, standardMarketMessage, corner.getHome());
            }
        }
        //上半场客队角球大小
        else if (marketCategoryId == 124) {
            CommonItem corner = cacheScores.getCorner();
            if (null != corner) {
                category2ValidateHomeOrAway(linkId, standardMatchId, standardMarketMessage, corner.getAway());
            }
        }
        //上半场罚牌大小
        else if (marketCategoryId == 309) {
            CommonItem faCard = cacheScores.getFaCard();
            if (null != faCard) {
                category2Validate(linkId, standardMatchId, standardMarketMessage, faCard.getHome(), faCard.getAway());
            }
        }

    }


    /**
     * 下半场玩法判断处理
     *
     * @param standardMatchId
     * @return
     */
    public void market2HtProcessor(String linkId, Long standardMatchId, FootballCacheScores cacheScores, StandardMarketDataMessage standardMarketMessage) {
        Long marketCategoryId = standardMarketMessage.getMarketCategoryId();
        //全场大小
        if (marketCategoryId == 2) {
            CommonItem goal = cacheScores.getGoal();
            if (null != goal) {
                category2Validate(linkId, standardMatchId, standardMarketMessage, goal.getHome(), goal.getAway());
            }
        }
        //角球大小
        else if (marketCategoryId == 114) {
            CommonItem corner = cacheScores.getCorner();
            if (null != corner) {
                category2Validate(linkId, standardMatchId, standardMarketMessage, corner.getHome(), corner.getAway());
            }
        }
        //主队角球大小
        else if (marketCategoryId == 115) {
            CommonItem corner = cacheScores.getCorner();
            if (null != corner) {
                category2ValidateHomeOrAway(linkId, standardMatchId, standardMarketMessage, corner.getHome());
            }
        }
        //客队角球大小
        else if (marketCategoryId == 116) {
            CommonItem corner = cacheScores.getCorner();
            if (null != corner) {
                category2ValidateHomeOrAway(linkId, standardMatchId, standardMarketMessage, corner.getAway());
            }
        }
        //罚牌大小
        else if (marketCategoryId == 307) {
            CommonItem faCard = cacheScores.getFaCard();
            if (null != faCard) {
                category2Validate(linkId, standardMatchId, standardMarketMessage, faCard.getHome(), faCard.getAway());
            }
        }

    }

    /**
     * 初始化比分
     *
     * @param scores
     * @return
     */
    public static FootballCacheScores init(FootballCacheScores scores) {
        if (null == scores) {
            FootballCacheScores newScores = new FootballCacheScores();
            newScores.setGoal(new CommonItem(0, 0));
            newScores.setCorner(new CommonItem(0, 0));
            newScores.setFaCard(new CommonItem(0, 0));
            return newScores;
        } else {
            if (null == scores.getGoal()) {
                scores.setGoal(new CommonItem(0, 0));
            }
            if (null == scores.getCorner()) {
                scores.setCorner(new CommonItem(0, 0));
            }
            if (null == scores.getFaCard()) {
                scores.setFaCard(new CommonItem(0, 0));
            }
            return scores;
        }

    }

    /**
     * 对比
     *
     * @param linkId
     * @param matchId
     * @param standardMarketMessage
     * @param home
     * @param away
     * @return
     */
    private boolean category2Validate(String linkId, Long matchId, StandardMarketDataMessage standardMarketMessage, Integer home, Integer away) {
        double a1 = Double.valueOf(standardMarketMessage.getAddition1()).doubleValue();
        double goalTotal = Double.valueOf(home + away).doubleValue() + 0.25;
        boolean isTrue = a1 <= goalTotal;
        if (isTrue) {
            standardMarketMessage.setThirdMarketSourceStatus(Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED);
            standardMarketMessage.setStatus(Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED);
            log.info("::{}::category2Validate 比分盘口校验满足关盘条件。盘口id:{},条件Addition1信息:{}, 比分+25:{}", linkId, standardMarketMessage.getRelationMarketId(), a1, goalTotal);
        }
        return isTrue;
    }

    /**
     * 对比
     *
     * @param linkId
     * @param matchId
     * @param standardMarketMessage
     * @return
     */
    private boolean category2ValidateHomeOrAway(String linkId, Long matchId, StandardMarketDataMessage standardMarketMessage, Integer score) {
        double a1 = Double.valueOf(standardMarketMessage.getAddition1()).doubleValue();
        double goalTotal = Double.valueOf(score).doubleValue();
        boolean isTrue = goalTotal > a1;
        if (isTrue) {
            standardMarketMessage.setThirdMarketSourceStatus(Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED);
            standardMarketMessage.setStatus(Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED);
            log.info("::{}::category2ValidateHomeOrAway 比分盘口校验满足关盘条件。盘口id:{},条件Addition1信息:{}, 比分+25:{}", linkId, standardMarketMessage.getRelationMarketId(), a1, goalTotal);
        }
        return isTrue;
    }
}