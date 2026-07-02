package com.panda.merge.service.impl;

import com.panda.merge.dao.ConfigTemplateCategoryDao;
import com.panda.merge.mapper.ConfigTemplateCategoryMapper;
import com.panda.merge.model.ConfigTemplateCategory;
import com.panda.merge.model.ConfigTemplateCategoryExample;
import com.panda.merge.service.ConfigTemplateCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author :  Riben
 * @Project Name :  panda-merge
 * @Package Name :  com.panda.merge.service.impl
 * @Description :  TODO
 * @Date: 2020-09-11 10:04
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
public class ConfigTemplateCategoryServiceImpl implements ConfigTemplateCategoryService {

    @Autowired
    private ConfigTemplateCategoryMapper mapper;

    @Autowired
    private ConfigTemplateCategoryDao dao;

    @Override
    public List<ConfigTemplateCategory> getCategoryConfigurationByTemplateId(Long templateId) {
        ConfigTemplateCategoryExample query = new  ConfigTemplateCategoryExample();
        query.createCriteria().andTemplateIdEqualTo(templateId);
        List<ConfigTemplateCategory> result = mapper.selectByExample(query);
        if(CollectionUtils.isEmpty(result)){
            return null;
        }
        return result;
    }

    @Override
    public void saveBatch(List<ConfigTemplateCategory> addTournamentCategoryList) {
        dao.insertList(addTournamentCategoryList);
    }

    @Override
    public void cancelRecs(Set<Long> cancaleTemplateCatagoryIds) {
        Integer cancelFlag= 1;
        dao.updateRecs(new ArrayList<>(cancaleTemplateCatagoryIds), cancelFlag);
    }

    @Override
    public void updateBatch(List<ConfigTemplateCategory> uptTournamentCategoryList) {
        dao.updateList(uptTournamentCategoryList);
    }


}
