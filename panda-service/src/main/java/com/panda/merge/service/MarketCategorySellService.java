package com.panda.merge.service;

import com.panda.merge.bo.MarketCategorySellBO;
import com.panda.merge.model.MarketCategorySell;

import java.util.Collection;
import java.util.List;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/18 <br>
 * @see com.panda.merge.service <br>
 */
public interface MarketCategorySellService {

    MarketCategorySell getItem(String linkid ,Long matchId, Integer marketType, Long marketCategoryId);

    List<MarketCategorySell> getItems(List<String> marketSellkeys);

    /**
     * 获取玩法开售key 非完整redis key
     *
     * @param matchId
     * @param marketCategoryId
     * @param marketType
     * @return
     */
    String getKey(Long matchId, Long marketCategoryId, Integer marketType);

    List<MarketCategorySell> getItem(Long matchId, String marketType);

    MarketCategorySell update(MarketCategorySell marketCategorySell);

    List<MarketCategorySell> getItemByPrimary(Long matchId, Long periodId);
    List<MarketCategorySellBO> getItemByPrimaryCache(Long matchId, Long periodId);

    List<MarketCategorySell> getItemByPrimaryOpen(Long matchId, Long periodId);
    List<MarketCategorySellBO> getItemByPrimaryOpenCache(Long matchId, Long periodId);

    void saveBatch(Long standardMatchId, Integer marketType,List<MarketCategorySell> categorySellConfigurations);

    void batchUpdate(Long standardMatchId , Integer marketType,List<MarketCategorySell> existCategorySellConfigurations);
    void batchUpdateById(Long standardMatchId , Integer marketType,List<MarketCategorySell> existCategorySellConfigurations);

    MarketCategorySell updateByItem(MarketCategorySell marketCategorySell);

    void removeCache(Long standardMatchId , Integer marketType, Long marketCategoryId);

    void removeCashes(Long standardMatchId , Integer marketType, Collection<Long> marketCategoryIds);

    List<MarketCategorySell> getItemByMatchId(Long matchId);

    List<MarketCategorySell> getItem(Long matchId, List<Long> marketCategoryIds);

    /**
     *  赛事 + 玩法数据源  + 类型
     * @param matchId
     * @return
     */
    List<MarketCategorySell> getItemByDataSourceCodeAndMarketType(Long matchId, String dataSourceCode, String marketType);


    /**
     *  赛事   + 类型
     * @param matchId
     * @return
     */
    List<MarketCategorySell> getItemByMarketType(Long matchId, String marketType);

}
