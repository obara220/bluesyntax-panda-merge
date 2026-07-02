package com.panda.merge.common.enums;

/**
 * @author :  gasol
 * @Project Name :  association
 * @Package Name :  com.panda.sport.data.association.service
 * @Description :  是否使用状态通用枚举类
 * @Date: 2019-09-02 17:02
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public enum ActiveEnum {
    // 该数据源是否使用。1：使用；0：不使用
    NONUSE(0, "不使用"),

    USE(1, "使用"),
    ;

    private Integer code;

    private String val;

    ActiveEnum(Integer code, String val) {
        this.code = code;
        this.val = val;
    }

    public Integer getCode() {
        return code;
    }

    public String getVal() {
        return val;
    }


    /**
     * 根据是否使用状态code获取一个枚举对象
     *
     * @param code 是否取用编号 0,1
     * @return 具体的枚举对象
     */
    public static ActiveEnum getEnum(int code) {
        for (ActiveEnum activeEnum : ActiveEnum.values()) {
            if (activeEnum.code == code) {
                return activeEnum;
            }
        }
        return null;
    }
}
