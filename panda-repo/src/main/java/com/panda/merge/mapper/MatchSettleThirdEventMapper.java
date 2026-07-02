package com.panda.merge.mapper;

import com.panda.merge.model.MatchSettleThirdEvent;
import com.panda.merge.model.MatchSettleThirdEventExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchSettleThirdEventMapper {
    long countByExample(MatchSettleThirdEventExample example);

    int deleteByExample(MatchSettleThirdEventExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchSettleThirdEvent record);

    int insertSelective(MatchSettleThirdEvent record);

    List<MatchSettleThirdEvent> selectByExample(MatchSettleThirdEventExample example);

    MatchSettleThirdEvent selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MatchSettleThirdEvent record, @Param("example") MatchSettleThirdEventExample example);

    int updateByExample(@Param("record") MatchSettleThirdEvent record, @Param("example") MatchSettleThirdEventExample example);

    int updateByPrimaryKeySelective(MatchSettleThirdEvent record);

    int updateByPrimaryKey(MatchSettleThirdEvent record);
}