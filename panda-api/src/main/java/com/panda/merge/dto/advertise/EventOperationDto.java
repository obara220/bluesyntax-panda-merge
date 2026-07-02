package com.panda.merge.dto.advertise;

import lombok.Data;

@Data
public class EventOperationDto extends AbstructAdvertiseDto {
    private Long matchId;
    private Integer isDanger;  //1 危险 0安全
    private Long eventTime;
    private Long matchPeriodId;
    private Long timeFromStartSecond;
    private Long thirdMatchId;
    // PA赛事编码：possible_video_assistant_referee
    private String eventCode;
    // 事件类型：video_assistant_referee [0 = 进球, 1 = 点球, 2 = 红牌]
    private String extraInfo;
    /**
     * 补时时长：分钟
     */
    private Long injuryTime;

    /**
     *  "eventType":安全=safe，危险=danger，Tmax=Tmax，拒单=reject,
     */
    private String eventType;
    /**
     *  "scoresType":进球=goal,角球=goal,罚牌=faCard
     */
    private String scoresType;

}
