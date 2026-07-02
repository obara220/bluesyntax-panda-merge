package com.panda.merge.service;

import com.panda.merge.dto.DataSourceDTO;
import com.panda.merge.model.DataSource;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <Description> 三方数据源信息配置信息
 * @author      tell
 * @since       2020年9月3日15:24:52
 */
public interface DataSourceService {
    /**
     * 根据数据源编码获取数据源信息
     * @param dataSourceCode   数据来源
     * */
    DataSource getItemByCode(String dataSourceCode);

    /**
     * 根据数据源编码批量获取数据源信息
     * @param dataSourceCodes   数据来源
     * */
    Map<String, DataSource> batchGetItemByCode(Set<String> dataSourceCodes);

    /**
     * 根据修改时间获取标准运动类型列表信息
     * @param dto   参数对象
     * */
    List<DataSource> getItemList(DataSourceDTO dto);

    /**
     * 获取标准运动类型列表信息
     * */
    List<DataSource> getItemList();

    /** 刷新缓存*/
    void refreshCache();

    /**
     * 获取商业数据源编码
     *
     * @return
     */
    List<String> getCommerceDataSources();

}
