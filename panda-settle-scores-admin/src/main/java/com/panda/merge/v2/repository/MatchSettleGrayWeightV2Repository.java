package com.panda.merge.v2.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.merge.model.MatchSettleGrayWeight;
import com.panda.merge.v2.entity.MatchSettleGrayWeightEntity;

import java.util.List;
import java.util.Set;

public interface MatchSettleGrayWeightV2Repository extends IService<MatchSettleGrayWeightEntity> {
    boolean updateById(MatchSettleGrayWeight matchSettleGrayWeight);

    void saveOrUpdateBatch(List<MatchSettleGrayWeight> matchSettleGrayWeights);

    boolean save(MatchSettleGrayWeight matchSettleGrayWeight);

    MatchSettleGrayWeight getById(Long id);

    List<MatchSettleGrayWeight> getByIds(Set<Long> ids);

    List<MatchSettleGrayWeight> getByItems(Long standardMatchId, Long sportId, String grayCode, Integer grayArea, Integer grayStatus);
}