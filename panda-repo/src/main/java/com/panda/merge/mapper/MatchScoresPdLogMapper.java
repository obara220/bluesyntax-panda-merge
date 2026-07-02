package com.panda.merge.mapper;

import com.panda.merge.model.MatchScoresPdLog;
import com.panda.merge.model.MatchScoresPdLogExample;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchScoresPdLogMapper {
    long countByExample(MatchScoresPdLogExample example);

    int deleteByExample(MatchScoresPdLogExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchScoresPdLog record);

    int insertSelective(MatchScoresPdLog record);

    List<MatchScoresPdLog> selectByExample(MatchScoresPdLogExample example);

    MatchScoresPdLog selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MatchScoresPdLog record, @Param("example") MatchScoresPdLogExample example);

    int updateByExample(@Param("record") MatchScoresPdLog record, @Param("example") MatchScoresPdLogExample example);

    int updateByPrimaryKeySelective(MatchScoresPdLog record);

    int updateByPrimaryKey(MatchScoresPdLog record);
}