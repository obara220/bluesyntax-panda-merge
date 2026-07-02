package com.panda.merge.filter.football;

import com.panda.merge.dto.CommonStandardScoresDto;
import com.panda.merge.dto.CommonThirdScoresDto;
import com.panda.merge.dto.FootballScores;
import com.panda.merge.model.MatchSettleEvent;
import com.panda.merge.model.MatchSettleScore;

import java.util.List;
import java.util.Map;

/**
 * 随着时间推移的阶段比分生成过滤器-责任链模式
 * */
public interface IMatchEventSettleInitFilter {
    /**
     * 生成阶段比分过滤方法
     * */
    List<MatchSettleEvent> filter(Map<String, FootballScores> footballScoresMap, CommonThirdScoresDto standardScoresDto, List<MatchSettleEvent> list);


}
