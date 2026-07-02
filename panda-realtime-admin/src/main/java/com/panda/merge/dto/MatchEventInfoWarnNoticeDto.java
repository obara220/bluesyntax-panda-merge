package com.panda.merge.dto;

import lombok.Data;

/**
 * 4123-操盘风控-紧急事件告警
 */
@Data
public class MatchEventInfoWarnNoticeDto {

    /**
     * 赛种ID
     */
    private Long sportId;

    /**
     * 数据源编码
     */
    private String dataSourceCode;

    /**
     * 联赛ID
     */
    private Long tournamentId;

    /**
     * 赛事ID
     */
    private Long matchId;

    /**
     * 玩法ID
     */
    private Long playId;

    /**
     * 事件编码
     */
    private String eventCode;

    /**
     * 比赛开始时间
     */
    private Long matchBeginTime;

    /**
     * 赛事进行时间
     */
    private Long secondsFromStart;

    /**
     * 事件发生时间
     */
    private Long reportTime;

    /**
     * 操作用户ID
     */
    private String trader;


    /**
     * 类型
     */
    private Integer eventType;
}
