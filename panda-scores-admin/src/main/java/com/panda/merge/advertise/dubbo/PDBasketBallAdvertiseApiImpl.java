package com.panda.merge.advertise.dubbo;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.panda.merge.advertise.dto.MatchScoreAndTimeVo;
import com.panda.merge.advertise.dto.MatchScoreCommonVo;
import com.panda.merge.advertise.event.CommonEventService;
import com.panda.merge.advertise.mq.EventProducer;
import com.panda.merge.advertise.service.BasketBallAdvertiseService;
import com.panda.merge.advertise.service.BasketBallScoreService;
import com.panda.merge.advertise.service.CommonAdvertiseService;
import com.panda.merge.advertise.utils.MatchEventUtils;
import com.panda.merge.advertise.utils.MatchPeriodUtils;
import com.panda.merge.api.IPDBasketBallAdvertiseApi;
import com.panda.merge.common.enums.PDScoreChangeEnum;
import com.panda.merge.common.utils.IdWorker;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.SportPeriodConstant;
import com.panda.merge.dto.BasketballScores;
import com.panda.merge.dto.BasketballScoresPDDto;
import com.panda.merge.dto.CommonItem;
import com.panda.merge.dto.MatchEventInfoDTO;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.advertise.*;
import com.panda.merge.mapper.MatchScoresEventInfoMapper;
import com.panda.merge.mapper.MatchScoresInfoMapper;
import com.panda.merge.mapper.MatchTimeInfoMapper;
import com.panda.merge.mapper.ThirdMatchInfoMapper;
import com.panda.merge.model.MatchScoresEventInfo;
import com.panda.merge.model.MatchScoresEventInfoExample;
import com.panda.merge.model.MatchScoresInfo;
import com.panda.merge.model.MatchTimeInfo;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.mq.producer.ScoresProducer;
import com.panda.merge.repository.MatchScoreInfoRepository;
import com.panda.merge.repository.PdMatchInfoRepository;
import com.panda.merge.service.IMatchScorePdLogService;
import com.panda.merge.util.CategoryUtils;
import com.panda.merge.utils.JsonMapUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.panda.merge.advertise.common.Constant.PD;
import static com.panda.merge.constant.SportPeriodConstant.SportPeriod.WHOLE_MATCH;

/**
 * 新版篮球报球版2.0
 * */
@Service
@Slf4j
@DubboService
public class PDBasketBallAdvertiseApiImpl implements IPDBasketBallAdvertiseApi {

    @Autowired
    CommonAdvertiseService commonAdvertiseService;
    @Autowired
    RedisService redisService;
    @Autowired
    BasketBallScoreService basketBallScoreService;
    @Autowired
    EventProducer eventProducer;
    @Autowired
    MatchScoresEventInfoMapper matchScoresEventInfoMapper;
    @Autowired
    ScoresProducer scoresProducer;
    @Autowired
    BasketBallAdvertiseService basketBallAdvertiseService;
    @Autowired
    MatchBasketBallAdvertiseApiImpl matchBasketBallAdvertiseApi;
//    @Autowired
//    ThirdMatchInfoMapper thirdMatchInfoMapper;
    @Autowired
    private IMatchScorePdLogService matchScorePdLogService;
//    @Autowired
//    MatchTimeInfoMapper matchTimeInfoMapper;
    @Autowired
    MatchScoreInfoRepository matchScoreInfoRepository;
//    @Autowired
//    MatchScoresInfoMapper matchScoresInfoMapper;
    @Autowired
    CommonEventService commonEventService;
    @Autowired
    PdMatchInfoRepository pdMatchInfoRepository;

    /**
     * 查询报球版详情+比分统计
     * */
    @Override
    public Response searchDetail(MatchAdvertiseQueryDto matchAdvertiseQueryDto) {
        Long thirdMatchId = matchAdvertiseQueryDto.getThirdMatchId();
        log.info("PDBasketBallAdvertiseApiImpl::searchDetail::matchAdvertiseQueryDto={}",JSON.toJSONString(matchAdvertiseQueryDto));
        try {
            Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(thirdMatchId);
            if (!response.isSuccess()) {
                return response;
            }
            return basketBallAdvertiseService.buildPDBasketBallAdvertiseVo(response.getData());
        } catch (Exception e) {
            log.error("PA赛事报球版查询异常::{}::", e);

            return Response.failed(e.getMessage());
        }
    }

    @Override
    public Response searchAllScore(PDBasketBallParseContinueDto pauseContinueDto) {
        log.info("PDBasketBallAdvertiseApiImpl::searchAllScore::pauseContinueDto={}", JSON.toJSONString(pauseContinueDto));
        try {
            Long thirdMatchId = pauseContinueDto.getThirdMatchId();
            Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(thirdMatchId);
            if (!response.isSuccess()) {
                return response;
            }
            return basketBallAdvertiseService.buildPDAllScore(response.getData());
        } catch (Exception e) {
            log.error("PA赛事报球版查询异常::{}::", e);

            return Response.failed(e.getMessage());
        }
    }

