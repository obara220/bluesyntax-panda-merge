package com.panda.merge.mapper;

import com.panda.merge.model.StandardSportMarketTrade;
import com.panda.merge.model.StandardSportMarketTradeExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StandardSportMarketTradeMapper {
    long countByExample(StandardSportMarketTradeExample example);

    int deleteByExample(StandardSportMarketTradeExample example);

    int deleteByPrimaryKey(Long id);

    int insert(StandardSportMarketTrade record);

    int insertSelective(StandardSportMarketTrade record);

    List<StandardSportMarketTrade> selectByExample(StandardSportMarketTradeExample example);

    StandardSportMarketTrade selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") StandardSportMarketTrade record, @Param("example") StandardSportMarketTradeExample example);

    int updateByExample(@Param("record") StandardSportMarketTrade record, @Param("example") StandardSportMarketTradeExample example);

    int updateByPrimaryKeySelective(StandardSportMarketTrade record);

    int updateByPrimaryKey(StandardSportMarketTrade record);
}