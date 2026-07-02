package com.panda.merge.mapper;

import com.panda.merge.model.MatchSettleRollBackInfo;
import com.panda.merge.model.MatchSettleRollBackInfoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface MatchSettleRollBackInfoMapper {
    long countByExample(MatchSettleRollBackInfoExample example);

    int deleteByExample(MatchSettleRollBackInfoExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchSettleRollBackInfo record);

    int insertSelective(MatchSettleRollBackInfo record);

    List<MatchSettleRollBackInfo> selectByExample(MatchSettleRollBackInfoExample example);

    MatchSettleRollBackInfo selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MatchSettleRollBackInfo record, @Param("example") MatchSettleRollBackInfoExample example);

    int updateByExample(@Param("record") MatchSettleRollBackInfo record, @Param("example") MatchSettleRollBackInfoExample example);

    int updateByPrimaryKeySelective(MatchSettleRollBackInfo record);

    int updateByPrimaryKey(MatchSettleRollBackInfo record);
}