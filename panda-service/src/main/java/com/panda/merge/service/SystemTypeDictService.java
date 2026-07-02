package com.panda.merge.service;

import com.panda.merge.model.SystemTypeDict;

import java.util.List;

/**
 * 字典类型表
 * @author tell
 * @since 2020年9月9日20:19:45
 */
public interface SystemTypeDictService {

    /**
     * 查询全部字典类型列表
     * @return List<SystemTypeDict>
     */
    List<SystemTypeDict> getItemAll();

    /**
     * 根据字典类型coede和是否激活获取字典类型
     * @param code   字典关键字
     * @param active 是否激活
     * @return SystemTypeDict
     */
    SystemTypeDict getItemByCodeAndActive(String code, Integer active);

    /**
     * 修改字典类型数据
     * */
    int updataSystemTypeDict(SystemTypeDict item);


    SystemTypeDict getItemById(Long id);
}
