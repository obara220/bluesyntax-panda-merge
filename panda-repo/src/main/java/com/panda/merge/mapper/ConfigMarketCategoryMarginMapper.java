package com.panda.merge.mapper;

import com.panda.merge.model.ConfigMarketCategoryMargin;
import com.panda.merge.model.ConfigMarketCategoryMarginExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigMarketCategoryMarginMapper {
    long countByExample(ConfigMarketCategoryMarginExample example);

    int deleteByExample(ConfigMarketCategoryMarginExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ConfigMarketCategoryMargin record);

    int insertSelective(ConfigMarketCategoryMargin record);

    List<ConfigMarketCategoryMargin> selectByExample(ConfigMarketCategoryMarginExample example);

    ConfigMarketCategoryMargin selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ConfigMarketCategoryMargin record, @Param("example") ConfigMarketCategoryMarginExample example);

    int updateByExample(@Param("record") ConfigMarketCategoryMargin record, @Param("example") ConfigMarketCategoryMarginExample example);

    int updateByPrimaryKeySelective(ConfigMarketCategoryMargin record);

    int updateByPrimaryKey(ConfigMarketCategoryMargin record);
}