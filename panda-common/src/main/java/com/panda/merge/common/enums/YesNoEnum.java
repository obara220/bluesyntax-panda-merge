package com.panda.merge.common.enums;


/**
 * 是否状态枚举
 * @author  tell
 * @since   2020年9月11日19:27:39
 */
public enum YesNoEnum {
    Y(1, "是"),
    N(0, "否"),
    Other(2, "临时状态");

    public Integer value;
    public String desc;

    YesNoEnum(Integer value, String desc) {
        this.desc = desc;
        this.value = value;
    }

    /**
     * 是否状态枚举对象
     * @param value Y,N
     * @return 具体的枚举对象
     */
    public static YesNoEnum getEnum(Integer value) {
        for (YesNoEnum ynEnum : YesNoEnum.values()) {
            if (ynEnum.value.equals(value)) {
                return ynEnum;
            }
        }
        return null;
    }
}