    /**
     *  可能事件  确认事件  取消事件
     * */
    @Override
    public Response sendEvent(PDBasketBallSendEventDto sendEventDto) {
        String linkId = sendEventDto.getLinkedId();
        log.info("::{}::新篮球报球板收到事件请求-事件:{}", linkId, JSON.toJSONString(sendEventDto));
        String key = "PA_createMatchAdvertise:" + sendEventDto.getThirdMatchId();
        try
        {
            if (redisService.tryLock(key, key, 2, 3)) {
                //1.查询出当前赛事的赛制
                Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(sendEventDto.getThirdMatchId());
                JSONObject periodFootballScores = JSONObject.parseObject(response.getData().getMatchScoresInfo().getScoresJson());
                Map<Long, BasketballScoresPDDto> allPeriodScores= JsonMapUtils.parseBasketballPDDtoMap(periodFootballScores);
                BasketballScoresPDDto oldScore = allPeriodScores.get(WHOLE_MATCH);
                //根据事件编码返回前端事件状态
                String eventCode =  MatchEventUtils.getEventCodeByType( sendEventDto.getEventType());

                //2.1 根据主客队  事件编码修改 当前比分 和 总比分
               BasketballScoresPDDto basketballScores = basketBallScoreService.changeScoreByHomeAwayAndEventCode(response,sendEventDto.getHomeAway(),eventCode);
                //2.2 组装事件下发
                MatchScoreCommonVo matchScoreCommonVo =new MatchScoreCommonVo();
                matchScoreCommonVo.setHomeAway(sendEventDto.getHomeAway());
                //倒计时为现有记录时长-  (当前时间/1000-上次事件时间/1000)
                Long startTimeSecond = response.getData().getMatchTimeInfo().getSecondFromStart()-(System.currentTimeMillis()/1000-  response.getData().getMatchTimeInfo().getEventTime()/1000);
                if (response.getData().getMatchTimeInfo().getTimeGo() == 0) {
                    startTimeSecond = response.getData().getMatchTimeInfo().getSecondFromStart();
                }
                //事件构建
                MatchEventInfoDTO eventInfoDTO= MatchEventUtils.createMatchScoreEvent(eventCode,response.getData().getThirdMatchInfo(),matchScoreCommonVo,startTimeSecond,
                        response.getData().getMatchTimeInfo().getPeriod(),sendEventDto.getLinkedId(), sendEventDto.getOperatorName());
                eventInfoDTO.setSportId(2L);
                eventProducer.sendPD2EventInfo(response,eventInfoDTO);
                //2.3 组装赛事消息入库
                MatchScoresEventInfo matchScoresEventInfo=new MatchScoresEventInfo();
                BeanUtils.copyProperties(eventInfoDTO, matchScoresEventInfo);
                //赛事消息要展示实时的比赛比分
                matchScoresEventInfo.setT1(basketballScores.getMatchScore().getHome());
                matchScoresEventInfo.setT2(basketballScores.getMatchScore().getAway());
                matchScoresEventInfo.setCreateTime(System.currentTimeMillis());
                matchScoresEventInfo.setExtraInfo(eventInfoDTO.getExtrainfo());
                matchScoresEventInfo.setLinkId(eventInfoDTO.getCopyLinkId());
                matchScoresEventInfo.setId(IdWorker.getId());
                matchScoresEventInfo.setExtraInfo(eventInfoDTO.getExtrainfo());
                matchScoresEventInfoMapper.insert(matchScoresEventInfo);
                //2.4 下发比分
                //7. 下发比分变更事件  或者比分修正事件
//                int eventType = Integer.parseInt(sendEventDto.getEventType());
//                if (1 <= eventType && eventType <= 7) {
//                    commonEventService.updateBasketballEvent(response.getData(),sendEventDto);
//                }
                scoresProducer.sendToMQ(response.getData().getThirdMatchInfo(),response.getData().getMatchScoresInfo(),sendEventDto.getLinkedId());
                //2.5 记录日志
                matchScorePdLogService.sendEventLog(oldScore, basketballScores, response, sendEventDto);
            }

        }catch (Exception e){
                log.error(":处理数据发生异常:", e);
        }finally {
            redisService.unLock(key,key);
        }
        return Response.success();
    }
    /**
     * 下发球未命中  命中  取消(不发)
     *  //  1 未命中  2投篮命中  3取消投篮
     * */
    @Override
    public Response sendBall(PDBasketBallSendBallDto sendBallDto) {
        String linkId = sendBallDto.getLinkedId();
        log.info("::{}::新篮球报球板收到事件请求-2分球3分球:{}", linkId, JSON.toJSONString(sendBallDto));
        String key = "PA_createMatchAdvertise:" + sendBallDto.getThirdMatchId();
        try
        {
            if (redisService.tryLock(key, key, 2, 3)) {
                //1.查询出当前赛事的赛制
                Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(sendBallDto.getThirdMatchId());
                //根据事件编码返回前端事件状态
                String eventCode =  MatchEventUtils.getScoreCodeByEventType( sendBallDto.getBallEventType());
                if(eventCode==null){
                    return Response.failed("error");
                }
                // 倒计时为现有记录时长-  (当前时间/1000-上次事件时间/1000)
                Long startTimeSecond = getMatchTime(response);
                // 取消页面赛事时间，使用服务端时间
                sendBallDto.setTimeFromStartSecond(startTimeSecond);
                if ("score_miss".equals(eventCode) && sendBallDto.getScore() == 2) {
                    eventCode = "2p_miss";
                }
                if ("score_miss".equals(eventCode) && sendBallDto.getScore() == 3) {
                    eventCode = "3p_miss";
                }
                //2.2 组装事件下发
                MatchScoreCommonVo matchScoreCommonVo =new MatchScoreCommonVo();
                matchScoreCommonVo.setHomeAway(sendBallDto.getHomeAway());
                JSONObject periodFootballScores = JSONObject.parseObject(response.getData().getMatchScoresInfo().getScoresJson());
                Map<Long, BasketballScores> allPeriodScores= JsonMapUtils.parseBasketballMap(periodFootballScores);
                BasketballScores oldScore = allPeriodScores.get(WHOLE_MATCH);
                //2.1 根据主客队  事件编码修改 当前比分 和 总比分
                BasketballScores basketballScores = basketBallScoreService.changeScoreBySendBallDto(response,sendBallDto);
                //进球要记录进球比分
               if(sendBallDto.getBallEventType()==2){
                   matchScoreCommonVo.setT1(basketballScores.getMatchScore().getHome());
                   matchScoreCommonVo.setT2(basketballScores.getMatchScore().getAway());
                   BasketballScores periodScores =basketBallScoreService.getPeriodScore(response);
                   if(ObjectUtil.isNotEmpty(periodScores)){
                       matchScoreCommonVo.setPeriodT1(periodScores.getMatchScore().getHome());
                       matchScoreCommonVo.setPeriodT2(periodScores.getMatchScore().getAway());
                   }
               }
                //事件构建
                MatchEventInfoDTO eventInfoDTO= MatchEventUtils.createMatchScoreEvent(eventCode,response.getData().getThirdMatchInfo(),matchScoreCommonVo,sendBallDto.getTimeFromStartSecond(),
                        response.getData().getMatchTimeInfo().getPeriod(),sendBallDto.getLinkedId(), sendBallDto.getOperatorName());
                eventInfoDTO.setExtrainfo(sendBallDto.getScore().toString());
                eventProducer.sendPD2EventInfo(response,eventInfoDTO);
                //2.3 组装赛事消息入库
                MatchScoresEventInfo matchScoresEventInfo=new MatchScoresEventInfo();
                BeanUtils.copyProperties(eventInfoDTO, matchScoresEventInfo);
                //赛事消息要展示实时的比赛比分
                CommonItem matchScore = basketballScores.getMatchScore();
                if (ObjectUtil.isEmpty(matchScore)) {
                    matchScore=new CommonItem();
                }
                matchScoresEventInfo.setT1(matchScore.getHome());
                matchScoresEventInfo.setT2(matchScore.getAway());
                matchScoresEventInfo.setCreateTime(System.currentTimeMillis());
                matchScoresEventInfo.setExtraInfo(eventInfoDTO.getExtrainfo());
                matchScoresEventInfo.setLinkId(eventInfoDTO.getCopyLinkId());
                matchScoresEventInfo.setId(IdWorker.getId());
                matchScoresEventInfo.setExtraInfo(eventInfoDTO.getExtrainfo());
                Long period = response.getData().getMatchScoresInfo().getPeriod();
                Integer matchLength = response.getData().getStandardMatchInfo().getMatchLength();
                Long sixPeriod = MatchPeriodUtils.getBasketSixPeriod(period, sendBallDto.getTimeFromStartSecond(), matchLength);
                if (!ObjectUtils.isEmpty(sixPeriod)) {
                    matchScoresEventInfo.setAddition3(sixPeriod + "");
                }
                matchScoresEventInfoMapper.insert(matchScoresEventInfo);
                //2.4 下发比分
                //7. 下发比分变更事件  或者比分修正事件
                MatchScoresInfo matchScoresInfo = response.getData().getMatchScoresInfo();
                scoresProducer.sendToMQ(response.getData().getThirdMatchInfo(),matchScoresInfo,sendBallDto.getLinkedId());
                //2.5 记录日志
                matchScorePdLogService.sendBallLog(oldScore, basketballScores, response, sendBallDto);
            }

        }catch (Exception e){
            log.error(":处理数据发生异常:", e);
        }finally {
            redisService.unLock(key,key);
        }
        return Response.success();
    }

    public Response sendBall(Response<MatchScoreAndTimeVo> responseBuffer, PDBasketBallSendBallDto sendBallDto) {
        String linkId = sendBallDto.getLinkedId();
        log.info("::{}::新篮球报球板收到事件请求-罚球:{}", linkId, JSON.toJSONString(sendBallDto));
        String key = "PA_createMatchAdvertise:" + sendBallDto.getThirdMatchId();
        try
        {
            if (redisService.tryLock(key, key, 2, 3)) {
                //1.查询出当前赛事的赛制
                Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(sendBallDto.getThirdMatchId());
                //根据事件编码返回前端事件状态
                String eventCode =  MatchEventUtils.getScoreCodeByEventType( sendBallDto.getBallEventType());
                if(eventCode==null){
                    return Response.failed("error");
                }
                //2.2 组装事件下发
                MatchScoreCommonVo matchScoreCommonVo =new MatchScoreCommonVo();
                matchScoreCommonVo.setHomeAway(sendBallDto.getHomeAway());
                //2.1 根据主客队  事件编码修改 当前比分 和 总比分
                BasketballScores basketballScores = basketBallScoreService.changeScoreBySendBallDto(response,sendBallDto);
                //进球要记录进球比分
                if(sendBallDto.getBallEventType()==2){
                    matchScoreCommonVo.setT1(basketballScores.getMatchScore().getHome());
                    matchScoreCommonVo.setT2(basketballScores.getMatchScore().getAway());
                    BasketballScores periodScores =basketBallScoreService.getPeriodScore(response);
                    if(ObjectUtil.isNotEmpty(periodScores)){
                        matchScoreCommonVo.setPeriodT1(periodScores.getMatchScore().getHome());
                        matchScoreCommonVo.setPeriodT2(periodScores.getMatchScore().getAway());
                    }
                }
                //倒计时为现有记录时长-  (当前时间/1000-上次事件时间/1000)
//                Long startTimeSecond = response.getData().getMatchTimeInfo().getSecondFromStart()-(System.currentTimeMillis()/1000-  response.getData().getMatchTimeInfo().getEventTime()/1000);
                //事件构建
                MatchEventInfoDTO eventInfoDTO= MatchEventUtils.createMatchScoreEvent(eventCode,response.getData().getThirdMatchInfo(),matchScoreCommonVo,sendBallDto.getTimeFromStartSecond(),
                        response.getData().getMatchTimeInfo().getPeriod(),sendBallDto.getLinkedId(), sendBallDto.getOperatorName());
                eventInfoDTO.setExtrainfo(sendBallDto.getScore().toString());
                if (sendBallDto.isFreeThrow() && sendBallDto.getScore() != null && sendBallDto.getScore() == 0) {
                    eventInfoDTO.setAddition5(String.valueOf(1));
                    eventProducer.sendPD2EventInfo(response, eventInfoDTO);
                } else {
                    eventProducer.sendPD2EventInfo(response, eventInfoDTO);
                }
                //2.3 组装赛事消息入库
                MatchScoresEventInfo matchScoresEventInfo=new MatchScoresEventInfo();
                BeanUtils.copyProperties(eventInfoDTO, matchScoresEventInfo);
                //赛事消息要展示实时的比赛比分
                CommonItem matchScore = basketballScores.getMatchScore();
                if (ObjectUtil.isEmpty(matchScore)) {
                    matchScore=new CommonItem();
                }
                matchScoresEventInfo.setT1(matchScore.getHome());
                matchScoresEventInfo.setT2(matchScore.getAway());
                matchScoresEventInfo.setCreateTime(System.currentTimeMillis());
                matchScoresEventInfo.setExtraInfo(eventInfoDTO.getExtrainfo());
                matchScoresEventInfo.setLinkId(eventInfoDTO.getCopyLinkId());
                matchScoresEventInfo.setId(IdWorker.getId());
                matchScoresEventInfo.setExtraInfo(eventInfoDTO.getExtrainfo());
                matchScoresEventInfo.setAddition1(sendBallDto.isFreeThrow() ? String.valueOf(true) : null);
                // 罚球ID设置入赛事消息
                matchScoresEventInfo.setAddition8(sendBallDto.getEventOrder() + "");
                Long period = response.getData().getMatchScoresInfo().getPeriod();
                Integer matchLength = response.getData().getStandardMatchInfo().getMatchLength();
                Long sixPeriod = MatchPeriodUtils.getBasketSixPeriod(period, sendBallDto.getTimeFromStartSecond(), matchLength);
                if (!ObjectUtils.isEmpty(sixPeriod)) {
                    matchScoresEventInfo.setAddition3(sixPeriod + "");
                }
                if (sendBallDto.isInput()) {
                    matchScoresEventInfo.setAddition2(String.valueOf(sendBallDto.getBallId()));
                } else {
                    matchScoresEventInfo.setAddition5(sendBallDto.getScore() + CategoryUtils.SPLIT_LINE + sendBallDto.getFreeThrowNumber());
                }
                matchScoresEventInfoMapper.insert(matchScoresEventInfo);
                //2.4 下发比分
                //7. 下发比分变更事件  或者比分修正事件
                MatchScoresInfo matchScoresInfo = response.getData().getMatchScoresInfo();
                scoresProducer.sendToMQ(response.getData().getThirdMatchInfo(),matchScoresInfo,sendBallDto.getLinkedId());
                //2.5 记录日志
                matchScorePdLogService.sendBallLog(responseBuffer, response, sendBallDto);
            }

        }catch (Exception e){
            log.error(":处理数据发生异常:", e);
        }finally {
            redisService.unLock(key,key);
        }
        return Response.success();
    }

