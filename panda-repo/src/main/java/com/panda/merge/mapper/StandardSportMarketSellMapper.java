package com.panda.merge.mapper;

import com.panda.merge.model.StandardSportMarketSell;
import com.panda.merge.model.StandardSportMarketSellExample;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface StandardSportMarketSellMapper {
    long countByExample(StandardSportMarketSellExample example);

    int deleteByExample(StandardSportMarketSellExample example);

    int deleteByPrimaryKey(Long id);

    int insert(StandardSportMarketSell record);

    int insertSelective(StandardSportMarketSell record);

    List<StandardSportMarketSell> selectByExample(StandardSportMarketSellExample example);

    StandardSportMarketSell selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") StandardSportMarketSell record, @Param("example") StandardSportMarketSellExample example);

    int updateByExample(@Param("record") StandardSportMarketSell record, @Param("example") StandardSportMarketSellExample example);

    int updateByPrimaryKeySelective(StandardSportMarketSell record);

    int updateByPrimaryKey(StandardSportMarketSell record);
    int updateShowResultStatusAll(@Param("list") List<Long> list, @Param("modifyTime") Long settleStatus,@Param("showResultStatus") Integer showResultStatus);


    List<StandardSportMarketSell> selectByMatchIds(@Param("matchIdList") List<Long> matchIdList);
}