package com.panda.merge.mapper;

import com.panda.merge.model.MatchSettleDataSourceSwitchExample;
import com.panda.merge.model.MatchSettleDataSourceSwitch;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchSettleDataSourceSwitchMapper {
    long countByExample(MatchSettleDataSourceSwitchExample example);

    int deleteByExample(MatchSettleDataSourceSwitchExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchSettleDataSourceSwitch record);

    int insertSelective(MatchSettleDataSourceSwitch record);

    List<MatchSettleDataSourceSwitch> selectByExample(MatchSettleDataSourceSwitchExample example);

    MatchSettleDataSourceSwitch selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MatchSettleDataSourceSwitch record, @Param("example") MatchSettleDataSourceSwitchExample example);

    int updateByExample(@Param("record") MatchSettleDataSourceSwitch record, @Param("example") MatchSettleDataSourceSwitchExample example);

    int updateByPrimaryKeySelective(MatchSettleDataSourceSwitch record);

    int updateByPrimaryKey(MatchSettleDataSourceSwitch record);
}