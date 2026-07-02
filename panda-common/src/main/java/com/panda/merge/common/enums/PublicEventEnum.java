package com.panda.merge.common.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * @author warren
 * @since 2024/10/11 13:04:44
 */
@Getter
public enum PublicEventEnum {
    /**
     * 比赛开始
     */
    KICK_OFF("kickOff", "开球"),
    /**
     * 主队公共事件
     */
    HOME_EVENT("home", "主队公共事件"),
    /**
     * 客队公共事件
     */
    AWAY_EVENT("away", "客队公共事件"),
    /**
     * 公共事件1
     */
    PUBLIC_EVENT_ONE("publicEventOne", "公共事件1"),
    /**
     * 公共事件2
     */
    PUBLIC_EVENT_TWO("publicEventTwo", "公共事件2"),

    /**
     * 事件时间不在计时阶段
     */
    OUT_OF_EVENT("outOfEvent", "事件时间不在计时阶段时，终止计时"),
    ;
    private final String code;
    private final String name;

    PublicEventEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

}
