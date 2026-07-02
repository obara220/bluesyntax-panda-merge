package com.panda.merge.mapper;

import com.panda.merge.model.MatchStatisticsInfo;
import com.panda.merge.model.MatchStatisticsInfoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchStatisticsInfoMapper {
    long countByExample(MatchStatisticsInfoExample example);

    int deleteByExample(MatchStatisticsInfoExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchStatisticsInfo record);

    int insertSelective(MatchStatisticsInfo record);

    List<MatchStatisticsInfo> selectByExample(MatchStatisticsInfoExample example);

    MatchStatisticsInfo selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MatchStatisticsInfo record, @Param("example") MatchStatisticsInfoExample example);

    int updateByExample(@Param("record") MatchStatisticsInfo record, @Param("example") MatchStatisticsInfoExample example);

    int updateByPrimaryKeySelective(MatchStatisticsInfo record);

    int updateByPrimaryKey(MatchStatisticsInfo record);
}