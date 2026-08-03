package com.panda.merge.common.enums;

import lombok.Getter;
import lombok.Setter;

public enum SportBallPeroidEnum {

    /**
     * 篮球阶段中英文
     */
    BK_1HT(8, "第一盘", "1th Quarter"),
    BK_2HT(9, "第二盘", "2th Quarter"),
    BK_2HT_OT(10, "第三盘", "3th Quarter"),
    BK_ET(11, "第四盘", "4th Quarter"),
    BK_FT_ET(12, "第五盘", "5th Quarter"),
    BK_FT_RG(13, "第六盘", "6th Quarter"),
    BK_Q101(14, "第七盘", "7th Quarter"),
    BK_FT_RG2(441, "第六盘", "6th Quarter"),
    BK_Q1013(442, "第七盘", "7th Quarter"),

    SCORES_CENTER_SWITCH_EDIT(100965, "与主数据源联动", "Follow data source"),
    SPORT_RESULT_SHOW_STATUS(100094, "比分中心显示隐藏", "scores center show status"),
    SCORES_CENTER_SETTLE(100095, "比分中心结算", "scores center settle"),
    SCORES_CENTER_MATCH_STATUS(100096, "赛果显示", "match result show"),
    SCORES_CENTER_MANUAL_SCORE(100961, "手动输入比分", "manual input score"),
    SCORES_CENTER_MODIFY_DATASOURCE(100962, "修改主数据源", "modify main datasource"),
    SCORES_CENTER_MODIFY_MODIFY_STATUS(100963, "修改赛果显示", "modify match result show"),
    SCORES_CENTER_MAIN_DATASOURCE(100964, "主数据源", "main datasource"),
    SCORES_CENTER_OPEN(10000001, "开", "ON"),
    SCORES_CENTER_CLOSE(10000000, "关", "OFF"),
    SCORES_CENTER_MATCH_SETTING(100097, "赛果显示设置", "match result show setting"),
    SCORES_CENTER_DEFAULT(100098, "默认值", "default"),
    SCORES_CANCEL_WITH_ONE_CLICK(100300,"一键取消","One-click cancel"),
    SCORES_CENTER_DEFAULT_SETTING(100099, "默认值设置", "default setting"),
    MINUTES_SCORES_CHECK_SWITCH(100203,"区间比分校验开关","Minutes scores check switch"),


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

    SportBallPeroidEnum(Integer code, String valueZh, String valueEn) {
        this.code = code;
        this.valueZh = valueZh;
        this.valueEn = valueEn;
    }

    public static SportBallPeroidEnum getEnum(Integer code) {
        for (SportBallPeroidEnum basketBallPeroidEnum : SportBallPeroidEnum.values()) {
            if (basketBallPeroidEnum.getCode().equals(code)) {
                return basketBallPeroidEnum;
            }
        }
        return null;
    }

    public static String getEnumByZs(String code) {
        for (SportBallPeroidEnum sportballBallPeroidEnum : SportBallPeroidEnum.values()) {
            if (sportballBallPeroidEnum.getCode().toString().equals(code)) {
                return sportballBallPeroidEnum.getValueZh();
            }
        }
        return code;
    }

    public static String getEnumByEn(String code) {
        for (SportBallPeroidEnum sportballBallPeroidEnum : SportBallPeroidEnum.values()) {
            if (sportballBallPeroidEnum.getCode().toString().equals(code)) {
                return sportballBallPeroidEnum.getValueEn();
            }
        }
        return code;
    }

}
