package com.panda.merge.mapper;

import com.panda.merge.model.ThirdMatchTeamSkillStatistics;
import com.panda.merge.model.ThirdMatchTeamSkillStatisticsExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ThirdMatchTeamSkillStatisticsMapper {
    long countByExample(ThirdMatchTeamSkillStatisticsExample example);

    int deleteByExample(ThirdMatchTeamSkillStatisticsExample example);

    int deleteByPrimaryKey(String id);

    int insert(ThirdMatchTeamSkillStatistics record);

    int insertSelective(ThirdMatchTeamSkillStatistics record);

    List<ThirdMatchTeamSkillStatistics> selectByExample(ThirdMatchTeamSkillStatisticsExample example);

    ThirdMatchTeamSkillStatistics selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") ThirdMatchTeamSkillStatistics record, @Param("example") ThirdMatchTeamSkillStatisticsExample example);

    int updateByExample(@Param("record") ThirdMatchTeamSkillStatistics record, @Param("example") ThirdMatchTeamSkillStatisticsExample example);

    int updateByPrimaryKeySelective(ThirdMatchTeamSkillStatistics record);

    int updateByPrimaryKey(ThirdMatchTeamSkillStatistics record);
}