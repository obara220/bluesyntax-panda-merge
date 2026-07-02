package com.panda.merge.dao;


import com.panda.merge.dto.StandardMarketCategoryFieldDetail;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 标准玩法投注项信息自定义dao
 * @author     tell
 * @since      2020年9月10日09:44:48
 */
@Repository
public interface StandardMarketCategoryFieldDao {

    /**
     *  根据标准玩法Id 查询运动种类对应标准玩法玩投注项数据
     * @param   marketCategoryId      标准玩法ID
     * @return  Page<StandardMarketCategoryFieldChild>
     */
    List<StandardMarketCategoryFieldDetail> getItemsByMarketCategoryId(@Param("marketCategoryId") Long marketCategoryId);

    /**
     * 根据标准玩法Id列表 查询运动种类对应标准玩法玩投注项数据
     * @param   marketCategoryIds      标准玩法ID
     * @return  Page<StandardMarketCategoryFieldChild>
     */
    List<StandardMarketCategoryFieldDetail> getItemsByMarketCategoryIds(@Param("marketCategoryIds") List<Long> marketCategoryIds);

}
