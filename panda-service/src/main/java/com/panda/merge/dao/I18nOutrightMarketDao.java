package com.panda.merge.dao;

import com.panda.merge.model.I18nOutrightMarket;
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
public interface I18nOutrightMarketDao {
    /**
     * 批量添加
     *
     * @param i18nOutrightMarketList
     */
    void saveBatch(@Param("i18nOutrightMarketList") List<I18nOutrightMarket> i18nOutrightMarketList);

    /**
     * 批量修改
     *
     * @param i18nOutrightMarketList
     */
    void updateBatchById(@Param("i18nOutrightMarketList") List<I18nOutrightMarket> i18nOutrightMarketList);

}
