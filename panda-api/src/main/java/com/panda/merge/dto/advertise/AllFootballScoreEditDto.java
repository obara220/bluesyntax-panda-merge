package com.panda.merge.dto.advertise;

import lombok.Data;

@Data
public class AllFootballScoreEditDto extends AbstructAdvertiseDto  {

    /**
     * 三方赛事id
     * */
    private Long thirdMatchId;
    /**
     * 进球
     * */
    private Integer htGoalT1;//    上半场主队进球

    private Integer htGoalT2;//    上半场客队进球

    private Integer ht2GoalT1;//    下半场主队进球

    private Integer ht2GoalT2;//    下半场客队进球



    private Integer exGoalT1;//    加时赛上半场主队进球

    private Integer exGoalT2;//    加时赛上半场客队进球

    private Integer ex2GoalT1;//    加时赛下半场主队进球

    private Integer ex2GoalT2;//    加时赛下半场客队进球

    /**
     * 角球
     * */
    private Integer htCornerT1 ;//  上半场主队角球

    private Integer htCornerT2 ;// 上半场客队角球

    private Integer ht2CornerT1;//   下半场主队角球

    private Integer ht2CornerT2 ;//  下半场客队角球


    private Integer exCornerT1;//    加时赛上半场主队进球

    private Integer exCornerT2;//    加时赛上半场客队进球

    private Integer ex2CornerT1;//    加时赛下半场主队进球

    private Integer ex2CornerT2;//    加时赛下半场客队进球

    /**
     * 黄牌
     * */
    private Integer htYellowCardT1 ;//  上半场主队角球

    private Integer htYellowCardT2 ;// 上半场客队角球

    private Integer ht2YellowCardT1;//   下半场主队角球

    private Integer ht2YellowCardT2 ;//  下半场客队角球


    private Integer exYellowCardT1;//    加时赛上半场主队进球

    private Integer exYellowCardT2;//    加时赛上半场客队进球

    private Integer ex2YellowCardT1;//    加时赛下半场主队进球

    private Integer ex2YellowCardT2;//    加时赛下半场客队进球
    /**
     * 红牌
     * */
    private Integer htRedCardT1 ;//  上半场主队角球

    private Integer htRedCardT2 ;// 上半场客队角球

    private Integer ht2RedCardT1;//   下半场主队角球

    private Integer ht2RedCardT2 ;//  下半场客队角球


    private Integer exRedCardT1;//    加时赛上半场主队进球

    private Integer exRedCardT2;//    加时赛上半场客队进球

    private Integer ex2RedCardT1;//    加时赛下半场主队进球

    private Integer ex2RedCardT2;//    加时赛下半场客队进球
}
