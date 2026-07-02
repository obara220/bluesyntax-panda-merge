package com.panda.merge.v2.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.merge.model.MatchSettleRollBackInfo;
import com.panda.merge.v2.entity.MatchSettleRollBackInfoEntity;

import java.util.List;

public interface MatchSettleRollBackInfoRepository extends IService<MatchSettleRollBackInfoEntity> {

    List<MatchSettleRollBackInfoEntity> getByMatchId(Long standardMatchId);

    List<MatchSettleRollBackInfo> getModelByMatchId(Long standardMatchId);

    MatchSettleRollBackInfoEntity getMatchSettleRollBackInfo(Long id);

    MatchSettleRollBackInfo getModelMatchSettleRollBackInfo(Long id);

    void updateMatchSettleRollBackInfoToRedis(MatchSettleRollBackInfoEntity info, boolean isInsert);

    void updateMatchSettleRollBackInfoToRedis(MatchSettleRollBackInfo info, boolean isInsert);
}