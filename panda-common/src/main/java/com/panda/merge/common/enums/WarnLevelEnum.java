package com.panda.merge.common.enums;

import lombok.Getter;

/**
 * 异常监听报警级别
 * @author  tell
 * @since   2021年10月2日12:19:31
 * */
@Getter
public enum WarnLevelEnum {


    level1(1, "非常严重"),
    level2(2, "严重"),
    level3(3, "普通"),
    level99(99, "恢复"),
    ;

    private Integer code;
    private String msg;

    WarnLevelEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static WarnLevelEnum getWarnLevelByCode(Integer code) {
        for (WarnLevelEnum item : WarnLevelEnum.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return WarnLevelEnum.level3;
    }

}
