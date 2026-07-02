package com.panda.merge.dto;

import com.panda.aocollect.model.MatchEventHistory;
import com.panda.merge.model.MatchEventInfo;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class AoMatchEventsHistoryDto {
    private Long thirdMatchId;
    private String dataSourceCode;
    private List<MatchEventHistory> data;
    private String linkId;
    private Long dataSoueceTime;
    private Long matchId;
    public AoMatchEventsHistoryDto(){};
    public AoMatchEventsHistoryDto(MatchEventInfo eventInfo, List<MatchEventInfo> allMatchEvents) {
        this.thirdMatchId=eventInfo.getThirdMatchId();
        this.dataSourceCode=eventInfo.getDataSourceCode();
        this.data= new ArrayList<>();
        this.linkId=eventInfo.getLinkId();
        this.dataSoueceTime=eventInfo.getCreateTime();
        this.matchId=eventInfo.getStandardMatchId();
        for (MatchEventInfo matchEvent : allMatchEvents) {
            MatchEventHistory matchEventHistory=new MatchEventHistory();
            matchEventHistory.setAwayCount(matchEvent.getT2());
            matchEventHistory.setHomeCount(matchEvent.getT1());
            matchEventHistory.setHomeAway(matchEvent.getHomeAway());
            matchEventHistory.setId(matchEvent.getId());
            matchEventHistory.setMatchId(matchEvent.getStandardMatchId());
            matchEventHistory.setCreateTime(matchEvent.getCreateTime());
            matchEventHistory.setDataSourceCode(matchEvent.getDataSourceCode());
            matchEventHistory.setEventCode(matchEvent.getEventCode());
            matchEventHistory.setEventId(matchEvent.getId());
            matchEventHistory.setPeriodCode(matchEvent.getMatchPeriodId());
            matchEventHistory.setEventTime(matchEvent.getEventTime());
            matchEventHistory.setProgressTime(matchEvent.getSecondsFromStart());
            matchEventHistory.setPlayerId(matchEvent.getPlayer1Id());
            matchEventHistory.setPlayerName(matchEvent.getPlayer1Name());
            matchEventHistory.setTeamId(matchEvent.getStandardTeamId());
            //继续
            data.add(matchEventHistory);
        }
    }
}
