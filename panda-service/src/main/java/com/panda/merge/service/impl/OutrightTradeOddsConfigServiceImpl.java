package com.panda.merge.service.impl;

import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.dto.OutrightTradeOddsConfigDTO;
import com.panda.merge.mapper.ConfigOutrightTradeOddsMapper;
import com.panda.merge.model.ConfigOutrightTradeOdds;
import com.panda.merge.model.ConfigOutrightTradeOddsExample;
import com.panda.merge.service.OutrightTradeOddsConfigService;
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
public class OutrightTradeOddsConfigServiceImpl implements OutrightTradeOddsConfigService {

    @Autowired
    private ConfigOutrightTradeOddsMapper configOutrightTradeOddsMapper;

    @Override
    @CachePut(key = "'ConfigOutrightTradeOdds:' +#outrightTradeOddsConfigDTO.standardMatchId+'-'+#outrightTradeOddsConfigDTO.standardMarketOddsId")
    public ConfigOutrightTradeOdds insertItem(String linkId, OutrightTradeOddsConfigDTO outrightTradeOddsConfigDTO) {
        ConfigOutrightTradeOdds configOutrightTradeOdds = new ConfigOutrightTradeOdds();
        BeanUtils.copyProperties(outrightTradeOddsConfigDTO, configOutrightTradeOdds);
        configOutrightTradeOdds.setId(UUIdUtils.getId());
        configOutrightTradeOdds.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
        configOutrightTradeOdds.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        configOutrightTradeOdds.setLinkId(linkId);
        configOutrightTradeOddsMapper.insertSelective(configOutrightTradeOdds);
        return configOutrightTradeOdds;
    }

    @Override
    @CacheEvict(key = "'ConfigOutrightTradeOdds:' +#configOutrightTradeOdds.standardMatchId+'-'+#configOutrightTradeOdds.standardMarketOddsId")
    public ConfigOutrightTradeOdds updateItem(ConfigOutrightTradeOdds configOutrightTradeOdds) {
        configOutrightTradeOddsMapper.updateByPrimaryKeySelective(configOutrightTradeOdds);
        return configOutrightTradeOdds;
    }

    @Override
    @Cacheable(key = "'ConfigOutrightTradeOdds:' +#standardMatchId+'-'+#standardMarketOddsId")
    public ConfigOutrightTradeOdds selectItem(Long standardMatchId, Long standardMarketOddsId) {
        ConfigOutrightTradeOddsExample example = new ConfigOutrightTradeOddsExample();
        example.createCriteria().andStandardMatchIdEqualTo(standardMatchId).andStandardMarketOddsIdEqualTo(standardMarketOddsId);
        List<ConfigOutrightTradeOdds> configOutrightTradeOddsList = configOutrightTradeOddsMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(configOutrightTradeOddsList)){
            return null;
        }
        return configOutrightTradeOddsList.get(0);
    }
}
