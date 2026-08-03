package com.panda.merge.mapper;

import com.panda.merge.model.ThirdMatchSeasonStatistics;
import com.panda.merge.model.ThirdMatchSeasonStatisticsExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ThirdMatchSeasonStatisticsMapper {
    long countByExample(ThirdMatchSeasonStatisticsExample example);

    int deleteByExample(ThirdMatchSeasonStatisticsExample example);

    int deleteByPrimaryKey(String id);

    int insert(ThirdMatchSeasonStatistics record);

    int insertSelective(ThirdMatchSeasonStatistics record);

    List<ThirdMatchSeasonStatistics> selectByExample(ThirdMatchSeasonStatisticsExample example);

    ThirdMatchSeasonStatistics selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") ThirdMatchSeasonStatistics record, @Param("example") ThirdMatchSeasonStatisticsExample example);

    int updateByExample(@Param("record") ThirdMatchSeasonStatistics record, @Param("example") ThirdMatchSeasonStatisticsExample example);

    int updateByPrimaryKeySelective(ThirdMatchSeasonStatistics record);

    int updateByPrimaryKey(ThirdMatchSeasonStatistics record);
}