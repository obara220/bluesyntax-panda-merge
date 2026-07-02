package com.panda.merge.mapper;

import com.panda.merge.model.ConfigMarketDisplayTradeLog;
import com.panda.merge.model.ConfigMarketDisplayTradeLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigMarketDisplayTradeLogMapper {
    long countByExample(ConfigMarketDisplayTradeLogExample example);

    int deleteByExample(ConfigMarketDisplayTradeLogExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ConfigMarketDisplayTradeLog record);

    int insertSelective(ConfigMarketDisplayTradeLog record);

    List<ConfigMarketDisplayTradeLog> selectByExample(ConfigMarketDisplayTradeLogExample example);

    ConfigMarketDisplayTradeLog selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ConfigMarketDisplayTradeLog record, @Param("example") ConfigMarketDisplayTradeLogExample example);

    int updateByExample(@Param("record") ConfigMarketDisplayTradeLog record, @Param("example") ConfigMarketDisplayTradeLogExample example);

    int updateByPrimaryKeySelective(ConfigMarketDisplayTradeLog record);

    int updateByPrimaryKey(ConfigMarketDisplayTradeLog record);
}