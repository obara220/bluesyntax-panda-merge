package com.panda.merge.mapper;

import com.panda.merge.model.ThirdSportMarketOdds;
import com.panda.merge.model.ThirdSportMarketOddsExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ThirdSportMarketOddsMapper {
    long countByExample(ThirdSportMarketOddsExample example);

    int deleteByExample(ThirdSportMarketOddsExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ThirdSportMarketOdds record);

    int insertSelective(ThirdSportMarketOdds record);

    List<ThirdSportMarketOdds> selectByExample(ThirdSportMarketOddsExample example);

    ThirdSportMarketOdds selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ThirdSportMarketOdds record, @Param("example") ThirdSportMarketOddsExample example);

    int updateByExample(@Param("record") ThirdSportMarketOdds record, @Param("example") ThirdSportMarketOddsExample example);

    int updateByPrimaryKeySelective(ThirdSportMarketOdds record);

    int updateByPrimaryKey(ThirdSportMarketOdds record);
}