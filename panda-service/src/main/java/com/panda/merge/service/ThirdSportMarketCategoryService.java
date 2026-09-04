package com.panda.merge.service;

import com.panda.merge.model.ThirdMarketCategory;
import com.panda.merge.model.ThirdSportMarketCategory;

import java.util.List;
import java.util.Set;

/**
 * @author : bevan
 * @project Name : panda-merge
 * @package Name : com.panda.merge.service
 * @date: 2020-09-11 9:38
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */

public interface ThirdSportMarketCategoryService {

    /**
     * @param categoryIdSet 根据三方玩法主键id查询赛种玩法表
     * @return
     */
    List<ThirdSportMarketCategory> queryThirdSportMarketCategoryList(Set<Long> categoryIdSet);

    /**
     * 批量添加
     *
     * @param thirdSportMarketCategories
     */
    void saveBatch(List<ThirdSportMarketCategory> thirdSportMarketCategories);

    /**
     * 清理全量缓存
     * */
    int delRedisByAll();

    /**
     * 根据球种和标准玩法id查询对应的所有三方玩法信息
     * @param referenceIds
     * @param sportIds
     * @return
     */
    List<ThirdMarketCategory> queryThirdMarketCategoryList(List<Long> referenceIds, List<Long> sportIds);

}
