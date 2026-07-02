package com.panda.merge.service;

import com.panda.merge.model.I18nMarketCategory;
import com.panda.merge.model.I18nMarketOdds;

import java.util.List;
import java.util.Map;

/**
 * @author : raulvii
 * @project Name : panda-merge
 * @package Name : com.panda.merge.service
 * @description : TODO
 * @date: 2020-10-09
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */
public interface I18nMarketOddsService {

    /**
     * 批量添加
     *
     * @param i18nMarketOddsList
     */
    void saveBatch(List<I18nMarketOdds> i18nMarketOddsList);

    /**
     * 批量修改
     *
     * @param i18nMarketOddsList
     */
    void updateBatchById(List<I18nMarketOdds> i18nMarketOddsList);
}
