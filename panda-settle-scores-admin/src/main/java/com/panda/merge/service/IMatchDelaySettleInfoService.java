package com.panda.merge.service;

import com.panda.merge.model.MatchDelaySettleInfo;

import java.util.List;

public interface IMatchDelaySettleInfoService {

    void saveOrUpdateBatch(List<MatchDelaySettleInfo> matchDelaySettleInfos);

    void updateStatusByScoreIds(List<Long> scoreIds, Integer status);

    void updateStatusByCheckInfoIds(List<Long> checkInfoIds, Integer status);

}
