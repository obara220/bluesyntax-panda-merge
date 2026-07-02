package com.panda.merge.service.impl;

import com.panda.merge.common.OddsWrapper;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdMatchMarketDTO;
import com.panda.merge.mapper.ThirdOutrightMatchInfoMapper;
import com.panda.merge.model.ThirdOutrightMatchInfo;
import com.panda.merge.model.ThirdOutrightMatchInfoExample;
import com.panda.merge.service.ThirdOutrightMatchInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <Description> 三方冠军赛事信息
 * @author      tell
 * @since       2020年9月10日10:35:50
 */
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class ThirdOutrightMatchInfoServiceImpl extends BaseServiceImpl<ThirdOutrightMatchInfo> implements ThirdOutrightMatchInfoService {

    @Autowired
    private ThirdOutrightMatchInfoMapper thirdOutrightMatchInfoMapper;

    @Override
    //@Cacheable(key = "'ThirdOutrightMatchInfo:' + #dataSourceCode +  '-' + #thirdOutrightSourceId", unless = "#result == null ")
    public ThirdOutrightMatchInfo getItem(String dataSourceCode, String thirdOutrightSourceId) {
        ThirdOutrightMatchInfoExample example = new ThirdOutrightMatchInfoExample();
        example.createCriteria().andDataSourceCodeEqualTo(dataSourceCode).
                andThirdOutrightSourceIdEqualTo(thirdOutrightSourceId);
        List<ThirdOutrightMatchInfo> ThirdOutrightMatchInfos = thirdOutrightMatchInfoMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(ThirdOutrightMatchInfos)) {
            return null;
        }
        return ThirdOutrightMatchInfos.get(0);
    }

    @Override
    public List<ThirdOutrightMatchInfo> getItems(List<OddsWrapper<ThirdMatchMarketDTO>> matchMarketDto) {
        if(CollectionUtils.isEmpty(matchMarketDto)) {
            return Collections.EMPTY_LIST;
        }
        ThirdOutrightMatchInfoExample example = new ThirdOutrightMatchInfoExample();
        for (OddsWrapper<ThirdMatchMarketDTO> match : matchMarketDto) {
            example.or().andDataSourceCodeEqualTo(match.getDataSourceCode()).
                    andThirdOutrightSourceIdEqualTo(match.getData().getThirdMatchSourceId());
        }
        List<ThirdOutrightMatchInfo> ThirdOutrightMatchInfos = thirdOutrightMatchInfoMapper.selectByExample(example);
        Map<String, ThirdOutrightMatchInfo> matchInfoMap = ThirdOutrightMatchInfos.stream().collect(Collectors.toMap(
                t->t.getDataSourceCode()+"-"+t.getThirdOutrightSourceId(), Function.identity(), (v1, v2)->v1));
        return matchInfoMap.values().stream().collect(Collectors.toList());
    }

    @Override
    public ThirdOutrightMatchInfo getItemByMatchId(Long standardMatchId, String dataSourcecode) {
        ThirdOutrightMatchInfoExample example = new ThirdOutrightMatchInfoExample();
        example.createCriteria().andDataSourceCodeEqualTo(dataSourcecode).andReferenceIdEqualTo(standardMatchId);
        List<ThirdOutrightMatchInfo> ThirdOutrightMatchInfos = thirdOutrightMatchInfoMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(ThirdOutrightMatchInfos)) {
            return null;
        }
        return ThirdOutrightMatchInfos.get(0);
    }

    @Override
    @CachePut(key = "'ThirdOutrightMatchInfo:'+#item.dataSourceCode+'-'+#item.thirdOutrightSourceId",unless="#result == null")
    public ThirdOutrightMatchInfo saveOrupdate(ThirdOutrightMatchInfo item){
        if(null != item.getCreateTime()){
            thirdOutrightMatchInfoMapper.insertSelective(item);
        }else{
            thirdOutrightMatchInfoMapper.updateByPrimaryKeySelective(item);
        }
        return item;
    }

    @Override
    public ThirdOutrightMatchInfo getItem(Long thirdMatchId, String dataSourceCode) {
        ThirdOutrightMatchInfoExample example = new ThirdOutrightMatchInfoExample();
        example.createCriteria().andDataSourceCodeEqualTo(dataSourceCode).andIdEqualTo(thirdMatchId);
        List<ThirdOutrightMatchInfo> ThirdOutrightMatchInfos = thirdOutrightMatchInfoMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(ThirdOutrightMatchInfos)) {
            return null;
        }
        return ThirdOutrightMatchInfos.get(0);
    }


}
