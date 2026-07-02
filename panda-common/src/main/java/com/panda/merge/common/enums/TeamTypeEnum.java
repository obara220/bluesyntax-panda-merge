package com.panda.merge.common.enums;

import lombok.Getter;

/**
 * 主客队类型枚举
 * @author  tell
 * @since   2020年12月22日14:29:42
 */
@Getter
public enum TeamTypeEnum {
    HOME("home", "主队"),
    AWAY("away", "客队"),
    ;

    public String code;
    public String msg;

    TeamTypeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }


    /**
     * 主客队互换
     * @param teamCode 球队类型
     */
    public static String homeAwayExchange(String teamCode) {
        //主队变为客队
        if(TeamTypeEnum.HOME.code.equals(teamCode)){
            return TeamTypeEnum.AWAY.code;
        }
        //客队变为主队
        if(TeamTypeEnum.AWAY.code.equals(teamCode)){
            return TeamTypeEnum.HOME.code;
        }
        return teamCode;
    }

}
