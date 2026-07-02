package com.panda.merge.mapper;

import com.panda.merge.model.MatchResultEventLog;
import com.panda.merge.model.MatchResultEventLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchResultEventLogMapper {
    long countByExample(MatchResultEventLogExample example);

    int deleteByExample(MatchResultEventLogExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchResultEventLog record);

    int insertSelective(MatchResultEventLog record);

    List<MatchResultEventLog> selectByExample(MatchResultEventLogExample example);

    MatchResultEventLog selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MatchResultEventLog record, @Param("example") MatchResultEventLogExample example);

    int updateByExample(@Param("record") MatchResultEventLog record, @Param("example") MatchResultEventLogExample example);

    int updateByPrimaryKeySelective(MatchResultEventLog record);

    int updateByPrimaryKey(MatchResultEventLog record);
}