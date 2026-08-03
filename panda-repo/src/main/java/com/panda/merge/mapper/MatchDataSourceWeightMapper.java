package com.panda.merge.mapper;

import com.panda.merge.model.MatchDataSourceWeight;
import com.panda.merge.model.MatchDataSourceWeightExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface MatchDataSourceWeightMapper {
    long countByExample(MatchDataSourceWeightExample example);

    int deleteByExample(MatchDataSourceWeightExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchDataSourceWeight row);

    int insertSelective(MatchDataSourceWeight row);

    List<MatchDataSourceWeight> selectByExample(MatchDataSourceWeightExample example);

    MatchDataSourceWeight selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") MatchDataSourceWeight row, @Param("example") MatchDataSourceWeightExample example);

    int updateByExample(@Param("row") MatchDataSourceWeight row, @Param("example") MatchDataSourceWeightExample example);

    int updateByPrimaryKeySelective(MatchDataSourceWeight row);

    int updateByPrimaryKey(MatchDataSourceWeight row);
}