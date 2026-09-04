package com.panda.merge.dto.odds;

import com.alibaba.fastjson.annotation.JSONField;
import com.panda.merge.common.enums.Constant;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * StandardMarketScoreModification
 *
 * @description: 标准盘口比分接口
 * @date: 3/12/2025
 **/
public interface StandardMarketScoreModification extends StandardMarketModification {

    String homeScore();

    void setHomeScore(String homeScore);

    String awayScore();

    void setAwayScore(String awayScore);

    default String score() {
        String homeScore = homeScore();
        if (StringUtils.isEmpty(homeScore)) {
            return null;
        }
        String awayScore = awayScore();
        if (StringUtils.isEmpty(awayScore)) {
            return null;
        }
        return homeScore() + "_" + awayScore();
    }

    default Integer scoreSum() {
        String homeScore = homeScore();
        if (StringUtils.isEmpty(homeScore)) {
            return null;
        }
        String awayScore = awayScore();
        if (StringUtils.isEmpty(awayScore)) {
            return null;
        }
        return Integer.parseInt(homeScore()) + Integer.parseInt(awayScore());
    }

    default void scoreClose(String cacheScore) {
        setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
        setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
        setMergeMarketStatus(MergeMarketStatusEnum.SCORE_CLOSE.code);
        addRemark(MergeMarketStatusEnum.SCORE_CLOSE.name() + ",cacheScore:" + cacheScore);
    }



}