    @Override
    public Response gameStart(PDBaskectBallMatchStartDto pdBaskectBallMatchStartDto) {
        String linkId = pdBaskectBallMatchStartDto.getLinkedId();
        log.info("::{}::新篮球报球板收到事件请求-开始:{}", linkId, JSON.toJSONString(pdBaskectBallMatchStartDto));
        String key = "PA_createMatchAdvertise:" + pdBaskectBallMatchStartDto.getThirdMatchId();
        try {
            if (redisService.tryLock(key, key, 2, 3)) {
                //1.调用原有比赛开始的逻辑
                ChangeMatchStatusDto changeMatchStatusDto =new ChangeMatchStatusDto();
                changeMatchStatusDto.setControlType(1);
                changeMatchStatusDto.setPeriodId(null);
                changeMatchStatusDto.setThirdMatchId(pdBaskectBallMatchStartDto.getThirdMatchId());
                changeMatchStatusDto.setOperatorName(pdBaskectBallMatchStartDto.getOperatorName());
                changeMatchStatusDto.setIpAddress(pdBaskectBallMatchStartDto.getIpAddress());
                changeMatchStatusDto.setOperatorId(pdBaskectBallMatchStartDto.getOperatorId());
                changeMatchStatusDto.setLinkedId(pdBaskectBallMatchStartDto.getLinkedId());
                changeMatchStatusDto.setIsJump(changeMatchStatusDto.getIsJump());
                matchBasketBallAdvertiseApi.changeMatchStatus(changeMatchStatusDto);
                //2.跳球
                if (pdBaskectBallMatchStartDto.getIsJump() != null && pdBaskectBallMatchStartDto.getIsJump() == 1) {
                    this.takeJumpWonEvent(pdBaskectBallMatchStartDto);
                }
                return Response.success();
            }else {
                return Response.failed("system is busy!");
            }
        }catch (Exception e){
            log.error(":处理数据发生异常:", e);
        }finally {
            redisService.unLock(key,key);
        }
        return Response.failed("ERROR");
    }

    @Override
    public Response deleteEvent(PDBasketBallDeleteEventDto pdBasketBallDeleteEventDto) {
        log.info("PDBasketBallAdvertiseApiImpl::deleteEvent::pdBasketBallDeleteEventDto={}", JSON.toJSONString(pdBasketBallDeleteEventDto));
        Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(pdBasketBallDeleteEventDto.getThirdMatchId());
        // 倒计时为现有记录时长-  (当前时间/1000-上次事件时间/1000)
        Long startTimeSecond = getMatchTime(response);
        pdBasketBallDeleteEventDto.setMatchTimeSecond(startTimeSecond);
        //1.如果不是开打阶段不能下发删除事件
        Long periodId= response.getData().getMatchTimeInfo().getPeriod();
        Integer matchLength = response.getData().getStandardMatchInfo().getMatchLength();
        if(!SportPeriodConstant.BasketballPeriod.contans(periodId,matchLength)){
            return Response.failed("比赛非开打阶段无法删除事件");
        }
        MatchScoresEventInfo matchScoresEventInfo = matchScoresEventInfoMapper.selectByPrimaryKey(pdBasketBallDeleteEventDto.getDeleteEventId());
        if(matchScoresEventInfo==null){
            return Response.failed("被删除事件不存在");
        }
        if(!StringUtils.isEmpty(matchScoresEventInfo.getAddition10())&& "1".equals(matchScoresEventInfo.getAddition10())){
            return Response.failed("被删除的事件无法继续删除");
        }
        MatchScoresEventInfo matchScoresEventInfoOld = new MatchScoresEventInfo();
        BeanUtils.copyProperties(matchScoresEventInfo, matchScoresEventInfoOld);
        matchScoresEventInfoOld.setAddition10("1");
        matchScoresEventInfoOld.setModifyTime(System.currentTimeMillis());
        matchScoresEventInfoMapper.updateByPrimaryKey(matchScoresEventInfoOld);
//        if(!matchScoresEventInfo.getEventCode().equals("score_change")){
//            return Response.failed("非比分变更事件无法删除");
//        }
        //执行删除  1. 删除 6分支 2 .删除当前阶段 3.删除 总分 4.负分保护=0
        MatchScoreCommonVo matchScoreCommonVo= basketBallScoreService.doDeleteEvent(response,matchScoresEventInfo,pdBasketBallDeleteEventDto);
        if(matchScoreCommonVo==null){
            return Response.failed("被删除事件没有记录新增分数");
        }
        // 删除或编辑罚球时，根据罚球ID重置当前罚球状态
        String key = "PD_FREE_THROW:" + pdBasketBallDeleteEventDto.getThirdMatchId();
        updateFreeThrowStatus(key, matchScoresEventInfo, null,"delete");
        //下发新的比分变更事件 cancel=1
        matchScoresEventInfo.setCreateTime(System.currentTimeMillis());
        matchScoresEventInfo.setT1(matchScoreCommonVo.getT1());
        matchScoresEventInfo.setT2(matchScoreCommonVo.getT2());
        matchScoresEventInfo.setFirstT1(matchScoreCommonVo.getPeriodT1());
        matchScoresEventInfo.setFirstT2(matchScoreCommonVo.getPeriodT2());
        matchScoresEventInfo.setSecondsFromStart(pdBasketBallDeleteEventDto.getMatchTimeSecond());
        // 跨阶段修改比分，重置比赛进行时间为0，下发实时服务、风控、业务
//        if (matchScoresEventInfoOld.getMatchPeriodId() < pdBasketBallDeleteEventDto.getPeriodId()) {
//        }
        matchScoresEventInfo.setSecondsFromStart(matchScoresEventInfoOld.getSecondsFromStart());
        matchScoresEventInfo.setEventTime(System.currentTimeMillis());
        matchScoresEventInfo.setId(IdWorker.getId());
        matchScoresEventInfo.setAddition4(matchScoresEventInfo.getThirdEventId());
        matchScoresEventInfo.setAddition5(null);
        // 标记删除并下发实时服务
        matchScoresEventInfo.setCanceled(1);
        MatchEventInfoDTO matchEventInfoDto =new MatchEventInfoDTO();
        BeanUtils.copyProperties(matchScoresEventInfo,matchEventInfoDto);
        matchEventInfoDto.setEventCode("delete_event");
        matchEventInfoDto.setExtrainfo(matchScoresEventInfoOld.getThirdEventId());
        matchEventInfoDto.setThirdEventId(PD+"_"+ UUID.randomUUID());
        matchEventInfoDto.setCopyLinkId(matchEventInfoDto.getThirdEventId());
        matchScoresEventInfo.setThirdEventId(matchEventInfoDto.getThirdEventId());
        matchScoresEventInfo.setLinkId(matchEventInfoDto.getThirdEventId());
        matchScoresEventInfoMapper.insert(matchScoresEventInfo);
        //LIVE_DATA
        matchEventInfoDto.setSourceType("1");
        matchEventInfoDto.setMatchLength(response.getData().getStandardMatchInfo().getMatchLength());
        // 编辑/删除时，设置状态和当前阶段时间addition1&addition2给风控
        // 当前阶段时间设置到addition1
//        matchEventInfoDto.setAddition1(String.valueOf(startTimeSecond));
        // 删除修改状态设置到addition2
//        matchEventInfoDto.setAddition2("pd_basketball_delete");
        if (pdBasketBallDeleteEventDto.getPeriodId().equals(matchScoresEventInfo.getMatchPeriodId())) {
            matchEventInfoDto.setSecondsFromStart(startTimeSecond);
            eventProducer.sendPDDeleteEventInfo(matchEventInfoDto);
        }
        //下发新的比分
        scoresProducer.sendToMQ(response.getData().getThirdMatchInfo(),response.getData().getMatchScoresInfo(),pdBasketBallDeleteEventDto.getLinkedId());
        return Response.success();
    }

