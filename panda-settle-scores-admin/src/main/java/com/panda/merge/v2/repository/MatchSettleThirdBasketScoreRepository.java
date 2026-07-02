package com.panda.merge.v2.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.merge.model.MatchSettleThirdBasketScore;
import com.panda.merge.v2.entity.MatchSettleThirdBasketScoreEntity;

public interface MatchSettleThirdBasketScoreRepository extends IService<MatchSettleThirdBasketScoreEntity> {

    boolean updateById(MatchSettleThirdBasketScore matchSettleThirdBasketScore);

    boolean save(MatchSettleThirdBasketScore matchSettleThirdBasketScore);

    MatchSettleThirdBasketScore getById(Long id);

    void deleteSettleScores(Long thirdMatchId, String thirdEventId);

}
