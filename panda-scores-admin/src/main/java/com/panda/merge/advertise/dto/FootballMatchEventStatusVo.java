package com.panda.merge.advertise.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class FootballMatchEventStatusVo implements Serializable {
    //危险安全
    private Boolean  isDanger;
    //当前事件
    private String  currentEventCode;
    //当前事件主客队  home 主队  away 客队  none 或者 null  为么有
    private String  currentEventHomeAway;
    /**
     * 有可能进球
     * */
    private boolean hasHomePenalty;

    /**
     * 确认进球
     * */
    private boolean hasHomeConfirmPenalty;
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
     * 有可能进球
     * */
    private boolean hasAwayPenalty;

    /**
     * 确认进球
     * */
    private boolean hasAwayConfirmPenalty;
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
     * 主队有可能任意球
     */
    private boolean hasHomeFreeKick;

    /**
     * 客队有可能任意球
     */
    private boolean hasAwayFreeKick;

    /**
     * 客队有可能界外球
     */
    private boolean hasHomeThrowIn;

    /**
     * 客队有可能界外球
     */
    private boolean hasAwayThrowIn;
    /**
     * 主队有可能持球
     */
    private boolean hasHomePossession;

    /**
     * 客队有可能持球
     */
    private boolean hasAwayPossession;
    /**
     * 客队有可能界外球
     */
    private boolean hasHomeAttack;

    /**
     * 客队有可能界外球
     */
    private boolean hasAwayAttack;

    /**
     * 客队有可能红黄牌
     */
    private boolean hasHomeYellowRedCard;

    /**
     * 客队有可能红黄牌
     */
    private boolean hasAwayYellowRedCard;

    /**
     * 客队有可能球门球
     */
    private boolean hasHomeGoalKick;

    /**
     * 客队有可能球门球
     */
    private boolean hasAwayGoalKick;

    /**
     * 客队有可能越位
     */
    private boolean hasHomeOffside;

    /**
     * 客队有可能越位
     */
    private boolean hasAwayOffside;

    /**
     * 客队有可能射正
     */
    private boolean hasHomeShotOnTarget;

    /**
     * 客队有可能射正
     */
    private boolean hasAwayShotOnTarget;

    /**
     * 客队有可能射偏
     */
    private boolean hasHomeShotOffTarget;

    /**
     * 客队有可能射偏
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
    /**
     * VAR事件-事件类型: 1 = 点球
     */
    private boolean hasVARPenalty;
    /**
     * VAR事件-事件类型: 2 = 红牌
     */
    private boolean hasVARRedCard;

    /**
     * VAR事件-确认罚牌VAR
     */
    private boolean hasVARConfirmRedCard;

    /**
     * VAR事件-确认点球VAR
     */
    private boolean hasVARConfirmPenalty;

    /**
     * VAR事件-确认进球VAR
     */
    private boolean hasVARConfirmGoal;
    /**
     * 主队危险进攻
     */
    private boolean hasHomeDangerousAttack;
    /**
     * 客队危险进攻
     */
    private boolean hasAwayDangerousAttack;

    public FootballMatchEventStatusVo(){};

    public static FootballMatchEventStatusVo init(){
        FootballMatchEventStatusVo footballMatchEventStatusVo=new FootballMatchEventStatusVo();
        footballMatchEventStatusVo.isDanger=false;
        footballMatchEventStatusVo.currentEventCode="ball_safe";
        footballMatchEventStatusVo.hasHomeGoal=false;
        footballMatchEventStatusVo.hasHomeYellowCard=false;
        footballMatchEventStatusVo.hasHomeRedCard=false;
        footballMatchEventStatusVo.hasHomeCorner=false;
        footballMatchEventStatusVo.hasHomePenalty=false;
        footballMatchEventStatusVo.hasHomeConfirmPenalty=false;
        footballMatchEventStatusVo.hasAwayGoal=false;
        footballMatchEventStatusVo.hasAwayYellowCard=false;
        footballMatchEventStatusVo.hasAwayRedCard=false;
        footballMatchEventStatusVo.hasAwayCorner=false;
        footballMatchEventStatusVo.hasAwayPenalty=false;
        footballMatchEventStatusVo.hasAwayConfirmPenalty=false;
        footballMatchEventStatusVo.hasHomeFreeKick=false;
        footballMatchEventStatusVo.hasAwayFreeKick=false;
        footballMatchEventStatusVo.hasHomeThrowIn=false;
        footballMatchEventStatusVo.hasAwayThrowIn=false;
        footballMatchEventStatusVo.hasHomePossession=false;
        footballMatchEventStatusVo.hasAwayPossession=false;
        footballMatchEventStatusVo.hasHomeAttack=false;
        footballMatchEventStatusVo.hasAwayAttack=false;
        footballMatchEventStatusVo.hasHomeYellowRedCard=false;
        footballMatchEventStatusVo.hasAwayYellowRedCard=false;
        footballMatchEventStatusVo.hasHomeGoalKick=false;
        footballMatchEventStatusVo.hasAwayGoalKick=false;
        footballMatchEventStatusVo.hasHomeOffside=false;
        footballMatchEventStatusVo.hasAwayOffside=false;
        footballMatchEventStatusVo.hasHomeShotOnTarget=false;
        footballMatchEventStatusVo.hasAwayShotOnTarget=false;
        footballMatchEventStatusVo.hasHomeShotOffTarget=false;
        footballMatchEventStatusVo.hasAwayShotOffTarget=false;
        footballMatchEventStatusVo.hasVAREvent=false;
        footballMatchEventStatusVo.hasVARGoal=false;
        footballMatchEventStatusVo.hasVARConfirmGoal=false;
        footballMatchEventStatusVo.hasVARPenalty=false;
        footballMatchEventStatusVo.hasVARConfirmPenalty=false;
        footballMatchEventStatusVo.hasVARRedCard=false;
        footballMatchEventStatusVo.hasVARConfirmRedCard=false;
        footballMatchEventStatusVo.hasHomeDangerousAttack=false;
        footballMatchEventStatusVo.hasAwayDangerousAttack=false;
        return footballMatchEventStatusVo;
    }
}
