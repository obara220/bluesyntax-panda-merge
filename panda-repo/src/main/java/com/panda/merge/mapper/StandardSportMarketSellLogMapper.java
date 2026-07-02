package com.panda.merge.mapper;

import com.panda.merge.model.StandardSportMarketSellLog;
import com.panda.merge.model.StandardSportMarketSellLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StandardSportMarketSellLogMapper {
    long countByExample(StandardSportMarketSellLogExample example);

    int deleteByExample(StandardSportMarketSellLogExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(StandardSportMarketSellLog record);

    int insertSelective(StandardSportMarketSellLog record);

    List<StandardSportMarketSellLog> selectByExample(StandardSportMarketSellLogExample example);

    StandardSportMarketSellLog selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") StandardSportMarketSellLog record, @Param("example") StandardSportMarketSellLogExample example);

    int updateByExample(@Param("record") StandardSportMarketSellLog record, @Param("example") StandardSportMarketSellLogExample example);

    int updateByPrimaryKeySelective(StandardSportMarketSellLog record);

    int updateByPrimaryKey(StandardSportMarketSellLog record);
}