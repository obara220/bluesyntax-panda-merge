package com.panda.merge.mapper;

import com.panda.merge.model.ThirdSportMarketCategory2020421Delete;
import com.panda.merge.model.ThirdSportMarketCategory2020421DeleteExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ThirdSportMarketCategory2020421DeleteMapper {
    long countByExample(ThirdSportMarketCategory2020421DeleteExample example);

    int deleteByExample(ThirdSportMarketCategory2020421DeleteExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ThirdSportMarketCategory2020421Delete record);

    int insertSelective(ThirdSportMarketCategory2020421Delete record);

    List<ThirdSportMarketCategory2020421Delete> selectByExample(ThirdSportMarketCategory2020421DeleteExample example);

    ThirdSportMarketCategory2020421Delete selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ThirdSportMarketCategory2020421Delete record, @Param("example") ThirdSportMarketCategory2020421DeleteExample example);

    int updateByExample(@Param("record") ThirdSportMarketCategory2020421Delete record, @Param("example") ThirdSportMarketCategory2020421DeleteExample example);

    int updateByPrimaryKeySelective(ThirdSportMarketCategory2020421Delete record);

    int updateByPrimaryKey(ThirdSportMarketCategory2020421Delete record);
}