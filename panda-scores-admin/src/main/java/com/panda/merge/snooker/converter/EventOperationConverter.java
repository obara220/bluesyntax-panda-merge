package com.panda.merge.snooker.converter;

import com.panda.merge.constant.MatchInfoConvertEnum;
import com.panda.merge.dto.advertise.v2.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring",imports = {MatchInfoConvertEnum.class})
@Component
public interface EventOperationConverter {
    @Mapping(target = "eventCode", expression = "java(MatchInfoConvertEnum.getByCurCode(changeMatchStatusV2Dto.getControlType()).getValue())")
    @Mapping(target = "secondFromStart", constant = "0L")
    EventOperationV2Dto convertChangeMatchStatusToEvent(ChangeMatchStatusV2Dto changeMatchStatusV2Dto);

    @Mapping(target = "eventCode", constant = "which_team_serves_first")
    @Mapping(target = "homeAway", source = "whoKickOff")
    @Mapping(target = "secondFromStart", expression = "java(kickOffV2Dto.getSecondFromStart() != null ? kickOffV2Dto.getSecondFromStart() : 0L)")
    EventOperationV2Dto convertKickOffToEvent(KickOffV2Dto kickOffV2Dto);

    /** 仅阶段切换，与 changeMatchStatus（开始/中断等）语义分离，事件码固定为 match_status */
    @Mapping(target = "eventCode", constant = "match_status")
    @Mapping(target = "secondFromStart", constant = "0L")
    EventOperationV2Dto changeMatchPeriodToEvent(ChangeMatchPeriodV2Dto changeMatchPeriodV2Dto);

    @Mapping(target = "eventCode", constant = "rerack")
    @Mapping(target = "secondFromStart", constant = "0L")
    EventOperationV2Dto convertRerackToEvent(RerackV2Dto rerackV2Dto);

    // 保留原始的eventCode，不进行硬编码
    EventOperationV2Dto changeMatchScoreToEvent(ChangeMatchScoreV2Dto changeMatchScoreV2Dto);

    EventOperationV2Dto changeDelEventToEvent(DeleteEventV2Dto deleteEventDto, String eventCode, String homeAway);

    ChangeMatchPeriodV2Dto convertMatchStatusToMatchPeriod(ChangeMatchStatusV2Dto changeMatchStatusV2Dto);


}
