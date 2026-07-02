package com.panda.merge.mapper;

import com.panda.merge.model.ConfigMarketAutoDiffTrade;
import com.panda.merge.model.ConfigMarketAutoDiffTradeExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigMarketAutoDiffTradeMapper {
    long countByExample(ConfigMarketAutoDiffTradeExample example);

    int deleteByExample(ConfigMarketAutoDiffTradeExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ConfigMarketAutoDiffTrade record);

    int insertSelective(ConfigMarketAutoDiffTrade record);

    List<ConfigMarketAutoDiffTrade> selectByExample(ConfigMarketAutoDiffTradeExample example);

    ConfigMarketAutoDiffTrade selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ConfigMarketAutoDiffTrade record, @Param("example") ConfigMarketAutoDiffTradeExample example);

    int updateByExample(@Param("record") ConfigMarketAutoDiffTrade record, @Param("example") ConfigMarketAutoDiffTradeExample example);

    int updateByPrimaryKeySelective(ConfigMarketAutoDiffTrade record);

    int updateByPrimaryKey(ConfigMarketAutoDiffTrade record);
}