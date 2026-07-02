package com.panda.merge.mapper;

import com.panda.merge.model.ConfigMarketTradeItem;
import com.panda.merge.model.ConfigMarketTradeItemExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigMarketTradeItemMapper {
    long countByExample(ConfigMarketTradeItemExample example);

    int deleteByExample(ConfigMarketTradeItemExample example);

    int deleteByPrimaryKey(Long marketId);

    int insert(ConfigMarketTradeItem record);

    int insertSelective(ConfigMarketTradeItem record);

    List<ConfigMarketTradeItem> selectByExample(ConfigMarketTradeItemExample example);

    ConfigMarketTradeItem selectByPrimaryKey(Long marketId);

    int updateByExampleSelective(@Param("record") ConfigMarketTradeItem record, @Param("example") ConfigMarketTradeItemExample example);

    int updateByExample(@Param("record") ConfigMarketTradeItem record, @Param("example") ConfigMarketTradeItemExample example);

    int updateByPrimaryKeySelective(ConfigMarketTradeItem record);

    int updateByPrimaryKey(ConfigMarketTradeItem record);
}