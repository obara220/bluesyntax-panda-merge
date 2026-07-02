package com.panda.merge.mapper;

import com.panda.merge.model.MatchEventInfo;
import com.panda.merge.model.MatchEventInfoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchEventInfoMapper {
    long countByExample(MatchEventInfoExample example);

    int deleteByExample(MatchEventInfoExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchEventInfo record);

    int insertSelective(MatchEventInfo record);

    List<MatchEventInfo> selectByExample(MatchEventInfoExample example);

    MatchEventInfo selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MatchEventInfo record, @Param("example") MatchEventInfoExample example);

    int updateByExample(@Param("record") MatchEventInfo record, @Param("example") MatchEventInfoExample example);

    int updateByPrimaryKeySelective(MatchEventInfo record);

    int updateByPrimaryKey(MatchEventInfo record);
}