package com.panda.merge.service.impl;

import com.panda.merge.mapper.SystemTypeDictMapper;
import com.panda.merge.model.SystemTypeDict;
import com.panda.merge.model.SystemTypeDictExample;
import com.panda.merge.service.SystemTypeDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 字典类型表
 *
 * @author tell
 * @since 2020年9月3日15:24:52
 */
@Service
public class SystemTypeDictServiceImpl implements SystemTypeDictService {

    @Autowired
    private SystemTypeDictMapper systemTypeDictMapper;

    /**
     * 缓存全部字典类型数据
     */
    private List<SystemTypeDict> sysTemTypeList = new ArrayList<>();


    @Override
    public List<SystemTypeDict> getItemAll() {
        if (CollectionUtils.isEmpty(sysTemTypeList)) {
            refreshCache();
        }
        return sysTemTypeList;
    }

    @Override
    public SystemTypeDict getItemByCodeAndActive(String code, Integer active) {
        if (CollectionUtils.isEmpty(sysTemTypeList)) {
            refreshCache();
        }
        return sysTemTypeList.stream()
                .filter(obj -> obj.getCode().equals(code) && obj.getActive().equals(active))
                .findFirst().orElse(null);
    }

    @Override
    public int updataSystemTypeDict(SystemTypeDict item) {
        SystemTypeDict oldSystemTypeDict = systemTypeDictMapper.selectByPrimaryKey(item.getId());
        int num;
        item.setModifyTime(System.currentTimeMillis());
        if (oldSystemTypeDict != null) {
            item.setCreateTime(null);
            num = systemTypeDictMapper.updateByPrimaryKeySelective(item);
        } else {
            item.setCreateTime(System.currentTimeMillis());
            num = systemTypeDictMapper.insertSelective(item);
        }
        //刷新本地缓存
        refreshCache();
        return num;
    }

    @Override
    public SystemTypeDict getItemById(Long id) {
        if (CollectionUtils.isEmpty(sysTemTypeList)) {
            refreshCache();
        }
        return sysTemTypeList.stream()
                .filter(obj -> obj.getId().equals(id))
                .findFirst().orElse(null);
    }


    public void refreshCache() {
        sysTemTypeList = systemTypeDictMapper.selectByExample(new SystemTypeDictExample());
    }

}
