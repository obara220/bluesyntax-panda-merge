package com.panda.merge.mapper;

import com.panda.merge.model.ThirdMatchPromotionChart;
import com.panda.merge.model.ThirdMatchPromotionChartExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ThirdMatchPromotionChartMapper {
    long countByExample(ThirdMatchPromotionChartExample example);

    int deleteByExample(ThirdMatchPromotionChartExample example);

    int deleteByPrimaryKey(String id);

    int insert(ThirdMatchPromotionChart record);

    int insertSelective(ThirdMatchPromotionChart record);

    List<ThirdMatchPromotionChart> selectByExample(ThirdMatchPromotionChartExample example);

    ThirdMatchPromotionChart selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") ThirdMatchPromotionChart record, @Param("example") ThirdMatchPromotionChartExample example);

    int updateByExample(@Param("record") ThirdMatchPromotionChart record, @Param("example") ThirdMatchPromotionChartExample example);

    int updateByPrimaryKeySelective(ThirdMatchPromotionChart record);

    int updateByPrimaryKey(ThirdMatchPromotionChart record);
}