package com.panda.merge.mapper;

import com.panda.merge.model.StandardSportMarketCategory;
import com.panda.merge.model.StandardSportMarketCategoryExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface StandardSportMarketCategoryMapper {
    long countByExample(StandardSportMarketCategoryExample example);

    int deleteByExample(StandardSportMarketCategoryExample example);

    int deleteByPrimaryKey(Long id);

    int insert(StandardSportMarketCategory record);

    int insertSelective(StandardSportMarketCategory record);

    List<StandardSportMarketCategory> selectByExampleWithBLOBs(StandardSportMarketCategoryExample example);

    List<StandardSportMarketCategory> selectByExample(StandardSportMarketCategoryExample example);

    StandardSportMarketCategory selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") StandardSportMarketCategory record, @Param("example") StandardSportMarketCategoryExample example);

    int updateByExampleWithBLOBs(@Param("record") StandardSportMarketCategory record, @Param("example") StandardSportMarketCategoryExample example);

    int updateByExample(@Param("record") StandardSportMarketCategory record, @Param("example") StandardSportMarketCategoryExample example);

    int updateByPrimaryKeySelective(StandardSportMarketCategory record);

    int updateByPrimaryKeyWithBLOBs(StandardSportMarketCategory record);

    int updateByPrimaryKey(StandardSportMarketCategory record);
}