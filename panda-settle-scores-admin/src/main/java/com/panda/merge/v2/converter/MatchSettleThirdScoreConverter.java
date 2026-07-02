package com.panda.merge.v2.converter;

import com.panda.merge.model.MatchSettleFactorCheckInfo;
import com.panda.merge.model.MatchSettleThirdScore;
import com.panda.merge.v2.entity.MatchSettleFactorCheckInfoEntity;
import com.panda.merge.v2.entity.MatchSettleThirdScoreEntity;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring")
@Component
public interface MatchSettleThirdScoreConverter {

    MatchSettleThirdScoreEntity convertSettleThirdScoreToEntity(MatchSettleThirdScore matchSettleThirdScore);

    List<MatchSettleThirdScoreEntity> convertSettleThirdScoreToEntity(List<MatchSettleThirdScore> matchSettleThirdScores);

    MatchSettleThirdScore convertEntityToSettleThirdScore(MatchSettleThirdScoreEntity matchSettleThirdScoreEntity);

    List<MatchSettleThirdScore> convertEntityToSettleThirdScore(List<MatchSettleThirdScoreEntity> matchSettleThirdScoreEntities);

}
