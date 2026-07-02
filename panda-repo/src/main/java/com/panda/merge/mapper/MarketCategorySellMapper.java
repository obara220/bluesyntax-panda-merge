package com.panda.merge.mapper;

import com.panda.merge.model.MarketCategorySell;
import com.panda.merge.model.MarketCategorySellExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface MarketCategorySellMapper {
    long countByExample(MarketCategorySellExample example);

    int deleteByExample(MarketCategorySellExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MarketCategorySell record);

    int insertSelective(MarketCategorySell record);

    List<MarketCategorySell> selectByExample(MarketCategorySellExample example);

    MarketCategorySell selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MarketCategorySell record, @Param("example") MarketCategorySellExample example);

    int updateByExample(@Param("record") MarketCategorySell record, @Param("example") MarketCategorySellExample example);

    int updateByPrimaryKeySelective(MarketCategorySell record);

    int updateByPrimaryKey(MarketCategorySell record);
}