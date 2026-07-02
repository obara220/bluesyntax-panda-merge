package com.panda.merge.mapper;

import com.panda.merge.model.ConfigMarketCategoryHead;
import com.panda.merge.model.ConfigMarketCategoryHeadExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigMarketCategoryHeadMapper {
    long countByExample(ConfigMarketCategoryHeadExample example);

    int deleteByExample(ConfigMarketCategoryHeadExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ConfigMarketCategoryHead record);

    int insertSelective(ConfigMarketCategoryHead record);

    List<ConfigMarketCategoryHead> selectByExample(ConfigMarketCategoryHeadExample example);

    ConfigMarketCategoryHead selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ConfigMarketCategoryHead record, @Param("example") ConfigMarketCategoryHeadExample example);

    int updateByExample(@Param("record") ConfigMarketCategoryHead record, @Param("example") ConfigMarketCategoryHeadExample example);

    int updateByPrimaryKeySelective(ConfigMarketCategoryHead record);

    int updateByPrimaryKey(ConfigMarketCategoryHead record);
}