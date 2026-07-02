package com.panda.merge.mapper;

import com.panda.merge.model.MatchAutoAssociation;
import com.panda.merge.model.MatchAutoAssociationExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchAutoAssociationMapper {
    long countByExample(MatchAutoAssociationExample example);

    int deleteByExample(MatchAutoAssociationExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchAutoAssociation record);

    int insertSelective(MatchAutoAssociation record);

    List<MatchAutoAssociation> selectByExample(MatchAutoAssociationExample example);

    MatchAutoAssociation selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MatchAutoAssociation record, @Param("example") MatchAutoAssociationExample example);

    int updateByExample(@Param("record") MatchAutoAssociation record, @Param("example") MatchAutoAssociationExample example);

    int updateByPrimaryKeySelective(MatchAutoAssociation record);

    int updateByPrimaryKey(MatchAutoAssociation record);
}