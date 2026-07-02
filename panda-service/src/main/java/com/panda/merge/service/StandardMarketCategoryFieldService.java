package com.panda.merge.service;

import com.panda.merge.dto.StandardMarketCategoryFieldDetail;
import com.panda.merge.model.StandardMarketCategoryField;

import java.util.List;

/**
 *  标准玩法投注项 <br>
 * @author Aison<br>
 * @createDate 2020/8/14 <br>
 */
public interface StandardMarketCategoryFieldService {

    /**
     * 根据标准玩法id查询标准玩法投注项
     * @param  marketCategoryId  标准玩法id
     * @return List<StandardMarketCategoryFieldChild>
     * */
    List<StandardMarketCategoryFieldDetail> getItems(Long marketCategoryId);

    /**
     * 根据标准玩法id列表查询标准玩法投注项
     * @param  marketCategoryIds  标准玩法id
     * @return List<StandardMarketCategoryFieldDetail>
     * */
    List<StandardMarketCategoryFieldDetail> getItems(List<Long> marketCategoryIds);

    /**
     * 列表查询标准玩法投注项
     *
     * @return List<StandardMarketCategoryFieldDetail>
     */
    List<StandardMarketCategoryField> getItemList();

    /**
     * 清理全量缓存
     * */
    int delRedisByAll();
}
