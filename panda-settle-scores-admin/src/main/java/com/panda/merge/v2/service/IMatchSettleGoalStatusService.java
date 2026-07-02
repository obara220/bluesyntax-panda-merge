package com.panda.merge.v2.service;

import com.panda.merge.model.MatchEventInfo;

public interface IMatchSettleGoalStatusService {
    void updateMatchSettleCornerStatus(MatchEventInfo matchEventInfo);
    void updateMatchSettleGoalStatus(MatchEventInfo matchEventInfo);
}
