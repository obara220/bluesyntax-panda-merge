package com.panda.merge.mapper;

import com.panda.merge.model.StandardSportMarketSellCopy1Delete;
import com.panda.merge.model.StandardSportMarketSellCopy1DeleteExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StandardSportMarketSellCopy1DeleteMapper {
    long countByExample(StandardSportMarketSellCopy1DeleteExample example);

    int deleteByExample(StandardSportMarketSellCopy1DeleteExample example);

    int deleteByPrimaryKey(@Param("id") Long id, @Param("matchInfoId") Long matchInfoId);

    int insert(StandardSportMarketSellCopy1Delete record);

    int insertSelective(StandardSportMarketSellCopy1Delete record);

    List<StandardSportMarketSellCopy1Delete> selectByExample(StandardSportMarketSellCopy1DeleteExample example);

    StandardSportMarketSellCopy1Delete selectByPrimaryKey(@Param("id") Long id, @Param("matchInfoId") Long matchInfoId);

    int updateByExampleSelective(@Param("record") StandardSportMarketSellCopy1Delete record, @Param("example") StandardSportMarketSellCopy1DeleteExample example);

    int updateByExample(@Param("record") StandardSportMarketSellCopy1Delete record, @Param("example") StandardSportMarketSellCopy1DeleteExample example);

    int updateByPrimaryKeySelective(StandardSportMarketSellCopy1Delete record);

    int updateByPrimaryKey(StandardSportMarketSellCopy1Delete record);
}