package com.panda.merge.service;

import com.panda.merge.model.ConfigMarketCategoryMargin;

import java.util.List;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/26 <br>
 * @see com.panda.merge.service <br>
 */
public interface ConfigMarketCategoryMarginService {
    ConfigMarketCategoryMargin getItemThree(Long standardMatchInfoId, Long standardCategoryId, Long childStandardCategoryId,Integer placeNum, String oddsType);

    ConfigMarketCategoryMargin createThree(ConfigMarketCategoryMargin configMarketCategoryMargin);

    ConfigMarketCategoryMargin updateThree(ConfigMarketCategoryMargin configMarketCategoryMargin);

    ConfigMarketCategoryMargin getItemTwo(String linkId,Long standardMatchInfoId, Long standardCategoryId, Long childStandardCategoryId,Integer placeNum);

    ConfigMarketCategoryMargin createTwo(ConfigMarketCategoryMargin configMarketCategoryMargin);

    ConfigMarketCategoryMargin updateTwo(ConfigMarketCategoryMargin configMarketCategoryMargin);

    void insertListTwo(List<ConfigMarketCategoryMargin> configMarketCategoryMarginSaveListTwo);

    void updateListTwo(List<ConfigMarketCategoryMargin> configMarketCategoryMarginSaveListTwo);

    void insertListThree(List<ConfigMarketCategoryMargin> configMarketCategoryMarginSaveListThree);

    void updateListThree(List<ConfigMarketCategoryMargin> configMarketCategoryMarginUpdateListThree);

    void updateByCategory(String linkId, Long standardMatchInfoId, Long standardCategoryId, Double margin);
}
