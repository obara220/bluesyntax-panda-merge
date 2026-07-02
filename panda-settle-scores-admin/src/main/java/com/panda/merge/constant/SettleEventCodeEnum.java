package com.panda.merge.constant;

import lombok.Getter;

@Getter
public enum SettleEventCodeEnum {

    FOOTBALL_GOAL(1, 1, "goal", "足球进球"),

    FOOTBALL_CORNER(1, 2, "corner", "足球角球"),

    FOOTBALL_FA_CARD(1, 3, "fa_card", "足球罚牌"),
    FOOTBALL_RED_CARD(1, 4, "red_card", "足球红牌"),
    FOOTBALL_YELLOW_CARD(1, 5, "yellow_card", "足球黄牌"),
    BASKETBALL_SCORE_CHANGE(2, 6, "score_change", "篮球进球");

    private Integer sportId;
    private final Integer code;
    private final String value;
    private final String name;


    SettleEventCodeEnum(Integer sportId, Integer code, String value, String name) {
        this.sportId = sportId;
        this.code = code;
        this.value = value;
        this.name = name;
    }

    public static SettleEventCodeEnum getEventCodeEnum(String eventCode) {
        for (SettleEventCodeEnum value : SettleEventCodeEnum.values()) {
            if (value.getValue().equals(eventCode)) {
                return value;
            }
        }
        return null;
    }

}
