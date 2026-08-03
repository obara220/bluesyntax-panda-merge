package com.panda.merge.mq.consumer;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.SourceTypeEnum;
import com.panda.merge.dto.Request;
import com.panda.merge.model.*;
import com.panda.merge.mq.message.MatchEventInfoMessage;
import com.panda.merge.mq.message.RcsTradeUpdateEventConfig;
import com.panda.merge.mq.message.RcsTradeUpdateEventConfigDTO;
import com.panda.merge.mq.producer.CommonProducer;
import com.panda.merge.mq.spare.SpareBaseProducer;
import com.panda.merge.repository.MatchTimeInfoRepository;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.service.StandardSportMarketSellService;
import com.panda.merge.service.ThirdMatchInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.panda.merge.constant.ConstantSystem.THIRD_MATCH_EVENT_INFO_API;


/**
 * 玩法集tMax开关配置
 * 风控下发
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = "RCS_EVENT_PLAY_SET_SEAL_TMAX",
        consumerGroup = "scores-group-RCS_EVENT_PLAY_SET_SEAL_TMAX",
        consumeThreadMax = 256,
        consumeTimeout = 10000L
)
@DependsOn("scoresAdminApplication")
public class RcsTradeUpdateEventConfigConsumer implements RocketMQListener<String> {


    @Autowired
    StandardMatchInfoService standardMatchInfoService;
    @Autowired
    StandardSportMarketSellService standardSportMarketSellService;
    @Autowired
    ThirdMatchInfoService thirdMatchInfoService;
    @Autowired
    MatchTimeInfoRepository matchTimeInfoRepository;
    @Autowired
    private RocketMQTemplate rocketMqTemplate;
    @NacosValue(value = "${panda.data.mq.gateway.event:1}", autoRefreshed = true)
    private int pandaDataMqGatewayevent;
    @NacosValue(value = "${panda.data.mq.gateway.matchId:1,}", autoRefreshed = true)
    private String pandaDataMqGatewayMatchId;
    @Autowired
    SpareBaseProducer spareBaseProducer;

    @NacosValue(value = "${datacenter.scores.switch:false}", autoRefreshed = true)
    private Boolean datacenterMergeSwitch;
    @Autowired
    CommonProducer commonProducer;
    @NacosValue(value = "${livedata.tmax.switch:0}", autoRefreshed = true)
    private Integer tmaxSwitch;


    ScheduledThreadPoolExecutor executorService = new ScheduledThreadPoolExecutor(32);

    @Override
    public void onMessage(String s) {
        log.info("RcsTradeUpdateEventConfigConsumer MQ消费数据开始...{}",datacenterMergeSwitch);

        log.info("RCS_EVENT_PLAY_SET_SEAL_TMAX下发事件开始：{}", s);
        try{
            if(StrUtil.isEmpty(s)){
                return;
            }
            JSONObject jsonObj = new JSONObject(s);
            if(null == jsonObj.get("data") ) {
                log.info("RCS_EVENT_PLAY_SET_SEAL_TMAX下发事件 字段不全-data   {}",jsonObj);
                return;
            }
            if(null == jsonObj.get("linkId") ) {
                log.info("RCS_EVENT_PLAY_SET_SEAL_TMAX下发事件 字段不全-linkID   {}",jsonObj);
                return;
            }
            String linkId =  jsonObj.getStr("linkId");
            if (datacenterMergeSwitch) {
                //MQ消息转发给数据中心
                commonProducer.asyncSend(s, "datacenter-RCS_EVENT_PLAY_SET_SEAL_TMAX",linkId);
                return;
            }
            if(tmaxSwitch!=1){
                log.info("RCS_EVENT_PLAY_SET_SEAL_TMAX下发事件,开关关闭状态，本次不处理");
                return;
            }

            JSONObject jsonObject = jsonObj.getJSONObject("data");
            JSONArray jsonArray =  jsonObject.getJSONArray("playSetCodeList");
            Long matchId = jsonObject.getLong("matchId");
            Long secondFormStar = jsonObject.getLong("secondFormStar");
            Long matchPeriodId = jsonObject.getLong("matchPeriodId");
            String dataSourceCode = jsonObject.getStr("dataSourceCode");
            List<String> events = jsonArray.toList(String.class);
            log.info("{}，RCS_EVENT_PLAY_SET_SEAL_TMAX事件集合:{}",linkId,events);
            if(events==null || events.isEmpty()){
                return;
            }
            //1.5秒后执行校验 下发事件
            executorService.schedule(() ->    checkEndSendEvent(matchId, events, linkId,secondFormStar,matchPeriodId,dataSourceCode), 1500, TimeUnit.MILLISECONDS);
            log.info("{}，RCS_EVENT_PLAY_SET_SEAL_TMAX下发事件结束",linkId);
        }catch(Exception e){
            log.error("RCS_EVENT_PLAY_SET_SEAL_TMAX下发事件 异常:",e);
        }
    }

    private void checkEndSendEvent(Long matchId, List<String> events, String linkId,Long secondFormStar,Long matchPeriodId,String dataSourceCode) {
        StandardMatchInfo matchInfo =  standardMatchInfoService.getItem(matchId);
        if(matchInfo==null){
            log.info("{}，RCS_EVENT_PLAY_SET_SEAL_TMAX下发事件失败：无标准赛事信息",linkId);
            return;
        }
//        StandardSportMarketSell sell = standardSportMarketSellService.getItem(matchId);
//        if(sell==null){
//            log.info("{}，RCS_EVENT_PLAY_SET_SEAL_TMAX下发事件失败：无开售信息",linkId);
//            return;
//        }
        //下发对应事件源的tmax
        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem(matchId,dataSourceCode);
        if(thirdMatchInfo==null){
            log.info("{}，RCS_EVENT_PLAY_SET_SEAL_TMAX下发事件失败：无三方赛事信息",linkId);
            return;
        }
//        MatchTimeInfo matchTimeInfo = matchTimeInfoRepository.selectByThirdMatchId(thirdMatchInfo.getId(), SourceTypeEnum.LIVE_DATA.getCode());
//        if(matchTimeInfo==null || matchTimeInfo.getSecondFromStart()==null){
//            log.info("{}，RCS_EVENT_PLAY_SET_SEAL_TMAX下发事件失败：无三方赛事时间信息",linkId);
//            return;
//        }
//        log.info("{}，RCS_EVENT_PLAY_SET_SEAL_TMAX下发事件新增1{}，secondFormStar:{}",matchId,linkId,secondFormStar);
//        if(secondFormStar==null && matchTimeInfo!=null){
//            secondFormStar = matchTimeInfo.getSecondFromStart();
//        }
        log.info("{}，RCS_EVENT_PLAY_SET_SEAL_TMAX下发事件新增2{}，secondFormStar:{}",matchId,linkId,secondFormStar);
        //添加需要下发tmax的事件编码
        List<String> eventCodes = new ArrayList<>();
        for(String str:events){
            String eventCode = getEventCode(str);
            if(StringUtils.isNotEmpty(eventCode)){
                log.info("{}，RCS_EVENT_PLAY_SET_SEAL_TMAX下发事件新增{}，{}",matchId,linkId,eventCode);
                eventCodes.add(eventCode);
            }
        }
        if(!eventCodes.isEmpty()){
            //去重后下发事件
            eventCodes = eventCodes.stream().distinct().collect(Collectors.toList());
            for(String str:eventCodes){
                //封装事件信息
                MatchEventInfoMessage matchEventInfoMessage = new MatchEventInfoMessage();
                matchEventInfoMessage.setCanceled(0);//未取消
                matchEventInfoMessage.setSourceType("1"); //常规事件
                matchEventInfoMessage.setEventTime(System.currentTimeMillis());
                matchEventInfoMessage.setCopyLinkId(linkId+"_"+str);
                matchEventInfoMessage.setThirdMatchSourceId(thirdMatchInfo.getThirdMatchSourceId());
                matchEventInfoMessage.setIsErrorEndEvent(0);
                matchEventInfoMessage.setThirdEventId("PA_Event:"+ UUIdUtils.getId());
                matchEventInfoMessage.setExtrainfo("reject-auto");
                matchEventInfoMessage.setRemark("玩法集封盘拒单事件");
                matchEventInfoMessage.setSportId(matchInfo.getSportId());
                matchEventInfoMessage.setMatchPeriodId(matchInfo.getMatchPeriodId());
                matchEventInfoMessage.setAddition5("1");
                //因为事件延迟1.5秒下发，所以新增的事件这里需要+2秒，减少误差
                matchEventInfoMessage.setSecondsFromStart(secondFormStar+2);
                matchEventInfoMessage.setMatchPeriodId(matchPeriodId);
                matchEventInfoMessage.setEventCode(str);
                matchEventInfoMessage.setDataSourceCode(dataSourceCode);
                sendMatchEventMessage(matchEventInfoMessage,matchId);
            }
        }else{
            log.info("{}，RCS_EVENT_PLAY_SET_SEAL_TMAX下发事件失败：无事件:{}",matchId,linkId);
        }
    }


    /**
     * 发送到实时事件
     * @param matchEventInfoDTO
     */
    public void sendMatchEventMessage(MatchEventInfoMessage matchEventInfoDTO,Long matchId) {
        Request<MatchEventInfoMessage> request=new Request();
        request.setData(matchEventInfoDTO);
        String linkId = matchEventInfoDTO.getCopyLinkId();
        request.setLinkId(linkId);
        MessageBuilder<Request<MatchEventInfoMessage> > builder = MessageBuilder.withPayload(request)
                .setHeader(MessageConst.PROPERTY_KEYS, linkId);
        //通知预售开售赛事完赛
        boolean spareMqFlag = getSpareMqFlag(matchId+"");
        if (pandaDataMqGatewayevent == 2 && spareMqFlag) {
            String dataSourceCode = matchEventInfoDTO.getDataSourceCode();
            request = new Request<>(matchEventInfoDTO, request.getLinkId(), THIRD_MATCH_EVENT_INFO_API, request.getLinkId(), dataSourceCode);
            spareBaseProducer.send(request);
        } else {
            rocketMqTemplate.send("THIRD_MATCH_EVENT_INFO_API:" + matchEventInfoDTO.getThirdMatchSourceId(), builder.build());
        }
        log.info("::{}::RCS_EVENT_PLAY_SET_SEAL_TMAX下发事件 通知实时服务处理人工下发的事件 request={}", linkId, matchEventInfoDTO);
    }
    public boolean getSpareMqFlag(String standardMatchId) {
        // 快速失败：不满足基本条件直接返回
        if (pandaDataMqGatewayevent != 2 || org.apache.commons.lang3.StringUtils.isBlank(standardMatchId)) {
            return false;
        }
        // 处理备用MQ配置
        if (org.apache.commons.lang3.StringUtils.isBlank(pandaDataMqGatewayMatchId)) {
            return true;
        }

        // 转换为set集合
        Set<String> spareMatchIds = new HashSet<>(
                Arrays.asList(pandaDataMqGatewayMatchId.split(","))
        );
        return spareMatchIds.contains(standardMatchId);
    }

    private void processorMathcEvent(MatchEventInfoMessage matchEventInfoMessage, StandardMatchInfo matchInfo, ThirdMatchInfo thirdMatchInfo,String linkId) {
        matchEventInfoMessage.setCanceled(0);//未取消
//        matchEventInfoMessage.setDataSourceCode(sell.getBusinessEvent());
        matchEventInfoMessage.setSourceType("1"); //常规事件
        matchEventInfoMessage.setEventTime(System.currentTimeMillis());
        matchEventInfoMessage.setCopyLinkId(linkId);
        matchEventInfoMessage.setThirdMatchSourceId(thirdMatchInfo.getThirdMatchSourceId());
        matchEventInfoMessage.setIsErrorEndEvent(0);
        matchEventInfoMessage.setThirdEventId("PA_Event:"+ UUIdUtils.getId());
        matchEventInfoMessage.setExtrainfo("reject-auto");
        matchEventInfoMessage.setRemark("玩法集封盘拒单事件");
        matchEventInfoMessage.setSportId(matchInfo.getSportId());
        matchEventInfoMessage.setMatchPeriodId(matchInfo.getMatchPeriodId());
        matchEventInfoMessage.setAddition5("1");
    }

//        加时进球玩法集   FOOTBALL_OVERTIME
//        常规进球玩法集   FOOTBALL_GOAL
//        加时角球玩法集   FOOTBALL_CORNER_OVERTIME
//        常规角球玩法集   FOOTBALL_CORNER
//        加时罚牌玩法集   FOOTBALL_PENALTY_CARD
//        常规罚牌球玩法集  FOOTBALL_SERVE_OVERTIME
    private String getEventCode(String str) {
        if("FOOTBALL_OVERTIME".equals(str) || "FOOTBALL_GOAL".equals(str)){
            return "Tmax_event_goal";
        }else if ("FOOTBALL_CORNER_OVERTIME".equals(str) || "FOOTBALL_CORNER".equals(str)){
            return "Tmax_event_corner";
        }else if ("FOOTBALL_PENALTY_CARD".equals(str) || "FOOTBALL_SERVE_OVERTIME".equals(str)){
            return "Tmax_event_booking";
        }else{
            return "";
        }
    }

}
