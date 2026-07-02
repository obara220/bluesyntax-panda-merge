package com.panda.merge.mapper;

import com.panda.merge.model.ThirdMarketCategoryField;
import com.panda.merge.model.ThirdMarketCategoryFieldExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ThirdMarketCategoryFieldMapper {
    long countByExample(ThirdMarketCategoryFieldExample example);

    int deleteByExample(ThirdMarketCategoryFieldExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ThirdMarketCategoryField record);

    int insertSelective(ThirdMarketCategoryField record);

    List<ThirdMarketCategoryField> selectByExample(ThirdMarketCategoryFieldExample example);

    ThirdMarketCategoryField selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ThirdMarketCategoryField record, @Param("example") ThirdMarketCategoryFieldExample example);

    int updateByExample(@Param("record") ThirdMarketCategoryField record, @Param("example") ThirdMarketCategoryFieldExample example);

    int updateByPrimaryKeySelective(ThirdMarketCategoryField record);

    int updateByPrimaryKey(ThirdMarketCategoryField record);
}