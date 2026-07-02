package com.panda.merge.mapper;

import com.panda.merge.model.SportMarketRelation;
import com.panda.merge.model.SportMarketRelationExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SportMarketRelationMapper {
    long countByExample(SportMarketRelationExample example);

    int deleteByExample(SportMarketRelationExample example);

    int deleteByPrimaryKey(Long id);

    int insert(SportMarketRelation record);

    int insertSelective(SportMarketRelation record);

    List<SportMarketRelation> selectByExample(SportMarketRelationExample example);

    SportMarketRelation selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") SportMarketRelation record, @Param("example") SportMarketRelationExample example);

    int updateByExample(@Param("record") SportMarketRelation record, @Param("example") SportMarketRelationExample example);

    int updateByPrimaryKeySelective(SportMarketRelation record);

    int updateByPrimaryKey(SportMarketRelation record);
}