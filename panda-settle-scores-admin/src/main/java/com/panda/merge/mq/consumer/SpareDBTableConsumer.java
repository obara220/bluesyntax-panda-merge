package com.panda.merge.mq.consumer;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.fasterxml.jackson.databind.JavaType;
import com.panda.merge.constant.CommonConstant;
import com.panda.merge.dto.Request;
import com.panda.merge.mq.producer.CommonProducer;
import com.panda.merge.v2.entity.*;
import com.panda.merge.v2.repository.*;
import com.panda.merge.config.JacksonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SpareDBTableConsumer {

    @Resource(name = "MatchSettleDataSourceSwitchRepositoryV2")
    private MatchSettleDataSourceSwitchRepository matchSettleDataSourceSwitchRepository;
    @Resource(name = "MatchSettleDataSourceConfigRepositoryV2")
    private MatchSettleDataSourceConfigRepository matchSettleDataSourceConfigRepository;
    @Resource(name = "MatchSettleInfoRepositoryV2")
    private MatchSettleInfoRepository matchSettleInfoRepository;
    @Resource(name = "MatchSettleTemplateRepositoryV2")
    private MatchSettleTemplateRepository matchSettleTemplateRepository;
    @Resource(name = "MatchSettleTemplateRelationRepositoryV2")
    private MatchSettleTemplateRelationRepository matchSettleTemplateRelationRepository;
    @Resource(name = "MatchSettleGoalStatusRepositoryV2")
    private MatchSettleGoalStatusRepository matchSettleGoalStatusRepository;
    @Resource(name = "MatchSettleFactorCheckInfoRepositoryV2")
    private MatchSettleFactorCheckInfoRepository matchSettleFactorCheckInfoRepository;
    @Resource(name = "MatchSettleRollBackInfoRepositoryV2")
    private MatchSettleRollBackInfoRepository matchSettleRollBackInfoRepository;
    @Resource(name = "MatchSettleOperateLogRepositoryV2")
    private MatchSettleOperateLogV2Repository matchSettleOperateLogV2Repository;

    //是否使用数据中心接口
    @NacosValue(value = "${datacenter.settle.switch}", autoRefreshed = true)
    private Boolean datacenterSettleSwitch;
    @Autowired
    CommonProducer commonProducer;

    public static Map<String, Class> tableClass = new HashMap<String, Class>() {
        {
            put(CommonConstant.SETTLE_DATA_SOURCE_CONFIG_TABLE, MatchSettleDataSourceConfigEntity.class);
            put(CommonConstant.SETTLE_DATA_SOURCE_SWITCH_TABLE, MatchSettleDataSourceSwitchEntity.class);
            put(CommonConstant.SETTLE_INFO_TABLE, MatchSettleInfoEntity.class);
            put(CommonConstant.SETTLE_TEMPLATE_TABLE, MatchSettleTemplateEntity.class);
            put(CommonConstant.SETTLE_TEMPLATE_RELATION_TABLE, MatchSettleTemplateRelationEntity.class);
            put(CommonConstant.SETTLE_GOAL_STATUS_TABLE, MatchSettleGoalStatusEntity.class);
            put(CommonConstant.SETTLE_FACTOR_CHECK_INFO_TABLE, MatchSettleFactorCheckInfoEntity.class);
            put(CommonConstant.SETTLE_ROLL_BACK_INFO_TABLE, MatchSettleRollBackInfoEntity.class);
            put(CommonConstant.SETTLE_OPERATE_LOG_TABLE, MatchSettleOperateLogEntity.class);
        }
    };

    public ConsumeOrderlyStatus processMessages(List<MessageExt> msgs, ConsumeOrderlyContext context) {
        log.info("SpareDBTableConsumer start ...");
        if(datacenterSettleSwitch){
            log.info("SpareDBTableConsumer datacenterSettleSwitch turn on ...");
            return ConsumeOrderlyStatus.SUCCESS;
        }
        String linkIdTotal = msgs.get(0).getProperties().get("KEYS");
        log.info("linkId::{}::SpareDBTableConsumer start", linkIdTotal);
        try {
            Map<String, Pair<List<Object>, Map<Long, Object> >> dataByTag = new HashMap<>();
            for(MessageExt ext: msgs) {
                String linkId = ext.getProperties().get("KEYS");
                log.info("linkId::{}::SpareDBTableConsumer start", linkId);
                String tag = ext.getProperties().get(CommonConstant.TAG);
                Class clazz = tableClass.get(tag);
                Boolean isInsert = Boolean.valueOf(ext.getProperties().get(CommonConstant.IS_INSERT));
                String message = new String(ext.getBody(), StandardCharsets.UTF_8);
                log.info("linkId::{}::SpareDBTableConsumer isInsert:{} message:{} ", linkId, isInsert, message);
                JSONObject jsonObject = JSONObject.parseObject(message);
                JavaType javaType = JacksonUtils.getObjectMapper().getTypeFactory()
                        .constructCollectionType(List.class, clazz);
                List<Object> dataList = (List<Object>) JacksonUtils.fromJson(jsonObject.getString("data"), javaType);
                log.info("linkId::{}::SpareDBTableConsumer dataList:{}", linkId, dataList);
                if(isInsert) {
                    if(!dataByTag.containsKey(tag)) {
                        Pair<List<Object>, Map<Long, Object> > pair = Pair.of(new ArrayList<>(), new HashMap<>());
                        dataByTag.put(tag, pair);
                    }
                    dataByTag.get(tag).getLeft().addAll(dataList);
                } else {
                    if(!dataByTag.containsKey(tag)) {
                        Pair<List<Object>, Map<Long, Object> > pair = Pair.of(new ArrayList<>(), new HashMap<>());
                        dataByTag.put(tag, pair);
                    }
                    Map<Long, Object> updateDataMap = dataByTag.get(tag).getRight();
                    for(Object switchEntity : dataList) {
                        BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(switchEntity);
                        Long id = Long.parseLong(wrapper.getPropertyValue("id").toString());
                        updateDataMap.put(id, switchEntity);
                    }
                }
                log.info("linkId::{}::SpareDBTableConsumer dataByTag:{}", linkId, dataByTag);
            }
            dataByTag.entrySet().stream().forEach(t->doSave(t.getKey(), t.getValue().getLeft(), Arrays.asList(t.getValue().getRight().values().toArray())));
            log.info("linkId::{}::SpareDBTableConsumer end", linkIdTotal);
        } catch (Exception e) {
            log.error("linkId::{}::SpareDBTableConsumer 事件列表数据处理异常, Exception:", linkIdTotal, e);
        }
        return ConsumeOrderlyStatus.SUCCESS;
    }

    public void doSave(String tag, List<Object> insertData, List<Object> updateData) {
        switch (tag) {
            case CommonConstant.SETTLE_DATA_SOURCE_CONFIG_TABLE:
                if (!CollectionUtils.isEmpty(insertData)) {
                    List<MatchSettleDataSourceConfigEntity> insertDataEntity = insertData.stream().map(t->(MatchSettleDataSourceConfigEntity)t).collect(Collectors.toList());
                    matchSettleDataSourceConfigRepository.saveBatch(insertDataEntity);
                }
                if (!CollectionUtils.isEmpty(updateData)) {
                    List<MatchSettleDataSourceConfigEntity> updateDataEntity = updateData.stream().map(t->(MatchSettleDataSourceConfigEntity)t).collect(Collectors.toList());
                    matchSettleDataSourceConfigRepository.updateBatchById(updateDataEntity);
                }
                break;
            case CommonConstant.SETTLE_DATA_SOURCE_SWITCH_TABLE:
                if (!CollectionUtils.isEmpty(insertData)) {
                    List<MatchSettleDataSourceSwitchEntity> insertDataEntity = insertData.stream().map(t->(MatchSettleDataSourceSwitchEntity)t).collect(Collectors.toList());
                    matchSettleDataSourceSwitchRepository.saveBatch(insertDataEntity);
                }
                if (!CollectionUtils.isEmpty(updateData)) {
                    List<MatchSettleDataSourceSwitchEntity> updateDataEntity = updateData.stream().map(t->(MatchSettleDataSourceSwitchEntity)t).collect(Collectors.toList());
                    matchSettleDataSourceSwitchRepository.updateBatchById(updateDataEntity);
                }
                break;
            case CommonConstant.SETTLE_INFO_TABLE:
                if (!CollectionUtils.isEmpty(insertData)) {
                    List<MatchSettleInfoEntity> insertDataEntity = insertData.stream().map(t->(MatchSettleInfoEntity)t).collect(Collectors.toList());
                    boolean success = matchSettleInfoRepository.saveBatch(insertDataEntity);
                    log.info("SpareDBTableConsumer finish insert match_settle_info result:{} ", success);
                }
                if (!CollectionUtils.isEmpty(updateData)) {
                    List<MatchSettleInfoEntity> updateDataEntity = updateData.stream().map(t->(MatchSettleInfoEntity)t).collect(Collectors.toList());
                    boolean success = matchSettleInfoRepository.updateBatchById(updateDataEntity);
                    log.info("SpareDBTableConsumer finish update match_settle_info result:{} ", success);
                }
                break;
            case CommonConstant.SETTLE_TEMPLATE_TABLE:
                if (!CollectionUtils.isEmpty(insertData)) {
                    List<MatchSettleTemplateEntity> insertDataEntity = insertData.stream().map(t->(MatchSettleTemplateEntity)t).collect(Collectors.toList());
                    matchSettleTemplateRepository.saveBatch(insertDataEntity);
                }
                if (!CollectionUtils.isEmpty(updateData)) {
                    List<MatchSettleTemplateEntity> updateDataEntity = updateData.stream().map(t->(MatchSettleTemplateEntity)t).collect(Collectors.toList());
                    matchSettleTemplateRepository.updateBatchById(updateDataEntity);
                }
                break;
            case CommonConstant.SETTLE_TEMPLATE_RELATION_TABLE:
                if (!CollectionUtils.isEmpty(insertData)) {
                    List<MatchSettleTemplateRelationEntity> insertDataEntity = insertData.stream().map(t->(MatchSettleTemplateRelationEntity)t).collect(Collectors.toList());
                    matchSettleTemplateRelationRepository.saveBatch(insertDataEntity);
                }
                if (!CollectionUtils.isEmpty(updateData)) {
                    List<MatchSettleTemplateRelationEntity> updateDataEntity = updateData.stream().map(t->(MatchSettleTemplateRelationEntity)t).collect(Collectors.toList());
                    matchSettleTemplateRelationRepository.updateBatchById(updateDataEntity);
                }
                break;
            case CommonConstant.SETTLE_GOAL_STATUS_TABLE:
                if (!CollectionUtils.isEmpty(insertData)) {
                    List<MatchSettleGoalStatusEntity> insertDataEntity = insertData.stream().map(t->(MatchSettleGoalStatusEntity)t).collect(Collectors.toList());
                    matchSettleGoalStatusRepository.saveBatch(insertDataEntity);
                }
                if (!CollectionUtils.isEmpty(updateData)) {
                    List<MatchSettleGoalStatusEntity> updateDataEntity = updateData.stream().map(t->(MatchSettleGoalStatusEntity)t).collect(Collectors.toList());
                    matchSettleGoalStatusRepository.updateBatchById(updateDataEntity);
                }
                break;
            case CommonConstant.SETTLE_FACTOR_CHECK_INFO_TABLE:
                if (!CollectionUtils.isEmpty(insertData)) {
                    List<MatchSettleFactorCheckInfoEntity> insertDataEntity = insertData.stream().map(t->(MatchSettleFactorCheckInfoEntity)t).collect(Collectors.toList());
                    matchSettleFactorCheckInfoRepository.saveBatch(insertDataEntity);
                }
                if (!CollectionUtils.isEmpty(updateData)) {
                    List<MatchSettleFactorCheckInfoEntity> updateDataEntity = updateData.stream().map(t->(MatchSettleFactorCheckInfoEntity)t).collect(Collectors.toList());
                    matchSettleFactorCheckInfoRepository.updateBatchById(updateDataEntity);
                }
                break;
            case CommonConstant.SETTLE_ROLL_BACK_INFO_TABLE:
                if (!CollectionUtils.isEmpty(insertData)) {
                    List<MatchSettleRollBackInfoEntity> insertDataEntity = insertData.stream().map(t->(MatchSettleRollBackInfoEntity)t).collect(Collectors.toList());
                    matchSettleRollBackInfoRepository.saveBatch(insertDataEntity);
                }
                if (!CollectionUtils.isEmpty(updateData)) {
                    List<MatchSettleRollBackInfoEntity> updateDataEntity = updateData.stream().map(t->(MatchSettleRollBackInfoEntity)t).collect(Collectors.toList());
                    matchSettleRollBackInfoRepository.updateBatchById(updateDataEntity);
                }
                break;
            case CommonConstant.SETTLE_OPERATE_LOG_TABLE:
                if (!CollectionUtils.isEmpty(insertData)) {
                    List<MatchSettleOperateLogEntity> insertDataEntity = insertData.stream().map(t->(MatchSettleOperateLogEntity)t).collect(Collectors.toList());
                    matchSettleOperateLogV2Repository.saveBatch(insertDataEntity);
                }
                break;
        }
    }
}
