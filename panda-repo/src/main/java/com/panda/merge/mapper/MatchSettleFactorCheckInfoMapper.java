package com.panda.merge.mapper;

import com.panda.merge.model.MatchSettleFactorCheckInfo;
import com.panda.merge.model.MatchSettleFactorCheckInfoExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MatchSettleFactorCheckInfoMapper {
    long countByExample(MatchSettleFactorCheckInfoExample example);

    int deleteByExample(MatchSettleFactorCheckInfoExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchSettleFactorCheckInfo record);

    int insertSelective(MatchSettleFactorCheckInfo record);

    List<MatchSettleFactorCheckInfo> selectByExample(MatchSettleFactorCheckInfoExample example);

    MatchSettleFactorCheckInfo selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MatchSettleFactorCheckInfo record, @Param("example") MatchSettleFactorCheckInfoExample example);

    int updateByExample(@Param("record") MatchSettleFactorCheckInfo record, @Param("example") MatchSettleFactorCheckInfoExample example);

    int updateByPrimaryKeySelective(MatchSettleFactorCheckInfo record);

    int updateByPrimaryKey(MatchSettleFactorCheckInfo record);
}