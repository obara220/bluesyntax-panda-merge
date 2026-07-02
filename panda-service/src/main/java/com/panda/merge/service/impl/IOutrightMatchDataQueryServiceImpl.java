package com.panda.merge.service.impl;

import com.panda.merge.config.RedisConfig;
import com.panda.merge.dto.OutrightMatchInfoDTO;
import com.panda.merge.mapper.StandardOutrightMatchInfoMapper;
import com.panda.merge.model.StandardOutrightMatchInfo;
import com.panda.merge.model.StandardOutrightMatchInfoExample;
import com.panda.merge.service.IOutrightMatchDataQueryService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author : nonhung
 * @project Name : panda-merge
 * @package Name : com.panda.merge.service.impl
 * @description : TODO
 * @date: 2020-10-02 12:01
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */

@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class IOutrightMatchDataQueryServiceImpl extends BaseServiceImpl<StandardOutrightMatchInfo> implements IOutrightMatchDataQueryService {

    @Resource
    private StandardOutrightMatchInfoMapper standardOutrightMatchInfoMapper;

    @Override
    public List<StandardOutrightMatchInfo> queryOutrightMatch(OutrightMatchInfoDTO parDto) {
        StandardOutrightMatchInfoExample example = new StandardOutrightMatchInfoExample();
        example.setOrderByClause("modify_time desc");
        if(null == parDto.getEndTime()){
            example.createCriteria().andModifyTimeGreaterThanOrEqualTo(parDto.getBeginTime());
        }else{
            example.createCriteria().andModifyTimeGreaterThanOrEqualTo(parDto.getBeginTime()).andModifyTimeLessThanOrEqualTo(parDto.getEndTime());
        }
        return standardOutrightMatchInfoMapper.selectByExample(example);
    }

}
