package com.panda.merge.dao;


import com.panda.merge.dto.StandardSportMarketCategoryDetail;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 运动种类对应标准玩法玩数据自定义dao
 * @author     tell
 * @since      2020年9月10日09:44:48
 */
@Repository
public interface StandardSportMarketCategoryDao {

    /**
     * 根据标准玩法Id 查询运动种类对应标准玩法玩数据
     * @param   marketCategoryId  标准玩法Id列表
     * @return  List<StandardSportMarketCategoryChild>
     */
    List<StandardSportMarketCategoryDetail> getItemsByMarketCategoryId(@Param("marketCategoryId") Long marketCategoryId);

    /**
     * 根据标准玩法Id列表 查询运动种类对应标准玩法玩数据
     * @param   marketCategoryIds  标准玩法Id列表
     * @return  List<StandardSportMarketCategoryChild>
     */
    List<StandardSportMarketCategoryDetail> getItemsByMarketCategoryIds(@Param("marketCategoryIds") List<Long> marketCategoryIds);

}
