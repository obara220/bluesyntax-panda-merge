package com.panda.merge.service;

import com.panda.merge.model.ConfigMarketCategoryHead;

import java.util.List;

/**
 * @author :  myname
 * @Project Name :  panda-merge
 * @Package Name :  com.panda.merge.service
 * @Description :  TODO
 * @Date: 2020-10-03 11:33
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface ConfigMarketHeadGapService {

    ConfigMarketCategoryHead getItem(String linkId, Long standardMatchInfoId, Long standardCategoryId, Long childStandardCategoryId);

    ConfigMarketCategoryHead create(ConfigMarketCategoryHead configMarketCategoryHead);

    ConfigMarketCategoryHead update(ConfigMarketCategoryHead configMarketCategoryHead);

    void del(ConfigMarketCategoryHead configMarketCategoryHead);

    /**
     * 删除盘口差
     *
     * @param matchId
     */
    void delHeadByMatchInfoId(Long matchId, String linkId);

    void delHeadByMatchIdAndCategoryList(Long matchId, String linkId, List<Long> list);


    ConfigMarketCategoryHead getItemCache(String linkId, Long standardMatchInfoId, Long standardCategoryId, Long childStandardCategoryId);

    void saveOrUpdateCache(String linkId, ConfigMarketCategoryHead head);

    void delCacheByStandardMatchInfoId(Long matchId, String linkId);

    void delCacheByCategoryIdList(String linkId, Long matchId, List<Long> list);
}
