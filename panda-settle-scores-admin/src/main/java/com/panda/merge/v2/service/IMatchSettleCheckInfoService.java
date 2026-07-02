package com.panda.merge.v2.service;

import com.panda.merge.dto.settle.MatchSettleEventDto;
import com.panda.merge.dto.settle.MatchSettleScoreDto;
import com.panda.merge.dto.settle.PenaltyScoresVo;
import com.panda.merge.model.MatchSettleCheckInfo;
import com.panda.merge.model.MatchSettleEvent;
import com.panda.merge.model.MatchSettleScore;

import java.util.List;

public interface IMatchSettleCheckInfoService {
    void searchCheckStatusByScoresList(List<MatchSettleScoreDto> matchSettleScoreDtos, String operatorName);

    void searchCheckStatusByEventList(List<MatchSettleEventDto> matchSettleScoreDtos, String OperatorName);

    void searchCheckStatusByPenalty(PenaltyScoresVo penaltyScoresVo, String operatorName);

    Long searchEventTimeByEvent(MatchSettleEvent event);

    Long searchEventTimeByScores(MatchSettleScore settleScore);

    void rollbackScores(MatchSettleScore matchSettleScore);

    void rollbackEvent(MatchSettleEvent matchSettleEvent);

    MatchSettleCheckInfo searchCheckInfoByUser(Long scoreEventId,Long standardMatchId,String userName);
}
