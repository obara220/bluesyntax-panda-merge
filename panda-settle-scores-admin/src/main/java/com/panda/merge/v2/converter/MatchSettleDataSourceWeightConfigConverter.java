package com.panda.merge.v2.converter;

import com.panda.merge.model.MatchSettleDataSourceWeightConfig;
import com.panda.merge.v2.entity.MatchSettleDataSourceWeightConfigEntity;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring")
@Component
public interface MatchSettleDataSourceWeightConfigConverter {

    MatchSettleDataSourceWeightConfigEntity convertWeightConfigToEntity(MatchSettleDataSourceWeightConfig matchSettleDataSourceWeightConfig);

    List<MatchSettleDataSourceWeightConfigEntity> convertWeightConfigToEntity(List<MatchSettleDataSourceWeightConfig> matchSettleDataSourceConfigs);

    MatchSettleDataSourceWeightConfig convertEntityToWeightConfig(MatchSettleDataSourceWeightConfigEntity matchSettleDataSourceConfigEntity);

    List<MatchSettleDataSourceWeightConfig> convertEntityToWeightConfig(List<MatchSettleDataSourceWeightConfigEntity> matchSettleDataSourceConfigEntities);

}
