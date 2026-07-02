package com.panda.merge.service.impl;

import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.mapper.StandardSportTypeMapper;
import com.panda.merge.mapper.ThirdSportTypeMapper;
import com.panda.merge.model.*;
import com.panda.merge.service.DataSourceService;
import com.panda.merge.service.ThirdSportTypeService;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 三方数据源运动类型和三方运动类型关系配置
 * @author      tell
 * @since       2020年9月10日10:56:08
 */
@Service
public class ThirdSportTypeServiceImpl implements ThirdSportTypeService {

    @Resource
    private ThirdSportTypeMapper thirdSportTypeMapper;

    @Autowired
    private StandardSportTypeMapper standardSportTypeMapper;

    @Autowired
    private DataSourceService dataSourceService;

    /**
     * DataSourceCodeEnum.getCodeList() 中含无需要校验赛种的数据源编码
     * 缓存全部数据来源的关联运动类型（很少更新的数据建议缓存到本地）
     * */
    private Map<String,List<ThirdSportType>> dataSourceCode2Items = new LinkedHashMap<>();

    /** 缓存全部标准运动类型*/
    private List<StandardSportType> standardSportTypeList = new ArrayList<>();

    @Override
    public ThirdSportType getItem(String dataSourceCode, Long sportId) {
        return getThirdSportId2Item(dataSourceCode).get(String.valueOf(sportId));
    }

    @Override
    public Map<String,ThirdSportType> getThirdSportId2Item(String dataSourceCode) {
        return getItemsByCode(dataSourceCode).stream().collect(Collectors.toMap(ThirdSportType::getThirdSportId, i -> i));
    }

    @Override
    public Map<String, ThirdSportType> batchGetThirdSportId2Item(Set<String> dataSourceCodes) {
        if (CollectionUtils.isEmpty(dataSourceCodes)) {
            return MapUtils.EMPTY_MAP;
        }
        if(CollectionUtils.isEmpty(dataSourceCode2Items)){
            refreshCache();
        }
        List<ThirdSportType> thirdSportTypes = dataSourceCodes.stream().flatMap(t->dataSourceCode2Items.get(t).stream()).collect(Collectors.toList());
        return thirdSportTypes.stream().collect(Collectors.toMap(t->t.getDataSourceCode()+"-"+t.getThirdSportId(), Function.identity(), (v1, v2)->v1));
    }

    @Override
    public List<ThirdSportType> getItemsByCode(String dataSourceCode){
        if(CollectionUtils.isEmpty(dataSourceCode2Items)){
            refreshCache();
        }
        List<ThirdSportType> thirdSportTypes = dataSourceCode2Items.get(dataSourceCode);
        if(CollectionUtils.isEmpty(thirdSportTypes)){
            return new LinkedList<>();
        }
        return thirdSportTypes;
    }

    /**
     *  DataSourceCodeEnum.getCodeList() 中含无需要校验赛种的数据源编码
     * 部分数据源是没有配置三方运动类型关系的，数据源是直接传入的标准运动类型
     * */
    @Override
    @PostConstruct
    public void refreshCache(){
        //标准赛种缓存
        standardSportTypeList = standardSportTypeMapper.selectByExample(new StandardSportTypeExample());
        //查询需要配置运动类型关系的三方运动类型
        List<ThirdSportType> thirdSportTypeList = thirdSportTypeMapper.selectByExample(new ThirdSportTypeExample());
        if(!CollectionUtils.isEmpty(thirdSportTypeList)){
            //三方运动类型中配置的数据源编码
            Set<String>  thirdSportDataSourceCode = thirdSportTypeList.stream().map(obj -> obj.getDataSourceCode()).collect(Collectors.toSet());
            List<DataSource> dataSourceList = dataSourceService.getItemList();
            for(DataSource dataSource:dataSourceList){
                if(!thirdSportDataSourceCode.contains(dataSource.getCode())){
                    thirdSportTypeList.addAll(standardSportType2ThirdSportType(dataSource.getCode()));
                }
            }
            dataSourceCode2Items = thirdSportTypeList.stream().collect(Collectors.groupingBy(obj -> obj.getDataSourceCode()));
        }
    }

    @Override
    public String getThirdSportId(Long standardSportId,String dataSourceCode) {
        List<ThirdSportType> thirdSportTypes = dataSourceCode2Items.get(dataSourceCode);
        for (ThirdSportType thirdSportType: thirdSportTypes) {
            if(thirdSportType.getReferenceId().equals(standardSportId)){
                return thirdSportType.getThirdSportId();
            }
        }
        //如果未找到对应三方赛种id，默认使用标准赛种ID
        return String.valueOf(standardSportId);
    }


    /**
     * 无需配置运动类型关系的数据源
     * 标准运动类型列表转换为三方运动类型列表
     * */
    public List<ThirdSportType> standardSportType2ThirdSportType(String dataSourceCode){
        List<ThirdSportType> thirdSportTypeList = new LinkedList<>();
        for (StandardSportType standardSportType: standardSportTypeList) {
            ThirdSportType thirdSportType = new ThirdSportType();
            thirdSportType.setDataSourceCode(dataSourceCode);
            thirdSportType.setId(standardSportType.getId());
            thirdSportType.setReferenceId(standardSportType.getId());
            thirdSportType.setNameCode(standardSportType.getNameCode());
            thirdSportType.setThirdSportId(standardSportType.getId()+"");
            thirdSportType.setIntroduction(standardSportType.getIntroduction());
            thirdSportTypeList.add(thirdSportType);
        }
        return thirdSportTypeList;
    }
}
