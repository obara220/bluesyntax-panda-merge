package com.panda.merge.service;

import com.panda.merge.model.ConfigTemplateEvent;

import java.util.List;

/**
 * @author :  Riben
 * @Project Name :  panda-merge
 * @Package Name :  com.panda.merge.service
 * @Description :  TODO
 * @Date: 2020-09-11 9:52
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface ConfigTemplateEventService {
    /**
     * 根据模板id获取对应的事件审核配置信息
     * @param templateId
     * @return
     */
    List<ConfigTemplateEvent> getEventConfigurationByTemplateId(Long templateId);

    /**
     * 批量保存事件审核配置信息
     * @param addTournamentEventList
     */
    void saveBatch(List<ConfigTemplateEvent> addTournamentEventList);

    /**
     * 批量更新事件审核配置信息
     * @param uptTournamentEventList
     */
    void updateBatch(List<ConfigTemplateEvent> uptTournamentEventList);
}
