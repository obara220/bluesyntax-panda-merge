package com.panda.merge.mapper;

import com.panda.merge.model.MatchSettleTemplateRelation;
import com.panda.merge.model.MatchSettleTemplateRelationExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface MatchSettleTemplateRelationMapper {
    long countByExample(MatchSettleTemplateRelationExample example);

    int deleteByExample(MatchSettleTemplateRelationExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchSettleTemplateRelation record);

    int insertSelective(MatchSettleTemplateRelation record);

    List<MatchSettleTemplateRelation> selectByExample(MatchSettleTemplateRelationExample example);

    MatchSettleTemplateRelation selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MatchSettleTemplateRelation record, @Param("example") MatchSettleTemplateRelationExample example);

    int updateByExample(@Param("record") MatchSettleTemplateRelation record, @Param("example") MatchSettleTemplateRelationExample example);

    int updateByPrimaryKeySelective(MatchSettleTemplateRelation record);

    int updateByPrimaryKey(MatchSettleTemplateRelation record);
}