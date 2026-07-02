package com.panda.merge.v2.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.merge.model.MatchSettleGoalStatus;
import com.panda.merge.v2.entity.MatchSettleGoalStatusEntity;

public interface MatchSettleGoalStatusRepository extends IService<MatchSettleGoalStatusEntity> {

    MatchSettleGoalStatus getById(Long id);
    MatchSettleGoalStatus getByIdFromRedis(Long id);

    void updateOrInsertMatchSettleGoalStatus(MatchSettleGoalStatus matchSettleGoalStatus, boolean isInsert);
}