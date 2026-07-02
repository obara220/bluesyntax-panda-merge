package com.panda.merge.v2.converter;

import com.panda.merge.model.MatchSettleTemplateRelation;
import com.panda.merge.v2.entity.MatchSettleTemplateRelationEntity;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring")
@Component
public interface MatchSettleTemplateRelationConvert {

    MatchSettleTemplateRelation convertSettleTemplateRelation(MatchSettleTemplateRelationEntity matchSettleTemplateRelationEntity);

    List<MatchSettleTemplateRelation> convertSettleTemplateRelation(List<MatchSettleTemplateRelationEntity> matchSettleTemplateRelationEntities);
    MatchSettleTemplateRelationEntity convertSettleTemplateRelationToEntity(MatchSettleTemplateRelation matchSettleTemplateRelation);

    List<MatchSettleTemplateRelationEntity> convertSettleTemplateRelationToEntity(List<MatchSettleTemplateRelation> matchSettleTemplateRelations);

}