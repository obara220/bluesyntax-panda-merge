package com.panda.merge.dto.scores;


import com.panda.merge.cache.CommonItem;
import com.panda.merge.cache.CommonItemBigDecimal;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Objects;

/**
 * 赛事比分主要数据
 */
@Slf4j
@Data
public class MatchScoreDto implements Serializable{

    /**
     * 赛事管理DI
     */
    private Long matchInfoId;

    /**
     * 主要比分数据
     */
    private String scoresJson;

    /**
     * 附加比分-点球大战
     */
    private String scoresJsonExtra;
    /**
     * 常规赛上半场
     */
    private CommonItem halfTimeScores;
    /**
     * 常规赛全场
     */
    private CommonItem allTimeScores;
    /**
     * 加时赛上半场
     */
    private CommonItem firstOverTimeScores;
    /**
     * 加时赛全场
     */
    private CommonItem allOverTimeScores;

    /**
     * 点球大战
     */
    private CommonItem penaltyScore;
    /**
     * 总比分
     */
    private CommonItem AllScores;
    /**
     * 射正
     */
    private CommonItem shotOn ;
    /**
     * 射偏
     */
    private CommonItem shotOff ;
    /**
     * 预期失球
     */
    private CommonItemBigDecimal expectationLoss ;
    /**
     * 预期进球
     */
    private CommonItemBigDecimal expectationXg ;
    /**
     * 进攻
     */
    private CommonItem attack ;
    /**
     * 危险进攻
     */
    private CommonItem dangerousAttack ;
    /**
     * 控球率
     */
    private CommonItem ballPossessionPercentage ;
    /**
     * 角球
     */
    private CommonItem corner ;
    /**
     * 红牌
     */
    private CommonItem redCard ;
    /**
     * 黄牌
     */
    private CommonItem yellowCard ;

    public MatchScoreDto() {
        this.halfTimeScores = new CommonItem(0,0);
        this.allTimeScores = new CommonItem(0,0);
        this.firstOverTimeScores = new CommonItem(0,0);
        this.allOverTimeScores = new CommonItem(0,0);
        this.penaltyScore = new CommonItem(0,0);
        this.AllScores = new CommonItem(0,0);
    }

}
