package com.panda.merge.mapper;

import com.panda.merge.model.ConfigOutrightTradeMarket;
import com.panda.merge.model.ConfigOutrightTradeMarketExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigOutrightTradeMarketMapper {
    long countByExample(ConfigOutrightTradeMarketExample example);

    int deleteByExample(ConfigOutrightTradeMarketExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ConfigOutrightTradeMarket record);

    int insertSelective(ConfigOutrightTradeMarket record);

    List<ConfigOutrightTradeMarket> selectByExample(ConfigOutrightTradeMarketExample example);

    ConfigOutrightTradeMarket selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ConfigOutrightTradeMarket record, @Param("example") ConfigOutrightTradeMarketExample example);

    int updateByExample(@Param("record") ConfigOutrightTradeMarket record, @Param("example") ConfigOutrightTradeMarketExample example);

    int updateByPrimaryKeySelective(ConfigOutrightTradeMarket record);

    int updateByPrimaryKey(ConfigOutrightTradeMarket record);
}