    @Override
    public Response editEvent(PDBasketBallEditEventDto editEventDto) {
        log.info("PDBasketBallAdvertiseApiImpl::editEvent::editEventDto={}", JSON.toJSONString(editEventDto));
        Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(editEventDto.getThirdMatchId());
        // 倒计时为现有记录时长-  (当前时间/1000-上次事件时间/1000)
        Long startTimeSecond = getMatchTime(response);
        editEventDto.setMatchTimeSecond(startTimeSecond);
        //1.如果不是开打阶段不能下发删除事件
        Long periodId= response.getData().getMatchTimeInfo().getPeriod();
        Integer matchLength = response.getData().getStandardMatchInfo().getMatchLength();
        if(!SportPeriodConstant.BasketballPeriod.contans(periodId,matchLength)){
            return Response.failed("比赛非开打阶段无法删除事件");
        }
        MatchScoresEventInfo matchScoresEventInfo = matchScoresEventInfoMapper.selectByPrimaryKey(editEventDto.getDeleteEventId());
        if(matchScoresEventInfo==null){
            return Response.failed("被删除事件不存在");
        }
        String addition5 = matchScoresEventInfo.getAddition5();
        if (2 == editEventDto.getBallEventType() && editEventDto.getScore().equals(Integer.valueOf(matchScoresEventInfo.getExtraInfo()))) {
            if (StringUtils.isEmpty(addition5)) {
                return Response.failed("编辑前后比分相等");
            }
            boolean flag = Objects.equals(editEventDto.getFreeThrowNumber(), Integer.valueOf(addition5.split(" - ")[1]));
            if (!StringUtils.isEmpty(addition5) && flag) {
                return Response.failed("编辑前后比分相等");
            }
        }
        if(!StringUtils.isEmpty(matchScoresEventInfo.getAddition10())&& "1".equals(matchScoresEventInfo.getAddition10())){
            return Response.failed("已编辑的事件不能再编辑");
        }
        MatchScoresEventInfo matchScoresEventInfoOld = new MatchScoresEventInfo();
        BeanUtils.copyProperties(matchScoresEventInfo, matchScoresEventInfoOld);
        matchScoresEventInfoOld.setAddition10("1");
        matchScoresEventInfoOld.setModifyTime(System.currentTimeMillis());
        matchScoresEventInfoMapper.updateByPrimaryKey(matchScoresEventInfoOld);
//        if(!matchScoresEventInfo.getEventCode().equals("score_change")){
//            return Response.failed("非比分变更事件无法删除");
//        }
        //执行删除  1. 删除 6分支 2 .删除当前阶段 3.删除 总分 4.负分保护=0
        MatchScoreCommonVo matchScoreCommonVo= basketBallScoreService.editEvent(response,matchScoresEventInfo,editEventDto);
        if(matchScoreCommonVo==null){
            return Response.failed("被删除事件没有记录新增分数");
        }
        // 删除或编辑罚球时，根据罚球ID重置当前罚球状态
        Integer score = editEventDto.getScore();
        if (null != editEventDto.getBallEventType() && editEventDto.getBallEventType() == 1) {
            score = 0;
        }
        String key = "PD_FREE_THROW:" + editEventDto.getThirdMatchId();
        updateFreeThrowStatus(key, matchScoresEventInfo, score,"edit");
        //下发新的比分变更事件 cancel=1
        matchScoresEventInfo.setCreateTime(System.currentTimeMillis());
        matchScoresEventInfo.setModifyTime(System.currentTimeMillis());
        matchScoresEventInfo.setT1(matchScoreCommonVo.getT1());
        matchScoresEventInfo.setT2(matchScoreCommonVo.getT2());
        matchScoresEventInfo.setSecondsFromStart(editEventDto.getMatchTimeSecond());
        // 跨阶段修改比分，重置比赛进行时间为0，下发实时服务、风控、业务
//        if (matchScoresEventInfoOld.getMatchPeriodId() < editEventDto.getPeriodId()) {
//        }
        matchScoresEventInfo.setSecondsFromStart(matchScoresEventInfoOld.getSecondsFromStart());
        matchScoresEventInfo.setFirstT1(matchScoreCommonVo.getPeriodT1());
        matchScoresEventInfo.setFirstT2(matchScoreCommonVo.getPeriodT2());
        matchScoresEventInfo.setEventTime(System.currentTimeMillis());
        matchScoresEventInfo.setExtraInfo(matchScoresEventInfo.getThirdEventId());
        matchScoresEventInfo.setId(IdWorker.getId());
        matchScoresEventInfo.setCanceled(0);
        matchScoresEventInfo.setExtraInfo(editEventDto.getScore() + "");
        if (!StringUtils.isEmpty(addition5)) {
            matchScoresEventInfo.setAddition5(editEventDto.getScore() + " - " + editEventDto.getFreeThrowNumber());
            if (editEventDto.getScore() == 0) {
                matchScoresEventInfo.setEventCode("score_miss");
            }
        }
        MatchEventInfoDTO matchEventInfoDto =new MatchEventInfoDTO();
        BeanUtils.copyProperties(matchScoresEventInfo,matchEventInfoDto);
        matchEventInfoDto.setEventCode("score_change");
        matchEventInfoDto.setExtrainfo(matchScoresEventInfoOld.getLinkId());
        matchEventInfoDto.setThirdEventId(PD+"_"+ UUID.randomUUID());
        matchEventInfoDto.setCopyLinkId(matchEventInfoDto.getThirdEventId());
        matchScoresEventInfo.setThirdEventId(matchEventInfoDto.getThirdEventId());
        matchScoresEventInfo.setLinkId(matchEventInfoDto.getThirdEventId());
        matchScoresEventInfoMapper.insert(matchScoresEventInfo);
        //LIVE_DATA
        matchEventInfoDto.setSourceType("1");
        matchEventInfoDto.setMatchLength(response.getData().getStandardMatchInfo().getMatchLength());
        // 编辑/删除时，设置状态和当前阶段时间addition1&addition2给风控
        // 当前阶段时间设置到addition1
//        matchEventInfoDto.setAddition1(String.valueOf(startTimeSecond));
        // 删除修改状态设置到addition2
//        matchEventInfoDto.setAddition2("pd_basketball_update");
        if (editEventDto.getPeriodId().equals(matchScoresEventInfo.getMatchPeriodId())) {
            matchEventInfoDto.setSecondsFromStart(startTimeSecond);
            eventProducer.sendPDBasketballEditEventInfo(matchEventInfoDto);
        }
        //下发新的比分
        scoresProducer.sendToMQ(response.getData().getThirdMatchInfo(),response.getData().getMatchScoresInfo(),editEventDto.getLinkedId());
        return Response.success();
    }

    /**
     * 获取当前赛事时间
     *
     * @param response 响应数据
     * @return 当前赛事时间
     */
    private Long getMatchTime(Response<MatchScoreAndTimeVo> response) {
        MatchTimeInfo matchTimeInfo = response.getData().getMatchTimeInfo();
        Long startTimeSecond = matchTimeInfo.getSecondFromStart() - (System.currentTimeMillis() - matchTimeInfo.getEventTime()) / 1000;
        // 暂停时赛事时间取字段时间
        if (matchTimeInfo.getTimeGo() == 0) {
            startTimeSecond = matchTimeInfo.getSecondFromStart();
        }
        if (startTimeSecond < 0) {
            startTimeSecond = 0L;
        }
        return startTimeSecond;
    }

