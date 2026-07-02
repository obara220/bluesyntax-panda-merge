package com.panda.merge.service.impl;

import com.google.common.collect.Lists;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.dao.StandardSportTeamDao;
import com.panda.merge.dto.StandardSportTeamDetail;
import com.panda.merge.mapper.StandardSportTeamMapper;
import com.panda.merge.model.LanguageInternation;
import com.panda.merge.model.StandardSportTeam;
import com.panda.merge.model.StandardSportTeamExample;
import com.panda.merge.service.LanguageInternationService;
import com.panda.merge.service.StandardSportTeamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 标准赛事信息 <br>
 * @author   tell
 * @since    2020年9月10日10:32:26
 */
@Slf4j
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class StandardSportTeamServiceImpl implements StandardSportTeamService {

    @Autowired
    private StandardSportTeamMapper standardSportTeamMapper;

    @Autowired
    private StandardSportTeamDao standardSportTeamDao;

    @Autowired
    private LanguageInternationService languageInternationService;

    @Override
    public List<StandardSportTeamDetail> getItemByStandardMatchId(Long standardMatchId){
        return setLanguages(standardSportTeamDao.getItemByStandardMatchId(standardMatchId));
    }

    @Override
    public List<StandardSportTeamDetail> getItemByStandardTeamIds(List<Long> standardTeamIds){
        if(CollectionUtils.isEmpty(standardTeamIds)){
            return new LinkedList<>();
        }
        return setLanguages(standardSportTeamDao.getItemByStandardTeamIds(standardTeamIds));
    }


    private List<StandardSportTeamDetail> setLanguages(List<StandardSportTeamDetail> list){
        if(!CollectionUtils.isEmpty(list)){
            Set<Long> nameCodes = list.stream().map(obj -> obj.getNameCode()).collect(Collectors.toSet());
            Map<Long, List<LanguageInternation>> nameCode2Languages = languageInternationService.getItemsByNameCodes(Lists.newArrayList(nameCodes));
            for (StandardSportTeamDetail item : list) {
                item.setIl8nNameList(nameCode2Languages.get(item.getNameCode()));
            }
        }
        return list;
    }

    @Override
    public StandardSportTeamDetail saveItem(StandardSportTeamDetail item,String linkId){
        //需要返回只增长的球队ID
        standardSportTeamDao.saveItem(item);
        //新增标准球队多语言信息
        for (LanguageInternation languageInternation: item.getIl8nNameList()) {
            languageInternation.setId(UUIdUtils.getId());
            languageInternation.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
            languageInternation.setNameCode(item.getNameCode());
            languageInternation.setDataSourceCode(DataSourceCodeEnum.PA.getCode());
            languageInternationService.saveOrupdate(languageInternation,linkId);
            log.info("linkId:{},标准球队多语言编辑完成,item:{}",linkId,item);
        }
        return item;
    }

    @Override
    @Cacheable(key = "'StandardSportTeam:' + #sportId+ '-' + #thirdTeamId", unless = "#result == null ")
    public StandardSportTeam getItemByThirdTeamId(Long sportId, Long thirdTeamId) {
        StandardSportTeamExample example = new StandardSportTeamExample();
        example.createCriteria().andSportIdEqualTo(sportId).andThirdTeamIdEqualTo(thirdTeamId);
        List<StandardSportTeam> list = standardSportTeamMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    /**
     * @param sportId
     * @param betRadarId
     * @param dataSourceCode
     * @return
     */
    @Override
    @Cacheable(key = "'StandardSportTeam:' + #sportId + '-' +  #dataSourceCode + '-' +#betRadarId" , unless = "#result == null ")
    public StandardSportTeam getStandardTeamByBetRadarId(Long sportId, String dataSourceCode, Integer betRadarId) {
        StandardSportTeamExample example = new StandardSportTeamExample();
        example.createCriteria().andSportIdEqualTo(sportId).andDataSourceCodeEqualTo(dataSourceCode).andBetRadarIdEqualTo(betRadarId);
        List<StandardSportTeam> list = standardSportTeamMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }


}
