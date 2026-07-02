package com.panda.merge.v2.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.merge.model.MatchSettleOperateLog;
import com.panda.merge.model.MatchSettleOperateLogExample;
import com.panda.merge.v2.entity.MatchSettleOperateLogEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
public interface MatchSettleOperateLogV3Mapper extends BaseMapper<MatchSettleOperateLogEntity> {


    long countByExample(MatchSettleOperateLogExample example);

    int deleteByExample(MatchSettleOperateLogExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchSettleOperateLog record);

    int insertSelective(MatchSettleOperateLog record);

    List<MatchSettleOperateLogEntity> selectByExample(MatchSettleOperateLogExample example);

    int updateByExampleSelective(@Param("record") MatchSettleOperateLog record, @Param("example") MatchSettleOperateLogExample example);

    int updateByExample(@Param("record") MatchSettleOperateLog record, @Param("example") MatchSettleOperateLogExample example);

    int updateByPrimaryKeySelective(MatchSettleOperateLog record);


}
