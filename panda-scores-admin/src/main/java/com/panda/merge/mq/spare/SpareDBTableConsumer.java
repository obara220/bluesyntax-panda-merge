package com.panda.merge.mq.spare;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.fasterxml.jackson.databind.JavaType;
import com.panda.merge.constant.CommonConstant;
import com.panda.merge.dto.Request;
import com.panda.merge.mapper.MatchScoresInfoMapper;
import com.panda.merge.mapper.MatchScoresSourceTypeMapper;
import com.panda.merge.mapper.MatchTimeInfoMapper;
import com.panda.merge.model.MatchScoresInfo;
import com.panda.merge.model.MatchScoresSourceType;
import com.panda.merge.model.MatchTimeInfo;
import com.panda.merge.utils.JacksonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
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

/**
 * @author warren
 * @since 2025/06/14 04:43:42
 */
@Slf4j
@Component
public class SpareDBTableConsumer {
    @Autowired
    private MatchScoresInfoMapper matchScoresInfoMapper;
    @Autowired
    private MatchTimeInfoMapper matchTimeInfoMapper;
    @Autowired
    private MatchScoresSourceTypeMapper matchScoresSourceTypeMapper;

    @NacosValue(value = "${datacenter.scores.switch:false}", autoRefreshed = true)
    private Boolean datacenterMergeSwitch;

    public static Map<String, Class> tableClass = new HashMap<String, Class>() {
        {
            put(CommonConstant.SCORE_CENTER_MATCH_SCORES_INFO_TABLE, MatchScoresInfo.class);
            put(CommonConstant.SCORE_CENTER_MATCH_TIME_INFO_TABLE, MatchTimeInfo.class);
            put(CommonConstant.SCORE_CENTER_MATCH_SCORES_SOURCE_TYPE_TABLE, MatchScoresSourceType.class);
        }
    };

    public ConsumeOrderlyStatus processMessages(List<MessageExt> msgs, ConsumeOrderlyContext context) {
        if (datacenterMergeSwitch) {
            return ConsumeOrderlyStatus.SUCCESS;
        }
        String linkIdTotal = msgs.get(0).getProperties().get("KEYS");
        log.info("linkIdTotal::{}::SpareDBTableConsumer start", linkIdTotal);
        try {
            Map<String, Pair<List<Object>, Map<Long, Object>>> dataByTag = new HashMap<>();
            for (MessageExt ext : msgs) {
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
                if (isInsert) {
                    if (!dataByTag.containsKey(tag)) {
                        Pair<List<Object>, Map<Long, Object>> pair = Pair.of(new ArrayList<>(), new HashMap<>());
                        dataByTag.put(tag, pair);
                    }
                    dataByTag.get(tag).getLeft().addAll(dataList);
                } else {
                    if (!dataByTag.containsKey(tag)) {
                        Pair<List<Object>, Map<Long, Object>> pair = Pair.of(new ArrayList<>(), new HashMap<>());
                        dataByTag.put(tag, pair);
                    }
                    Map<Long, Object> updateDataMap = dataByTag.get(tag).getRight();
                    for (Object switchEntity : dataList) {
                        BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(switchEntity);
                        Long id = Long.parseLong(wrapper.getPropertyValue("id").toString());
                        updateDataMap.put(id, switchEntity);
                    }
                }
                log.info("linkId::{}::SpareDBTableConsumer dataByTag:{}", linkId, dataByTag);
            }
            dataByTag.entrySet().stream().forEach(t -> doSave(t.getKey(), t.getValue().getLeft(), Arrays.asList(t.getValue().getRight().values().toArray())));
            log.info("linkIdTotal::{}::SpareDBTableConsumer end", linkIdTotal);
        } catch (Exception e) {
            log.error("linkIdTotal::{}::SpareDBTableConsumer 事件列表数据处理异常, Exception:", linkIdTotal, e);
        }
        return ConsumeOrderlyStatus.SUCCESS;
    }

    public void doSave(String tag, List<Object> insertData, List<Object> updateData) {
        switch (tag) {
            case CommonConstant.SCORE_CENTER_MATCH_SCORES_INFO_TABLE:
                if (!CollectionUtils.isEmpty(insertData)) {
                    List<MatchScoresInfo> collect = insertData.stream().map(t -> (MatchScoresInfo) t).collect(Collectors.toList());
                    matchScoresInfoMapper.batchUpdateByPrimaryKey(collect);
                }
                if (!CollectionUtils.isEmpty(updateData)) {
                    List<MatchScoresInfo> collect = updateData.stream().map(t -> (MatchScoresInfo) t).collect(Collectors.toList());
                    matchScoresInfoMapper.batchUpdateByPrimaryKey(collect);
                }
                break;
            case CommonConstant.SCORE_CENTER_MATCH_TIME_INFO_TABLE:
                if (!CollectionUtils.isEmpty(insertData)) {
                    List<MatchTimeInfo> collect = insertData.stream().map(t -> (MatchTimeInfo) t).collect(Collectors.toList());
                    matchTimeInfoMapper.batchUpdateByPrimaryKey(collect);
                }
                if (!CollectionUtils.isEmpty(updateData)) {
                    List<MatchTimeInfo> collect = updateData.stream().map(t -> (MatchTimeInfo) t).collect(Collectors.toList());
                    matchTimeInfoMapper.batchUpdateByPrimaryKey(collect);
                }
                break;
            case CommonConstant.SCORE_CENTER_MATCH_SCORES_SOURCE_TYPE_TABLE:
                if (!CollectionUtils.isEmpty(insertData)) {
                    List<MatchScoresSourceType> collect = insertData.stream().map(t -> (MatchScoresSourceType) t).collect(Collectors.toList());
                    matchScoresSourceTypeMapper.batchUpdateByPrimaryKey(collect);
                }
                if (!CollectionUtils.isEmpty(updateData)) {
                    List<MatchScoresSourceType> collect = updateData.stream().map(t -> (MatchScoresSourceType) t).collect(Collectors.toList());
                    matchScoresSourceTypeMapper.batchUpdateByPrimaryKey(collect);
                }
            default:
                break;
        }
    }
}
