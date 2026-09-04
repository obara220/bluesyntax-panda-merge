package com.panda.merge.advertise.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.advertise.common.Constant;
import com.panda.merge.advertise.dto.FootBallAdvertiseVo;
import com.panda.merge.advertise.dto.FootballMatchEventStatusVo;
import com.panda.merge.advertise.dto.MatchEndCancelDto;
import com.panda.merge.advertise.dto.MatchScoreAndTimeVo;
import com.panda.merge.advertise.dto.MatchScoreCommonVo;
import com.panda.merge.advertise.event.FootBallEventService;
import com.panda.merge.advertise.service.*;
import com.panda.merge.advertise.service.impl.FootBallScoreServiceImpl;
import com.panda.merge.advertise.utils.MatchEventUtils;
import com.panda.merge.advertise.utils.MatchPeriodUtils;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.enums.DataSourceEncrypEnum;
import com.panda.merge.common.enums.TeamTypeEnum;
import com.panda.merge.common.utils.IdWorker;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.PDEventCodeEnum;
import com.panda.merge.constant.ScoreEventCodeSourceEnum;
import com.panda.merge.constant.SportTypeEnum;
import com.panda.merge.dto.CommonItem;
import com.panda.merge.dto.MangoRequest;
import com.panda.merge.dto.MatchEventInfoDTO;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.ThirdMatchStatusDTO;
import com.panda.merge.dto.advertise.ChangeMatchScoreDto;
import com.panda.merge.dto.advertise.ChangeMatchTimeDto;
import com.panda.merge.dto.advertise.InjuryTimeEventDto;
import com.panda.merge.dto.advertise.PDFootBallEventDto;
import com.panda.merge.dto.advertise.PDFootBallMatchEventDto;
import com.panda.merge.dto.advertise.PenaltyScoresEditDto;
import com.panda.merge.dto.advertise.TimeStatusEventDto;
import com.panda.merge.dto.message.StandardMatchStatusMessage;
import com.panda.merge.mapper.MatchScoresEventInfoMapper;
import com.panda.merge.mapper.StandardMatchInfoMapper;
import com.panda.merge.mapper.StandardSportMarketSellMapper;
import com.panda.merge.mapper.ThirdMatchInfoMapper;
import com.panda.merge.model.*;
import com.panda.merge.mq.message.CommonStandardScoresDto;
import com.panda.merge.mq.spare.SpareBaseProducer;
import com.panda.merge.repository.PdMatchInfoRepository;
import com.panda.merge.repository.ThirdMatchInfoRepository;
import com.panda.merge.service.IScoresService;
import com.panda.merge.service.ThirdMatchInfoService;
import com.panda.merge.utils.MessageBuilderUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StopWatch;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.panda.merge.advertise.common.Constant.*;
import static com.panda.merge.advertise.event.impl.FootBallEventServiceImpl.DELETE_EVENT_CODES;
import static com.panda.merge.common.enums.Constant.REDIS_KEY.RONGHE_PD_FOOTBALL_KICK_OFF;
import static com.panda.merge.config.RedisConfig.REDIS_FIVE_MINS_TIME;
import static com.panda.merge.config.RedisConfig.REDIS_WEEK_TIME;
import static com.panda.merge.constant.ConstantSystem.CHANGE_PERIOD_THIRD_MATCH_EVENT_INFO;
import static com.panda.merge.constant.ConstantSystem.CHANGE_PERIOD_THIRD_MATCH_EVENT_INFO_API;
import static com.panda.merge.constant.ConstantSystem.THIRD_MATCH_EVENT_INFO;
import static com.panda.merge.constant.ConstantSystem.THIRD_MATCH_EVENT_INFO_API;


@Service
@Slf4j
public class EventProducer {
    @NacosValue(value = "${panda.data.mq.gateway.event:1}", autoRefreshed = true)
    private int pandaDataMqGatewayevent;
    @NacosValue(value = "${panda.data.mq.gateway.matchId:1,}", autoRefreshed = true)
    private String pandaDataMqGatewayMatchId;

    @Autowired
    private RocketMQTemplate rocketMqTemplate;
    @Autowired
    private MatchScoresEventInfoMapper matchScoresEventInfoMapper;
    @Autowired
    FootBallAdvertiseService footBallAdvertiseService;
    @Autowired
    CommonAdvertiseService commonAdvertiseService;
    @Autowired
    TennisAdvertiseService tennisAdvertiseService;
    @Autowired
    SpareBaseProducer spareBaseProducer;
    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;

//    @Autowired
//    ThirdMatchInfoMapper thirdMatchInfoMapper;
//    @Autowired
//    StandardMatchInfoMapper standardMatchInfoMapper;

    @Autowired
    IScoresService scoresService;

    @Autowired
    private RedisService redisService;
    @Autowired
    MessageBuilderUtils messageBuilderUtils;

    @Autowired
    StandardSportMarketSellMapper standardSportMarketSellMapper;

    @Autowired
    private FootBallEventService footBallEventService;

    @Autowired
    private PdMatchInfoRepository pdMatchInfoRepository;

    @Autowired
    private FootBallScoreServiceImpl footBallScoreServiceImpl;

    @Autowired
    private ThirdMatchInfoRepository thirdMatchInfoRepository;

    @Autowired
    private CommonScoreEventService commonScoreEventService;

    private static String  PD_FOOTBALL_SCORE="PD_FOOTBALL_SCORE";
    private static String  PD_FOOTBALL_EVENT="PD_FOOTBALL_EVENT";

    private static final List<PDEventCodeEnum> VAR_FE_SHOW_CODES = Arrays.asList(PDEventCodeEnum.CANCELED_VAR_PENALTY,
            PDEventCodeEnum.VAR_RED_CARD, PDEventCodeEnum.VAR_YELLOW_CARD, PDEventCodeEnum.CANCELED_VAR_RED_CARD, PDEventCodeEnum.VAR_GOAL,
            PDEventCodeEnum.CANCELED_VAR_GOAL, PDEventCodeEnum.PENALTY_GOAL);

    public void sendPDEventInfo(MatchEventInfoDTO eventInfoDTO)
    {
        // 存放事件时间，用于报球板事件监控
        String actionMonitorKey =String.format(ACTION_MONITER_KEY,eventInfoDTO.getThirdMatchSourceId());
        if (eventInfoDTO.getSportId().compareTo(SportTypeEnum.FOOTBALL.getValue()) ==0 && ScoreEventCodeSourceEnum.getResult(eventInfoDTO.getDataSourceCode())) {
            log.info("::{}::pdFootballClickEventRedis",actionMonitorKey);
            redisService.set(actionMonitorKey, eventInfoDTO.getEventTime());
        }
        MatchScoresEventInfo matchScoresEventInfo = new MatchScoresEventInfo();
        BeanUtils.copyProperties( eventInfoDTO, matchScoresEventInfo);
        matchScoresEventInfo.setCreateTime(System.currentTimeMillis());
        matchScoresEventInfo.setLinkId(eventInfoDTO.getCopyLinkId());
        matchScoresEventInfo.setId(IdWorker.getId());
        matchScoresEventInfo.setExtraInfo(eventInfoDTO.getExtrainfo());
        if ("penalty_goal".equals(matchScoresEventInfo.getEventCode()) && !"no_retake_pen".equals(eventInfoDTO.getAddition2())) {
            matchScoresEventInfo.setAddition8("1");
        }
        // 兼容点球大战的实时比分
        if ( null != matchScoresEventInfo.getMatchPeriodId() && "50".equals(matchScoresEventInfo.getMatchPeriodId().toString()) ) {
            Integer homeScore = commonScoreEventService.getCurrentPenaltyScore( eventInfoDTO.getCopyLinkId(), matchScoresEventInfo.getDataSourceCode(), matchScoresEventInfo.getThirdMatchSourceId(), TeamTypeEnum.HOME.code );
            Integer awayScore = commonScoreEventService.getCurrentPenaltyScore( eventInfoDTO.getCopyLinkId(), matchScoresEventInfo.getDataSourceCode(), matchScoresEventInfo.getThirdMatchSourceId(), TeamTypeEnum.AWAY.code );
            matchScoresEventInfo.setAddition1( null == homeScore ? null : homeScore.toString());
            matchScoresEventInfo.setAddition2(  null == awayScore ? null : awayScore.toString());
        }
        matchScoresEventInfoMapper.insert(matchScoresEventInfo);

        eventInfoDTO.setPossibleEventId(matchScoresEventInfo.getId());
        if ("time_start".equals(eventInfoDTO.getEventCode()) && eventInfoDTO.getSecondsFromStart() == 0L) {
            if (eventInfoDTO.getSportId() == 2L) {
                eventInfoDTO.setSecondsFromStart(eventInfoDTO.getPeriodRemainingSeconds());
            } else {
                eventInfoDTO.setSecondsFromStart(1L);
            }
        }
        // 重踢
        if ("retake_pen".equals(eventInfoDTO.getAddition6()) && !"kick_off".equals(eventInfoDTO.getEventCode())) {
            eventInfoDTO.setEventCode("retake_pen");
        }
        // 没有重踢
        if ("no_retake_pen".equals(eventInfoDTO.getAddition6()) && !"kick_off".equals(eventInfoDTO.getEventCode())) {
            eventInfoDTO.setEventCode("no_retake_pen");
        }
        Request<MatchEventInfoDTO> reqMessage = new Request<>();
        reqMessage.setLinkId(eventInfoDTO.getCopyLinkId());
        reqMessage.setData(eventInfoDTO);
        MessageBuilder<Request<MatchEventInfoDTO>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, eventInfoDTO.getCopyLinkId());
        String dataSourceCode = eventInfoDTO.getDataSourceCode();
        Request<MatchEventInfoDTO> request = new Request<>(eventInfoDTO, reqMessage.getLinkId(), THIRD_MATCH_EVENT_INFO_API, reqMessage.getLinkId(), dataSourceCode);
        boolean spareMqFlag = getSpareMqFlag(eventInfoDTO.getThirdMatchSourceId());
        if (pandaDataMqGatewayevent == 2 && spareMqFlag) {
            spareBaseProducer.send(request);
        } else {
            rocketMqTemplate.send(THIRD_MATCH_EVENT_INFO_API +":" + eventInfoDTO.getCopyLinkId(), builder.build());
        }

