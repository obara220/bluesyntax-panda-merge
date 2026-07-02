package com.panda.merge.service;

import com.panda.merge.dto.settle.*;
import com.panda.merge.model.MatchEventInfo;
import com.panda.merge.model.MatchSettleEvent;
import com.panda.merge.service.settleMention.dto.AbstractMentionStatus;
import com.panda.merge.service.settleMention.dto.AbstractSettleMentionDto;
import com.panda.merge.service.settleMention.dto.FootballMentionStatus;
import com.panda.merge.service.settleMention.dto.FootballSettleMentionDto;

import java.util.List;
import java.util.Map;

public interface IMatchSettleService {
    void initMatchSettleScore(Long standardMatchId);
    MatchSettleEvent getExtryEvent(MatchSettleEvent matchSettleEvent);

    MatchSettleEvent getExtryEvent(MatchSettleEventDto matchSettleScoreDto);

    boolean checkIfOverSettleTime(Long standardMatchId);

    boolean checkIfEventBeforeAllEdit(MatchSettleEvent matchSettleEvent);

    boolean checkIfEventAfterSettled(MatchSettleEvent matchSettleEvent);

    void manGoEarlyWarning(MatchEventInfo matchEventInfo);

    //结算时把回滚订单数清零
    void settleRollBackSetNullOrderCount(Long id);

    void batchSettleRollBackSetNullOrderCount(List<Long> ids);

    void updateGoWaterPenaltyScores(EditMatchSettleEventDto settleScoreSearchDto);

    void initBasketballSettleScore(Long standardMatchId);

    AbstractMentionStatus getFootballMentionStatus(MentionQueryRequest mentionQueryRequest);

    Map<String, AbstractMentionStatus> getAllMentionStatus(MentionQueryRequest mentionQueryRequest);

    void cancelSettleEventMention(SettleEventDeleteRequest settleEventDeleteRequest);
}
