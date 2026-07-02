package com.panda.merge.constant;

/**
 * @author : nonhung
 * @project Name : panda_data_association
 * @package Name : com.panda.sport.data.association.api.enums
 * @description : 赛前开售状态
 * @date: 2019-12-31 16:34
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */
public enum SaleMatchSellStausEnum {
    /***未售***/
    Unsold,
    /***逾期未售***/
    Overdue_Unsold,
    /***申请延期***/
    Apply_Delay,
    /***开售***/
    Sold,
    /***申请停售***/
    Apply_Stop_Sold,
    /***停售***/
    Stop_Sold,
    /***意外停售***/
    Expected_End_Sold,
    /***取消开售***/
    Cancel_Sold;
    /**
     * 具体的枚举对象
     *
     * @return
     */
    public static SaleMatchSellStausEnum getEnum(String value) {
        for (SaleMatchSellStausEnum saleMatchSellStausEnum : SaleMatchSellStausEnum.values()) {
            if (saleMatchSellStausEnum.name().equalsIgnoreCase(value)) {
                return saleMatchSellStausEnum;
            }
        }
        return null;
    }

}
