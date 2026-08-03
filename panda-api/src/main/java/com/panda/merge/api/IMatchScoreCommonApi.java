package com.panda.merge.api;

import com.panda.merge.dto.Response;
import com.panda.merge.dto.advertise.v2.*;

import java.util.List;

/**
 * 比分报球板公共接口
 */
public interface IMatchScoreCommonApi {

    Response getCurrentMatchInfo(ChangeMatchPeriodV2Dto changeMatchPeriodV2Dto);

    Response eventList(EventListV2Dto eventListV2Dto);

    Response deleteEvent(DeleteEventV2Dto deleteEventV2Dto);

    Response changeMatchLength(ChangeMatchLengthV2Dto changeMatchLengthV2Dto);

    Response changeScore(ChangeMatchScoreV2Dto changeMatchScoreV2Dto);

    Response changeMatchStatus(ChangeMatchStatusV2Dto changeMatchStatusV2Dto);

    Response changeMatchPeriod(ChangeMatchPeriodV2Dto changeMatchPeriodV2Dto);

    Response kickOff(KickOffV2Dto kickOffV2Dto);

    /**
     * 发送事件（不影响比分的事件）
     * 用于发送持杆、未进球、自由球未进球等不影响比分的事件
     */
    Response sendEvent(SendEventDto sendEventDto);

    Response saveHotkeys(HotkeysSaveDto dto);

    Response getHotkeysBySportIdAndUsername(Long sportId, String userName);

    Response deleteHotkeysBySportIdAndUsername(Long sportId, String userName);

    Response deleteHotkeysByUserIds(Long sportId, String userName, List<Long> userIds);
}
