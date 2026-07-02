package com.panda.merge.mapper;

import com.panda.merge.model.ConfigStandardThirdOddsTempletRelation;
import com.panda.merge.model.ConfigStandardThirdOddsTempletRelationExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigStandardThirdOddsTempletRelationMapper {
    long countByExample(ConfigStandardThirdOddsTempletRelationExample example);

    int deleteByExample(ConfigStandardThirdOddsTempletRelationExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ConfigStandardThirdOddsTempletRelation record);

    int insertSelective(ConfigStandardThirdOddsTempletRelation record);

    List<ConfigStandardThirdOddsTempletRelation> selectByExample(ConfigStandardThirdOddsTempletRelationExample example);

    ConfigStandardThirdOddsTempletRelation selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ConfigStandardThirdOddsTempletRelation record, @Param("example") ConfigStandardThirdOddsTempletRelationExample example);

    int updateByExample(@Param("record") ConfigStandardThirdOddsTempletRelation record, @Param("example") ConfigStandardThirdOddsTempletRelationExample example);

    int updateByPrimaryKeySelective(ConfigStandardThirdOddsTempletRelation record);

    int updateByPrimaryKey(ConfigStandardThirdOddsTempletRelation record);
}