package com.panda.merge.rocketmq.producer;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.dto.Request;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.service.ThirdMatchInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.common.ThreadFactoryImpl;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.panda.merge.constant.ConstantSystem.*;

@Slf4j
@Component
public class DataCenterProducer<T> {

    @Resource
    private RocketMQTemplate rocketMqTemplate;

    /**
     * 事件赛事级分流
     */
    @NacosValue(value = "${consumer.switch.forwardEvent.matchIds}", autoRefreshed = true)
    private String forwardEventMatchIds;

    @Resource
    private ThirdMatchInfoService thirdMatchInfoService;

    private final ConcurrentHashMap<Long/*标准赛事id*/, Set<MatchObject>> forwardMatchIdTable = new ConcurrentHashMap<>();

    private final ScheduledExecutorService forwardMatchIdScheduledExecutorService = new ScheduledThreadPoolExecutor(1,new ThreadFactoryImpl(
            "forward-match-scheduled-thread"));

    @PostConstruct
    private void init() {
        forwardMatchIdScheduledExecutorService.scheduleAtFixedRate(this::refreshForwardMatchIdCache,0,1, TimeUnit.SECONDS);
    }

    public void send(Request<T> request,String topic) {
        String forwardTopic = topic+"_DC";
        log.info("linkId={},topic={},tag={}--向数据中心转发开始",request.getLinkId(),forwardTopic,request.getTag());
        if (StringUtils.isBlank(request.getTag())) {
            request.setTag("forward_to_dc");
        }
        try {
            MessageBuilder<Request<T>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, request.getLinkId());
            rocketMqTemplate.send(forwardTopic + ":" + request.getTag(), builder.build());
        } catch (Exception e) {
            log.info("linkId={},topic={},tag={}--向数据中心转发异常",request.getLinkId(),forwardTopic,request.getTag());
            log.error("linkId={}--向数据中心转发异常",request.getLinkId(),e);
        }
        log.info("linkId={},topic={},tag={}--向数据中心转发结束",request.getLinkId(),forwardTopic,request.getTag());
    }

    public void send(T request,String topic,String linkId,String tag) {
        String forwardTopic = topic+"_DC";
        log.info("linkId={},topic={},tag={}--向数据中心转发开始",linkId,forwardTopic,tag);
        try {
            MessageBuilder<T> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
            rocketMqTemplate.send(forwardTopic + ":" + tag, builder.build());
        } catch (Exception e) {
            log.info("linkId={},topic={},tag={}--向数据中心转发异常",linkId,forwardTopic,tag);
            log.error("linkId={}--向数据中心转发异常",linkId,e);
        }
        log.info("linkId={},topic={},tag={}--向数据中心转发结束",linkId,forwardTopic,tag);
    }

    public void send(MessageExt messageExt,String topic) {
        String linkId = messageExt.getKeys();
        String tag = messageExt.getTags();
        String forwardTopic = topic+"_DC";
        log.info("linkId={},topic={},tag={}--向数据中心转发开始",linkId,forwardTopic,tag);
        try {
            Message message = new Message(forwardTopic, tag, linkId, messageExt.getBody());
            rocketMqTemplate.getProducer().send(message);
        } catch (Exception e) {
            log.info("linkId={},topic={},tag={}--向数据中心转发异常",linkId,forwardTopic,tag);
            log.error("linkId={}--向数据中心转发异常",linkId,e);
        }
        log.info("linkId={},topic={},tag={}--向数据中心转发结束",linkId,forwardTopic,tag);
    }

    /** 批量拉取后,一次消费多条,需遍历转发 */
    public void send(List<Request<T>> requests, String topic) {
        if (CollectionUtils.isEmpty(requests)) {
            return;
        }
        for (Request<T> request : requests) {
            send(request,topic);
        }
    }

    public boolean checkForward(Long standardMatchId,String linkId){
        if (StringUtils.isBlank(forwardEventMatchIds)) {
            log.info("【{}】赛事分流check返回false,未配置赛事2",linkId);
            return false;
        }
        if (standardMatchId == null ) {
            log.info("【{}】赛事分流check返回false,standardMatchId: {}",linkId, standardMatchId);
            return false;
        }
        boolean result = false;
        try {
            List<Long> matchIds = Arrays.stream(forwardEventMatchIds.split(","))
                    .map(Long::valueOf)
                    .collect(Collectors.toList());
            for (Long matchId : matchIds) {
                if (standardMatchId.equals(matchId)) {
                    result = true;
                    log.info("【{}】赛事分流check返回true4",linkId);
                    break;
                }
            }
        } catch (Exception e) {
            log.info("【{}】赛事分流check异常,{}",linkId,ExceptionUtil.stacktraceToString(e));
        }
        if (!result) {
            log.info("【{}】赛事分流check返回false2",linkId);
        }
        return result;
    }

    public boolean checkForward(String thirdMatchSourceId, String dataSourceCode,String linkId){
        if (MapUtils.isEmpty(forwardMatchIdTable)) {
            log.info("【{}】赛事分流check返回false,未配置赛事",linkId);
            return false;
        }
        if (StringUtils.isBlank(thirdMatchSourceId) || StringUtils.isBlank(dataSourceCode)) {
            log.info("【{}】赛事分流check返回false,thirdMatchSourceId: {},dataSourceCode: {}",linkId, thirdMatchSourceId, dataSourceCode);
            return false;
        }
        boolean result = false;
        MatchObject matchObject = new MatchObject(thirdMatchSourceId, dataSourceCode);
        for (Set<MatchObject> matchObjects : forwardMatchIdTable.values()) {
            if (matchObjects.contains(matchObject)) {
                result = true;
                log.info("【{}】赛事分流check返回true2",linkId);
                break;
            }
        }
        if (!result) {
            log.info("【{}】赛事分流check返回false",linkId);
        }
        return result;
    }

    public boolean checkForward(MessageExt messageExt, String topic){
        String linkId = messageExt.getProperties().get("KEYS");
        String message = new String(messageExt.getBody(), StandardCharsets.UTF_8);
        if (StringUtils.isBlank(message)) {
            log.info("【{}】赛事分流check返回false,message为空",linkId);
            return false;
        }
        String thirdMatchSourceId = null;
        String dataSourceCode = null;
        try {
            JSONObject jsonObject = JSONObject.parseObject(message);
            if (THIRD_MATCH_EVENT_INFO_API.equals(topic)) {
                JSONObject matchEventInfo = jsonObject.getObject("data", JSONObject.class);
                thirdMatchSourceId = matchEventInfo.getString("thirdMatchSourceId");
                dataSourceCode = matchEventInfo.getString("dataSourceCode");
                return checkForward(thirdMatchSourceId,dataSourceCode,linkId);
            } else if (THIRD_MATCH_EVENT_LIST_INFO_API.equals(topic)) {
                JSONArray data = jsonObject.getJSONArray("data");
                thirdMatchSourceId = data.getJSONObject(0).getString("thirdMatchSourceId");
                dataSourceCode = data.getJSONObject(0).getString("dataSourceCode");
                return checkForward(thirdMatchSourceId,dataSourceCode,linkId);
            } else if (MATCH_OPERATE_MSG.equals(topic) || MATCH_ASSOCIATION_INFO_SK.equals(topic)) {
                Long standardMatchId = jsonObject.getLong("standardMatchId");
                return checkForward(standardMatchId,linkId);
            }
        } catch (Exception e) {
            log.info("【{}】赛事分流check异常,{}",linkId,ExceptionUtil.stacktraceToString(e));
        }
        return false;
    }

    private void refreshForwardMatchIdCache(){
        try {
            if (StringUtils.isBlank(forwardEventMatchIds)) {
                if (forwardMatchIdTable.size() > 0) {
                    forwardMatchIdTable.clear();
                    log.info("forwardEventMatchIdTable 已清空");
                }
                return;
            }
            List<Long> matchIds = Arrays.stream(forwardEventMatchIds.split(","))
                    .map(Long::valueOf)
                    .filter(aLong -> !forwardMatchIdTable.containsKey(aLong))
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(matchIds)) {
                return;
            }
            List<ThirdMatchInfo> thirdMatchInfos = thirdMatchInfoService.getItems(matchIds, null);
            if (CollectionUtils.isEmpty(thirdMatchInfos)) {
                return;
            }
            Map<Long, List<ThirdMatchInfo>> thirdMatchInfoMap = thirdMatchInfos.stream().collect(Collectors.groupingBy(ThirdMatchInfo::getReferenceId));
            Iterator<Map.Entry<Long, List<ThirdMatchInfo>>> it = thirdMatchInfoMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Long, List<ThirdMatchInfo>> next = it.next();
                Set<MatchObject> matchObjects = next.getValue()
                        .stream()
                        .map(t -> {
                            MatchObject obj = new MatchObject();
                            obj.setDataSourceCode(t.getDataSourceCode());
                            obj.setThirdMatchSourceId(t.getThirdMatchSourceId());
                            return obj;
                        }).collect(Collectors.toSet());
                forwardMatchIdTable.put(next.getKey(),matchObjects);
            }
            log.info("forwardEventMatchIdTable: {}", JSON.toJSONString(forwardMatchIdTable));
        } catch (Exception e) {
            log.error("refreshForwardMatchIdCache exception: {}", ExceptionUtil.stacktraceToString(e));
        }
    }

    class MatchObject {
        public String thirdMatchSourceId;
        public String dataSourceCode;

        public MatchObject() {
        }

        public MatchObject(String thirdMatchSourceId, String dataSourceCode) {
            this.thirdMatchSourceId = thirdMatchSourceId;
            this.dataSourceCode = dataSourceCode;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MatchObject that = (MatchObject) o;
            return Objects.equals(thirdMatchSourceId, that.thirdMatchSourceId) && Objects.equals(dataSourceCode, that.dataSourceCode);
        }

        @Override
        public int hashCode() {
            return Objects.hash(thirdMatchSourceId, dataSourceCode);
        }

        public String getThirdMatchSourceId() {
            return thirdMatchSourceId;
        }

        public void setThirdMatchSourceId(String thirdMatchSourceId) {
            this.thirdMatchSourceId = thirdMatchSourceId;
        }

        public String getDataSourceCode() {
            return dataSourceCode;
        }

        public void setDataSourceCode(String dataSourceCode) {
            this.dataSourceCode = dataSourceCode;
        }
    }
}
