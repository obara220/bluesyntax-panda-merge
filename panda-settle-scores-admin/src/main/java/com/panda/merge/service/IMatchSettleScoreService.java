package com.panda.merge.service;

import com.panda.merge.model.MatchSettleScore;

import java.util.List;

public interface IMatchSettleScoreService {

    void saveOrUpdateBatch(List<MatchSettleScore> matchSettleScore);

    void saveBatch(List<MatchSettleScore> matchSettleScore);

    List<MatchSettleScore> getByIds(List<Long> ids);
}
