package com.panda.merge.v2.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.merge.dto.settle.SettleTemplateBatchUpdateDto;
import com.panda.merge.model.MatchSettleTemplate;
import com.panda.merge.model.MatchSettleTemplateExample;
import com.panda.merge.v2.entity.MatchSettleTemplateEntity;

import java.util.List;

public interface MatchSettleTemplateRepository extends IService<MatchSettleTemplateEntity> {

    List<MatchSettleTemplate> selectDiySettleTemplateByTypeAndName(Integer type, String templateName, Long sportId);

    List<MatchSettleTemplateEntity> selectByExample(MatchSettleTemplateExample example);

    MatchSettleTemplate getByIdAndConvert(Long id);

    MatchSettleTemplate getMatchSettleTemplateByTypeAndLevel(Integer type,Integer level, Long sportId);

    MatchSettleTemplate getMatchSettleTemplateByPrimaryKey(Long id);

    void  insertOrUpdateTemplateToRedis(MatchSettleTemplateEntity matchSettleTemplate, boolean isInsert);

    void  insertOrUpdateTemplateToRedis(List<MatchSettleTemplateEntity> matchSettleTemplateList, boolean isInsert);


    void delTemplate(SettleTemplateBatchUpdateDto settleTemplateUpdateDto);
}