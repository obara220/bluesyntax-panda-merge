package com.panda.merge.v2.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.merge.mapper.MatchSettleThirdBasketScoreMapper;
import com.panda.merge.model.MatchSettleThirdBasketScore;
import com.panda.merge.model.MatchSettleThirdBasketScoreExample;
import com.panda.merge.v2.converter.MatchSettleThirdBasketScoreConverter;
import com.panda.merge.v2.entity.MatchSettleThirdBasketScoreEntity;
import com.panda.merge.v2.mapper.MatchSettleThirdBasketScoreV2Mapper;
import com.panda.merge.v2.repository.MatchSettleThirdBasketScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class MatchSettleThirdBasketScoreRepositoryImpl extends ServiceImpl<MatchSettleThirdBasketScoreV2Mapper, MatchSettleThirdBasketScoreEntity> implements MatchSettleThirdBasketScoreRepository {

    @Autowired
    private MatchSettleThirdBasketScoreConverter matchSettleThirdBasketScoreConverter;
    @Autowired
    MatchSettleThirdBasketScoreMapper matchSettleThirdBasketScoreMapper;

    @Override
    public boolean updateById(MatchSettleThirdBasketScore matchSettleThirdBasketScore) {
        MatchSettleThirdBasketScoreEntity entity = matchSettleThirdBasketScoreConverter.convertSettleThirdBasketScoreToEntity(matchSettleThirdBasketScore);
        return super.updateById(entity);
    }

    @Override
    public boolean save(MatchSettleThirdBasketScore matchSettleThirdBasketScore) {
        MatchSettleThirdBasketScoreEntity entity = matchSettleThirdBasketScoreConverter.convertSettleThirdBasketScoreToEntity(matchSettleThirdBasketScore);
        return super.save(entity);
    }

    @Override
    public MatchSettleThirdBasketScore getById(Long id) {
        MatchSettleThirdBasketScoreEntity entity = super.getById(id);
        return matchSettleThirdBasketScoreConverter.convertEntityToSettleThirdBasketScore(entity);
    }

    public void deleteSettleScores(Long thirdMatchId, String thirdEventId) {
        MatchSettleThirdBasketScoreExample matchSettleThirdBasketScoreExample= new MatchSettleThirdBasketScoreExample();
        matchSettleThirdBasketScoreExample.createCriteria().andThirdMatchIdEqualTo(thirdMatchId).andThirdEventIdEqualTo(thirdEventId);
        matchSettleThirdBasketScoreMapper.deleteByExample(matchSettleThirdBasketScoreExample);
    }



}
