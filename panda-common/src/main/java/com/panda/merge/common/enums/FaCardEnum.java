package com.panda.merge.common.enums;

import lombok.Getter;

/**
 * 罚牌类型枚举
 * @author  idol
 * @since   2022年3月23日18:46:53
 * 2.罚牌类型   1  主队黄牌   2.客队黄牌  3.主队红牌  4.客队红牌  5.没有罚牌  null -
 * */
@Getter
public enum FaCardEnum {

    Method_1(1, "主队黄牌","Home Yellow Card"),
    Method_2(2, "客队黄牌","Away Yellow Card"),
    Method_3(3, "主队红牌","Home Red Card"),
    Method_4(4, "客队红牌","Away Red Card"),
    Method_5(5, "没有罚牌","none"),
    Method_6(6, "走水","Return"),

    type_1(10001, "冻结 ","Stop"),
    type_2(10002, "取消冻结 ","Resume"),

    ;

    private Integer code;
    private String name;
    private String msg;

    FaCardEnum(Integer code, String name, String msg) {
        this.code = code;
        this.name = name;
        this.msg = msg;
    }

    public static FaCardEnum getFaCardEnumByCode(Integer code) {
        for (FaCardEnum item : FaCardEnum.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }

}
