package com.panda.merge.service;

import com.panda.merge.model.ThirdSportType;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 三方数据源运动类型和三方运动类型关系配置
 * @author      tell
 * @since       2020年9月10日10:56:02
 */
public interface ThirdSportTypeService {

    /**
     * 获取三方运动类型
     * @param dataSourceCode   数据来源
     * @param dataSourceCode   三方数据源运动类型
     * */
    ThirdSportType getItem(String dataSourceCode, Long sportId);

    /**
     * 获取三方数据源运动类型 和 三方运动类型关系
     * @param dataSourceCode   数据来源
     * */
    Map<String,ThirdSportType> getThirdSportId2Item(String dataSourceCode);

    Map<String, ThirdSportType> batchGetThirdSportId2Item(Set<String> dataSourceCodes);

    /**
     * 获取三方运动类型列表
     * @param dataSourceCode   数据来源
     * */
    List<ThirdSportType> getItemsByCode(String dataSourceCode);

    /** 刷新缓存*/
    void refreshCache();

    /**
     * 标准运动类型获取三方运动类型
     * @param standardSportId
     * @param dataSourceCode
     * @return
     */
    String getThirdSportId(Long standardSportId, String dataSourceCode);
}
