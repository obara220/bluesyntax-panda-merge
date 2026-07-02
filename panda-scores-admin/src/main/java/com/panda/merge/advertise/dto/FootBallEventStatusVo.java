package com.panda.merge.advertise.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class FootBallEventStatusVo implements Serializable {
    /**
     * 有可能进球
     * */
    private boolean hasHomeGoal;
    /**
     * 有可能黄牌
     * */
    private boolean hasHomeYellowCard;
    /**
     * 有可能红牌
     * */
    private boolean hasHomeRedCard;
    /**
     * 有可能角球
     * */
    private boolean hasHomeCorner;
    /**
     * 可能点球
     * */
    private boolean hasHomePenalty;
    private boolean hasHomeConfirmPenalty;

    /**
     * 有可能进球
     * */
    private boolean hasAwayGoal;
    /**
     * 有可能黄牌
     * */
    private boolean hasAwayYellowCard;
    /**
     * 有可能红牌
     * */
    private boolean hasAwayRedCard;
    /**
     * 有可能角球
     * */
    private boolean hasAwayCorner;
    /**
     * 可能点球
     * */
    private boolean hasAwayPenalty;
    private boolean hasAwayConfirmPenalty;

    /**
     * 主队界外球
     */
    private boolean hasHomeThrowIn;
    /**
     * 客队界外球
     */
    private boolean hasAwayThrowIn;
    /**
     * 主队进攻
     */
    private boolean hasHomeAttack;
    /**
     * 客队进攻
     */
    private boolean hasAwayAttack;
    /**
     * 主队球门球
     */
    private boolean hasHomeGoalKick;
    /**
     * 客队球门球
     */
    private boolean hasAwayGoalKick;
    /**
     * 主队越位
     */
    private boolean hasHomeOffside;
    /**
     * 客队越位
     */
    private boolean hasAwayOffside;
    /**
     * 主队射正
     */
    private boolean hasHomeShotOnTarget;
    /**
     * 客队射正
     */
    private boolean hasAwayShotOnTarget;
    /**
     * 主队射偏
     */
    private boolean hasHomeShotOffTarget;
    /**
     * 客队射偏
     */
    private boolean hasAwayShotOffTarget;
    /**
     * VAR事件
     */
    private boolean hasVAREvent;
    /**
     * VAR事件-事件类型: 0 = 进球
     */
    private boolean hasVARGoal;
    private boolean hasVARConfirmGoal;
    /**
     * VAR事件-事件类型: 1 = 点球
     */
    private boolean hasVARPenalty;
    private boolean hasVARConfirmPenalty;
    /**
     * VAR事件-事件类型: 2 = 红牌
     */
    private boolean hasVARRedCard;

    private boolean hasVARConfirmRedCard;
    /**
     * 主队红黄牌
     */
    private boolean hasHomeYellowRedCard;
    /**
     * 客队红黄牌
     */
    private boolean hasAwayYellowRedCard;
    /**
     * 主队任意球
     */
    private boolean hasHomeFreeKick;
    /**
     * 客队任意球
     */
    private boolean hasAwayFreeKick;
}
