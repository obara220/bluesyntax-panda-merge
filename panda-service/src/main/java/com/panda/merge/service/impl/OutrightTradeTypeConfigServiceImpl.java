package com.panda.merge.service.impl;

import com.google.common.collect.Lists;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.dto.OutrightTradeTypeConfigDTO;
import com.panda.merge.mapper.ConfigOutrightTradeTypeMapper;
import com.panda.merge.model.ConfigOutrightTradeType;
import com.panda.merge.model.ConfigOutrightTradeTypeExample;
import com.panda.merge.service.OutrightTradeTypeConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <Description> <br>
 *
 * @author raulvii<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/21 <br>
 */
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class OutrightTradeTypeConfigServiceImpl implements OutrightTradeTypeConfigService {

    @Autowired
    private ConfigOutrightTradeTypeMapper configOutrightTradeTypeMapper;


    @Override
    public ConfigOutrightTradeType insertItem(String linkId, OutrightTradeTypeConfigDTO outrightTradeTypeConfigDTO) {
        ConfigOutrightTradeType configOutrightTradeType = new ConfigOutrightTradeType();
        configOutrightTradeType.setId(UUIdUtils.getId());
        configOutrightTradeType.setLevel(outrightTradeTypeConfigDTO.getLevel());
        configOutrightTradeType.setStandardMatchId(outrightTradeTypeConfigDTO.getStandardMatchId());
        configOutrightTradeType.setStandardMarketId(outrightTradeTypeConfigDTO.getStandardMarketId());
        configOutrightTradeType.setTradeType(outrightTradeTypeConfigDTO.getTradeType());
        configOutrightTradeType.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
        configOutrightTradeType.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        configOutrightTradeType.setOperaterId(outrightTradeTypeConfigDTO.getOperaterId());
        configOutrightTradeTypeMapper.insertSelective(configOutrightTradeType);
        return configOutrightTradeType;
    }

    @Override
    //@CachePut(key = "'OutrightTradeType:' + #configOutrightTradeType.level + '-' + #configOutrightTradeType.standardMarketId")
    public ConfigOutrightTradeType updateItem(ConfigOutrightTradeType configOutrightTradeType) {
        configOutrightTradeTypeMapper.updateByPrimaryKey(configOutrightTradeType);
        return configOutrightTradeType;
    }

    @Override
    //@Cacheable(key = "'OutrightTradeType:' + #configOutrightTradeType.level + '-' + #configOutrightTradeType.standardMarketId",unless = "#result == null ")
    public ConfigOutrightTradeType selectItem(OutrightTradeTypeConfigDTO outrightTradeTypeConfigDTO) {
        ConfigOutrightTradeTypeExample example = new ConfigOutrightTradeTypeExample();
        example.createCriteria().andStandardMarketIdEqualTo(outrightTradeTypeConfigDTO.getStandardMarketId()).andLevelEqualTo(outrightTradeTypeConfigDTO.getLevel());
        List<ConfigOutrightTradeType> configOutrightTradeTypeList = configOutrightTradeTypeMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(configOutrightTradeTypeList)){
            return null;
        }
        return configOutrightTradeTypeList.get(0);
    }

    @Override
    public Map<Long, Integer> getTradeTypeMapByMatchId(Long standardMatchId, Set<Long> marketIdSet) {
        List<Long> marketIdList = new ArrayList<Long>();
        marketIdSet.forEach(e -> marketIdList.add(e));
        ConfigOutrightTradeTypeExample example = new ConfigOutrightTradeTypeExample();
        example.createCriteria().andStandardMatchIdEqualTo(standardMatchId).andStandardMarketIdIn(marketIdList);
        List<ConfigOutrightTradeType> configOutrightTradeTypeList = configOutrightTradeTypeMapper.selectByExample(example);
        Map<Long, Integer> tradeTypeMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(configOutrightTradeTypeList)) {
            configOutrightTradeTypeList.forEach(configOutrightTradeType -> {
                Integer tradeType = 0;
                if (null != configOutrightTradeType){
                    tradeType = configOutrightTradeType.getTradeType();
                }
                tradeTypeMap.put(configOutrightTradeType.getStandardMarketId(), tradeType);
            });
        }
        return tradeTypeMap;
    }


    @Override
    public List<ConfigOutrightTradeType> getTradeTypeList( List<Long> marketIds) {
        ConfigOutrightTradeTypeExample example = new ConfigOutrightTradeTypeExample();
        example.createCriteria().andStandardMarketIdIn(marketIds);
        List<ConfigOutrightTradeType> configOutrightTradeTypeList = configOutrightTradeTypeMapper.selectByExample(example);
        if( CollectionUtils.isEmpty(configOutrightTradeTypeList)){
            return null;
        }
        return configOutrightTradeTypeList;
    }

    @Override
    public List<ConfigOutrightTradeType> getTradeTypeList(Long standardMatchId, List<Long> marketIds, Integer tradeType) {
        ConfigOutrightTradeTypeExample example = new ConfigOutrightTradeTypeExample();
        example.createCriteria().andStandardMatchIdEqualTo(standardMatchId).andStandardMarketIdIn(marketIds).andTradeTypeEqualTo(tradeType);
        List<ConfigOutrightTradeType> configOutrightTradeTypeList = configOutrightTradeTypeMapper.selectByExample(example);
        if( CollectionUtils.isEmpty(configOutrightTradeTypeList)){
            return null;
        }
        return configOutrightTradeTypeList;
    }


    @Override
    public Map<Long, Integer> getTradeTypeMapByMatchIds(Map<Long, Set<Long>> matchAndmarketIdsMap) {
        ConfigOutrightTradeTypeExample example = new ConfigOutrightTradeTypeExample();
        for(Map.Entry<Long, Set<Long>> entry : matchAndmarketIdsMap.entrySet()) {
            example.or().andStandardMatchIdEqualTo(entry.getKey()).andStandardMarketIdIn((new ArrayList<>(entry.getValue())));
        }
        List<ConfigOutrightTradeType> configOutrightTradeTypeList = configOutrightTradeTypeMapper.selectByExample(example);
//        return configOutrightTradeTypeList.stream().collect(Collectors.toMap(t->t.getStandardMatchId()+"-"+t.getStandardMarketId(),
//                ConfigOutrightTradeType::getTradeType, (v1, v2) -> v1));
        if ( CollectionUtils.isEmpty(configOutrightTradeTypeList) ) {
            return null;
        }
        return configOutrightTradeTypeList.stream().collect(Collectors.toMap(ConfigOutrightTradeType::getStandardMarketId, ConfigOutrightTradeType::getTradeType));
    }
}
