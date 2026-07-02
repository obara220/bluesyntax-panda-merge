package com.panda.merge.v2.converter;

import com.panda.merge.model.MatchDelaySettleInfo;
import com.panda.merge.v2.entity.MatchDelaySettleInfoEntity;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring")
@Component
public interface MatchDelaySettleInfoConverter {

    MatchDelaySettleInfoEntity convertMatchDelaySettleInfoToEntity(MatchDelaySettleInfo matchDelaySettleInfo);

    List<MatchDelaySettleInfoEntity> convertMatchDelaySettleInfoToEntity(List<MatchDelaySettleInfo> matchDelaySettleInfos);

    MatchDelaySettleInfo convertEntityToDelaySettle(MatchDelaySettleInfoEntity matchDelaySettleInfo);

    List<MatchDelaySettleInfo> convertEntityToDelaySettle(List<MatchDelaySettleInfoEntity> matchDelaySettleInfoEntities);

}
