package com.panda.merge.common.enums;

import lombok.Getter;

@Getter
public enum MarketTypeEnum {
    PREMATCH(1), // 赛前盘
    LIVE(0);     // 滚球盘

    private final Integer code;

    MarketTypeEnum(Integer code) {
        this.code = code;
    }
}
