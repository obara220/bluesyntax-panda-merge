package com.panda.merge.mapper;

import com.panda.merge.model.ThirdMatchHistoryOdds;
import com.panda.merge.model.ThirdMatchHistoryOddsExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ThirdMatchHistoryOddsMapper {
    long countByExample(ThirdMatchHistoryOddsExample example);

    int deleteByExample(ThirdMatchHistoryOddsExample example);

    int deleteByPrimaryKey(String id);

    int insert(ThirdMatchHistoryOdds record);

    int insertSelective(ThirdMatchHistoryOdds record);

    List<ThirdMatchHistoryOdds> selectByExample(ThirdMatchHistoryOddsExample example);

    ThirdMatchHistoryOdds selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") ThirdMatchHistoryOdds record, @Param("example") ThirdMatchHistoryOddsExample example);

    int updateByExample(@Param("record") ThirdMatchHistoryOdds record, @Param("example") ThirdMatchHistoryOddsExample example);

    int updateByPrimaryKeySelective(ThirdMatchHistoryOdds record);

    int updateByPrimaryKey(ThirdMatchHistoryOdds record);
}