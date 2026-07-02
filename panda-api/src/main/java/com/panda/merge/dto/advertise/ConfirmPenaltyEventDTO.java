package com.panda.merge.dto.advertise;

import lombok.Data;

/**
 * @author Kepa
 */
@Data
public class ConfirmPenaltyEventDTO extends AbstructAdvertiseDto {

    private Long thirdMatchId;

    private Long standardMatchId;

    /**
     * 操作的轮次
     */
    private Integer targetRound;

    /**
     * 主队比分
     */
    private Integer home;

    /**
     * 客队比分
     */
    private Integer away;

    private String homeAway;

    private String confirmEventCode;

    private Long timeFromStartSecond;

    private String penaltyGoal;

    /**
     * 确认事件Id
     */
    private Long confirmEventId;





}
