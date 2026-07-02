package com.panda.merge.mapper;

import com.panda.merge.model.ConfigOutrightTradeType;
import com.panda.merge.model.ConfigOutrightTradeTypeExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigOutrightTradeTypeMapper {
    long countByExample(ConfigOutrightTradeTypeExample example);

    int deleteByExample(ConfigOutrightTradeTypeExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ConfigOutrightTradeType record);

    int insertSelective(ConfigOutrightTradeType record);

    List<ConfigOutrightTradeType> selectByExample(ConfigOutrightTradeTypeExample example);

    ConfigOutrightTradeType selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ConfigOutrightTradeType record, @Param("example") ConfigOutrightTradeTypeExample example);

    int updateByExample(@Param("record") ConfigOutrightTradeType record, @Param("example") ConfigOutrightTradeTypeExample example);

    int updateByPrimaryKeySelective(ConfigOutrightTradeType record);

    int updateByPrimaryKey(ConfigOutrightTradeType record);
}