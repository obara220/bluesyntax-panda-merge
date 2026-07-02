package com.panda.merge.constant;

public enum SellTypeEnums {
    /**
     * 早盘
     */
    PRE,
    /**
     * 滚球
     */
    LIVE,
    ;

    /**
     * 具体的枚举对象
     *
     * @return
     */
    public static SellTypeEnums getEnum(String value) {
        for (SellTypeEnums sellTypeEnum : SellTypeEnums.values()) {
            if (sellTypeEnum.name().equalsIgnoreCase(value)) {
                return sellTypeEnum;
            }
        }
        return null;
    }
}
