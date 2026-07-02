package com.panda.merge.dao;

import com.panda.merge.model.I18nMarketCategory;
import com.panda.merge.model.I18nMarketOdds;
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
public interface I18nMarketOddsDao {
    /**
     * 批量添加
     *
     * @param i18nMarketOddsList
     */
    void saveBatch(@Param("i18nMarketOddsList") List<I18nMarketOdds> i18nMarketOddsList);

    /**
     * 批量修改
     *
     * @param i18nMarketOddsList
     */
    void updateBatchById(@Param("i18nMarketOddsList") List<I18nMarketOdds> i18nMarketOddsList);
}
