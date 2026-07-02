package com.panda.merge.v2.converter;

import com.panda.merge.model.MatchSettleFactorCheckInfo;
import com.panda.merge.model.MatchSettleGoalStatus;
import com.panda.merge.v2.entity.MatchSettleFactorCheckInfoEntity;
import com.panda.merge.v2.entity.MatchSettleGoalStatusEntity;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring")
@Component
public interface MatchSettleGoalStatusConverter {

    MatchSettleGoalStatusEntity convertSettleGoalStatusToEntity(MatchSettleGoalStatus matchSettleGoalStatus);

    List<MatchSettleGoalStatusEntity> convertSettleFactorCheckInfoToEntity(List<MatchSettleGoalStatus> matchSettleGoalStatuses);

    MatchSettleGoalStatus convertEntityToSettleGoalStatus(MatchSettleGoalStatusEntity matchSettleGoalStatusEntity);

    List<MatchSettleGoalStatus> convertEntityToSettleGoalStatus(List<MatchSettleGoalStatusEntity> matchSettleGoalStatusEntities);

}
