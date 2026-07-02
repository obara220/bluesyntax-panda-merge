package com.panda.merge.mapper;

import com.panda.merge.model.MatchResultReportEvent;
import com.panda.merge.model.MatchResultReportEventExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface MatchResultReportEventMapper {
    long countByExample(MatchResultReportEventExample example);

    int deleteByExample(MatchResultReportEventExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchResultReportEvent record);

    int insertSelective(MatchResultReportEvent record);

    List<MatchResultReportEvent> selectByExample(MatchResultReportEventExample example);

    MatchResultReportEvent selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MatchResultReportEvent record, @Param("example") MatchResultReportEventExample example);

    int updateByExample(@Param("record") MatchResultReportEvent record, @Param("example") MatchResultReportEventExample example);

    int updateByPrimaryKeySelective(MatchResultReportEvent record);

    int updateByPrimaryKey(MatchResultReportEvent record);
}