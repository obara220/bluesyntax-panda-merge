package com.panda.merge.v2.converter;

import com.panda.merge.model.MatchSettleTemplate;
import com.panda.merge.v2.entity.MatchSettleTemplateEntity;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring")
@Component
public interface MatchSettleTemplateConvert {

    MatchSettleTemplate convertMatchSettleTemplate(MatchSettleTemplateEntity matchSettleTemplateEntity);

    List<MatchSettleTemplate> convertMatchSettleTemplate(List<MatchSettleTemplateEntity> matchSettleTemplateEntities);

    MatchSettleTemplateEntity convertMatchSettleTemplateToEntity(MatchSettleTemplate matchSettleTemplate);

    List<MatchSettleTemplateEntity> convertMatchSettleTemplateToEntity(List<MatchSettleTemplate> matchSettleTemplates);

}
