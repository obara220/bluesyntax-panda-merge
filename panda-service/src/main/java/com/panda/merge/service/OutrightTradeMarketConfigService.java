package com.panda.merge.service;

import com.panda.merge.dto.OutrightTradeMarketConfigDTO;
import com.panda.merge.model.ConfigOutrightTradeMarket;

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
public interface OutrightTradeMarketConfigService {

    /**
     * 新增
     *
     *
     * @param linkId
     * @param outrightTradeMarketConfigDTO
     * @return
     */
    ConfigOutrightTradeMarket insertItem(String linkId, OutrightTradeMarketConfigDTO outrightTradeMarketConfigDTO);

    /**
     * 修改
     *
     * @param configOutrightTradeMarket
     * @return
     */
    ConfigOutrightTradeMarket updateItem(ConfigOutrightTradeMarket configOutrightTradeMarket);

    /**
     * 查询
     * @param standardMatchId
     * @param standardMarketId
     * @return
     */
    ConfigOutrightTradeMarket selectItem(Long standardMatchId, Long standardMarketId);

    List<ConfigOutrightTradeMarket> selectItems(Map<Long, Set<Long>> matchAndMarketIdsMap);


    /**
     * 查询
     * @param standardMatchId
     * @return
     */
    List<ConfigOutrightTradeMarket> selectListItem(Long standardMatchId);


    /**
     * 根据盘口id获取对应的盘口操盘状态
     * @param ids
     * @return
     */
    List<ConfigOutrightTradeMarket> selectList(List<Long> ids);

    /**
     * 根据赛事查询
     * @param matchIds
     * @return
     */
    Map<Long, Integer> queryTradeStatus(List<Long> matchIds);


    /**
     * 赛事批量查询对应的操盘开关
     * @param matchIds
     * @return
     */
    List<ConfigOutrightTradeMarket> queryTradeMarketList( List<Long> matchIds);


    /**
     * 批量修改
     *
     * @param configOutrightTradeMarkets
     */
    void updateBatchById(List<ConfigOutrightTradeMarket> configOutrightTradeMarkets);

    /**
     * 批量保存
     *
     * @param configOutrightTradeMarketList
     */
    void saveBatch(List<ConfigOutrightTradeMarket> configOutrightTradeMarketList);
}
