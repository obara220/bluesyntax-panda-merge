package com.panda.merge.mapper;

import com.panda.merge.model.StandardSportMarket;
import com.panda.merge.model.StandardSportMarketExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StandardSportMarketMapper {
    long countByExample(StandardSportMarketExample example);

    int deleteByExample(StandardSportMarketExample example);

    int deleteByPrimaryKey(Long id);

    int insert(StandardSportMarket record);

    int insertSelective(StandardSportMarket record);

    List<StandardSportMarket> selectByExample(StandardSportMarketExample example);

    StandardSportMarket selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") StandardSportMarket record, @Param("example") StandardSportMarketExample example);

    int updateByExample(@Param("record") StandardSportMarket record, @Param("example") StandardSportMarketExample example);

    int updateByPrimaryKeySelective(StandardSportMarket record);

    int updateByPrimaryKey(StandardSportMarket record);
}