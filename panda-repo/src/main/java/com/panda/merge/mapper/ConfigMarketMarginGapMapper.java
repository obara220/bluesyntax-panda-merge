package com.panda.merge.mapper;

import com.panda.merge.model.ConfigMarketMarginGap;
import com.panda.merge.model.ConfigMarketMarginGapExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigMarketMarginGapMapper {
    long countByExample(ConfigMarketMarginGapExample example);

    int deleteByExample(ConfigMarketMarginGapExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ConfigMarketMarginGap record);

    int insertSelective(ConfigMarketMarginGap record);

    List<ConfigMarketMarginGap> selectByExample(ConfigMarketMarginGapExample example);

    ConfigMarketMarginGap selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ConfigMarketMarginGap record, @Param("example") ConfigMarketMarginGapExample example);

    int updateByExample(@Param("record") ConfigMarketMarginGap record, @Param("example") ConfigMarketMarginGapExample example);

    int updateByPrimaryKeySelective(ConfigMarketMarginGap record);

    int updateByPrimaryKey(ConfigMarketMarginGap record);
}