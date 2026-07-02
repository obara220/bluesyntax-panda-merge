package com.panda.merge.dao;

import com.panda.merge.model.I18nOutrightMarketOdds;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author : raulvii
 * @project Name : panda-merge
 * @package Name : com.panda.merge.dao
 * @date: 2020-10-09
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */
@Repository
public interface I18nOutrightMarketOddsDao {
    /**
     * 批量添加
     *
     * @param i18nOutrightMarketOddsList
     */
    void saveBatch(@Param("i18nOutrightMarketOddsList") List<I18nOutrightMarketOdds> i18nOutrightMarketOddsList);



}
