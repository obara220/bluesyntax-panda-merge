package com.panda.merge.mapper;

import com.panda.merge.model.ConfigSportThirdMarketCategoryRelation;
import com.panda.merge.model.ConfigSportThirdMarketCategoryRelationExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigSportThirdMarketCategoryRelationMapper {
    long countByExample(ConfigSportThirdMarketCategoryRelationExample example);

    int deleteByExample(ConfigSportThirdMarketCategoryRelationExample example);

    int insert(ConfigSportThirdMarketCategoryRelation record);

    int insertSelective(ConfigSportThirdMarketCategoryRelation record);

    List<ConfigSportThirdMarketCategoryRelation> selectByExample(ConfigSportThirdMarketCategoryRelationExample example);

    int updateByExampleSelective(@Param("record") ConfigSportThirdMarketCategoryRelation record, @Param("example") ConfigSportThirdMarketCategoryRelationExample example);

    int updateByExample(@Param("record") ConfigSportThirdMarketCategoryRelation record, @Param("example") ConfigSportThirdMarketCategoryRelationExample example);
}