package com.panda.merge.service.impl;

import com.panda.merge.dto.DataSourceDTO;
import com.panda.merge.mapper.DataSourceMapper;
import com.panda.merge.model.DataSource;
import com.panda.merge.model.DataSourceExample;
import com.panda.merge.service.DataSourceService;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <Description> 三方数据源信息配置信息
 * @author      tell
 * @since       2020年9月3日15:24:52
 */
@Service
public class DataSourceServiceImpl implements DataSourceService {

    @Autowired
    private DataSourceMapper dataSourceMapper;

    /** 缓存全部数据来源（很少更新的数据建议缓存到本地）*/
    private List<DataSource> cacheDataSourceList = new LinkedList<>();

    private Map<String, DataSource> cacheDataSourceMap = new ConcurrentHashMap<>();

    @Override
    public DataSource getItemByCode(String dataSourceCode) {
        if(CollectionUtils.isEmpty(cacheDataSourceList)){
            refreshCache();
        }
        List<DataSource> resList = cacheDataSourceList.stream().filter(obj -> obj.getCode().equals(dataSourceCode)).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(resList)){
            return null;
        }
        return resList.get(0);
    }

    @Override
    public Map<String, DataSource> batchGetItemByCode(Set<String> dataSourceCodes) {
        if(CollectionUtils.isEmpty(dataSourceCodes)){
            return MapUtils.EMPTY_MAP;
        }
        if(CollectionUtils.isEmpty(cacheDataSourceMap)){
            refreshCache();
        }
        return dataSourceCodes.stream().map(t->cacheDataSourceMap.get(t)).collect(Collectors.toMap(t->t.getCode(), Function.identity(), (v1, v2)->v1));
    }

    @Override
    public List<DataSource> getItemList(DataSourceDTO dto){
        if(CollectionUtils.isEmpty(cacheDataSourceList)){
            return cacheDataSourceList;
        }
        List<DataSource> resList;
        //按修改时间降序排序
        if(null == dto.getEndTime()){
            resList = cacheDataSourceList.stream().filter(obj-> obj.getModifyTime() >= dto.getBeginTime())
                    .sorted(Comparator.comparing(DataSource::getModifyTime).reversed()).collect(Collectors.toList());
        }else{
            resList = cacheDataSourceList.stream().filter(obj-> obj.getModifyTime() >= dto.getBeginTime() && obj.getModifyTime() <= dto.getEndTime())
                    .sorted(Comparator.comparing(DataSource::getModifyTime).reversed()).collect(Collectors.toList());
        }
        return resList;
    }

    @Override
    public List<DataSource> getItemList(){
        if(CollectionUtils.isEmpty(cacheDataSourceList)){
            refreshCache();
        }
        return this.cacheDataSourceList;
    }

    @Override
    @PostConstruct
    public void refreshCache(){
        List<DataSource> dataSources = dataSourceMapper.selectByExample(new DataSourceExample());
        this.cacheDataSourceList = dataSources;
        this.cacheDataSourceMap = dataSources.stream().collect(Collectors.toMap(t->t.getCode(), Function.identity(), (v1, v2)->v1));
    }

    @Override
    public List<String> getCommerceDataSources() {
        if (CollectionUtils.isEmpty(cacheDataSourceList)) {
            refreshCache();
        }
        return cacheDataSourceList.stream().filter(e -> e.getCommerce().equals(1)).collect(Collectors.toList()).stream().map(DataSource::getCode).collect(Collectors.toList());
    }

}
