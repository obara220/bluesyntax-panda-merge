package com.panda.merge.mapper;

import com.panda.merge.model.ConfigTemplateCategory;
import com.panda.merge.model.ConfigTemplateCategoryExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigTemplateCategoryMapper {
    long countByExample(ConfigTemplateCategoryExample example);

    int deleteByExample(ConfigTemplateCategoryExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ConfigTemplateCategory record);

    int insertSelective(ConfigTemplateCategory record);

    List<ConfigTemplateCategory> selectByExample(ConfigTemplateCategoryExample example);

    ConfigTemplateCategory selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ConfigTemplateCategory record, @Param("example") ConfigTemplateCategoryExample example);

    int updateByExample(@Param("record") ConfigTemplateCategory record, @Param("example") ConfigTemplateCategoryExample example);

    int updateByPrimaryKeySelective(ConfigTemplateCategory record);

    int updateByPrimaryKey(ConfigTemplateCategory record);
}