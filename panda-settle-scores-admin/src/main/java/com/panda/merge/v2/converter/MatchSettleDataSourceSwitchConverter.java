package com.panda.merge.v2.converter;

import com.panda.merge.model.MatchSettleDataSourceSwitch;
import com.panda.merge.v2.entity.MatchSettleDataSourceSwitchEntity;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring")
@Component
public interface MatchSettleDataSourceSwitchConverter {

    MatchSettleDataSourceSwitchEntity convertDataSourceSwitchToEntity(MatchSettleDataSourceSwitch matchSettleDataSourceSwitch);

    List<MatchSettleDataSourceSwitchEntity> convertDataSourceSwitchToEntity(List<MatchSettleDataSourceSwitch> matchSettleDataSourceSwitches);

    MatchSettleDataSourceSwitch convertEntityToSettleDataSource(MatchSettleDataSourceSwitchEntity matchSettleDataSourceSwitchEntity);

    List<MatchSettleDataSourceSwitch> convertEntityToSettleDataSource(List<MatchSettleDataSourceSwitchEntity> matchSettleDataSourceSwitchEntities);

}
