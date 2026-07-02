package com.panda.merge.mapper;

import com.panda.merge.model.StandardMarketCategoryMargin;
import com.panda.merge.model.StandardMarketCategoryMarginExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StandardMarketCategoryMarginMapper {
    long countByExample(StandardMarketCategoryMarginExample example);

    int deleteByExample(StandardMarketCategoryMarginExample example);

    int deleteByPrimaryKey(Long id);

    int insert(StandardMarketCategoryMargin record);

    int insertSelective(StandardMarketCategoryMargin record);

    List<StandardMarketCategoryMargin> selectByExample(StandardMarketCategoryMarginExample example);

    StandardMarketCategoryMargin selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") StandardMarketCategoryMargin record, @Param("example") StandardMarketCategoryMarginExample example);

    int updateByExample(@Param("record") StandardMarketCategoryMargin record, @Param("example") StandardMarketCategoryMarginExample example);

    int updateByPrimaryKeySelective(StandardMarketCategoryMargin record);

    int updateByPrimaryKey(StandardMarketCategoryMargin record);
}