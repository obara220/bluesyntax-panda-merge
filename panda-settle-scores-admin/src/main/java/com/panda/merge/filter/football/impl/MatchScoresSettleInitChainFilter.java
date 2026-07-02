package com.panda.merge.filter.football.impl;

import com.panda.merge.dto.CommonThirdScoresDto;
import com.panda.merge.dto.FootballScores;
import com.panda.merge.filter.football.IMatchScoresSettleInitFilter;
import com.panda.merge.model.MatchEventInfo;
import com.panda.merge.model.MatchSettleScore;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MatchScoresSettleInitChainFilter implements IMatchScoresSettleInitFilter {
    private static List<IMatchScoresSettleInitFilter> filters=new ArrayList<>();
    //静态加载过滤器类
    static {
        filters.add(new Match15MScoresSettleInitFilter());
        filters.add(new Match30MScoresSettleInitFilter());
        filters.add(new Match45MScoresSettleInitFilter());
        filters.add(new Match45ScoresSettleInitFilter());
        filters.add(new Match60MScoresSettleInitFilter());
        filters.add(new Match75MScoresSettleInitFilter());
        filters.add(new Match90MScoresSettleInitFilter());
        filters.add(new Match90ScoresSettleInitFilter());
        filters.add(new MatchET1ScoresSettleInitFilter());
        filters.add(new MatchET2ScoresSettleInitFilter());
        filters.add(new MatchBFZXScoresSettleInitFilter());
    }
    @Override
    public List<MatchSettleScore> filter(Map<String, FootballScores> footballScoresMap, CommonThirdScoresDto standardScoresDto, List<MatchSettleScore> list) {
        for (IMatchScoresSettleInitFilter filter : filters) {
            filter.filter(footballScoresMap,standardScoresDto,list);
        }
        return list;
    }

    @Override
    public List<String> deleteEventPeriodScorefilter(MatchEventInfo data, List<String> list) {
        //从被删除事件找到原事件

        for (IMatchScoresSettleInitFilter filter : filters) {
            filter.deleteEventPeriodScorefilter(data,list);
        }
        return list;
    }
}
