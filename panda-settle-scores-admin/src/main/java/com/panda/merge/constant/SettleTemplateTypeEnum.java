package com.panda.merge.constant;

import lombok.Data;

/**
 *
 * 1. 数据商结算权重  2.结算倒计时模版 3.灰色区间模版
 *
 * */
public enum SettleTemplateTypeEnum {
    /**
     *数据商权重
     */
    DATA_SOURCE_WEIGHT(1, "数据商权重"),
    /**
     * 结算倒计时
     */
    COUNT_DOWEN(2, "结算倒计时"),
    /**
     * 灰色区间
     */
    GRAY_AREA(3, "灰色区间"),

    /**
     * 最大联赛等级
     */
    MAX_LEVEL(15, "最大联赛等级"),
    BASKETBALL_MAX_LEVEL(20, "篮球最大联赛等级"),
    ON_CODE(1,"开启"),
    OFF_CODE(0,"关闭"),
    INIT_TOP_WEIGHT(90,"初始权重上限"),
    INIT_TEMPLATE_WEIGHT(50,"模板初始权限"),
    INIT_GARY_TEMPLATE_SECOND(30,"灰色区间模板初始时间");


    public String typeName;
    public Integer code;

    SettleTemplateTypeEnum(Integer code, String name) {
        this.code = code;
        this.typeName = name;
    }

}
