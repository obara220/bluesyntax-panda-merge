package com.panda.merge.common.enums;

import lombok.Getter;

/**
 * 盘口类型枚举类型
 * @author  tell
 * @since   2020年12月9日14:23:32
 */
@Getter
public enum HandicapTypeEnum {
    MATCHUNBEGIN(1,"赛前盘"),
    MATCHINPLAY(0,"滚球盘");

    private Integer code;
    private String message;

    HandicapTypeEnum(Integer code, String message){
        this.code = code;
        this.message = message;
    }

}
