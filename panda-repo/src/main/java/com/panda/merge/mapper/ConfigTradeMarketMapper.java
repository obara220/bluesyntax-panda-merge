package com.panda.merge.mapper;

import com.panda.merge.model.ConfigTradeMarket;
import com.panda.merge.model.ConfigTradeMarketExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigTradeMarketMapper {
    long countByExample(ConfigTradeMarketExample example);

    int deleteByExample(ConfigTradeMarketExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ConfigTradeMarket record);

    int insertSelective(ConfigTradeMarket record);

    List<ConfigTradeMarket> selectByExample(ConfigTradeMarketExample example);

    ConfigTradeMarket selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ConfigTradeMarket record, @Param("example") ConfigTradeMarketExample example);

    int updateByExample(@Param("record") ConfigTradeMarket record, @Param("example") ConfigTradeMarketExample example);

    int updateByPrimaryKeySelective(ConfigTradeMarket record);

    int updateByPrimaryKey(ConfigTradeMarket record);
}