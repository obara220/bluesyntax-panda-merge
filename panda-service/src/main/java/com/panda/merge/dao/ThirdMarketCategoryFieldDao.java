package com.panda.merge.dao;

import com.panda.merge.dto.ThirdMarketCategoryFieldDetail;
import com.panda.merge.model.ThirdMarketCategoryField;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author : bevan
 * @project Name : panda-merge
 * @package Name : com.panda.merge.dao
 * @description : TODO
 * @date: 2020-09-11 15:55
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */
public interface ThirdMarketCategoryFieldDao {

    /**
     * 批量添加
     *
     * @param thirdMarketCategoryFieldList
     */
    void saveBatch(@Param("thirdMarketCategoryFieldList") List<ThirdMarketCategoryField> thirdMarketCategoryFieldList);

    /**
     * 批量修改
     *
     * @param thirdMarketCategoryFieldList
     */
    void updateBatchById(@Param("thirdMarketCategoryFieldList") List<ThirdMarketCategoryField> thirdMarketCategoryFieldList);

    /**
     * 根据标准玩法id 获取投注项模板信息
     * @param dataSourceCode
     * @param marketCategoryId
     * @return
     */
    List<ThirdMarketCategoryFieldDetail> queryThirdMarketCategoryFieldDetail(@Param("dataSourceCode") String dataSourceCode, @Param("marketCategoryId") Long marketCategoryId);


}
