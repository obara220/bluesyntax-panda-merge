package com.panda.merge.mapper;

import com.panda.merge.model.ConfigTemplateCategoryMargin;
import com.panda.merge.model.ConfigTemplateCategoryMarginExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigTemplateCategoryMarginMapper {
    long countByExample(ConfigTemplateCategoryMarginExample example);

    int deleteByExample(ConfigTemplateCategoryMarginExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ConfigTemplateCategoryMargin record);

    int insertSelective(ConfigTemplateCategoryMargin record);

    List<ConfigTemplateCategoryMargin> selectByExample(ConfigTemplateCategoryMarginExample example);

    ConfigTemplateCategoryMargin selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ConfigTemplateCategoryMargin record, @Param("example") ConfigTemplateCategoryMarginExample example);

    int updateByExample(@Param("record") ConfigTemplateCategoryMargin record, @Param("example") ConfigTemplateCategoryMarginExample example);

    int updateByPrimaryKeySelective(ConfigTemplateCategoryMargin record);

    int updateByPrimaryKey(ConfigTemplateCategoryMargin record);
}