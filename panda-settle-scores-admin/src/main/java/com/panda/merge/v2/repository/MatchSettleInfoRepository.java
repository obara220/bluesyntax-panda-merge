package com.panda.merge.v2.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.merge.model.MatchSettleInfo;
import com.panda.merge.model.MatchSettleInfoExample;
import com.panda.merge.v2.entity.MatchSettleInfoEntity;

import java.util.List;

public interface MatchSettleInfoRepository extends IService<MatchSettleInfoEntity> {
    MatchSettleInfoEntity getMatchSettleInfo(Long id);
    MatchSettleInfo getModelMatchSettleInfo(Long id);

    void updateMatchSettleInfoToRedis(MatchSettleInfoEntity entity,boolean tag);

    void batchSaveOrUpdateToRedis(List<MatchSettleInfoEntity> info,boolean isInsert);

    void updateMatchSettleInfoToRedis(MatchSettleInfo matchSettleInfo,boolean tag);

    MatchSettleInfo getById(Long id);

    List<MatchSettleInfoEntity> selectByExample(MatchSettleInfoExample example);

    int updateByExampleSelective(MatchSettleInfoEntity record, MatchSettleInfoExample example);

    List<MatchSettleInfo> selectByCurIdAndLimit(Long curId, int limit);

    MatchSettleInfo getOneFromDB(Long id);
}