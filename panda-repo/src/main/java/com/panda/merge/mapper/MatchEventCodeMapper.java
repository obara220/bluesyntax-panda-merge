package com.panda.merge.mapper;

import com.panda.merge.model.MatchEventCode;
import com.panda.merge.model.MatchEventCodeExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchEventCodeMapper {
    long countByExample(MatchEventCodeExample example);

    int deleteByExample(MatchEventCodeExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchEventCode record);

    int insertSelective(MatchEventCode record);

    List<MatchEventCode> selectByExample(MatchEventCodeExample example);

    MatchEventCode selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MatchEventCode record, @Param("example") MatchEventCodeExample example);

    int updateByExample(@Param("record") MatchEventCode record, @Param("example") MatchEventCodeExample example);

    int updateByPrimaryKeySelective(MatchEventCode record);

    int updateByPrimaryKey(MatchEventCode record);
}