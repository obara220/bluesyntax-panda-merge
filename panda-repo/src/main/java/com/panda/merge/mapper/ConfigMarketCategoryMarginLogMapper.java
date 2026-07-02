package com.panda.merge.mapper;

import com.panda.merge.model.ConfigMarketCategoryMarginLog;
import com.panda.merge.model.ConfigMarketCategoryMarginLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigMarketCategoryMarginLogMapper {
    long countByExample(ConfigMarketCategoryMarginLogExample example);

    int deleteByExample(ConfigMarketCategoryMarginLogExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ConfigMarketCategoryMarginLog record);

    int insertSelective(ConfigMarketCategoryMarginLog record);

    List<ConfigMarketCategoryMarginLog> selectByExample(ConfigMarketCategoryMarginLogExample example);

    ConfigMarketCategoryMarginLog selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ConfigMarketCategoryMarginLog record, @Param("example") ConfigMarketCategoryMarginLogExample example);

    int updateByExample(@Param("record") ConfigMarketCategoryMarginLog record, @Param("example") ConfigMarketCategoryMarginLogExample example);

    int updateByPrimaryKeySelective(ConfigMarketCategoryMarginLog record);

    int updateByPrimaryKey(ConfigMarketCategoryMarginLog record);
}