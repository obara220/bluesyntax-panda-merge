package com.panda.merge.mapper;

import com.panda.merge.model.ConfigOutrightTradeOdds;
import com.panda.merge.model.ConfigOutrightTradeOddsExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigOutrightTradeOddsMapper {
    long countByExample(ConfigOutrightTradeOddsExample example);

    int deleteByExample(ConfigOutrightTradeOddsExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ConfigOutrightTradeOdds record);

    int insertSelective(ConfigOutrightTradeOdds record);

    List<ConfigOutrightTradeOdds> selectByExample(ConfigOutrightTradeOddsExample example);

    ConfigOutrightTradeOdds selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ConfigOutrightTradeOdds record, @Param("example") ConfigOutrightTradeOddsExample example);

    int updateByExample(@Param("record") ConfigOutrightTradeOdds record, @Param("example") ConfigOutrightTradeOddsExample example);

    int updateByPrimaryKeySelective(ConfigOutrightTradeOdds record);

    int updateByPrimaryKey(ConfigOutrightTradeOdds record);
}