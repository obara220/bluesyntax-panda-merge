package com.panda.merge.service.impl;

import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.dto.OutrightTradeProbabilityConfigDTO;
import com.panda.merge.mapper.ConfigOutrightTradeProbabilityMapper;
import com.panda.merge.model.ConfigOutrightTradeProbability;
import com.panda.merge.model.ConfigOutrightTradeProbabilityExample;
import com.panda.merge.service.OutrightTradeProbabilityConfigService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/21 <br>
 */
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class OutrightTradeProbabilityConfigServiceImpl implements OutrightTradeProbabilityConfigService {

    @Autowired
    private ConfigOutrightTradeProbabilityMapper configOutrightTradeProbabilityMapper;

    @Override
    @CachePut(key = "'ConfigOutrightTradeProbability:' +#outrightTradeProbabilityConfigDTO.standardMatchId+'-'+#outrightTradeProbabilityConfigDTO.standardMarketOddsId")
    public ConfigOutrightTradeProbability insertItem(String linkId, OutrightTradeProbabilityConfigDTO outrightTradeProbabilityConfigDTO) {
        ConfigOutrightTradeProbability configOutrightTradeProbability = new ConfigOutrightTradeProbability();
        BeanUtils.copyProperties(outrightTradeProbabilityConfigDTO, configOutrightTradeProbability);
        configOutrightTradeProbability.setId(UUIdUtils.getId());
        configOutrightTradeProbability.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
        configOutrightTradeProbability.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        configOutrightTradeProbability.setLinkId(linkId);
        configOutrightTradeProbabilityMapper.insertSelective(configOutrightTradeProbability);
        return configOutrightTradeProbability;
    }

    @Override
    @CacheEvict(key = "'ConfigOutrightTradeProbability:' +#configOutrightTradeProbability.standardMatchId+'-'+#configOutrightTradeProbability.standardMarketOddsId")
    public ConfigOutrightTradeProbability updateItem(ConfigOutrightTradeProbability configOutrightTradeProbability) {
        configOutrightTradeProbabilityMapper.updateByPrimaryKeySelective(configOutrightTradeProbability);
        return configOutrightTradeProbability;
    }

    @Override
    @Cacheable(key = "'ConfigOutrightTradeProbability:' +#standardMatchId+'-'+#standardMarketOddsId")
    public ConfigOutrightTradeProbability selectItem(Long standardMatchId, Long standardMarketOddsId) {
        ConfigOutrightTradeProbabilityExample example = new ConfigOutrightTradeProbabilityExample();
        example.createCriteria().andStandardMatchIdEqualTo(standardMatchId).andStandardMarketOddsIdEqualTo(standardMarketOddsId);
        List<ConfigOutrightTradeProbability> configOutrightTradeProbabilityList = configOutrightTradeProbabilityMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(configOutrightTradeProbabilityList)){
            return null;
        }
        return configOutrightTradeProbabilityList.get(0);
    }

    @Override
    public List<ConfigOutrightTradeProbability> getItemList(Long standardMatchId, Long standardMarketId) {
        ConfigOutrightTradeProbabilityExample example = new ConfigOutrightTradeProbabilityExample();
        example.createCriteria().andStandardMatchIdEqualTo(standardMatchId).andStandardMarketIdEqualTo(standardMarketId);
        List<ConfigOutrightTradeProbability> configOutrightTradeProbabilityList = configOutrightTradeProbabilityMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(configOutrightTradeProbabilityList)){
            return null;
        }
        return configOutrightTradeProbabilityList;
    }

    @Override
    @CacheEvict(key = "'ConfigOutrightTradeProbability:' +#configOutrightTradeProbability.standardMatchId+'-'+#configOutrightTradeProbability.standardMarketOddsId")
    public void del(ConfigOutrightTradeProbability configOutrightTradeProbability) {
        configOutrightTradeProbabilityMapper.deleteByPrimaryKey(configOutrightTradeProbability.getId());
    }
}
