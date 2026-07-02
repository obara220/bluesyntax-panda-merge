package com.panda.merge.constant;

import lombok.Getter;

@Getter
public enum SettleMentionEnum {
    FOOTBALL_DELETE_EVENT(1L, 1, 1, "deleteStatus", "足球-删除事件提示"),
    FOOTBALL_SCORE_MISMATCH(1L,2,2, "dataMismatchStatus", "足球-数据不匹配"),
    FOOTBALL_PHASE_SCORE_MISMATCH(1L,2, 3, "dataMismatchStatus", "足球-数据不匹配"),
    BASKETBALL_PHASE_SCORE_MISMATCH(2L,2, 4, "dataMismatchStatus", "篮球-数据不匹配"),
    BASKETBALL_GRAY_AREA(2L,3, 5, "grayAreaStatus", "篮球-灰色区间");

    private final Long sportId;
    private final Integer type;
    private final Integer code;
    private final String value;
    private final String name;


    SettleMentionEnum(Long sportId, Integer type, Integer code, String value, String name) {
        this.sportId = sportId;
        this.type = type;
        this.code = code;
        this.value = value;
        this.name = name;
    }

    public static SettleMentionEnum getEnumByMentionCode(Integer code) {
        for (SettleMentionEnum value : SettleMentionEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }

    public static SettleMentionEnum getEnumBySportIdAndCode(Long sportId, Integer type) {
        for (SettleMentionEnum value : SettleMentionEnum.values()) {
            if (value.getSportId().equals(sportId) && value.getType().equals(type)) {
                return value;
            }
        }
        return null;
    }
}
