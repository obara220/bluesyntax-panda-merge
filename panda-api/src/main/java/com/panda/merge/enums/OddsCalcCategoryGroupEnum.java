package com.panda.merge.enums;


/**
 * @name: OddsCalcCategoryGroupEnum
 * @description: 赔率计算玩法分组枚举
 * @date: 1/14/2025
 **/
public enum OddsCalcCategoryGroupEnum {

    GROUP_NONE(0),
    GROUP_NORMAL(1),
    GROUP_50(2),
    GROUP_THREE_ODDS(3);

    public final Integer groupId;

    OddsCalcCategoryGroupEnum(int groupId) {
        this.groupId = groupId;
    }

    public static OddsCalcCategoryGroupEnum fromGroupId(Integer groupId) {
        if (groupId == null) {
            return GROUP_NONE;
        }
        for (OddsCalcCategoryGroupEnum groupEnum : OddsCalcCategoryGroupEnum.values()) {
            if (groupEnum.groupId.equals(groupId)) {
                return groupEnum;
            }
        }
        return GROUP_NONE;
    }
}
