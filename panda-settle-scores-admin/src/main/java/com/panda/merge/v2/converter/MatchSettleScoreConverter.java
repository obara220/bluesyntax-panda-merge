package com.panda.merge.v2.converter;

import com.panda.merge.model.MatchSettleScore;
import com.panda.merge.v2.entity.MatchSettleScoreEntity;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring")
@Component
public interface MatchSettleScoreConverter {

    MatchSettleScoreEntity convertMatchSettleScoreToEntity(MatchSettleScore matchSettleScore);

    List<MatchSettleScoreEntity> convertMatchSettleScoreToEntity(List<MatchSettleScore> matchSettleScores);

    MatchSettleScore convertMatchSettleScoreEntityToScore(MatchSettleScoreEntity matchSettleScoreEntity);

    List<MatchSettleScore> convertMatchSettleScoreEntityToScore(List<MatchSettleScoreEntity> matchSettleScoreEntities);

}
