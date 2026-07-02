package com.panda.merge.v2.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.merge.model.MatchSettleSpOddsExample;
import com.panda.merge.v2.entity.MatchSettleSpOddsEntity;

import java.util.List;
import java.util.Map;

public interface MatchSettleSpOddsRepository extends IService<MatchSettleSpOddsEntity> {

    List<MatchSettleSpOddsEntity> selectByExample(MatchSettleSpOddsExample example);

    Map<Long, List<MatchSettleSpOddsEntity>> toMap(List<Long> markIdList, Long standardMatchId);

}
