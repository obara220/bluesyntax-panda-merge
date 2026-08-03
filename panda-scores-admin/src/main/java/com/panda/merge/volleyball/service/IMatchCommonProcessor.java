//package com.panda.merge.volleyball.service;
//
//import com.panda.merge.advertise.dto.MatchScoreAndTimeVo;
//import com.panda.merge.dto.Response;
//import com.panda.merge.dto.advertise.v2.*;
//
//import java.util.List;
//
//public interface IMatchCommonProcessor<T> {
//
//    Response getCurrentMatchInfo(ChangeMatchPeriodV2Dto changeMatchPeriodV2Dto, MatchScoreAndTimeVo matchScoreAndTimeVo);
//
//    Response eventList(EventListV2Dto eventListDto);
//
//    Response deleteEvent(MatchScoreAndTimeVo matchScoreAndTimeVo, DeleteEventV2Dto deleteEventDto) throws Exception;
//
//    Response changeMatchLength(MatchScoreAndTimeVo matchScoreAndTimeVo, ChangeMatchLengthV2Dto changeMatchLengthDto);
//
//    Response changeScore(MatchScoreAndTimeVo matchScoreAndTimeVo, ChangeMatchScoreV2Dto changeMatchScoreV2Dto) throws Exception;
//
//    Response changeMatchStatus(MatchScoreAndTimeVo matchScoreAndTimeVo, ChangeMatchStatusV2Dto changeMatchStatusV2Dto);
//
//    Response changeMatchPeriod(MatchScoreAndTimeVo matchScoreAndTimeVo, ChangeMatchPeriodV2Dto changeMatchPeriodV2Dto);
//
//    Response kickOff(MatchScoreAndTimeVo matchScoreAndTimeVo, KickOffV2Dto kickOffV2Dto) throws Exception;
//
//    Response saveHotkeys(HotkeysSaveDto dto);
//
//    Response getHotkeysBySportIdAndUsername(Long sportId, String userName);
//
//    Response deleteHotkeysBySportIdAndUsername(Long sportId, String userName);
//
//    Response deleteHotkeysByUserIds(Long sportId, String userName, List<Long> userIds);
//
//}
