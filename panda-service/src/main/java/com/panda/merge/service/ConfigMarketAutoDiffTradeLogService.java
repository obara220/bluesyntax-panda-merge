package com.panda.merge.service;

import com.panda.merge.dto.TradeMarketAutoDiffConfigItemDTO;
import com.panda.merge.model.ConfigMarketAutoDiffTradeLog;

/**
 * @author :  myname
 * @Project Name :  panda-merge
 * @Package Name :  com.panda.merge.service
 * @Description :  TODO
 * @Date: 2020-10-20 15:04
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface ConfigMarketAutoDiffTradeLogService {

    ConfigMarketAutoDiffTradeLog create(String linkId, TradeMarketAutoDiffConfigItemDTO tradeMarketAutoDiffConfigItemDTO, Long matchId, Long operaterId);

}
