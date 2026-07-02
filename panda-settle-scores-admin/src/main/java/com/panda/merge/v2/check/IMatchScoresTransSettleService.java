package com.panda.merge.v2.check;

import com.panda.merge.dto.CheckIsGreyDto;
import com.panda.merge.dto.CommonThirdScoresDto;
import com.panda.merge.model.MatchEventInfo;

public interface IMatchScoresTransSettleService {
    //接收比分
    void tansforScoreSettle(CommonThirdScoresDto data, boolean b);

    CheckIsGreyDto checkIsGreyEvent(MatchEventInfo matchEventInfo);

    void updateGrayMatchSettleScore(CheckIsGreyDto checkIsGreyDto,String homeAway);
    //接收事件
    void tansforEventSettle( MatchEventInfo data,boolean isStandard);

    boolean isStandardEvent(String dataSourceCode,Long standardMatchId);

     MatchEventInfo getEventFromCacheByBT(String linkedId, String dataSourceCode);
     MatchEventInfo getEventFromCache(Long eventId,String dataSourceCode);
}
