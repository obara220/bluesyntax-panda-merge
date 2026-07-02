package com.panda.merge.mapper;

import com.panda.merge.model.ConfigSportCategoryGroup;
import com.panda.merge.model.ConfigSportCategoryGroupExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ConfigSportCategoryGroupMapper {
    long countByExample(ConfigSportCategoryGroupExample example);

    int deleteByExample(ConfigSportCategoryGroupExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ConfigSportCategoryGroup record);

    int insertSelective(ConfigSportCategoryGroup record);

    List<ConfigSportCategoryGroup> selectByExample(ConfigSportCategoryGroupExample example);

    ConfigSportCategoryGroup selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") ConfigSportCategoryGroup record, @Param("example") ConfigSportCategoryGroupExample example);

    int updateByExample(@Param("record") ConfigSportCategoryGroup record, @Param("example") ConfigSportCategoryGroupExample example);

    int updateByPrimaryKeySelective(ConfigSportCategoryGroup record);

    int updateByPrimaryKey(ConfigSportCategoryGroup record);
}