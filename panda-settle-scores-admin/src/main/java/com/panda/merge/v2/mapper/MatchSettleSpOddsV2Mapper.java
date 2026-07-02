package com.panda.merge.v2.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.merge.model.MatchSettleSpOdds;
import com.panda.merge.model.MatchSettleSpOddsExample;
import com.panda.merge.v2.entity.MatchSettleSpOddsEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchSettleSpOddsV2Mapper extends BaseMapper<MatchSettleSpOddsEntity> {

    long countByExample(MatchSettleSpOddsExample example);

    int deleteByExample(MatchSettleSpOddsExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchSettleSpOdds record);

    int insertSelective(MatchSettleSpOdds record);

    List<MatchSettleSpOddsEntity> selectByExample(MatchSettleSpOddsExample example);

    int updateByExampleSelective(@Param("record") MatchSettleSpOdds record, @Param("example") MatchSettleSpOddsExample example);

    int updateByExample(@Param("record") MatchSettleSpOdds record, @Param("example") MatchSettleSpOddsExample example);

    int updateByPrimaryKeySelective(MatchSettleSpOdds record);

    int updateByPrimaryKey(MatchSettleSpOdds record);

}
