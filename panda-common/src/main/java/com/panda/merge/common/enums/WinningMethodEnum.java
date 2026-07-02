package com.panda.merge.common.enums;

import lombok.Getter;

/**
 * 获胜方式枚举
 * @author  idol
 * @since   2022年3月23日18:46:53
 * */
@Getter
public enum WinningMethodEnum {

    Method_1(1, "主队常规时间","Home Regular Time"),
    Method_2(2, "客队常规时间","Away Regular Time"),
    Method_3(3, "主队加时赛","Home Overtime"),
    Method_4(4, "客队加时赛","Away Overtime"),
    Method_5(5, "主队点球大战","Home Penalties"),
    Method_6(6, "客队点球大战","Away Penalties"),
    Method_7(7, "没有","none"),
    Method_8(8, "走水","Return"),

    type_1(10001, "冻结 ","Stop"),
    type_2(10002, "取消冻结 ","Resume"),

    ;

    private Integer code;
    private String name;
    private String msg;

    WinningMethodEnum(Integer code, String name, String msg) {
        this.code = code;
        this.name = name;
        this.msg = msg;
    }

    public static WinningMethodEnum getWinningMethodByCode(Integer code) {
        for (WinningMethodEnum item : WinningMethodEnum.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }

}
