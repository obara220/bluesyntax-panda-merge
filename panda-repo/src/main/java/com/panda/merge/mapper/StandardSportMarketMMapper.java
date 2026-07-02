package com.panda.merge.mapper;

import com.panda.merge.model.StandardSportMarketM;
import com.panda.merge.model.StandardSportMarketMExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface StandardSportMarketMMapper {
    long countByExample(StandardSportMarketMExample example);

    int deleteByExample(StandardSportMarketMExample example);

    int deleteByPrimaryKey(Long id);

    int insert(StandardSportMarketM record);

    int insertSelective(StandardSportMarketM record);

    List<StandardSportMarketM> selectByExample(StandardSportMarketMExample example);

    StandardSportMarketM selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") StandardSportMarketM record, @Param("example") StandardSportMarketMExample example);

    int updateByExample(@Param("record") StandardSportMarketM record, @Param("example") StandardSportMarketMExample example);

    int updateByPrimaryKeySelective(StandardSportMarketM record);

    int updateByPrimaryKey(StandardSportMarketM record);
}