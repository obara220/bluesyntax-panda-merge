package com.panda.merge.mapper;

import com.panda.merge.model.MatchScoresStandardRelation;
import com.panda.merge.model.MatchScoresStandardRelationExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchScoresStandardRelationMapper {
    long countByExample(MatchScoresStandardRelationExample example);

    int deleteByExample(MatchScoresStandardRelationExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchScoresStandardRelation record);

    int insertSelective(MatchScoresStandardRelation record);

    List<MatchScoresStandardRelation> selectByExample(MatchScoresStandardRelationExample example);

    MatchScoresStandardRelation selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MatchScoresStandardRelation record, @Param("example") MatchScoresStandardRelationExample example);

    int updateByExample(@Param("record") MatchScoresStandardRelation record, @Param("example") MatchScoresStandardRelationExample example);

    int updateByPrimaryKeySelective(MatchScoresStandardRelation record);

    int updateByPrimaryKey(MatchScoresStandardRelation record);
}