        // 对于var进行单独处理
        if (DELETE_EVENT_CODES.contains(eventInfoDTO.getEventCode())) {
            MatchEventInfoDTO targetEventInfo = new MatchEventInfoDTO();
            BeanUtils.copyProperties(eventInfoDTO, targetEventInfo);
            switch (eventInfoDTO.getEventCode()) {
                case "var_goal":
                case "penalty_goal":
                    targetEventInfo.setEventCode(PDEventCodeEnum.GOAL.getEventCode());
                    break;
                case "var_red_card":
                    targetEventInfo.setEventCode(PDEventCodeEnum.RED_CARD.getEventCode());
                    break;
                case "var_yellow_card":
                    targetEventInfo.setEventCode(PDEventCodeEnum.YELLOW_CARD.getEventCode());
            }
            targetEventInfo.setCopyLinkId(targetEventInfo.getCopyLinkId()+"-1");
            targetEventInfo.setExtrainfo(targetEventInfo.getExtrainfo()+"-1");
            targetEventInfo.setThirdEventId(targetEventInfo.getThirdEventId()+"-1");
            reqMessage.setLinkId(targetEventInfo.getCopyLinkId());
            reqMessage.setData(targetEventInfo);
            dataSourceCode = targetEventInfo.getDataSourceCode();
            request = new Request<>(targetEventInfo, reqMessage.getLinkId(), THIRD_MATCH_EVENT_INFO_API, reqMessage.getLinkId(), dataSourceCode);
            if (pandaDataMqGatewayevent == 2 && spareMqFlag) {
                spareBaseProducer.send(request);
            } else {
                rocketMqTemplate.send(THIRD_MATCH_EVENT_INFO_API +":" + targetEventInfo.getCopyLinkId(), builder.build());
            }
        }
        log.info("::{}::开始组装赛事比分事件并下发,topic:THIRD_MATCH_EVENT_INFO_API", eventInfoDTO.getCopyLinkId());
    }

    public void updateMatchScoresEventInfo(MatchEventInfoDTO eventInfoDTO) {
        MatchScoresEventInfo matchScoresEventInfo = new MatchScoresEventInfo();
        BeanUtils.copyProperties(eventInfoDTO, matchScoresEventInfo);
        matchScoresEventInfo.setCreateTime(System.currentTimeMillis());
        matchScoresEventInfo.setLinkId(eventInfoDTO.getCopyLinkId());
        matchScoresEventInfo.setId(IdWorker.getId());
        matchScoresEventInfoMapper.insert(matchScoresEventInfo);
    }

    public void sendPDBasketballEditEventInfo(MatchEventInfoDTO eventInfoDTO) {
        MatchScoresEventInfo matchScoresEventInfo = new MatchScoresEventInfo();
        BeanUtils.copyProperties(eventInfoDTO, matchScoresEventInfo);
        eventInfoDTO.setPossibleEventId(matchScoresEventInfo.getId());
        if ("time_start".equals(eventInfoDTO.getEventCode()) && eventInfoDTO.getSecondsFromStart() == 0L) {
            if (eventInfoDTO.getSportId() == 2L) {
                eventInfoDTO.setSecondsFromStart(eventInfoDTO.getPeriodRemainingSeconds());
            } else {
                eventInfoDTO.setSecondsFromStart(1L);
            }
        }
        Request<MatchEventInfoDTO> reqMessage = new Request<>();
        reqMessage.setLinkId(eventInfoDTO.getCopyLinkId());
        reqMessage.setData(eventInfoDTO);
        MessageBuilder<Request<MatchEventInfoDTO>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, eventInfoDTO.getCopyLinkId());
        String dataSourceCode = eventInfoDTO.getDataSourceCode();
        Request<MatchEventInfoDTO> request = new Request<>(eventInfoDTO, reqMessage.getLinkId(), THIRD_MATCH_EVENT_INFO_API, reqMessage.getLinkId(), dataSourceCode);
        boolean spareMqFlag = getSpareMqFlag(eventInfoDTO.getThirdMatchSourceId());
        if (pandaDataMqGatewayevent == 2 && spareMqFlag) {
            spareBaseProducer.send(request);
        } else {
            rocketMqTemplate.send(THIRD_MATCH_EVENT_INFO_API + ":" + eventInfoDTO.getCopyLinkId(), builder.build());
        }
        log.info("::{}::开始组装赛事比分事件并下发,topic:THIRD_MATCH_EVENT_INFO_API", eventInfoDTO.getCopyLinkId());
    }

    public void sendBasketballPDEventInfo(MatchEventInfoDTO eventInfoDTO, ChangeMatchTimeDto dto) {
        MatchScoresEventInfo matchScoresEventInfo = new MatchScoresEventInfo();
        BeanUtils.copyProperties(eventInfoDTO, matchScoresEventInfo);
        if (null == dto.getType() || 0 != dto.getType()) {
            matchScoresEventInfo.setCreateTime(System.currentTimeMillis());
            matchScoresEventInfo.setLinkId(eventInfoDTO.getCopyLinkId());
            matchScoresEventInfo.setId(IdWorker.getId());
            matchScoresEventInfo.setExtraInfo(eventInfoDTO.getExtrainfo());
            matchScoresEventInfoMapper.insert(matchScoresEventInfo);
        }
        eventInfoDTO.setPossibleEventId(matchScoresEventInfo.getId());
        if ("time_start".equals(eventInfoDTO.getEventCode()) && eventInfoDTO.getSecondsFromStart() == 0L) {
            if (eventInfoDTO.getSportId() == 2L) {
                eventInfoDTO.setSecondsFromStart(eventInfoDTO.getPeriodRemainingSeconds());
            } else {
                eventInfoDTO.setSecondsFromStart(1L);
            }
        }
        Request<MatchEventInfoDTO> reqMessage = new Request<>();
        reqMessage.setLinkId(eventInfoDTO.getCopyLinkId());
        reqMessage.setData(eventInfoDTO);
        MessageBuilder<Request<MatchEventInfoDTO>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, eventInfoDTO.getCopyLinkId());
        String dataSourceCode = eventInfoDTO.getDataSourceCode();
        Request<MatchEventInfoDTO> request = new Request<>(eventInfoDTO, reqMessage.getLinkId(), THIRD_MATCH_EVENT_INFO_API, reqMessage.getLinkId(), dataSourceCode);
        boolean spareMqFlag = getSpareMqFlag(eventInfoDTO.getThirdMatchSourceId());
        if (pandaDataMqGatewayevent == 2 && spareMqFlag) {
            spareBaseProducer.send(request);
        } else {
            rocketMqTemplate.send(THIRD_MATCH_EVENT_INFO_API + ":" + eventInfoDTO.getCopyLinkId(), builder.build());
        }
        log.info("::{}::开始组装赛事比分事件并下发,topic:THIRD_MATCH_EVENT_INFO_API", eventInfoDTO.getCopyLinkId());
    }

    public void sendPDDeleteEventInfo(MatchEventInfoDTO eventInfoDTO) {
        MatchScoresEventInfo matchScoresEventInfo = new MatchScoresEventInfo();
        BeanUtils.copyProperties(eventInfoDTO, matchScoresEventInfo);
        eventInfoDTO.setPossibleEventId(matchScoresEventInfo.getId());
        if ("time_start".equals(eventInfoDTO.getEventCode()) && eventInfoDTO.getSecondsFromStart() == 0L) {
            if (eventInfoDTO.getSportId() == 2L) {
                eventInfoDTO.setSecondsFromStart(eventInfoDTO.getPeriodRemainingSeconds());
            } else {
                eventInfoDTO.setSecondsFromStart(1L);
            }
        }
        Request<MatchEventInfoDTO> reqMessage = new Request<>();
        reqMessage.setLinkId(eventInfoDTO.getCopyLinkId());
        reqMessage.setData(eventInfoDTO);
        MessageBuilder<Request<MatchEventInfoDTO>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, eventInfoDTO.getCopyLinkId());
        String dataSourceCode = eventInfoDTO.getDataSourceCode();
        Request<MatchEventInfoDTO> request = new Request<>(eventInfoDTO, reqMessage.getLinkId(), THIRD_MATCH_EVENT_INFO_API, reqMessage.getLinkId(), dataSourceCode);
        boolean spareMqFlag = getSpareMqFlag(eventInfoDTO.getThirdMatchSourceId());
        if (pandaDataMqGatewayevent == 2 && spareMqFlag) {
            spareBaseProducer.send(request);
        } else {
            rocketMqTemplate.send(THIRD_MATCH_EVENT_INFO_API + ":" + eventInfoDTO.getCopyLinkId(), builder.build());
        }
        log.info("::{}::开始组装赛事比分事件并下发,topic:THIRD_MATCH_EVENT_INFO_API", eventInfoDTO.getCopyLinkId());
    }


    public void sendPD2EventInfo(  Response<MatchScoreAndTimeVo> response,MatchEventInfoDTO eventInfoDTO)
    {
        //根据事件时间 和阶段 补充对应比分
        MatchScoresInfo matchScoresInfo = scoresService.checkBasketPeriodAndSixScore(response.getData() ,eventInfoDTO.getMatchPeriodId(),eventInfoDTO.getSecondsFromStart());
        Integer matchLength = response.getData().getStandardMatchInfo().getMatchLength();
        Long period = response.getData().getMatchScoresInfo().getPeriod();
        Long sixPeriod = MatchPeriodUtils.getBasketSixPeriod(period, eventInfoDTO.getSecondsFromStart(), matchLength);
        if (sixPeriod != null) {
            eventInfoDTO.setAddition3(sixPeriod + "");
        }
        Request<MatchEventInfoDTO> reqMessage = new Request<>();
        reqMessage.setLinkId(eventInfoDTO.getCopyLinkId());
        reqMessage.setData(eventInfoDTO);
        MessageBuilder<Request<MatchEventInfoDTO>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, eventInfoDTO.getCopyLinkId());
        String dataSourceCode = eventInfoDTO.getDataSourceCode();
        Request<MatchEventInfoDTO> request = new Request<>(eventInfoDTO, reqMessage.getLinkId(), THIRD_MATCH_EVENT_INFO_API, reqMessage.getLinkId(), dataSourceCode);
        boolean spareMqFlag = getSpareMqFlag(eventInfoDTO.getThirdMatchSourceId());
        if (pandaDataMqGatewayevent == 2 && spareMqFlag) {
            spareBaseProducer.send(request);
        } else {
            rocketMqTemplate.send(THIRD_MATCH_EVENT_INFO_API +":" + eventInfoDTO.getCopyLinkId(), builder.build());
        }
        log.info("::{}::开始组装赛事比分事件并下发,topic:THIRD_MATCH_EVENT_INFO_API", eventInfoDTO.getCopyLinkId());
    }
    public void sendChangePeriodPDEventInfo(MatchEventInfoDTO eventInfoDTO) {
        eventInfoDTO.setPossibleEventId(IdWorker.getId());
        if ("time_start".equals(eventInfoDTO.getEventCode()) && eventInfoDTO.getSecondsFromStart() == 0L) {
            eventInfoDTO.setSecondsFromStart(1L);
        }
        Request<MatchEventInfoDTO> reqMessage = new Request<>();
        reqMessage.setLinkId(eventInfoDTO.getCopyLinkId());
        reqMessage.setData(eventInfoDTO);
        MessageBuilder<Request<MatchEventInfoDTO>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, eventInfoDTO.getCopyLinkId());
        String dataSourceCode = eventInfoDTO.getDataSourceCode();
        Request<MatchEventInfoDTO> request = new Request<>(eventInfoDTO, reqMessage.getLinkId(), CHANGE_PERIOD_THIRD_MATCH_EVENT_INFO_API, reqMessage.getLinkId(), dataSourceCode);
        boolean spareMqFlag = getSpareMqFlag(eventInfoDTO.getThirdMatchSourceId());
        if (pandaDataMqGatewayevent == 2 && spareMqFlag) {
            spareBaseProducer.send(request);
        } else {
            rocketMqTemplate.send(CHANGE_PERIOD_THIRD_MATCH_EVENT_INFO_API + ":" + eventInfoDTO.getCopyLinkId(), builder.build());
        }
        log.info("::{}::开始组装赛事比分信息并下发,topic:CHANGE_PERIOD_THIRD_MATCH_EVENT_INFO_API", eventInfoDTO.getCopyLinkId());
    }

    public void sendMatchEventInfo(MatchEventInfoDTO eventInfoDTO) {
        eventInfoDTO.setPossibleEventId(IdWorker.getId());
        if (eventInfoDTO.getSecondsFromStart() == 0L) {
            eventInfoDTO.setSecondsFromStart(1L);
        }
        Request<MatchEventInfoDTO> reqMessage = new Request<>();
        reqMessage.setLinkId(eventInfoDTO.getCopyLinkId());
        reqMessage.setData(eventInfoDTO);
        MessageBuilder<Request<MatchEventInfoDTO>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, eventInfoDTO.getCopyLinkId());
        rocketMqTemplate.send(CHANGE_PERIOD_THIRD_MATCH_EVENT_INFO, builder.build());
        log.info("::{}::开始组装赛事比分信息并下发,topic:CHANGE_PERIOD_THIRD_MATCH_EVENT_INFO", eventInfoDTO.getCopyLinkId());
    }

    public void sendPDFootballClickEventInfo(MatchEventInfoDTO eventInfoDTO, Long timeFromStartSecond, Map<String, String> eventCodeNumMap) {
        // 存放事件时间，用于报球板事件监控
        String actionMonitorKey =String.format(ACTION_MONITER_KEY,eventInfoDTO.getThirdMatchSourceId());
        if (eventInfoDTO.getSportId().compareTo(SportTypeEnum.FOOTBALL.getValue()) ==0 && ScoreEventCodeSourceEnum.getResult(eventInfoDTO.getDataSourceCode())) {
            log.info("::{}::pdFootballClickEventRedis",actionMonitorKey);
            redisService.set(actionMonitorKey, eventInfoDTO.getEventTime());
        }
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        MatchScoresEventInfo matchScoresEventInfo = new MatchScoresEventInfo();
        BeanUtils.copyProperties(eventInfoDTO, matchScoresEventInfo);
        matchScoresEventInfo.setAddition7(eventInfoDTO.getExtraInfo());
        matchScoresEventInfo.setCreateTime(System.currentTimeMillis());
        matchScoresEventInfo.setExtraInfo(eventInfoDTO.getExtrainfo());
        matchScoresEventInfo.setLinkId(eventInfoDTO.getCopyLinkId());
        matchScoresEventInfo.setId(IdWorker.getId());
        matchScoresEventInfo.setExtraInfo(eventInfoDTO.getExtrainfo());
        matchScoresEventInfo.setSecondsFromStart(timeFromStartSecond);
        if ("suspension".equals(eventInfoDTO.getEventCode()) || "suspension_over".equals(eventInfoDTO.getEventCode())) {
            matchScoresEventInfo.setHomeAway("all");
            matchScoresEventInfo.setSecondsFromStart(eventInfoDTO.getSecondsFromStart());
        }
        // 兼容点球大战的实时比分
        if ( null != matchScoresEventInfo.getMatchPeriodId() && "50".equals(matchScoresEventInfo.getMatchPeriodId().toString()) ) {
            Integer homeScore = commonScoreEventService.getCurrentPenaltyScore( eventInfoDTO.getCopyLinkId(), matchScoresEventInfo.getDataSourceCode(), matchScoresEventInfo.getThirdMatchSourceId(), TeamTypeEnum.HOME.code );
            Integer awayScore = commonScoreEventService.getCurrentPenaltyScore( eventInfoDTO.getCopyLinkId(), matchScoresEventInfo.getDataSourceCode(), matchScoresEventInfo.getThirdMatchSourceId(), TeamTypeEnum.AWAY.code );
            matchScoresEventInfo.setAddition1( null == homeScore ? null : homeScore.toString());
            matchScoresEventInfo.setAddition2(  null == awayScore ? null : awayScore.toString());
            ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem( DataSourceCodeEnum.PD.getCode(), eventInfoDTO.getThirdMatchSourceId() );
            if( null != thirdMatchInfo ){
                String preventDuplicationKey = String.format(PREVENT_DUPLICATION_KEY, thirdMatchInfo.getId());
                if ( redisService.hasKey(preventDuplicationKey) ) {
                    log.info("preventDuplication-sendPDFootballClickEventInfo, thirdEventId:{}, thirdMatchSourceId:{}", matchScoresEventInfo.getThirdEventId(), eventInfoDTO.getThirdMatchSourceId());
                    matchScoresEventInfo.setAddition8("1");
                } else {
                    log.info("preventDuplication-sendPDFootballClickEventInfo缓存不存在, thirdEventId:{}, thirdMatchSourceId:{}", matchScoresEventInfo.getThirdEventId(), eventInfoDTO.getThirdMatchSourceId());
                }
            } else {
                log.info("preventDuplication-sendPDFootballClickEventInfo三方赛事不存在, thirdEventId:{}, thirdMatchSourceId:{}", matchScoresEventInfo.getThirdEventId(), eventInfoDTO.getThirdMatchSourceId());
            }
        }
        matchScoresEventInfoMapper.insert(matchScoresEventInfo);

        eventInfoDTO.setPossibleEventId(matchScoresEventInfo.getId());
        if ("time_start".equals(eventInfoDTO.getEventCode()) && eventInfoDTO.getSecondsFromStart() == 0L) {
            eventInfoDTO.setSecondsFromStart(1L);
        }
        Request<MatchEventInfoDTO> reqMessage = new Request<>();
        reqMessage.setLinkId(eventInfoDTO.getCopyLinkId());
//        eventInfoDTO.setDataSourceCode("PD");
        reqMessage.setData(eventInfoDTO);
        MessageBuilder<Request<MatchEventInfoDTO>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, eventInfoDTO.getCopyLinkId());
        String dataSourceCode = eventInfoDTO.getDataSourceCode();
        Request<MatchEventInfoDTO> request = new Request<>(eventInfoDTO, reqMessage.getLinkId(), THIRD_MATCH_EVENT_INFO_API, reqMessage.getLinkId(), dataSourceCode);
        boolean spareMqFlag = getSpareMqFlag(eventInfoDTO.getThirdMatchSourceId());
        if (pandaDataMqGatewayevent == 2 && spareMqFlag) {
            spareBaseProducer.send(request);
        } else {
            rocketMqTemplate.send(THIRD_MATCH_EVENT_INFO_API + ":" + eventInfoDTO.getCopyLinkId(), builder.build());
        }

        // 对于var需要再次进行下发事件
        PDEventCodeEnum eventCodeEnum = PDEventCodeEnum.getEventCodeEnum(eventInfoDTO.getEventCode());
        if (eventCodeEnum!=null&&matchScoresEventInfo.getAddition7()!=null&&VAR_FE_SHOW_CODES.contains(eventCodeEnum) && (!"0".equals(matchScoresEventInfo.getAddition7()))) {
            MatchEventInfoDTO targetEventInfo = new MatchEventInfoDTO();
            BeanUtils.copyProperties(eventInfoDTO, targetEventInfo);
            if (PDEventCodeEnum.VAR_GOAL == eventCodeEnum || PDEventCodeEnum.PENALTY_GOAL == eventCodeEnum){
                targetEventInfo.setEventCode(PDEventCodeEnum.GOAL.getEventCode());
            } else if (PDEventCodeEnum.CANCELED_VAR_GOAL==eventCodeEnum || PDEventCodeEnum.CANCELED_VAR_PENALTY==eventCodeEnum) {
                targetEventInfo.setEventCode(PDEventCodeEnum.GOAL.getEventCode());
                targetEventInfo.setCanceled(1);
                targetEventInfo.setExtraInfo(eventCodeNumMap.getOrDefault("oldThirdEventId", null));
                targetEventInfo.setExtrainfo(eventCodeNumMap.getOrDefault("oldThirdEventId", null));
            } else if (PDEventCodeEnum.VAR_RED_CARD==eventCodeEnum) {
                targetEventInfo.setEventCode(PDEventCodeEnum.RED_CARD.getEventCode());
            } else if (PDEventCodeEnum.VAR_YELLOW_CARD ==eventCodeEnum) {
                if ("-1".equals(eventCodeNumMap.getOrDefault(PDEventCodeEnum.RED_CARD.getEventCode(), ""))) {
                    MatchEventInfoDTO redCardInfo = new MatchEventInfoDTO();
                    BeanUtils.copyProperties(eventInfoDTO, redCardInfo);
                    redCardInfo.setEventCode(PDEventCodeEnum.RED_CARD.getEventCode());
                    redCardInfo.setCanceled(1);
                    redCardInfo.setExtraInfo(eventCodeNumMap.getOrDefault("oldThirdEventId", null));
                    redCardInfo.setExtrainfo(eventCodeNumMap.getOrDefault("oldThirdEventId", null));
                    redCardInfo.setCopyLinkId(targetEventInfo.getCopyLinkId()+"-2");
                    redCardInfo.setThirdEventId(targetEventInfo.getThirdEventId()+"-2");
                    redCardInfo.setT1(eventCodeNumMap.getOrDefault("redHome", null) == null ? null:Integer.valueOf(eventCodeNumMap.get("redHome")));
                    redCardInfo.setT2(eventCodeNumMap.getOrDefault("redAway", null) == null ? null:Integer.valueOf(eventCodeNumMap.get("redAway")));
                    reqMessage.setLinkId(redCardInfo.getCopyLinkId());
                    reqMessage.setData(redCardInfo);
                    dataSourceCode = eventInfoDTO.getDataSourceCode();
                    request = new Request<>(eventInfoDTO, reqMessage.getLinkId(), THIRD_MATCH_EVENT_INFO_API, reqMessage.getLinkId(), dataSourceCode);
                    if (pandaDataMqGatewayevent == 2 && spareMqFlag) {
                        spareBaseProducer.send(request);
                    } else {
                        rocketMqTemplate.send(THIRD_MATCH_EVENT_INFO_API + ":" + redCardInfo.getCopyLinkId(), builder.build());
                    }
                }
                targetEventInfo.setEventCode(PDEventCodeEnum.YELLOW_CARD.getEventCode());
                targetEventInfo.setCanceled(0);
            } else if (PDEventCodeEnum.CANCELED_VAR_RED_CARD == eventCodeEnum) {
                targetEventInfo.setEventCode(PDEventCodeEnum.RED_CARD.getEventCode());
                targetEventInfo.setCanceled(1);
                targetEventInfo.setExtraInfo(eventCodeNumMap.getOrDefault("oldThirdEventId", null));
                targetEventInfo.setExtrainfo(eventCodeNumMap.getOrDefault("oldThirdEventId", null));
            }
            targetEventInfo.setAddition9("true");
            targetEventInfo.setCopyLinkId(targetEventInfo.getCopyLinkId()+"-1");
            targetEventInfo.setThirdEventId(targetEventInfo.getThirdEventId()+"-1");
            reqMessage.setLinkId(targetEventInfo.getCopyLinkId());
            reqMessage.setData(targetEventInfo);
            dataSourceCode = targetEventInfo.getDataSourceCode();
            request = new Request<>(targetEventInfo, reqMessage.getLinkId(), THIRD_MATCH_EVENT_INFO_API, reqMessage.getLinkId(), dataSourceCode);
            if (pandaDataMqGatewayevent == 2 && spareMqFlag) {
                spareBaseProducer.send(request);
            } else {
                rocketMqTemplate.send(THIRD_MATCH_EVENT_INFO_API + ":" + targetEventInfo.getCopyLinkId(), builder.build());
            }
        }
        stopWatch.stop();
        log.info("EventProducer-sendPDFootballClickEventInfo-耗时={}, thirdMatchId={}",stopWatch.getTotalTimeMillis(),eventInfoDTO.getThirdEventId());
        log.info("::{}::开始组装赛事比分事件并下发,topic:THIRD_MATCH_EVENT_INFO_API", eventInfoDTO.getCopyLinkId());
    }


    public void sendShotOnEvent( String shotLinkId, String dataSourceCode, MatchEventInfoDTO shotTargetEventInfo)
    {
        boolean spareMqFlag = getSpareMqFlag( shotTargetEventInfo.getThirdMatchSourceId());
        Request<MatchEventInfoDTO> shotTargetMessage = new Request<>();
        shotTargetMessage.setLinkId( shotLinkId);
        shotTargetMessage.setData( shotTargetEventInfo);
        MessageBuilder<Request<MatchEventInfoDTO>> shotTargetBuilder = MessageBuilder.withPayload(shotTargetMessage).setHeader(MessageConst.PROPERTY_KEYS, shotLinkId);

        Request<MatchEventInfoDTO> targetRequest = new Request<>( shotTargetEventInfo, shotLinkId, THIRD_MATCH_EVENT_INFO_API, shotLinkId, dataSourceCode);
        if (pandaDataMqGatewayevent == 2 && spareMqFlag)
        {
            spareBaseProducer.send( targetRequest);
        } else
        {
            rocketMqTemplate.send(THIRD_MATCH_EVENT_INFO_API + ":" + shotLinkId, shotTargetBuilder.build());
        }
    }

    public void sendPDFootballInjuryTimeClickEventInfo(MatchEventInfoDTO eventInfoDTO, Long timeFromStartSecond) {
        MatchScoresEventInfo matchScoresEventInfo = new MatchScoresEventInfo();
        BeanUtils.copyProperties(eventInfoDTO, matchScoresEventInfo);
        matchScoresEventInfo.setCreateTime(System.currentTimeMillis());
        matchScoresEventInfo.setExtraInfo(eventInfoDTO.getExtrainfo());
        matchScoresEventInfo.setLinkId(eventInfoDTO.getCopyLinkId());
        matchScoresEventInfo.setId(IdWorker.getId());
        matchScoresEventInfo.setExtraInfo(eventInfoDTO.getExtrainfo());
        matchScoresEventInfo.setSecondsFromStart(timeFromStartSecond);
        matchScoresEventInfoMapper.insert(matchScoresEventInfo);
        eventInfoDTO.setPossibleEventId(matchScoresEventInfo.getId());
        if ("time_start".equals(eventInfoDTO.getEventCode()) && eventInfoDTO.getSecondsFromStart() == 0L) {
            eventInfoDTO.setSecondsFromStart(1L);
        }
        eventInfoDTO.setSecondsFromStart(timeFromStartSecond);
        Request<MatchEventInfoDTO> reqMessage = new Request<>();
        reqMessage.setLinkId(eventInfoDTO.getCopyLinkId());
        reqMessage.setData(eventInfoDTO);
        MessageBuilder<Request<MatchEventInfoDTO>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, eventInfoDTO.getCopyLinkId());
        String dataSourceCode = eventInfoDTO.getDataSourceCode();
        Request<MatchEventInfoDTO> request = new Request<>(eventInfoDTO, reqMessage.getLinkId(), THIRD_MATCH_EVENT_INFO_API, reqMessage.getLinkId(), dataSourceCode);
        boolean spareMqFlag = getSpareMqFlag(eventInfoDTO.getThirdMatchSourceId());
        if (pandaDataMqGatewayevent == 2 && spareMqFlag) {
            spareBaseProducer.send(request);
        } else {
            rocketMqTemplate.send(THIRD_MATCH_EVENT_INFO_API + ":" + eventInfoDTO.getCopyLinkId(), builder.build());
        }
        log.info("injury_time::{}::开始组装赛事比分事件并下发,topic:THIRD_MATCH_EVENT_INFO_API, secondsFromStart: {}",
                eventInfoDTO.getCopyLinkId(), eventInfoDTO.getSecondsFromStart());
    }

