package com.panda.merge.service;

import com.panda.merge.model.ThirdMarketCategory;

import java.util.List;
import java.util.Set;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/14 <br>
 * @see com.panda.merge.service <br>
 */
public interface ThirdMarketCategoryService {

    ThirdMarketCategory getItem(String dataSourceCode, String thirdMarketCategorySourceId);

    List<ThirdMarketCategory> getItems(List<String> dataSourceCategory);

    List<ThirdMarketCategory> getItem(String dataSourceCode, Long marketCategoryId);

    List<ThirdMarketCategory> getItemsByDataSourceAndReferenceIds(List<String> dataSourceReferences);

    /**
     * 查询三方玩法表
     *
     * @param dataSourceCodeSet 数据源
     * @param thirdSourceIdSet  三方玩法原始id
     * @return
     */
    List<ThirdMarketCategory> queryThirdMarketCategoryList(Set<String> dataSourceCodeSet, Set<String> thirdSourceIdSet);

    /**
     * 根据三方玩法原始id查询三方玩法表
     *
     * @param thirdMarketCategorySourceIdSet 三方玩法原始id
     * @return
     */
    List<ThirdMarketCategory> queryByThirdMarketCategorySourceIdSet(Set<String> thirdMarketCategorySourceIdSet);

    /**
     * 添加
     *
     * @param thirdMarketCategory
     * @return
     */
    ThirdMarketCategory create(ThirdMarketCategory thirdMarketCategory);

    /**
     * 批量新增
     * @param categoryList
     */
    void saveBatch(List<ThirdMarketCategory> categoryList);

    /**
     * 批量修改
     * @param categoryList
     */
    void updateBatchById(List<ThirdMarketCategory> categoryList);

    /**
     * 清理全量缓存
     * */
    int delRedisByAll();
}
