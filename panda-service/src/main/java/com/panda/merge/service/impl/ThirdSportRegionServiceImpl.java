package com.panda.merge.service.impl;

import com.panda.merge.config.RedisConfig;
import com.panda.merge.mapper.ThirdSportRegionMapper;
import com.panda.merge.model.ThirdSportRegion;
import com.panda.merge.model.ThirdSportRegionExample;
import com.panda.merge.service.ThirdSportRegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.stream.Collectors;

import static com.panda.merge.constant.ConstantSystem.FIX;

/**
 * <Description> 三方运动类型和标准运动类型对应关系配置
 * @author      tell
 * @since       2020年9月3日15:24:52
 */
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class ThirdSportRegionServiceImpl extends BaseServiceImpl<ThirdSportRegion> implements ThirdSportRegionService {

    @Autowired
    private ThirdSportRegionMapper thirdSportRegionMapper;

    @Override
    @Cacheable(key = "'ThirdSportRegionMap:'+#dataSourceCode",unless="#result == null || #result.size() == 0")
    public Map<String, ThirdSportRegion> getUnique2ItemByDataSourceCode(String dataSourceCode) {
        ThirdSportRegionExample example = new ThirdSportRegionExample();
        example.createCriteria().andDataSourceCodeEqualTo(dataSourceCode);
        return thirdSportRegionMapper.selectByExample(example).stream().collect(Collectors.toMap(thi->thi.getId(), i -> i));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(key="'ThirdSportRegionMap:'+#item.dataSourceCode")
    public ThirdSportRegion saveOrupdate(ThirdSportRegion item){
        /** 根据创建时间来区分新增或修改（创建时间不为空是为新增）*/
        if(null != item.getCreateTime()){
            thirdSportRegionMapper.insertSelective(item);
        }else{
            thirdSportRegionMapper.updateByPrimaryKeySelective(item);
        }
        return item;
    }
}
