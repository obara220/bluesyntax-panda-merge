package com.panda.merge.service;

import com.panda.merge.dto.TradeMarketConfigDTO;
import com.panda.merge.model.ConfigTradeMarketLog;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/21 <br>
 * @see com.panda.merge.service <br>
 */
public interface ConfigTradeMarketLogService {

    ConfigTradeMarketLog create(String linkId, TradeMarketConfigDTO tradeMarketConfigDTO);

}
