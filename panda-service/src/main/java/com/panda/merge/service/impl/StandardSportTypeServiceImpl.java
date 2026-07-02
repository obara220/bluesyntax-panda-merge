package com.panda.merge.service.impl;

import com.panda.merge.config.RedisConfig;
import com.panda.merge.dto.StandardSportTypeDTO;
import com.panda.merge.mapper.StandardSportTypeMapper;
import com.panda.merge.model.StandardSportType;
import com.panda.merge.model.StandardSportTypeExample;
import com.panda.merge.service.StandardSportTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <Description> 标准运动类型信息（含多语言）
 * @author      tell
 * @since       2020年9月10日14:07:09
 */
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class StandardSportTypeServiceImpl implements StandardSportTypeService {

    @Autowired
    private StandardSportTypeMapper standardSportTypeMapper;

    @Override
    public List<StandardSportType> getItemListByModifyTime(StandardSportTypeDTO parDto){
        StandardSportTypeExample example = new StandardSportTypeExample();
        example.setOrderByClause("modify_time desc");
        if(null == parDto.getEndTime()){
            example.createCriteria().andModifyTimeGreaterThanOrEqualTo(parDto.getBeginTime());
        }else{
            example.createCriteria().andModifyTimeGreaterThanOrEqualTo(parDto.getBeginTime()).andModifyTimeLessThanOrEqualTo(parDto.getEndTime());
        }
        return standardSportTypeMapper.selectByExample(example);
    }
}