//    public void sendPDBasketballEvent(MatchEventInfoDTO eventInfoDTO)
//    {
//        MatchScoresEventInfo matchScoresEventInfo=new MatchScoresEventInfo();
//        BeanUtils.copyProperties(eventInfoDTO, matchScoresEventInfo);
//        eventInfoDTO.setPossibleEventId(matchScoresEventInfo.getId());
//        Request<MatchEventInfoDTO> reqMessage = new Request<>();
//        reqMessage.setLinkId(eventInfoDTO.getCopyLinkId());
//        reqMessage.setData(eventInfoDTO);
//        MessageBuilder<Request<MatchEventInfoDTO>> builder = MessageBuilder.withPayload(reqMessage)
//                .setHeader(MessageConst.PROPERTY_KEYS, eventInfoDTO.getCopyLinkId());
//        rocketMqTemplate.send(THIRD_MATCH_EVENT_INFO_API +":" + eventInfoDTO.getCopyLinkId(), builder.build());
//        log.info("::{}::开始组装赛事比分事件并下发,topic:THIRD_MATCH_EVENT_INFO_API", eventInfoDTO.getCopyLinkId());
//    }

    public void sendPDFootballEventInfo(MatchEventInfoDTO eventInfoDTO)
    {
        MatchScoresEventInfo matchScoresEventInfo=new MatchScoresEventInfo();
        BeanUtils.copyProperties(eventInfoDTO, matchScoresEventInfo);
        matchScoresEventInfo.setCreateTime(System.currentTimeMillis());
        matchScoresEventInfo.setExtraInfo(eventInfoDTO.getExtrainfo());
        matchScoresEventInfo.setLinkId(eventInfoDTO.getCopyLinkId());
        matchScoresEventInfo.setId(IdWorker.getId());
        matchScoresEventInfo.setExtraInfo(eventInfoDTO.getExtrainfo());
        if ("game_on".equals(eventInfoDTO.getEventCode()) || "temporary_interruption".equals(eventInfoDTO.getEventCode())) {
            matchScoresEventInfo.setHomeAway("all");
        }
        matchScoresEventInfoMapper.insert(matchScoresEventInfo);
        eventInfoDTO.setPossibleEventId(matchScoresEventInfo.getId());
        Request<MatchEventInfoDTO> reqMessage = new Request<>();
        reqMessage.setLinkId(eventInfoDTO.getCopyLinkId());
        reqMessage.setData(eventInfoDTO);
        MessageBuilder<Request<MatchEventInfoDTO>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, eventInfoDTO.getCopyLinkId());
        String dataSourceCode = eventInfoDTO.getDataSourceCode();
        Request<MatchEventInfoDTO> request = new Request<>(eventInfoDTO, reqMessage.getLinkId(), THIRD_MATCH_EVENT_INFO_API, reqMessage.getLinkId(), dataSourceCode);
        boolean spareMqFlag = getSpareMqFlag(eventInfoDTO.getThirdMatchSourceId());
        if (pandaDataMqGatewayevent == 2 && spareMqFlag) {
            spareBaseProducer.send(request);
        } else {
            rocketMqTemplate.send(THIRD_MATCH_EVENT_INFO_API +":" + eventInfoDTO.getCopyLinkId(), builder.build());
        }
        log.info("::{}::开始组装赛事比分事件并下发,topic:THIRD_MATCH_EVENT_INFO_API", eventInfoDTO.getCopyLinkId());
    }

    public void sendPDBasketballEventInfo(MatchEventInfoDTO eventInfoDTO)
    {
        MatchScoresEventInfo matchScoresEventInfo=new MatchScoresEventInfo();
        BeanUtils.copyProperties(eventInfoDTO, matchScoresEventInfo);
        eventInfoDTO.setPossibleEventId(matchScoresEventInfo.getId());
        Request<MatchEventInfoDTO> reqMessage = new Request<>();
        reqMessage.setLinkId(eventInfoDTO.getCopyLinkId());
        reqMessage.setData(eventInfoDTO);
        MessageBuilder<Request<MatchEventInfoDTO>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, eventInfoDTO.getCopyLinkId());
        String dataSourceCode = eventInfoDTO.getDataSourceCode();
        Request<MatchEventInfoDTO> request = new Request<>(eventInfoDTO, reqMessage.getLinkId(), THIRD_MATCH_EVENT_INFO_API, reqMessage.getLinkId(), dataSourceCode);
        boolean spareMqFlag = getSpareMqFlag(eventInfoDTO.getThirdMatchSourceId());
        if (pandaDataMqGatewayevent == 2 && spareMqFlag) {
            spareBaseProducer.send(request);
        } else {
            rocketMqTemplate.send(THIRD_MATCH_EVENT_INFO_API +":" + eventInfoDTO.getCopyLinkId(), builder.build());
        }
        log.info("::{}::开始组装赛事比分事件并下发,topic:THIRD_MATCH_EVENT_INFO_API", eventInfoDTO.getCopyLinkId());
    }


    public void sendFreezeSettle(ChangeMatchScoreDto changeMatchScoreDto, Long referenceId, Long sportId) {
//        EventFreezeSettleInfoVo settleInfoVo=new EventFreezeSettleInfoVo();
//        settleInfoVo.setMatchId(referenceId);
//        settleInfoVo.setSportId(sportId.intValue());
//        try {
//            Integer id = Integer.parseInt(changeMatchScoreDto.getOperatorId());
//            settleInfoVo.setOperatorId(id);
//        }catch (Exception e){
//
//        }
//        settleInfoVo.setOperatorName(changeMatchScoreDto.getOperatorName());
//        Request<EventFreezeSettleInfoVo> reqMessage = new Request<>();
//        reqMessage.setLinkId(changeMatchScoreDto.getLinkedId());
//        reqMessage.setData(settleInfoVo);
//        MessageBuilder<Request<EventFreezeSettleInfoVo>> builder = MessageBuilder.withPayload(reqMessage)
//                .setHeader(MessageConst.PROPERTY_KEYS, changeMatchScoreDto.getLinkedId());
//        rocketMqTemplate.send("EVENT_CHECK_FREEZE_SETTLE_TOPIC" +":" + changeMatchScoreDto.getLinkedId(), builder.build());
//        log.info("::{}::开始组装赛事比分信息并下发,topic:EVENT_CHECK_FREEZE_SETTLE_TOPIC,request={}", changeMatchScoreDto.getLinkedId(), JSON.toJSONString(reqMessage));
    }
    /**
     * 标准赛事状态逻辑
     * */
    public void sendMatchStartStatus(ThirdMatchInfo thirdMatchInfo, String linkedId) {
//        StandardMatchInfo standardMatchInfo =standardMatchInfoMapper.selectByPrimaryKey(thirdMatchInfo.getReferenceId());
        StandardMatchInfo standardMatchInfo = pdMatchInfoRepository.getStandardMatchInfo(thirdMatchInfo.getReferenceId(), null);
        if(standardMatchInfo==null){
            return;
        }
        //设置为滚球下发
        standardMatchInfo.setMatchStatus(1);
        thirdMatchInfo.setMatchStatus(1);
        StandardMatchStatusMessage standardMatchStatusMessage = new StandardMatchStatusMessage();
        BeanUtils.copyProperties(standardMatchInfo, standardMatchStatusMessage);
        standardMatchStatusMessage.setStandardMatchId(standardMatchInfo.getId());
        Request<StandardMatchStatusMessage> requestMsg = new Request<>();
        requestMsg.setData(standardMatchStatusMessage);
        requestMsg.setLinkId(linkedId);
        requestMsg.setDataSourceTime(System.currentTimeMillis());
        MessageBuilder<Request<StandardMatchStatusMessage>> requestMessageBuilder = MessageBuilder.withPayload(requestMsg).setHeader(MessageConst.PROPERTY_KEYS, linkedId);
        if (DataSourceCodeEnum.PD.code.equals(thirdMatchInfo.getDataSourceCode()) || DataSourceCodeEnum.PD2.code.equals(thirdMatchInfo.getDataSourceCode())) {
            sendMatchStatusTopic(linkedId,thirdMatchInfo,thirdMatchInfo.getMatchStatus());
            log.info(" ::{}::标准赛事状态信息下发完成2,topic:STANDARD_MATCH_STATUS,标准赛事ID：{}", linkedId, standardMatchInfo.getId());

        } else {
            rocketMqTemplate.send("STANDARD_MATCH_STATUS:" + standardMatchInfo.getMatchStatus(), requestMessageBuilder.build());
            log.info(" ::{}::标准赛事状态信息下发完成,topic:STANDARD_MATCH_STATUS,标准赛事ID：{}", linkedId, standardMatchInfo.getId());
        }
//
//        Request<ThirdMatchStatusDTO> reqMessage = new Request<>();
//        ThirdMatchStatusDTO thirdMatchStatusDTO=new ThirdMatchStatusDTO();
//        thirdMatchStatusDTO.setDataSourceCode(thirdMatchInfo.getDataSourceCode());
//        thirdMatchStatusDTO.setMatchStatus(1);
//        thirdMatchStatusDTO.setModifyTime(System.currentTimeMillis());
//        thirdMatchStatusDTO.setSportId(thirdMatchInfo.getSportId());
//        thirdMatchStatusDTO.setThirdMatchSourceId(thirdMatchInfo.getThirdMatchSourceId());
//        reqMessage.setLinkId(linkedId+"_PD");
//        reqMessage.setData(thirdMatchStatusDTO);
//        MessageBuilder<Request<ThirdMatchStatusDTO>> builder = MessageBuilder.withPayload(reqMessage)
//                .setHeader(MessageConst.PROPERTY_KEYS, linkedId);
//        rocketMqTemplate.send("THIRD_MATCH_STATUS_API" +":" +linkedId, builder.build());
//        log.info("::{}:{}:sendMatchStartStatus ,topic:THIRD_MATCH_STATUS_API,request={}",thirdMatchInfo.getThirdMatchSourceId(),linkedId, JSON.toJSONString(reqMessage));
    }

    public void sendMatchStatus(ThirdMatchInfo thirdMatchInfo, String linkedId,Integer status) {
//        StandardMatchInfo standardMatchInfo =standardMatchInfoMapper.selectByPrimaryKey(thirdMatchInfo.getReferenceId());
        StandardMatchInfo standardMatchInfo = pdMatchInfoRepository.getStandardMatchInfo(thirdMatchInfo.getReferenceId(), null);
        if(standardMatchInfo==null){
            return;
        }
        //设置为滚球下发
        standardMatchInfo.setMatchStatus(1);
        StandardMatchStatusMessage standardMatchStatusMessage = new StandardMatchStatusMessage();
        BeanUtils.copyProperties(standardMatchInfo, standardMatchStatusMessage);
        standardMatchStatusMessage.setStandardMatchId(standardMatchInfo.getId());
        standardMatchStatusMessage.setMatchStatus(status);
        Request<StandardMatchStatusMessage> requestMsg = new Request<>();
        requestMsg.setData(standardMatchStatusMessage);
        requestMsg.setLinkId(linkedId);
        requestMsg.setDataSourceTime(System.currentTimeMillis());
        MessageBuilder<Request<StandardMatchStatusMessage>> requestMessageBuilder = MessageBuilder.withPayload(requestMsg).setHeader(MessageConst.PROPERTY_KEYS, linkedId);
        rocketMqTemplate.send("STANDARD_MATCH_STATUS:" + standardMatchInfo.getMatchStatus(), requestMessageBuilder.build());
        log.info(" ::{}::标准赛事状态信息下发完成,topic:STANDARD_MATCH_STATUS", linkedId);
    }

    /**
     * 赛事状态变更Topic【原为赛事结束专用:sendMatchEndStatus】
     * @param linkId
     * @param thirdMatchInfo
     * @param matchStatus
     */
    public void sendMatchStatusTopic(String linkId, ThirdMatchInfo thirdMatchInfo, Integer matchStatus)
    {
        log.info("::{}::sendMatchStatusTopic入参; thirdMatchInfo:{}, matchStatus:{}", linkId, JSON.toJSONString(thirdMatchInfo), matchStatus);
        Request<ThirdMatchStatusDTO> reqMessage = new Request<>();
        ThirdMatchStatusDTO thirdMatchStatusDTO = new ThirdMatchStatusDTO();
        thirdMatchStatusDTO.setDataSourceCode(thirdMatchInfo.getDataSourceCode());
        //thirdMatchStatusDTO.setMatchStatus(3);
        thirdMatchStatusDTO.setMatchStatus(matchStatus);
        thirdMatchStatusDTO.setModifyTime(System.currentTimeMillis());
        thirdMatchStatusDTO.setSportId(thirdMatchInfo.getSportId());
        thirdMatchStatusDTO.setThirdMatchSourceId(thirdMatchInfo.getThirdMatchSourceId());
        thirdMatchStatusDTO.setDataSourceCode(thirdMatchInfo.getDataSourceCode());
        reqMessage.setLinkId(linkId);
        reqMessage.setData(thirdMatchStatusDTO);
        MessageBuilder<Request<ThirdMatchStatusDTO>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, linkId);
        rocketMqTemplate.send("THIRD_MATCH_STATUS_API" +":" + linkId, builder.build());
        log.info("::{}::topic:THIRD_MATCH_STATUS_API sendMatchStatusTopic请求参数:{}",linkId, JSON.toJSONString(reqMessage) );
    }


    /**
     * 推送广播
     * */
    public void pushFootBallScore(Long thirdMatchId){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(thirdMatchId);
        Response<FootBallAdvertiseVo> footBallAdvertiseVoResponse= footBallAdvertiseService.buildFootBallAdvertiseVo(response.getData());
        Request<String> reqMessage = new Request<>();
        reqMessage.setLinkId(UUIdUtils.getId() +"_PD");
        reqMessage.setData(JSONObject.toJSONString(footBallAdvertiseVoResponse.getData(), SerializerFeature.DisableCircularReferenceDetect,SerializerFeature.WriteMapNullValue));
        MessageBuilder<Request<String>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, reqMessage.getLinkId());
        String dataSourceCode = footBallAdvertiseVoResponse.getData().getDataSourceCode();
        Request<String> request = new Request<>(reqMessage.getData(), reqMessage.getLinkId(), PD_FOOTBALL_SCORE, reqMessage.getLinkId(), dataSourceCode);
        boolean spareMqFlag = getSpareMqFlag(response.getData().getThirdMatchInfo().getThirdMatchSourceId());
        if (pandaDataMqGatewayevent == 2 && spareMqFlag) {
            spareBaseProducer.send(request);
        } else {
            rocketMqTemplate.send(PD_FOOTBALL_SCORE + ":" + reqMessage.getLinkId(), builder.build());
        }
        stopWatch.stop();
        log.info("EventProducer-pushFootBallScore-耗时={}, thirdMatchId={}",stopWatch.getTotalTimeMillis(),thirdMatchId);
        log.info("::{}:{}:sendMatchStartStatus ,topic:PD_FOOTBALL_SCORE",thirdMatchId,reqMessage.getLinkId());

    }

    /**
     * 推送广播
     * */
    public   void pushTenniseScore(Long thirdMatchId){
//        MatchAdvertiseQueryDto matchAdvertiseQueryDto=new MatchAdvertiseQueryDto();
//        matchAdvertiseQueryDto.setThirdMatchId(thirdMatchId);
//        Response<FootBallAdvertiseVo> footBallAdvertiseVoResponse= tennisAdvertiseService.getMatchAdvertiseInfo(matchAdvertiseQueryDto);
//        Request<String> reqMessage = new Request<>();
//        reqMessage.setLinkId(UUIdUtils.getId() +"_PD");
//        reqMessage.setData(JSONObject.toJSONString(footBallAdvertiseVoResponse.getData()));
//        MessageBuilder<Request<String>> builder = MessageBuilder.withPayload(reqMessage)
//                .setHeader(MessageConst.PROPERTY_KEYS, reqMessage.getLinkId());
//        rocketMqTemplate.send(PD_FOOTBALL_SCORE +":" +reqMessage.getLinkId(), builder.build());
//        log.info("::{}:{}:sendMatchStartStatus ,topic:PD_FOOTBALL_SCORE",thirdMatchId,reqMessage.getLinkId());

    }

    /**
     * 推送广播
     * */
    public   void pushFootBallEvent(Long thirdMatchId){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
//        ThirdMatchInfo thirdMatchInfo=thirdMatchInfoMapper.selectByPrimaryKey(thirdMatchId);
        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoRepository.selectThirdMatchInfoByPrimaryKey(thirdMatchId);
        List<PDFootBallEventDto> eventDtoList = footBallEventService.buildEventList(thirdMatchInfo.getThirdMatchSourceId(), thirdMatchInfo.getDataSourceCode(), null);
        PDFootBallMatchEventDto PDFootBallMatchEventDto =new PDFootBallMatchEventDto();
        PDFootBallMatchEventDto.setThirdMatchId(thirdMatchId.toString());
        PDFootBallMatchEventDto.setEventDtoList(eventDtoList);

        Request<String> reqMessage = new Request<>();
        reqMessage.setLinkId(UUIdUtils.getId() +"_PD");
        reqMessage.setData(JSONObject.toJSONString(PDFootBallMatchEventDto));
        MessageBuilder<Request<String>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, reqMessage.getLinkId());
        Request<String> request = new Request<>(reqMessage.getData(), reqMessage.getLinkId(), PD_FOOTBALL_EVENT, reqMessage.getLinkId(), "PD");
        boolean spareMqFlag = getSpareMqFlag(thirdMatchInfo.getThirdMatchSourceId());
        if (pandaDataMqGatewayevent == 2 && spareMqFlag) {
            spareBaseProducer.send(request);
        } else {
            rocketMqTemplate.send(PD_FOOTBALL_EVENT + ":" + reqMessage.getLinkId(), builder.build());
        }
        stopWatch.stop();
        log.info("EventProducer-pushFootBallEvent-耗时={}, thirdMatchId={}",stopWatch.getTotalTimeMillis(),thirdMatchId);
        log.info("::{}:{}:sendMatchStartStatus ,topic:PD_FOOTBALL_EVENT",thirdMatchId,reqMessage.getLinkId());

    }

    /**
     * 推送广播
     * */
    public void pushFootBallCancelEndEvent(Long thirdMatchId, Long periodId) {
//        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoMapper.selectByPrimaryKey(thirdMatchId);
        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoRepository.selectThirdMatchInfoByPrimaryKey(thirdMatchId);
        List<PDFootBallEventDto> eventDtoList = footBallEventService.buildEventList(thirdMatchInfo.getThirdMatchSourceId(), thirdMatchInfo.getDataSourceCode(), null);
        eventDtoList = eventDtoList.stream().filter(t->t.getMatchPeriodId().equals(periodId)).collect(Collectors.toList());
        PDFootBallMatchEventDto PDFootBallMatchEventDto = new PDFootBallMatchEventDto();
        PDFootBallMatchEventDto.setThirdMatchId(thirdMatchId.toString());
        PDFootBallMatchEventDto.setEventDtoList(eventDtoList);

        Request<String> reqMessage = new Request<>();
        reqMessage.setLinkId(UUIdUtils.getId() + "_PD");
        reqMessage.setData(JSONObject.toJSONString(PDFootBallMatchEventDto));
        MessageBuilder<Request<String>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, reqMessage.getLinkId());
        rocketMqTemplate.send(PD_FOOTBALL_EVENT + ":" + reqMessage.getLinkId(), builder.build());
        log.info("::{}:{}:sendMatchStartStatus ,topic:PD_FOOTBALL_EVENT", thirdMatchId, reqMessage.getLinkId());
    }

    public   void pushFootBallPauseContinueEvent(Long thirdMatchId, Integer controlType){
//        ThirdMatchInfo thirdMatchInfo=thirdMatchInfoMapper.selectByPrimaryKey(thirdMatchId);
        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoRepository.selectThirdMatchInfoByPrimaryKey(thirdMatchId);
        List<PDFootBallEventDto> eventDtoList = footBallEventService.buildEventList(thirdMatchInfo.getThirdMatchSourceId(), thirdMatchInfo.getDataSourceCode(), null);
        PDFootBallMatchEventDto PDFootBallMatchEventDto =new PDFootBallMatchEventDto();
        PDFootBallMatchEventDto.setThirdMatchId(thirdMatchId.toString());
        PDFootBallMatchEventDto.setEventDtoList(eventDtoList);

        Request<String> reqMessage = new Request<>();
        reqMessage.setLinkId(UUIdUtils.getId() +"_PD");
        reqMessage.setData(JSONObject.toJSONString(PDFootBallMatchEventDto));
        MessageBuilder<Request<String>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, reqMessage.getLinkId());
        rocketMqTemplate.send(PD_FOOTBALL_EVENT +":" +reqMessage.getLinkId(), builder.build());
        log.info("::{}:{}:sendMatchStartStatus ,topic:PD_FOOTBALL_EVENT",thirdMatchId,reqMessage.getLinkId());
    }

    public void pushFootBallEventPABoard(Long thirdMatchId, Long periodId) {
//        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoMapper.selectByPrimaryKey(thirdMatchId);
        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoRepository.selectThirdMatchInfoByPrimaryKey(thirdMatchId);
        List<PDFootBallEventDto> eventDtoList = footBallEventService.buildEventList(thirdMatchInfo.getThirdMatchSourceId(), thirdMatchInfo.getDataSourceCode(), null);
        eventDtoList = eventDtoList.stream().filter(t->t.getMatchPeriodId().equals(periodId)).collect(Collectors.toList());
        PDFootBallMatchEventDto PDFootBallMatchEventDto = new PDFootBallMatchEventDto();
        PDFootBallMatchEventDto.setThirdMatchId(thirdMatchId.toString());
        PDFootBallMatchEventDto.setEventDtoList(eventDtoList);

        Request<String> reqMessage = new Request<>();
        reqMessage.setLinkId(UUIdUtils.getId() + "_PD");
        reqMessage.setData(JSONObject.toJSONString(PDFootBallMatchEventDto));
        MessageBuilder<Request<String>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, reqMessage.getLinkId());
        rocketMqTemplate.send(PD_FOOTBALL_EVENT + ":" + reqMessage.getLinkId(), builder.build());
        log.info("::{}:{}:sendMatchStartStatus ,topic:PD_FOOTBALL_EVENT", thirdMatchId, reqMessage.getLinkId());
    }

    //删除事件芒果预警MQ下发
    public void manGoEarlyWarning(String linkId,String data, String type) {
        MangoRequest<String> reqMessage = new MangoRequest<>();
        //reqMessage.setLinkId(linkId);
        reqMessage.setData(data);
        reqMessage.setDataSourceCode("1917");
        String data1 = JSONObject.toJSONString(reqMessage).replace("\\n", "\n");
        MessageBuilder<String> builder = MessageBuilder.withPayload(data1)
                .setHeader(MessageConst.PROPERTY_KEYS, linkId);
        rocketMqTemplate.send("MATCH_DELETE_MANGO_EARLY_WARNING:" + type, builder.build());
        log.info("::{}::下发删除事件芒果预警消息下发完毕,topic:MATCH_DELETE_MANGO_EARLY_WARNING,request={}", linkId, data1);
    }

    /**
     * PD 报球板删除事件芒果报警
     * @param commonOldItem
     * @param oldMatchInfo
     */
    public void deleteEventPDManGoEarlyWarning(MatchScoreAndTimeVo data, CommonItem commonOldItem, MatchScoresEventInfo oldMatchInfo, String env) {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = sdf.format(new Date());
            String sport = "FootBall";//默认足球
            if (oldMatchInfo.getSportId().equals(2L)) {
                sport = "BasketBall";
            }
            StandardMatchInfo standardMatchInfo = data.getStandardMatchInfo();
            long seconds = oldMatchInfo.getSecondsFromStart();//开始多少秒
            int t1 = commonOldItem.getHome();//主队进球
            int t2 = commonOldItem.getAway();//客队进球
            String eventCode = oldMatchInfo.getEventCode();//事件
            String homeAway = oldMatchInfo.getHomeAway();
            //标准赛事
            String match = standardMatchInfo.getHomeAwayInfo();
            String matchs[] = match.split("vs.");
            if ("home".equals(homeAway)) {
                homeAway = matchs[0].trim();
            } else {
                homeAway = matchs[1].trim();
            }
            String event = (seconds / 60) + "'" + (seconds % 60) + "''-" + (t1 + t2) + "nd " + eventCode + "-(" + homeAway + " " + t1 + ":" + t2 + ")";
            String sendData = "[Env]:" + env + "\n" +
                    "[Time]:" + time + "\n" +
                    "[Sport]:" + sport + "\n" +
                    "[Match ID]:" + standardMatchInfo.getMatchManageId() + "\n" +
                    "[Match]:" + match + "\n" +
                    "[Event]:" + event;
            String linkId = IdWorker.getId() + "_MATCH_DELETE_MANGO_EARLY_WARNING";
            manGoEarlyWarning(linkId, sendData, "删除事件芒果预警");

        } catch (Exception e) {
            log.info("标准赛事ID:{},PD报球板删除事件,芒果预警推送失败, error:{} ", data.getStandardMatchInfo().getId(), e);
        }

    }
    /**
     * 回滚消息下发X
     * */
    public void sendCancelMatchEnd(ThirdMatchInfo thirdMatchInfo,Integer secondFromStart,Long periodId,MatchScoreAndTimeVo data) {
        MatchScoresInfo matchScoresInfo = data.getMatchScoresInfo();
        CommonStandardScoresDto commonScoresDto = messageBuilderUtils.buildCommonScoresDto(thirdMatchInfo,matchScoresInfo);
        MatchEndCancelDto matchEndCancelDto =new MatchEndCancelDto();
        StandardMatchInfo standardMatchInfo = data.getStandardMatchInfo();
        StandardSportMarketSellExample marketExample = new StandardSportMarketSellExample();
        marketExample.createCriteria().andMatchInfoIdEqualTo(standardMatchInfo.getId());
        List<StandardSportMarketSell> marketSells = standardSportMarketSellMapper.selectByExample(marketExample);
        if (!CollectionUtils.isEmpty(marketSells)) {
            String dataSourceCode = thirdMatchInfo.getDataSourceCode();
            if (!ObjectUtils.isEmpty(dataSourceCode) && dataSourceCode.equals(marketSells.get(0).getBusinessEvent())) {
                matchEndCancelDto.setMatchStatus(1L);
            }
        }
        matchEndCancelDto.setPeriodId(periodId);
        matchEndCancelDto.setSecondFromStart(secondFromStart.longValue());
        matchEndCancelDto.setSportId(thirdMatchInfo.getSportId());
        matchEndCancelDto.setStandardMatchId(thirdMatchInfo.getReferenceId());
        matchEndCancelDto.setMatchScore(commonScoresDto);
        if(commonScoresDto.getSportId().equals(1L)||commonScoresDto.getSportId().equals(2L)){
            commonScoresDto.setAoMatchId(scoresService.selectAoMatchId(commonScoresDto.getStandardMatchId()));
        }
        Request<MatchEndCancelDto> reqMessage = new Request<>();
        reqMessage.setLinkId(thirdMatchInfo.getReferenceId().toString());
        reqMessage.setData(matchEndCancelDto);
        MessageBuilder<Request<MatchEndCancelDto>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, reqMessage.getLinkId());
        rocketMqTemplate.send("MATCH_CANCEL_END" +":" + reqMessage.getLinkId(), builder.build());
        log.info("::{}::开始组装赛事比分信息并下发,topic:MATCH_CANCEL_END",reqMessage.getLinkId());
    }

    public void sendCancelMatchEndStatus(MatchScoreAndTimeVo data, Integer secondFromStart, Long periodId, MatchScoresInfo matchScoresInfo) {
        MatchEventInfoDTO matchEventInfoDTO= MatchEventUtils.createMatchTimeEvent(data,secondFromStart.longValue(),secondFromStart.longValue(),System.currentTimeMillis(),1,periodId,matchScoresInfo.getThirdMatchId()+"_sendCancelMatchEndStatus");
        matchEventInfoDTO.setEventCode("match_status");
        sendPDEventInfo(matchEventInfoDTO);
    }

    /**
     * 伤停补时下发
     *
     * @param dto           伤补对象，事件触发初始数据
     * @param matchTimeInfo 查出的赛事时间数据
     * @param data          响应数据
     * @return 赛事事件信息
     */
    public void sendPAInjuryStopEvent(InjuryTimeEventDto dto, MatchTimeInfo matchTimeInfo, MatchScoreAndTimeVo data) {
        MatchEventInfoDTO matchEventInfoDTO = MatchEventUtils.createInjuryStopEvent(dto, matchTimeInfo, data);
        sendPDFootballInjuryTimeClickEventInfo(matchEventInfoDTO,dto.getTimeFromStartSecond());
    }

    /**
     * 时间状态-重置时间
     *
     * @param matchTimeInfo 赛事时间信息
     */
    public void sendTimeStatusEvent(TimeStatusEventDto dto, MatchTimeInfo matchTimeInfo, MatchScoreAndTimeVo data) {
        Long period = matchTimeInfo.getPeriod();
        Long thirdMatchId = data.getMatchScoresInfo().getThirdMatchId();
        Long startTimeSecond = matchTimeInfo.getSecondFromStart();
        MatchEventInfoDTO matchEventInfoDTO = MatchEventUtils.createMatchTimeEvent(data, startTimeSecond,
                startTimeSecond, System.currentTimeMillis(), dto.getTimeGo(), period, thirdMatchId + "_PD");
        // 0暂停
        if ("0".equals(matchEventInfoDTO.getExtrainfo())) {
            matchEventInfoDTO.setEventCode("suspension");
        }
        // 1继续
        if ("1".equals(matchEventInfoDTO.getExtrainfo())) {
            matchEventInfoDTO.setEventCode("suspension_over");
        }
        String suspensionKey = thirdMatchId + "suspension";
        Object suspensionKeyObj = redisService.get(suspensionKey);
        if (null != suspensionKeyObj) {
            matchEventInfoDTO.setExtrainfo(String.valueOf(suspensionKeyObj));
            matchEventInfoDTO.setRemark(String.valueOf(suspensionKeyObj));
        } else {
            matchEventInfoDTO.setExtrainfo(matchEventInfoDTO.getMatchPeriodId() + "");
            matchEventInfoDTO.setRemark(matchEventInfoDTO.getMatchPeriodId() + "");
        }
        sendPDFootballClickEventInfo(matchEventInfoDTO,dto.getTimeFromStartSecond(), null);
        //中断补发match_status事件
        if ("suspension".equals(matchEventInfoDTO.getEventCode())) {
            MatchEventInfoDTO matchStatusEventInfo = matchEventInfoDTO;
            matchStatusEventInfo.setEventCode("match_status");
            matchStatusEventInfo.setRemark("中断补发");
            matchEventInfoDTO.setMatchPeriodId(80L);
            sendPDEventInfo(matchStatusEventInfo);
        }

    }

    public void sendNowPeriodStatus(Response<MatchScoreAndTimeVo> response) {
        String link = response.getData().getThirdMatchInfo().getId() + "_PD_"+  response.getData().getMatchTimeInfo().getPeriod();
        MatchEventInfoDTO matchEventInfoDTO = MatchEventUtils.createMatchStatusEvent(response.getData(),response.getData().getMatchTimeInfo().getSecondFromStart(),
                response.getData().getMatchTimeInfo().getSecondFromStart(),System.currentTimeMillis(),new MatchScoreCommonVo(),
                response.getData().getMatchTimeInfo().getPeriod(),link,"");
//        matchEventInfoDTO.setExtrainfo(matchEventInfoDTO.getMatchPeriodId().toString());

        MatchScoresEventInfo matchScoresEventInfo=new MatchScoresEventInfo();
        BeanUtils.copyProperties(matchEventInfoDTO, matchScoresEventInfo);
        matchScoresEventInfo.setCreateTime(System.currentTimeMillis());
        matchScoresEventInfo.setExtraInfo(response.getData().getMatchTimeInfo().getTimeGo()+"");
        matchScoresEventInfo.setLinkId(matchEventInfoDTO.getCopyLinkId());
        matchScoresEventInfo.setId(IdWorker.getId());
        MatchScoresInfo matchScoresInfo = response.getData().getMatchScoresInfo();
        matchScoresEventInfo.setT1(matchScoresInfo.getT1());
        matchScoresEventInfo.setT2(matchScoresInfo.getT2());
        matchScoresEventInfo.setFirstT1(matchScoresInfo.getPeriodT1());
        matchScoresEventInfo.setFirstT2(matchScoresInfo.getPeriodT2());
        matchScoresEventInfoMapper.insert(matchScoresEventInfo);
        matchEventInfoDTO.setPossibleEventId(matchScoresEventInfo.getId());
        matchEventInfoDTO.setExtrainfo(matchEventInfoDTO.getMatchPeriodId() + "");
        Request<MatchEventInfoDTO> reqMessage = new Request<>();
        reqMessage.setLinkId(link);
        reqMessage.setData(matchEventInfoDTO);
        MessageBuilder<Request<MatchEventInfoDTO>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS,link);
        String dataSourceCode = matchEventInfoDTO.getDataSourceCode();
        Request<MatchEventInfoDTO> request = new Request<>(matchEventInfoDTO, reqMessage.getLinkId(), THIRD_MATCH_EVENT_INFO_API, reqMessage.getLinkId(), dataSourceCode);
        boolean spareMqFlag = getSpareMqFlag(matchEventInfoDTO.getThirdMatchSourceId());
        if (pandaDataMqGatewayevent == 2 && spareMqFlag) {
            spareBaseProducer.send(request);
        } else {
            rocketMqTemplate.send(THIRD_MATCH_EVENT_INFO_API +":" + link, builder.build());
        }
        log.info("::{}::开始组装赛事比分事件并下发,topic:THIRD_MATCH_EVENT_INFO_API", link);
    }

    public void sendPenaltyEvent(PenaltyScoresEditDto penaltyScoresEditDto, MatchScoreAndTimeVo data) {
        MatchEventInfoDTO matchEventInfoDTO = MatchEventUtils.createPenaltyEvent(penaltyScoresEditDto, data);
        // 更新缓存当前eventCode
        String key = MATCH_ADVERTIS_EVENT_STATUS + penaltyScoresEditDto.getThirdMatchId();
        Object cacheEventStatus = redisService.get(key);
        if (cacheEventStatus != null) {
            try {
                FootballMatchEventStatusVo footballMatchEventStatusVo = JSONObject.toJavaObject(JSONObject.parseObject(cacheEventStatus.toString()), FootballMatchEventStatusVo.class);
                footballMatchEventStatusVo.setCurrentEventCode(matchEventInfoDTO.getEventCode());
                redisService.set(MATCH_ADVERTIS_EVENT_STATUS + penaltyScoresEditDto.getThirdMatchId(), JSON.toJSONString(footballMatchEventStatusVo));
            } catch (Exception e) {
                log.error("buildCacheMatchStatus error::", e);
            }
        }
        sendPenaltyEventInfo( matchEventInfoDTO, penaltyScoresEditDto);
        // 补充事件
        if ( null != matchEventInfoDTO.getMatchPeriodId() && 50L == matchEventInfoDTO.getMatchPeriodId() ) {
            addPenaltyScoreEventInfo( matchEventInfoDTO, penaltyScoresEditDto, data.getMatchScoresInfo());
        }
    }

    /**
     * 点球阶段的补充
     * @param matchEventInfoDTO
     * @param penaltyScoresEditDto
     */
    private void addPenaltyScoreEventInfo( MatchEventInfoDTO matchEventInfoDTO, PenaltyScoresEditDto penaltyScoresEditDto, MatchScoresInfo matchScoresInfo) {
        String linkId = penaltyScoresEditDto.getLinkedId();
        if ( StringUtils.isEmpty(linkId))  {
            linkId = UUID.randomUUID().toString();
        }
        MatchScoresEventInfo matchScoresEventInfo = new MatchScoresEventInfo();
        BeanUtils.copyProperties( matchEventInfoDTO, matchScoresEventInfo);
        matchScoresEventInfo.setCreateTime(System.currentTimeMillis());
        matchScoresEventInfo.setLinkId(matchEventInfoDTO.getCopyLinkId());
        matchScoresEventInfo.setId(IdWorker.getId());
        // addition8=1页面禁用重踢入口
        Integer homeScore = commonScoreEventService.getCurrentPenaltyScore( linkId, matchScoresInfo, TeamTypeEnum.HOME.code );
        Integer awayScore = commonScoreEventService.getCurrentPenaltyScore( linkId, matchScoresInfo, TeamTypeEnum.AWAY.code );
        matchScoresEventInfo.setAddition1( null == homeScore ? null : homeScore.toString());
        matchScoresEventInfo.setAddition2(  null == awayScore ? null : awayScore.toString());
        String preventDuplicationKey = String.format(PREVENT_DUPLICATION_KEY, penaltyScoresEditDto.getThirdMatchId());
        if ( redisService.hasKey(preventDuplicationKey) ) {
            log.info("::{}::preventDuplication--addPenaltyScoreEventInfo,eventId:{}", linkId, matchScoresEventInfo.getThirdEventId());
            matchScoresEventInfo.setAddition8("1");
        } else {
            log.info("::{}::preventDuplication--addPenaltyScoreEventInfo没有缓存记录,eventId:{}", linkId, matchScoresEventInfo.getThirdEventId());
        }
        matchScoresEventInfo.setAddition7("1");
        matchScoresEventInfo.setAddition9("true");
        matchScoresEventInfo.setEventCode("penalty_goal");
        matchScoresEventInfoMapper.insert(matchScoresEventInfo);
    }

    private void sendPenaltyEventInfo( MatchEventInfoDTO matchEventInfoDTO, PenaltyScoresEditDto penaltyScoresEditDto) {

        MatchScoresEventInfo matchScoresEventInfo = new MatchScoresEventInfo();
        BeanUtils.copyProperties( matchEventInfoDTO, matchScoresEventInfo);
        matchScoresEventInfo.setCreateTime(System.currentTimeMillis());
        matchScoresEventInfo.setLinkId(matchEventInfoDTO.getCopyLinkId());
        matchScoresEventInfo.setId(IdWorker.getId());
        // addition8=1页面禁用重踢入口
        matchScoresEventInfo.setAddition8("1");
        // 兼容点球大战的实时比分
        if ( null != matchScoresEventInfo.getMatchPeriodId() && "50".equals(matchScoresEventInfo.getMatchPeriodId().toString()) ) {
            Integer homeScore = commonScoreEventService.getCurrentPenaltyScore( matchEventInfoDTO.getCopyLinkId(), matchScoresEventInfo.getDataSourceCode(), matchScoresEventInfo.getThirdMatchSourceId(), TeamTypeEnum.HOME.code );
            Integer awayScore = commonScoreEventService.getCurrentPenaltyScore( matchEventInfoDTO.getCopyLinkId(), matchScoresEventInfo.getDataSourceCode(), matchScoresEventInfo.getThirdMatchSourceId(), TeamTypeEnum.AWAY.code );
            matchScoresEventInfo.setAddition1( null == homeScore ? null : homeScore.toString());
            matchScoresEventInfo.setAddition2(  null == awayScore ? null : awayScore.toString());
        }
        matchScoresEventInfoMapper.insert(matchScoresEventInfo);

        matchEventInfoDTO.setPossibleEventId(matchScoresEventInfo.getId());
        Request<MatchEventInfoDTO> reqMessage = new Request<>();
        reqMessage.setLinkId(matchEventInfoDTO.getCopyLinkId());
        reqMessage.setData(matchEventInfoDTO);
        MessageBuilder<Request<MatchEventInfoDTO>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, matchEventInfoDTO.getCopyLinkId());
        String dataSourceCode = matchEventInfoDTO.getDataSourceCode();
        Request<MatchEventInfoDTO> request = new Request<>( matchEventInfoDTO, reqMessage.getLinkId(), THIRD_MATCH_EVENT_INFO_API, reqMessage.getLinkId(), dataSourceCode);
        boolean spareMqFlag = getSpareMqFlag(matchEventInfoDTO.getThirdMatchSourceId());
        if ( pandaDataMqGatewayevent == 2 && spareMqFlag ) {
            spareBaseProducer.send(request);
        } else {
            rocketMqTemplate.send(THIRD_MATCH_EVENT_INFO_API + ":" + matchEventInfoDTO.getCopyLinkId(), builder.build());
        }
        log.info("editPenaltyScore::{}::开始组装点球大战比分信息并下发, topic:THIRD_MATCH_EVENT_INFO_API,secondsFromStart:{}",
                matchEventInfoDTO.getCopyLinkId(), matchEventInfoDTO.getSecondsFromStart());
    }

    public boolean getSpareMqFlag(String thirdSourceId) {
        log.info("开始获取备用MQ标识,thirdSourceId：{}", thirdSourceId);
        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem(DataSourceCodeEnum.PD.getCode(),thirdSourceId);
        if(thirdMatchInfo==null || thirdMatchInfo.getReferenceId()==null){
            return false;
        }
        // 快速失败：不满足基本条件直接返回
        if (pandaDataMqGatewayevent != 2) {
            return false;
        }
        // 处理备用MQ配置
        if (StringUtils.isBlank(pandaDataMqGatewayMatchId)) {
            return true;
        }
        log.info("开始获取备用MQ标识,pandaDataMqGatewayMatchId：{}", pandaDataMqGatewayMatchId);
        // 转换为set集合
        Set<String> spareMatchIds = new HashSet<>(
                Arrays.asList(pandaDataMqGatewayMatchId.split(","))
        );
        return spareMatchIds.contains(thirdMatchInfo.getReferenceId()+"");
    }

}
