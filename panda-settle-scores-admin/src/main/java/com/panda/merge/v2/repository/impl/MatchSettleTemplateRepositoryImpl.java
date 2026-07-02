package com.panda.merge.v2.repository.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.CommonConstant;
import com.panda.merge.dto.settle.SettleTemplateBatchUpdateDto;
import com.panda.merge.mapper.MatchSettleTemplateMapper;
import com.panda.merge.model.MatchSettleTemplate;
import com.panda.merge.model.MatchSettleTemplateExample;
import com.panda.merge.mq.producer.MatchSettleSPOddsProducer;
import com.panda.merge.v2.converter.MatchSettleTemplateConvert;
import com.panda.merge.v2.entity.MatchSettleTemplateEntity;
import com.panda.merge.v2.mapper.MatchSettleTemplateV2Mapper;
import com.panda.merge.v2.repository.MatchSettleTemplateRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.panda.merge.constant.RepositoryConstant.*;

@Slf4j
@Repository("MatchSettleTemplateRepositoryV2")
public class MatchSettleTemplateRepositoryImpl extends ServiceImpl<MatchSettleTemplateV2Mapper, MatchSettleTemplateEntity> implements MatchSettleTemplateRepository {

    @Autowired
    private RedisService redisService;
    @Autowired
    private MatchSettleTemplateV2Mapper matchSettleTemplateV2Mapper;
    @Autowired
    private MatchSettleTemplateConvert matchSettleTemplateConvert;
    @Autowired
    private MatchSettleTemplateMapper matchSettleTemplateMapper;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private MatchSettleSPOddsProducer matchSettleSPOddsProducer;

    @Override
    public List<MatchSettleTemplate> selectDiySettleTemplateByTypeAndName(Integer type, String templateName, Long sportId) {
        LambdaQueryWrapper<MatchSettleTemplateEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(MatchSettleTemplateEntity::getTournamentLevel, -1)
                .eq(sportId != null, MatchSettleTemplateEntity::getSportId, sportId)
                .eq(type != null, MatchSettleTemplateEntity::getTemplateType, type)
                .like(StringUtils.isNotBlank(templateName), MatchSettleTemplateEntity::getTemplateName, templateName);
        List<MatchSettleTemplateEntity> entities = this.list(queryWrapper);
        return matchSettleTemplateConvert.convertMatchSettleTemplate(entities);
    }

    @Override
    public List<MatchSettleTemplateEntity> selectByExample(MatchSettleTemplateExample example) {
        return matchSettleTemplateV2Mapper.selectByExample(example);
    }

    @Override
    public MatchSettleTemplate getByIdAndConvert(Long id) {
        return getMatchSettleTemplateByPrimaryKey(id);
    }


    @Override
    public MatchSettleTemplate getMatchSettleTemplateByTypeAndLevel(Integer type,Integer level, Long sportId){
        String key = "TEMPLATE_LEVEL"+level+"_TYPE"+type+"_SPORT_"+sportId;
        Object o = null;
        try{
            o = redisService.get(key);
        }catch (Exception e){
            log.error("redis读异常TemplateByTypeAndLevel：key=[{}]", key, e);
        }
        MatchSettleTemplate matchSettleTemplate =null;
        if (o != null) {
            matchSettleTemplate = JSONObject.toJavaObject(JSONObject.parseObject(o.toString()), MatchSettleTemplate.class);
            return matchSettleTemplate;
        }else{
            MatchSettleTemplateExample templateExample=new MatchSettleTemplateExample();
            templateExample.createCriteria().andTournamentLevelEqualTo(level).andTemplateTypeEqualTo(type).andSportIdEqualTo(sportId);
            List<MatchSettleTemplate> list = matchSettleTemplateMapper.selectByExample(templateExample);
            if (!list.isEmpty()){
                matchSettleTemplate = list.get(0);
                try {
                    redisService.set(key,JSONObject.toJSON(matchSettleTemplate),REDIS_THREE_TIME);
                }catch (Exception e){
                    log.error("getMatchSettleTemplateByTypeAndLevel:redis写入异常TemplateByTypeAndLevel：key=[{}]MatchSettleTemplate[{}]", key,JSONObject.toJSON(matchSettleTemplate), e);
                }

            }
        }
        return matchSettleTemplate;

    }

    @Override
    public  MatchSettleTemplate getMatchSettleTemplateByPrimaryKey(Long id){
        String key = TEMPLATE_ID+id;
        Object o = null;
        try {
            o = redisService.get(key);
        }catch (Exception e){
            log.error("redis读异常TemplateByPrimaryKey：key=[{}]", key, e);
        }

        MatchSettleTemplate matchSettleTemplate = null;
        if (o != null) {
            matchSettleTemplate = JSONObject.toJavaObject(JSONObject.parseObject(o.toString()), MatchSettleTemplate.class);
            if (null!=matchSettleTemplate){
                return matchSettleTemplate;
            }
        }else{
            MatchSettleTemplateEntity entity = super.getById(id);
            matchSettleTemplate = matchSettleTemplateConvert.convertMatchSettleTemplate(entity);
            if (null!=matchSettleTemplate){
                try {
                    redisService.set(key,JSONObject.toJSON(matchSettleTemplate),REDIS_THREE_TIME);
                }catch (Exception e){
                    log.error("getMatchSettleTemplateByPrimaryKey:redis写入异常TemplateByPrimaryKey：key=[{}]MatchSettleTemplate[{}]", key,JSONObject.toJSON(matchSettleTemplate), e);
                }

            }
        }
        return matchSettleTemplate;
    }

