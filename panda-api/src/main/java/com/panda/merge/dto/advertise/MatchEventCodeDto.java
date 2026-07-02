package com.panda.merge.dto.advertise;

import lombok.Data;

@Data
public class MatchEventCodeDto extends AbstructAdvertiseDto  {

    private static final long serialVersionUID = 1L;

    private Long thirdMatchId;

    private String possibleEventCode;

    private Long eventTime;

    /**
     * 可能事件发时的比赛进行的时长。用来记录可能进球、黄牌、红牌事件，
     * 具体是属于可能事件的哪个5、15分钟进球区间
     */
    private Long possibleEventStarTime;

    private Long possibleEventTime;

    private Long possibleEventId;

}
