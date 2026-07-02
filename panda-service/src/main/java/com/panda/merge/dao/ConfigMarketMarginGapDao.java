package com.panda.merge.dao;

import com.panda.merge.model.ConfigMarketCategoryMargin;
import com.panda.merge.model.ConfigMarketMarginGap;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ConfigMarketMarginGapDao {

    /**
     * 批量创建
     */
    int insertList(@Param("list") List<ConfigMarketMarginGap> list);

    /**
     * 批量创建
     */
    int updateList(@Param("list") List<ConfigMarketMarginGap> list);
}
