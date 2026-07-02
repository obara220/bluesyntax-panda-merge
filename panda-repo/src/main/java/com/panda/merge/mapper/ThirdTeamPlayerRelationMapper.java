package com.panda.merge.mapper;

import com.panda.merge.model.ThirdTeamPlayerRelation;
import com.panda.merge.model.ThirdTeamPlayerRelationExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ThirdTeamPlayerRelationMapper {
    long countByExample(ThirdTeamPlayerRelationExample example);

    int deleteByExample(ThirdTeamPlayerRelationExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ThirdTeamPlayerRelation record);

    int insertSelective(ThirdTeamPlayerRelation record);

    List<ThirdTeamPlayerRelation> selectByExample(ThirdTeamPlayerRelationExample example);

    ThirdTeamPlayerRelation selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ThirdTeamPlayerRelation record, @Param("example") ThirdTeamPlayerRelationExample example);

    int updateByExample(@Param("record") ThirdTeamPlayerRelation record, @Param("example") ThirdTeamPlayerRelationExample example);

    int updateByPrimaryKeySelective(ThirdTeamPlayerRelation record);

    int updateByPrimaryKey(ThirdTeamPlayerRelation record);
}