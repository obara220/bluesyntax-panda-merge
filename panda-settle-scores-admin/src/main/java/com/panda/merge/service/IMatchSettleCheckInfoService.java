package com.panda.merge.service;

import com.panda.merge.model.MatchSettleCheckInfo;

import java.util.List;

public interface IMatchSettleCheckInfoService {

    void saveOrUpdateBatch(List<MatchSettleCheckInfo> matchSettleCheckInfos);
}
