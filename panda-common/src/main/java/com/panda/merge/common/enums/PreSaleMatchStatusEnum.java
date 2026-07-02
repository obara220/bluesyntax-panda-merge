package com.panda.merge.common.enums;

/**
 * 盘口开售状态枚举
 * @author : tell
 * @since    2020年9月13日11:39:38
 */
public enum PreSaleMatchStatusEnum {
    //正常或移入状态
    Enable,
    //移出
    Move_Out,
    //完赛
    End,
    ;

    /**
     * 具体的枚举对象
     *
     * @return
     */
    public static PreSaleMatchStatusEnum getEnum(String value) {
        for (PreSaleMatchStatusEnum preSaleMatchStatusEnum : PreSaleMatchStatusEnum.values()) {
            if (preSaleMatchStatusEnum.name().equalsIgnoreCase(value)) {
                return preSaleMatchStatusEnum;
            }
        }
        return null;
    }
}