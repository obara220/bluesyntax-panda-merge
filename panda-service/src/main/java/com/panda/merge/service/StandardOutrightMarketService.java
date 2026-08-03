package com.panda.merge.service;

import com.panda.merge.model.StandardOutrightMarket;
import com.panda.merge.model.StandardOutrightMarketExample;

import java.util.List;

/**
 * @author : raulvii
 * @project Name : panda-merge
 * @package Name : com.panda.merge.service
 * @description : TODO
 * @date: 2020-10-09
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */
public interface StandardOutrightMarketService {

    /**
     * 批量添加
     *
     * @param standardOutrightMarketList
     */
    void saveBatch(List<StandardOutrightMarket> standardOutrightMarketList);

    /**
     * 批量修改
     *
     * @param standardOutrightMarketList
     */
    void updateBatchById(List<StandardOutrightMarket> standardOutrightMarketList);

    int updateByExampleSelective(StandardOutrightMarket record, StandardOutrightMarketExample example);

    List<StandardOutrightMarket> selectOutrightMarketSellList(Long standardMatchId);

    List<StandardOutrightMarket> selectOutrightMarketSellListByIds(List<Long> standardMatchIds);

    StandardOutrightMarket selectByExample(Long standardMarketId);

    /**
     * 主键修改
     * @param record
     */
    void updateByPrimaryKeySelective(StandardOutrightMarket record);

    /**
     * 根据主键id获取冠军盘口数据
     * @param relationMarketId
     * @return
     */
    public StandardOutrightMarket getOutrightMarketData (Long relationMarketId);

    /**
     * 查询赛事下的所有冠军盘口
     *
     * @param matchId
     * @return
     */
    List<StandardOutrightMarket> queryChampionMarket(Long matchId);

    /**
     * 批量冠军盘口数据的查询
     * @param ids
     * @return
     */
    List<StandardOutrightMarket> queryChampionMarket(List<Long> ids);


}
