package com.panda.merge.common.enums;

import lombok.Getter;

/**
 * 赛事类型
 */
@Getter
public enum MatchTypeEnum {
    NORMAL(1,"普通赛事"),
    ESPORTS(2,"电竞赛事"),
    BASKETBALL3x3(3,"篮球3x3(如果运动类型为篮球）"),
    MMA(4,"MMA(如果运动类型为拳击）")
    ;
    private Integer code;
    private String msg;

    MatchTypeEnum(Integer code,String msg) {
        this.code = code;
        this.msg = msg;
    }

}
