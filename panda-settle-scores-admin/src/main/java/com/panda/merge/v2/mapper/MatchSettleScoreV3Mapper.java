package com.panda.merge.v2.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.merge.model.MatchSettleScore;
import com.panda.merge.model.MatchSettleScoreExample;
import com.panda.merge.v2.entity.MatchSettleScoreEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface MatchSettleScoreV3Mapper extends BaseMapper<MatchSettleScoreEntity> {


    long countByExample(MatchSettleScoreExample example);

    int deleteByExample(MatchSettleScoreExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchSettleScore record);

    int insertSelective(MatchSettleScore record);

    List<MatchSettleScoreEntity> selectByExample(MatchSettleScoreExample example);

    MatchSettleScoreEntity selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MatchSettleScore record, @Param("example") MatchSettleScoreExample example);

    int updateByExample(@Param("record") MatchSettleScore record, @Param("example") MatchSettleScoreExample example);

    int updateByPrimaryKeySelective(MatchSettleScore record);

    int updateByPrimaryKey(MatchSettleScore record);

    void batchInsert(List<MatchSettleScore> configs);

}
