package com.panda.merge.dao;

import com.panda.merge.model.ConfigTemplateCategoryMargin;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;

/**
 * @author :  Riben
 * @Project Name :  panda-merge
 * @Package Name :  com.panda.merge.dao
 * @Description :  TODO
 * @Date: 2020-09-11 11:32
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface ConfigTemplateCategoryMarginDao {
    /**
     * 批量创建
     */
    int insertList(@Param("list") List<ConfigTemplateCategoryMargin> list);

    /**
     * 批量更新
     */
    int updateList(@Param("list") List<ConfigTemplateCategoryMargin> list);

    /**
     * 根据玩法配置Id批量取消或激活
     * @param list
     * @param cancelFlag
     */
    void updateRecsByCategoryIds(@Param("list") ArrayList<Long> list, @Param("cancelFlag")Integer cancelFlag);

    /**
     * 根据margin配置Id批量取消或激活
     * @param list
     * @param cancelFlag
     */
    void updateRecsByMarginIds(@Param("list") ArrayList<Long> list, @Param("cancelFlag")Integer cancelFlag);
}
