package com.panda.merge.v2.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.merge.model.MatchSettleInfoExample;
import com.panda.merge.v2.entity.MatchSettleInfoEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MatchSettleInfoV2Mapper extends BaseMapper<MatchSettleInfoEntity> {

    List<MatchSettleInfoEntity> selectByExample(MatchSettleInfoExample example);

    int updateByExampleSelective(@Param("record") MatchSettleInfoEntity record, @Param("example") MatchSettleInfoExample example);

}
