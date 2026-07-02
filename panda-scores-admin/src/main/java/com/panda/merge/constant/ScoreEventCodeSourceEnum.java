package com.panda.merge.constant;

/**
 * 事件-数据源枚举
 * @author vere
 * @date 2025-03-21
 */
public enum ScoreEventCodeSourceEnum {
    PD("PD"),
    PD2("PD2");
    ScoreEventCodeSourceEnum(String type) {
        this.type = type;
    }
    /**
     * 串关类型
     */
    private String type;

    public String getType() {
        return type;
    }
    public static boolean getResult(String type) {
        for (ScoreEventCodeSourceEnum seriesTypeEnum:values()){
            if (seriesTypeEnum.type.equals(type)){
                return true;
            }
        }
        return false;
    }
}
