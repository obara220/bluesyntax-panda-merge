package com.panda.merge.service;

import com.panda.merge.dto.OutrightTradeOddsConfigDTO;
import com.panda.merge.model.ConfigOutrightTradeOdds;

import java.util.List;
import java.util.Map;
import java.util.Set;

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

    List<ConfigOutrightTradeOdds> selectItems(Map<Long, Set<Long>> matchAndOddIdsMap);

    /**
     *  批量查询投注项的状态
     *
     * @param oddsIds
     * @return
     */
    List<ConfigOutrightTradeOdds> selectOddsTradeList(List<Long> oddsIds);


}
