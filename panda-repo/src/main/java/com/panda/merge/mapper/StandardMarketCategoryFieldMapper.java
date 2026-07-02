package com.panda.merge.mapper;

import com.panda.merge.model.StandardMarketCategoryField;
import com.panda.merge.model.StandardMarketCategoryFieldExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StandardMarketCategoryFieldMapper {
    long countByExample(StandardMarketCategoryFieldExample example);

    int deleteByExample(StandardMarketCategoryFieldExample example);

    int deleteByPrimaryKey(Long id);

    int insert(StandardMarketCategoryField record);

    int insertSelective(StandardMarketCategoryField record);

    List<StandardMarketCategoryField> selectByExample(StandardMarketCategoryFieldExample example);

    StandardMarketCategoryField selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") StandardMarketCategoryField record, @Param("example") StandardMarketCategoryFieldExample example);

    int updateByExample(@Param("record") StandardMarketCategoryField record, @Param("example") StandardMarketCategoryFieldExample example);

    int updateByPrimaryKeySelective(StandardMarketCategoryField record);

    int updateByPrimaryKey(StandardMarketCategoryField record);
}