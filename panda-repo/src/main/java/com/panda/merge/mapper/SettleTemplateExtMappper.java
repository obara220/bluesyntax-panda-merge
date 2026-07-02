package com.panda.merge.mapper;

import com.panda.merge.dto.settle.MatchSettleTemplateTournamentDto;
import com.panda.merge.dto.settle.TemplateListSearchDto;
import com.panda.merge.model.MatchSettleTemplate;
import com.panda.merge.model.MatchSettleTemplateRelation;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SettleTemplateExtMappper {
    List<MatchSettleTemplateTournamentDto> list(TemplateListSearchDto templateListSearchDto);

    Integer listTotal(TemplateListSearchDto templateListSearchDto);

    List<MatchSettleTemplateTournamentDto> listAndLevel(TemplateListSearchDto templateListSearchDto);

    Integer listAndLevelTotal(TemplateListSearchDto templateListSearchDto);

    List<MatchSettleTemplate> selectDiySettleTemplateByTypeAndName(@Param("type") Integer type, @Param("templateName")String templateName, @Param("sportId")Long sportId);
    /**
     * 批量更新到联赛等级模版 则清空配置
     * */
    void updateBatchRelationWeightIdToLevel(@Param("tournamentLevel") Integer tournamentLevel);

    /**
     * 批量更新到专用模版则绑定id
     * */
    void updateBatchRelationWeightId(@Param("tournamentLevel")Integer tournamentLevel,@Param("templateId") Long templateId);


    /**
     * 批量更新到联赛等级模版 则清空配置
     * */
    void updateBatchRelationGrayIdToLevel(@Param("tournamentLevel")Integer tournamentLevel);
    /**
     * 批量更新到专用模版则绑定id
     * */
    void updateBatchRelationGrayId(@Param("tournamentLevel")Integer tournamentLevel,@Param("templateId") Long templateId);


    /**
     * 根据联赛等级查询模板关系【结算模板和灰色区间模板共用此方法】
     * @param tournamentLevel
     * @return
     */
    List<MatchSettleTemplateRelation> selectTemplateRelationByLevel(@Param("tournamentLevel") Integer tournamentLevel);


}
