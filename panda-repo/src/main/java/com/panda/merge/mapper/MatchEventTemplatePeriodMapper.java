package com.panda.merge.mapper;

import com.panda.merge.model.MatchEventTemplatePeriod;
import com.panda.merge.model.MatchEventTemplatePeriodExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchEventTemplatePeriodMapper {
    long countByExample(MatchEventTemplatePeriodExample example);

    int deleteByExample(MatchEventTemplatePeriodExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchEventTemplatePeriod record);

    int insertSelective(MatchEventTemplatePeriod record);

    List<MatchEventTemplatePeriod> selectByExample(MatchEventTemplatePeriodExample example);

    MatchEventTemplatePeriod selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MatchEventTemplatePeriod record, @Param("example") MatchEventTemplatePeriodExample example);

    int updateByExample(@Param("record") MatchEventTemplatePeriod record, @Param("example") MatchEventTemplatePeriodExample example);

    int updateByPrimaryKeySelective(MatchEventTemplatePeriod record);

    int updateByPrimaryKey(MatchEventTemplatePeriod record);
}