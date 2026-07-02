package com.panda.merge.mapper;

import com.panda.merge.model.MatchSettleSpOdds;
import com.panda.merge.model.MatchSettleSpOddsExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface MatchSettleSpOddsMapper {
    long countByExample(MatchSettleSpOddsExample example);

    int deleteByExample(MatchSettleSpOddsExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchSettleSpOdds record);

    int insertSelective(MatchSettleSpOdds record);

    List<MatchSettleSpOdds> selectByExample(MatchSettleSpOddsExample example);

    MatchSettleSpOdds selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MatchSettleSpOdds record, @Param("example") MatchSettleSpOddsExample example);

    int updateByExample(@Param("record") MatchSettleSpOdds record, @Param("example") MatchSettleSpOddsExample example);

    int updateByPrimaryKeySelective(MatchSettleSpOdds record);

    int updateByPrimaryKey(MatchSettleSpOdds record);
}