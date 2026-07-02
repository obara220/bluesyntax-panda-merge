package com.panda.merge.v2.converter;

import com.panda.merge.model.MatchSettleFactorCheckInfo;
import com.panda.merge.v2.entity.MatchSettleFactorCheckInfoEntity;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring")
@Component
public interface MatchSettleFactorCheckInfoConverter {

    MatchSettleFactorCheckInfoEntity convertSettleFactorCheckInfoToEntity(MatchSettleFactorCheckInfo matchSettleFactorCheckInfo);

    List<MatchSettleFactorCheckInfoEntity> convertSettleFactorCheckInfoToEntity(List<MatchSettleFactorCheckInfo> matchSettleFactorCheckInfos);

    MatchSettleFactorCheckInfo convertEntityToSettleFactorCheckInfo(MatchSettleFactorCheckInfoEntity matchSettleFactorCheckInfoEntity);

    List<MatchSettleFactorCheckInfo> convertEntityToSettleFactorCheckInfo(List<MatchSettleFactorCheckInfoEntity> matchSettleFactorCheckInfoEntities);

}
