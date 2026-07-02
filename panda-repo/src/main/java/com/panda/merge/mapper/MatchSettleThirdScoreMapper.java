package com.panda.merge.mapper;

import com.panda.merge.model.MatchSettleThirdScore;
import com.panda.merge.model.MatchSettleThirdScoreExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchSettleThirdScoreMapper {
    long countByExample(MatchSettleThirdScoreExample example);

    int deleteByExample(MatchSettleThirdScoreExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchSettleThirdScore record);

    int insertSelective(MatchSettleThirdScore record);

    List<MatchSettleThirdScore> selectByExample(MatchSettleThirdScoreExample example);

    MatchSettleThirdScore selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MatchSettleThirdScore record, @Param("example") MatchSettleThirdScoreExample example);

    int updateByExample(@Param("record") MatchSettleThirdScore record, @Param("example") MatchSettleThirdScoreExample example);

    int updateByPrimaryKeySelective(MatchSettleThirdScore record);

    int updateByPrimaryKey(MatchSettleThirdScore record);
}