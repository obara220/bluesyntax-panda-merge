package com.panda.merge.mapper;

import com.panda.merge.model.MatchScoresStandardChangeLog;
import com.panda.merge.model.MatchScoresStandardChangeLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchScoresStandardChangeLogMapper {
    long countByExample(MatchScoresStandardChangeLogExample example);

    int deleteByExample(MatchScoresStandardChangeLogExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchScoresStandardChangeLog record);

    int insertSelective(MatchScoresStandardChangeLog record);

    List<MatchScoresStandardChangeLog> selectByExample(MatchScoresStandardChangeLogExample example);

    MatchScoresStandardChangeLog selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MatchScoresStandardChangeLog record, @Param("example") MatchScoresStandardChangeLogExample example);

    int updateByExample(@Param("record") MatchScoresStandardChangeLog record, @Param("example") MatchScoresStandardChangeLogExample example);

    int updateByPrimaryKeySelective(MatchScoresStandardChangeLog record);

    int updateByPrimaryKey(MatchScoresStandardChangeLog record);
}