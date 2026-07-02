package com.panda.merge.common.enums;

/**
 * 视频状态枚举
 * @author : tell
 */
public enum VideoStatusEnum {
    NUM_1(1L, "预告"),
    NUM_2(2L, "准备中"),
    NUM_3(3L, "直播中"),
    NUM_4(4L, "视频中断"),
    NUM_10(10L, "已结束"),
    NUM_11(11L, "取消"),
    ;

    private Long code;

    private String value;

    VideoStatusEnum(Long code, String val) {
        this.code = code;
        this.value = val;
    }

    public Long getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
}
