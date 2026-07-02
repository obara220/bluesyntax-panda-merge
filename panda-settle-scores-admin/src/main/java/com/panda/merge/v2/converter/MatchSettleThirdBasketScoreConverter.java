package com.panda.merge.v2.converter;

import com.panda.merge.model.MatchSettleThirdBasketScore;
import com.panda.merge.model.MatchSettleThirdScore;
import com.panda.merge.v2.entity.MatchSettleThirdBasketScoreEntity;
import com.panda.merge.v2.entity.MatchSettleThirdScoreEntity;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring")
@Component
public interface MatchSettleThirdBasketScoreConverter {

    MatchSettleThirdBasketScoreEntity convertSettleThirdBasketScoreToEntity(MatchSettleThirdBasketScore matchSettleThirdBasketScore);

    List<MatchSettleThirdBasketScoreEntity> convertSettleThirdBasketScoreToEntity(List<MatchSettleThirdBasketScore> matchSettleThirdBasketScores);

    MatchSettleThirdBasketScore convertEntityToSettleThirdBasketScore(MatchSettleThirdBasketScoreEntity matchSettleThirdBasketScoreEntity);

    List<MatchSettleThirdBasketScore> convertEntityToSettleThirdBasketScore(List<MatchSettleThirdBasketScoreEntity> matchSettleThirdBasketScoreEntities);

}
