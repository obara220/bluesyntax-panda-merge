package com.panda.merge.service;

import com.panda.merge.dto.TradeCategoryAutoDiffConfigItemDTO;
import com.panda.merge.model.ConfigCategoryAutoDiffTrade;

import java.util.List;

public interface ConfigCategoryAutoDiffTradeService {
    ConfigCategoryAutoDiffTrade getItem(String linkId, Long matchId, Long categoryId,Long childCategoryId);

    ConfigCategoryAutoDiffTrade create(String linkId, TradeCategoryAutoDiffConfigItemDTO tradeCategoryAutoDiffConfigItemDTO, Long matchId, Long operaterId);

    ConfigCategoryAutoDiffTrade updata(ConfigCategoryAutoDiffTrade configCategoryAutoDiffTrade);

//    void del(ConfigCategoryAutoDiffTrade configCategoryAutoDiffTrade);

    /**
     * 删除水差
     *
     * @param matchId
     */
    void delDiffByMatchInfoId(Long matchId, String linkId);

    /**
     * 根据赛事id，玩法集合清除水差
     *
     * @param linkId
     * @param matchId
     * @param categoryList
     */
    void delDiffByMatchIdAndCategoryList(String linkId, Long matchId, List<Long> categoryList);
}
