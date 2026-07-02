package com.panda.merge.service;

import com.panda.merge.model.ThirdSportRegion;

import java.util.Map;

/**
 * <Description> 三方运动区域
 * @author      tell
 * @since       2020年9月3日15:24:52
 */
public interface ThirdSportRegionService {
    /**
     * 根据数据来源 获取 唯一键(数据源+运动类型+三方数据源区域ID)和三方运动区域的关系
     * @param dataSourceCode   数据来源
     * @return  Map<String, ThirdSportRegion>
     * */
    Map<String, ThirdSportRegion> getUnique2ItemByDataSourceCode(String dataSourceCode);

    /**
     * 新增或修改
     * @param item  对象信息
     * @return  ThirdSportRegion
     * */
    ThirdSportRegion saveOrupdate(ThirdSportRegion item);
}
