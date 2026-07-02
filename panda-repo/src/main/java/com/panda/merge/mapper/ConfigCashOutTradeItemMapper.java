package com.panda.merge.mapper;

import com.panda.merge.model.ConfigCashOutTradeItem;
import com.panda.merge.model.ConfigCashOutTradeItemExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigCashOutTradeItemMapper {
    long countByExample(ConfigCashOutTradeItemExample example);

    int deleteByExample(ConfigCashOutTradeItemExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ConfigCashOutTradeItem record);

    int insertSelective(ConfigCashOutTradeItem record);

    List<ConfigCashOutTradeItem> selectByExample(ConfigCashOutTradeItemExample example);

    ConfigCashOutTradeItem selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ConfigCashOutTradeItem record, @Param("example") ConfigCashOutTradeItemExample example);

    int updateByExample(@Param("record") ConfigCashOutTradeItem record, @Param("example") ConfigCashOutTradeItemExample example);

    int updateByPrimaryKeySelective(ConfigCashOutTradeItem record);

    int updateByPrimaryKey(ConfigCashOutTradeItem record);
}