package com.panda.merge.mapper;

import com.panda.merge.model.MatchSettleGoalStatus;
import com.panda.merge.model.MatchSettleGoalStatusExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface MatchSettleGoalStatusMapper {
    long countByExample(MatchSettleGoalStatusExample example);

    int deleteByExample(MatchSettleGoalStatusExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchSettleGoalStatus record);

    int insertSelective(MatchSettleGoalStatus record);

    List<MatchSettleGoalStatus> selectByExample(MatchSettleGoalStatusExample example);

    MatchSettleGoalStatus selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MatchSettleGoalStatus record, @Param("example") MatchSettleGoalStatusExample example);

    int updateByExample(@Param("record") MatchSettleGoalStatus record, @Param("example") MatchSettleGoalStatusExample example);

    int updateByPrimaryKeySelective(MatchSettleGoalStatus record);

    int updateByPrimaryKey(MatchSettleGoalStatus record);
}