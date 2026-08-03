package com.panda.merge.mapper;

import com.panda.merge.model.ThirdMatchHistoryStatistics;
import com.panda.merge.model.ThirdMatchHistoryStatisticsExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ThirdMatchHistoryStatisticsMapper {
    long countByExample(ThirdMatchHistoryStatisticsExample example);

    int deleteByExample(ThirdMatchHistoryStatisticsExample example);

    int deleteByPrimaryKey(String id);

    int insert(ThirdMatchHistoryStatistics record);

    int insertSelective(ThirdMatchHistoryStatistics record);

    List<ThirdMatchHistoryStatistics> selectByExample(ThirdMatchHistoryStatisticsExample example);

    ThirdMatchHistoryStatistics selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") ThirdMatchHistoryStatistics record, @Param("example") ThirdMatchHistoryStatisticsExample example);

    int updateByExample(@Param("record") ThirdMatchHistoryStatistics record, @Param("example") ThirdMatchHistoryStatisticsExample example);

    int updateByPrimaryKeySelective(ThirdMatchHistoryStatistics record);

    int updateByPrimaryKey(ThirdMatchHistoryStatistics record);
}