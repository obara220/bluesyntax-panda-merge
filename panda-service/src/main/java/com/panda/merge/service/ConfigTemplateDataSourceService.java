package com.panda.merge.service;

import com.panda.merge.model.ConfigTemplateDataSource;

/**
 * @author :  Riben
 * @Project Name :  panda-merge
 * @Package Name :  com.panda.merge.service
 * @Description :  TODO
 * @Date: 2020-09-11 9:49
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface ConfigTemplateDataSourceService {
    /**
     * 根据模板id获取对应数据源权重配置
     * @param templateId
     * @return
     */
    ConfigTemplateDataSource getDataSourceConfigurationBytemplateId(Long templateId);

    /**
     * 保存数据源权重配置
     * @param configTemplateDataSource
     * @return
     */
    boolean save(ConfigTemplateDataSource configTemplateDataSource);

    /**
     * 根据数据源权重配置记录ID更新
     * @param configTemplateDataSource
     */
    void updateById(ConfigTemplateDataSource configTemplateDataSource);
}
