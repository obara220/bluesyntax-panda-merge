package com.panda.merge.dto.sourceSwitch;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;


/**
 * 足球标准比分中心与数据源联动开关
 * fymen
 */
@Slf4j
@Data
public class FootballSwitch{
    //上半场黄牌
    private int yellowHf;
    //下半场黄牌
    private int yellowFt;
    //加时赛黄牌
    private int yellowOt;
    //上半场红牌
    private int redHf;
    //下半场红牌
    private int redFt;
    //加时赛红牌
    private int redOt;
    //上半场角球
    private int cornerHf;
    //下半场角球
    private int cornerFt;
    //加时赛角球
    private int cornerOt;
    //上半场进球
    private int goalHf;
    //下半场进球
    private int goalFt;
    //加时赛进球
    private int goalOt;
    //常规点球
    private int penaltyAwarded;
    //点球大战
    private int penalty;

    private int goal60899;
    private int goal61799;
    private int goal62699;
    private int goal73599;
    private int goal74499;
    private int goal75399;
    private int corner60899;
    private int corner61799;
    private int corner62699;
    private int corner73599;
    private int corner74499;
    private int corner75399;
    private int redCard60899;
    private int redCard61799;
    private int redCard62699;
    private int redCard73599;
    private int redCard74499;
    private int redCard75399;
    private int yellowCard60899;
    private int yellowCard61799;
    private int yellowCard62699;
    private int yellowCard73599;
    private int yellowCard74499;
    private int yellowCard75399;

    public FootballSwitch(){
        this.yellowFt = 1;
        this.yellowHf = 1;
        this.yellowOt = 1;
        this.redFt = 1;
        this.redHf = 1;
        this.redOt = 1;
        this.cornerFt = 1;
        this.cornerHf = 1;
        this.cornerOt = 1;
        this.goalFt = 1;
        this.goalHf = 1;
        this.goalOt = 1;
        this.penalty = 1;
        this.penaltyAwarded = 1;
        this.goal60899 = 1;
        this.goal61799 = 1;
        this.goal62699 = 1;
        this.goal73599 = 1;
        this.goal74499 = 1;
        this.goal75399 = 1;
        this.corner60899 = 1;
        this.corner61799 = 1;
        this.corner62699 = 1;
        this.corner73599 = 1;
        this.corner74499 = 1;
        this.corner75399 = 1;
        this.redCard60899 = 1;
        this.redCard61799 = 1;
        this.redCard62699 = 1;
        this.redCard73599 = 1;
        this.redCard74499 = 1;
        this.redCard75399 = 1;
        this.yellowCard60899 = 1;
        this.yellowCard61799 = 1;
        this.yellowCard62699 = 1;
        this.yellowCard73599 = 1;
        this.yellowCard74499 = 1;
        this.yellowCard75399 = 1;
    }
}

