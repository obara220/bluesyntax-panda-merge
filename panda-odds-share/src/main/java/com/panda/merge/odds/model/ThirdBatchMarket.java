package com.panda.merge.odds.model;

import com.panda.merge.common.OddsWrapper;
import com.panda.merge.dto.ThirdMarketDTO;
import com.panda.merge.model.MarketCategorySell;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ThirdBatchMarket
 *
 * @description:
 * @date: 7/12/2025
 **/

public class ThirdBatchMarket implements Serializable {

    public String uuid;

    public Map<Long, ThirdMatchMarket> matchDataMap;

    public Map<String, MarketCategorySell> categorySellMap;

    public List<OddsWrapper<ThirdMarketDTO>> marketList;

    public static ThirdBatchMarket create(String uuid,
                                          List<OddsWrapper<ThirdMarketDTO>> marketList,
                                          Map<String, MarketCategorySell> categorySellMap) {
        ThirdBatchMarket thirdBatchMarket = new ThirdBatchMarket();
        thirdBatchMarket.uuid = uuid;
        thirdBatchMarket.marketList = marketList;
        thirdBatchMarket.categorySellMap = categorySellMap;

        if (CollectionUtils.isNotEmpty(marketList)) {
            Map<Long, ThirdMatchMarket> matchDataMap = thirdBatchMarket.matchDataMap = new HashMap<>();
            marketList.forEach(wrapper -> {
                matchDataMap.compute(wrapper.getStandardSourceId(), (k, v) -> {
                    if (v == null) {
                        return new ThirdMatchMarket(wrapper.getStandardSourceId(),
                                                    new ArrayList<OddsWrapper<ThirdMarketDTO>>() {{add(wrapper);}});
                    }
                    v.addMarket(wrapper);
                    return v;
                });
            });
        }
        return thirdBatchMarket;
    }

}
