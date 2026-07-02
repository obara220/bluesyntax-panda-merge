package com.panda.merge.common.enums;

import lombok.Getter;

/**
 * @author warren
 * @since 2024/04/10 12:17:20
 */
@Getter
public enum PDScoreChangeEnum {
    NUMBER_LESS_EQUAL_ZERO(1, "比分小于等于0"),
    EDIT_SIX_SCORE(2,"编辑6分钟比分"),
    NUMBER_LESS_ZERO(3, "小于0"),
    SCORE_EQUAL(4,"修改前后比分相等"),
    OPERATE_NORMAL(999,"操作正常"),
    ;

    private final Integer code;
    private final String msg;

    PDScoreChangeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
