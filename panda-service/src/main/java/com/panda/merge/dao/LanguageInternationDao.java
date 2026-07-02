package com.panda.merge.dao;

import com.panda.merge.model.LanguageInternation;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 多语言自定义dao
 * @author     tell
 * @since      2020年9月4日12:07:19
 */
@Repository
public interface LanguageInternationDao {
    /**
     * 批量创建
     */
    int insertList(@Param("list") List<LanguageInternation> list);

    /**
     * 批量更新
     */
    int updateList(@Param("list") List<LanguageInternation> list);
}
