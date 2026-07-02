package com.panda.merge.mapper;

import com.panda.merge.model.MarketOddsChangeHistory;
import com.panda.merge.model.MarketOddsChangeHistoryExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MarketOddsChangeHistoryMapper {
    long countByExample(MarketOddsChangeHistoryExample example);

    int deleteByExample(MarketOddsChangeHistoryExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MarketOddsChangeHistory record);

    int insertSelective(MarketOddsChangeHistory record);

    List<MarketOddsChangeHistory> selectByExample(MarketOddsChangeHistoryExample example);

    MarketOddsChangeHistory selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MarketOddsChangeHistory record, @Param("example") MarketOddsChangeHistoryExample example);

    int updateByExample(@Param("record") MarketOddsChangeHistory record, @Param("example") MarketOddsChangeHistoryExample example);

    int updateByPrimaryKeySelective(MarketOddsChangeHistory record);

    int updateByPrimaryKey(MarketOddsChangeHistory record);
}