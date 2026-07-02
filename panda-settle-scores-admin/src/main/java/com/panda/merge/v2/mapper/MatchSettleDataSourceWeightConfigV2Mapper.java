package com.panda.merge.v2.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.merge.model.MatchSettleDataSourceWeightConfig;
import com.panda.merge.model.MatchSettleDataSourceWeightConfigExample;
import com.panda.merge.v2.entity.MatchSettleDataSourceWeightConfigEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface MatchSettleDataSourceWeightConfigV2Mapper extends BaseMapper<MatchSettleDataSourceWeightConfigEntity> {

    long countByExample(MatchSettleDataSourceWeightConfigExample example);

    int deleteByExample(MatchSettleDataSourceWeightConfigExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchSettleDataSourceWeightConfig record);

    void batchInsert(List<MatchSettleDataSourceWeightConfig> configs);

    int insertSelective(MatchSettleDataSourceWeightConfig record);

    List<MatchSettleDataSourceWeightConfigEntity> selectByExample(MatchSettleDataSourceWeightConfigExample example);

    int updateByExampleSelective(@Param("record") MatchSettleDataSourceWeightConfig record, @Param("example") MatchSettleDataSourceWeightConfigExample example);

    int updateByExample(@Param("record") MatchSettleDataSourceWeightConfig record, @Param("example") MatchSettleDataSourceWeightConfigExample example);

    int updateByPrimaryKeySelective(MatchSettleDataSourceWeightConfig record);

    int updateByPrimaryKey(MatchSettleDataSourceWeightConfig record);

}
