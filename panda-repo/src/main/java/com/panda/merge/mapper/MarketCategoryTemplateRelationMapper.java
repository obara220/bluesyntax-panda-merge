package com.panda.merge.mapper;

import com.panda.merge.model.MarketCategoryTemplateRelation;
import com.panda.merge.model.MarketCategoryTemplateRelationExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MarketCategoryTemplateRelationMapper {
    long countByExample(MarketCategoryTemplateRelationExample example);

    int deleteByExample(MarketCategoryTemplateRelationExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MarketCategoryTemplateRelation record);

    int insertSelective(MarketCategoryTemplateRelation record);

    List<MarketCategoryTemplateRelation> selectByExample(MarketCategoryTemplateRelationExample example);

    MarketCategoryTemplateRelation selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MarketCategoryTemplateRelation record, @Param("example") MarketCategoryTemplateRelationExample example);

    int updateByExample(@Param("record") MarketCategoryTemplateRelation record, @Param("example") MarketCategoryTemplateRelationExample example);

    int updateByPrimaryKeySelective(MarketCategoryTemplateRelation record);

    int updateByPrimaryKey(MarketCategoryTemplateRelation record);
}