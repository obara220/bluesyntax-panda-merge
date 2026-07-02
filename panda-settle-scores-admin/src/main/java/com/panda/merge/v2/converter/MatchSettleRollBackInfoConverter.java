package com.panda.merge.v2.converter;

import com.panda.merge.model.MatchSettleRollBackInfo;
import com.panda.merge.v2.entity.MatchSettleRollBackInfoEntity;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring")
@Component
public interface MatchSettleRollBackInfoConverter {

    MatchSettleRollBackInfoEntity convertRollBackInfoToEntity(MatchSettleRollBackInfo matchSettleRollBackInfo);

    List<MatchSettleRollBackInfoEntity> convertRollBackInfoToEntity(List<MatchSettleRollBackInfo> matchSettleRollBackInfos);

    MatchSettleRollBackInfo convertEntityToRollBackInfo(MatchSettleRollBackInfoEntity matchSettleRollBackInfoEntity);

    List<MatchSettleRollBackInfo> convertEntityToRollBackInfo(List<MatchSettleRollBackInfoEntity> matchSettleRollBackInfoEntities);

}
