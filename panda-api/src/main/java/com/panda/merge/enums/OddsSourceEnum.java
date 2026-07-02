package com.panda.merge.enums;

public enum OddsSourceEnum {
    /**其他*/
    SOURCE_OTHER(-1),
    /**数据源*/
    SOURCE_DATA(0),
    /**分时节点*/
    SOURCE_TIME_SHARING_NODE(1),
    /**跳分*/
    SOURCE_CHANGE_POINTS(2),
    /**跳水*/
    SOURCE_CHANGE_MARGIN(3),
    /**封盘开盘*/
    SOURCE_STATUS_OPERATE(4);

    public final Integer groupId;

    OddsSourceEnum(Integer groupId){this.groupId = groupId;}

    public static OddsSourceEnum fromGroupId(Integer groupId) {
        if (groupId == null) {
            return SOURCE_OTHER;
        }
        for (OddsSourceEnum groupEnum : OddsSourceEnum.values()) {
            if (groupEnum.groupId.equals(groupId)) {
                return groupEnum;
            }
        }
        return SOURCE_OTHER;
    }
}
