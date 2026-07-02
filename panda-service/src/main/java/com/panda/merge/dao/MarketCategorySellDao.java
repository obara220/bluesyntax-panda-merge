package com.panda.merge.dao;

import com.panda.merge.model.MarketCategorySell;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author :  Riben
 * @Project Name :  panda-merge
 * @Package Name :  com.panda.merge.dao
 * @Description :  TODO
 * @Date: 2020-09-18 19:13
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface MarketCategorySellDao {


    void saveBatch(@Param("categorySellConfigurations")List<MarketCategorySell> categorySellConfigurations);

    void batchUpdate(@Param("categorySellConfigurations") List<MarketCategorySell> categorySellConfigurations);
    void batchUpdateById(@Param("categorySellConfigurations") List<MarketCategorySell> categorySellConfigurations);

    void updateByItem(@Param("item") MarketCategorySell marketCategorySell);
}
