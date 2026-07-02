package com.panda.merge.mapper;

import com.panda.merge.model.ConfigurationMatchDataSource;
import com.panda.merge.model.ConfigurationMatchDataSourceExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigurationMatchDataSourceMapper {
    long countByExample(ConfigurationMatchDataSourceExample example);

    int deleteByExample(ConfigurationMatchDataSourceExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ConfigurationMatchDataSource record);

    int insertSelective(ConfigurationMatchDataSource record);

    List<ConfigurationMatchDataSource> selectByExample(ConfigurationMatchDataSourceExample example);

    ConfigurationMatchDataSource selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ConfigurationMatchDataSource record, @Param("example") ConfigurationMatchDataSourceExample example);

    int updateByExample(@Param("record") ConfigurationMatchDataSource record, @Param("example") ConfigurationMatchDataSourceExample example);

    int updateByPrimaryKeySelective(ConfigurationMatchDataSource record);

    int updateByPrimaryKey(ConfigurationMatchDataSource record);
}