package com.panda.merge.mapper;

import com.panda.merge.model.ThirdSportMarket;
import com.panda.merge.model.ThirdSportMarketExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ThirdSportMarketMapper {
    long countByExample(ThirdSportMarketExample example);

    int deleteByExample(ThirdSportMarketExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ThirdSportMarket record);

    int insertSelective(ThirdSportMarket record);

    List<ThirdSportMarket> selectByExample(ThirdSportMarketExample example);

    ThirdSportMarket selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ThirdSportMarket record, @Param("example") ThirdSportMarketExample example);

    int updateByExample(@Param("record") ThirdSportMarket record, @Param("example") ThirdSportMarketExample example);

    int updateByPrimaryKeySelective(ThirdSportMarket record);

    int updateByPrimaryKey(ThirdSportMarket record);

    long countByThirdMatchIds(@Param("matchIds") List<Long> matchIds);
}
