package com.panda.merge.dao;

import com.panda.merge.model.I18nMarketCategory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author : bevan
 * @project Name : panda-merge
 * @package Name : com.panda.merge.dao
 * @description : TODO
 * @date: 2020-09-11 13:43
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */
public interface I18nMarketCategoryDao {
    /**
     * 批量添加
     *
     * @param i18nMarketCategories
     */
    void saveBatch(@Param("i18nMarketCategories") List<I18nMarketCategory> i18nMarketCategories);

    /**
     * 批量修改
     *
     * @param i18nMarketCategories
     */
    void updateBatchById(@Param("i18nMarketCategories") List<I18nMarketCategory> i18nMarketCategories);
}
