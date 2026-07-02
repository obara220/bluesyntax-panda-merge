package com.panda.merge.common.enums;

import lombok.Getter;

/**
 * 伤停补时枚举类
 *
 * @author warren
 * @since 2023/12/06 19:27:34
 */
@Getter
public enum TimeStatusEnum {
    /**
     * 继续
     */
    CONTINUE(1, "继续"),

    /**
     *
     */
    INIT_PERSIST(-1, "初始化时间"),

    /**
     * 暂停
     */
    PAUSE(0, "暂停");

    public final Integer desc;
    public final String msg;

    TimeStatusEnum(Integer desc, String msg) {
        this.desc = desc;
        this.msg = msg;
    }
}
