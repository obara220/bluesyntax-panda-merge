package com.panda.merge.service;

import com.panda.merge.model.MatchSettleOperateLog;

import java.util.List;

public interface IMatchSettleOperateLogService {

    void saveOrUpdateBatch(List<MatchSettleOperateLog> matchSettleOperateLogs);
}
