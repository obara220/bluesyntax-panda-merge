package com.panda.merge.v2.converter;

import com.panda.merge.model.MatchSettleDataSourceConfig;
import com.panda.merge.v2.entity.MatchSettleDataSourceConfigEntity;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring")
@Component
public interface MatchSettleDataSourceConfigConverter {

    MatchSettleDataSourceConfigEntity convertSettleDataSourceToEntity(MatchSettleDataSourceConfig matchSettleDataSourceConfig);

    List<MatchSettleDataSourceConfigEntity> convertSettleDataSourceToEntity(List<MatchSettleDataSourceConfig> matchSettleDataSourceConfigs);

    MatchSettleDataSourceConfig convertEntityToSettleDataSource(MatchSettleDataSourceConfigEntity matchSettleDataSourceConfigEntity);

    List<MatchSettleDataSourceConfig> convertEntityToSettleDataSource(List<MatchSettleDataSourceConfigEntity> matchSettleDataSourceConfigEntities);

}
