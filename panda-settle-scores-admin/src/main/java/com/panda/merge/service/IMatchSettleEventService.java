package com.panda.merge.service;

import com.panda.merge.model.MatchSettleEvent;

import java.util.List;

public interface IMatchSettleEventService {

    void saveOrUpdateBatch(List<MatchSettleEvent> matchSettleEvents);

    List<MatchSettleEvent> getByIds(List<Long> ids);
}
