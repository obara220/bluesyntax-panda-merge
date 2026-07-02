package com.panda.merge.dao;

import com.panda.merge.model.I18nnamesOutrightMatchName;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 三方赛事，标准赛事，投注项多语言
 * @author : tell
 * @since    2020年9月16日17:21:05
 */
public interface I18nnamesOutrightMatchNameDao {
    /**
     * 批量创建
     */
    int insertList(@Param("list") List<I18nnamesOutrightMatchName> list);

    /**
     * 批量更新
     */
    int updateList(@Param("list") List<I18nnamesOutrightMatchName> list);
}
