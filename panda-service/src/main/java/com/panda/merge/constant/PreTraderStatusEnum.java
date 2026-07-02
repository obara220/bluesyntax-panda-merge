package com.panda.merge.constant;

public enum PreTraderStatusEnum {

    /***未设置***/
    Not_Set,
    /***待审批***/
    Pending_Approval,
    /***已设置***/
    Setted,
    /***取消取消未设置***/
    Cancel_Sold_Not_Set
    ;

    /**
     * 具体的枚举对象
     *
     * @return
     */
    public static PreTraderStatusEnum getEnum(String value) {
        for (PreTraderStatusEnum preTraderStatusEnum : PreTraderStatusEnum.values()) {
            if (preTraderStatusEnum.name().equalsIgnoreCase(value)) {
                return preTraderStatusEnum;
            }
        }
        return null;
    }
}
