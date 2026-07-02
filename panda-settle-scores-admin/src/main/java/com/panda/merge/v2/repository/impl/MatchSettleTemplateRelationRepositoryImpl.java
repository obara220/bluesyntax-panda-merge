package com.panda.merge.v2.repository.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.CommonConstant;
import com.panda.merge.dto.settle.MatchSettleTemplateTournamentDto;
import com.panda.merge.dto.settle.TemplateListSearchDto;
import com.panda.merge.mapper.SettleTemplateExtMappper;
import com.panda.merge.model.MatchSettleTemplateRelation;
import com.panda.merge.model.MatchSettleTemplateRelationExample;
import com.panda.merge.mq.producer.MatchSettleSPOddsProducer;
import com.panda.merge.v2.converter.MatchSettleTemplateRelationConvert;
import com.panda.merge.v2.entity.MatchSettleTemplateRelationEntity;
import com.panda.merge.v2.mapper.MatchSettleTemplateRelationV2Mapper;
import com.panda.merge.v2.repository.MatchSettleTemplateRelationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.panda.merge.constant.RepositoryConstant.REDIS_THREE_TIME;
import static com.panda.merge.constant.RepositoryConstant.TEMPLATE_RELATION;

@Slf4j
@Repository("MatchSettleTemplateRelationRepositoryV2")
public class MatchSettleTemplateRelationRepositoryImpl extends ServiceImpl<MatchSettleTemplateRelationV2Mapper, MatchSettleTemplateRelationEntity> implements MatchSettleTemplateRelationRepository {
    @Autowired
    private RedisService redisService;
    @Autowired
    private MatchSettleTemplateRelationV2Mapper matchSettleTemplateRelationV2Mapper;
    @Autowired
    private SettleTemplateExtMappper settleTemplateExtMappper;
    @Autowired
    private MatchSettleTemplateRelationConvert matchSettleTemplateRelationConvert;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private MatchSettleSPOddsProducer matchSettleSPOddsProducer;

    @Override
    public List<MatchSettleTemplateTournamentDto> list(TemplateListSearchDto templateListSearchDto) {
        return settleTemplateExtMappper.list(templateListSearchDto);
    }

    @Override
    public List<MatchSettleTemplateTournamentDto> listAndLevel(TemplateListSearchDto templateListSearchDto) {
        return settleTemplateExtMappper.listAndLevel(templateListSearchDto);
    }

    @Override
    public Integer listAndLevelTotal(TemplateListSearchDto templateListSearchDto) {
        return settleTemplateExtMappper.listAndLevelTotal(templateListSearchDto);
    }

    @Override
    public Integer listTotal(TemplateListSearchDto templateListSearchDto) {
        return settleTemplateExtMappper.listTotal(templateListSearchDto);
    }

    @Override
    public MatchSettleTemplateRelation getMatchSettleTemplateRelation(Long id) {
        String key = TEMPLATE_RELATION + id;
        Object o = null;
        try {
            o = redisService.get(key);
        } catch (Exception e) {
            log.error("redis读异常TemplateRelation：key:" + key, e);
        }
        MatchSettleTemplateRelationEntity matchSettleTemplateRelation = null;
        if (o != null) {
            matchSettleTemplateRelation = JSONObject.toJavaObject(JSONObject.parseObject(o.toString()), MatchSettleTemplateRelationEntity.class);
            return matchSettleTemplateRelationConvert.convertSettleTemplateRelation(matchSettleTemplateRelation);
        } else {
            MatchSettleTemplateRelationExample example = new MatchSettleTemplateRelationExample();
            example.createCriteria().andStandardTournamentIdEqualTo(id);
            List<MatchSettleTemplateRelationEntity> list = matchSettleTemplateRelationV2Mapper.selectByExample(example);
            if (!list.isEmpty()) {
                matchSettleTemplateRelation = list.get(0);
                try {
                    redisService.set(key, JSONObject.toJSON(matchSettleTemplateRelation), REDIS_THREE_TIME);
                } catch (Exception e) {
                    log.error("getMatchSettleTemplateRelation:redis写入异常TemplateRelation：key=[{}]TemplateRelation[{}]", key, JSONObject.toJSON(matchSettleTemplateRelation), e);
                }

            }
        }
        return matchSettleTemplateRelationConvert.convertSettleTemplateRelation(matchSettleTemplateRelation);
    }

    @Override
    public List<MatchSettleTemplateRelationEntity> selectByExample(MatchSettleTemplateRelationExample example) {
        return matchSettleTemplateRelationV2Mapper.selectByExample(example);
    }

    @Override
    public void updateBatchRelationWeightIdToLevel(Integer tournamentLevel) {
        List<MatchSettleTemplateRelation> list = settleTemplateExtMappper.selectTemplateRelationByLevel(tournamentLevel);
        list.forEach(t -> t.setTemplateSettleWeightId(null));
        batchInsertTemplateRelationToRedis(list);
        //matchSettleTemplateRelationV2Mapper.updateBatchRelationWeightIdToLevel(tournamentLevel);
    }

