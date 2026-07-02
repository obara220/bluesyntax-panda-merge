package com.panda.merge.mapper;

import com.panda.merge.model.ConfigTradeMarketLog;
import com.panda.merge.model.ConfigTradeMarketLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigTradeMarketLogMapper {
    long countByExample(ConfigTradeMarketLogExample example);

    int deleteByExample(ConfigTradeMarketLogExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ConfigTradeMarketLog record);

    int insertSelective(ConfigTradeMarketLog record);

    List<ConfigTradeMarketLog> selectByExample(ConfigTradeMarketLogExample example);

    ConfigTradeMarketLog selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ConfigTradeMarketLog record, @Param("example") ConfigTradeMarketLogExample example);

    int updateByExample(@Param("record") ConfigTradeMarketLog record, @Param("example") ConfigTradeMarketLogExample example);

    int updateByPrimaryKeySelective(ConfigTradeMarketLog record);

    int updateByPrimaryKey(ConfigTradeMarketLog record);
}