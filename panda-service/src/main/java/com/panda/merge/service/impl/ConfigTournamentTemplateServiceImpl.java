package com.panda.merge.service.impl;

import com.panda.merge.common.utils.IdWorker;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.dto.TradeTournamentTemplateConfigDTO;
import com.panda.merge.mapper.ConfigTournamentTemplateMapper;
import com.panda.merge.model.ConfigTournamentTemplate;
import com.panda.merge.model.ConfigTournamentTemplateExample;
import com.panda.merge.service.ConfigTournamentTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author :  Riben
 * @Project Name :  panda-merge
 * @Package Name :  com.panda.merge.service.impl
 * @Description :  TODO
 * @Date: 2020-09-10 17:29
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class ConfigTournamentTemplateServiceImpl implements ConfigTournamentTemplateService {

    @Autowired
    private ConfigTournamentTemplateMapper mapper;


    @Override
    @Cacheable(key = "'ConfigTournamentTemplate:' + #tournamentId+'-'+#marketType",unless = "#result == null ")
    public List<ConfigTournamentTemplate> getTournamentTemplateInfoByIdAndMarketType(Long tournamentId, Integer marketType) {
        ConfigTournamentTemplateExample example = new ConfigTournamentTemplateExample();
        example.createCriteria().andStandardTournamentIdEqualTo(tournamentId).andMarketTypeEqualTo(marketType);
        List<ConfigTournamentTemplate> result = mapper.selectByExample(example);
        if(!CollectionUtils.isEmpty(result)){
            return result;
        }
        return null;
    }

    @Override
    @CachePut(key = "'ConfigTournamentTemplate:' + #relationParams.standardTournamentId+'-'+#relationParams.marketType")
    public ConfigTournamentTemplate save(TradeTournamentTemplateConfigDTO relationParams) {
        ConfigTournamentTemplate relationEntity = new ConfigTournamentTemplate();
        relationEntity.setId(IdWorker.getId());
        relationEntity.setStandardTournamentId(relationParams.getStandardTournamentId());
        relationEntity.setMarketType(relationParams.getMarketType());
        relationEntity.setTemplateId(relationParams.getTemplateId());
        Long now = TimeUtils.millsSecondsEast8ZoneGmt();
        relationEntity.setCreateTime(now);
        relationEntity.setModifyTime(now);
        mapper.insertSelective(relationEntity);
        return relationEntity;
    }

    @Override
    @CacheEvict(key = "'ConfigTournamentTemplate:' + #realtionEntity.standardTournamentId+'-'+#realtionEntity.marketType")
    public void update(ConfigTournamentTemplate realtionEntity) {
        mapper.updateByPrimaryKey(realtionEntity);
    }


}
