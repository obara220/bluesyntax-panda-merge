package com.panda.merge.service.impl;

import com.panda.merge.common.enums.Constant;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.dao.ConfigurationMatchTemplateEventDao;
import com.panda.merge.mapper.ConfigurationMatchTemplateEventMapper;
import com.panda.merge.model.ConfigurationMatchTemplateEvent;
import com.panda.merge.model.ConfigurationMatchTemplateEventExample;
import com.panda.merge.service.ConfigurationMatchTemplateEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author :  Riben
 * @Project Name :  panda-merge
 * @Package Name :  com.panda.merge.service.impl
 * @Description :  TODO
 * @Date: 2020-09-17 14:32
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
public class ConfigurationMatchTemplateEventServiceImpl implements ConfigurationMatchTemplateEventService {
    @Autowired
    private ConfigurationMatchTemplateEventMapper  matchTemplateEventMapper;

    @Autowired
    private ConfigurationMatchTemplateEventDao  matchTemplateEventDao;

    @Autowired
    private RedisService redisService;

    @Override
    public void batchSave(List<ConfigurationMatchTemplateEvent> eventConfigurations) {
        matchTemplateEventDao.insertBatch(eventConfigurations);
        this.batchCache(eventConfigurations);
    }

    @Override
    public List<ConfigurationMatchTemplateEvent> getRecsByMatchId(Long standardMatchId) {
        ConfigurationMatchTemplateEventExample query = new ConfigurationMatchTemplateEventExample();
        query.createCriteria().andStandardMatchIdEqualTo(standardMatchId);
        List<ConfigurationMatchTemplateEvent> eventConfigs = matchTemplateEventMapper.selectByExample(query);
        if(CollectionUtils.isEmpty(eventConfigs)){
            return null;
        }
        return eventConfigs;
    }

    @Override
    public void batchUpdate(List<ConfigurationMatchTemplateEvent> updateConfigurations) {
        matchTemplateEventDao.batchUpdate(updateConfigurations);
        this.batchCache(updateConfigurations);
    }

    /**
     * 批量缓存
     * @param eventConfigurations
     */
    private void batchCache(List<ConfigurationMatchTemplateEvent> eventConfigurations){
        Long standardMatchId = null;
        //审核时间key名
        String auditTime = "auditTime";
        //结算时间key名
        String settleTimekeyName = "settleHandleTime";
        for (ConfigurationMatchTemplateEvent eventConfiguration : eventConfigurations) {
            standardMatchId = eventConfiguration.getStandardMatchId();
            String configurationKey = Constant.REDIS_KEY.RONGHE_MATCH_MARKET_CONFIGURATION_EVENT + Long.toString(standardMatchId) + ":" +  eventConfiguration.getEventCode();
            redisService.hSet(configurationKey, auditTime, eventConfiguration.getEventAuditTime(), RedisConfig.REDIS_WEEK_TIME);
            redisService.hSet(configurationKey, settleTimekeyName, eventConfiguration.getEventSettlementTime(),RedisConfig.REDIS_WEEK_TIME);
        }
    }
}
