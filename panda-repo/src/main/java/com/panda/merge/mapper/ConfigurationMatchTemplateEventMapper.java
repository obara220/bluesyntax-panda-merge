package com.panda.merge.mapper;

import com.panda.merge.model.ConfigurationMatchTemplateEvent;
import com.panda.merge.model.ConfigurationMatchTemplateEventExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigurationMatchTemplateEventMapper {
    long countByExample(ConfigurationMatchTemplateEventExample example);

    int deleteByExample(ConfigurationMatchTemplateEventExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ConfigurationMatchTemplateEvent record);

    int insertSelective(ConfigurationMatchTemplateEvent record);

    List<ConfigurationMatchTemplateEvent> selectByExample(ConfigurationMatchTemplateEventExample example);

    ConfigurationMatchTemplateEvent selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ConfigurationMatchTemplateEvent record, @Param("example") ConfigurationMatchTemplateEventExample example);

    int updateByExample(@Param("record") ConfigurationMatchTemplateEvent record, @Param("example") ConfigurationMatchTemplateEventExample example);

    int updateByPrimaryKeySelective(ConfigurationMatchTemplateEvent record);

    int updateByPrimaryKey(ConfigurationMatchTemplateEvent record);
}