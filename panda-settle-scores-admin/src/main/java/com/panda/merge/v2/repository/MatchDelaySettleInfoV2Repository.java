package com.panda.merge.v2.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.merge.model.MatchDelaySettleInfo;
import com.panda.merge.v2.entity.MatchDelaySettleInfoEntity;

import java.util.List;

public interface MatchDelaySettleInfoV2Repository extends IService<MatchDelaySettleInfoEntity> {
    boolean updateById(MatchDelaySettleInfo matchDelaySettleInfo);

    boolean save(MatchDelaySettleInfo matchDelaySettleInfo);

    MatchDelaySettleInfo getById(Long id);

    boolean removeByMatchIdAndDataSourceCodeAndCheckInfoId(Long standardMatchId, String dataSourceCode, Long checkInfoId);

    List<MatchDelaySettleInfo> getModelByStandardMatchId(Long standardMatchId);
    List<MatchDelaySettleInfo> getModelByMatchIdAndCheckIds(Long standardMatchId, List<Long> checkInfoIds);

    void saveOrUpdateBatch(List<MatchDelaySettleInfo> matchDelaySettleInfos);

    void updateStatusByScoreIds(List<Long> scoreIds, Integer status);

    void updateStatusByCheckInfoIds(List<Long> checkInfoIds, Integer status);

}