    /**
     * 删除或编辑罚球时，根据罚球ID重置当前罚球状态
     *
     * @param key                  redis key
     * @param matchScoresEventInfo 当前罚球赛事消息
     * @param score                当前比分
     * @param operate              删除或编辑操作
     */
    private void updateFreeThrowStatus(String key, MatchScoresEventInfo matchScoresEventInfo, Integer score, String operate) {
        Object obj = redisService.get(key);
        if (null != obj) {
            FreeThrowDetailDto freeThrowDetailDto = JSONObject.parseObject(obj.toString(), new TypeReference<FreeThrowDetailDto>() {
            });
            List<SetFreeThrowBasketballDto> ballOrder = freeThrowDetailDto.getBallOrder();
            for (SetFreeThrowBasketballDto bean : ballOrder) {
                if (null != matchScoresEventInfo.getAddition8() && bean.getId() == Long.parseLong(matchScoresEventInfo.getAddition8())) {
                    if (null != score && 1 == score) {
                        bean.setStatus(1);
                    } else {
                        if ("delete".equals(operate)) {
                            bean.setStatus(-1);
                        }
                        if ("edit".equals(operate)) {
                            bean.setStatus(0);
                        }
                        freeThrowDetailDto.setScore(freeThrowDetailDto.getScore() - 1);
                    }
                }
            }
            redisService.set(key, JSONObject.toJSON(freeThrowDetailDto).toString());
        }
    }


    @Override
    public Response pauseAndContinue(PDBasketBallPauseDto pdBasketBallPauseDto) {
        log.info("PDBasketBallAdvertiseApiImpl::pauseAndContinue::pdBasketBallPauseDto={}", JSON.toJSONString(pdBasketBallPauseDto));
        Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(pdBasketBallPauseDto.getThirdMatchId());
        Integer matchLength = null;
        if (pdBasketBallPauseDto.getPeriodId() == 21) {
            matchLength = response.getData().getMatchTimeInfo().getMatchLength();
        }
        if(!SportPeriodConstant.BasketballPeriod.contans(pdBasketBallPauseDto.getPeriodId(),matchLength)){
            return Response.failed("比赛非开打阶段无法暂停");
        }
        if(pdBasketBallPauseDto.getType()==1){
            basketBallAdvertiseService.matchPauseBasketball(response.getData(),pdBasketBallPauseDto);
            MatchScoresInfo matchScoresInfo = pdMatchInfoRepository.getMatchScoresInfoByPrimaryKey(response.getData().getMatchScoresInfo().getId(), null);
            response.getData().setMatchScoresInfo(matchScoresInfo);
            basketBallScoreService.addPauseScore(response,pdBasketBallPauseDto);
            // 记录暂停日志
            matchScorePdLogService.parseContinueLog(response, pdBasketBallPauseDto);
        }/*else {
            basketBallAdvertiseService.matchContinueBasketball(response.getData(),pdBasketBallPauseDto);
            // 记录继续日志
            matchScorePdLogService.parseContinueLog(response, pdBasketBallPauseDto);
        }*/
        return Response.success();
    }

    @Override
    public Response getFreeThrow(PDBasketBallPauseDto basketBallPauseDto) {
        log.info("PDBasketBallAdvertiseApiImpl::getFreeThrow::basketBallPauseDto={}", JSON.toJSONString(basketBallPauseDto));
        Long thirdMatchId = basketBallPauseDto.getThirdMatchId();
        String key  = "PD_FREE_THROW:"+thirdMatchId;
        Object o     = redisService.get(key);
        if(o==null){
            return Response.success(new FreeThrowDetailDto(String.valueOf(thirdMatchId)));
        }else {
            FreeThrowDetailDto freeThrowDetailDto = JSONObject.toJavaObject((JSONObject)JSONObject.parse(o.toString()),FreeThrowDetailDto.class);
            return Response.success(freeThrowDetailDto);
        }
    }

    @Override
    public Response setFreeThrow(SetFreeThrowDto setFreeThrowDto) {
        log.info("PDBasketBallAdvertiseApiImpl::setFreeThrow::setFreeThrowDto={}", JSON.toJSONString(setFreeThrowDto));
        String key  = "PD_FREE_THROW:"+setFreeThrowDto.getThirdMatchId();
        Object o     = redisService.get(key);
        //1.查询出当前赛事的赛制
        Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(setFreeThrowDto.getThirdMatchId());
        //倒计时为现有记录时长-  (当前时间/1000-上次事件时间/1000)
        Long startTimeSecond = getMatchTime(response);
        setFreeThrowDto.setTimeFromStartSecond(startTimeSecond);
        if(o==null){
            FreeThrowDetailDto freeThrowDetailDto =new FreeThrowDetailDto(String.valueOf(setFreeThrowDto.getThirdMatchId()));
            Integer freeThrowNumber = setFreeThrowDto.getFreeThrowNumber();
            freeThrowDetailDto.setFreeThrowNumber(freeThrowNumber);
            freeThrowDetailDto.setScore(0);
            freeThrowDetailDto.setHomeAway(setFreeThrowDto.getHomeAway());
            List<SetFreeThrowBasketballDto> list = new ArrayList<>();
            for (int i = 0; i < freeThrowNumber; i++) {
                SetFreeThrowBasketballDto element = new SetFreeThrowBasketballDto();
                element.setId(IdWorker.getId());
                element.setStatus(-1);
                element.setSportId(setFreeThrowDto.getSportId());
                element.setLinkedId(setFreeThrowDto.getLinkedId());
                element.setOperatorId(setFreeThrowDto.getOperatorId());
                element.setIpAddress(setFreeThrowDto.getIpAddress());
                element.setOperatorName(setFreeThrowDto.getOperatorName());
                list.add(element);
            }
            freeThrowDetailDto.setBallOrder(list);
            redisService.set(key,JSONObject.toJSON(freeThrowDetailDto).toString() );
            /**
             * todo 首次点击罚球1|2|3时
             * 1.下发MQ
             * 2.更新赛事消息
             * 3.记录操作日志
             */
            setFreeThrowDto.setEventCode("free_throw");
            setFreeThrowDto.setLinkedId(setFreeThrowDto.getLinkedId() + "_" + setFreeThrowDto.getEventCode());
            dataTransferAndProcess(setFreeThrowDto, response);
            return Response.success(JSON.toJSONString(freeThrowDetailDto));
        }else {
            // 罚球界面，点1，2，3时，需清空redis数据，初始化新数据
            if (1 == setFreeThrowDto.getType()) {
                redisService.del(key);
                FreeThrowDetailDto freeThrowDetailDto = new FreeThrowDetailDto(String.valueOf(setFreeThrowDto.getThirdMatchId()));
                Integer freeThrowNumber = setFreeThrowDto.getFreeThrowNumber();
                freeThrowDetailDto.setFreeThrowNumber(freeThrowNumber);
                freeThrowDetailDto.setScore(0);
                freeThrowDetailDto.setHomeAway(setFreeThrowDto.getHomeAway());
                List<SetFreeThrowBasketballDto> list = new ArrayList<>();
                for (int i = 0; i < freeThrowNumber; i++) {
                    SetFreeThrowBasketballDto element = new SetFreeThrowBasketballDto();
                    element.setId(IdWorker.getId());
                    element.setStatus(-1);
                    element.setSportId(setFreeThrowDto.getSportId());
                    element.setLinkedId(setFreeThrowDto.getLinkedId());
                    element.setOperatorId(setFreeThrowDto.getOperatorId());
                    element.setIpAddress(setFreeThrowDto.getIpAddress());
                    element.setOperatorName(setFreeThrowDto.getOperatorName());
                    list.add(element);
                }
                freeThrowDetailDto.setBallOrder(list);
                redisService.set(key, JSONObject.toJSON(freeThrowDetailDto).toString());
                /**
                 * todo 首次点击罚球1|2|3时
                 * 1.下发MQ
                 * 2.更新赛事消息
                 * 3.记录操作日志
                 */
                setFreeThrowDto.setEventCode("free_throw");
                setFreeThrowDto.setLinkedId(setFreeThrowDto.getLinkedId() + "_" + setFreeThrowDto.getEventCode());
                dataTransferAndProcess(setFreeThrowDto, response);
                return Response.success(JSON.toJSONString(freeThrowDetailDto));
            } else {
                FreeThrowDetailDto freeThrowDetailDto = JSONObject.toJavaObject((JSONObject) JSONObject.parse(o.toString()), FreeThrowDetailDto.class);
                List<SetFreeThrowBasketballDto> ballOrderList = freeThrowDetailDto.getBallOrder();
                // 根据ID删除-减操作
                if (setFreeThrowDto.isDelete() && setFreeThrowDto.getType()==0) {
                    ballOrderList.removeIf(dto -> {
                        boolean flag = setFreeThrowDto.isDelete() && dto.getId() == setFreeThrowDto.getId();
                        if (flag) {
                            freeThrowDetailDto.setFreeThrowNumber(freeThrowDetailDto.getFreeThrowNumber() - 1);
                            /**
                             * todo 删除罚球时
                             * 1.下发MQ
                             * 2.更新赛事消息
                             * 3.记录操作日志
                             */
                            setFreeThrowDto.setEventCode("free_throw_sub");
                            setFreeThrowDto.setLinkedId(setFreeThrowDto.getLinkedId() + "_" + setFreeThrowDto.getEventCode());
                            dataTransferAndProcess(setFreeThrowDto, response);
                        }
                        return flag;
                    });
                }
                // 加操作
                if (2 == setFreeThrowDto.getType()) {
                    SetFreeThrowBasketballDto element = new SetFreeThrowBasketballDto();
                    element.setId(IdWorker.getId());
                    element.setStatus(-1);
                    element.setSportId(setFreeThrowDto.getSportId());
                    element.setLinkedId(setFreeThrowDto.getLinkedId());
                    element.setOperatorId(setFreeThrowDto.getOperatorId());
                    element.setIpAddress(setFreeThrowDto.getIpAddress());
                    element.setOperatorName(setFreeThrowDto.getOperatorName());
                    freeThrowDetailDto.setFreeThrowNumber(freeThrowDetailDto.getFreeThrowNumber() + 1);
                    if (ballOrderList.size() >= 3) {
                        return Response.success(JSON.toJSONString(freeThrowDetailDto));
                    }
                    ballOrderList.add(element);
                    /**
                     * todo 增加罚球时
                     * 1.下发MQ
                     * 2.更新赛事消息
                     * 3.记录操作日志
                     */
                    setFreeThrowDto.setEventCode("free_throw_add");
                    setFreeThrowDto.setLinkedId(setFreeThrowDto.getLinkedId() + "_" + setFreeThrowDto.getEventCode());
                    dataTransferAndProcess(setFreeThrowDto, response);
                }
                redisService.set(key, JSONObject.toJSON(freeThrowDetailDto).toString());
                return Response.success(JSON.toJSONString(freeThrowDetailDto));
            }
        }

    }

