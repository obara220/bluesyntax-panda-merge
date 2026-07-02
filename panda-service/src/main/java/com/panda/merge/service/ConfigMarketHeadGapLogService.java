package com.panda.merge.service;

import com.panda.merge.dto.TradeMarketHeadGapConfigDTO;
import com.panda.merge.model.ConfigMarketCategoryHeadLog;

/**
 * @author :  myname
 * @Project Name :  panda-merge
 * @Package Name :  com.panda.merge.service
 * @Description :  TODO
 * @Date: 2020-10-03 11:33
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface ConfigMarketHeadGapLogService {


    ConfigMarketCategoryHeadLog create(String linkId, Long operaterId, TradeMarketHeadGapConfigDTO tradeMarketHeadGapConfigDTO);

}
