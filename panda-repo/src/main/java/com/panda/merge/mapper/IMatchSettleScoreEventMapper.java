package com.panda.merge.mapper;

import com.panda.merge.model.MatchSettleEvent;
import com.panda.merge.model.MatchSettleScore;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IMatchSettleScoreEventMapper {

    //批量更新比分
    int updateScoreByList(List<MatchSettleScore> list);

    //批量更新事件
    int updateEventByList(List<MatchSettleEvent> list);

}