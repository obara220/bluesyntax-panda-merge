package com.panda.merge.mapper;

import com.panda.merge.model.MatchStatisticsInfoDetail;
import com.panda.merge.model.MatchStatisticsInfoDetailExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchStatisticsInfoDetailMapper {
    long countByExample(MatchStatisticsInfoDetailExample example);

    int deleteByExample(MatchStatisticsInfoDetailExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchStatisticsInfoDetail record);

    int insertSelective(MatchStatisticsInfoDetail record);

    List<MatchStatisticsInfoDetail> selectByExample(MatchStatisticsInfoDetailExample example);

    MatchStatisticsInfoDetail selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MatchStatisticsInfoDetail record, @Param("example") MatchStatisticsInfoDetailExample example);

    int updateByExample(@Param("record") MatchStatisticsInfoDetail record, @Param("example") MatchStatisticsInfoDetailExample example);

    int updateByPrimaryKeySelective(MatchStatisticsInfoDetail record);

    int updateByPrimaryKey(MatchStatisticsInfoDetail record);
}