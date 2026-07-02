package com.panda.merge.filter.basketball;


import com.panda.merge.dto.BasketballScores;
import com.panda.merge.dto.CommonThirdScoresDto;
import com.panda.merge.filter.football.IMatchScoresSettleInitFilter;
import com.panda.merge.model.MatchEventInfo;
import com.panda.merge.model.MatchSettleScore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class BasketballScoreFilter implements IBascketballScoresFilter {
    private static List<IBascketballScoresFilter> filters=new ArrayList<>();
    //静态加载过滤器类
    static {
        filters.add(new BasketballMatch13ScoresFilter());
        filters.add(new BasketballMatch14ScoresFilter());
        filters.add(new BasketballMatch15ScoresFilter());
        filters.add(new BasketballMatch16ScoresFilter());
        filters.add(new BasketballMatch40ScoresFilter());
        filters.add(new BasketballMatch99ScoresFilter());
        filters.add(new BasketballBFZXInitFilter());

    }

    @Override
    public void filter(Map<String, BasketballScores> basketballScoresMap, CommonThirdScoresDto standardScoresDto, List<MatchSettleScore> before, List<MatchSettleScore> after) {
        for (IBascketballScoresFilter filter : filters) {
            filter.filter(basketballScoresMap,standardScoresDto,before,after);
        }
        return ;
    }

    @Override
    public List<String> deleteEventPeriodScoreFilter(MatchEventInfo data, List<String> list) {
        for (IBascketballScoresFilter filter : filters) {
            filter.deleteEventPeriodScoreFilter(data,list);
        }
        return list;
    }
}
