package com.panda.merge.mapper;

import com.panda.merge.model.MarketCategoryEventCodeRelation;
import com.panda.merge.model.MarketCategoryEventCodeRelationExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MarketCategoryEventCodeRelationMapper {
    long countByExample(MarketCategoryEventCodeRelationExample example);

    int deleteByExample(MarketCategoryEventCodeRelationExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MarketCategoryEventCodeRelation record);

    int insertSelective(MarketCategoryEventCodeRelation record);

    List<MarketCategoryEventCodeRelation> selectByExample(MarketCategoryEventCodeRelationExample example);

    MarketCategoryEventCodeRelation selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MarketCategoryEventCodeRelation record, @Param("example") MarketCategoryEventCodeRelationExample example);

    int updateByExample(@Param("record") MarketCategoryEventCodeRelation record, @Param("example") MarketCategoryEventCodeRelationExample example);

    int updateByPrimaryKeySelective(MarketCategoryEventCodeRelation record);

    int updateByPrimaryKey(MarketCategoryEventCodeRelation record);
}