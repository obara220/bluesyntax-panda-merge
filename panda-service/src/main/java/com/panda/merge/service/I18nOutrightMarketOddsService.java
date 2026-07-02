package com.panda.merge.service;

import com.panda.merge.model.I18nOutrightMarket;
import com.panda.merge.model.I18nOutrightMarketOdds;
import org.apache.commons.lang3.tuple.Pair;

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
public interface I18nOutrightMarketOddsService {

    /**
     * 批量添加
     *
     * @param i18nOutrightMarketOddsList
     */
    void saveBatch(List<I18nOutrightMarketOdds> i18nOutrightMarketOddsList);

    /**
     *
     * @param dataSourceCode
     * @param nameCode
     * @return
     */
    List<I18nOutrightMarketOdds> selectI18nOutRightMarketOddsList(String dataSourceCode, Long nameCode);

    List<I18nOutrightMarketOdds> selectI18nOutRightMarketOddsList(List<Pair<String, Long>> i18nPairs);


    /**
     * 批量获取冠军投注项多语言
     */
    List<I18nOutrightMarketOdds> getListOutrightMarketOdds(List<Long> nameCodes, String dataSourceCode);


    /**
     * 批量修改投注项对应的多语言
     * @param records
     */
    void updateBatchByPrimaryKeys(List<I18nOutrightMarketOdds> records);
    

}
