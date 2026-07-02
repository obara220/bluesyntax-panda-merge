package com.panda.merge.service;

import com.panda.merge.dto.TradeMarketConfigDTO;
import com.panda.merge.model.ConfigTradeMarket;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/21 <br>
 * @see com.panda.merge.service <br>
 */
public interface ConfigTradeMarketService {

    ConfigTradeMarket create(String linkId, TradeMarketConfigDTO tradeMarketConfigDTO);

}
