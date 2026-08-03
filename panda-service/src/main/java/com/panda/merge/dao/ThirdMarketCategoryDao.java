package com.panda.merge.dao;

import com.panda.merge.bo.ThirdSportMarketCategoryBO;
import com.panda.merge.dto.ThirdCategoryDTO;
import com.panda.merge.dto.ThirdMarketCategoryDTO;
import com.panda.merge.model.ThirdMarketCategory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author : bevan
 * @project Name : panda-merge
 * @package Name : com.panda.merge.dao
 * @description : TODO
 * @date: 2020-09-11 10:21
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */
public interface ThirdMarketCategoryDao {
    /**
     * 批量修改
     *
     * @param categoryList
     */
    void updateBatchById(@Param("categoryList") List<ThirdMarketCategory> categoryList);

    /**
     * 查询三方玩法列表
     * @param dto
     * @return
     */
    List<ThirdSportMarketCategoryBO> queryThirdMarketCategory(@Param("dto") ThirdCategoryDTO dto);
}