    private void dataTransferAndProcess(SetFreeThrowDto setFreeThrowDto, Response<MatchScoreAndTimeVo> response) {
        //1.下发MQ 事件构建
        MatchScoreCommonVo matchScoreCommonVo =new MatchScoreCommonVo();
        matchScoreCommonVo.setHomeAway(setFreeThrowDto.getHomeAway());
        MatchScoresInfo matchScoresInfo = response.getData().getMatchScoresInfo();
        matchScoreCommonVo.setT1(matchScoresInfo.getT1());
        matchScoreCommonVo.setT2(matchScoresInfo.getT2());
        matchScoreCommonVo.setPeriodT1(matchScoresInfo.getPeriodT1());
        matchScoreCommonVo.setPeriodT2(matchScoresInfo.getPeriodT2());
        MatchEventInfoDTO eventInfoDTO= MatchEventUtils.createMatchScoreEvent(setFreeThrowDto.getEventCode(), response.getData().getThirdMatchInfo(),matchScoreCommonVo, setFreeThrowDto.getTimeFromStartSecond(),
                response.getData().getMatchTimeInfo().getPeriod(), setFreeThrowDto.getLinkedId(), setFreeThrowDto.getOperatorName());
        // 罚球总数
        eventInfoDTO.setExtrainfo(setFreeThrowDto.getFreeThrowNumber() + "");
        Long period = matchScoresInfo.getPeriod();
        Integer matchLength = response.getData().getStandardMatchInfo().getMatchLength();
        Long sixPeriod = MatchPeriodUtils.getBasketSixPeriod(period, setFreeThrowDto.getTimeFromStartSecond(), matchLength);
        if (!ObjectUtils.isEmpty(sixPeriod)) {
            eventInfoDTO.setAddition3(sixPeriod+"");
        }
        // 下发实时服务
        eventProducer.sendPD2EventInfo(response,eventInfoDTO);
        //2 组装赛事消息入库
        MatchScoresEventInfo matchScoresEventInfo=new MatchScoresEventInfo();
        BeanUtils.copyProperties(eventInfoDTO, matchScoresEventInfo);
        matchScoresEventInfo.setT1(matchScoresInfo.getT1());
        matchScoresEventInfo.setT2(matchScoresInfo.getT2());
        matchScoresEventInfo.setCreateTime(System.currentTimeMillis());
        // 当前罚球总数
        matchScoresEventInfo.setAddition6(eventInfoDTO.getExtrainfo());
        matchScoresEventInfo.setLinkId(eventInfoDTO.getCopyLinkId());
        matchScoresEventInfo.setId(IdWorker.getId());
        matchScoresEventInfo.setAddition3(eventInfoDTO.getAddition3());
        matchScoresEventInfoMapper.insert(matchScoresEventInfo);
        matchScorePdLogService.sendFreeThrowLog(setFreeThrowDto, response);
    }

    @Override
    public Response cancelFreeThrow(SetFreeThrowDto setFreeThrowDto) {
        log.info("PDBasketBallAdvertiseApiImpl::cancelFreeThrow::setFreeThrowDto={}", JSON.toJSONString(setFreeThrowDto));
        String key  = "PD_FREE_THROW:"+setFreeThrowDto.getThirdMatchId();
        Object obj = redisService.get(key);
        //1.查询出当前赛事的赛制
        Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(setFreeThrowDto.getThirdMatchId());
        //倒计时为现有记录时长-  (当前时间/1000-上次事件时间/1000)
        Long startTimeSecond = getMatchTime(response);
        setFreeThrowDto.setTimeFromStartSecond(startTimeSecond);
        if (obj != null) {
            setFreeThrowDto.setEventCode("play_canceled");
            setFreeThrowDto.setLinkedId(setFreeThrowDto.getLinkedId() + "_" + setFreeThrowDto.getEventCode());
            FreeThrowDetailDto freeThrowDetailDto = JSON.parseObject(obj.toString(), new TypeReference<FreeThrowDetailDto>() {
            });
            setFreeThrowDto.setHomeAway(freeThrowDetailDto.getHomeAway());
            setFreeThrowDto.setFreeThrowNumber(freeThrowDetailDto.getFreeThrowNumber());
            setFreeThrowDto.setCancel(true);
            dataTransferAndProcess(setFreeThrowDto, response);
        }
        Response.success(redisService.del(key));
        PDBasketBallPauseDto dto = new PDBasketBallPauseDto();
        dto.setThirdMatchId(setFreeThrowDto.getThirdMatchId());
        return getFreeThrow(dto);
    }

    @Override
    public Response goFreeThrow(GoFreeThrowDto setFreeThrowDto) {
        log.info("PDBasketBallAdvertiseApiImpl::goFreeThrow::setFreeThrowDto={}", JSON.toJSONString(setFreeThrowDto));
        String key  = "PD_FREE_THROW:"+setFreeThrowDto.getThirdMatchId();
        Object o     = redisService.get(key);
        if(o==null){
            return Response.failed("罚球阶段不存在");
        }else {
            FreeThrowDetailDto freeThrowDetailDto = JSONObject.toJavaObject((JSONObject)JSONObject.parse(o.toString()),FreeThrowDetailDto.class);
            if(freeThrowDetailDto.getStatus()==2){
                return Response.failed("进行中无法修改罚球次数");
            }
            if(freeThrowDetailDto.getBallOrder()==null){
                return Response.failed("罚球阶段不存在");
            }
            int ballId = 0;
            List<SetFreeThrowBasketballDto> ballOrderList = freeThrowDetailDto.getBallOrder();
            for (int i = 0; i < ballOrderList.size(); i++) {
                if (setFreeThrowDto.getEventOrder() == ballOrderList.get(i).getId()) {
                    boolean flag = ballOrderList.get(i).getStatus() == 0 && setFreeThrowDto.getFreeThrowResult() == 0 || ballOrderList.get(i).getStatus() == 1 && setFreeThrowDto.getFreeThrowResult() == 1;
                    if (flag) {
                        return Response.success();
                    }
                    ballOrderList.get(i).setStatus(setFreeThrowDto.getFreeThrowResult());
                    ballId = ++i;
                }
            }
//            freeThrowDetailDto.getFreeThrowDetailList().set(setFreeThrowDto.getEventOrder()-1,setFreeThrowDto.getFreeThrowResult());
//            freeThrowDetailDto.setEventOrder(setFreeThrowDto.getEventOrder());
            if(setFreeThrowDto.getFreeThrowResult()==1){
                freeThrowDetailDto.setScore(freeThrowDetailDto.getScore()+1);
            }
            if(freeThrowDetailDto.getScore()>3){
                return Response.failed("罚球比分传递错误 "+freeThrowDetailDto.getScore());
            }
            redisService.set(key,JSONObject.toJSON(freeThrowDetailDto).toString() );
            //1.查询出当前赛事的赛制
            Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(setFreeThrowDto.getThirdMatchId());
            // 倒计时为现有记录时长-  (当前时间/1000-上次事件时间/1000)
            Long startTimeSecond = getMatchTime(response);
            setFreeThrowDto.setTimeFromStartSecond(startTimeSecond);
            PDBasketBallSendBallDto sendBallDto =new PDBasketBallSendBallDto();
            sendBallDto.setOperatorName(setFreeThrowDto.getOperatorName());
            sendBallDto.setLinkedId(setFreeThrowDto.getLinkedId());
            sendBallDto.setIpAddress(setFreeThrowDto.getIpAddress());
            sendBallDto.setThirdMatchId(setFreeThrowDto.getThirdMatchId());
            sendBallDto.setTimeFromStartSecond(setFreeThrowDto.getTimeFromStartSecond());
            if (1 == setFreeThrowDto.getFreeThrowResult()) {
                sendBallDto.setBallEventType(2);
            } else {
                sendBallDto.setBallEventType(1);
            }
            sendBallDto.setScore(setFreeThrowDto.getFreeThrowResult());
            sendBallDto.setHomeAway(freeThrowDetailDto.getHomeAway());
            sendBallDto.setFreeThrow(true);
            sendBallDto.setBallId(ballId);
            sendBallDto.setFreeThrowNumber(1);
            sendBallDto.setInput(true);
            // 罚球ID设置-用于存储赛事消息表
            sendBallDto.setEventOrder(setFreeThrowDto.getEventOrder());
            this.sendBall(response,sendBallDto);
//            redisService.del(key);
            return Response.success(freeThrowDetailDto);
        }
    }

