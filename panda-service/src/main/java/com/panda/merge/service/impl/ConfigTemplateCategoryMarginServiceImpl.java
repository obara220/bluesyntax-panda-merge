package com.panda.merge.service.impl;

import com.panda.merge.dao.ConfigTemplateCategoryMarginDao;
import com.panda.merge.mapper.ConfigTemplateCategoryMarginMapper;
import com.panda.merge.model.ConfigTemplateCategoryMargin;
import com.panda.merge.model.ConfigTemplateCategoryMarginExample;
import com.panda.merge.service.ConfigTemplateCategoryMarginService;
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
public class ConfigTemplateCategoryMarginServiceImpl implements ConfigTemplateCategoryMarginService {

    @Autowired
    private ConfigTemplateCategoryMarginMapper mapper;

    @Autowired
    private ConfigTemplateCategoryMarginDao dao;

    @Override
    public List<ConfigTemplateCategoryMargin> getMarginConfigurationsByCategoryIds(Set<Long> categoryIds) {
        ConfigTemplateCategoryMarginExample query = new ConfigTemplateCategoryMarginExample();
        query.createCriteria().andTemplateCategoryIdIn(new ArrayList<>(categoryIds));
        List<ConfigTemplateCategoryMargin> result = mapper.selectByExample(query);
        if(CollectionUtils.isEmpty(result)){
            return null;
        }
        return result;
    }

    @Override
    public void saveBatch(List<ConfigTemplateCategoryMargin> addCategoryMarginAllList) {
        //每200调执行一次
        int excutorNum = 200;
        int count = addCategoryMarginAllList.size();
        List<ConfigTemplateCategoryMargin> excutorList = new ArrayList<>();
        for(int i = 1; i < count; i ++){
            excutorList.add(addCategoryMarginAllList.get(i - 1));
            if(i%excutorNum == 0){
                dao.insertList(addCategoryMarginAllList);
                excutorList.clear();
            }
        }
        dao.insertList(addCategoryMarginAllList);
    }

    @Override
    public void cancelRecsByCategoryIds(Set<Long> templateCategoryIds) {
        Integer cancelFlag = 1;
        dao.updateRecsByCategoryIds(new ArrayList<Long>(templateCategoryIds), cancelFlag);
    }

    @Override
    public void cancelRecsByMarginIds(Set<Long> cancaleMarginIds) {
        Integer cancelFlag = 1;
        dao.updateRecsByMarginIds(new ArrayList<Long>(cancaleMarginIds), cancelFlag);
    }

    @Override
    public void activateMarginConfiguration(Set<Long> templateCategoryIds) {
        Integer cancelFlag = 0;
        dao.updateRecsByCategoryIds(new ArrayList<Long>(templateCategoryIds), cancelFlag);
    }

    @Override
    public void updateRecs(List<ConfigTemplateCategoryMargin> uptCategoryMarginAllList) {
        dao.updateList(uptCategoryMarginAllList);
    }
}
