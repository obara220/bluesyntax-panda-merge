package com.panda.merge.dao;

import com.panda.merge.model.StandardOutrightMarket;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author : raulvii
 * @project Name : panda-merge
 * @package Name : com.panda.merge.dao
 * @date: 2020-10-09
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */
public interface StandardOutrightMarketDao {

    /**
     * 批量添加
     *
     * @param standardOutrightMarketList
     */
    void saveBatch(@Param("standardOutrightMarketList") List<StandardOutrightMarket> standardOutrightMarketList);

    /**
     * 批量修改
     *
     * @param i18nOutrightMarketList
     */
    void updateBatchById(@Param("standardOutrightMarketList") List<StandardOutrightMarket> standardOutrightMarketList);

}
