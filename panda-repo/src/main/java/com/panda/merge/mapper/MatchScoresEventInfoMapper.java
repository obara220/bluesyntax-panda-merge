package com.panda.merge.mapper;

import com.panda.merge.model.MatchScoresEventInfo;
import com.panda.merge.model.MatchScoresEventInfoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchScoresEventInfoMapper {
    long countByExample(MatchScoresEventInfoExample example);

    int deleteByExample(MatchScoresEventInfoExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchScoresEventInfo record);

    int insertSelective(MatchScoresEventInfo record);

    List<MatchScoresEventInfo> selectByExample(MatchScoresEventInfoExample example);

    MatchScoresEventInfo selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MatchScoresEventInfo record, @Param("example") MatchScoresEventInfoExample example);

    int updateByExample(@Param("record") MatchScoresEventInfo record, @Param("example") MatchScoresEventInfoExample example);

    int updateByPrimaryKeySelective(MatchScoresEventInfo record);

    int updateByPrimaryKey(MatchScoresEventInfo record);

    int updateByPrimaryList(List<Long> list);

    int updateByPrimaryKeys(@Param("records") List<MatchScoresEventInfo> records);
}