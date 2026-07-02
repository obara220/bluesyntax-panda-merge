package com.panda.merge.mapper;

import com.panda.merge.model.StandardSportMarketOddsTrade;
import com.panda.merge.model.StandardSportMarketOddsTradeExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StandardSportMarketOddsTradeMapper {
    long countByExample(StandardSportMarketOddsTradeExample example);

    int deleteByExample(StandardSportMarketOddsTradeExample example);

    int deleteByPrimaryKey(Long id);

    int insert(StandardSportMarketOddsTrade record);

    int insertSelective(StandardSportMarketOddsTrade record);

    List<StandardSportMarketOddsTrade> selectByExample(StandardSportMarketOddsTradeExample example);

    StandardSportMarketOddsTrade selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") StandardSportMarketOddsTrade record, @Param("example") StandardSportMarketOddsTradeExample example);

    int updateByExample(@Param("record") StandardSportMarketOddsTrade record, @Param("example") StandardSportMarketOddsTradeExample example);

    int updateByPrimaryKeySelective(StandardSportMarketOddsTrade record);

    int updateByPrimaryKey(StandardSportMarketOddsTrade record);
}