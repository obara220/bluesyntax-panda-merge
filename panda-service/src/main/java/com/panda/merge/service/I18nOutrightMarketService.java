package com.panda.merge.service;

import com.panda.merge.model.I18nOutrightMarket;

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
public interface I18nOutrightMarketService {

    /**
     * 批量添加
     *
     * @param i18nOutrightMarketList
     */
    void saveBatch(List<I18nOutrightMarket> i18nOutrightMarketList);

    /**
     * 批量修改
     *
     * @param i18nOutrightMarketList
     */
    void updateBatchById(List<I18nOutrightMarket> i18nOutrightMarketList);

    List<I18nOutrightMarket> selectI18nOutrightMarketList(String dataSourceCode, List<Long> nameCodeList);

    List<I18nOutrightMarket> selectI18nOutrightMarketList(String dataSourceCode, Long nameCode);
}
