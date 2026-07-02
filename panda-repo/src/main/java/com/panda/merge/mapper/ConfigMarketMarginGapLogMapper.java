package com.panda.merge.mapper;

import com.panda.merge.model.ConfigMarketMarginGapLog;
import com.panda.merge.model.ConfigMarketMarginGapLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigMarketMarginGapLogMapper {
    long countByExample(ConfigMarketMarginGapLogExample example);

    int deleteByExample(ConfigMarketMarginGapLogExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ConfigMarketMarginGapLog record);

    int insertSelective(ConfigMarketMarginGapLog record);

    List<ConfigMarketMarginGapLog> selectByExample(ConfigMarketMarginGapLogExample example);

    ConfigMarketMarginGapLog selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ConfigMarketMarginGapLog record, @Param("example") ConfigMarketMarginGapLogExample example);

    int updateByExample(@Param("record") ConfigMarketMarginGapLog record, @Param("example") ConfigMarketMarginGapLogExample example);

    int updateByPrimaryKeySelective(ConfigMarketMarginGapLog record);

    int updateByPrimaryKey(ConfigMarketMarginGapLog record);
}