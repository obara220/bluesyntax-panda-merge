package com.panda.merge.v2.service;

import com.panda.merge.dto.settle.EditMatchSettleEventDto;
import com.panda.merge.dto.settle.MatchSettleEventDto;
import com.panda.merge.dto.settle.SettleMatchScoreDto;
import com.panda.merge.model.MatchSettleEvent;

import java.util.List;

public interface IMatchSettleEventService {

    void endEventSettleByEvent(MatchSettleEvent matchSettleEvent);

    boolean settlePenaltyTeamFirst(MatchSettleEvent event);

    void updateGoWaterPenaltyScores(EditMatchSettleEventDto settleScoreSearchDto);

    void secondSettleWarnMango(SettleMatchScoreDto matchSettleScoreDto, Integer sportId);

}
