package com.panda.merge.dto.settle;

import com.panda.merge.model.MatchSettleEvent;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PenaltyScoresVo implements Serializable{

    private Long standardMatchId;

    private MatchSettleEventDto teamFirst;

    private List<MatchSettleEventDto> homeEventList;

    private List<MatchSettleEventDto> awayEventList;
    /**
     * 前5轮主客队比分
     * */
    private MatchSettleEventDto  homeAway5RoundEvent;
    /**
     * 所有轮主客队比分
     * */
    private MatchSettleEventDto  homeAwayAllRoundEvent;

    /**
     * 点球走水比分
     * */
    private MatchSettleEventDto  goWaterPenaltyEvent;
}
