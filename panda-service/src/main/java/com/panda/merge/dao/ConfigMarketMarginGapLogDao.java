package com.panda.merge.dao;

import com.panda.merge.model.ConfigMarketMarginGapLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ConfigMarketMarginGapLogDao {

    /**
     * 批量创建
     */
    int insertList(@Param("list") List<ConfigMarketMarginGapLog> list);

}
