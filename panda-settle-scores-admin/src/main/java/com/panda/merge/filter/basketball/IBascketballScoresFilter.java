package com.panda.merge.filter.basketball;

import com.panda.merge.dto.BasketballScores;
import com.panda.merge.dto.CommonThirdScoresDto;
import com.panda.merge.dto.FootballScores;
import com.panda.merge.model.MatchEventInfo;
import com.panda.merge.model.MatchSettleScore;

import java.util.List;
import java.util.Map;

/**
 * 随着时间推移的阶段比分生成过滤器-责任链模式
 * */
public interface IBascketballScoresFilter {
    /**
     * 生成阶段比分过滤方法
     * */
   void filter(Map<String, BasketballScores> footballScoresMap , CommonThirdScoresDto standardScoresDto, List<MatchSettleScore> before, List<MatchSettleScore> after);

   /*
   *篮球删除事件
    */
    List<String> deleteEventPeriodScoreFilter(MatchEventInfo data, List<String> list);
}
