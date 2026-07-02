package com.panda.merge.service;

import com.panda.merge.model.ConfigurationMatchTemplateEvent;

import java.util.List;

/**
 * @author :  Riben
 * @Project Name :  panda-merge
 * @Package Name :  com.panda.merge.service
 * @Description :  TODO
 * @Date: 2020-09-17 14:27
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface ConfigurationMatchTemplateEventService {

    void batchSave(List<ConfigurationMatchTemplateEvent> eventConfigurations);

    List<ConfigurationMatchTemplateEvent> getRecsByMatchId(Long standardMatchId);

    void batchUpdate(List<ConfigurationMatchTemplateEvent> updateConfigurations);
}
