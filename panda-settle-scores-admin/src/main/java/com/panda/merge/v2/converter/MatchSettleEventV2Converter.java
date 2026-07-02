package com.panda.merge.v2.converter;

import com.panda.merge.model.MatchSettleEvent;
import com.panda.merge.v2.entity.MatchSettleEventEntity;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring")
@Component
public interface MatchSettleEventV2Converter {

    MatchSettleEventEntity convertMatchSettleEventToEntity(MatchSettleEvent matchSettleEvent);

    List<MatchSettleEventEntity> convertMatchSettleEventToEntity(List<MatchSettleEvent> matchSettleEvents);

    MatchSettleEvent convertMatchSettleEventEntityToEvent(MatchSettleEventEntity eventEntity);

    List<MatchSettleEvent> convertMatchSettleEventEntityToEvent(List<MatchSettleEventEntity> eventEntities);

}
