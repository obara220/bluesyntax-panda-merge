package com.panda.merge.mapper;

import com.panda.merge.model.MatchSettleEvent;
import com.panda.merge.model.MatchSettleEventExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchSettleEventMapper {
    long countByExample(MatchSettleEventExample example);

    int deleteByExample(MatchSettleEventExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchSettleEvent record);

    int insertSelective(MatchSettleEvent record);

    List<MatchSettleEvent> selectByExample(MatchSettleEventExample example);

    MatchSettleEvent selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MatchSettleEvent record, @Param("example") MatchSettleEventExample example);

    int updateByExample(@Param("record") MatchSettleEvent record, @Param("example") MatchSettleEventExample example);

    int updateByPrimaryKeySelective(MatchSettleEvent record);

    int updateByPrimaryKey(MatchSettleEvent record);
}