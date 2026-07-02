package com.panda.merge.v2.converter;

import com.panda.merge.model.MatchSettleThirdEvent;
import com.panda.merge.v2.entity.MatchSettleThirdEventEntity;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring")
@Component
public interface MatchSettleThirdEventConverter {

    MatchSettleThirdEventEntity convertSettleThirdEventToEntity(MatchSettleThirdEvent matchSettleThirdEvent);

    List<MatchSettleThirdEventEntity> convertSettleThirdEventToEntity(List<MatchSettleThirdEvent> matchSettleThirdEvents);

    MatchSettleThirdEvent convertEntityToSettleThirdEvent(MatchSettleThirdEventEntity matchSettleThirdEventEntity);

    List<MatchSettleThirdEvent> convertEntityToSettleThirdEvent(List<MatchSettleThirdEventEntity> matchSettleThirdEventEntities);

}
