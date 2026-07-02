package com.panda.merge.constant;

/**
 * @Description
 * @ClassName RiskManagerCodeEnums
 * @Author Top
 * @date 2020.06.20 14:18
 */
public enum RiskManagerCodeEnums {
    /**
     * Panda操盘
     */
    PA,
    /**
     * MTS操盘
     */
    MTS,
    /**
     * GTS操盘
     */
    GTS,
    /**
     * OTS操盘
     */
    OTS,
    /**
     * CTS操盘
     */
    CTS,
    /**
     * BTS操盘
     */
    BTS,
    /**
     * F2TS操盘
     */
    F2TS,
    ;

    /**
     * 具体的枚举对象
     *
     * @return
     */
    public static RiskManagerCodeEnums getEnum(String value) {
        for (RiskManagerCodeEnums riskManagerCodeEnum : RiskManagerCodeEnums.values()) {
            if (riskManagerCodeEnum.name().equalsIgnoreCase(value)) {
                return riskManagerCodeEnum;
            }
        }
        return null;
    }
}
