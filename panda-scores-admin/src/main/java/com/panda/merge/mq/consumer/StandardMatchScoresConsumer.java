package com.panda.merge.mq.consumer;

import cn.hutool.core.lang.TypeReference;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.advertise.dto.MatchScoreAndTimeVo;
import com.panda.merge.advertise.service.CommonAdvertiseService;
import com.panda.merge.common.enums.EventCodeEnum;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.RedisService;
import com.panda.merge.config.mq.ConsumerConfigDetail;
import com.panda.merge.config.mq.MqConsumerConfig;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.scores.StandardMatchScoreChangeDTO;
import com.panda.merge.model.*;
import com.panda.merge.mq.message.CommonStandardScoresDto;
import com.panda.merge.mq.message.MatchEventInfoMessage;
import com.panda.merge.mq.producer.CommonProducer;
import com.panda.merge.mq.spare.SpareBaseProducer;
import com.panda.merge.repository.StandardMatchInfoRepository;
import com.panda.merge.repository.StandardSportMarketSellRepository;
import com.panda.merge.repository.ThirdMatchInfoRepository;
import com.panda.merge.service.ThirdSportTypeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.panda.merge.constant.ConstantSystem.THIRD_MATCH_EVENT_INFO_API;


/**
 * 监控下发下游的标准比分变化
 * 触发tMax事件
 */
@Slf4j
@Component
@DependsOn("scoresAdminApplication")
public class StandardMatchScoresConsumer extends AbstractSingleMessageMQConsumer<Request<CommonStandardScoresDto>>{

    private static final String TOPIC ="STANDARD_MATCH_SCORES";
    private static final String CONSUMER_GROUP="scores-group-STANDARD_MATCH_SCORES";


    @NacosValue(value = "${panda.data.mq.gateway.event:1}", autoRefreshed = true)
    private int pandaDataMqGatewayevent;
    @NacosValue(value = "${panda.data.mq.gateway.matchId:1,}", autoRefreshed = true)
    private String pandaDataMqGatewayMatchId;
    @Autowired
    private CommonAdvertiseService commonAdvertiseService;
    @Autowired
    private RocketMQTemplate rocketMqTemplate;
    @Autowired
    public ThirdSportTypeService thirdSportTypeService;
    @Autowired
    RedisService redisService;
    @Autowired
    SpareBaseProducer spareBaseProducer;
    @Autowired
    StandardSportMarketSellRepository standardSportMarketSellRepository;
    @Autowired
    StandardMatchInfoRepository standardMatchInfoRepository;
    @Autowired
    ThirdMatchInfoRepository thirdMatchInfoRepository;
    @NacosValue(value = "${datacenter.scores.switch:false}", autoRefreshed = true)
    private Boolean datacenterMergeSwitch;
    @Autowired
    CommonProducer commonProducer;
    @Override
    MqConsumerConfig buildConfig() {
        ConsumerConfigDetail consumerConfigDetail = ConsumerConfigDetail.builder()
                .threadNumber(20).pullBatchSize(64).build();
        return new MqConsumerConfig(TOPIC, CONSUMER_GROUP
                , new TypeReference<Request<CommonStandardScoresDto>>() {},consumerConfigDetail);
    }
    ScheduledThreadPoolExecutor executorService = new ScheduledThreadPoolExecutor(32);


    @Override
    public void processMessage(Request<CommonStandardScoresDto> request) {
        log.info("StandardMatchScoresConsumer MQ消费数据开始...{}",datacenterMergeSwitch);
        if (datacenterMergeSwitch && commonProducer.getDatacenterMatchIds(request.getData().getStandardMatchId().toString())) {
            //MQ消息转发给数据中心
            commonProducer.asyncSend(request, "datacenter-STANDARD_MATCH_SCORES",request.getLinkId());
            return;
        }
        StopWatch extWatch = new StopWatch();
        extWatch.start();
        String linkId = request.getLinkId();
        if (request.getData() == null) {
            return;
        }
        try{
            //监控的比分事件
            List<String> scoreCodes = Arrays.asList("corner","fa_card","red_card","yellow_card");
            if(null==request.getData().getEventCode()){
                return;
            }
            //比分变更通知赔率服务
            sendScoreChangeApi(request.getData());
            if(!scoreCodes.contains(request.getData().getEventCode())){
                return;
            }

//            //休息1.5秒，事件下发到实时服务
//            Thread.currentThread().sleep(1500);
            executorService.schedule(() ->  checkEndSendEvent(request, linkId), 1500, TimeUnit.MILLISECONDS);
//            checkEndSendEvent(request, linkId);

        }catch(Exception e){
            log.error("{}StandardMatchScoresConsumer触发tMax事件异常:{}",linkId,e);
        }
        extWatch.stop();
        log.info("linkId::{}::StandardMatchScoresConsumer触发tMax事件异常,已处理事件执行耗时：{}", linkId, extWatch.getTotalTimeMillis());
    }

