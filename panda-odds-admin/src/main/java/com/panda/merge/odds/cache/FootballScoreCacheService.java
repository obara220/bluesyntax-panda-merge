package com.panda.merge.odds.cache;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.panda.merge.cache.CommonItem;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.FootballCacheScores;
import com.panda.merge.odds.enums.MarketScoreTypeEnum;
import com.panda.merge.odds.model.CategoryMarketMessageData;
import com.panda.merge.odds.model.MatchMarketMessageData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.panda.merge.odds.constants.CategoryConstant.CATEGORY_SCORE_TYPE_MAP;

/**
 * FootballScoreCacheService
 *
 * @description:
 * @date: 4/1/2025
 **/
@Service
@Slf4j
public class FootballScoreCacheService {

    @Autowired
    private RedisService redisService;

    public FootballCacheScores getCache(String linkId, Long matchId) {
        String key = DigestUtil.md5Hex("FOOTBALL_STANDARD_MATCH_SCORES:" + matchId);
        Object obj = redisService.get(key);
        if (Objects.isNull(obj)) {
            log.info("::{}::赛事id:{},score cache is null", linkId, matchId);
            return null;
        }
        log.info("::{}::赛事id:{}, score cache:{},", linkId, matchId, obj);
        String json = (String) obj;
        return JSONUtil.toBean(json, FootballCacheScores.class);
    }

    public FootballCacheScores getCache(MatchMarketMessageData matchData) {
        if (matchData.isScoreCacheEmpty) {
            return null;
        }
        if (Objects.nonNull(matchData.footballCacheScores)) {
            return matchData.footballCacheScores;
        }
        FootballCacheScores footballCacheScores = getCache(matchData.linkId, matchData.standardMatchInfo.getId());
        if (Objects.isNull(footballCacheScores)) {
            matchData.isScoreCacheEmpty = true;
            return null;
        }
        matchData.footballCacheScores = footballCacheScores;
        return footballCacheScores;
    }

    public Integer getCacheScoreSum(CategoryMarketMessageData categoryData) {
        FootballCacheScores cacheScores = getCache(categoryData.matchData);

        if (Objects.isNull(cacheScores) || Objects.isNull(categoryData.categoryId)) {
            return null;
        }
        MarketScoreTypeEnum marketScoreTypeEnum = CATEGORY_SCORE_TYPE_MAP.get(categoryData.categoryId);
        if (Objects.isNull(marketScoreTypeEnum)) {
            return null;
        }
        CommonItem score = marketScoreTypeEnum.getScore(cacheScores);
        if (Objects.isNull(score) || Objects.isNull(score.getHome()) || Objects.isNull(score.getAway())) {
            return null;
        }
        return score.getHome() + score.getAway();
    }

    public CommonItem getCacheScoreMarketScoreType(CategoryMarketMessageData categoryData) {
        FootballCacheScores cacheScores = getCache(categoryData.matchData);

        if (Objects.isNull(cacheScores) || Objects.isNull(categoryData.categoryId)) {
            return new CommonItem();
        }
        MarketScoreTypeEnum marketScoreTypeEnum = CATEGORY_SCORE_TYPE_MAP.get(categoryData.categoryId);
        if (Objects.isNull(marketScoreTypeEnum)) {
            return new CommonItem();
        }
        CommonItem score = marketScoreTypeEnum.getScore(cacheScores);
        return score;
    }
}
