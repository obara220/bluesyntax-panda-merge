package com.panda.merge.cache;


import lombok.Data;

import java.io.Serializable;

@Data
public class FootballCacheScores implements Serializable {
    /** 角球*/
    private CommonItem corner ;
    /** 常规赛事比分*/
    private CommonItem goal ;

    /**
     * 加时赛比分
     * 新redis key加的字段：FOOTBALL_STANDARD_MATCH_SCORES
     */
    private CommonItem overTimeGoal ;

    /** 加时赛比分*/
    private CommonItem goalOverTime ;
    /** 点球大战比分*/
    private CommonItem goalPenalty ;
    /** 红牌*/
    private CommonItem redCard ;
    /** 黄牌*/
    private CommonItem yellowCard ;
    /** 罚牌*/
    private CommonItem faCard ;
}
