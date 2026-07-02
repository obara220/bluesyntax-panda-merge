package com.panda.merge.service;

import com.panda.merge.model.LanguageType;

import java.util.List;

/**
 * 对语言类型表
 * @author      tell
 * @since       2020年9月3日15:24:52
 */
public interface LanguageTypeService {
    /**
     * 获取全部多语言类型
     * */
    List<LanguageType> getLanguageTypeList();

    /** 刷新缓存*/
    void refreshCache();
}
