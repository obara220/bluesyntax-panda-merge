package com.panda.merge.common.enums;

import java.io.Serializable;

/**
 * 联赛轮次类型
 * @author   tell
 * @since    2021年1月27日10:43:02
 */
public enum TournamentRoundTypeEnum implements Serializable {

    Other( 0,"其它"),
    Group( 1,"联赛"),
    Cup( 2,"杯赛"),
    Qualification( 3,"资格赛"),
    ;

    public Integer code;
    public String msg;

    TournamentRoundTypeEnum(Integer code,String msg) {
        this.code = code;
        this.msg = msg;
    }

}
