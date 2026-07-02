package com.panda.merge.mapper;

import com.panda.merge.model.MatchSettleThirdBasketScore;
import com.panda.merge.model.MatchSettleThirdBasketScoreExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface MatchSettleThirdBasketScoreMapper {
    long countByExample(MatchSettleThirdBasketScoreExample example);

    int deleteByExample(MatchSettleThirdBasketScoreExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchSettleThirdBasketScore record);

    int insertSelective(MatchSettleThirdBasketScore record);

    List<MatchSettleThirdBasketScore> selectByExample(MatchSettleThirdBasketScoreExample example);

    MatchSettleThirdBasketScore selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MatchSettleThirdBasketScore record, @Param("example") MatchSettleThirdBasketScoreExample example);

    int updateByExample(@Param("record") MatchSettleThirdBasketScore record, @Param("example") MatchSettleThirdBasketScoreExample example);

    int updateByPrimaryKeySelective(MatchSettleThirdBasketScore record);

    int updateByPrimaryKey(MatchSettleThirdBasketScore record);
}