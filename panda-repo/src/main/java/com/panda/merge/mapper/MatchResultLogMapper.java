package com.panda.merge.mapper;

import com.panda.merge.model.MatchResultLog;
import com.panda.merge.model.MatchResultLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchResultLogMapper {
    long countByExample(MatchResultLogExample example);

    int deleteByExample(MatchResultLogExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchResultLog record);

    int insertSelective(MatchResultLog record);

    List<MatchResultLog> selectByExample(MatchResultLogExample example);

    MatchResultLog selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MatchResultLog record, @Param("example") MatchResultLogExample example);

    int updateByExample(@Param("record") MatchResultLog record, @Param("example") MatchResultLogExample example);

    int updateByPrimaryKeySelective(MatchResultLog record);

    int updateByPrimaryKey(MatchResultLog record);
}