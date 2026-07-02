package com.panda.merge.mapper;

import com.panda.merge.model.ConfigMarketLevel;
import com.panda.merge.model.ConfigMarketLevelExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigMarketLevelMapper {
    long countByExample(ConfigMarketLevelExample example);

    int deleteByExample(ConfigMarketLevelExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ConfigMarketLevel record);

    int insertSelective(ConfigMarketLevel record);

    List<ConfigMarketLevel> selectByExample(ConfigMarketLevelExample example);

    ConfigMarketLevel selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ConfigMarketLevel record, @Param("example") ConfigMarketLevelExample example);

    int updateByExample(@Param("record") ConfigMarketLevel record, @Param("example") ConfigMarketLevelExample example);

    int updateByPrimaryKeySelective(ConfigMarketLevel record);

    int updateByPrimaryKey(ConfigMarketLevel record);
}