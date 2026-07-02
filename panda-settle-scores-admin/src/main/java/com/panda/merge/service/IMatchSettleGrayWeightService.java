package com.panda.merge.service;

import com.panda.merge.model.MatchSettleGrayWeight;

import java.util.List;
import java.util.Set;

public interface IMatchSettleGrayWeightService {

    void saveOrUpdateBatch(List<MatchSettleGrayWeight> matchSettleGrayWeights);

    List<MatchSettleGrayWeight> getByIds(Set<Long> ids);
}
