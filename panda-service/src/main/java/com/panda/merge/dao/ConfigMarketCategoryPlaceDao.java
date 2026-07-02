package com.panda.merge.dao;

import com.panda.merge.model.ConfigMarketCategoryPlace;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/24 <br>
 * @see com.panda.merge.dao <br>
 */
public interface ConfigMarketCategoryPlaceDao {
    /**
     * 批量创建
     */
    int insertList(@Param("list") List<ConfigMarketCategoryPlace> configMarketCategoryPlace);

    /**
     * 批量修改
     */
    int updateList(@Param("list") List<ConfigMarketCategoryPlace> configMarketCategoryPlace);
}
