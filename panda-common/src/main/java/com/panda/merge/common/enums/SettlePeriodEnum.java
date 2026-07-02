package com.panda.merge.common.enums;



/**
 * 结算日志枚举  (不可改动-idol)
 */

public enum SettlePeriodEnum {

    OverTime_1H(41L, "加时赛上半场","Overtime 1H"),
    OverTime_2H(42L, "加时赛下半场","Overtime 2H"),
    GOAL_1H(6L, "上半场","1H"),
    GOAL_2H(7L, "下半场","2H"),
    ;
    public Long value;
    public String desc;
    public String name;

    SettlePeriodEnum(Long value, String desc, String name) {
        this.value = value;
        this.desc = desc;
        this.name = name;
    }

    SettlePeriodEnum() {
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 根据状态值获取枚举对象
     *
     * @param statusValue 状态值
     * @return MatchStatusEnum 具体状态枚举对象
     */
    public static SettlePeriodEnum getEnum(Long statusValue) {
        for (SettlePeriodEnum matchStatusEnum : SettlePeriodEnum.values()) {
            if (matchStatusEnum.value.equals(statusValue)) {
                return matchStatusEnum;
            }
        }
        return null;
    }


}
