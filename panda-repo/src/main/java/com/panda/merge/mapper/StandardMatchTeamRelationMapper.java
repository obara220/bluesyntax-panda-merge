package com.panda.merge.mapper;

import com.panda.merge.model.StandardMatchTeamRelation;
import com.panda.merge.model.StandardMatchTeamRelationExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StandardMatchTeamRelationMapper {
    long countByExample(StandardMatchTeamRelationExample example);

    int deleteByExample(StandardMatchTeamRelationExample example);

    int deleteByPrimaryKey(Long id);

    int insert(StandardMatchTeamRelation record);

    int insertSelective(StandardMatchTeamRelation record);

    List<StandardMatchTeamRelation> selectByExampleWithBLOBs(StandardMatchTeamRelationExample example);

    List<StandardMatchTeamRelation> selectByExample(StandardMatchTeamRelationExample example);

    StandardMatchTeamRelation selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") StandardMatchTeamRelation record, @Param("example") StandardMatchTeamRelationExample example);

    int updateByExampleWithBLOBs(@Param("record") StandardMatchTeamRelation record, @Param("example") StandardMatchTeamRelationExample example);

    int updateByExample(@Param("record") StandardMatchTeamRelation record, @Param("example") StandardMatchTeamRelationExample example);

    int updateByPrimaryKeySelective(StandardMatchTeamRelation record);

    int updateByPrimaryKeyWithBLOBs(StandardMatchTeamRelation record);

    int updateByPrimaryKey(StandardMatchTeamRelation record);
}