    @Override
    public Response sendFreeThrow(SendFreeThrowDto setFreeThrowDto) {
        log.info("PDBasketBallAdvertiseApiImpl::sendFreeThrow::setFreeThrowDto={}", JSON.toJSONString(setFreeThrowDto));
        String key  = "PD_FREE_THROW:"+setFreeThrowDto.getThirdMatchId();
        if (setFreeThrowDto.isType()) {
            redisService.del(key);
            return Response.success();
        }
        Object o     = redisService.get(key);
        Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(setFreeThrowDto.getThirdMatchId());
        // 无罚球 有 进/总: 点 进/总 逻辑
        if (!setFreeThrowDto.isType()) {
            if (setFreeThrowDto.getScore() > 0) {
                // 倒计时为现有记录时长-  (当前时间/1000-上次事件时间/1000)
                Long startTimeSecond = getMatchTime(response);
                PDBasketBallSendBallDto sendBallDto = new PDBasketBallSendBallDto();
                sendBallDto.setOperatorName(setFreeThrowDto.getOperatorName());
                sendBallDto.setLinkedId(setFreeThrowDto.getLinkedId());
                sendBallDto.setIpAddress(setFreeThrowDto.getIpAddress());
                sendBallDto.setThirdMatchId(setFreeThrowDto.getThirdMatchId());
                sendBallDto.setTimeFromStartSecond(startTimeSecond);
                sendBallDto.setBallEventType(2);
                sendBallDto.setScore(setFreeThrowDto.getScore());
                sendBallDto.setHomeAway(setFreeThrowDto.getHomeAway());
                sendBallDto.setFreeThrow(true);
                sendBallDto.setFreeThrowNumber(setFreeThrowDto.getFreeThrowNumber());
                sendBallDto.setInput(setFreeThrowDto.isType());
                this.sendBall(response, sendBallDto);
            } else {
                // 倒计时为现有记录时长-  (当前时间/1000-上次事件时间/1000)
                Long startTimeSecond = getMatchTime(response);
                PDBasketBallSendBallDto sendBallDto = new PDBasketBallSendBallDto();
                sendBallDto.setOperatorName(setFreeThrowDto.getOperatorName());

                sendBallDto.setLinkedId(setFreeThrowDto.getLinkedId());
                sendBallDto.setIpAddress(setFreeThrowDto.getIpAddress());
                sendBallDto.setThirdMatchId(setFreeThrowDto.getThirdMatchId());
                sendBallDto.setTimeFromStartSecond(startTimeSecond);
                sendBallDto.setBallEventType(1);
                sendBallDto.setScore(setFreeThrowDto.getScore());
                sendBallDto.setHomeAway(setFreeThrowDto.getHomeAway());
                sendBallDto.setFreeThrow(true);
                sendBallDto.setInput(setFreeThrowDto.isType());
                sendBallDto.setFreeThrowNumber(setFreeThrowDto.getFreeThrowNumber());
                this.sendBall(response, sendBallDto);
            }
            if (o != null) {
                redisService.del(key);
            }
            return Response.success();
        } else {
            // 有罚球 无 进/总: 罚球后，点确认逻辑
            if (o != null) {
                redisService.del(key);
            }
            return Response.success();
        }
    }


    private void takeJumpWonEvent(PDBaskectBallMatchStartDto pdBaskectBallMatchStartDto) {
        //1.修改跳球比分
        Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(pdBaskectBallMatchStartDto.getThirdMatchId());
        //根据事件编码返回前端事件状态
        String eventCode =  "won_jump_ball";
        BasketballScores jumpScore  = basketBallScoreService.changeJumpWonScore(response,pdBaskectBallMatchStartDto);
        //2.2 组装事件下发
        MatchScoreCommonVo matchScoreCommonVo =new MatchScoreCommonVo();
        matchScoreCommonVo.setT1(jumpScore.getWonJumpBall().getHome());
        matchScoreCommonVo.setT2(jumpScore.getWonJumpBall().getAway());
        matchScoreCommonVo.setHomeAway(pdBaskectBallMatchStartDto.getJumpWonHomeAway());
        //倒计时为现有记录时长-  (当前时间/1000-上次事件时间/1000)
        Long startTimeSecond = response.getData().getMatchTimeInfo().getSecondFromStart()-(System.currentTimeMillis()/1000-  response.getData().getMatchTimeInfo().getEventTime()/1000);
        //事件构建
        MatchEventInfoDTO eventInfoDTO= MatchEventUtils.createMatchScoreEvent(eventCode,response.getData().getThirdMatchInfo(),matchScoreCommonVo,startTimeSecond,
                response.getData().getMatchTimeInfo().getPeriod(),pdBaskectBallMatchStartDto.getLinkedId(), pdBaskectBallMatchStartDto.getOperatorName());
        eventProducer.sendPD2EventInfo(response,eventInfoDTO);
        //2.3 组装赛事消息入库
        MatchScoresEventInfo matchScoresEventInfo=new MatchScoresEventInfo();
        BeanUtils.copyProperties(eventInfoDTO, matchScoresEventInfo);
        //赛事消息要展示实时的比赛比分
        matchScoresEventInfo.setT1(0);
        matchScoresEventInfo.setT2(0);
        matchScoresEventInfo.setCreateTime(System.currentTimeMillis());
        matchScoresEventInfo.setExtraInfo(eventInfoDTO.getExtrainfo());
        matchScoresEventInfo.setLinkId(eventInfoDTO.getCopyLinkId());
        matchScoresEventInfo.setId(IdWorker.getId());
        matchScoresEventInfo.setExtraInfo(eventInfoDTO.getExtrainfo());
        matchScoresEventInfoMapper.insert(matchScoresEventInfo);
        //2.4 下发比分
        //7. 下发比分变更事件  或者比分修正事件
        scoresProducer.sendToMQ(response.getData().getThirdMatchInfo(),response.getData().getMatchScoresInfo(),pdBaskectBallMatchStartDto.getLinkedId());

        //4.记录跳球日志
        matchScorePdLogService.changeJumpWonScoreLog(response,pdBaskectBallMatchStartDto);
    }

    /**
     * 事件列表
     * */
    @Override
    public Response searchEventList(PDBasketBallSearchEventDto eventDto) {
        log.info("PDBasketBallAdvertiseApiImpl::searchEventList::eventDto={}", JSON.toJSONString(eventDto));
//        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoMapper.selectByPrimaryKey(eventDto.getThirdMatchId());
        ThirdMatchInfo thirdMatchInfo = pdMatchInfoRepository.getThirdMatchInfo(eventDto.getThirdMatchId(), null);
        if (ObjectUtils.isEmpty(thirdMatchInfo)) {
            return Response.success(new ArrayList<>());
        }
        if(eventDto.getPeriodId()==null||eventDto.getPeriodId().equals(-1L)){
            MatchScoresEventInfoExample eventInfoExample =new MatchScoresEventInfoExample();
            eventInfoExample.createCriteria().andThirdMatchSourceIdEqualTo(thirdMatchInfo.getThirdMatchSourceId()).andSportIdEqualTo(2L);
            eventInfoExample.setOrderByClause("create_time desc");
            List< MatchScoresEventInfo> list= matchScoresEventInfoMapper.selectByExample(eventInfoExample);
            return Response.success(list);
        }else {
            MatchScoresEventInfoExample eventInfoExample =new MatchScoresEventInfoExample();
            eventInfoExample.createCriteria().andThirdMatchSourceIdEqualTo(thirdMatchInfo.getThirdMatchSourceId()).andSportIdEqualTo(2L)
            .andMatchPeriodIdEqualTo(eventDto.getPeriodId());
            eventInfoExample.setOrderByClause("create_time desc");
            List< MatchScoresEventInfo> list= matchScoresEventInfoMapper.selectByExample(eventInfoExample);
            return Response.success(list);
        }

    }


