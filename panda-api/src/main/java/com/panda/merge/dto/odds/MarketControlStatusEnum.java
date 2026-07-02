package com.panda.merge.dto.odds;

/**
 * MarketControlStatusEnum
 *
 * @description: 盘口内部控制状态
 * @date: 6/23/2025
 **/
public enum MarketControlStatusEnum {

    // 控制状态 识别盘口未数据源切换时旧数据源关盘

    // 控制状态 盘口不再参与后续状态校验
    VALIDATED(61),
    // 控制状态 盘口状态不在变更,
    FINAL(62);

    public final Integer code;

    MarketControlStatusEnum(Integer code) {
        this.code = code;
    }
}
