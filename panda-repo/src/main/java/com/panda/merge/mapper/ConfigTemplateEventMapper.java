package com.panda.merge.mapper;

import com.panda.merge.model.ConfigTemplateEvent;
import com.panda.merge.model.ConfigTemplateEventExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigTemplateEventMapper {
    long countByExample(ConfigTemplateEventExample example);

    int deleteByExample(ConfigTemplateEventExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ConfigTemplateEvent record);

    int insertSelective(ConfigTemplateEvent record);

    List<ConfigTemplateEvent> selectByExample(ConfigTemplateEventExample example);

    ConfigTemplateEvent selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ConfigTemplateEvent record, @Param("example") ConfigTemplateEventExample example);

    int updateByExample(@Param("record") ConfigTemplateEvent record, @Param("example") ConfigTemplateEventExample example);

    int updateByPrimaryKeySelective(ConfigTemplateEvent record);

    int updateByPrimaryKey(ConfigTemplateEvent record);
}