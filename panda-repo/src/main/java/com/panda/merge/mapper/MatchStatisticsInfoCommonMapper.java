package com.panda.merge.mapper;

import com.panda.merge.model.MatchStatisticsInfoCommon;
import com.panda.merge.model.MatchStatisticsInfoCommonExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchStatisticsInfoCommonMapper {
    long countByExample(MatchStatisticsInfoCommonExample example);

    int deleteByExample(MatchStatisticsInfoCommonExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchStatisticsInfoCommon record);

    int insertSelective(MatchStatisticsInfoCommon record);

    List<MatchStatisticsInfoCommon> selectByExample(MatchStatisticsInfoCommonExample example);

    MatchStatisticsInfoCommon selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MatchStatisticsInfoCommon record, @Param("example") MatchStatisticsInfoCommonExample example);

    int updateByExample(@Param("record") MatchStatisticsInfoCommon record, @Param("example") MatchStatisticsInfoCommonExample example);

    int updateByPrimaryKeySelective(MatchStatisticsInfoCommon record);

    int updateByPrimaryKey(MatchStatisticsInfoCommon record);
}