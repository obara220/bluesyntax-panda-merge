package com.panda.merge.service.impl;

import com.panda.merge.exception.Asserts;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.mapper.SystemItemDictMapper;
import com.panda.merge.model.SystemItemDict;
import com.panda.merge.model.SystemItemDictExample;
import com.panda.merge.service.SystemItemDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 字典值表
 * @author      tell
 * @since       2020年9月3日15:24:52
 */
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class SystemItemDictServiceImpl implements SystemItemDictService {

    @Autowired
    private SystemItemDictMapper systemItemDictMapper;

    @Override
    @Cacheable(key = "'SystemItemDict:'+#parentTypeId",unless="#result == null || #result.size() == 0")
    public List<SystemItemDict> getListByParentTypeId(Long parentTypeId){
        SystemItemDictExample example = new SystemItemDictExample();
        example.createCriteria().andParentTypeIdEqualTo(parentTypeId);
        List<SystemItemDict> dicts = systemItemDictMapper.selectByExample(example);
        Asserts.validateListForEmpty(dicts, "字典值ParentTypeId:"+parentTypeId+"数据为空,请检查!");
        return dicts;
    }

    @Override
    public List<SystemItemDict> getItemAll() {
        return systemItemDictMapper.selectByExample(new SystemItemDictExample());
    }
}
