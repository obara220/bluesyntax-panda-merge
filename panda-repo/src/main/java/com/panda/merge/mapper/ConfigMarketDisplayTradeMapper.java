package com.panda.merge.mapper;

import com.panda.merge.model.ConfigMarketDisplayTrade;
import com.panda.merge.model.ConfigMarketDisplayTradeExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigMarketDisplayTradeMapper {
    long countByExample(ConfigMarketDisplayTradeExample example);

    int deleteByExample(ConfigMarketDisplayTradeExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ConfigMarketDisplayTrade record);

    int insertSelective(ConfigMarketDisplayTrade record);

    List<ConfigMarketDisplayTrade> selectByExample(ConfigMarketDisplayTradeExample example);

    ConfigMarketDisplayTrade selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ConfigMarketDisplayTrade record, @Param("example") ConfigMarketDisplayTradeExample example);

    int updateByExample(@Param("record") ConfigMarketDisplayTrade record, @Param("example") ConfigMarketDisplayTradeExample example);

    int updateByPrimaryKeySelective(ConfigMarketDisplayTrade record);

    int updateByPrimaryKey(ConfigMarketDisplayTrade record);
}