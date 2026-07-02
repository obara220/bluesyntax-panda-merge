package com.panda.merge.service;

import com.panda.merge.dto.TradeMarketDisplayConfigDTO;
import com.panda.merge.model.ConfigMarketDisplayTrade;

import java.util.List;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/16 <br>
 * @see com.panda.merge.service <br>
 */
public interface ConfigMarketDisplayTradeService {
    ConfigMarketDisplayTrade getItem(Long standardMatchInfoId);

    List<ConfigMarketDisplayTrade> getItems(List<Long> standardMatchIds);

    ConfigMarketDisplayTrade create(TradeMarketDisplayConfigDTO displayConfigDTO);

    ConfigMarketDisplayTrade update(ConfigMarketDisplayTrade configMarketDisplayTrade);
}
