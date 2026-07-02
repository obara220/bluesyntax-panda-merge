package com.panda.merge.mapper;

import com.panda.merge.model.MatchEventType;
import com.panda.merge.model.MatchEventTypeExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchEventTypeMapper {
    long countByExample(MatchEventTypeExample example);

    int deleteByExample(MatchEventTypeExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchEventType record);

    int insertSelective(MatchEventType record);

    List<MatchEventType> selectByExample(MatchEventTypeExample example);

    MatchEventType selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MatchEventType record, @Param("example") MatchEventTypeExample example);

    int updateByExample(@Param("record") MatchEventType record, @Param("example") MatchEventTypeExample example);

    int updateByPrimaryKeySelective(MatchEventType record);

    int updateByPrimaryKey(MatchEventType record);
}