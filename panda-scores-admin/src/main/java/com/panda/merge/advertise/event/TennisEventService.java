package com.panda.merge.advertise.event;

import com.panda.merge.advertise.dto.MatchScoreAndTimeVo;
import com.panda.merge.advertise.dto.MatchScoreCommonVo;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.advertise.EventListDto;

public interface TennisEventService {

    void addScoreChangeEvent(MatchScoreAndTimeVo data, MatchScoreCommonVo matchScoreCommonVo, Long startTimeSecond, Long period, String linkedId, String remark);

    Response eventList(MatchScoreAndTimeVo data, EventListDto eventListDto);

}
