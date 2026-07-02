package com.panda.merge.mapper;

import com.panda.merge.model.MatchEventInfoScores;
import com.panda.merge.model.MatchEventInfoScoresExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface MatchEventInfoScoresMapper {
    long countByExample(MatchEventInfoScoresExample example);

    int deleteByExample(MatchEventInfoScoresExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchEventInfoScores record);

    int insertSelective(MatchEventInfoScores record);

    List<MatchEventInfoScores> selectByExample(MatchEventInfoScoresExample example);

    MatchEventInfoScores selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MatchEventInfoScores record, @Param("example") MatchEventInfoScoresExample example);

    int updateByExample(@Param("record") MatchEventInfoScores record, @Param("example") MatchEventInfoScoresExample example);

    int updateByPrimaryKeySelective(MatchEventInfoScores record);

    int updateByPrimaryKey(MatchEventInfoScores record);
}