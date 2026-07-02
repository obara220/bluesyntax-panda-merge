package com.panda.merge.dto.advertise;

import lombok.Data;

@Data
public class ConfirmEventDto extends AbstructAdvertiseDto  {
    private Long thirdMatchId;
    private String confirmEventCode;
    private String homeAway;
    private Long timeFromStartSecond;
    private String penaltyGoal;
    /**
     * 操作赛事ID
     */
    private Long deleteEventId;
    /**
     * 没有重踢标记 页面传 99
     */
    private String retakeStatus;
}
