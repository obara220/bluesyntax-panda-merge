package com.panda.merge.mapper;

import com.panda.merge.model.ConfigMarketStatusTrade;
import com.panda.merge.model.ConfigMarketStatusTradeExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigMarketStatusTradeMapper {
    long countByExample(ConfigMarketStatusTradeExample example);

    int deleteByExample(ConfigMarketStatusTradeExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ConfigMarketStatusTrade record);

    int insertSelective(ConfigMarketStatusTrade record);

    List<ConfigMarketStatusTrade> selectByExample(ConfigMarketStatusTradeExample example);

    ConfigMarketStatusTrade selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ConfigMarketStatusTrade record, @Param("example") ConfigMarketStatusTradeExample example);

    int updateByExample(@Param("record") ConfigMarketStatusTrade record, @Param("example") ConfigMarketStatusTradeExample example);

    int updateByPrimaryKeySelective(ConfigMarketStatusTrade record);

    int updateByPrimaryKey(ConfigMarketStatusTrade record);
}