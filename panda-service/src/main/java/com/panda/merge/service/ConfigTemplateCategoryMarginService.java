package com.panda.merge.service;

import com.panda.merge.model.ConfigTemplateCategoryMargin;

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
public interface ConfigTemplateCategoryMarginService {

    /**
     * 根据玩法配置记录的id集，获取玩法margin记录
     * @param categoryIds 当前模板Id对应的template_category的记录的id
     * @return
     */
    List<ConfigTemplateCategoryMargin> getMarginConfigurationsByCategoryIds(Set<Long> categoryIds);

    /**
     * 批量保存玩法配置
     * @param addCategoryMarginAllList
     */
    void saveBatch(List<ConfigTemplateCategoryMargin> addCategoryMarginAllList);

    /**
     * 根据玩法配置id取消对应的margin配置记录，取消margin配置
     * @param templateCategoryIds
     */
    void cancelRecsByCategoryIds(Set<Long> templateCategoryIds);

    /**
     * 根据margin配置记录的id集，取消margin配置
     * @param cancaleMarginIds
     */
    void cancelRecsByMarginIds(Set<Long> cancaleMarginIds);

    /**
     * 根据玩法配置记录的id集，激活玩法记录id集对应的margin配置
     * @param templateCategoryIds
     */
    void activateMarginConfiguration(Set<Long> templateCategoryIds);

    /**
     * 批量更新margin配置
     * @param uptCategoryMarginAllList
     */
    void updateRecs(List<ConfigTemplateCategoryMargin> uptCategoryMarginAllList);
}
