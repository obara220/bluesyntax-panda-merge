package com.panda.merge.service;

import com.panda.merge.model.LanguageInternation;

import java.util.List;
import java.util.Map;

/**
 * <Description> 多语言信息
 * @author      tell
 * @since       2020年9月3日15:24:52
 */
public interface LanguageInternationService {
    /**
     * 根据数据源和nameCode获取 多语言类型和多语言关系
     * @param dataSourceCode   数据来源
     * @param nameCode
     * */
    Map<String,LanguageInternation> getLanguageType2Item(String dataSourceCode, Long nameCode);

    /**
     * 根据nameCode列表获取 多语言类型和多语言关系
     * @param nameCodes
     * */
    Map<Long,List<LanguageInternation>> getItemsByNameCodes(List<Long> nameCodes);

    /**
     * 新增或修改
     * @param item  对象息
     * */
    LanguageInternation saveOrupdate(LanguageInternation item,String linkId);

    /**
     * 新增或修改列表
     * @param list  对象列表信息
     * */
    List<LanguageInternation> saveOrupdateList(List<LanguageInternation> list,String linkId);

    /**
     * 删除不需要的多语言信息
     * @param item  多语言信息
     * */
    void delItem(LanguageInternation item,String linkId);

    /**
     * 根据nameCode获取多语言
     * @param nameCode
     * @return
     */
    List<LanguageInternation> getLanguageInternationByNameCode (Long nameCode) ;
}
