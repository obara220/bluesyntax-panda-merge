package com.panda.merge.mapper;

import com.panda.merge.model.StandardSportMarketOdds;
import com.panda.merge.model.StandardSportMarketOddsExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StandardSportMarketOddsMapper {
    long countByExample(StandardSportMarketOddsExample example);

    int deleteByExample(StandardSportMarketOddsExample example);

    int deleteByPrimaryKey(Long id);

    int insert(StandardSportMarketOdds record);

    int insertSelective(StandardSportMarketOdds record);

    List<StandardSportMarketOdds> selectByExample(StandardSportMarketOddsExample example);

    StandardSportMarketOdds selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") StandardSportMarketOdds record, @Param("example") StandardSportMarketOddsExample example);

    int updateByExample(@Param("record") StandardSportMarketOdds record, @Param("example") StandardSportMarketOddsExample example);

    int updateByPrimaryKeySelective(StandardSportMarketOdds record);

    int updateByPrimaryKey(StandardSportMarketOdds record);
}