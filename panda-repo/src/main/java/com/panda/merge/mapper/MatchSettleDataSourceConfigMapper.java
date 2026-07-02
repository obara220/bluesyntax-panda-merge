package com.panda.merge.mapper;

import com.panda.merge.model.MatchSettleDataSourceConfig;
import com.panda.merge.model.MatchSettleDataSourceConfigExample;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchSettleDataSourceConfigMapper {
    long countByExample(MatchSettleDataSourceConfigExample example);

    int deleteByExample(MatchSettleDataSourceConfigExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchSettleDataSourceConfig record);

    int insertSelective(MatchSettleDataSourceConfig record);

    List<MatchSettleDataSourceConfig> selectByExample(MatchSettleDataSourceConfigExample example);

    MatchSettleDataSourceConfig selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MatchSettleDataSourceConfig record, @Param("example") MatchSettleDataSourceConfigExample example);

    int updateByExample(@Param("record") MatchSettleDataSourceConfig record, @Param("example") MatchSettleDataSourceConfigExample example);

    int updateByPrimaryKeySelective(MatchSettleDataSourceConfig record);

    int updateByPrimaryKey(MatchSettleDataSourceConfig record);
}