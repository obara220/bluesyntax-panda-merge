package com.panda.merge.common.enums;

/**
 * 是否取消枚举
 *
 * @author :  Franz
 * @Project Name :  data-realtime
 * @Package Name :  com.panda.sport.data.realtime.common.enums
 * @Description :  是否取消枚举
 * @Date: 2019-11-11 11:29
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public enum CanceledEnum {
    Canceled(1),//被取消
    Not_Canceled(0),//没有被取消
    ;
    public int value;

    CanceledEnum(int value) {
        this.value = value;
    }

    /**
     * 根据事件数据源类型获取枚举对象用于比对
     *
     * @param value 状态值
     * @return SourceTypeEnum 具体对象
     */
    public static CanceledEnum getEnum(int value) {
        for (CanceledEnum sourceTypeEnum : CanceledEnum.values()) {
            if (sourceTypeEnum.value == value) {
                return sourceTypeEnum;
            }
        }
        return null;
    }
}
