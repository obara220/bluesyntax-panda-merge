package com.panda.merge.mapper;

import com.panda.merge.model.ThirdMarketCategory;
import com.panda.merge.model.ThirdMarketCategoryExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ThirdMarketCategoryMapper {
    long countByExample(ThirdMarketCategoryExample example);

    int deleteByExample(ThirdMarketCategoryExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ThirdMarketCategory record);

    int insertSelective(ThirdMarketCategory record);

    List<ThirdMarketCategory> selectByExample(ThirdMarketCategoryExample example);

    ThirdMarketCategory selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ThirdMarketCategory record, @Param("example") ThirdMarketCategoryExample example);

    int updateByExample(@Param("record") ThirdMarketCategory record, @Param("example") ThirdMarketCategoryExample example);

    int updateByPrimaryKeySelective(ThirdMarketCategory record);

    int updateByPrimaryKey(ThirdMarketCategory record);
}