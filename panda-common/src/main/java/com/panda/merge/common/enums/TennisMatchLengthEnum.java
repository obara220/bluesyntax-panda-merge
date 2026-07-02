package com.panda.merge.common.enums;

import lombok.Getter;

/**
 * 主客队类型枚举
 * @author  idol
 * @since   2023年3月1日18:52:57
 */
@Getter
public enum TennisMatchLengthEnum {
//"局制:1长盘制,2抢七制,3单人抢十,4双人抢十,5特"
    LONGTRAY("1", "15"),
    SEVEN("2", "13"),
    SOLO("3", "1"),
    DOUBLE("4", "13"),
    PARTICULARLY("5", "7"),
    ;

    public String code;
    public String value;

    TennisMatchLengthEnum(String code, String value) {
        this.code = code;
        this.value = value;
    }

    public static TennisMatchLengthEnum getByCode(String code) {
        for (TennisMatchLengthEnum item : TennisMatchLengthEnum.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }


}
