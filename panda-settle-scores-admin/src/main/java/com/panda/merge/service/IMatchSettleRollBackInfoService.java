package com.panda.merge.service;

import com.panda.merge.model.MatchSettleRollBackInfo;

import java.util.List;

public interface IMatchSettleRollBackInfoService {

    void saveOrUpdateBatch(List<MatchSettleRollBackInfo> matchSettleRollBackInfos);

    List<MatchSettleRollBackInfo> getByIds(List<Long> ids);
}