    @Override
    public void  insertOrUpdateTemplateToRedis(MatchSettleTemplateEntity matchSettleTemplate, boolean isInsert){
        String matchSettleTemplateKey = TEMPLATE_ID+matchSettleTemplate.getId();
        String matchSettleTemplateTypeAndLevelKey = "TEMPLATE_LEVEL"+matchSettleTemplate.getTournamentLevel()+"_TYPE"+matchSettleTemplate.getTemplateType()+"_SPORT_"+matchSettleTemplate.getSportId();
        try {
            Map<String, Object> map = new HashMap<>();
            map.put(matchSettleTemplateKey, JSONObject.toJSON(matchSettleTemplate));
            map.put(matchSettleTemplateTypeAndLevelKey, JSONObject.toJSON(matchSettleTemplate));
            redisService.mSetExpire(map,REDIS_THREE_TIME);
            applicationContext.getBean(MatchSettleTemplateRepositoryImpl.class).updateOrInsertAsync(matchSettleTemplate, isInsert);
        }catch (Exception e){
            log.error("insertMatchSettleTemplateByTypeAndLevel:redis新增异常TemplateByPrimaryKey：key=[{}]MatchSettleTemplate[{}]", matchSettleTemplateTypeAndLevelKey,JSONObject.toJSON(matchSettleTemplate), e);
            log.error("insertTemplateToRedis:redis新增异常TemplateByPrimaryKey：key=[{}]MatchSettleTemplate[{}]", matchSettleTemplateKey,JSONObject.toJSON(matchSettleTemplate), e);
        }
    }

    @Override
    public void insertOrUpdateTemplateToRedis(List<MatchSettleTemplateEntity> matchSettleTemplateList, boolean isInsert) {
        if (CollectionUtils.isEmpty(matchSettleTemplateList)){
            return;
        }
        String matchSettleTemplateKey=null;
        String matchSettleTemplateTypeAndLevelKey=null;
        MatchSettleTemplateEntity matchSettleTemplateLogObject = null;
        try {
            Map<String, Object> map = new HashMap<>();
            for(MatchSettleTemplateEntity matchSettleTemplate: matchSettleTemplateList){
                matchSettleTemplateLogObject = matchSettleTemplate;
                matchSettleTemplateKey = TEMPLATE_ID+matchSettleTemplate.getId();
                matchSettleTemplateTypeAndLevelKey = "TEMPLATE_LEVEL"+matchSettleTemplate.getTournamentLevel()+"_TYPE"+matchSettleTemplate.getTemplateType()+"_SPORT_"+matchSettleTemplate.getSportId();
                map.put(matchSettleTemplateKey, JSONObject.toJSON(matchSettleTemplate));
                map.put(matchSettleTemplateTypeAndLevelKey, JSONObject.toJSON(matchSettleTemplate));
            }
            redisService.mSetExpire(map,REDIS_THREE_TIME);
            applicationContext.getBean(MatchSettleTemplateRepositoryImpl.class).batchUpdateOrInsertAsync(matchSettleTemplateList, isInsert);
        } catch (Exception e){
            log.error("insertMatchSettleTemplateByTypeAndLevel:redis新增异常TemplateByPrimaryKey：key=[{}]MatchSettleTemplate[{}]", matchSettleTemplateTypeAndLevelKey,JSONObject.toJSON(matchSettleTemplateLogObject), e);
            log.error("insertTemplateToRedis:redis新增异常TemplateByPrimaryKey：key=[{}]MatchSettleTemplate[{}]", matchSettleTemplateKey,JSONObject.toJSON(matchSettleTemplateLogObject), e);
        }
    }

    @Override
    public void delTemplate(SettleTemplateBatchUpdateDto settleTemplateUpdateDto){
        String key = TEMPLATE_ID+settleTemplateUpdateDto.getTemplateId();
        String keyLevel = "TEMPLATE_LEVEL"+settleTemplateUpdateDto.getTournamentLevel()+"_TYPE"+settleTemplateUpdateDto.getTemplateType()+"_SPORT_"+settleTemplateUpdateDto.getSportId();
        try {
            redisService.del(Arrays.asList(key, keyLevel));
            applicationContext.getBean(MatchSettleTemplateRepositoryImpl.class).deleteByTemplateId(settleTemplateUpdateDto.getTemplateId());
        }catch (Exception e){
            log.error("deleteTemplate:redis删除异常TemplateByPrimaryKey：key=[{}]", key, e);
        }
    }

    boolean updateOrInsertAsync(MatchSettleTemplateEntity matchSettleTemplate, boolean isInsert){
        String linkId = "match-settle-template-"+matchSettleTemplate.getId();
        matchSettleSPOddsProducer.sendToSlaveMq(CommonConstant.SETTLE_SLAVE_DB_TOPIC, linkId, Arrays.asList(matchSettleTemplate),
                CommonConstant.SETTLE_TEMPLATE_TABLE, isInsert);
        return true;
    }

    boolean batchUpdateOrInsertAsync(List<MatchSettleTemplateEntity> matchSettleTemplateList, boolean isInsert){
        String linkId = "match-settle-template-batch-" + UUIdUtils.getId();
        List<Object> objects = matchSettleTemplateList.stream().map(t->(Object) t).collect(Collectors.toList());
        matchSettleSPOddsProducer.sendToSlaveMq(CommonConstant.SETTLE_SLAVE_DB_TOPIC, linkId, objects,
                CommonConstant.SETTLE_TEMPLATE_TABLE, isInsert);
        return true;
    }

    @Async("RemoveDBThreadPool")
    Boolean deleteByTemplateId(Long templateId){
        return this.removeById(templateId);
    }

}
