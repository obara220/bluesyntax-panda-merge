package com.panda.merge.dao;

import com.panda.merge.model.ConfigOutrightTradeMarket;
import com.panda.merge.model.StandardOutrightMarket;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author kepa
 */
public interface ConfigOutrightTradeMarketDao {


    /**
     * 批量修改
     *
     * @param configOutrightTradeMarkets
     */
    void updateBatchById(@Param("configOutrightTradeMarkets") List<ConfigOutrightTradeMarket> configOutrightTradeMarkets);

    /**
     * 批量插入
     *
     * @param configOutrightTradeMarketList
     */
    void saveBatch(@Param("configOutrightTradeMarketList") List<ConfigOutrightTradeMarket> configOutrightTradeMarketList);
}
