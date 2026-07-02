package com.panda.merge.mapper;

import com.panda.merge.model.ThirdMatchFrontStatistics;
import com.panda.merge.model.ThirdMatchFrontStatisticsExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ThirdMatchFrontStatisticsMapper {
    long countByExample(ThirdMatchFrontStatisticsExample example);

    int deleteByExample(ThirdMatchFrontStatisticsExample example);

    int deleteByPrimaryKey(String id);

    int insert(ThirdMatchFrontStatistics record);

    int insertSelective(ThirdMatchFrontStatistics record);

    List<ThirdMatchFrontStatistics> selectByExample(ThirdMatchFrontStatisticsExample example);

    ThirdMatchFrontStatistics selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") ThirdMatchFrontStatistics record, @Param("example") ThirdMatchFrontStatisticsExample example);

    int updateByExample(@Param("record") ThirdMatchFrontStatistics record, @Param("example") ThirdMatchFrontStatisticsExample example);

    int updateByPrimaryKeySelective(ThirdMatchFrontStatistics record);

    int updateByPrimaryKey(ThirdMatchFrontStatistics record);
}