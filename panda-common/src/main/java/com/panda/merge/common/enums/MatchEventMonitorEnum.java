package com.panda.merge.common.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * @author warren
 * @since 2024/11/04 13:13:44
 */
@Getter
public enum MatchEventMonitorEnum {
    /**
     * 初始化在线状态
     */
    ONLINE_INIT("0", "初始化在线状态"),

    /**
     * 在线状态转为离线状态
     */
    ONLINE_TO_OFFLINE("1", "在线状态转为离线状态"),

    /**
     * 离线状态转为在线状态
     */
    OFFLINE_TO_ONLINE("2", "离线状态转为在线状态"),
    ;
    private final String code;

    private final String desc;

    MatchEventMonitorEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 在线状态集合
     */
    public static final List<MatchEventMonitorEnum> ONLINE_STATUS = Arrays.asList(ONLINE_INIT, OFFLINE_TO_ONLINE);

    /**
     * 是否在线
     *
     * @param status 在线状态
     * @return 是否在线
     */
    public static boolean onlineStatus(String status) {
        for (MatchEventMonitorEnum onlineStatus : ONLINE_STATUS) {
            if (onlineStatus.getCode().equals(status)) {
                return true;
            }
        }
        return false;
    }
}
