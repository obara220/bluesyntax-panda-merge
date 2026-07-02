package com.panda.merge.common.enums;

import java.io.Serializable;

/**
 * 设置事件类型用于接入时比对设置特定的事件类型
 *
 * @author :  Franz
 * @Project Name :  data-realtime
 * @Package Name :  com.panda.sport.data.realtime.common.enums
 * @Description :  设置事件类型用于接入时比对设置特定的事件类型
 * @Date: 2019-11-11 11:00
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public enum EventTypeEnum implements Serializable {
    DELETE_EVENT( "删除事件"),
    DELETE_EVENT_ALERT( "删除事件");
    /**
     * 描述说明
     */
    public String desc;

    EventTypeEnum(String desc) {
        this.desc = desc;
    }

    /**
     * 根据事件编码值获取枚举对象用于比对
     *
     * @param eventCode 事件编码
     * @return MatchEventTypeEnum 具体对象
     */
    public static EventTypeEnum getEnum(String eventCode) {
        for (EventTypeEnum moduleEnum : EventTypeEnum.values()) {
            if (moduleEnum.name().equalsIgnoreCase(eventCode)) {
                return moduleEnum;
            }
        }
        return null;
    }

}
