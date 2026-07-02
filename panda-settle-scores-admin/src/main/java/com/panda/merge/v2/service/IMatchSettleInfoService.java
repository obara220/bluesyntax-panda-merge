package com.panda.merge.v2.service;

import com.panda.merge.model.MatchSettleScore;

public interface IMatchSettleInfoService {

    void updateMatchCurrentEventStatus(Long standardMatchId);
    boolean updateMatchGrayStatus(Long standardMatchId);
    boolean checkBasketPeriodScoreOrder(MatchSettleScore matchSettleScore);
    boolean matchIsAutoSettle(long standardMatchId);
}
