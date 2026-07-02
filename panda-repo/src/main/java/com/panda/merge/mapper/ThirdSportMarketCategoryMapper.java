package com.panda.merge.mapper;

import com.panda.merge.model.ThirdMarketCategory;
import com.panda.merge.model.ThirdSportMarketCategory;
import com.panda.merge.model.ThirdSportMarketCategoryExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ThirdSportMarketCategoryMapper {
    long countByExample(ThirdSportMarketCategoryExample example);

    int deleteByExample(ThirdSportMarketCategoryExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ThirdSportMarketCategory record);

    int insertSelective(ThirdSportMarketCategory record);

    List<ThirdSportMarketCategory> selectByExample(ThirdSportMarketCategoryExample example);

    ThirdSportMarketCategory selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ThirdSportMarketCategory record, @Param("example") ThirdSportMarketCategoryExample example);

    int updateByExample(@Param("record") ThirdSportMarketCategory record, @Param("example") ThirdSportMarketCategoryExample example);

    int updateByPrimaryKeySelective(ThirdSportMarketCategory record);

    int updateByPrimaryKey(ThirdSportMarketCategory record);

    List<ThirdMarketCategory> queryThirdMarketCategoryList(@Param("referenceIds") List<Long> referenceIds, @Param("sportIds") List<Long> sportIds);
}