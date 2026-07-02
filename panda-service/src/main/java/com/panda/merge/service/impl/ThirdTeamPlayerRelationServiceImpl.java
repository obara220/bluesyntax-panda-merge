package com.panda.merge.service.impl;

import com.panda.merge.config.RedisConfig;
import com.panda.merge.dao.ThirdTeamPlayerRelationDao;
import com.panda.merge.mapper.ThirdTeamPlayerRelationMapper;
import com.panda.merge.model.ThirdTeamPlayerRelation;
import com.panda.merge.service.ThirdTeamPlayerRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * <Description> 球队球员关系信息
 * @author      tell
 * @since       2020年9月3日15:24:52
 */
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class ThirdTeamPlayerRelationServiceImpl extends BaseServiceImpl<ThirdTeamPlayerRelation> implements ThirdTeamPlayerRelationService {

    @Autowired
    private ThirdTeamPlayerRelationMapper thirdTeamPlayerRelationMapper;

    @Autowired
    private ThirdTeamPlayerRelationDao thirdTeamPlayerRelationDao;

    @Override
    public ThirdTeamPlayerRelation saveOrupdate(ThirdTeamPlayerRelation item){
        /** 根据创建时间来区分新增或修改（创建时间不为空是为新增）*/
        if(!Objects.isNull(item.getCreateTime())){
            thirdTeamPlayerRelationMapper.insertSelective(item);
        }else{
            if(!Objects.isNull(item.getModifyTime())){
                thirdTeamPlayerRelationMapper.updateByPrimaryKeySelective(item);
            }
        }
        return item;
    }
}
