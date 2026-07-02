package com.panda.merge.enums;

import lombok.Getter;

@Getter
public enum RequestTypeEnum {
    G_GOAL("10001", "g_goal", "常规进球"),
    G_CORNER("10002", "g_corner", "常规角球"),
    G_BOOKING("10003", "g_booking", "常规罚牌"),
    EX_GOAL("10005", "ex_goal", "加时进球"),
    EX_CORNER("10006", "ex_corner", "加时角球"),
    EX_BOOKING("10007", "ex_booking", "加时罚牌");

    private String playSetId;

    private String requestType;

    private String desc;

    RequestTypeEnum(String playSetId, String requestType, String desc) {
        this.playSetId = playSetId;
        this.requestType = requestType;
        this.desc = desc;
    }

    public static String getRequestTypeEnumByPlaySetId(String playSetId){
        for (RequestTypeEnum requestTypeEnum : RequestTypeEnum.values()) {
            if (requestTypeEnum.getPlaySetId().equals(playSetId)) {
                return requestTypeEnum.getRequestType();
            }
        }
        return null;
    }

}
