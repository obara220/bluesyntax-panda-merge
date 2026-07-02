package com.panda.merge.mapper;

import com.panda.merge.model.MatchSettleAbnormal;
import com.panda.merge.model.MatchSettleAbnormalExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MatchSettleAbnormalMapper {
    long countByExample(MatchSettleAbnormalExample example);

    int deleteByExample(MatchSettleAbnormalExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchSettleAbnormal record);

    int insertSelective(MatchSettleAbnormal record);

    List<MatchSettleAbnormal> selectByExample(MatchSettleAbnormalExample example);

    MatchSettleAbnormal selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MatchSettleAbnormal record, @Param("example") MatchSettleAbnormalExample example);

    int updateByExample(@Param("record") MatchSettleAbnormal record, @Param("example") MatchSettleAbnormalExample example);

    int updateByPrimaryKeySelective(MatchSettleAbnormal record);

    int updateByPrimaryKey(MatchSettleAbnormal record);
}