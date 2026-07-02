package com.panda.merge.service;

import com.panda.merge.model.StandardSportMarketSell;

import java.util.List;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/14 <br>
 * @see com.panda.merge.service <br>
 */
public interface StandardSportMarketSellService {

    StandardSportMarketSell getItem(Long standardMatchId);

    List<StandardSportMarketSell> getItems(List<Long> standardMatchIds);

    StandardSportMarketSell update(StandardSportMarketSell standardSportMarketSell);

    /** 刷新缓存*/
    StandardSportMarketSell refreshCache(Long standardMatchId);

    StandardSportMarketSell refreshCache(StandardSportMarketSell item);

    List<StandardSportMarketSell> selectByStandardMatchIds(List<Long> standardMatchIds);

    List<StandardSportMarketSell> selectByStandardMatchIdsAndRefreshCache(List<Long> ids);

    /** 清除特定赛事缓存 **/
    void evictCache(Long standardMatchId);



    List<Long> getMatchSellMatchIdByExample();
}
