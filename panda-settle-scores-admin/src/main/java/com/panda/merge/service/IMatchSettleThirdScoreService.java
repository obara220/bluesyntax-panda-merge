package com.panda.merge.service;

import com.panda.merge.model.MatchSettleThirdScore;

import java.util.List;

public interface IMatchSettleThirdScoreService {

    void saveOrUpdateBatch(List<MatchSettleThirdScore> matchSettleThirdScore);
}
