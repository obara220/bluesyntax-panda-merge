package com.panda.merge.mapper;

import com.panda.merge.model.MatchSettleResult;
import com.panda.merge.model.MatchSettleResultExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface MatchSettleResultMapper {
    long countByExample(MatchSettleResultExample example);

    int deleteByExample(MatchSettleResultExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchSettleResult record);

    int insertSelective(MatchSettleResult record);

    List<MatchSettleResult> selectByExample(MatchSettleResultExample example);

    MatchSettleResult selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MatchSettleResult record, @Param("example") MatchSettleResultExample example);

    int updateByExample(@Param("record") MatchSettleResult record, @Param("example") MatchSettleResultExample example);

    int updateByPrimaryKeySelective(MatchSettleResult record);

    int updateByPrimaryKey(MatchSettleResult record);
}