    /**
     * 比分变更通知赔率服务
     * @param data
     */
    private void sendScoreChangeApi(CommonStandardScoresDto data) {
        StandardMatchScoreChangeDTO standardMatchScoreChangeDTO = new StandardMatchScoreChangeDTO();
        standardMatchScoreChangeDTO.setMatchId(data.getStandardMatchId());
        standardMatchScoreChangeDTO.setType(-1);
        if("goal".equals(data.getEventCode())){
            standardMatchScoreChangeDTO.setType(1);
        }else if ("corner".equals(data.getEventCode())){
            standardMatchScoreChangeDTO.setType(3);
        }else if("yellow_card".equals(data.getEventCode())|| "red_card".equals(data.getEventCode())){
            standardMatchScoreChangeDTO.setType(2);
        }
        Request<StandardMatchScoreChangeDTO> request=new Request();
        request.setData(standardMatchScoreChangeDTO);
        request.setLinkId(data.getLinkedId());
        MessageBuilder<Request<StandardMatchScoreChangeDTO> > builder = MessageBuilder.withPayload(request)
                .setHeader(MessageConst.PROPERTY_KEYS, data.getLinkedId());
        //检查备用MQ赛事ID配置
        boolean spareMqFlag = getSpareMqFlag(data.getStandardMatchId()+"");
        if (pandaDataMqGatewayevent == 2 && spareMqFlag) {
            String dataSourceCode = data.getDataSourceCode();
            request = new Request<>(standardMatchScoreChangeDTO, request.getLinkId(), THIRD_MATCH_EVENT_INFO_API, request.getLinkId(), dataSourceCode);
            spareBaseProducer.send(request);
        } else {
            rocketMqTemplate.send("STANDARD_MATCH_SCORE_CHANGE_API:" + data.getLinkedId(), builder.build());
        }
        log.info("::{}::StandardMatchScoresConsumer比分变更通知赔率服务处理人比分逻辑 ", data.getLinkedId());
    }

    private void checkEndSendEvent(Request<CommonStandardScoresDto> request, String linkId) {
        String eventCode;
        CommonStandardScoresDto data = request.getData();
        StandardMatchInfo matchInfo =  standardMatchInfoRepository.selectStandardMatchPrimaryKey(data.getStandardMatchId());
        Long matchId = data.getStandardMatchId();
        StandardSportMarketSell standardSportMarketSells = standardSportMarketSellRepository.selectThirdMatchInfoPrimaryKey(matchId);
        if(null == standardSportMarketSells){
            log.info("StandardMatchScoresConsumer触发tMax事件：查询开售信息为空==={}", linkId);
            return;
        }
        if(matchInfo==null){
            log.info("StandardMatchScoresConsumer触发tMax事件,查询赛事不存在，{},{}", linkId,matchId);
            return;
        }
        //查询三方赛事信息
        String businessEvent = standardSportMarketSells.getBusinessEvent();
        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoRepository.selectByStandardIdAndDataSourceCode(matchId,businessEvent);
        if (thirdMatchInfo == null ) {
            log.info("StandardMatchScoresConsumer触发tMax事件：找不到三方赛事信息!{}", linkId);
            return;
        }
        Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreateApi(thirdMatchInfo.getId());
        if (!response.isSuccess()) {
            log.info("StandardMatchScoresConsumer触发tMax事件-创建比分失败：{}，{}", linkId,response.getMsg());
            return;
        }
        String sportIdStr = thirdSportTypeService.getThirdSportId(matchInfo.getSportId(),businessEvent);
        if(businessEvent.equals("PD")||businessEvent.equals("PD2")){
            sportIdStr=matchInfo.getSportId().toString();
        }
        if(sportIdStr==null){
            log.info("StandardMatchScoresConsumer触发tMax事件：找不到三方体种信息!{}，{}", linkId,businessEvent);
            return;
        }
        if("PD".equals(businessEvent) || "PD2".equals(businessEvent)){
            sportIdStr=matchInfo.getSportId().toString();
        }
        log.info("StandardMatchScoresConsumer触发tMax事件：，开始获取开关和事件编码{}", linkId);

        eventCode = checkAndSetEventCode(request.getData().getEventCode(),matchInfo.getId(),data.getPeriodId());
        if(StringUtils.isEmpty(eventCode)){
            return;
        }
        log.info("StandardMatchScoresConsumer触发tMax事件：，获取开关和事件编码{},{}", linkId,eventCode);

        //封装事件信息
        MatchEventInfoMessage matchEventInfoMessage = new MatchEventInfoMessage();
        processorMathcEvent(matchEventInfoMessage, response, businessEvent, data,eventCode);
        matchEventInfoMessage.setSportId(Long.valueOf(sportIdStr));
        matchEventInfoMessage.setMatchPeriodId(matchInfo.getMatchPeriodId());
        matchEventInfoMessage.setSecondsFromStart(data.getSecondFromStart()+2);
        matchEventInfoMessage.setHomeAway(data.getHomeAway());

        sendMatchEventMessage(matchEventInfoMessage, linkId,matchId);
    }

