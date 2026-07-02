package com.panda.merge.mapper;

import com.panda.merge.model.MatchDataSourceWeight;
import com.panda.merge.model.MatchDataSourceWeightExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface MatchDataSourceWeightMapper {
    long countByExample(MatchDataSourceWeightExample example);

    int deleteByExample(MatchDataSourceWeightExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchDataSourceWeight record);

    int insertSelective(MatchDataSourceWeight record);

    List<MatchDataSourceWeight> selectByExample(MatchDataSourceWeightExample example);

    MatchDataSourceWeight selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MatchDataSourceWeight record, @Param("example") MatchDataSourceWeightExample example);

    int updateByExample(@Param("record") MatchDataSourceWeight record, @Param("example") MatchDataSourceWeightExample example);

    int updateByPrimaryKeySelective(MatchDataSourceWeight record);

    int updateByPrimaryKey(MatchDataSourceWeight record);
}