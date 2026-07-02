package com.panda.merge.v2.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.merge.model.MatchSettleThirdEvent;
import com.panda.merge.model.MatchSettleThirdScore;
import com.panda.merge.v2.entity.MatchSettleThirdEventEntity;

import java.util.List;

public interface MatchSettleThirdEventRepository extends IService<MatchSettleThirdEventEntity> {
    boolean updateById(MatchSettleThirdEvent matchSettleThirdEvent);

    boolean save(MatchSettleThirdEvent matchSettleThirdEvent);

    MatchSettleThirdEvent getById(Long id);

    List<MatchSettleThirdEvent> getModelByItemsOrderBySettleNum(Long standardMatchId, List<String> eventCodes, List<Long> periodIds, Integer eventType, Long thirdEventSourceId);

}