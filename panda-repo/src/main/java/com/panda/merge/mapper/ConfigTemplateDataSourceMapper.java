package com.panda.merge.mapper;

import com.panda.merge.model.ConfigTemplateDataSource;
import com.panda.merge.model.ConfigTemplateDataSourceExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigTemplateDataSourceMapper {
    long countByExample(ConfigTemplateDataSourceExample example);

    int deleteByExample(ConfigTemplateDataSourceExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ConfigTemplateDataSource record);

    int insertSelective(ConfigTemplateDataSource record);

    List<ConfigTemplateDataSource> selectByExample(ConfigTemplateDataSourceExample example);

    ConfigTemplateDataSource selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ConfigTemplateDataSource record, @Param("example") ConfigTemplateDataSourceExample example);

    int updateByExample(@Param("record") ConfigTemplateDataSource record, @Param("example") ConfigTemplateDataSourceExample example);

    int updateByPrimaryKeySelective(ConfigTemplateDataSource record);

    int updateByPrimaryKey(ConfigTemplateDataSource record);
}