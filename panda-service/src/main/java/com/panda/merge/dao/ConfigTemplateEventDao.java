package com.panda.merge.dao;

import com.panda.merge.model.ConfigTemplateEvent;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author :  Riben
 * @Project Name :  panda-merge
 * @Package Name :  com.panda.merge.dao
 * @Description :  TODO
 * @Date: 2020-09-11 16:17
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface ConfigTemplateEventDao {

    /**
     * 批量保存
     * @param list
     */
    void insertList(@Param("list") List<ConfigTemplateEvent> list);

    void updateBatch(@Param("list") List<ConfigTemplateEvent> list);
}
