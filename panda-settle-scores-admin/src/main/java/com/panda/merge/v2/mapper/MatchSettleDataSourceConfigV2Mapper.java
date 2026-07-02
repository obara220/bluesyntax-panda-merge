package com.panda.merge.v2.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.merge.model.MatchSettleDataSourceConfig;
import com.panda.merge.model.MatchSettleDataSourceConfigExample;
import com.panda.merge.v2.entity.MatchSettleDataSourceConfigEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MatchSettleDataSourceConfigV2Mapper extends BaseMapper<MatchSettleDataSourceConfigEntity> {

    long countByExample(MatchSettleDataSourceConfigExample example);

    int deleteByExample(MatchSettleDataSourceConfigExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchSettleDataSourceConfig record);

    int insertSelective(MatchSettleDataSourceConfig record);

    List<MatchSettleDataSourceConfigEntity> selectByExample(MatchSettleDataSourceConfigExample example);

    MatchSettleDataSourceConfig selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MatchSettleDataSourceConfig record, @Param("example") MatchSettleDataSourceConfigExample example);

    int updateByExample(@Param("record") MatchSettleDataSourceConfig record, @Param("example") MatchSettleDataSourceConfigExample example);

    int updateByPrimaryKeySelective(MatchSettleDataSourceConfig record);

    int updateByPrimaryKey(MatchSettleDataSourceConfig record);


}
