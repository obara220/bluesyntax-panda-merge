package com.panda.merge.common.enums;

/**
 * 开售日志表  操作类型枚举
 */

public enum SaleOperateTypeEnum {
    /***全部***/
    all,
    /***赛前操盘手***/
    pre_match,
    /***滚球操盘手***/
    live_odd,
    /***赛果审核员***/
    match_result,
    ;

    /**
     * 具体的枚举对象
     *
     * @return
     */
    public static SaleOperateTypeEnum getEnum(String value) {
        for (SaleOperateTypeEnum saleOperateTypeEnum : SaleOperateTypeEnum.values()) {
            if (saleOperateTypeEnum.name().equalsIgnoreCase(value)) {
                return saleOperateTypeEnum;
            }
        }
        return null;
    }

}
