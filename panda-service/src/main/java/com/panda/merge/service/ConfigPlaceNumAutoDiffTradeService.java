package com.panda.merge.service;

import com.panda.merge.dto.TradePlaceNumAutoDiffConfigItemDTO;
import com.panda.merge.model.ConfigPlacenumAutoDiffTrade;

import java.util.List;

public interface ConfigPlaceNumAutoDiffTradeService {

    ConfigPlacenumAutoDiffTrade getItem(String linkId,Long matchId,Long categoryId,Long childCategoryId, Integer placeNum);

//    ConfigPlacenumAutoDiffTrade getItem(String linkId,Long matchId,Long categoryId,Long childCategoryId);

    ConfigPlacenumAutoDiffTrade create(String linkId, TradePlaceNumAutoDiffConfigItemDTO tradePlaceNumAutoDiffConfigItemDTO, Long matchId, Long operaterId);

    ConfigPlacenumAutoDiffTrade updata(ConfigPlacenumAutoDiffTrade configPlacenumAutoDiffTrade);

//    void del(ConfigPlacenumAutoDiffTrade configPlacenumAutoDiffTrade);

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
