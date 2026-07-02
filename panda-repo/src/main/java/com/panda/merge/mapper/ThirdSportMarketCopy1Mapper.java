package com.panda.merge.mapper;

import com.panda.merge.model.ThirdSportMarketCopy1;
import com.panda.merge.model.ThirdSportMarketCopy1Example;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ThirdSportMarketCopy1Mapper {
    long countByExample(ThirdSportMarketCopy1Example example);

    int deleteByExample(ThirdSportMarketCopy1Example example);

    int deleteByPrimaryKey(Long id);

    int insert(ThirdSportMarketCopy1 record);

    int insertSelective(ThirdSportMarketCopy1 record);

    List<ThirdSportMarketCopy1> selectByExample(ThirdSportMarketCopy1Example example);

    ThirdSportMarketCopy1 selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ThirdSportMarketCopy1 record, @Param("example") ThirdSportMarketCopy1Example example);

    int updateByExample(@Param("record") ThirdSportMarketCopy1 record, @Param("example") ThirdSportMarketCopy1Example example);

    int updateByPrimaryKeySelective(ThirdSportMarketCopy1 record);

    int updateByPrimaryKey(ThirdSportMarketCopy1 record);
}