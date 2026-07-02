package com.panda.merge.mapper;

import com.panda.merge.model.ConfigTemplate;
import com.panda.merge.model.ConfigTemplateExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigTemplateMapper {
    long countByExample(ConfigTemplateExample example);

    int deleteByExample(ConfigTemplateExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ConfigTemplate record);

    int insertSelective(ConfigTemplate record);

    List<ConfigTemplate> selectByExample(ConfigTemplateExample example);

    ConfigTemplate selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ConfigTemplate record, @Param("example") ConfigTemplateExample example);

    int updateByExample(@Param("record") ConfigTemplate record, @Param("example") ConfigTemplateExample example);

    int updateByPrimaryKeySelective(ConfigTemplate record);

    int updateByPrimaryKey(ConfigTemplate record);
}