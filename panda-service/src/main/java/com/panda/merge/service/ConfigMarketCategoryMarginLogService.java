package com.panda.merge.service;

import com.panda.merge.dto.MarketMarginDtlDTO;
import com.panda.merge.dto.TradeMarketMarginConfigDTO;
import com.panda.merge.model.ConfigMarketCategoryMarginLog;

/**
 * @author :  myname
 * @Project Name :  panda-merge
 * @Package Name :  com.panda.merge.service
 * @Description :  TODO
 * @Date: 2020-10-03 11:33
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface ConfigMarketCategoryMarginLogService {

    ConfigMarketCategoryMarginLog create(String linkId, Long standardMatchInfoId, Long standardCategoryId, Long childStandardCategoryId,Integer marketType, Integer placeNum, MarketMarginDtlDTO marketMarginDtlDTO, Long operaterId);

}
