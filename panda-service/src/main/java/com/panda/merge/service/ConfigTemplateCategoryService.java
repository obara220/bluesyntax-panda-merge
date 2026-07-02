package com.panda.merge.service;

import com.panda.merge.model.ConfigTemplateCategory;

import java.util.List;
import java.util.Set;

/**
 * @author :  Riben
 * @Project Name :  panda-merge
 * @Package Name :  com.panda.merge.service
 * @Description :  TODO
 * @Date: 2020-09-11 9:52
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface ConfigTemplateCategoryService {

    /**
     * 根据模板id获取模板id对应的玩法配置
     * @param templateId
     * @return
     */
    List<ConfigTemplateCategory> getCategoryConfigurationByTemplateId(Long templateId);

    /**
     * 批量保存模板对应玩法配置信息
     * @param addTournamentCategoryList
     */
    void saveBatch(List<ConfigTemplateCategory> addTournamentCategoryList);

    /**
     * 页面操作取消的玩法，后台更新cancel状态
     * @param cancaleTemplateCatagoryIds
     */
    void cancelRecs(Set<Long> cancaleTemplateCatagoryIds);

    /**
     * 批量更新玩法配置
     * @param uptTournamentCategoryList
     */
    void updateBatch(List<ConfigTemplateCategory> uptTournamentCategoryList);
}
