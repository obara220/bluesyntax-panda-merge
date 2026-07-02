package com.panda.merge.service;

import com.panda.merge.model.I18nMarketCategory;

import java.util.List;
import java.util.Map;

/**
 * @author : bevan
 * @project Name : panda-merge
 * @package Name : com.panda.merge.service
 * @description : TODO
 * @date: 2020-09-11 9:52
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */
public interface I18nMarketCategoryService {
    /**
     * 查询多语言
     *
     * @param dataSourceCode
     * @param nameCode
     * @return
     */
    Map<String, I18nMarketCategory> queryLanguageInternation(String dataSourceCode, Long nameCode);

    /**
     * 根据namecode列表查询多语言
     * @param nameCodes
     * @return
     */
    Map<Long,List<I18nMarketCategory>> getItemsByNameCodes(List<Long> nameCodes);

    List<I18nMarketCategory> getItemsByDatasourceLangNameCode(List<String> datasourceLangAndNameCode);

    /**
     * 批量添加
     *
     * @param i18nMarketCategories
     */
    void saveBatch(List<I18nMarketCategory> i18nMarketCategories);

    /**
     * 批量修改
     *
     * @param i18nMarketCategories
     */
    void updateBatchById(List<I18nMarketCategory> i18nMarketCategories);
}
