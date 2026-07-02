package com.panda.merge.mapper;

import com.panda.merge.model.ConfigMarketCategoryHeadLog;
import com.panda.merge.model.ConfigMarketCategoryHeadLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigMarketCategoryHeadLogMapper {
    long countByExample(ConfigMarketCategoryHeadLogExample example);

    int deleteByExample(ConfigMarketCategoryHeadLogExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ConfigMarketCategoryHeadLog record);

    int insertSelective(ConfigMarketCategoryHeadLog record);

    List<ConfigMarketCategoryHeadLog> selectByExample(ConfigMarketCategoryHeadLogExample example);

    ConfigMarketCategoryHeadLog selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ConfigMarketCategoryHeadLog record, @Param("example") ConfigMarketCategoryHeadLogExample example);

    int updateByExample(@Param("record") ConfigMarketCategoryHeadLog record, @Param("example") ConfigMarketCategoryHeadLogExample example);

    int updateByPrimaryKeySelective(ConfigMarketCategoryHeadLog record);

    int updateByPrimaryKey(ConfigMarketCategoryHeadLog record);
}