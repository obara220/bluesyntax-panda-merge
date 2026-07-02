package com.panda.merge.filter.football;

import com.panda.merge.dto.CommonStandardScoresDto;
import com.panda.merge.dto.CommonThirdScoresDto;
import com.panda.merge.dto.FootballScores;
import com.panda.merge.model.MatchEventInfo;
import com.panda.merge.model.MatchSettleScore;

import java.util.List;
import java.util.Map;

/**
 * 随着时间推移的阶段比分生成过滤器-责任链模式
 * */
public interface IMatchScoresSettleInitFilter {
    /**
     * 生成阶段比分过滤方法
     * */
    List<MatchSettleScore> filter(Map<String, FootballScores> footballScoresMap,CommonThirdScoresDto standardScoresDto, List<MatchSettleScore> list);

    /**
     * 根据删除事件获得 应该要被标记删除的阶段比分的 settleNum
     * */
    List<String> deleteEventPeriodScorefilter(MatchEventInfo data,  List<String> list);
}
