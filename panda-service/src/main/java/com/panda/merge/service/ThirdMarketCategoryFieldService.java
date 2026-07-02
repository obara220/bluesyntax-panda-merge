package com.panda.merge.service;

import com.panda.merge.dto.ThirdMarketCategoryFieldDetail;
import com.panda.merge.model.ThirdMarketCategoryField;

import java.util.List;
import java.util.Map;
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
public interface ThirdMarketCategoryFieldService {

    ThirdMarketCategoryField getItem(String dataSourceCode, String thirdTemplateSourceId, Long thirdMarketCategoryId);

    ThirdMarketCategoryField getItem(Long id, String thirdTemplateSourceId);

    /**
     * 根据三方玩法投注项id查询 三方玩法投注项表
     *
     * @param thirdTempletSourceIdSet 三方玩法投注项id
     * @return
     */
    List<ThirdMarketCategoryField> queryThirdSportOddsFieldsList(Set<String> thirdTempletSourceIdSet);
    /**
     * 根据三方玩法投注项id查询 三方玩法投注项表
     *
     * @param thirdTempletSourceIdSet 三方玩法投注项id
     * @return
     */
    List<ThirdMarketCategoryField> queryThirdSportOddsFieldsLists(Set<String> thirdTempletSourceIdSet);

    List<ThirdMarketCategoryField> queryFieldsByDataSourceAndMarketCategoryIds(Set<String> dataSourceAndMarketCategoryIds);

    /**
     * 批量添加
     *
     * @param thirdMarketCategoryFieldList
     */
    void saveBatch(List<ThirdMarketCategoryField> thirdMarketCategoryFieldList);

    /**
     * 批量修改
     *
     * @param thirdMarketCategoryFieldList
     */
    void updateBatchById(List<ThirdMarketCategoryField> thirdMarketCategoryFieldList);

    /**
     * 根据数据源查询 三方玩法投注项表
     *
     * @param dataSourceCode
     * @return
     */
    List<ThirdMarketCategoryField> queryThirdMarketCategoryField(String dataSourceCode);

    /**
     * 根据标准玩法id 获取投注项模板信息
     * @param dataSourceCode
     * @param marketCategoryId
     * @return
     */
    List<ThirdMarketCategoryFieldDetail> queryThirdMarketCategoryFieldDetail(String dataSourceCode, Long marketCategoryId);

    /**
     * 清理全量缓存
     * */
    int delRedisByAll();
}
