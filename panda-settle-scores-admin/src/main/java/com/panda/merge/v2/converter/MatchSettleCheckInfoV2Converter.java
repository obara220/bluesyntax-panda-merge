package com.panda.merge.v2.converter;

import com.panda.merge.model.MatchSettleCheckInfo;
import com.panda.merge.v2.entity.MatchSettleCheckInfoEntity;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring")
@Component
public interface MatchSettleCheckInfoV2Converter {

    MatchSettleCheckInfoEntity convertCheckInfoToEntity(MatchSettleCheckInfo checkInfo);

    List<MatchSettleCheckInfoEntity> convertCheckInfoToEntity(List<MatchSettleCheckInfo> checkInfos);

    MatchSettleCheckInfo convertEntityToCheckInfo(MatchSettleCheckInfoEntity matchSettleCheckInfo);

    List<MatchSettleCheckInfo> convertEntityToCheckInfo(List<MatchSettleCheckInfoEntity> matchSettleCheckInfoEntities);

}
