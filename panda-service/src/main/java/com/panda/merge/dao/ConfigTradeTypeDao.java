package com.panda.merge.dao;

import com.panda.merge.model.ConfigTradeType;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ConfigTradeTypeDao {
    /**
     * 批量保存
     * @param list 新增数据
     */
    void insertList(@Param("list") List<ConfigTradeType> list);
}
