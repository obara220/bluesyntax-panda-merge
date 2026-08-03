package com.panda.merge.mapper;

import com.panda.merge.model.MatchSettleGrayCheck;
import com.panda.merge.model.MatchSettleGrayCheckExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface MatchSettleGrayCheckMapper {
    long countByExample(MatchSettleGrayCheckExample example);

    int deleteByExample(MatchSettleGrayCheckExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchSettleGrayCheck record);

    int insertSelective(MatchSettleGrayCheck record);

    List<MatchSettleGrayCheck> selectByExample(MatchSettleGrayCheckExample example);

    MatchSettleGrayCheck selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MatchSettleGrayCheck record, @Param("example") MatchSettleGrayCheckExample example);

    int updateByExample(@Param("record") MatchSettleGrayCheck record, @Param("example") MatchSettleGrayCheckExample example);

    int updateByPrimaryKeySelective(MatchSettleGrayCheck record);

    int updateByPrimaryKey(MatchSettleGrayCheck record);
}