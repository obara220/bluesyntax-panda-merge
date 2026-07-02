package com.panda.merge.mapper;

import com.panda.merge.model.I18nMarketCategory;
import com.panda.merge.model.I18nMarketCategoryExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface I18nMarketCategoryMapper {
    long countByExample(I18nMarketCategoryExample example);

    int deleteByExample(I18nMarketCategoryExample example);

    int deleteByPrimaryKey(Long id);

    int insert(I18nMarketCategory record);

    int insertSelective(I18nMarketCategory record);

    List<I18nMarketCategory> selectByExample(I18nMarketCategoryExample example);

    I18nMarketCategory selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") I18nMarketCategory record, @Param("example") I18nMarketCategoryExample example);

    int updateByExample(@Param("record") I18nMarketCategory record, @Param("example") I18nMarketCategoryExample example);

    int updateByPrimaryKeySelective(I18nMarketCategory record);

    int updateByPrimaryKey(I18nMarketCategory record);
}