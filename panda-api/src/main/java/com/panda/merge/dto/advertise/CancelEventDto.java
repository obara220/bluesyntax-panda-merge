package com.panda.merge.dto.advertise;

import lombok.Data;

@Data
public class CancelEventDto extends AbstructAdvertiseDto  {
    private Long thirdMatchId;
    private String cancelEventCode;
    private String homeAway;
    private Long timeFromStartSecond;

    /**
     * 操作赛事ID
     */
    private Long deleteEventId;
    /**
     * 没有重踢标记 页面传 99
     */
    private String retakeStatus;
}
