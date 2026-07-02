package com.panda.merge.mapper;

import com.panda.merge.model.ConfigMarketTradeItemLog;
import com.panda.merge.model.ConfigMarketTradeItemLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigMarketTradeItemLogMapper {
    long countByExample(ConfigMarketTradeItemLogExample example);

    int deleteByExample(ConfigMarketTradeItemLogExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ConfigMarketTradeItemLog record);

    int insertSelective(ConfigMarketTradeItemLog record);

    List<ConfigMarketTradeItemLog> selectByExample(ConfigMarketTradeItemLogExample example);

    ConfigMarketTradeItemLog selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ConfigMarketTradeItemLog record, @Param("example") ConfigMarketTradeItemLogExample example);

    int updateByExample(@Param("record") ConfigMarketTradeItemLog record, @Param("example") ConfigMarketTradeItemLogExample example);

    int updateByPrimaryKeySelective(ConfigMarketTradeItemLog record);

    int updateByPrimaryKey(ConfigMarketTradeItemLog record);
}