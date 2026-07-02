package com.panda.merge.service.impl;

import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.dao.ConfigOutrightTradeMarketDao;
import com.panda.merge.dto.OutrightTradeMarketConfigDTO;
import com.panda.merge.mapper.ConfigOutrightTradeMarketMapper;
import com.panda.merge.model.ConfigOutrightTradeMarket;
import com.panda.merge.model.ConfigOutrightTradeMarketExample;
import com.panda.merge.service.OutrightTradeMarketConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
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
public class OutrightTradeMarketConfigServiceImpl implements OutrightTradeMarketConfigService {

    @Autowired
    private ConfigOutrightTradeMarketMapper configOutrightTradeMarketMapper;

    @Autowired
    private ConfigOutrightTradeMarketDao configOutrightTradeMarketDao;

    @Override
    @Caching(put = @CachePut(key = "'ConfigOutrightTradeMarket:' +#outrightTradeMarketConfigDTO.standardMatchId+'-'+#outrightTradeMarketConfigDTO.standardMarketId"),
            evict = @CacheEvict(key = "'ConfigTradeMarketByMatchId:' +#outrightTradeMarketConfigDTO.standardMatchId"))
    public ConfigOutrightTradeMarket insertItem(String linkId, OutrightTradeMarketConfigDTO outrightTradeMarketConfigDTO) {
        ConfigOutrightTradeMarket configOutrightTradeMarket = new ConfigOutrightTradeMarket();
        configOutrightTradeMarket.setId(UUIdUtils.getId());
        configOutrightTradeMarket.setStandardMatchId(outrightTradeMarketConfigDTO.getStandardMatchId());
        configOutrightTradeMarket.setStandardMarketId(outrightTradeMarketConfigDTO.getStandardMarketId());
        configOutrightTradeMarket.setMarketStatus(outrightTradeMarketConfigDTO.getMarketStatus());
        configOutrightTradeMarket.setLinkId(linkId);
        configOutrightTradeMarket.setOperaterId(outrightTradeMarketConfigDTO.getOperaterId());
        configOutrightTradeMarket.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
        configOutrightTradeMarket.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        configOutrightTradeMarketMapper.insert(configOutrightTradeMarket);
        return configOutrightTradeMarket;
    }

    @Override
    @CacheEvict(key = "'ConfigOutrightTradeMarket:' +#configOutrightTradeMarket.standardMatchId+'-'+#configOutrightTradeMarket.standardMarketId")
    public ConfigOutrightTradeMarket updateItem(ConfigOutrightTradeMarket configOutrightTradeMarket) {
        configOutrightTradeMarketMapper.updateByPrimaryKeySelective(configOutrightTradeMarket);
        return configOutrightTradeMarket;
    }

    @Override
    @Cacheable(key = "'ConfigOutrightTradeMarket:' +#standardMatchId+'-'+#standardMarketId")
    public ConfigOutrightTradeMarket selectItem(Long standardMatchId,Long standardMarketId) {
        ConfigOutrightTradeMarketExample example = new ConfigOutrightTradeMarketExample();
        example.createCriteria().andStandardMatchIdEqualTo(standardMatchId).andStandardMarketIdEqualTo(standardMarketId);
        List<ConfigOutrightTradeMarket> configOutrightTradeMarketList = configOutrightTradeMarketMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(configOutrightTradeMarketList)){
            return null;
        }
        return configOutrightTradeMarketList.get(0);
    }


    @Override
    @Cacheable(key = "'ConfigTradeMarketByMatchId:' +#standardMatchId")
    public List<ConfigOutrightTradeMarket> selectListItem(Long standardMatchId) {
        ConfigOutrightTradeMarketExample example = new ConfigOutrightTradeMarketExample();
        example.createCriteria().andStandardMatchIdEqualTo(standardMatchId);
        List<ConfigOutrightTradeMarket> configOutrightTradeMarketList = configOutrightTradeMarketMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(configOutrightTradeMarketList)){
            return null;
        }
        return configOutrightTradeMarketList;
    }

    @Override
    public Map<Long, Integer> queryTradeStatus(List<Long> matchIds) {
        ConfigOutrightTradeMarketExample example = new ConfigOutrightTradeMarketExample();
        example.createCriteria().andStandardMatchIdIn(matchIds);
        List<ConfigOutrightTradeMarket> configOutrightTradeMarketList = configOutrightTradeMarketMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(configOutrightTradeMarketList)){
            return null;
        }
        return configOutrightTradeMarketList.stream().collect(Collectors.toMap(ConfigOutrightTradeMarket::getStandardMarketId, ConfigOutrightTradeMarket::getMarketStatus));
    }

    @Override
    public void updateBatchById(List<ConfigOutrightTradeMarket> configOutrightTradeMarkets) {
        configOutrightTradeMarketDao.updateBatchById(configOutrightTradeMarkets);
    }



    @Override
    @CacheEvict(key = "'ConfigTradeMarketByMatchId:' + #configOutrightTradeMarketList[0].standardMatchId")
    public void saveBatch(List<ConfigOutrightTradeMarket> configOutrightTradeMarketList) {
        configOutrightTradeMarketDao.saveBatch(configOutrightTradeMarketList);
    }


}
