package com.panda.merge.constant.converter;

import com.panda.merge.data.entity.MatchSettleEventEntity;
import com.panda.merge.model.MatchSettleEvent;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring")
@Component
public interface MatchSettleEventConverter {

    MatchSettleEventEntity convertMatchSettleEventToEntity(MatchSettleEvent matchSettleEvent);

    List<MatchSettleEventEntity> convertMatchSettleEventToEntity(List<MatchSettleEvent> matchSettleEvents);

    MatchSettleEvent convertMatchSettleEventEntityToEvent(MatchSettleEventEntity eventEntity);

    List<MatchSettleEvent> convertMatchSettleEventEntityToEvent(List<MatchSettleEventEntity> eventEntities);

}
