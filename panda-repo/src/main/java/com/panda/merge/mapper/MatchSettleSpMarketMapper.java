package com.panda.merge.mapper;

import com.panda.merge.model.MatchSettleSpMarket;
import com.panda.merge.model.MatchSettleSpMarketExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface MatchSettleSpMarketMapper {
    long countByExample(MatchSettleSpMarketExample example);

    int deleteByExample(MatchSettleSpMarketExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchSettleSpMarket record);

    int insertSelective(MatchSettleSpMarket record);

    List<MatchSettleSpMarket> selectByExample(MatchSettleSpMarketExample example);

    MatchSettleSpMarket selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MatchSettleSpMarket record, @Param("example") MatchSettleSpMarketExample example);

    int updateByExample(@Param("record") MatchSettleSpMarket record, @Param("example") MatchSettleSpMarketExample example);

    int updateByPrimaryKeySelective(MatchSettleSpMarket record);

    int updateByPrimaryKey(MatchSettleSpMarket record);
}