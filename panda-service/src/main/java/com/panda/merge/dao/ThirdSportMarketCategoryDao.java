package com.panda.merge.dao;

import com.panda.merge.model.ThirdSportMarketCategory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author : bevan
 * @project Name : panda-merge
 * @package Name : com.panda.merge.dao
 * @description : TODO
 * @date: 2020-09-11 13:54
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */
public interface ThirdSportMarketCategoryDao {
    /**
     * 批量添加
     *
     * @param thirdSportMarketCategories
     */
    void saveBatch(@Param("thirdSportMarketCategories") List<ThirdSportMarketCategory> thirdSportMarketCategories);
}
