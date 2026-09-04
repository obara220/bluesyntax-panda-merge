package com.panda.merge.cache;


import lombok.Data;

import java.io.Serializable;

@Data
public class FootballCacheScores implements Serializable {

    /**
     * 老redis key的字段：STANDARD_MATCH_SCORES，已弃用
     */
    //加时赛比分
    private CommonItem goalOverTime;
    //点球大战比分
    private CommonItem goalPenalty;


    /**
     * 新redis key的字段：FOOTBALL_STANDARD_MATCH_SCORES
     */
    //常规赛比分（常规不包含加时）
    private CommonItem corner;
    private CommonItem goal;
    private CommonItem faCard;
    private CommonItem redCard;
    private CommonItem yellowCard;

    //上半场比分
    private CommonItem hfCorner;
    private CommonItem hfGoal;
    private CommonItem hfFaCard;
    private CommonItem hfRedCard;
    private CommonItem hfYellowCard;
    //下半场比分
    private CommonItem htCorner;
    private CommonItem htGoal;
    private CommonItem htFaCard;
    private CommonItem htRedCard;
    private CommonItem htYellowCard;
    //加时赛比分
    private CommonItem overTimeGoal;
    private CommonItem overTimeFaCard;
    private CommonItem overTimeCorner;
    private CommonItem overTimeRedCard;
    private CommonItem overTimeYellowCard;
    //加时赛上半场比分
    private CommonItem overTimeHfGoal;
    private CommonItem overTimeHfFaCard;
    private CommonItem overTimeHfCorner;
    private CommonItem overTimeHfRedCard;
    private CommonItem overTimeHfYellowCard;

    //加时赛下半场比分
    private CommonItem overTimeHtGoal;
    private CommonItem overTimeHtFaCard;
    private CommonItem overTimeHtCorner;
    private CommonItem overTimeHtRedCard;
    private CommonItem overTimeHtYellowCard;
    //点球大战比分
    private CommonItem penaltyScores;

}
