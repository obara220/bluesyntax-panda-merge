package com.panda.merge.odds.enums;

import com.panda.merge.cache.CommonItem;
import com.panda.merge.dto.FootballCacheScores;
import com.panda.merge.odds.constants.CategoryConstant;

import java.util.Set;

public enum MarketScoreTypeEnum {
    HT_GOAL,
    HT_CARD,
    HT_CORNER,
    GOAL,
    CARD,
    CORNER,
    OVERTIME_GOAL,
    OVERTIME_CARD,
    OVERTIME_CORNER,
    OVERTIME_HT_GOAL,
    OVERTIME_HT_CARD,
    OVERTIME_HT_CORNER;

    public CommonItem getScore(FootballCacheScores footballCacheScores) {
        switch (this) {
            case GOAL:
                return footballCacheScores.getGoal();
            case HT_GOAL:
                return footballCacheScores.getHfGoal();
            case OVERTIME_GOAL:
                return footballCacheScores.getOverTimeGoal();
            case OVERTIME_HT_GOAL:
                return footballCacheScores.getOverTimeHfGoal();
            case CARD:
                return footballCacheScores.getFaCard();
            case HT_CARD:
                return footballCacheScores.getHfFaCard();
            case OVERTIME_CARD:
                return footballCacheScores.getOverTimeFaCard();
            case OVERTIME_HT_CARD:
                return footballCacheScores.getOverTimeHfFaCard();
            case CORNER:
                return footballCacheScores.getCorner();
            case HT_CORNER:
                return footballCacheScores.getHfCorner();
            case OVERTIME_CORNER:
                return footballCacheScores.getOverTimeCorner();
            case OVERTIME_HT_CORNER:
                return footballCacheScores.getOverTimeHfCorner();
            default:
                return null;
        }
    }

    public Set<Long> getCategoryIds() {
        switch (this) {
            case GOAL:
                return CategoryConstant.REGULAR_GOAL_SET;
            case OVERTIME_GOAL:
                return CategoryConstant.OVERTIME_GOAL_SET;
            case CARD:
                return CategoryConstant.REGULAR_CARD_SET;
            case OVERTIME_CARD:
                return CategoryConstant.OVERTIME_CARD_SET;
            case CORNER:
                return CategoryConstant.REGULAR_CORNER_SET;
            case OVERTIME_CORNER:
                return CategoryConstant.OVERTIME_CORNER_SET;
            default:
                return null;
        }

    }
}
