package com.panda.merge.service;

import com.panda.merge.dto.TradeMarketAutoDiffConfigItemDTO;
import com.panda.merge.model.ConfigMarketAutoDiffTrade;

import java.util.List;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/25 <br>
 * @see com.panda.merge.service <br>
 */
public interface ConfigMarketAutoDiffTradeService {

    ConfigMarketAutoDiffTrade getItem(String linkId,Long matchId, Long relationMarketId, String oddsType);

    ConfigMarketAutoDiffTrade create(String linkId, TradeMarketAutoDiffConfigItemDTO tradeMarketAutoDiffConfigItemDTO, Long matchId, Long operaterId);

    ConfigMarketAutoDiffTrade updata(ConfigMarketAutoDiffTrade configMarketAutoDiffTrade);

//    void del(ConfigMarketAutoDiffTrade diffTrade);

    /**
     * 删除水差
     *
     * @param matchId
     */
    void delDiffByMatchInfoId(Long matchId, String linkId);

    /**
     * 根据赛事id，玩法集合清除水差
     *
     * @param linkId
     * @param matchId
     * @param categoryList
     */
    void delDiffByMatchIdAndCategoryList(String linkId, Long matchId, List<Long> categoryList);
}
