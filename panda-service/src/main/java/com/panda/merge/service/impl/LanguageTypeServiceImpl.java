package com.panda.merge.service.impl;

import com.panda.merge.mapper.LanguageTypeMapper;
import com.panda.merge.model.LanguageType;
import com.panda.merge.model.LanguageTypeExample;
import com.panda.merge.service.LanguageTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 对语言类型表
 * @author      tell
 * @since       2020年9月3日15:24:52
 */
@Service
public class LanguageTypeServiceImpl implements LanguageTypeService {

    @Autowired
    private LanguageTypeMapper languageTypeMapper;

    /** 缓存全部多语言类型（很少更新的数据建议缓存到本地）*/
    private List<LanguageType> cacheLanguageTypeList = new LinkedList<>();

    @Override
    public List<LanguageType> getLanguageTypeList() {
        if(CollectionUtils.isEmpty(cacheLanguageTypeList)){
            refreshCache();
        }
        return this.cacheLanguageTypeList;
    }

    @Override
    @PostConstruct
    public void refreshCache(){
        List<LanguageType> resLanguageTypeList = languageTypeMapper.selectByExample(new LanguageTypeExample());
        if(!CollectionUtils.isEmpty(resLanguageTypeList)){
            //按ID升序
            this.cacheLanguageTypeList = resLanguageTypeList.stream().sorted(Comparator.comparingDouble(LanguageType::getId)).collect(Collectors.toList());
        }
    }

}
