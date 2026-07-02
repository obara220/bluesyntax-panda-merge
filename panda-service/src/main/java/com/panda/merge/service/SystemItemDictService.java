package com.panda.merge.service;

import com.panda.merge.model.SystemItemDict;

import java.util.List;

/**
 * 字典值表
 * @author      tell
 * @since       2020年9月3日15:24:52
 */
public interface SystemItemDictService {

    /**
     * 根据字典类型获取字典值列表
     * @param parentTypeId   字典类型id.system_type_dict.id
     * */
    List<SystemItemDict> getListByParentTypeId(Long parentTypeId);

    /**
     * 获取所有的字典对象
     * @return
     */
    List<SystemItemDict> getItemAll();
}
