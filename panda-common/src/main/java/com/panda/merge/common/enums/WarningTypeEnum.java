package com.panda.merge.common.enums;

public enum WarningTypeEnum {

    /**
     * 赛事跨阶段下发修正/删除比分
     */
    CROSS_PERIOD_SCORE_CHANGED("103715","赛事跨阶段下发修正/删除比分")
    ;

    private final String code;

    private final String desc;

    WarningTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

}
