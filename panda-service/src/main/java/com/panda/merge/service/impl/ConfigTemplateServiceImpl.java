package com.panda.merge.service.impl;

import com.panda.merge.config.RedisConfig;
import com.panda.merge.dto.TradeTournamentConfigDTO;
import com.panda.merge.mapper.ConfigTemplateMapper;
import com.panda.merge.model.ConfigTemplate;
import com.panda.merge.model.ConfigTemplateExample;
import com.panda.merge.service.ConfigTemplateService;
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
 * @Date: 2020-09-10 19:31
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class ConfigTemplateServiceImpl implements ConfigTemplateService {

    @Autowired
    private ConfigTemplateMapper configTemplateMapper;

    @Override
    public ConfigTemplate getTemplateRec(TradeTournamentConfigDTO tournamentConfigDTO) {
        ConfigTemplateExample query = new ConfigTemplateExample();
        //这里在模板类型为2时，不用联赛等级作为查询条件，以防止联赛的等级变更时，找不到对应模板
        if("2".equals(tournamentConfigDTO.getTemplateType()) ){
            query.createCriteria().andSportIdEqualTo(tournamentConfigDTO.getSportId())
                    .andMarketTypeEqualTo(tournamentConfigDTO.getMarketType())
                    .andTemplateTypeEqualTo(tournamentConfigDTO.getTemplateType()).andStandardTournamentIdEqualTo(tournamentConfigDTO.getStandardTournamentId());
        }else{
            //联赛等级默认模板
            query.createCriteria().andSportIdEqualTo(tournamentConfigDTO.getSportId())
                    .andMarketTypeEqualTo(tournamentConfigDTO.getMarketType())
                    .andTemplateTypeEqualTo(tournamentConfigDTO.getTemplateType()).andTournamentLevelEqualTo(tournamentConfigDTO.getTournamentLevel());
        }
        List<ConfigTemplate> result = configTemplateMapper.selectByExample(query);
        if(CollectionUtils.isEmpty(result)){
            return null;
        }
        return result.get(0);
    }

    @Override
    @Cacheable(key = "'ConfigTemplate:' + #templateId",unless = "#result == null ")
    public ConfigTemplate getTemplateRecByTemplateId(Long templateId) {
        ConfigTemplateExample query = new ConfigTemplateExample();
        query.createCriteria().andTemplateIdEqualTo(templateId);
        List<ConfigTemplate> result = configTemplateMapper.selectByExample(query);
        if(CollectionUtils.isEmpty(result)){
            return null;
        }
        return result.get(0);
    }

    @Override
    @CacheEvict(key = "'ConfigTemplate:' + #existConfigTemplate.templateId")
    public boolean updateByTemlateId(ConfigTemplate existConfigTemplate){
        return  configTemplateMapper.updateByPrimaryKeySelective(existConfigTemplate) > 0;
    }

    @Override
    @CachePut(key = "'ConfigTemplate:' + #configTemplate.templateId")
    public boolean save(ConfigTemplate configTemplate) {
        return configTemplateMapper.insertSelective(configTemplate) > 0;
    }
}
