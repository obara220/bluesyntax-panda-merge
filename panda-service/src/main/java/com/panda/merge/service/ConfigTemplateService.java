package com.panda.merge.service;

import com.panda.merge.dto.TradeTournamentConfigDTO;
import com.panda.merge.model.ConfigTemplate;

/**
 * @author :  Riben
 * @Project Name :  panda-merge
 * @Package Name :  com.panda.merge.service
 * @Description :  TODO
 * @Date: 2020-09-10 19:29
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface ConfigTemplateService {
    /**
     * 根据页面参数获取配置模板信息
     * @param tournamentConfigDTO
     * @return
     */
    ConfigTemplate getTemplateRec(TradeTournamentConfigDTO tournamentConfigDTO);

    /**
     * 根据模板Id获取对应模板配置信息
     * @param templateId
     * @return
     */
    ConfigTemplate getTemplateRecByTemplateId(Long templateId);

    /**
     * 根据模板id跟新对应数据
     * @param existConfigTemplate
     * @return
     */
    boolean updateByTemlateId(ConfigTemplate existConfigTemplate);

    /**
     * 保存模板配置信息
     * @param configTemplate
     * @return
     */
    boolean save(ConfigTemplate configTemplate);
}
