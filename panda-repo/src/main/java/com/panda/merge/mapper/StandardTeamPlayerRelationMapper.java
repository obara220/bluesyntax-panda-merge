package com.panda.merge.mapper;

import com.panda.merge.model.StandardTeamPlayerRelation;
import com.panda.merge.model.StandardTeamPlayerRelationExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StandardTeamPlayerRelationMapper {
    long countByExample(StandardTeamPlayerRelationExample example);

    int deleteByExample(StandardTeamPlayerRelationExample example);

    int deleteByPrimaryKey(Long id);

    int insert(StandardTeamPlayerRelation record);

    int insertSelective(StandardTeamPlayerRelation record);

    List<StandardTeamPlayerRelation> selectByExample(StandardTeamPlayerRelationExample example);

    StandardTeamPlayerRelation selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") StandardTeamPlayerRelation record, @Param("example") StandardTeamPlayerRelationExample example);

    int updateByExample(@Param("record") StandardTeamPlayerRelation record, @Param("example") StandardTeamPlayerRelationExample example);

    int updateByPrimaryKeySelective(StandardTeamPlayerRelation record);

    int updateByPrimaryKey(StandardTeamPlayerRelation record);
}