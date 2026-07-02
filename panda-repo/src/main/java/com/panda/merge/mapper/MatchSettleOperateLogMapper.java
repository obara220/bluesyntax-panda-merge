package com.panda.merge.mapper;

import com.panda.merge.model.MatchSettleOperateLog;
import com.panda.merge.model.MatchSettleOperateLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchSettleOperateLogMapper {
    long countByExample(MatchSettleOperateLogExample example);

    int deleteByExample(MatchSettleOperateLogExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchSettleOperateLog record);

    int insertSelective(MatchSettleOperateLog record);

    List<MatchSettleOperateLog> selectByExample(MatchSettleOperateLogExample example);

    MatchSettleOperateLog selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MatchSettleOperateLog record, @Param("example") MatchSettleOperateLogExample example);

    int updateByExample(@Param("record") MatchSettleOperateLog record, @Param("example") MatchSettleOperateLogExample example);

    int updateByPrimaryKeySelective(MatchSettleOperateLog record);

    int updateByPrimaryKey(MatchSettleOperateLog record);
}