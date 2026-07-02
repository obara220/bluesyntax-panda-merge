package com.panda.merge.mapper;

import com.panda.merge.model.MatchSettleScore;
import com.panda.merge.model.MatchSettleScoreExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchSettleScoreMapper {

    long countByExample(MatchSettleScoreExample example);

    int deleteByExample(MatchSettleScoreExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchSettleScore record);

    int insertSelective(MatchSettleScore record);

    List<MatchSettleScore> selectByExample(MatchSettleScoreExample example);

    MatchSettleScore selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MatchSettleScore record, @Param("example") MatchSettleScoreExample example);

    int updateByExample(@Param("record") MatchSettleScore record, @Param("example") MatchSettleScoreExample example);

    int updateByPrimaryKeySelective(MatchSettleScore record);

    int updateByPrimaryKey(MatchSettleScore record);

    void batchInsert(List<MatchSettleScore> configs);
}