package com.panda.merge.service;

import com.panda.merge.model.ConfigMarketMarginGap;

import java.util.List;

public interface ConfigMarketMarginGapService {

    List<ConfigMarketMarginGap> getItemList(Long standardMatchId, Long marketCategoryId,Long childMarketCategoryId, Integer placeNum);

    ConfigMarketMarginGap getItem(Long standardMatchId, Long marketCategoryId,Long childMarketCategoryId, String oddsType, Integer placeNum);

    void insertList(String linkId, Long standardMatchInfoId, List<ConfigMarketMarginGap> configMarketMargins);

    void updateList(String linkId, Long standardMatchInfoId, List<ConfigMarketMarginGap> configMarketMargins);

    /**
     * 赛事 玩法集合 清除水差 概率
     *
     * @param linkId
     * @param standardMatchId
     * @param categoryList
     */
    void updateByMatchIdAndCategoryList(String linkId, Long standardMatchId, List<Long> categoryList);


    /**
     * 赛事  清除水差 概率
     *
     * @param linkId
     * @param standardMatchId
     */
    void updateByMatchId(String linkId, Long standardMatchId);

    /**
     * 赔率变动清理概率差
     *
     * @param linkId
     * @param matchId
     * @param marketCategoryId
     * @param changeOddsType
     */
    void upProbabilityByMatchIdAndCategoryId(String linkId, Long matchId, Long marketCategoryId,Long childMarketCategoryId, List<String> changeOddsType, Integer placeNum);

    /**
     * 赔率变动清理概率差
     *
     * @param linkId
     * @param matchId
     * @param marketCategoryId
     */
    void upProbabilityByMatchIdAndCategoryIdList(String linkId, Long matchId, List<Long> marketCategoryId);


    void updateByCategory(String linkId, Long standardMatchInfoId, Long standardCategoryId, Double margin);

}
