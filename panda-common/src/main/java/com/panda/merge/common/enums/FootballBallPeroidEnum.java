package com.panda.merge.common.enums;

import lombok.Getter;
import lombok.Setter;

public enum FootballBallPeroidEnum {

    /**
     * 足球阶段中英文(阶段系比分中心页面下标)
     */
    HF_YELLOW(1, "上半场黄牌", "HF YELLOW_CARD"),
    FT_YELLOW(2, "下半场黄牌", "FT YELLOW_CARD"),
    YELLOW(3, "-", "-"),
    OT_YELLOW(4, "加时赛黄牌", "ot YELLOW_CARD"),
    HF_RED(5, "上半场红牌", "HF RED_CARD"),
    FT_RED(6, "下半场红牌", "FT RED_CARD"),
    RED(7, "-", "-"),
    OT_RED(8, "加时赛红牌", "OT RED_CARD"),
    HF_CORNER(9, "上半场角球", "HF CORNER"),
    FT_CORNER(10, "下半场角球", "FT CORNER"),
    CORNER(11, "-", "-"),
    OT_CORNER(12, "加时赛角球", "OT CORNER"),
    HF_GOAL(13, "上半场进球", "HF GOAL"),
    FT_GOAL(14, "下半场进球", "FT GOAL"),
    GOAL(15, "-", "-"),
    OT_GOAL(16, "加时赛进球", "OT GOAL"),
    PENALTY_AWARDED(17, "常规赛点球", "PENALTY AWARADED"),
    PENALTY(18, "点球大战", "PENALTY"),
    SCORES_CENTER_SWITCH_EDIT(100965,"与主数据源联动","Follow data source"),
    SPORT_RESULT_SHOW_STATUS(100094,"比分中心显示隐藏","scores center show status"),
    SCORES_CENTER_SETTLE(100095,"比分中心结算","scores center settle"),
    SCORES_CENTER_MATCH_STATUS(100096,"赛果显示","match result show"),
    SCORES_CENTER_MANUAL_SCORE(100961,"手动输入比分","manual input score"),
    SCORES_CENTER_MODIFY_DATASOURCE(100962,"修改主数据源","modify main datasource"),
    SCORES_CANCEL_WITH_ONE_CLICK(100300,"一键取消","One-click cancel"),
    SCORES_CENTER_MODIFY_MODIFY_STATUS(100963,"修改赛果显示","modify match result show"),
    SCORES_CENTER_MAIN_DATASOURCE(100964,"主数据源","main datasource"),
    SCORES_CENTER_OPEN(10000001,"开","ON"),
    SCORES_CENTER_CLOSE(10000000,"关","OFF"),
    SCORES_CENTER_MATCH_SETTING(100097,"赛果显示设置","match result show setting"),
    SCORES_CENTER_DEFAULT(100098,"默认值","default"),
    SCORES_CENTER_DEFAULT_SETTING(100099,"默认值设置","default setting"),

    SCORES_CENTER_SETTLE_CONV(100201,"常规比分下发","Match Scores Settle"),
    SCORES_CENTER_SETTLE_BREAK(100202,"比赛中断比分下发","Match Interruption Settle"),
    MIN_0_15_GOAL(21, "0~14:59 分钟进球", "0~14:59 GOAL"),
    MIN_0_15_CORNER(22, "0~14:59 分钟角球", "0~14:59 CORNER"),
    MIN_0_15_RED_CARD(23, "0~14:59 分钟红牌", "0~14:59 RED_CARD"),
    MIN_0_15_YELLOW_CARD(24, "0~14:59 分钟黄牌", "0~14:59 YELLOW_CARD"),
    MIN_15_30_GOAL(25, "15~29:59 分钟进球", "15~29:59 GOAL"),
    MIN_15_30_CORNER(26, "15~29:59 分钟角球", "15~29:59 CORNER"),
    MIN_15_30_RED_CARD(27, "15~29:59 分钟红牌", "15~29:59 RED_CARD"),
    MIN_15_30_YELLOW_CARD(28, "15~29:59 分钟黄牌", "15~29:59 YELLOW_CARD"),
    MIN_30_45_GOAL(29, "30~44:59 分钟进球", "30~44:59 GOAL"),
    MIN_30_45_CORNER(30, "30~44:59 分钟角球", "30~44:59 CORNER"),
    MIN_30_45_RED_CARD(31, "30~44:59 分钟红牌", "30~44:59 RED_CARD"),
    MIN_30_45_YELLOW_CARD(32, "30~44:59 分钟黄牌", "30~44:59 YELLOW_CARD"),
    MIN_45_60_GOAL(33, "45~59:59 分钟进球", "45~59:59 GOAL"),
    MIN_45_60_CORNER(34, "45~59:59 分钟角球", "45~59:59 CORNER"),
    MIN_45_60_RED_CARD(35, "45~59:59 分钟红牌", "45~59:59 RED_CARD"),
    MIN_45_60_YELLOW_CARD(36, "45~59:59 分钟黄牌", "45~59:59 YELLOW_CARD"),
    MIN_60_75_GOAL(37, "60~74:59 分钟进球", "60~74:59 GOAL"),
    MIN_60_75_CORNER(38, "60~74:59 分钟角球", "60~74:59 CORNER"),
    MIN_60_75_RED_CARD(39, "60~74:59 分钟红牌", "60~74:59 RED_CARD"),
    MIN_60_75_YELLOW_CARD(40, "60~74:59 分钟黄牌", "60~74:59 YELLOW_CARD"),
    MIN_75_90_GOAL(41, "75~89:59 分钟进球", "75~89:59 GOAL"),
    MIN_75_90_CORNER(42, "75~89:59 分钟角球", "75~89:59 CORNER"),
    MIN_75_90_RED_CARD(43, "75~89:59 分钟红牌", "75~89:59 RED_CARD"),
    MIN_75_90_YELLOW_CARD(44, "75~89:59 分钟黄牌", "75~89:59 YELLOW_CARD"),

    ;

    @Getter
    @Setter
    private Integer code;

    @Getter
    @Setter
    private String valueZh;

    @Getter
    @Setter
    private String valueEn;

    FootballBallPeroidEnum(Integer code, String valueZh, String valueEn) {
        this.code = code;
        this.valueZh = valueZh;
        this.valueEn = valueEn;
    }

    public static FootballBallPeroidEnum getEnum(Integer code) {
        for (FootballBallPeroidEnum basketBallPeroidEnum : FootballBallPeroidEnum.values()) {
            if (basketBallPeroidEnum.getCode().equals(code)) {
                return basketBallPeroidEnum;
            }
        }
        return null;
    }

    public static String getEnumByZs(String code) {
        for (FootballBallPeroidEnum footballBallPeroidEnum : FootballBallPeroidEnum.values()) {
            if (footballBallPeroidEnum.getCode().toString().equals(code)) {
                return footballBallPeroidEnum.getValueZh();
            }
        }
        return code;
    }

    public static String getEnumByEn(String code) {
        for (FootballBallPeroidEnum footballBallPeroidEnum : FootballBallPeroidEnum.values()) {
            if (footballBallPeroidEnum.getCode().toString().equals(code)) {
                return footballBallPeroidEnum.getValueEn();
            }
        }
        return code;
    }
}
