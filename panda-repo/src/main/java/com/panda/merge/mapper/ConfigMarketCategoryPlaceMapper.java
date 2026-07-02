package com.panda.merge.mapper;

import com.panda.merge.model.ConfigMarketCategoryPlace;
import com.panda.merge.model.ConfigMarketCategoryPlaceExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigMarketCategoryPlaceMapper {
    long countByExample(ConfigMarketCategoryPlaceExample example);

    int deleteByExample(ConfigMarketCategoryPlaceExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ConfigMarketCategoryPlace record);

    int insertSelective(ConfigMarketCategoryPlace record);

    List<ConfigMarketCategoryPlace> selectByExample(ConfigMarketCategoryPlaceExample example);

    ConfigMarketCategoryPlace selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ConfigMarketCategoryPlace record, @Param("example") ConfigMarketCategoryPlaceExample example);

    int updateByExample(@Param("record") ConfigMarketCategoryPlace record, @Param("example") ConfigMarketCategoryPlaceExample example);

    int updateByPrimaryKeySelective(ConfigMarketCategoryPlace record);

    int updateByPrimaryKey(ConfigMarketCategoryPlace record);
}