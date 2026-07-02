package com.panda.merge.rocketmq.processor;

import com.alibaba.fastjson.JSON;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.MatchMarketConfigurationMessage;
import com.panda.merge.dto.message.MatchMarketEventConfigurationMessage;
import com.panda.merge.model.ConfigurationMatchDataSource;
import com.panda.merge.model.ConfigurationMatchTemplateEvent;
import com.panda.merge.service.ConfigurationMatchDataSourceService;
import com.panda.merge.service.ConfigurationMatchTemplateEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Description :  数据源权重及事件审核的配置
 * @author :  Riben
 * @since :  2020年12月9日13:43:05
 */
@Slf4j
@Component
@Validated
public class MatchMarketConfigruationProcessor {
    @Autowired
    private ConfigurationMatchDataSourceService matchDataSourceService;

    @Autowired
    private ConfigurationMatchTemplateEventService matchTemplateEventService;


    @Transactional(rollbackFor = Exception.class)
    public void handleMatchMarketConfigurationData(@Valid Request<MatchMarketConfigurationMessage> matchMarketConfigurationMessageRequest) {
        MatchMarketConfigurationMessage message = matchMarketConfigurationMessageRequest.getData();
        Long standardMatchId = message.getStandardMatchId();
        String linkId = matchMarketConfigurationMessageRequest.getLinkId();
        Integer marketType = message.getMarketType();
        log.info("::{}::开始处理赛事开盘数据权重及事件配置, Request: {}", linkId, JSON.toJSONString(matchMarketConfigurationMessageRequest));
        ConfigurationMatchDataSource existDataSource = matchDataSourceService.getRecByMatchIdAndMarketType(standardMatchId, marketType);
        Long now = TimeUtils.millsSecondsEast8ZoneGmt();
        if(existDataSource == null){
            log.info("::{}::赛事id：{}，盘口类型{}赛事配置数据{}！DB 中无数据，开始保存！",linkId, standardMatchId, marketType, JSON.toJSONString(message));
            ConfigurationMatchDataSource dataSource = new ConfigurationMatchDataSource();
            BeanUtils.copyProperties(message, dataSource);
            dataSource.setMarketType(message.getMarketType());
            dataSource.setCreateTime(now);
            dataSource.setModifyTime(now);
            matchDataSourceService.save(dataSource);
        }else{
            existDataSource.setSrWeight(message.getSrWeight());
            existDataSource.setBcWeight(message.getBcWeight());
            existDataSource.setBgWeight(message.getBgWeight());
            existDataSource.setTxWeight(message.getTxWeight());
            existDataSource.setScoreSource(message.getScoreSource());
            existDataSource.setModifyTime(now);
            matchDataSourceService.update(existDataSource);
        }
        log.info("::{}::赛事id：{}，盘口类型{}赛事开盘数据源配置处理完毕！",linkId, standardMatchId, marketType);

        List<MatchMarketEventConfigurationMessage> templateEventList = message.getTemplateEventList();
        if(CollectionUtils.isEmpty(templateEventList)){
            log.info("::{}::赛事id：{}，盘口类型{}无事件审核配置！赛事开盘数据源配置处理结束",linkId, standardMatchId, marketType);
            return;
        }
        log.info("::{}::赛事id：{}，盘口类型{}，开始处理赛事开盘事件审核配置！",linkId, standardMatchId, marketType);
        List<ConfigurationMatchTemplateEvent> existEventConfigurations = matchTemplateEventService.getRecsByMatchId(standardMatchId);
        if(existEventConfigurations != null){
            Set<String> exstEventCodes = existEventConfigurations.stream().map(ConfigurationMatchTemplateEvent :: getEventCode).collect(Collectors.toSet());
            List<ConfigurationMatchTemplateEvent> updateConfigurations = new ArrayList<>();
            templateEventList.stream().filter(e -> exstEventCodes.contains(e.getEventCode())).forEach(e-> updateConfigurations.add(constructMatchTemplateEvent(e, standardMatchId, false)));
            matchTemplateEventService.batchUpdate(updateConfigurations);
            //过滤掉更新的记录，
            templateEventList = templateEventList.stream().filter(e -> !exstEventCodes.contains(e.getEventCode())).collect(Collectors.toList());
        }
        //若过滤完数据库里包含的数据还有其他事件审核配置则新增
        if(!CollectionUtils.isEmpty(templateEventList)){
            List<ConfigurationMatchTemplateEvent> eventConfigurations = new ArrayList<>();
            templateEventList.forEach(e -> eventConfigurations.add(constructMatchTemplateEvent(e, standardMatchId, true)));
            matchTemplateEventService.batchSave(eventConfigurations);
        }
        log.info("::{}::赛事id：{}，盘口类型{}，处理赛事开盘数据权重事件审核配置完成！",linkId, standardMatchId, marketType);
    }

    private ConfigurationMatchTemplateEvent constructMatchTemplateEvent(MatchMarketEventConfigurationMessage message, Long standardMatchId, boolean newFlag){
        ConfigurationMatchTemplateEvent eventConfiguration = new ConfigurationMatchTemplateEvent();
        eventConfiguration.setStandardMatchId(standardMatchId);
        eventConfiguration.setEventCode(message.getEventCode());
        eventConfiguration.setEventAuditTime(message.getEventHandleTime());
        eventConfiguration.setEventSettlementTime(message.getSettleHandleTime());
        Long now = TimeUtils.millsSecondsEast8ZoneGmt();
        if(newFlag){
            eventConfiguration.setCreateTime(now);
        }
        eventConfiguration.setModifyTime(now);
        return eventConfiguration;
    }


}
