package com.panda.merge.v2.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.merge.model.MatchSettleSpMarket;
import com.panda.merge.model.MatchSettleSpMarketExample;
import com.panda.merge.v2.entity.MatchSettleSpMarketEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface MatchSettleSpMarketV2Mapper extends BaseMapper<MatchSettleSpMarketEntity> {

    long countByExample(MatchSettleSpMarketExample example);

    int deleteByExample(MatchSettleSpMarketExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchSettleSpMarket record);

    int insertSelective(MatchSettleSpMarket record);

    List<MatchSettleSpMarketEntity> selectByExample(MatchSettleSpMarketExample example);

    int updateByExampleSelective(@Param("record") MatchSettleSpMarket record, @Param("example") MatchSettleSpMarketExample example);

    int updateByExample(@Param("record") MatchSettleSpMarket record, @Param("example") MatchSettleSpMarketExample example);

    int updateByPrimaryKeySelective(MatchSettleSpMarket record);

    int updateByPrimaryKey(MatchSettleSpMarket record);

}
