package com.panda.merge.service.impl;

import com.panda.merge.config.RedisConfig;
import com.panda.merge.mapper.StandardOutrightMatchInfoMapper;
import com.panda.merge.model.StandardOutrightMatchInfo;
import com.panda.merge.model.StandardOutrightMatchInfoExample;
import com.panda.merge.service.StandardOutrightMatchInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * <Description> 三方冠军赛事信息
 * @author      tell
 * @since       2020年9月10日10:35:50
 */
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class StandardOutrightMatchInfoServiceImpl extends BaseServiceImpl<StandardOutrightMatchInfo> implements StandardOutrightMatchInfoService {

    @Autowired
    private StandardOutrightMatchInfoMapper standardOutrightMatchInfoMapper;

    @Override
    @Cacheable(key = "'StandardOutrightMatchInfo:' + #dataSourceCode +  '-' + #thirdMatchSourceId", unless = "#result == null ")
    public StandardOutrightMatchInfo getItem(String dataSourceCode, Long thirdMatchSourceId) {
        StandardOutrightMatchInfoExample example = new StandardOutrightMatchInfoExample();
        example.createCriteria().andDataSourceCodeEqualTo(dataSourceCode).
                andThirdOutrightMatchIdEqualTo(thirdMatchSourceId);
        List<StandardOutrightMatchInfo> StandardOutrightMatchInfos = standardOutrightMatchInfoMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(StandardOutrightMatchInfos)) {
            return null;
        }
        return StandardOutrightMatchInfos.get(0);
    }

    @Override
    //@Cacheable(key = "'StandardOutrightMatchInfo:' + #matchId", unless = "#result == null ")
    public StandardOutrightMatchInfo getItem(Long matchId) {
        return standardOutrightMatchInfoMapper.selectByPrimaryKey(matchId);
    }

    @Override
    public List<StandardOutrightMatchInfo> getItems(List<Long> matchIds) {
        if(CollectionUtils.isEmpty(matchIds)){
            return Collections.emptyList();
        }
        StandardOutrightMatchInfoExample example = new StandardOutrightMatchInfoExample();
        example.createCriteria().andIdIn(matchIds);
        return standardOutrightMatchInfoMapper.selectByExample(example);
    }

    @Override
    @CacheEvict(key = "'StandardOutrightMatchInfo:'+#item.dataSourceCode+'-'+#item.thirdOutrightMatchId")
    public StandardOutrightMatchInfo updateByPrimaryKeySelective(StandardOutrightMatchInfo item){
        standardOutrightMatchInfoMapper.updateByPrimaryKeySelective(item);
        return item;
    }



}