    /**
     * 中断或者重开
     * 中断下发中断阶段事件 修改阶段 保存缓存 30天 之前的阶段信息
     * 重开获取缓存中的阶段信息
     * 重新下发当前缓存的阶段事件
     * */
    @Override
    public Response breakOrReStart(PDBasketBallParseContinueDto parseContinueDto) {
        log.info("PDBasketBallAdvertiseApiImpl::breakOrReStart::parseContinueDto={}", JSON.toJSONString(parseContinueDto));
        long currentTime = System.currentTimeMillis();
        //1.查询当前比赛阶段 根据前端请求方式判断
        Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(parseContinueDto.getThirdMatchId());
        String key =  "BREAK_OR_RESTART:"+parseContinueDto.getThirdMatchId();
        //1.-1 如果操作为中断
        if(parseContinueDto.getMatchGoStatus()==1){
            MatchTimeInfo matchTimeInfo = response.getData().getMatchTimeInfo();
            MatchScoresInfo matchScoresInfo = response.getData().getMatchScoresInfo();
            //1.0 若比赛已经中断阶段 则返回失败
            if(matchTimeInfo.getPeriod().equals(80L)){
                return Response.failed("比赛已经中断");
            }
            //1.1 如果不是中断状态 则保存当前阶段
            redisService.set(key,matchTimeInfo.getPeriod(),3600*24*7);
            //1.2 更新阶段为中断
            matchTimeInfo.setPeriod(80L);
            matchScoresInfo.setPeriod(80L);
            matchTimeInfo.setTimeGo(0);
            //1.计算时间 当前剩余秒 = 上次倒计时- (现在系统时间-上次事件时间)/1000
            Long startTimeSecond = matchTimeInfo.getSecondFromStart() - (currentTime - matchTimeInfo.getEventTime()) / 1000;
            if(startTimeSecond<0){
                startTimeSecond = 0L;
            }
            matchScoresInfo.setSecondsMatchStart(startTimeSecond);
            matchScoresInfo.setRemainingTime(startTimeSecond);
            matchScoresInfo.setEventTime(currentTime);
            matchTimeInfo.setSecondFromStart(startTimeSecond);
            matchTimeInfo.setRemainingTime(startTimeSecond);
            matchTimeInfo.setEventTime(currentTime);
//            matchTimeInfoMapper.updateByPrimaryKey(matchTimeInfo);
//            matchScoresInfoMapper.updateByPrimaryKey(matchScoresInfo);
            pdMatchInfoRepository.setRedisAndMatchTimeInfo(matchTimeInfo, null);
            pdMatchInfoRepository.setRedisAndMatchScoresInfo(matchScoresInfo, null);
            //1.3 下发中断阶段事件
            eventProducer.sendNowPeriodStatus(response);
            //记录日志
            matchScorePdLogService.breakOrReStartLog(response, parseContinueDto);
        }else {
            MatchTimeInfo matchTimeInfo = response.getData().getMatchTimeInfo();
            if(matchTimeInfo.getSecondFromStart()<0){
                matchTimeInfo.setSecondFromStart(0L);
            }
            MatchScoresInfo matchScoresInfo = response.getData().getMatchScoresInfo();
            //2  如果操作是恢复
            //2.1 查询当前比赛如果不是中断则返回失败
            if(!matchTimeInfo.getPeriod().equals(80L)){
                return Response.failed("比赛已经中断");
            }
            //2.2 更新阶段为缓存的阶段
            Object o  =  redisService.get(key);
            if(o==null){
                return Response.failed("中断的阶段已经丢失");
            }
            Long period= Long.parseLong(o.toString());
            matchTimeInfo.setPeriod(period);
            matchScoresInfo.setPeriod(period);
            matchTimeInfo.setTimeGo(1);
            matchTimeInfo.setEventTime(currentTime);
            matchScoresInfo.setEventTime(currentTime);
//            matchTimeInfoMapper.updateByPrimaryKey(matchTimeInfo);
//            matchScoresInfoMapper.updateByPrimaryKey(matchScoresInfo);
            pdMatchInfoRepository.setRedisAndMatchTimeInfo(matchTimeInfo, null);
            pdMatchInfoRepository.setRedisAndMatchScoresInfo(matchScoresInfo, null);
            //2.3 下发阶段事件
            eventProducer.sendNowPeriodStatus(response);
            //记录日志
            matchScorePdLogService.breakOrReStartLog(response, parseContinueDto);
        }


        return Response.success();
    }

    @Override
    public Response editSixScore(PDBasketBallEditSixScoreDto editSixScoreDto) {
        log.info("PDBasketBallAdvertiseApiImpl::editSixScore::editSixScoreDto={}", JSON.toJSONString(editSixScoreDto));
        Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(editSixScoreDto.getThirdMatchId());
        // 倒计时为现有记录时长-  (当前时间/1000-上次事件时间/1000)
        Long secondFromStart = getMatchTime(response);
        JSONObject periodFootballScores = JSONObject.parseObject(response.getData().getMatchScoresInfo().getScoresJson());
        Map<Long, BasketballScores> oldAllScores = JsonMapUtils.parseBasketballMap(periodFootballScores);
        int flag = basketBallScoreService.changeSixPeriodScore(response, editSixScoreDto);
        if (flag == PDScoreChangeEnum.NUMBER_LESS_EQUAL_ZERO.getCode()) {
            return Response.failed("比分不能小于0");
        }
        if (flag == PDScoreChangeEnum.SCORE_EQUAL.getCode()) {
            return Response.failed(PDScoreChangeEnum.SCORE_EQUAL.getMsg());
        }
        MatchScoresInfo matchScoresInfo = response.getData().getMatchScoresInfo();
        ThirdMatchInfo thirdMatchInfo = response.getData().getThirdMatchInfo();
        //2.2 组装事件下发
        MatchScoreCommonVo matchScoreCommonVo =new MatchScoreCommonVo();
        matchScoreCommonVo.setT1(matchScoresInfo.getT1());
        matchScoreCommonVo.setT2(matchScoresInfo.getT2());
        matchScoreCommonVo.setPeriodT1(matchScoresInfo.getPeriodT1());
        matchScoreCommonVo.setPeriodT2(matchScoresInfo.getPeriodT2());
        //事件构建
        MatchEventInfoDTO eventInfoDTO= MatchEventUtils.createMatchScoreEvent("score_change",thirdMatchInfo,matchScoreCommonVo,secondFromStart,
                editSixScoreDto.getPeriodId(),editSixScoreDto.getLinkedId(), editSixScoreDto.getOperatorName());
//        Integer matchLength = response.getData().getStandardMatchInfo().getMatchLength();
//        Long period = matchScoresInfo.getPeriod();
//        Long sixPeriod = MatchPeriodUtils.getBasketSixPeriod(period, eventInfoDTO.getSecondsFromStart(), matchLength);
        Map<Long, CommonItem> sixPeriodMap = MatchPeriodUtils.getSixPeriodMap(editSixScoreDto);
        Long sixPeriodId = 0L;
        for (Map.Entry<Long, CommonItem> entry : sixPeriodMap.entrySet()) {
            sixPeriodId = entry.getKey();
        }
        if (sixPeriodId != null) {
            eventInfoDTO.setAddition3(sixPeriodId + "");
        }
        //2.3 组装赛事消息入库
        MatchScoresEventInfo matchScoresEventInfo=new MatchScoresEventInfo();
        BeanUtils.copyProperties(eventInfoDTO, matchScoresEventInfo);
        matchScoresEventInfo.setT1(matchScoresInfo.getT1());
        matchScoresEventInfo.setT2(matchScoresInfo.getT2());
        matchScoresEventInfo.setFirstT1(matchScoresInfo.getPeriodT1());
        matchScoresEventInfo.setFirstT2(matchScoresInfo.getPeriodT2());
        matchScoresEventInfo.setCreateTime(System.currentTimeMillis());
        matchScoresEventInfo.setLinkId(eventInfoDTO.getCopyLinkId());
        matchScoresEventInfo.setId(IdWorker.getId());
        matchScoresEventInfoMapper.insert(matchScoresEventInfo);
        //3.下发MQ给实时服务
        eventInfoDTO.setExtrainfo("1001");
        if (editSixScoreDto.getPeriodId().equals(matchScoresInfo.getPeriod())) {
            eventProducer.sendPDBasketballEditEventInfo(eventInfoDTO);
        }
        scoresProducer.sendToMQ(response.getData().getThirdMatchInfo(), response.getData().getMatchScoresInfo(), editSixScoreDto.getLinkedId());
        // 记录日志
        matchScorePdLogService.editSixScoreLog(oldAllScores, response, editSixScoreDto);
        return Response.success();
    }


}
