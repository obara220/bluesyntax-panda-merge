package com.panda.merge.v2.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.merge.model.MatchSettleAbnormal;
import com.panda.merge.model.MatchSettleAbnormalExample;
import com.panda.merge.v2.entity.MatchSettleAbnormalEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MatchSettleAbnormalV2Mapper extends BaseMapper<MatchSettleAbnormalEntity> {

    long countByExample(MatchSettleAbnormalExample example);

    int deleteByExample(MatchSettleAbnormalExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchSettleAbnormal record);

    int insertSelective(MatchSettleAbnormal record);

    List<MatchSettleAbnormalEntity> selectByExample(MatchSettleAbnormalExample example);

    int updateByExampleSelective(@Param("record") MatchSettleAbnormal record, @Param("example") MatchSettleAbnormalExample example);

    int updateByExample(@Param("record") MatchSettleAbnormal record, @Param("example") MatchSettleAbnormalExample example);

    int updateByPrimaryKeySelective(MatchSettleAbnormal record);

    int updateByPrimaryKey(MatchSettleAbnormal record);


}
