package com.panda.merge.mapper;

import com.panda.merge.model.ConfigMarketAutoDiffTradeLog;
import com.panda.merge.model.ConfigMarketAutoDiffTradeLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigMarketAutoDiffTradeLogMapper {
    long countByExample(ConfigMarketAutoDiffTradeLogExample example);

    int deleteByExample(ConfigMarketAutoDiffTradeLogExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ConfigMarketAutoDiffTradeLog record);

    int insertSelective(ConfigMarketAutoDiffTradeLog record);

    List<ConfigMarketAutoDiffTradeLog> selectByExample(ConfigMarketAutoDiffTradeLogExample example);

    ConfigMarketAutoDiffTradeLog selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ConfigMarketAutoDiffTradeLog record, @Param("example") ConfigMarketAutoDiffTradeLogExample example);

    int updateByExample(@Param("record") ConfigMarketAutoDiffTradeLog record, @Param("example") ConfigMarketAutoDiffTradeLogExample example);

    int updateByPrimaryKeySelective(ConfigMarketAutoDiffTradeLog record);

    int updateByPrimaryKey(ConfigMarketAutoDiffTradeLog record);
}