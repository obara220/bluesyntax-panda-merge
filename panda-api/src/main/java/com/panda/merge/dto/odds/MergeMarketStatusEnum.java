package com.panda.merge.dto.odds;

/**
 * MergeMarketStatusEnum
 *
 * @description: 融合盘口内部状态枚举
 * @date: 4/20/2025
 **/
public enum MergeMarketStatusEnum {

    // 内部状态追踪
    DATASOURCE(0),
    DATA_SOURCE_SWITCH_OLD_CLOSE(11),
    DATASOURCE_INVALID(12),
    CLOSE_DISPLAY(21),
    SCORE_CLOSE(22),
    AUTO_CLOSE(23),
    MATCH(31),
    MATCH_END(32),
    PLACE_NUM(41),
    MARKET_END(42),
    CATEGORY_SET(51),
    CATEGORY_INVALID(52);




    public final Integer code;

    MergeMarketStatusEnum(Integer code) {
        this.code = code;
    }

}
