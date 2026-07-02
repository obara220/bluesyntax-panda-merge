package com.panda.merge.constant;


/**
 * 人工操盘赛事状态枚举
 * @version 1.0.0
 * @author vere
 * @date 2025-03-30
 */
public enum OperateMatchStatusEnum {
    /**
     * 开盘
     */
    ACTIVE(0, "开盘"),

    /**
     * 封盘 suspended
     */

    SUSPENDED(1, "封盘"),
    /**
     * 关盘 closed
     */
    CLOSED(2, "关盘"),
    /**
     * 锁盘 deactivated
     */
    DEACTIVATED(11, "锁盘"),

    ;

    private String name;
    private Integer value;

    OperateMatchStatusEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getName() {
        return this.name;
    }
}
