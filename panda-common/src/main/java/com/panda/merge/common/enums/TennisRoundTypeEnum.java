package com.panda.merge.common.enums;

import lombok.Getter;

/**
 * 获胜方式枚举
 * @author  idol
 * @since   2023年4月7日18:35:55
 * */
@Getter
public enum TennisRoundTypeEnum {


    Method_3(3, "3盘","3 Sets"),
    Method_5(5, "5盘","5 Sets"),



    ;

    private Integer code;
    private String name;
    private String msg;

    TennisRoundTypeEnum(Integer code, String name, String msg) {
        this.code = code;
        this.name = name;
        this.msg = msg;
    }

    public static String tennisRoundTypeEnumByZs(String code) {
        for (TennisRoundTypeEnum item : TennisRoundTypeEnum.values()) {
            if (item.code.toString().equals(code)) {
                return item.getName();
            }
        }
        return null;
    }



    public static String tennisRoundTypeEnumByEn(String code) {
        for (TennisRoundTypeEnum item : TennisRoundTypeEnum.values()) {
            if (item.code.toString().equals(code)) {
                return item.getMsg();
            }
        }
        return null;
    }
}
