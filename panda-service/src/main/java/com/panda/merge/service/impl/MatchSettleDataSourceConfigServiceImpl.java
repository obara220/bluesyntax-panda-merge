package com.panda.merge.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.mapper.MatchSettleDataSourceConfigMapper;
import com.panda.merge.mapper.MatchSettleDataSourceSwitchMapper;
import com.panda.merge.mapper.StandardMatchInfoMapper;
import com.panda.merge.mapper.StandardSportTournamentMapper;
import com.panda.merge.model.*;
import com.panda.merge.service.MatchSettleDataSourceConfigService;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.service.StandardSportTournamentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;


@Slf4j
@Service
public class MatchSettleDataSourceConfigServiceImpl implements MatchSettleDataSourceConfigService {
    @Autowired
    private MatchSettleDataSourceConfigMapper matchSettleDataSourceConfigMapper;
    @Autowired
    MatchSettleDataSourceSwitchMapper matchSettleDataSourceSwitchMapper;

    @Autowired
    RedisService redisService;
    @Autowired
    StandardSportTournamentService standardSportTournamentService;
    @Autowired
    StandardMatchInfoService standardMatchInfoService;


    /**
     * 根据联赛等级查询出结算数据源列表
     * @param standardMatchId
     * @return
     */
    @Override
    public List<String> getTournamentLevelDataSources(Long standardMatchId) {

        List<String> dataSourceCodes = new LinkedList<>();
        try {
            //1、查询标准赛事对应的联赛Id,并根据联赛Id查询出联赛的等级
            StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardMatchId);
            if (standardMatchInfo != null && standardMatchInfo.getStandardTournamentId() != null) {
                StandardSportTournament standardSportTournament = standardSportTournamentService.getItem(standardMatchInfo.getStandardTournamentId());
                //2、查询联赛对应开启结算的数据商，并返回
                if (standardSportTournament != null && standardSportTournament.getTournamentLevel() != null) {
                    List<MatchSettleDataSourceConfig> matchSettleDataSourceConfigList = getMatchSettleDataSourceConfigTwo(standardSportTournament.getTournamentLevel(),standardMatchInfo.getSportId(),Constant.OUTRIGHT_ONE);
                    //3、循环出开启的数据商并返回
                    for (MatchSettleDataSourceConfig matchSettleDataSourceConfig : matchSettleDataSourceConfigList) {
                        if (!StringUtils.isAnyEmpty(matchSettleDataSourceConfig.getDataSourceCode())) {
                            dataSourceCodes.add(matchSettleDataSourceConfig.getDataSourceCode());
                        }
                    }
                }

            }
        } catch (Exception e) {
            log.error("::::根据标准赛事Id:{},结算查询联赛对应的数据源,异常信息:{}", standardMatchId, e);
        }
        return dataSourceCodes;
    }

    /**
     * 查询联赛等级对应的结算数据源的开关状态
     * @param standardMatchId
     * @return
     */
    @Override
    public Integer getTournamentLevelStatus(Long standardMatchId, String dataSourceCode,String eventCode) {

        Integer status = null;
        try {
            //1、查询标准赛事对应的联赛Id,并根据联赛Id查询出联赛的等级
            StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardMatchId);
            if (standardMatchInfo != null && standardMatchInfo.getStandardTournamentId() != null) {
                //3139需求 足球开关由结算设置控制
                if (standardMatchInfo.getSportId().equals(1L)){
                    List<MatchSettleDataSourceSwitch> switches = getMatchSettleDataSourceSwitch(standardMatchInfo.getSportId(),dataSourceCode);
                    if (!switches.isEmpty()){
                        MatchSettleDataSourceSwitch sourceSwitch = switches.get(0);
                        if(eventCode.equals("corner")) {
                            status = sourceSwitch.getCorner();
                        }else if(eventCode.equals("goal")||eventCode.equals("kick_off")){
                            status = sourceSwitch.getGoal();
                        }else {
                            status = sourceSwitch.getBooking();
                        }
                    }
                    if (dataSourceCode.equals("PA")){
                        status =1;
                    }
                }else if (standardMatchInfo.getSportId().equals(2L)){
                    List<MatchSettleDataSourceSwitch> switches = getMatchSettleDataSourceSwitch(standardMatchInfo.getSportId(),dataSourceCode);
                    if (!switches.isEmpty()){
                        MatchSettleDataSourceSwitch sourceSwitch = switches.get(0);
                        if(eventCode.equals("score_change")) {
                            status = sourceSwitch.getGoal();
                        }
                    }
                    if (dataSourceCode.equals("PA")){
                        status =1;
                    }
                } else {
                    StandardSportTournament standardSportTournament = standardSportTournamentService.getItem(standardMatchInfo.getStandardTournamentId());
                    //2、查询联赛对应开启结算的数据商
                    if (!Objects.isNull(standardSportTournament) && standardSportTournament.getTournamentLevel() != null) {
                        List<MatchSettleDataSourceConfig> matchSettleDataSourceConfigList = getMatchSettleDataSourceConfig(standardSportTournament.getTournamentLevel(),standardMatchInfo.getSportId(),dataSourceCode);
                        //3、查询数据商状态并返回
                        if (!matchSettleDataSourceConfigList.isEmpty()) {
                            MatchSettleDataSourceConfig matchSettleDataSourceConfig = matchSettleDataSourceConfigList.get(0);
                            status = matchSettleDataSourceConfig.getStatus();
                        }
                }



                }
            }
        } catch (Exception e) {
            log.error("::::根据标准赛事Id:{},结算查询联赛对应的数据源:{},状态异常信息:{}", standardMatchId,dataSourceCode, e);
        }
        return status;
    }

    public List<MatchSettleDataSourceSwitch> getMatchSettleDataSourceSwitch(Long sportId,String dataSource) {
        String key = "MATCH_SETTLE_DATA_SOURCE_SWITCH_INFO:"+sportId+"_"+dataSource;
        Object o = null;
        try{
            o = redisService.get(key);
        }catch (Exception e){
            log.error("redis读异常MatchSettleDataSourceSwitch：key:"+key, e);
        }
        List<MatchSettleDataSourceSwitch> switches  =null;
        if (o != null) {
            switches = JSONObject.parseArray(o.toString(), MatchSettleDataSourceSwitch.class);
            return switches;
        }else{
            MatchSettleDataSourceSwitchExample example = new MatchSettleDataSourceSwitchExample();
            example.createCriteria().andDataSourceCodeEqualTo(dataSource).andSportIdEqualTo(sportId);
            switches = matchSettleDataSourceSwitchMapper.selectByExample(example);
            if (null!= switches){
                try{
                    redisService.set(key,JSONObject.toJSON(switches),60 * 60 * 3);
                }catch (Exception e){
                    log.error("MatchSettleDataSourceSwitch:redis写入异常getMatchSettleDataSourceSwitch：key=[{}]StandardMatchInfo[{}]", key,JSONObject.toJSON(switches), e);
                }

            }
        }
        return switches;
    }

    public void forceUpdateSwitchRedis(Long sportId,String dataSource, List<MatchSettleDataSourceSwitch> switches) {
        String key = "MATCH_SETTLE_DATA_SOURCE_SWITCH_INFO:"+sportId+"_"+dataSource;
        redisService.set(key,JSONObject.toJSON(switches),60 * 60 * 3);
    }

    public List<MatchSettleDataSourceConfig> getMatchSettleDataSourceConfig(Integer level,Long sportId,String dataSourceCode) {
        String key = "MATCH_SETTLE_DATA_SOURCE_CONFIG:"+level+dataSourceCode+sportId;
        Object o = null;
        try{
            o = redisService.get(key);
        }catch (Exception e){
            log.error("redis读异常StandardMatchInfo：key:"+key, e);
        }
        List<MatchSettleDataSourceConfig> matchSettleDataSourceConfigList=null;
        if (o != null) {
            matchSettleDataSourceConfigList = JSONObject.parseArray(o.toString(), MatchSettleDataSourceConfig.class);
            return matchSettleDataSourceConfigList;
        }else{
            MatchSettleDataSourceConfigExample matchSettleDataSourceConfigExample = new MatchSettleDataSourceConfigExample();
            matchSettleDataSourceConfigExample.createCriteria().andTournamentLevelEqualTo(level)
                    .andSportIdEqualTo(sportId).andDataSourceCodeEqualTo(dataSourceCode);
           matchSettleDataSourceConfigList = matchSettleDataSourceConfigMapper.selectByExample(matchSettleDataSourceConfigExample);

            if (!CollectionUtils.isEmpty(matchSettleDataSourceConfigList)){
                try{
                    redisService.set(key,JSONObject.toJSON(matchSettleDataSourceConfigList),60 * 60 * 3);
                }catch (Exception e){
                    log.error("matchSettleDataSourceConfigList:redis写入异常matchSettleDataSourceConfigList：key=[{}]StandardMatchInfo[{}]", key,JSONObject.toJSON(matchSettleDataSourceConfigList), e);
                }

            }
        }
        return matchSettleDataSourceConfigList;
    }

    public List<MatchSettleDataSourceConfig> getMatchSettleDataSourceConfigTwo(Integer level,Long sportId,Integer status) {
        String key = "MATCH_SETTLE_DATA_SOURCE_CONFIG:"+level+"_"+status+"_"+sportId;
        Object o = null;
        try{
            o = redisService.get(key);
        }catch (Exception e){
            log.error("redis读异常StandardMatchInfo：key:"+key, e);
        }
        List<MatchSettleDataSourceConfig> matchSettleDataSourceConfigList=null;
        if (o != null) {
            matchSettleDataSourceConfigList = JSONObject.parseArray(o.toString(), MatchSettleDataSourceConfig.class);
            return matchSettleDataSourceConfigList;
        }else{
            MatchSettleDataSourceConfigExample matchSettleDataSourceConfigExample = new MatchSettleDataSourceConfigExample();
            matchSettleDataSourceConfigExample.createCriteria().andTournamentLevelEqualTo(level)
                    .andSportIdEqualTo(sportId).andStatusEqualTo(status);
            matchSettleDataSourceConfigExample.setOrderByClause("id");
            matchSettleDataSourceConfigList = matchSettleDataSourceConfigMapper.selectByExample(matchSettleDataSourceConfigExample);

            if (!CollectionUtils.isEmpty(matchSettleDataSourceConfigList)){
                try{
                    redisService.set(key,JSONObject.toJSON(matchSettleDataSourceConfigList),60 * 60 * 3);
                }catch (Exception e){
                    log.error("getMatchSettleDataSourceConfigTwo:redis写入异常matchSettleDataSourceConfigList：key=[{}]StandardMatchInfo[{}]", key,JSONObject.toJSON(matchSettleDataSourceConfigList), e);
                }

            }
        }
        return matchSettleDataSourceConfigList;
    }
}
