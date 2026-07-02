package com.panda.merge.v2.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.merge.dto.settle.MatchSettleTemplateTournamentDto;
import com.panda.merge.dto.settle.TemplateListSearchDto;
import com.panda.merge.model.MatchSettleTemplateRelation;
import com.panda.merge.model.MatchSettleTemplateRelationExample;
import com.panda.merge.v2.entity.MatchSettleTemplateRelationEntity;

import java.util.List;

public interface MatchSettleTemplateRelationRepository extends IService<MatchSettleTemplateRelationEntity> {

    List<MatchSettleTemplateTournamentDto> list(TemplateListSearchDto templateListSearchDto);

    List<MatchSettleTemplateTournamentDto> listAndLevel(TemplateListSearchDto templateListSearchDto);

    Integer listAndLevelTotal(TemplateListSearchDto templateListSearchDto);

    Integer listTotal(TemplateListSearchDto templateListSearchDto);

    MatchSettleTemplateRelation getMatchSettleTemplateRelation(Long id);

    List<MatchSettleTemplateRelationEntity> selectByExample(MatchSettleTemplateRelationExample example);

    void updateBatchRelationWeightIdToLevel(Integer tournamentLevel);

    void updateBatchRelationWeightId(Integer tournamentLevel, Long templateId);

    void updateBatchRelationGrayIdToLevel(Integer tournamentLevel);

    void updateBatchRelationGrayId(Integer tournamentLevel, Long templateId);

    void delTemplateRelationByExample(MatchSettleTemplateRelationExample example);

    void insertTemplateRelationToRedis(MatchSettleTemplateRelationEntity matchSettleTemplateRelation, boolean isInsert);

    void batchInsertTemplateRelationToRedis(List<MatchSettleTemplateRelation> list);

    void insertTemplateRelationOnlyRedis(MatchSettleTemplateRelationEntity matchSettleTemplateRelation);



}