package com.panda.merge.mapper;

import com.panda.merge.model.MatchSettleGrayWeight;
import com.panda.merge.model.MatchSettleGrayWeightExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface MatchSettleGrayWeightMapper {
    long countByExample(MatchSettleGrayWeightExample example);

    int deleteByExample(MatchSettleGrayWeightExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchSettleGrayWeight record);

    int insertSelective(MatchSettleGrayWeight record);

    List<MatchSettleGrayWeight> selectByExample(MatchSettleGrayWeightExample example);

    MatchSettleGrayWeight selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MatchSettleGrayWeight record, @Param("example") MatchSettleGrayWeightExample example);

    int updateByExample(@Param("record") MatchSettleGrayWeight record, @Param("example") MatchSettleGrayWeightExample example);

    int updateByPrimaryKeySelective(MatchSettleGrayWeight record);

    int updateByPrimaryKey(MatchSettleGrayWeight record);
}