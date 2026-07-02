package com.panda.merge.mapper;

import com.panda.merge.model.ConfigTradeType;
import com.panda.merge.model.ConfigTradeTypeExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigTradeTypeMapper {
    long countByExample(ConfigTradeTypeExample example);

    int deleteByExample(ConfigTradeTypeExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ConfigTradeType record);

    int insertSelective(ConfigTradeType record);

    List<ConfigTradeType> selectByExample(ConfigTradeTypeExample example);

    ConfigTradeType selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ConfigTradeType record, @Param("example") ConfigTradeTypeExample example);

    int updateByExample(@Param("record") ConfigTradeType record, @Param("example") ConfigTradeTypeExample example);

    int updateByPrimaryKeySelective(ConfigTradeType record);

    int updateByPrimaryKey(ConfigTradeType record);
}