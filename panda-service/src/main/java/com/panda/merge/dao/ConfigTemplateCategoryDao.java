package com.panda.merge.dao;

import com.panda.merge.model.ConfigTemplateCategory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author :  Riben
 * @Project Name :  panda-merge
 * @Package Name :  com.panda.merge.dao
 * @Description :  TODO
 * @Date: 2020-09-11 11:26
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface ConfigTemplateCategoryDao {
    /**
     * 批量创建
     */
    int insertList(@Param("list") List<ConfigTemplateCategory> list);

    /**
     * 批量更新-foreach标签
     */
    int updateList(@Param("list") List<ConfigTemplateCategory> list);

    /**
     * 根据条件更新数据
     * @param ids 玩法模板配置表记录主键集
     * @return
     */
    int updateRecs(@Param("ids") List<Long> ids, @Param("cancelFlag") Integer cancelFlag);
}
