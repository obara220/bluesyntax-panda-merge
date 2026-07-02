package com.panda.merge.mapper;

import com.panda.merge.model.StandardMarketCategory;
import com.panda.merge.model.StandardMarketCategoryExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface StandardMarketCategoryMapper {
    long countByExample(StandardMarketCategoryExample example);

    int deleteByExample(StandardMarketCategoryExample example);

    int deleteByPrimaryKey(Long id);

    int insert(StandardMarketCategory record);

    int insertSelective(StandardMarketCategory record);

    List<StandardMarketCategory> selectByExampleWithBLOBs(StandardMarketCategoryExample example);

    List<StandardMarketCategory> selectByExample(StandardMarketCategoryExample example);

    StandardMarketCategory selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") StandardMarketCategory record, @Param("example") StandardMarketCategoryExample example);

    int updateByExampleWithBLOBs(@Param("record") StandardMarketCategory record, @Param("example") StandardMarketCategoryExample example);

    int updateByExample(@Param("record") StandardMarketCategory record, @Param("example") StandardMarketCategoryExample example);

    int updateByPrimaryKeySelective(StandardMarketCategory record);

    int updateByPrimaryKeyWithBLOBs(StandardMarketCategory record);

    int updateByPrimaryKey(StandardMarketCategory record);
}