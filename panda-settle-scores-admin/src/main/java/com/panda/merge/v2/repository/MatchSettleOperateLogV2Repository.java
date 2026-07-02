package com.panda.merge.v2.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.merge.model.MatchSettleOperateLog;
import com.panda.merge.v2.entity.MatchSettleOperateLogEntity;

import java.util.List;

public interface MatchSettleOperateLogV2Repository extends IService<MatchSettleOperateLogEntity> {

    void saveOrUpdateBatch(List<MatchSettleOperateLog> matchSettleOperateLogs);

    void saveOrUpdateBatch(List<MatchSettleOperateLogEntity> matchSettleOperateLogEntityList, boolean isInsert);

    void save(MatchSettleOperateLog matchSettleOperateLogs);

    boolean save(MatchSettleOperateLogEntity entity);

}