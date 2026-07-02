package com.panda.merge.mapper;

import com.panda.merge.model.ThirdMatchTeamRelation;
import com.panda.merge.model.ThirdMatchTeamRelationExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ThirdMatchTeamRelationMapper {
    long countByExample(ThirdMatchTeamRelationExample example);

    int deleteByExample(ThirdMatchTeamRelationExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ThirdMatchTeamRelation record);

    int insertSelective(ThirdMatchTeamRelation record);

    List<ThirdMatchTeamRelation> selectByExample(ThirdMatchTeamRelationExample example);

    ThirdMatchTeamRelation selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ThirdMatchTeamRelation record, @Param("example") ThirdMatchTeamRelationExample example);

    int updateByExample(@Param("record") ThirdMatchTeamRelation record, @Param("example") ThirdMatchTeamRelationExample example);

    int updateByPrimaryKeySelective(ThirdMatchTeamRelation record);

    int updateByPrimaryKey(ThirdMatchTeamRelation record);
}