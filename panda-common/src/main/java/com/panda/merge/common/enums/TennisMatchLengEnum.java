package com.panda.merge.common.enums;

import lombok.Getter;

/**
 * 获胜方式枚举
 * @author  idol
 * @since   2023年4月7日18:35:55
 * */
@Getter
public enum TennisMatchLengEnum {

    Method_1(1, "长盘制","LongGame"),
    Method_2(2, "抢七制","Seven"),
    Method_3(3, "抢十制(单)","Ten(One)"),
    Method_4(4, "抢十制(多)","Ten(More)"),
    Method_5(5, "特规制","Special"),



    ;

    private Integer code;
    private String name;
    private String msg;

    TennisMatchLengEnum(Integer code, String name, String msg) {
        this.code = code;
        this.name = name;
        this.msg = msg;
    }

    public static String TennisMatchLengEnumByZs(String code) {
        for (TennisMatchLengEnum item : TennisMatchLengEnum.values()) {
            if (item.code.toString().equals(code)) {
                return item.getName();
            }
        }
        return null;
    }



    public static String TennisMatchLengEnumByEn(String code) {
        for (TennisMatchLengEnum item : TennisMatchLengEnum.values()) {
            if (item.code.toString().equals(code)) {
                return item.getMsg();
            }
        }
        return null;
    }
}
