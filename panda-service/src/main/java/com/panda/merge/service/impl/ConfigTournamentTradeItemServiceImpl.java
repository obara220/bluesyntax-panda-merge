package com.panda.merge.service.impl;

import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.dto.ConfigTournamentTradeItemDTO;
import com.panda.merge.mapper.ConfigTournamentTradeItemMapper;
import com.panda.merge.model.ConfigTournamentTradeItem;
import com.panda.merge.model.ConfigTournamentTradeItemExample;
import com.panda.merge.service.ConfigTournamentTradeItemService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class ConfigTournamentTradeItemServiceImpl implements ConfigTournamentTradeItemService {

    @Autowired
    private ConfigTournamentTradeItemMapper configTournamentTradeItemMapper;

    @Override
    @Cacheable(key = "'ConfigTournamentTradeItem:' + #sportId+'-'+#tournamentId+'-'+#matchType", unless = "#result == null ")
    public ConfigTournamentTradeItem getItem(Long sportId, Long tournamentId, Integer matchType) {
        ConfigTournamentTradeItemExample configTournamentTradeItemExample = new ConfigTournamentTradeItemExample();
        configTournamentTradeItemExample.createCriteria()
                .andSportIdEqualTo(sportId) .andTournamentIdEqualTo(tournamentId).andMatchTypeEqualTo(matchType);
        List<ConfigTournamentTradeItem> configTournamentTradeItems = configTournamentTradeItemMapper.selectByExample(configTournamentTradeItemExample);
        if (CollectionUtils.isEmpty(configTournamentTradeItems)) {
            return null;
        }
        return configTournamentTradeItems.get(0);
    }

    @Override
    @CachePut(key = "'ConfigTournamentTradeItem:' + #dto.sportId + '-'+ #dto.tournamentId+ '-'+ #dto.matchType")
    public ConfigTournamentTradeItem create(ConfigTournamentTradeItemDTO dto) {
        ConfigTournamentTradeItem createItem = new ConfigTournamentTradeItem();
        BeanUtils.copyProperties(dto, createItem);
        createItem.setId(UUIdUtils.getId());
        createItem.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
        configTournamentTradeItemMapper.insert(createItem);
        return createItem;
    }

    @Override
    @CacheEvict(key = "'ConfigTournamentTradeItem:' + #item.sportId + '-'+ #item.tournamentId+ '-'+ #item.matchType")
    public void update(ConfigTournamentTradeItem item, ConfigTournamentTradeItemDTO dto) {
        ConfigTournamentTradeItem upItem = new ConfigTournamentTradeItem();
        BeanUtils.copyProperties(dto, upItem);
        upItem.setId(item.getId());
        upItem.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        configTournamentTradeItemMapper.updateByPrimaryKeySelective(upItem);
    }
}
