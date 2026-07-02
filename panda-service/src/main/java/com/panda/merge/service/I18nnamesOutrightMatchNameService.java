package com.panda.merge.service;

import com.panda.merge.model.I18nnamesOutrightMatchName;

import java.util.List;
import java.util.Map;

/**
 * 三方赛事，标准赛事，投注项多语言
 * @author : tell
 * @since    2020年9月16日17:37:59
 */
public interface I18nnamesOutrightMatchNameService {
    /**
     * 查询多语言
     * @param dataSourceCode
     * @param matchCategoryFiled
     * @return
     */
    Map<String, I18nnamesOutrightMatchName> getLanguageType2Item(String dataSourceCode, Long matchCategoryFiled);

    /**
     * 批量新增和修改
     * @param i18nMarketCategories
     */
    void saveOrupdateList(List<I18nnamesOutrightMatchName> i18nMarketCategories);


}