    @Override
    public void updateBatchRelationGrayIdToLevel(Integer tournamentLevel) {
        List<MatchSettleTemplateRelation> list = settleTemplateExtMappper.selectTemplateRelationByLevel(tournamentLevel);
        list.forEach(t -> t.setTemplateGrayAreaId(null));
        batchInsertTemplateRelationToRedis(list);
        //matchSettleTemplateRelationV2Mapper.updateBatchRelationGrayIdToLevel(tournamentLevel);
    }


    @Override
    public void updateBatchRelationWeightId(Integer tournamentLevel, Long templateId) {
        List<MatchSettleTemplateRelation> list = settleTemplateExtMappper.selectTemplateRelationByLevel(tournamentLevel);
        list.forEach(t -> t.setTemplateSettleWeightId(templateId));
        batchInsertTemplateRelationToRedis(list);
        //matchSettleTemplateRelationV2Mapper.updateBatchRelationWeightId(tournamentLevel, templateId);
    }


    @Override
    public void updateBatchRelationGrayId(Integer tournamentLevel, Long templateId) {
        List<MatchSettleTemplateRelation> list = settleTemplateExtMappper.selectTemplateRelationByLevel(tournamentLevel);
        list.forEach(t -> t.setTemplateGrayAreaId(templateId));
        batchInsertTemplateRelationToRedis(list);
        //matchSettleTemplateRelationV2Mapper.updateBatchRelationGrayId(tournamentLevel, templateId);
    }

    @Override
    public void delTemplateRelationByExample(MatchSettleTemplateRelationExample example) {
        List<MatchSettleTemplateRelationEntity> list = matchSettleTemplateRelationV2Mapper.selectByExample(example);
        List<String> keys = list.stream().map(t->TEMPLATE_RELATION + t.getId()).collect(Collectors.toList());
        try {
            redisService.del(keys);
            applicationContext.getBean(MatchSettleTemplateRelationRepositoryImpl.class).deleteByExample(example);
        } catch (Exception e) {
            log.error("delTemplateRelationByExample:redis插入异常：key:{} error:", keys, e);
        }

    }

    @Override
    public void insertTemplateRelationToRedis(MatchSettleTemplateRelationEntity matchSettleTemplateRelation, boolean isInsert) {
        String key = TEMPLATE_RELATION + matchSettleTemplateRelation.getId();
        try {
            redisService.set(key, JSONObject.toJSON(matchSettleTemplateRelation), REDIS_THREE_TIME);
            applicationContext.getBean(MatchSettleTemplateRelationRepositoryImpl.class).doInsertOrUpdate(matchSettleTemplateRelation, isInsert);
        } catch (Exception e) {
            log.error("insertTemplateRelationToRedis:redis插入异常：key:{} value:{} error:", key, JSONObject.toJSON(matchSettleTemplateRelation), e);
        }
    }

    @Override
    public void batchInsertTemplateRelationToRedis(List<MatchSettleTemplateRelation> list) {
        try {
            Map<String, Object> redisMap = list.stream().collect(Collectors.toMap(t -> TEMPLATE_RELATION + t.getId(), Function.identity(), (v1, v2) -> v1));
            redisService.mSetExpire(redisMap, REDIS_THREE_TIME);
            List<MatchSettleTemplateRelationEntity> listEntities = matchSettleTemplateRelationConvert.convertSettleTemplateRelationToEntity(list);
            applicationContext.getBean(MatchSettleTemplateRelationRepositoryImpl.class).doBatchUpdate(listEntities);
        } catch (Exception e) {
            log.error("batchInsertTemplateRelationToRedis:redis插入异常：", e);
        }
    }

    @Override
    public void insertTemplateRelationOnlyRedis(MatchSettleTemplateRelationEntity matchSettleTemplateRelation) {
        String key = TEMPLATE_RELATION + matchSettleTemplateRelation.getId();
        try {
            redisService.set(key, JSONObject.toJSON(matchSettleTemplateRelation), REDIS_THREE_TIME);
        } catch (Exception e) {
            log.error("insertTemplateRelationOnlyRedis:redis插入异常：key:{} value:{} error:", key, JSONObject.toJSON(matchSettleTemplateRelation), e);
        }
    }

    void doBatchUpdate(List<MatchSettleTemplateRelationEntity> listEntities) {
        super.updateBatchById(listEntities);
        String linkId = "match-settle-template-relation-batch-" + UUIdUtils.getId();
        List<Object> objects = listEntities.stream().map(t->(Object) t).collect(Collectors.toList());
        matchSettleSPOddsProducer.sendToSlaveMq(CommonConstant.SETTLE_SLAVE_DB_TOPIC, linkId, objects,
                CommonConstant.SETTLE_TEMPLATE_RELATION_TABLE, false);
    }

    void doInsertOrUpdate(MatchSettleTemplateRelationEntity matchSettleTemplateRelation, boolean isInsert) {
        String linkId = "match-settle-template-relation-"+matchSettleTemplateRelation.getId();
        matchSettleSPOddsProducer.sendToSlaveMq(CommonConstant.SETTLE_SLAVE_DB_TOPIC, linkId, Arrays.asList(matchSettleTemplateRelation),
                CommonConstant.SETTLE_TEMPLATE_RELATION_TABLE, isInsert);
    }

    @Async("RemoveDBThreadPool")
    void deleteByExample(MatchSettleTemplateRelationExample example) {
        matchSettleTemplateRelationV2Mapper.deleteByExample(example);
    }

}
