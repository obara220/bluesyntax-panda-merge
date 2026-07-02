package com.panda.merge.service;

import com.panda.merge.dto.TradeMarketConfigItemDTO;
import com.panda.merge.model.ConfigMarketTradeItem;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/19 <br>
 */
public interface ConfigMarketTradeItemService {

    ConfigMarketTradeItem getItem(Long standardMatchId, Long marketCategoryId,Long childMarketCategoryId, Integer placeNum);

    Map<String, ConfigMarketTradeItem> getItemByMatchAndCategorys(Long standardMatchId, Set<Long> marketCategoryIdSet);

    ConfigMarketTradeItem create(String linkId, TradeMarketConfigItemDTO tradeMarketConfigItemDTO, Long standardMatchId, Integer placeNum, Long operaterId);

    ConfigMarketTradeItem update(ConfigMarketTradeItem configMarketTradeItem);

    List<ConfigMarketTradeItem> getRecsByMatchId(String standardMatchId);
}
