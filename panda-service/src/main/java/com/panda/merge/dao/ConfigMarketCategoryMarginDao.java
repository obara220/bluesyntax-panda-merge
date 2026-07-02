package com.panda.merge.dao;

import com.panda.merge.model.ConfigMarketCategoryMargin;
import com.panda.merge.model.ConfigMarketCategoryPlace;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/10/27 <br>
 * @see com.panda.merge.dao <br>
 */
public interface ConfigMarketCategoryMarginDao {
    /**
     * 批量创建
     */
    int insertList(@Param("list") List<ConfigMarketCategoryMargin> configMarketCategoryMarginList);

    /**
     * 批量修改
     */
    int updateList(@Param("list") List<ConfigMarketCategoryMargin> configMarketCategoryMarginList);

}
