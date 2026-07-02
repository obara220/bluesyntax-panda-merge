package com.panda.merge.mapper;

import com.panda.merge.dto.settle.MatchSettleDataSourceWeightConfigDto;
import com.panda.merge.model.MatchSettleDataSourceConfig;
import com.panda.merge.model.MatchSettleDataSourceConfigExample;
import com.panda.merge.model.MatchSettleDataSourceWeightConfig;
import com.panda.merge.model.MatchSettleDataSourceWeightConfigExample;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchSettleDataSourceWeightConfigMapper {
    long countByExample(MatchSettleDataSourceWeightConfigExample example);

    int deleteByExample(MatchSettleDataSourceWeightConfigExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchSettleDataSourceWeightConfig record);

    void batchInsert(List<MatchSettleDataSourceWeightConfig> configs);

    int insertSelective(MatchSettleDataSourceWeightConfig record);

    List<MatchSettleDataSourceWeightConfig> selectByExample(MatchSettleDataSourceWeightConfigExample example);

    MatchSettleDataSourceWeightConfig selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MatchSettleDataSourceWeightConfig record, @Param("example") MatchSettleDataSourceWeightConfigExample example);

    int updateByExample(@Param("record") MatchSettleDataSourceWeightConfig record, @Param("example") MatchSettleDataSourceWeightConfigExample example);

    int updateByPrimaryKeySelective(MatchSettleDataSourceWeightConfig record);

    int updateByPrimaryKey(MatchSettleDataSourceWeightConfig record);
}