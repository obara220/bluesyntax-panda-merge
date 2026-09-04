package com.panda.merge.service;

import com.panda.merge.dto.OutrightTradeOddsConfigDTO;
import com.panda.merge.model.ConfigOutrightTradeOdds;

/**
 * <Description> <br>
 * 操作表config_outright_trade_type
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2021/6/10 <br>
 * @see com.panda.merge.service <br>
 */
public interface OutrightTradeOddsConfigService {

    /**
     * 新增
     *
     *
     * @param linkId
     * @param outrightTradeOddsConfigDTO
     * @return
     */
    ConfigOutrightTradeOdds insertItem(String linkId, OutrightTradeOddsConfigDTO outrightTradeOddsConfigDTO);

    /**
     * 修改
     *
     * @param configOutrightTradeOdds
     * @return
     */
    ConfigOutrightTradeOdds updateItem(ConfigOutrightTradeOdds configOutrightTradeOdds);

    /**
     * 查询
     *
     * @param standardMatchId
     * @param standardMarketOddsId
     * @return
     */
    ConfigOutrightTradeOdds selectItem(Long standardMatchId, Long standardMarketOddsId);


}
