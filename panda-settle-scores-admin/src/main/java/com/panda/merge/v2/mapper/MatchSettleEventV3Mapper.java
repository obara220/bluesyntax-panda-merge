package com.panda.merge.v2.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.merge.model.MatchSettleEvent;
import com.panda.merge.model.MatchSettleEventExample;
import com.panda.merge.v2.entity.MatchSettleEventEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface MatchSettleEventV3Mapper extends BaseMapper<MatchSettleEventEntity> {
    long countByExample(MatchSettleEventExample example);

    int deleteByExample(MatchSettleEventExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchSettleEvent record);

    int insertSelective(MatchSettleEvent record);

    List<MatchSettleEventEntity> selectByExample(MatchSettleEventExample example);

    MatchSettleEventEntity selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MatchSettleEvent record, @Param("example") MatchSettleEventExample example);

    int updateByExample(@Param("record") MatchSettleEvent record, @Param("example") MatchSettleEventExample example);

    int updateByPrimaryKeySelective(MatchSettleEvent record);

    int updateByPrimaryKey(MatchSettleEvent record);

}
