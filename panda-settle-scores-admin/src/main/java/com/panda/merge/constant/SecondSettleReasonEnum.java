package com.panda.merge.constant;

import lombok.Getter;

@Getter
public enum SecondSettleReasonEnum {
    OFFICIAL_ERROR(80, "官网错误"),
    DATA_SOURCE_ERROR(81, "数据商错误"),
    SYSTEM_ERROR(82, "系统问题"),
    PEOPLE_OPERATION_ERROR(83, "人为错误"),
    MATCH_RESULT_UNCHANGE(84, "赛果不变"),
    OTHERS(85, "待定");

    private final Integer code;
    private final String name;

    SecondSettleReasonEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static SecondSettleReasonEnum getByCode(Integer code) {
        for (SecondSettleReasonEnum value : SecondSettleReasonEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
