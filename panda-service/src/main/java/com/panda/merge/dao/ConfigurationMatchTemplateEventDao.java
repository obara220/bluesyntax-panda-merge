package com.panda.merge.dao;

import com.panda.merge.model.ConfigurationMatchTemplateEvent;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author :  Riben
 * @Project Name :  panda-merge
 * @Package Name :  com.panda.merge.dao
 * @Description :  TODO
 * @Date: 2020-09-17 14:32
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface ConfigurationMatchTemplateEventDao {
    void insertBatch(@Param("eventConfigurations") List<ConfigurationMatchTemplateEvent> eventConfigurations);

    void batchUpdate(@Param("updateConfigurations") List<ConfigurationMatchTemplateEvent> updateConfigurations);
}