    private String checkAndSetEventCode(String scoreCode, Long matchId,Long periodId) {
        String eventCode = "Tmax_event_corner";
        String categoryType = "corner";
        //角球
        if(EventCodeEnum.CORNER.code.equals(scoreCode)){
            categoryType = "corner";
            //加时角球
            if(periodId==41L || periodId==42L){
                categoryType = "otCorner";
            }
        }else{
            //罚牌
            categoryType = "faCard";
            //加时罚牌
            if(periodId==41L || periodId==42L){
                categoryType = "otFaCard";
            }
        }
        String redisKey = "MATCH:CATEGORYSET:SWITCH:"+matchId+"_"+categoryType;
        if(redisService.hasKey(redisKey)){
            Object obj = redisService.get(redisKey);
            log.info("{} StandardMatchScoresConsumer触发tMax事件：redisKey:{}，value={}",matchId,redisKey,obj);
            int isOpen = 1;
            try{
                isOpen = Integer.parseInt(obj.toString());
            }catch(Exception e){
                log.error("{}StandardMatchScoresConsumer触发tMax事件异常,取默认值1",redisKey,e);
            }
            if(isOpen==1){
                if(EventCodeEnum.CORNER.code.equals(scoreCode)){
                    eventCode = "Tmax_event_corner";
                }else{
                    //罚牌
                    eventCode = "Tmax_event_booking";
                }
            }else{
                if(EventCodeEnum.CORNER.code.equals(scoreCode)){
                    eventCode = "safe_event_corner";
                }else{
                    //罚牌
                    eventCode = "safe_event_booking";
                }
            }
            return eventCode;
        }else{
            if(EventCodeEnum.CORNER.code.equals(scoreCode)){
                eventCode = "Tmax_event_corner";
            }else{
                //罚牌
                eventCode = "Tmax_event_booking";
            }
        }
        return eventCode;
    }

    /**
     * 组装下发消息
     * @param matchEventInfoMessage
     * @param response
     * @param businessEvent
     */
    private void processorMathcEvent(MatchEventInfoMessage matchEventInfoMessage, Response<MatchScoreAndTimeVo> response, String businessEvent, CommonStandardScoresDto request,String eventCode) {
        ThirdMatchInfo thirdMatchInfo = response.getData().getThirdMatchInfo();
        MatchScoresInfo matchScoresInfo = response.getData().getMatchScoresInfo();
        matchEventInfoMessage.setCanceled(0);//未取消
        matchEventInfoMessage.setDataSourceCode(businessEvent);
        matchEventInfoMessage.setSourceType("1");//常规事件
        matchEventInfoMessage.setEventCode(eventCode);
        matchEventInfoMessage.setEventTime(System.currentTimeMillis());
        matchEventInfoMessage.setT1(matchScoresInfo.getT1());
        matchEventInfoMessage.setT2(matchScoresInfo.getT2());
        matchEventInfoMessage.setCopyLinkId(request.getLinkedId()+"scores-auto");
        matchEventInfoMessage.setThirdMatchSourceId(thirdMatchInfo.getThirdMatchSourceId());
        matchEventInfoMessage.setIsErrorEndEvent(0);
        matchEventInfoMessage.setThirdEventId("PA_Event:"+ UUIdUtils.getId());
        matchEventInfoMessage.setExtrainfo("score-auto");
        matchEventInfoMessage.setRemark("比分变更自动触发");
        matchEventInfoMessage.setAddition5(request.getAddition5());
    }

    /**
     * 发送到实时事件
     * @param matchEventInfoDTO
     * @param linkId
     */
    public void sendMatchEventMessage(MatchEventInfoMessage matchEventInfoDTO, String linkId,Long matchId) {
        Request<MatchEventInfoMessage> request=new Request();
        request.setData(matchEventInfoDTO);
        linkId = linkId+"-scores-auto";
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
        log.info("::{}::StandardMatchScoresConsumer触发tMax事件 通知实时服务处理人工下发的事件 request={}", linkId, matchEventInfoDTO);
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
}
