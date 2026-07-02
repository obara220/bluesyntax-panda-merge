package com.panda.merge.mapper;

import com.panda.merge.model.ConfigMarketOddsStatus;
import com.panda.merge.model.ConfigMarketOddsStatusExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ConfigMarketOddsStatusMapper {
    long countByExample(ConfigMarketOddsStatusExample example);

    int deleteByExample(ConfigMarketOddsStatusExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ConfigMarketOddsStatus record);

    int insertSelective(ConfigMarketOddsStatus record);

    List<ConfigMarketOddsStatus> selectByExample(ConfigMarketOddsStatusExample example);

    ConfigMarketOddsStatus selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ConfigMarketOddsStatus record, @Param("example") ConfigMarketOddsStatusExample example);

    int updateByExample(@Param("record") ConfigMarketOddsStatus record, @Param("example") ConfigMarketOddsStatusExample example);

    int updateByPrimaryKeySelective(ConfigMarketOddsStatus record);

    int updateByPrimaryKey(ConfigMarketOddsStatus record);
}