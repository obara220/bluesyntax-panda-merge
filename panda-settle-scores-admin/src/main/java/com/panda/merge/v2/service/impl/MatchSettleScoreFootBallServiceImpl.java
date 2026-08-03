package com.panda.merge.v2.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.api.IFootballMatchScoresSettleApi;
import com.panda.merge.common.enums.*;
import com.panda.merge.common.utils.IdWorker;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.utils.IdGenerator;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.*;
import com.panda.merge.constant.converter.SettleMentionConverter;
import com.panda.merge.dto.*;
import com.panda.merge.dto.advertise.MatchSettleSwitcherDto;
import com.panda.merge.dto.message.MatchFreezeMessage;
import com.panda.merge.dto.settle.*;
import com.panda.merge.model.*;
import com.panda.merge.mq.producer.MatchSettleCenterProducer;
import com.panda.merge.mq.producer.MatchSettleScoresProducer;
import com.panda.merge.service.IWsPushService;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.service.StandardSportMarketSellService;
import com.panda.merge.service.StandardSportTournamentService;
import com.panda.merge.service.impl.GrayIntervalService;
import com.panda.merge.service.settleMention.dto.AbstractMentionStatus;
import com.panda.merge.service.settleMention.dto.BasketballMentionStatus;
import com.panda.merge.service.settleMention.dto.FootballMentionStatus;
import com.panda.merge.service.syncScore.SyncScoreFactory;
import com.panda.merge.utils.*;
import com.panda.merge.v2.check.IMatchSettleBatchCheckService;
import com.panda.merge.v2.converter.MatchSettleCheckInfoV2Converter;
import com.panda.merge.v2.converter.MatchSettleScoreConverter;
import com.panda.merge.v2.entity.*;
import com.panda.merge.v2.repository.*;
import com.panda.merge.v2.service.IMatchSettleCheckInfoService;
import com.panda.merge.v2.service.IMatchSettleEventService;
import com.panda.merge.v2.service.IMatchSettleOperateLogService;
import com.panda.merge.v2.service.IMatchSettleScoreFootBallService;
import com.panda.merge.v2.service.assemble.MatchSettleEventAssemble;
import com.panda.merge.v2.service.assemble.MatchSettleScoreAssemble;
import com.panda.merge.v2.service.helper.*;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.panda.merge.constant.MatchSettleScoreConstant.MatchSettleScoreStatus.*;

@Slf4j
@Service
public class MatchSettleScoreFootBallServiceImpl implements IMatchSettleScoreFootBallService {

    @Autowired
    private MatchSettleScoreV2Repository matchSettleScoreRepository;
    @Autowired
    RedisService redisService;
    @Autowired
    IMatchSettleCheckInfoService matchSettleCheckInfoService;

    @Autowired
    MatchSettleCheckInfoHelper matchSettleCheckInfoHelper;
    @Autowired
    MatchSettleRollBackInfoRepository matchSettleRollBackInfoRepository;
    @Autowired
    private StandardSportMarketSellService standardSportMarketSellService;

    @Autowired
    private MatchSettleThirdScoreV2Repository matchSettleThirdScoreRepository;

    @Autowired
    IMatchSettleOperateLogService matchSettleOperateLogService;


    @Autowired
    StandardMatchInfoService standardMatchInfoService;
    @Autowired
    MatchSettleInfoRepository matchSettleInfoRepository;

    @Autowired
    IWsPushService wsPushService;

    @Autowired
    MatchServiceHelper matchServiceHelper;
    @Autowired
    MatchSettleScoresProducer matchSettleScoresProducer;

    @Autowired
    private MatchSettleScoreConverter matchSettleScoreConverter;

    @Autowired
    private GrayIntervalServiceHelper grayIntervalServiceHelper;

    @Autowired
    private MatchSettleEventV2Repository matchSettleEventRepository;
    @Autowired
    private MatchSettleCheckInfoRepository matchSettleCheckInfoRepository;

    @Autowired
    private StandardSportTournamentService standardSportTournamentService;

    @Autowired
    MatchSettleCenterProducer matchSettleCenterProducer;
    @Autowired
    private MatchSettleInfoHelper matchSettleInfoHelper;

    @Autowired
    private MatchSettleScoreAssemble matchSettleScoreAssemble;

    @Autowired
    private MatchSettleEventAssemble matchSettleEventAssemble;

    @Autowired
    SyncScoreFactory syncScoreFactory;
    @Autowired
    private MatchDelaySettleInfoV2Repository matchDelaySettleInfoRepository;

    @Autowired
    IFootballMatchScoresSettleApi footballMatchScoresSettleApi;

    @Autowired
    SettleMentionConverter settleMentionConverter;
    @Autowired
    IMatchSettleBatchCheckService matchSettleBatchCheckService;
    @Autowired
    private MatchSettleTemplateHelper matchSettleTemplateHelper;
    @Autowired
    private MatchSettleCheckInfoV2Converter matchSettleCheckInfoV2Converter;
    @Autowired
    private IMatchSettleEventService matchSettleEventService;

    /**
     * 管理员编辑阶段比分
     * @param matchSettleScoreDto
     * @return
     */
    @Override
    public Response updateMatchSettleScore(UpdateMatchSettleScoreDto matchSettleScoreDto) {
        log.info("[IFootballMatchScoresSettleApiImpl] updateMatchSettleScore-v2 with linkId:{} and param:{} start!",matchSettleScoreDto.getLinkedId(), matchSettleScoreDto);
        String key = CommonConstant.MATCH_PHASE_SCORE_SETTLE+ matchSettleScoreDto.getStandardMatchId();
        if(matchServiceHelper.checkIfOverSettleTime(matchSettleScoreDto.getStandardMatchId())){
            return Response.failed("1031930");
        }
        try {

            if(redisService.tryLock(key,key,2,5)) {
                //0.加redis锁
                StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(matchSettleScoreDto.getStandardMatchId());
                if (standardMatchInfo == null) {
                    return Response.failed("1031931");
                }
                MatchSettleScore matchSettleScore = null;
                MatchSettleScore matchSettleBefore = new MatchSettleScore();
                String forwScore ="" ;
                if (matchSettleScoreDto.getMatchScoreId() != null && matchSettleScoreDto.getMatchScoreId() != 0) {
                    matchSettleScore = matchSettleScoreRepository.getById(matchSettleScoreDto.getMatchScoreId());
                    BeanUtils.copyProperties(matchSettleScore,matchSettleBefore);
                    if (matchSettleScore == null) {
                        return Response.failed("1031931");
                    }
                    if(!matchSettleCheckInfoHelper.isAllPeriodScoresBeforeSettled(matchSettleScore) &&
                            !matchSettleScoreDto.getSettleNum().equals(MatchPeriodEnum.GOAL_5.getCode().toString()) &&
                            !matchSettleScoreDto.getSettleNum().equals(MatchPeriodEnum.GOAL_9.getCode().toString())) {
                        // 请确保上一个比分已结算。
                        return Response.failed("1031946");
                    }
                    if(matchSettleScore.getEventCode().equals("kick_off")||matchSettleScore.getSettleNum().equals("1020")){
                        if(matchSettleScoreDto.getT1()!=null&&matchSettleScoreDto.getT2()!=null){
                            if(!((matchSettleScoreDto.getT1()==0&&matchSettleScoreDto.getT2()==1)||(matchSettleScoreDto.getT1()==1&&matchSettleScoreDto.getT2()==0))){
                                if(matchSettleScoreDto.getGoWaterStatus()==null||matchSettleScoreDto.getGoWaterStatus()==0){
                                    return Response.failed("1031939");
                                }
                            }
                        }else {
                            if(matchSettleScoreDto.getGoWaterStatus()==null||matchSettleScoreDto.getGoWaterStatus()==0){
                                return Response.failed("1031939");
                            }
                        }
                    }
                    //修改前比分
                    forwScore= matchSettleScore.getT1()+"-"+ matchSettleScore.getT2();
                    String t1 =matchSettleScore.getT1()==null ?"":matchSettleScore.getT1().toString();
                    String t2 =matchSettleScore.getT2()==null ?"":matchSettleScore.getT2().toString();
                    forwScore= t1+"-"+t2;

                    List<Integer> integers = Arrays.asList(1021,1031,1032,1033);
                    List<String> corner = Arrays.asList("206","207","208");
                    if (integers.contains(matchSettleScoreDto.getSettleNum())) {
                        String extryInfo = matchSettleScore.getExtryInfo();
                        Integer integer = null;
                        if (!StringUtils.isBlank(extryInfo) ) {
                            integer = Integer.valueOf(extryInfo);
                            forwScore = processedScore(forwScore, matchSettleScoreDto.getSettleNum(), integer);
                        }else if(matchSettleScore.getGoWaterStatus()!=null && "1".equals(matchSettleScore.getGoWaterStatus().toString())){
                            forwScore = WinningMethodEnum.Method_8.getCode().toString();
                        }
                    }
                    if (corner.contains(matchSettleScore.getSettleNum())) {
                        Integer goWaterStatus = matchSettleScore.getGoWaterStatus();
                        //角球走水 10031
                        if (goWaterStatus!=null && goWaterStatus.equals(1))  forwScore = OperateLogTypeEnum.SCORES_SETTLE_10031.getCode().toString();
                    }
                }else {
                    return Response.failed("1031931");
                }
                matchSettleScore.setT1(matchSettleScoreDto.getT1());
                matchSettleScore.setT2(matchSettleScoreDto.getT2());
                matchSettleScore.setStatus(NOT_CONFIRM);
                matchSettleScore.setOperater(matchSettleScoreDto.getOperatorName());
                if(matchSettleScoreDto.getGoWaterStatus()!=null&&matchSettleScoreDto.getGoWaterStatus()==1){
                    matchSettleScore.setGoWaterStatus(1);
                }else {
                    matchSettleScore.setGoWaterStatus(0);
                }
                matchSettleScore.setExtryInfo(matchSettleScoreDto.getExtryInfo());
                matchSettleScore.setModifyTime(System.currentTimeMillis());
                //比分判断是否相同
//                if(MatchEventInfoSettleUtils.equileMatchSettleScores(matchSettleBefore,matchSettleScore)){
//                    return Response.failed("1031940");
//                }
                //1 判断顺序 结算开关是否开启，如果没开启则返回 简单校验逻辑();
                if (!matchSettleInfoHelper.checkSettleScoreAndAutoSettleNonEvent(matchSettleScore,null)){
                    return Response.failed("1031946");
                }
                matchSettleScoreRepository.updateById(matchSettleScore);

                //2.判断更新上半场(5)和全场比分(10) 更新结算信息
                if (matchSettleScore.getSettleNum().equals("105") || matchSettleScore.getSettleNum().equals("1010")) {
                    recordScore(matchSettleScoreDto);
                }
                wsPushService.pushStandardSettleScores(matchSettleScoreDto.getStandardMatchId(),
                        matchSettleScoreDto.getEventCode());

                //3.操作日志记录
                matchSettleOperateLogService.updateMatchSettleScoreAddLog(matchSettleScoreDto,forwScore,matchSettleScore,standardMatchInfo,OperateLogTypeEnum.EDIT.getCode().toString());
                log.info("[IFootballMatchScoresSettleApiImpl] updateMatchSettleScore with linkId:{} end!",matchSettleScoreDto.getLinkedId());
                return Response.success();
            }else {
                return Response.failed("1031933");
            }
        }catch (Exception e){
            log.error("[IFootballMatchScoresSettleApiImpl] updateMatchSettleScore with linkId:{} error:",matchSettleScoreDto.getLinkedId(), e);
            return Response.failed();
        }finally {
            redisService.unLock(key,key);
        }
    }

    /**
     * 管理员确认阶段比分
     * @param matchSettleScoreDto
     * @return
     */
    @Override
    public Response confirmMatchSettleScore(ConfirmMatchSettleScoreDto matchSettleScoreDto) {
        log.info("[IFootballMatchScoresSettleApiImpl] confirmMatchSettleScore-v2 with linkId:{} and param:{} start!",matchSettleScoreDto.getLinkedId(), matchSettleScoreDto);
        //0.加redis锁
        String key =CommonConstant.MATCH_PHASE_SCORE_SETTLE+ matchSettleScoreDto.getStandardMatchId();
        if(matchServiceHelper.checkIfOverSettleTime(matchSettleScoreDto.getStandardMatchId())){
            return Response.failed("1031930");
        }
        try {
            if(redisService.tryLock(key,key,2,5)) {
                MatchSettleScore matchSettleScore =null;
                matchSettleScore=matchSettleScoreRepository.getById(matchSettleScoreDto.getMatchScoreId());
                if (((matchSettleScore.getT1() == null || matchSettleScore.getT2() == null) && matchSettleScore.getExtryInfo() == null) && !"kick_off".equals(matchSettleScore.getEventCode())) {
                    return Response.failed("该阶段比分为null，请重新编辑比分");
                }
                if(matchSettleScore==null){
                    return Response.failed("1031931");
                }
                if(matchSettleScore.getStatus()>=CONFIRM){
                    return Response.failed("1031934");
                }
                matchSettleScore.setStatus(CONFIRM);
                matchSettleScore.setModifyTime(System.currentTimeMillis());
                matchSettleScoreRepository.updateById(matchSettleScore);
                //2.记录日志
                //走水 将编码设置为8
                if (matchSettleScore.getGoWaterStatus()!=null&&matchSettleScore.getGoWaterStatus().equals(1))  matchSettleScore.setExtryInfo("8");
                matchSettleOperateLogService.matchSettleScoreAddLog(matchSettleScore,matchSettleScoreDto.getOperatorName(),
                        OperateLogTypeEnum.CONFIRM_SCORE,"",matchSettleScoreDto.getIpAddress());
                //推送比分WS
                wsPushService.pushStandardSettleScores(matchSettleScoreDto.getStandardMatchId(),
                        matchSettleScoreDto.getEventCode());
                log.info("[IFootballMatchScoresSettleApiImpl] confirmMatchSettleScore with linkId:{} end!",matchSettleScoreDto.getLinkedId());
                return Response.success();
            }else {
                return Response.failed("1031933");
            }
        }catch (Exception e){
            log.error("[IFootballMatchScoresSettleApiImpl] confirmMatchSettleScore with linkId:{} error:",matchSettleScoreDto.getLinkedId(), e);
            return Response.failed();
        }finally {
            redisService.unLock(key,key);
        }
    }

    /**
     * 管理员结算阶段比分
     * @param matchSettleScoreDto
     * @return
     */
    @Override
    public Response settleMatchScore(SettleMatchScoreDto matchSettleScoreDto) {
        log.info("[IFootballMatchScoresSettleApiImpl] settleMatchScore with linkId:{} and param:{} start!",matchSettleScoreDto.getLinkedId(), matchSettleScoreDto);
        String key =CommonConstant.MATCH_PHASE_SCORE_SETTLE+ matchSettleScoreDto.getStandardMatchId();
        if(matchServiceHelper.checkIfOverSettleTime(matchSettleScoreDto.getStandardMatchId())){
            return Response.failed("1031930");
        }
        log.info("读取SETTLE_ROLLBACK_MATCH_ID_"+matchSettleScoreDto.getMatchScoreId()+redisService.get("SETTLE_ROLLBACK_MATCH_ID_"+matchSettleScoreDto.getMatchScoreId()));
        if(redisService.hasKey("SETTLE_ROLLBACK_MATCH_ID_"+matchSettleScoreDto.getMatchScoreId())){
            return Response.failed("1031960");
        }
        try {
            if(redisService.tryLock(key,key,2,5)) {
                MatchSettleScore matchSettleScore =null;
                matchSettleScore=matchSettleScoreRepository.getById(matchSettleScoreDto.getMatchScoreId());
                if(matchSettleScore==null){
                    return Response.failed("1031931");
                }
                if(matchSettleScore.getStatus()!=CONFIRM){
                    return Response.failed("1031932");
                }
                Integer settleTimes =matchSettleScore.getSettleTimes();
                if(settleTimes==null){
                    settleTimes=0;
                }
                if (matchSettleScore.getSettleCount()== null ) {
                    matchSettleScore.setSettleCount(0);
                }
                settleTimes++;

                //二次结算,必须给出结算原因
                if (matchSettleScore.getSettleCount() >  0 &&
                        (matchSettleScoreDto.getSettleReason()==null  ||
                                matchSettleScoreDto.getSettleReason()== 0) ) {
                    return Response.failed("1031953");
                }

                String  before= "-";
                Integer settleReason = matchSettleScore.getSettleReason();
                if (settleReason != null &&  settleReason != 0 ) {
                    before = settleReason.toString();
                    if (settleReason == 118) {
                        before += ": "+matchSettleScore.getSettleReasonDetail();
                    }
                }
                //这是理论时间不对 应该先查数据商，如果没数据商再赋值当前
                if(matchSettleScore.getEventTime()==null||matchSettleScore.getEventTime().equals(0l)){
                    Long eventTime =matchSettleCheckInfoHelper.searchEventTimeByScores(matchSettleScore);
                    if(eventTime==0l){
                        eventTime=matchSettleScore.getModifyTime();
                    }
                    matchSettleScore.setEventTime(eventTime);
                }
                matchSettleScore.setStatus(SETTLED);
                matchSettleScore.setOperater(matchSettleScoreDto.getOperatorName());
                matchSettleScore.setSettleTimes(settleTimes);
                matchSettleScore.setSettleCount(matchSettleScore.getSettleCount()+1);
                matchSettleScore.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.SETTLE);
                matchSettleScore.setUserid(matchSettleScoreDto.getOperatorId());
                matchSettleScore.setModifyTime(System.currentTimeMillis());
                matchSettleScore.setSettleReason(matchSettleScoreDto.getSettleReason());
                matchSettleScore.setSettleReasonDetail(matchSettleScoreDto.getSettleReasonDetail());

                matchSettleScore.setIsGrey(0);
                matchSettleScore.setHasDeleteEvent(0);
                matchSettleScore.setCurrentEventStatus(0);
                matchSettleCheckInfoHelper.endEventSettleByScore(matchSettleScore);
                matchSettleScoreRepository.updateById(matchSettleScore);
                matchSettleInfoHelper.updateMatchGrayStatus(matchSettleScore.getStandardMatchId());
                matchSettleInfoHelper.updateMatchCurrentEventStatus(matchSettleScore.getStandardMatchId());
                matchSettleCheckInfoHelper.updateMatchFifteenMinGraySettleFactor(matchSettleScore.getStandardMatchId(),matchSettleScore.getSettleNum());
                //结算时把回滚订单数清零
                matchServiceHelper.settleRollBackSetNullOrderCount(matchSettleScore.getId());
                //2.MQ下发

                if (matchSettleScore.getPeriodId()==100 && (matchSettleScore.getSettleNum().equals(MatchPeriodEnum.GOAL_10.getCode().toString()) ||
                        matchSettleScore.getSettleNum().equals(MatchPeriodEnum.Corner_3.getCode().toString()) ||
                        matchSettleScore.getSettleNum().equals(MatchPeriodEnum.BOOKINGS_9.getCode().toString()))) {
                    matchSettleScoresProducer.sendMatchSettleScores(matchSettleScore, 2);
                } else {
                    matchSettleScoresProducer.sendMatchSettleScores(matchSettleScore);
                }

                wsPushService.pushStandardSettleScores(matchSettleScoreDto.getStandardMatchId(),
                        matchSettleScoreDto.getEventCode());
                if (matchSettleScoreDto.getSettleReason() != null) {
                    matchSettleEventService.secondSettleWarnMango(matchSettleScoreDto, 1);
                }
                //1.比分结算增加操作日志
                if (matchSettleScore.getGoWaterStatus()!=null&&matchSettleScore.getGoWaterStatus().equals(1))  matchSettleScore.setExtryInfo("8");
                matchSettleOperateLogService.matchSettleScoreAddLog(matchSettleScore,matchSettleScoreDto.getOperatorName(),OperateLogTypeEnum.SCORE_SETTLE,before,matchSettleScoreDto.getIpAddress());
                syncScoreFactory.getProcessor(SettleSyncEnum.FOOTBALL_SYNC_SCORE).syncScore(matchSettleScoreDto);
                log.info("[IFootballMatchScoresSettleApiImpl] settleMatchScore with linkId:{} end!",matchSettleScoreDto.getLinkedId());
                return Response.success();
            }else {
                log.info("[IFootballMatchScoresSettleApiImpl] settleMatchScore with linkId:{} obtain redis fail!",matchSettleScoreDto.getLinkedId());
                return Response.failed("1031933");
            }
        }catch (Exception e){
            log.error("[IFootballMatchScoresSettleApiImpl] settleMatchScore with linkId:{} error:",matchSettleScoreDto.getLinkedId(), e);
            return Response.failed();
        }finally {
            redisService.unLock(key,key);
        }
    }

    /**
     * 管理员新增次序
     * @param addMatchSettleEventDto
     * @return
     */
    @Override
    public Response addMatchSettleEvent(AddMatchSettleEventDto addMatchSettleEventDto) {
        log.info("addMatchSettleEvent param,addMatchSettleEventDto: {}",addMatchSettleEventDto);
        if(matchServiceHelper.checkIfOverSettleTime(addMatchSettleEventDto.getStandardMatchId())){
            return Response.failed("1031930");
        }
        String key ="MATCH_SETTLE_INFO:"+ addMatchSettleEventDto.getStandardMatchId();
        try {
            if(redisService.tryLock(key,key,2,5)) {
                StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(addMatchSettleEventDto.getStandardMatchId());
                //1.校验
                if (standardMatchInfo == null) {
                    return Response.failed("1031931");
                }
                List<String> settleNumbers = new ArrayList<>();
                settleNumbers.add(addMatchSettleEventDto.getSettleNum());
                List<MatchSettleEvent> list = matchSettleEventRepository.getModelByStandardMatchIdAndSettleNums(addMatchSettleEventDto.getStandardMatchId(),settleNumbers);
                //2.判断事件序号
                Integer eventOrder = checkEventOrder(list);
                if (eventOrder == 0) {
                    return Response.failed("1031931");
                }
                eventOrder++;
                //3.新增
                MatchSettleEvent matchSettleEvent = new MatchSettleEvent();
                //0：未编码（初始化对应事件编码的数据）
                matchSettleEvent.setStatus(0);
                matchSettleEvent.setStandardMatchId(addMatchSettleEventDto.getStandardMatchId());
                matchSettleEvent.setEventCode(addMatchSettleEventDto.getEventCode());
//        matchSettleEvent.setSettleNum(eventOrder.toString());
                matchSettleEvent.setSettleNum(SettleNumUtils.getEventSettleNum(addMatchSettleEventDto.getEventCode(), addMatchSettleEventDto.getPeriodId()));
                matchSettleEvent.setModifyTime(System.currentTimeMillis());
                matchSettleEvent.setCreateTime(System.currentTimeMillis());
                matchSettleEvent.setEventOrder(eventOrder);
                matchSettleEvent.setSportId(1l);
                matchSettleEvent.setId(IdGenerator.nextId());
                matchSettleEvent.setThirdEventSourceId(matchSettleEvent.getId());
                matchSettleEvent.setDataSourceCode("PA");
                matchSettleEvent.setPeriodId(addMatchSettleEventDto.getPeriodId());
                matchSettleEvent.setCheckNumber(1);
                matchSettleEvent.setEventType(1);
                matchSettleEvent.setSettleCount(0);
                matchSettleEvent.setSettleTimes(0);
                // eventType=1不应该设置时段信息，时段信息应该设置在对应的eventType=3事件中
                matchSettleEvent.setFiveMinSection(null);
                //2.2 获得上个事件比分 自动计算比分
                List<MatchSettleEvent> matchSettleEventList = new ArrayList<>();
                matchSettleEventList.add(matchSettleEvent);
                if (!(matchSettleEvent.getEventCode().equals("corner") || matchSettleEvent.getPeriodId().equals(50l))) {
                    MatchSettleEvent matchSettleEvent2 = new MatchSettleEvent();
                    BeanUtils.copyProperties(matchSettleEvent, matchSettleEvent2);
                    matchSettleEvent2.setEventType(2);
                    matchSettleEvent2.setId(IdGenerator.nextId());
                    matchSettleEvent2.setSportId(1l);
                    matchSettleEvent2.setDataSourceCode("PA");
                    matchSettleEvent2.setCheckNumber(1);
                    matchSettleEvent2.setSettleNum(SettleNumUtils.getTypeEventSettleNum(matchSettleEvent2.getEventCode(), matchSettleEvent2.getPeriodId(), 2));
                    matchSettleEventList.add(matchSettleEvent2);
                }
                // 创建eventType=3（时段事件）- 只在periodId=6L或7L时创建（加时赛41L, 42L没有eventType=3）
                Long periodId = matchSettleEvent.getPeriodId();
                if (periodId != null && (periodId.equals(6L) || periodId.equals(7L))) {
                    MatchSettleEvent timePhaseEvent = new MatchSettleEvent();
                    BeanUtils.copyProperties(matchSettleEvent, timePhaseEvent);
                    timePhaseEvent.setId(IdGenerator.nextId());
                    timePhaseEvent.setEventType(3);
                    timePhaseEvent.setSettleNum(SettleNumUtils.getTypeEventSettleNum(timePhaseEvent.getEventCode(), timePhaseEvent.getPeriodId(), 3));
                    // eventType=3才设置时段信息
                    timePhaseEvent.setFiveMinSection(addMatchSettleEventDto.getFiveMinSection());
                    matchSettleEventList.add(timePhaseEvent);
                }
                matchSettleEventRepository.saveOrUpdateBatch(matchSettleEventList);
                //4.查询事件列表返回
                if("goal".equals(addMatchSettleEventDto.getEventCode()) && StringUtils.isNotBlank(addMatchSettleEventDto.getOperatorName())){
                    wsPushService.pushSettleMatchList(new MatchListSettleDto(addMatchSettleEventDto.getStandardMatchId(),
                            addMatchSettleEventDto.getEventCode(),null,null,5));
                }else {
                    wsPushService.pushStandardSettleEvent(addMatchSettleEventDto.getStandardMatchId(),
                            addMatchSettleEventDto.getEventCode());
                }
                redisService.unLock(key,key);
                return Response.success();
            }else {
                return Response.failed();
            }
        }catch (Exception e){
            e.printStackTrace();
            return Response.failed();
        }
    }

    /**
     * 管理员编辑次序比分
     * @param editMatchSettleEventDto
     * @return
     */

    @Override
    public Response editMatchSettleEvent(EditMatchSettleEventDto editMatchSettleEventDto) {
        log.info("editMatchSettleEvent param,editMatchSettleEvent-v2: {}",editMatchSettleEventDto);
        String key =CommonConstant.MATCH_SEQUENCE_SCORE_SETTLE + editMatchSettleEventDto.getStandardMatchId();
        MatchSettleEvent extryevent = null ;
        MatchSettleEvent matchSettleEvent = null;
        MatchSettleEvent matchSettleEventBefore = new MatchSettleEvent();
        try {
            if(redisService.tryLock(key,key,2,5)) {
                // 先获取事件，检查eventType（注意：corner事件可能不存在matchSettleEvent，而是MatchSettleScore）
                matchSettleEvent = matchSettleEventRepository.getById(editMatchSettleEventDto.getEventId());
                if(matchSettleEvent==null){
                    return Response.failed("1031935");
                }
                BeanUtils.copyProperties(matchSettleEvent,matchSettleEventBefore);
                // 如果编辑的是eventType=3（时段事件），必须先确保对应的eventType=1（比分事件）已结算（status=3）
                if (matchSettleEvent != null && matchSettleEvent.getEventType() != null && matchSettleEvent.getEventType() == 3) {
                    // 查找对应的eventType=1事件
                    String scoreSettleNum = SettleNumUtils.getTypeEventSettleNum(
                            matchSettleEvent.getEventCode(), 
                            matchSettleEvent.getPeriodId(), 
                            1);
                    List<MatchSettleEvent> scoreEvents = matchSettleEventRepository.getByMatchIdAndSettleNumAndEventOrderAndPeriodId(
                            matchSettleEvent.getStandardMatchId(), 
                            scoreSettleNum, 
                            matchSettleEvent.getEventOrder(), 
                            matchSettleEvent.getPeriodId());
                    MatchSettleEvent scoreEvent = scoreEvents.stream()
                            .filter(e -> e.getEventType() != null && e.getEventType() == 1)
                            .findFirst()
                            .orElse(null);
                    
                    // 检查eventType=1事件是否存在
                    if (scoreEvent == null) {
                        log.warn("editMatchSettleEvent::尝试编辑时段事件，但对应的比分事件不存在, eventId:{}, eventCode:{}", 
                                matchSettleEvent.getId(), matchSettleEvent.getEventCode());
                        return Response.failed("1031941"); // 返回错误码：必须先编辑比分
                    }
                    
                    // 检查eventType=1事件是否已结算（status=3）
                    if (scoreEvent.getStatus() == null || scoreEvent.getStatus() != 3) {
                        log.warn("editMatchSettleEvent::尝试编辑时段事件，但对应的比分事件未结算, eventId:{}, scoreEventId:{}, scoreEventStatus:{}, eventCode:{}", 
                                matchSettleEvent.getId(), scoreEvent.getId(), scoreEvent.getStatus(), matchSettleEvent.getEventCode());
                        return Response.failed("1031941"); // 返回错误码：必须先结算比分事件
                    }
                    editMatchSettleEventDto.setT1(scoreEvent.getT1());
                    editMatchSettleEventDto.setT2(scoreEvent.getT2());
                    matchSettleEvent.setT1(scoreEvent.getT1());
                    matchSettleEvent.setT2(scoreEvent.getT2());
                    matchSettleEvent.setExtryInfo(scoreEvent.getExtryInfo());
                    matchSettleEvent.setGoWaterStatus(scoreEvent.getGoWaterStatus());
                    editMatchSettleEventDto.setHomeAway(scoreEvent.getHomeAway());
                    matchSettleEvent.setHomeAway(scoreEvent.getHomeAway());
                    if(editMatchSettleEventDto.getEventCode().equals("fa_card")) {
                        matchSettleEvent.setEventCode(scoreEvent.getEventCode());
                    }
                }
                if(StringUtils.isEmpty(editMatchSettleEventDto.getHomeAway())){
                    return Response.failed("1031939");
                }
                if(matchSettleEvent.getEventType() == 3 && StringUtils.isEmpty(editMatchSettleEventDto.getFiveMinSection())){
                    return Response.failed("1031939");
                }
                if(editMatchSettleEventDto.getEventCode().equals("goal")){
                    // matchSettleEvent已经在上面获取了，如果为null则重新获取

                    if(!matchSettleCheckInfoHelper.isPeriodScoresBeforeSettledByEvent(matchSettleEvent)){
                        return Response.failed("10138");
                    }

                    //1.自动计算进球比分
                    updateGoalAndCornerEventByInfo(matchSettleEvent,editMatchSettleEventDto.getHomeAway(),"goal");
                    //比分校验是否相同
                    if(editMatchSettleEventDto.getGoWaterStatus()!=null&&editMatchSettleEventDto.getGoWaterStatus()==1){
                        matchSettleEvent.setGoWaterStatus(1);
                    }else {
                        matchSettleEvent.setGoWaterStatus(0);
                    }
                    matchSettleEvent.setExtryInfo(editMatchSettleEventDto.getExtryInfo());
                    matchSettleEvent.setModifyTime(System.currentTimeMillis());
                    matchSettleEvent.setHomeAway(editMatchSettleEventDto.getHomeAway());
                    matchSettleEvent.setStatus(1);
                    matchSettleEvent.setOperater(editMatchSettleEventDto.getOperatorName());
                    // eventType=1不应该设置时段信息，时段信息应该设置在对应的eventType=3事件中
                    if (matchSettleEvent.getEventType() != null && matchSettleEvent.getEventType() == 3) {
                        matchSettleEvent.setFiveMinSection(editMatchSettleEventDto.getFiveMinSection());
                    }
//                    if(MatchEventInfoSettleUtils.equileMatchSettleEvent(matchSettleEventBefore,matchSettleEvent)){
//                        return Response.failed("1031940");
//                    }
//                    if(matchSettleEvent.getSettleCount() == 1 && matchSettleEvent.getT1().equals(matchSettleEventBefore.getT1()) && matchSettleEvent.getT2().equals(matchSettleEventBefore.getT2())
//                            && matchSettleEventBefore.getFiveMinSection() == null && matchSettleEvent.getFiveMinSection() != null) {
//                        redisService.set(CommonConstant.FIVE_MIN_SETTLE_TIMES+matchSettleEvent.getId(), 1, RedisConfig.REDIS_WEEK_TIME);
//                    }

                    matchSettleEventRepository.updateById(matchSettleEvent);
                    //编辑影子事件比分和homeAway
                    extryevent =matchServiceHelper.getExtryEvent(matchSettleEvent);
                    if(extryevent!=null){
                        MatchSettleEventDto matchSettleEventDto =new MatchSettleEventDto();
                        BeanUtils.copyProperties(matchSettleEvent,matchSettleEventDto);
                        extryevent.setExtryInfo(editMatchSettleEventDto.getExtryInfo());
                        extryevent.setT1(matchSettleEvent.getT1());
                        extryevent.setT2(matchSettleEvent.getT2());
                        extryevent.setModifyTime(System.currentTimeMillis());
                        extryevent.setHomeAway(editMatchSettleEventDto.getHomeAway());
//                        extryevent.setStatus(1);
                        extryevent.setOperater(editMatchSettleEventDto.getOperatorName());
                        matchSettleEventRepository.updateById(extryevent);
                    }

                }else if(editMatchSettleEventDto.getEventCode().equals("corner")){
                    //1.事件只编辑比分
                    // matchSettleEvent已经在上面获取了，如果为null则重新获取
                    if(matchSettleEvent!=null){
                        //自动计算角球比分
                        updateGoalAndCornerEventByInfo(matchSettleEvent,editMatchSettleEventDto.getHomeAway(),"corner");
                        matchSettleEvent.setExtryInfo(editMatchSettleEventDto.getExtryInfo());
                        matchSettleEvent.setModifyTime(System.currentTimeMillis());
                        matchSettleEvent.setHomeAway(editMatchSettleEventDto.getHomeAway());
                        matchSettleEvent.setStatus(1);
                        matchSettleEvent.setOperater(editMatchSettleEventDto.getOperatorName());
                        // eventType=1不应该设置时段信息，时段信息应该设置在对应的eventType=3事件中
                        if (matchSettleEvent.getEventType() != null && matchSettleEvent.getEventType() == 3) {
                            matchSettleEvent.setFiveMinSection(editMatchSettleEventDto.getFiveMinSection());
                        }
                        if(editMatchSettleEventDto.getGoWaterStatus()!=null&&editMatchSettleEventDto.getGoWaterStatus()==1){
                            matchSettleEvent.setGoWaterStatus(1);
                        }else {
                            matchSettleEvent.setGoWaterStatus(0);
                        }
//                        if(MatchEventInfoSettleUtils.equileMatchSettleEvent(matchSettleEventBefore,matchSettleEvent)){
//                            return Response.failed("1031940");
//                        }
                        matchSettleEvent.setModifyTime(System.currentTimeMillis());
//                        if(matchSettleEvent.getSettleCount() == 1 && matchSettleEvent.getT1().equals(matchSettleEventBefore.getT1()) && matchSettleEvent.getT2().equals(matchSettleEventBefore.getT2())
//                                && matchSettleEventBefore.getFiveMinSection() == null && matchSettleEvent.getFiveMinSection() != null) {
//                            redisService.set(CommonConstant.FIVE_MIN_SETTLE_TIMES+matchSettleEvent.getId(), 1, RedisConfig.REDIS_WEEK_TIME);
//                        }
                        matchSettleEventRepository.updateById(matchSettleEvent);
                    }else {
                        MatchSettleScore matchSettleScore =matchSettleScoreRepository.getById(editMatchSettleEventDto.getEventId());
                        if(matchSettleScore!=null) {
                            //角球阶段比分由人工录入
                            //比分判断是否相同
//                            if(MatchEventInfoSettleUtils.equileMatchSettleEvent(matchSettleEventBefore,matchSettleEvent)){
//                                return Response.failed("1031940");
//                            }
                            if(editMatchSettleEventDto.getGoWaterStatus()!=null&&editMatchSettleEventDto.getGoWaterStatus()==1){
                                matchSettleEvent.setGoWaterStatus(1);
                            }else {
                                matchSettleEvent.setGoWaterStatus(0);
                            }
                            matchSettleScore.setExtryInfo(editMatchSettleEventDto.getExtryInfo());
                            matchSettleScore.setT1(editMatchSettleEventDto.getT1());
                            matchSettleScore.setT2(editMatchSettleEventDto.getT2());
                            matchSettleScore.setModifyTime(System.currentTimeMillis());
                            matchSettleEvent.setOperater(editMatchSettleEventDto.getOperatorName());
                            matchSettleScore.setStatus(1);
                            matchSettleScoreRepository.updateById(matchSettleScore);
                        }
                    }
                    //2.阶段比分
                }else if(editMatchSettleEventDto.getEventCode().equals("fa_card")){
                    //1.根据facard条件设置 主客队和 罚牌类型
//                    if(StringUtils.isEmpty(editMatchSettleEventDto.getHomeAway())){
//                        return Response.failed("1031939");
//                    }
//
//                    if(matchSettleEvent.getEventType() == 3 && StringUtils.isEmpty(editMatchSettleEventDto.getFiveMinSection())){
//                        return Response.failed("1031939");
//                    }

                    //2.自动计算罚牌比分
                    if (matchSettleEvent.getEventType() == 1) {
                        updateFaCardEventByInfo(matchSettleEvent,editMatchSettleEventDto.getHomeAway());
                    }
                    matchSettleEvent.setModifyTime(System.currentTimeMillis());
                    matchSettleEvent.setStatus(1);
                    if(editMatchSettleEventDto.getGoWaterStatus()!=null&&editMatchSettleEventDto.getGoWaterStatus()==1){
                        matchSettleEvent.setGoWaterStatus(1);
                    }else {
                        matchSettleEvent.setGoWaterStatus(0);
                    }
                    matchSettleEvent.setOperater(editMatchSettleEventDto.getOperatorName());
                    // eventType=1不应该设置时段信息，时段信息应该设置在对应的eventType=3事件中
                    if (matchSettleEvent.getEventType() != null && matchSettleEvent.getEventType() == 3) {
                        matchSettleEvent.setFiveMinSection(editMatchSettleEventDto.getFiveMinSection());
                    }
//                    if(MatchEventInfoSettleUtils.equileMatchSettleEvent(matchSettleEventBefore,matchSettleEvent)){
//                        return Response.failed("1031940");
//                    }
//                    if(matchSettleEvent.getSettleCount() == 1 && matchSettleEvent.getT1().equals(matchSettleEventBefore.getT1()) && matchSettleEvent.getT2().equals(matchSettleEventBefore.getT2())
//                            && matchSettleEventBefore.getFiveMinSection() == null && matchSettleEvent.getFiveMinSection() != null) {
//                        redisService.set(CommonConstant.FIVE_MIN_SETTLE_TIMES+matchSettleEvent.getId(), 1, RedisConfig.REDIS_WEEK_TIME);
//                    }
                    matchSettleEventRepository.updateById(matchSettleEvent);
                    //3.设置到影子事件中比分 以及主客队 罚牌类型等
                    if (matchSettleEvent.getEventType()==1){
                        extryevent =matchServiceHelper.getExtryEvent(matchSettleEvent);
                        if(extryevent!=null){
                            extryevent.setHomeAway(matchSettleEvent.getHomeAway());
                            extryevent.setT1(matchSettleEvent.getT1());
                            extryevent.setT2(matchSettleEvent.getT2());
                            extryevent.setFirstT1(matchSettleEvent.getFirstT1());
                            extryevent.setFirstT2(matchSettleEvent.getFirstT2());
                            extryevent.setSecondT1(matchSettleEvent.getSecondT1());
                            extryevent.setSecondT2(matchSettleEvent.getSecondT2());
                            extryevent.setModifyTime(System.currentTimeMillis());
                            extryevent.setEventCode(matchSettleEvent.getEventCode());
//                        extryevent.setStatus(1);
                            extryevent.setOperater(editMatchSettleEventDto.getOperatorName());
                            matchSettleEventRepository.updateById(extryevent);
                        }
                    }
                }
                //2.事件编辑记录日志
                // 注意：corner事件中，如果matchSettleEvent为null（是MatchSettleScore的情况），则跳过后续的日志和eventType处理
                if (matchSettleEvent != null) {
                    log.info("{}--保存15分钟日志:event {}",editMatchSettleEventDto.getEventCode(),matchSettleEvent);
                    // 根据eventType决定处理逻辑
                    if (matchSettleEvent.getEventType() != null && matchSettleEvent.getEventType() == 1) {
                        // 编辑eventType=1（次序比分）时：
                        // 1. 确保存在时段事件（eventType=3）并同步比分信息
                        ensurePeriodEventExists(matchSettleEvent, editMatchSettleEventDto);
                    }
                    if (!(matchSettleEvent.getEventType()==3 && "fa_card".equals(editMatchSettleEventDto.getEventCode()))) {
                        matchSettleEvent.setEventCode(editMatchSettleEventDto.getEventCode());
                        matchSettleEvent.setHomeAway(editMatchSettleEventDto.getHomeAway());
                    }

                    String homeAway = matchSettleEventBefore.getHomeAway();
                    String homeAwayNew = matchSettleEvent.getHomeAway();
                    if ("goal".equals(editMatchSettleEventDto.getEventCode())) {
                        homeAway = goalProcessRest( matchSettleEventBefore.getHomeAway(),matchSettleEventBefore.getStatus());
                        homeAwayNew = goalProcessRest( matchSettleEvent.getHomeAway(),matchSettleEvent.getStatus());

                    };
                    if ("fa_card".equals(editMatchSettleEventDto.getEventCode())){
                        homeAway = faCardProcessRest(matchSettleEventBefore.getEventCode(), matchSettleEventBefore.getHomeAway(),matchSettleEventBefore.getStatus());
                        if (matchSettleEvent.getEventType() == 3) {
                            homeAwayNew = faCardProcessRest(matchSettleEvent.getEventCode(), matchSettleEvent.getHomeAway(),matchSettleEvent.getStatus());
                        }
                    }
                    matchSettleEventBefore.setHomeAway(homeAway);
                    matchSettleEvent.setHomeAway(homeAwayNew);
                    //事件编辑增加日志
                    // eventType=1不应该设置时段信息，只记录日志
                    // eventType=3的时段信息已经在上面更新了
                    if (matchSettleEvent.getEventType() != null && matchSettleEvent.getEventType() == 3 && (!matchSettleEvent.getEventCode().equals("goal"))) {
                        matchSettleEvent.setFifteenMinSection(editMatchSettleEventDto.getFiveMinSection());
                    }
                    log.info("{}--保存15分钟日志:修改前 {}---{}",editMatchSettleEventDto.getEventCode()
                            ,matchSettleEventBefore.getFiveMinSection(),
                            matchSettleEventBefore.getFifteenMinSection());
                    log.info("{}--保存15分钟日志:修改后 {}---{}",editMatchSettleEventDto.getEventCode()
                            ,matchSettleEvent.getFiveMinSection(),
                            matchSettleEvent.getFifteenMinSection());
                    matchSettleOperateLogService.matchSettleEventAddLog(matchSettleEventBefore,matchSettleEvent,editMatchSettleEventDto.getOperatorName(),
                            OperateLogTypeEnum.EDIT,editMatchSettleEventDto.getIpAddress());
                }
                
                //3.返回查询事件列表
                wsPushService.pushStandardSettleEvent(editMatchSettleEventDto.getStandardMatchId(),
                        editMatchSettleEventDto.getEventCode());
                return Response.success();
            }else {
                return Response.failed("1031933");
            }
        }catch (Exception e){
            log.error("IFootballMatchScoresSettleApiImpl-editMatchSettleEvent:",e);
            return Response.failed();
        }finally {
            redisService.unLock(key,key);
        }
    }

    /**
     *
     * 管理员编辑次序跟球员玩法
     * @param editMatchSettleEventDto
     * @return
     */
    @Override
    public Response editMatchSettleEventMethodAndPlayer(EditMatchSettleEventDto editMatchSettleEventDto) {
        log.info("editMatchSettleEventMethodAndPlayer param,editMatchSettleEventDto: {}",editMatchSettleEventDto);
        String key ="MATCH_SETTLE_INFO:"+ editMatchSettleEventDto.getEventId();
        if(matchServiceHelper.checkIfOverSettleTime(editMatchSettleEventDto.getStandardMatchId())){
            return Response.failed("1031930");
        }
        try {
            if(redisService.tryLock(key,key,2,5)) {
                MatchSettleEvent matchSettleEvent =matchSettleEventRepository.getById(editMatchSettleEventDto.getEventId());
                if(matchSettleEvent==null){
                    return Response.failed("1031935");
                }
                MatchSettleEvent matchSettleEventBefore =new MatchSettleEvent();
                BeanUtils.copyProperties(matchSettleEvent,matchSettleEventBefore);
                if(editMatchSettleEventDto.getGoWaterStatus()!=null&&editMatchSettleEventDto.getGoWaterStatus()==1){
                    matchSettleEvent.setGoWaterStatus(1);
                }
                matchSettleEvent.setExtryInfo(editMatchSettleEventDto.getExtryInfo());
                matchSettleEvent.setPlayerNameCode(editMatchSettleEventDto.getMatchPlayerNameCode());
                matchSettleEvent.setStatus(1);
                matchSettleEvent.setModifyTime(System.currentTimeMillis());
                matchSettleEvent.setOperater(editMatchSettleEventDto.getOperatorName());
                matchSettleEventRepository.updateById(matchSettleEvent);

                //进球方式和球员_操作日志
                matchSettleOperateLogService.matchSettleEventAddLog(matchSettleEventBefore,matchSettleEvent,editMatchSettleEventDto.getOperatorName(),
                        OperateLogTypeEnum.PLAYER_AND_GOAL_TYPE,editMatchSettleEventDto.getIpAddress());
                if("goal".equals(editMatchSettleEventDto.getEventCode()) && StringUtils.isNotBlank(editMatchSettleEventDto.getOperatorName())){
                    wsPushService.pushSettleMatchList(new MatchListSettleDto(editMatchSettleEventDto.getStandardMatchId(),
                            editMatchSettleEventDto.getEventCode(),null,null,6));
                }else {
                    wsPushService.pushStandardSettleEvent(editMatchSettleEventDto.getStandardMatchId(),
                            editMatchSettleEventDto.getEventCode());
                }
                return Response.success();
            }else {
                return Response.failed("1031933");
            }
        }catch (Exception e){
            log.error("IFootballMatchScoresSettleApiImpl-editMatchSettleEventMethodAndPlayer:",e);
            return Response.failed();
        }finally {
            redisService.unLock(key,key);
        }
    }

    /**
     * 管理员确认次序比分
     * @param matchSettleEventDto
     * @return
     */
    @Override
    public Response confirmMatchSettleEvent(EditMatchSettleEventDto matchSettleEventDto) {
        log.info("confirmMatchSettleEvent param,matchSettleEventDto: {}",matchSettleEventDto);
        String key = CommonConstant.MATCH_SEQUENCE_SCORE_SETTLE + matchSettleEventDto.getStandardMatchId();
        if(matchServiceHelper.checkIfOverSettleTime(matchSettleEventDto.getStandardMatchId())){
            return Response.failed("1031930");
        }
        log.info("事件Id::{}:: 当前事件被确认参数:{} ",matchSettleEventDto.getEventId(),matchSettleEventDto);
        try {
            if(redisService.tryLock(key,key,2,5)) {
                MatchSettleEvent matchSettleEvent =matchSettleEventRepository.getById(matchSettleEventDto.getEventId());
                if(matchSettleEvent==null){
                    return Response.failed("1031935");
                }
                if(matchSettleEvent.getStatus()!=NOT_CONFIRM){
                    return Response.failed("1031934");
                }
                matchSettleEvent.setModifyTime(System.currentTimeMillis());
                matchSettleEvent.setStatus(CONFIRM);
                matchSettleEventRepository.updateById(matchSettleEvent);
                //2.确认记录日志
                matchSettleEvent.setFifteenMinSection(matchSettleEvent.getFiveMinSection());
                matchSettleOperateLogService.matchSettleEventAddLog(matchSettleEvent,matchSettleEventDto.getOperatorName(),
                        OperateLogTypeEnum.CONFIRM_SCORE.getCode().toString(),"",matchSettleEventDto.getIpAddress());
                //3.返回查询事件列表
                wsPushService.pushStandardSettleEvent(matchSettleEventDto.getStandardMatchId(),
                        matchSettleEventDto.getEventCode());
                return Response.success();
            }else {
                return Response.failed("1031933");
            }
        }catch (Exception e){
            log.error("IFootballMatchScoresSettleApiImpl-confirmMatchSettleEvent:",e);
            return Response.failed();
        }finally {
            redisService.unLock(key,key);
        }
    }

    /**
     * 管理员结算次序比分
     * @param matchSettleScoreDto
     * @return
     */
    @Override
    public Response settleMatchSettleEvent(EditMatchSettleEventDto matchSettleScoreDto) {
        log.info("settleMatchSettleEvent param,matchSettleScoreDto: {}",matchSettleScoreDto);
        String key = CommonConstant.MATCH_SEQUENCE_SCORE_SETTLE + matchSettleScoreDto.getStandardMatchId();
        if(matchServiceHelper.checkIfOverSettleTime(matchSettleScoreDto.getStandardMatchId())){
            return Response.failed("1031930");
        }
        if(redisService.hasKey("SETTLE_ROLLBACK_MATCH_ID_"+matchSettleScoreDto.getEventId())){
            return Response.failed("1031960");
        }
        try {
            if(redisService.tryLock(key,key,2,5)) {
                MatchSettleEvent matchSettleEvent =matchSettleEventRepository.getById(matchSettleScoreDto.getEventId());
                if(matchSettleEvent==null){
                    return Response.failed("1031935");
                }
                if(matchSettleEvent.getStatus()!=CONFIRM){
                    return Response.failed("1031936");
                }
                Integer settleTimes =matchSettleEvent.getSettleTimes();
                if(settleTimes==null){
                    settleTimes=0;
                }
                if (matchSettleEvent.getSettleCount()== null ) {
                    matchSettleEvent.setSettleCount(0);
                }

                settleTimes++;


//                if(matchSettleEvent.getSettleCount() == 1) {
//                    Object object = redisService.get(CommonConstant.FIVE_MIN_SETTLE_TIMES+matchSettleEvent.getId());
//                    if(object != null) {
//                        settleTimes--;
//                        matchSettleEvent.setSettleCount(0);
//                        redisService.del(CommonConstant.FIVE_MIN_SETTLE_TIMES+matchSettleEvent.getId());
//                    }
//                }

                //二次结算,必须给出结算原因
                if (matchSettleEvent.getSettleCount() >  0 &&
                        (matchSettleScoreDto.getSettleReason()==null  ||
                                matchSettleScoreDto.getSettleReason()== 0) ) {
                    return Response.failed("1031953");
                }

                String  before= "-";
                Integer settleReason = matchSettleEvent.getSettleReason();
                if (settleReason != null &&  settleReason != 0 ) {
                    before = settleReason.toString();
                    if (settleReason == 118) {
                        before += ": "+matchSettleEvent.getSettleReasonDetail();
                    }
                }
                if(!matchSettleEvent.getSettleNum().equals("1028")){
                    if(matchSettleEvent.getEventTime()==null||matchSettleEvent.getEventTime().equals(0l)){
                        Long eventTime =matchSettleCheckInfoHelper.searchEventTimeByEvent(matchSettleEvent);
                        if(eventTime==0l){
                            eventTime=matchSettleEvent.getModifyTime();
                        }
                        matchSettleEvent.setEventTime(eventTime);
                    }
                }
                matchSettleEvent.setStatus(SETTLED);
                matchSettleEvent.setSettleCount(matchSettleEvent.getSettleCount()+1);
                matchSettleEvent.setModifyTime(System.currentTimeMillis());
                matchSettleEvent.setSettleTimes(settleTimes);
                matchSettleEvent.setOperater(matchSettleScoreDto.getOperatorName());
                matchSettleEvent.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.SETTLE);
                matchSettleEvent.setUserid(matchSettleScoreDto.getOperatorId());
                matchSettleEvent.setSettleReason(matchSettleScoreDto.getSettleReason());
                matchSettleEvent.setSettleReasonDetail(matchSettleScoreDto.getSettleReasonDetail());
                matchSettleEvent.setIsGrey(0);
                matchSettleEvent.setHasDeleteEvent(0);
                matchSettleEvent.setCurrentEventStatus(0);
                matchSettleCheckInfoHelper.endEventSettleByEvent(matchSettleEvent);
                matchSettleEventRepository.updateById(matchSettleEvent);
                matchSettleInfoHelper.updateMatchGrayStatus(matchSettleEvent.getStandardMatchId());
                matchSettleInfoHelper.updateMatchCurrentEventStatus(matchSettleEvent.getStandardMatchId());
                log.info("比分Id::{}:: 当前事件被结算参数:{} ",matchSettleScoreDto.getEventId(),matchSettleEvent);
                if (matchSettleScoreDto.getSettleReason() != null) {
                    SettleMatchScoreDto warnPara = new SettleMatchScoreDto();
                    warnPara.setStandardMatchId(matchSettleScoreDto.getStandardMatchId());
                    warnPara.setSettleReason(matchSettleScoreDto.getSettleReason());
                    warnPara.setSettleNum(Integer.valueOf(matchSettleScoreDto.getSettleNum()));
                    matchSettleEventService.secondSettleWarnMango(warnPara, 1);
                }
                //1.日志
                matchSettleEvent.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.SETTLE);
                matchSettleEvent.setFifteenMinSection(matchSettleScoreDto.getFifteenMinSection());
                matchSettleOperateLogService.matchSettleEventAddLog(matchSettleEvent,
                        matchSettleScoreDto.getOperatorName(),OperateLogTypeEnum.SCORE_SETTLE.getCode().toString()
                        ,before,matchSettleScoreDto.getIpAddress());

                //2.MQ下发
                matchSettleScoresProducer.sendMatchSettleEvent(matchSettleEvent);
                wsPushService.pushStandardSettleEvent(matchSettleScoreDto.getStandardMatchId(),
                        matchSettleScoreDto.getEventCode());
                return Response.success();
            }else {
                return Response.failed("1031933");
            }
        }catch (Exception e){
            log.error("IFootballMatchScoresSettleApiImpl-settleMatchSettleEvent:",e);
            return Response.failed();
        }finally {
            redisService.unLock(key,key);
        }
    }

    /**
     * 查询点球大战比分
     * @param settleScoreSearchDto
     * @return
     */
    @Override
    public Response<PenaltyScoresVo> searchPenaltyScores(MatchSettleScoreSearchDto settleScoreSearchDto) {
        PenaltyScoresVo penaltyScoresVo =new PenaltyScoresVo();
        penaltyScoresVo.setStandardMatchId(settleScoreSearchDto.getStandardMatchId());
        List<MatchSettleEventEntity> homeEvent =matchSettleEventRepository.getByMatchIdAndEventCodeAndSettleNumAndHomeAway(settleScoreSearchDto.getStandardMatchId(),"goal","1030","home");
        List<MatchSettleEventDto> homeEventList=new ArrayList<>();
        for (MatchSettleEventEntity matchSettleEvent : homeEvent) {
            MatchSettleEventDto matchSettleEventDto=new MatchSettleEventDto();
            BeanUtils.copyProperties(matchSettleEvent,matchSettleEventDto);
            matchSettleEventDto.setId(matchSettleEvent.getId().toString());
            matchSettleEventDto.setScoresPeriodFreeze(matchSettleEvent.getSettleFreeze());
            homeEventList.add(matchSettleEventDto);
        }
        penaltyScoresVo.setHomeEventList(homeEventList);
        List<MatchSettleEventEntity> awayEvent =matchSettleEventRepository.getByMatchIdAndEventCodeAndSettleNumAndHomeAway(settleScoreSearchDto.getStandardMatchId(),"goal","1030","away");
        List<MatchSettleEventDto> awayEventList=new ArrayList<>();
        for (MatchSettleEventEntity matchSettleEvent : awayEvent) {
            MatchSettleEventDto matchSettleEventDto=new MatchSettleEventDto();
            BeanUtils.copyProperties(matchSettleEvent,matchSettleEventDto);
            matchSettleEventDto.setId(matchSettleEvent.getId().toString());
            matchSettleEventDto.setScoresPeriodFreeze(matchSettleEvent.getSettleFreeze());
            awayEventList.add(matchSettleEventDto);
        }
        penaltyScoresVo.setAwayEventList(awayEventList);

        List<MatchSettleEventEntity> homeAway5 =matchSettleEventRepository.getByMatchIdAndEventCodeAndSettleNumAndHomeAway(settleScoreSearchDto.getStandardMatchId(),"goal","1029",null);
        if(homeAway5.size()!=0){
            MatchSettleEventDto matchSettleEventDto=new MatchSettleEventDto();
            BeanUtils.copyProperties(homeAway5.get(0),matchSettleEventDto);
            matchSettleEventDto.setId(homeAway5.get(0).getId().toString());
            matchSettleEventDto.setScoresPeriodFreeze(homeAway5.get(0).getSettleFreeze());
            penaltyScoresVo.setHomeAway5RoundEvent(matchSettleEventDto);
        }
        //查询谁先射门
        MatchSettleEventExample teamFirstExample =new MatchSettleEventExample();
        teamFirstExample.createCriteria().andStandardMatchIdEqualTo(settleScoreSearchDto.getStandardMatchId())
                .andEventCodeEqualTo("goal").andSettleNumEqualTo("-1030");
        List<MatchSettleEventEntity> teamFirstList =matchSettleEventRepository.getByMatchIdAndEventCodeAndSettleNumAndHomeAway(settleScoreSearchDto.getStandardMatchId(),"goal","-1030",null);
        if(teamFirstList.size()!=0){
            MatchSettleEventEntity event= teamFirstList.get(0);
            MatchSettleEventDto matchSettleEventDto =new MatchSettleEventDto();
            BeanUtils.copyProperties(event,matchSettleEventDto);
            matchSettleEventDto.setId(event.getId().toString());
            penaltyScoresVo.setTeamFirst(matchSettleEventDto);
        }else {
            //旧数据兼容插入一条记录
            this.addTeamFirstEvent(penaltyScoresVo,settleScoreSearchDto.getStandardMatchId(),homeEventList,awayEventList);
        }
        List<MatchSettleEventEntity> homeAwayAll =matchSettleEventRepository.getByMatchIdAndEventCodeAndSettleNumAndHomeAway(settleScoreSearchDto.getStandardMatchId(),"goal","1028",null);
        if(homeAwayAll.size()!=0){
            MatchSettleEventDto matchSettleEventDto=new MatchSettleEventDto();
            BeanUtils.copyProperties(homeAwayAll.get(0),matchSettleEventDto);
            matchSettleEventDto.setId(homeAwayAll.get(0).getId().toString());
            matchSettleEventDto.setScoresPeriodFreeze(homeAwayAll.get(0).getSettleFreeze());

            penaltyScoresVo.setHomeAwayAllRoundEvent(matchSettleEventDto);
        }
        //点球大战走水查询
        List<MatchSettleEventEntity> goWaterEventList =matchSettleEventRepository.getByMatchIdAndEventCodeAndSettleNumAndHomeAway(settleScoreSearchDto.getStandardMatchId(),"goal","1054",null);
        if(goWaterEventList.size()!=0){
            MatchSettleEventDto matchSettleEventDto=new MatchSettleEventDto();
            BeanUtils.copyProperties(goWaterEventList.get(0),matchSettleEventDto);
            matchSettleEventDto.setId(goWaterEventList.get(0).getId().toString());
            matchSettleEventDto.setScoresPeriodFreeze(goWaterEventList.get(0).getSettleFreeze());
            penaltyScoresVo.setGoWaterPenaltyEvent(matchSettleEventDto);
        }else {
            MatchSettleEvent matchSettleScore13 = FootballPenaltySettleEventUtils.initPenaltySettleEvent(settleScoreSearchDto.getStandardMatchId());
            matchSettleScore13.setEventCode("goal");
            matchSettleScore13.setSettleNum("1054");
            matchSettleScore13.setPeriodId(120l);
            matchSettleScore13.setEventName("点球大战走水");
            matchSettleEventRepository.save(matchSettleScore13);
            MatchSettleEventDto matchSettleEventDto=new MatchSettleEventDto();
            BeanUtils.copyProperties(matchSettleScore13,matchSettleEventDto);
            matchSettleEventDto.setId(matchSettleScore13.getId().toString());
            penaltyScoresVo.setGoWaterPenaltyEvent(matchSettleEventDto);
        }
        //如果查询的时候第一轮都是第二个球，则改为第一个球
        MatchSettleEventDto homeFEvent =null;
        MatchSettleEventDto awayFEvent =null;
        for (MatchSettleEventDto matchSettleEventDto : penaltyScoresVo.getAwayEventList()) {
            if(matchSettleEventDto.getFirstNum()==1){
                awayFEvent=matchSettleEventDto;
            }
        }
        for (MatchSettleEventDto matchSettleEventDto : penaltyScoresVo.getHomeEventList()) {
            if(matchSettleEventDto.getFirstNum()==1){
                homeFEvent=matchSettleEventDto;
            }
        }
        if(awayFEvent!=null&&homeFEvent!=null){
            if(awayFEvent.getEventOrder()==2&&homeFEvent.getEventOrder()==2){
                homeFEvent.setEventOrder(1);
                awayFEvent.setEventOrder(1);
            }
        }
        matchSettleCheckInfoHelper.searchCheckStatusByPenalty(penaltyScoresVo,settleScoreSearchDto.getOperatorName());
        setRollBackStatusPenalty(penaltyScoresVo,settleScoreSearchDto.getStandardMatchId());
        return Response.success(penaltyScoresVo);
    }

    /**
     * 新增点球大赛比分
     * @param settleScoreSearchDto
     * @return
     */
    @Override
    public Response addPenaltyScores(MatchSettleScoreSearchDto settleScoreSearchDto) {
        log.info("addPenaltyScores param,settleScoreSearchDto: {}",settleScoreSearchDto);
        if(matchServiceHelper.checkIfOverSettleTime(settleScoreSearchDto.getStandardMatchId())){
            return Response.failed("1031930");
        }
        //每次新增都是新增一轮点球
        MatchSettleEventExample homeEventExample =new MatchSettleEventExample();
        homeEventExample.createCriteria().andStandardMatchIdEqualTo(settleScoreSearchDto.getStandardMatchId())
                .andEventCodeEqualTo("goal").andSettleNumEqualTo("1030");
        homeEventExample.setOrderByClause("event_order desc");
        List<MatchSettleEventEntity> homeEvent =matchSettleEventRepository.getByMatchIdAndEventCodeAndSettleNumAndHomeAway(settleScoreSearchDto.getStandardMatchId(),"goal","1030",null);
        Integer round=0;
        Integer point=0;
        String lastHomeAway="home";
        Integer t1 =0;
        Integer t2 =0;
        for (MatchSettleEventEntity matchSettleEvent : homeEvent) {
            if(matchSettleEvent.getFirstNum()>round){
                round=matchSettleEvent.getFirstNum();
            }
            if(matchSettleEvent.getEventOrder()>point){
                point=matchSettleEvent.getEventOrder();
                lastHomeAway=matchSettleEvent.getHomeAway();
                t1=matchSettleEvent.getT1();
                t2=matchSettleEvent.getT2();
            }
        }
        //计算最大轮数
        round++;point++;
        //计算最大轮数
        MatchSettleEvent initMatchSettleScoreT1 = FootballPenaltySettleEventUtils.initPenaltySettleEvent(settleScoreSearchDto.getStandardMatchId());
        initMatchSettleScoreT1.setFirstNum(round);
        initMatchSettleScoreT1.setEventCode("goal");
        initMatchSettleScoreT1.setSettleNum("1030");
        initMatchSettleScoreT1.setPeriodId(50l);
        initMatchSettleScoreT1.setT1(t1);
        initMatchSettleScoreT1.setT2(t2);
        initMatchSettleScoreT1.setHomeAway("home");
        MatchSettleEvent initMatchSettleScoreT2 = FootballPenaltySettleEventUtils.initPenaltySettleEvent(settleScoreSearchDto.getStandardMatchId());
        initMatchSettleScoreT2.setFirstNum(round);
        initMatchSettleScoreT2.setEventCode("goal");
        initMatchSettleScoreT2.setSettleNum("1030");
        initMatchSettleScoreT2.setHomeAway("away");
        initMatchSettleScoreT2.setT1(t1);
        initMatchSettleScoreT2.setT2(t2);
        initMatchSettleScoreT2.setPeriodId(50l);
        //判断球头
        if(lastHomeAway.equals("home")){
            initMatchSettleScoreT2.setEventOrder(point);
            point++;
            initMatchSettleScoreT1.setEventOrder(point);
        }else {
            initMatchSettleScoreT1.setEventOrder(point);
            point++;
            initMatchSettleScoreT2.setEventOrder(point);
        }
        matchSettleEventRepository.save(initMatchSettleScoreT1);
        matchSettleEventRepository.save(initMatchSettleScoreT2);
        //新增数据
        return Response.success();
    }

    /**
     * 设置点球大赛比分
     * @param settleScoreSearchDto
     * @return
     */
    @Override
    public Response setPenaltyScores(EditMatchSettleEventDto settleScoreSearchDto) {
        log.info("setPenaltyScores param,settleScoreSearchDto: {}",settleScoreSearchDto);
        String key ="MATCH_SETTLE_INFO:"+ settleScoreSearchDto.getEventId();
        if(matchServiceHelper.checkIfOverSettleTime(settleScoreSearchDto.getStandardMatchId())){
            return Response.failed("1031930");
        }
        try {
            if(redisService.tryLock(key,key,2,5)) {
                //设置进球
                MatchSettleEvent matchSettleEvent =matchSettleEventRepository.getById(settleScoreSearchDto.getEventId());
                MatchSettleEvent matchSettleEventBefore =new MatchSettleEvent();
                BeanUtils.copyProperties(matchSettleEvent,matchSettleEventBefore);
                //自动计算比分
                if(matchSettleEvent==null){
                    return Response.failed("1031935");
                }
                //1.判断谁先射门是否已经结算如果没结算则直接返回失败
                if(!isTeamFirstSettled(settleScoreSearchDto.getStandardMatchId())){
                    return Response.failed("1031952");
                }
                MatchSettleEvent oidMatchSettleEventLog = new MatchSettleEvent();
                BeanUtils.copyProperties(matchSettleEvent,oidMatchSettleEventLog);
                matchSettleEvent.setStatus(1);
                if(!matchSettleEvent.getSettleNum().equals("1030")){
                    matchSettleEvent.setT1(settleScoreSearchDto.getT1());
                    matchSettleEvent.setT2(settleScoreSearchDto.getT2());
                    matchSettleEvent.setModifyTime(System.currentTimeMillis());
                    matchSettleEvent.setOperater(settleScoreSearchDto.getOperatorName());
                    if(settleScoreSearchDto.getGoWaterStatus()!=null&&settleScoreSearchDto.getGoWaterStatus()==1){
                        matchSettleEvent.setGoWaterStatus(1);
                    }else {
                        matchSettleEvent.setGoWaterStatus(0);
                    }
//                    if(MatchEventInfoSettleUtils.equileMatchSettleEvent(matchSettleEventBefore,matchSettleEvent)){
//                        return Response.failed("1031940");
//                    }
                    //如果是1028 则编辑走水 1054
                    if(matchSettleEvent.getSettleNum().equals("1028")){
                        updateGoWaterPenaltyScores(settleScoreSearchDto);
                    }
                    matchSettleEventRepository.updateById(matchSettleEvent);
                }else {
                    boolean isCanCount= countPenaltyScores(settleScoreSearchDto,matchSettleEvent);
                    if(!isCanCount){
                        return Response.failed("1031937");
                    }
                    matchSettleEvent.setExtryInfo(settleScoreSearchDto.getExtryInfo());
                    matchSettleEvent.setModifyTime(System.currentTimeMillis());
                    matchSettleEvent.setOperater(settleScoreSearchDto.getOperatorName());
                    if(settleScoreSearchDto.getGoWaterStatus()!=null&&settleScoreSearchDto.getGoWaterStatus()==1){
                        matchSettleEvent.setGoWaterStatus(1);
                    }else {
                        matchSettleEvent.setGoWaterStatus(0);
                    }
//                    if(MatchEventInfoSettleUtils.equileMatchSettleEvent(matchSettleEventBefore,matchSettleEvent)){
//                        return Response.failed("1031940");
//                    }
                    matchSettleEventRepository.updateById(matchSettleEvent);
                }
                matchSettleOperateLogService.matchSettleEventAddLog(oidMatchSettleEventLog,matchSettleEvent,
                        settleScoreSearchDto.getOperatorName(),OperateLogTypeEnum.EDIT,settleScoreSearchDto.getIpAddress());
                return Response.success();
            }else {
                return Response.failed("1031933");
            }
        }catch (Exception e){
            log.error("IFootballMatchScoresSettleApiImpl-setPenaltyScores:",e);
            return Response.failed();
        }finally {
            redisService.unLock(key,key);
        }
    }

    /**
     * 审核员回滚次序比分
     * @param matchSettleScoreDto
     * @return
     */
    @Override
    public Response reSettleMatchEvent(EditMatchSettleEventDto matchSettleScoreDto) {
        log.info("reSettleMatchEvent param,matchSettleScoreDto: {}",matchSettleScoreDto);
        String key ="MATCH_SETTLE_INFO:"+ matchSettleScoreDto.getEventId();
        try {
            if(redisService.tryLock(key,key,2,5)) {
                MatchSettleEvent matchSettleEvent = matchSettleEventRepository.getById(matchSettleScoreDto.getEventId());
                if(matchSettleEvent==null){
                    return Response.failed("1031935");
                }
                Integer settleTimes =matchSettleEvent.getSettleTimes();
                if(settleTimes!=null&&settleTimes>0){
                }else {
                    return Response.failed("1031938");
                }
                matchSettleEvent.setModifyTime(System.currentTimeMillis());
                matchSettleEvent.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.RE_SETTLE);
                matchSettleEvent.setSettleTimes(settleTimes);
                matchSettleEvent.setOperater(matchSettleScoreDto.getOperatorName());
                matchSettleEvent.setUserid(matchSettleScoreDto.getOperatorId());
                matchSettleEvent.setIsGrey(0);
                matchSettleEvent.setHasDeleteEvent(0);
                matchSettleEvent.setCurrentEventStatus(0);
                matchSettleEventRepository.updateById(matchSettleEvent);
                //结算时把回滚订单数清零
                matchServiceHelper.settleRollBackSetNullOrderCount(matchSettleEvent.getId());
                //1.日志
                matchSettleOperateLogService.matchSettleEventAddLog(matchSettleEvent,matchSettleScoreDto.getOperatorName(),
                        OperateLogTypeEnum.ROLLBACK_EXECUTE.getCode().toString(),"",matchSettleScoreDto.getIpAddress());

                //2.MQ下发
                MatchSettleEventMessage event = new MatchSettleEventMessage();
                BeanUtils.copyProperties(matchSettleEvent,event);
                event.setLevel(3);
                matchSettleScoresProducer.sendMatchSettleEvent(event);
                matchSettleInfoHelper.updateMatchCurrentEventStatus(matchSettleEvent.getStandardMatchId());
                return Response.success();
            }else {
                return Response.failed("1031933");
            }
        }catch (Exception e){
            log.error("IFootballMatchScoresSettleApiImpl-reSettleMatchEvent:",e);
            return Response.failed();
        }finally {
            redisService.unLock(key,key);
        }
    }

    /**
     * 审核员回滚阶段比分
     * @param matchSettleScoreDto
     * @return
     */
    @Override
    public Response reSettleMatchScore(UpdateMatchSettleScoreDto matchSettleScoreDto) {
        log.info("reSettleMatchScore param,matchSettleScoreDto: {}",matchSettleScoreDto);
        String key ="MATCH_SETTLE_INFO:"+ matchSettleScoreDto.getMatchScoreId();
        try {
            if(redisService.tryLock(key,key,2,5)) {
                MatchSettleScore matchSettleScore = matchSettleScoreRepository.getById(matchSettleScoreDto.getMatchScoreId());
                if(matchSettleScore==null){
                    return Response.failed("1031935");
                }
                Integer settleTimes =matchSettleScore.getSettleTimes();
                if(settleTimes!=null&&settleTimes>0){
                }else {
                    return Response.failed("1031938");
                }
                matchSettleScore.setModifyTime(System.currentTimeMillis());
                matchSettleScore.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.RE_SETTLE);
                matchSettleScore.setSettleTimes(settleTimes);
                matchSettleScore.setOperater(matchSettleScoreDto.getOperatorName());
                matchSettleScore.setUserid(matchSettleScoreDto.getOperatorId());
                matchSettleScore.setIsGrey(0);
                matchSettleScore.setHasDeleteEvent(0);
                matchSettleScore.setCurrentEventStatus(0);
                matchSettleScoreRepository.updateById(matchSettleScore);
                matchSettleInfoHelper.updateMatchCurrentEventStatus(matchSettleScore.getStandardMatchId());
                //2.MQ下发
                MatchSettleScoreMessage Score = new MatchSettleScoreMessage();
                BeanUtils.copyProperties(matchSettleScore,Score);
                Score.setLevel(3);
                matchSettleScoresProducer.sendMatchSettleScores(Score);


                //1.比分结算增加操作日志
                //走水设置编码为8
                if (matchSettleScore.getGoWaterStatus()!=null&&matchSettleScore.getGoWaterStatus().equals(1))  matchSettleScore.setExtryInfo("8");
                matchSettleOperateLogService.matchSettleScoreAddLog(matchSettleScore,matchSettleScoreDto.getOperatorName(),
                        OperateLogTypeEnum.ROLLBACK_EXECUTE,"",matchSettleScoreDto.getIpAddress());

                return Response.success();
            }else {
                return Response.failed("1031933");
            }
        }catch (Exception e){
            log.error("IFootballMatchScoresSettleApiImpl-reSettleMatchScore:",e);
            return Response.failed();
        }finally {
            redisService.unLock(key,key);
        }
    }

    /**
     * 审核员回滚阶段比分
     * @param matchSettleScoreDto
     * @return
     */
    @Override
    public Response rollBackSettleMatchScores(UpdateMatchSettleScoreDto matchSettleScoreDto) {
        log.info("rollBackSettleMatchScores param,matchSettleScoreDto: {}",matchSettleScoreDto);
        String key ="MATCH_SETTLE_INFO:"+ matchSettleScoreDto.getMatchScoreId();
        if(matchServiceHelper.checkIfOverSettleTime(matchSettleScoreDto.getStandardMatchId())){
            return Response.failed("1031930");
        }
        try {
            MatchSettleScore matchSettleScore = matchSettleScoreRepository.getById(matchSettleScoreDto.getMatchScoreId());
            if(matchSettleScore==null){
                return Response.failed("1031935");
            }
            MatchSettleScore oIdMatchSettleScore = new   MatchSettleScore();
            BeanUtils.copyProperties(matchSettleScore,oIdMatchSettleScore);
            matchSettleScore.setGoWaterStatus(0);
            matchSettleScore.setStatus(NOT_EDIT);
            matchSettleScore.setT1(null);
            matchSettleScore.setT2(null);
            matchSettleScore.setExtryInfo(null);
            matchSettleScore.setFirstT1(null);
            matchSettleScore.setFirstT2(null);
            matchSettleScore.setSecondT1(null);
            matchSettleScore.setSecondT2(null);
            matchSettleScore.setSettleTimes(0);
            matchSettleScore.setModifyTime(System.currentTimeMillis());
            matchSettleScore.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.ROLL_BACK);
            matchSettleScore.setOperater(matchSettleScoreDto.getOperatorName());
            matchSettleScore.setUserid(matchSettleScoreDto.getOperatorId());
            matchSettleScore.setSettleReasonDetail(null);
            matchSettleScore.setSettleReason(null);
            matchSettleScoreRepository.updateById(matchSettleScore);
            //将核对信息进行无效处理
            matchSettleCheckInfoHelper.rollbackScores(matchSettleScore);
            //2.MQ下发
            matchSettleScoresProducer.sendMatchSettleScores(matchSettleScore);
            wsPushService.pushStandardSettleScores(matchSettleScoreDto.getStandardMatchId(),
                    matchSettleScoreDto.getEventCode());
            MatchSettleScoreEntity old = new MatchSettleScoreEntity();
            BeanUtils.copyProperties(oIdMatchSettleScore,old);
            //1.记录日志
            matchSettleOperateLogService.matchSettleScoreAddLog(old,matchSettleScore,matchSettleScoreDto.getOperatorName(),
                    OperateLogTypeEnum.ROLLBACK_SCORES_SETTLE.getCode().toString(),matchSettleScoreDto.getLinkedId(),matchSettleScoreDto.getIpAddress());
            //回滚新增记录
            insertRollbackData(matchSettleScoreDto.getStandardMatchId(),matchSettleScoreDto.getMatchScoreId(),1,matchSettleScoreDto.getEventCode(),matchSettleScore.getSettleNum());
            //回滚保存赛事ID一分钟
            redisService.set("SETTLE_ROLLBACK_MATCH_ID_"+matchSettleScoreDto.getMatchScoreId(),System.currentTimeMillis(),60);
            log.info("添加SETTLE_ROLLBACK_MATCH_ID_"+matchSettleScoreDto.getMatchScoreId()+redisService.get("SETTLE_ROLLBACK_MATCH_ID_"+matchSettleScoreDto.getMatchScoreId()));
            return Response.success();
        }catch (Exception e){
            log.error("IFootballMatchScoresSettleApiImpl-rollBackSettleMatchScores:",e);
            return Response.failed();
        }finally {
            redisService.unLock(key,key);
        }
    }

    /**
     * 审核员回滚次序比分
     * @param matchSettleScoreDto
     * @return
     */
    @Override
    public Response rollBackSettleMatchEvent(EditMatchSettleEventDto matchSettleScoreDto) {
        log.info("rollBackSettleMatchEvent param,matchSettleScoreDto:{}",matchSettleScoreDto);
        String key ="MATCH_SETTLE_INFO:"+ matchSettleScoreDto.getEventId();
        if(matchServiceHelper.checkIfOverSettleTime(matchSettleScoreDto.getStandardMatchId())){
            return Response.failed("1031930");
        }
        try {
            /* if(redisService.tryLock(key,key,2,5)) {*/
            MatchSettleEvent matchSettleScore = matchSettleEventRepository.getById(matchSettleScoreDto.getEventId());
            if(matchSettleScore==null){
                return Response.failed("1031935");
            }
            MatchSettleEvent matchSettleEvent = new MatchSettleEvent();
            BeanUtils.copyProperties(matchSettleScore,matchSettleEvent);
            matchSettleScore.setStatus(NOT_EDIT);
            matchSettleScore.setGoWaterStatus(0);
            //进球方式和球员玩法无需回滚比分
            if(matchSettleEvent.getEventType()!=2){
                matchSettleScore.setT1(null);
                matchSettleScore.setT2(null);
                matchSettleScore.setFirstT1(null);
                matchSettleScore.setFirstT2(null);
                matchSettleScore.setSecondT1(null);
                matchSettleScore.setSecondT2(null);
                if(!matchSettleScore.getSettleNum().equals("1030")){
                    matchSettleScore.setHomeAway("none");
                }
                if(matchSettleScore.getEventCode().equals("3019")||matchSettleScore.getEventCode().equals("3020")||matchSettleScore.getEventCode().equals("3021")||
                        matchSettleScore.getEventCode().equals("3022")||matchSettleScore.getEventCode().equals("3023")||matchSettleScore.getEventCode().equals("3024")){
                    matchSettleScore.setEventCode("fa_card");
                    matchSettleScore.setHomeAway("none");
                }
            }
            matchSettleScore.setExtryInfo(null);
            matchSettleScore.setSettleTimes(0);
            matchSettleScore.setPlayerNameCode(null);
            matchSettleScore.setModifyTime(System.currentTimeMillis());
            matchSettleScore.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.ROLL_BACK);
            matchSettleScore.setOperater(matchSettleScoreDto.getOperatorName());
            matchSettleScore.setUserid(matchSettleScoreDto.getOperatorId());
            matchSettleScore.setSettleReasonDetail(null);
            matchSettleScore.setFiveMinSection(null);
            matchSettleScore.setFifteenMinSection(null);
            matchSettleScore.setSettleReason(null);
            matchSettleEventRepository.updateById(matchSettleScore);
            log.info("事件Id::{}:: 当前事件被回滚,回滚后参数:{} ",matchSettleScoreDto.getEventId(),matchSettleEvent);

            //将核对信息进行无效处理
            matchSettleCheckInfoHelper.rollbackEvent(matchSettleScore);
            //1.日志
            matchSettleOperateLogService.matchSettleEventAddLog(matchSettleEvent,matchSettleScore,matchSettleScoreDto.getOperatorName(),
                    OperateLogTypeEnum.ROLLBACK_SCORES_SETTLE,matchSettleScoreDto.getIpAddress());
            //2.MQ下发
            matchSettleScoresProducer.sendMatchSettleEvent(matchSettleScore);
            wsPushService.pushStandardSettleEvent(matchSettleScoreDto.getStandardMatchId(),
                    matchSettleScoreDto.getEventCode());
            //回滚新增记录
            insertRollbackData(matchSettleScoreDto.getStandardMatchId(),matchSettleScoreDto.getEventId(),2,matchSettleScoreDto.getEventCode(),matchSettleScore.getSettleNum());
            //回滚保存赛事ID一分钟
            redisService.set("SETTLE_ROLLBACK_MATCH_ID_"+matchSettleScoreDto.getEventId(),matchSettleScoreDto.getEventId(),60);
            return Response.success();
        }catch (Exception e){
            log.error("IFootballMatchScoresSettleApiImpl-rollBackSettleMatchEvent:",e);
            return Response.failed(e.getMessage());
        }finally {
            redisService.unLock(key,key);
        }
    }

    /**
     * 查询赛事阶段比分
     * @param matchPeriodQueryDto
     * @return
     */
    @Override
    public Response matchPeriodQuery(MatchPeriodQueryDto matchPeriodQueryDto) {
        List<String> num = Arrays.asList("1011", "1012", "1013", "1014", "1015", "1016", "1017", "1018", "1019");
        List<String> eventNum = Arrays.asList("201", "202", "203", "206", "207", "208");

        List<String> settleNum = null;
        //常规10001, 角球10002, 加时10003, 点球大战10004, 罚牌10005
        if (matchPeriodQueryDto.getCategorySetId().longValue() == 10001L) {
            //settleNum=   Arrays.asList("101","102","103","104","105","106","107", "108","109","1010");
            MatchSettleScoreSearchDto settleScoreSearchDto =new  MatchSettleScoreSearchDto();
            settleScoreSearchDto.setStandardMatchId(matchPeriodQueryDto.getStandardMatchId());
            settleScoreSearchDto.setEventCode("goal");
            settleScoreSearchDto.setOperatorName(matchPeriodQueryDto.getOperatorName());
            List<MatchSettleScoreDto> matchSettleScoreDtos = matchSettleScoreAssemble.searchFootballMatchSettleScores(settleScoreSearchDto);

            List<MatchSettleEventDto> matchSettleEventDtos = matchSettleEventAssemble.searchFootballMatchSettleEvent(settleScoreSearchDto);
            HashMap<String, Object> map = new HashMap<>();
            if (matchSettleScoreDtos.size() != 0) {
                matchSettleScoreDtos.removeIf(exe -> num.contains(exe.getSettleNum().toString()));
                map.put("score",matchSettleScoreDtos);
                //
                matchSettleEventDtos.removeIf(exe -> eventNum.contains(exe.getSettleNum()));
                map.put("event",matchSettleEventDtos);
            }
            //结算2.0 优化查询审核状态
            matchSettleCheckInfoHelper.searchCheckStatusByScoresList(matchSettleScoreDtos,matchPeriodQueryDto.getOperatorName());
            return Response.success(map);


        }
        //角球10002L
        if (matchPeriodQueryDto.getCategorySetId().longValue() == 10002L) {
            MatchSettleScoreSearchDto settleScoreSearchDto =new  MatchSettleScoreSearchDto();
            settleScoreSearchDto.setStandardMatchId(matchPeriodQueryDto.getStandardMatchId());
            settleScoreSearchDto.setEventCode("corner");
            settleScoreSearchDto.setOperatorName(matchPeriodQueryDto.getOperatorName());
            List<MatchSettleScoreDto> matchSettleScoreDtos = matchSettleScoreAssemble.searchFootballMatchSettleScores(settleScoreSearchDto);

            List<MatchSettleEventDto> matchSettleEventDtos = matchSettleEventAssemble.searchFootballMatchSettleEvent(settleScoreSearchDto);
            List<MatchSettleEventDto> eventDtos  =  new ArrayList<>();
            if (matchSettleEventDtos.size() != 0 ) {
                for (MatchSettleEventDto dto:matchSettleEventDtos) {
                    eventDtos.add(dto);
                }
            }

            HashMap<String, Object> map = new HashMap<>();
            if (matchSettleScoreDtos.size() != 0 && eventDtos.size() != 0) {
                map.put("score",matchSettleScoreDtos);
                map.put("event",eventDtos);
            }
            matchSettleCheckInfoHelper.searchCheckStatusByScoresList(matchSettleScoreDtos,matchPeriodQueryDto.getOperatorName());
            return Response.success(map);

        }
        //加时赛事10003L
        if (matchPeriodQueryDto.getCategorySetId().longValue() == 10003L ) {
            settleNum= num;
            MatchSettleScoreExample example =new MatchSettleScoreExample();
            example.createCriteria().andStandardMatchIdEqualTo(matchPeriodQueryDto.getStandardMatchId())
                    .andSettleNumIn(settleNum);
            example.setOrderByClause("settle_num desc");
            List<MatchSettleScore> list =matchSettleScoreRepository.getModelBySettleNumAndMatchIdIdAndStatus(settleNum,matchPeriodQueryDto.getStandardMatchId(),null);
            List<MatchSettleScoreDto> matchSettleScoreDtos=new ArrayList<>();
            for (MatchSettleScore matchSettleScore : list) {
                MatchSettleScoreDto matchSettleScoreDto =new MatchSettleScoreDto();
                BeanUtils.copyProperties(matchSettleScore,matchSettleScoreDto);
                matchSettleScoreDto.setSettleNum(matchSettleScore.getSettleNum());
                matchSettleScoreDto.setId(matchSettleScore.getId().toString());
                matchSettleScoreDto.setScoresPeriodFreeze(matchSettleScore.getSettleFreeze());
                //查询阶段比分的时候要过滤角球 阶段比分 界面查询展示效果变更
                matchSettleScoreDtos.add(matchSettleScoreDto);
            }

            MatchSettleScoreSearchDto settleScoreSearchDto =new  MatchSettleScoreSearchDto();
            settleScoreSearchDto.setStandardMatchId(matchPeriodQueryDto.getStandardMatchId());
            settleScoreSearchDto.setEventCode("goal");
            List<MatchSettleEventDto> matchSettleEventDtos = matchSettleEventAssemble.searchFootballMatchSettleEvent(settleScoreSearchDto);

            HashMap<String, Object> map = new HashMap<>();
            if (matchSettleScoreDtos.size() != 0 && matchSettleEventDtos.size() != 0) {

                map.put("score",matchSettleScoreDtos);
                matchSettleEventDtos.removeIf(exe -> !eventNum.contains(exe.getSettleNum()));
                map.put("event",matchSettleEventDtos);
            }
            matchSettleCheckInfoHelper.searchCheckStatusByScoresList(matchSettleScoreDtos,matchPeriodQueryDto.getOperatorName());
            return Response.success(map);

        }
        //点球大战10004
        if (matchPeriodQueryDto.getCategorySetId().longValue() == 10004L ) {
            MatchSettleScoreSearchDto settleScoreSearchDto =new  MatchSettleScoreSearchDto();
            settleScoreSearchDto.setStandardMatchId(matchPeriodQueryDto.getStandardMatchId());
            return   searchPenaltyScores(settleScoreSearchDto);
        }
        //罚牌10005
        if (matchPeriodQueryDto.getCategorySetId().longValue() == 10005L) {
            MatchSettleScoreSearchDto settleScoreSearchDto =new  MatchSettleScoreSearchDto();
            settleScoreSearchDto.setStandardMatchId(matchPeriodQueryDto.getStandardMatchId());
            settleScoreSearchDto.setEventCode("fa_card");
            settleScoreSearchDto.setOperatorName(matchPeriodQueryDto.getOperatorName());
            List<MatchSettleScoreDto> matchSettleScoreDtos = matchSettleScoreAssemble.searchFootballMatchSettleScores(settleScoreSearchDto);
            List<MatchSettleEventDto> matchSettleEventDtos = matchSettleEventAssemble.searchFootballMatchSettleEvent(settleScoreSearchDto);
            HashMap<String, Object> map = new HashMap<>();
            if (matchSettleScoreDtos.size() != 0) {
                map.put("score",matchSettleScoreDtos);
                map.put("event",matchSettleEventDtos);
            }
            matchSettleCheckInfoHelper.searchCheckStatusByScoresList(matchSettleScoreDtos,matchPeriodQueryDto.getOperatorName());
            return Response.success(map);

        }
        return Response.success();
    }

    /**
     * 校验阶段次序比分
     * @param dto
     * @return
     */
    @Override
    public Response checkScoresOrEvent(MatchCheckSettleScoreEventDto dto) {
        try {
            //1.先查询得到对应结果
            MatchSettleScore matchSettleScore = matchSettleScoreRepository.getById(dto.getId());
            //2.进行核对
            if (matchSettleScore != null) {
                return checkMatchSettleScores(matchSettleScore, dto);
            }

            MatchSettleEvent matchSettleEvent = matchSettleEventRepository.getById(dto.getId());
            if (matchSettleEvent != null) {
                return checkMatchSettleEvent(matchSettleEvent, dto);
            }
            return Response.failed("比分不存在:" + dto.getId());
        }catch (Exception e){
            e.printStackTrace();
            log.error("IFootballMatchScoresSettleApiImpl-checkScoresOrEvent:",e);
        }
        return Response.failed("比分异常");
    }

    /**
     * 郊野次序比分
     * @param matchSettleEvent
     * @param dto
     * @return
     */
    private Response checkMatchSettleEvent(MatchSettleEvent matchSettleEvent, MatchCheckSettleScoreEventDto dto) {
        MatchSettleCheckResultDto resultDto =new MatchSettleCheckResultDto();
        resultDto.setId(matchSettleEvent.getId());
        resultDto.setSettleNum(matchSettleEvent.getSettleNum());
        resultDto.setCheckResult(0);
        //检查比分
        if(matchSettleEvent.getEventType()==1){
            MatchSettleCheckEventDto before = new MatchSettleCheckEventDto();
            MatchSettleCheckEventDto after = new MatchSettleCheckEventDto();
            BeanUtils.copyProperties(matchSettleEvent,before);
            BeanUtils.copyProperties(dto,after);
            String jsonBefore = JSONObject.toJSONString(before);
            String jsonAfter = JSONObject.toJSONString(after);
            if(jsonBefore.equals(jsonAfter)){
                resultDto.setCheckResult(1);
            }
        }else {
            //检查 球员和进球方式
            MatchSettleCheckExtryEventDto before = new MatchSettleCheckExtryEventDto();
            MatchSettleCheckExtryEventDto after = new MatchSettleCheckExtryEventDto();
            BeanUtils.copyProperties(matchSettleEvent,before);
            BeanUtils.copyProperties(dto,after);
            String jsonBefore = JSONObject.toJSONString(before);
            String jsonAfter = JSONObject.toJSONString(after);
            if(jsonBefore.equals(jsonAfter)){
                resultDto.setCheckResult(1);
            }
        }
        return Response.success(resultDto);
    }

    /**
     * 校验阶段比分
     * @param matchSettleScore
     * @param dto
     * @return
     */
    private Response checkMatchSettleScores(MatchSettleScore matchSettleScore, MatchCheckSettleScoreEventDto dto) {
        MatchSettleCheckResultDto resultDto =new MatchSettleCheckResultDto();
        resultDto.setId(matchSettleScore.getId());
        resultDto.setSettleNum(matchSettleScore.getSettleNum());
        resultDto.setCheckResult(0);
        if(dto.getT1()!=null&&dto.getT2()!=null&&dto.getT1()==matchSettleScore.getT1()&&dto.getT2()==matchSettleScore.getT2()){
            resultDto.setCheckResult(1);
        }else if(matchSettleScore.getT1()==null&&matchSettleScore.getT2()==null){
            if(dto.getT1()==null&&dto.getT2()==null){
                if(dto.getExtryInfo()!=null&& dto.getExtryInfo().equals(matchSettleScore.getExtryInfo())){
                    resultDto.setCheckResult(1);
                }
            }
        }
        return Response.success(resultDto);
    }
    private void insertRollbackData(Long standardMatchId,Long scoreEventId,Integer type,String eventCode,String settleNum){
        log.info("insertRollbackData param,standardMatchId: {},scoreEventId: {},type: {},eventCode: {},settleNum: {}",standardMatchId,scoreEventId,type,eventCode,settleNum);
        MatchSettleRollBackInfoEntity oldInfo = matchSettleRollBackInfoRepository.getMatchSettleRollBackInfo(scoreEventId);
        Integer isPenalty =0;
        if(settleNum.equals("1030")||settleNum.equals("1029")||settleNum.equals("1028")){
            isPenalty=1;
        }
        //多次回滚，存在更新，不存在新增
        if(oldInfo != null){
            oldInfo.setRollBackStatus(1);
            oldInfo.setRollBackOrderCount(0l);
            oldInfo.setOrderCount(0l);
            oldInfo.setRollBackTime(System.currentTimeMillis());
            matchSettleRollBackInfoRepository.updateMatchSettleRollBackInfoToRedis(oldInfo,false);
        } else {
            MatchSettleRollBackInfoEntity info = new MatchSettleRollBackInfoEntity();
            info.setId(scoreEventId);
            info.setSettleScoreEventId(scoreEventId);
            info.setDataType(type);
            info.setRollBackStatus(1);
            info.setRollBackTime(System.currentTimeMillis());
            info.setStandardMatchId(standardMatchId);
            info.setCreateTime(System.currentTimeMillis());
            info.setModifyTime(System.currentTimeMillis());
            info.setEventCode(eventCode);
            info.setIsDianQiu(isPenalty);
            matchSettleRollBackInfoRepository.updateMatchSettleRollBackInfoToRedis(info,true);
        }
    }
    public boolean countPenaltyScores(EditMatchSettleEventDto matchSettleEvent, MatchSettleEvent settleEvent) {
        List<MatchSettleEventEntity> homeEvent =matchSettleEventRepository.getByMatchIdAndEventCodeAndSettleNumAndEventOrderLeAndNotId(matchSettleEvent.getStandardMatchId(),"goal","1030",settleEvent.getEventOrder(),settleEvent.getId());
        Integer t1 =0,t2=0;
        for (MatchSettleEventEntity event : homeEvent) {
            //可能 修正 eventOder后会有 5-5 的情况,所以和自己相同的排序的事件要被过滤
            if(event.getEventOrder()==settleEvent.getEventOrder()){
                continue;
            }
            //1.如果有个没有编辑则返回失败
            if(event.getT1()==null||event.getT2()==null||event.getStatus()==NOT_EDIT){
                return false;
            }
            if("1".equals(event.getExtryInfo())){
                if(event.getHomeAway().equals("home")){
                    t1++;
                }else {
                    t2++;
                }
            }
        }
        if("1".equals(matchSettleEvent.getExtryInfo())){
            if(settleEvent.getHomeAway().equals("home")){
                t1++;
            }else {
                t2++;
            }
        }
        settleEvent.setT1(t1);
        settleEvent.setT2(t2);
        return true;
    }

    /**
     * 更新点球大战比分
     * @param settleScoreSearchDto
     */
    public void updateGoWaterPenaltyScores(EditMatchSettleEventDto settleScoreSearchDto) {
        //查询
        List<MatchSettleEvent> list =matchSettleEventRepository.getModelByStandardMatchIdAndSettleNums(settleScoreSearchDto.getStandardMatchId(), Arrays.asList("1054"));
        if(list.size()==0){
            return;
        }
        MatchSettleEvent matchSettleEvent= list.get(0);
        //结算后不做编辑
        if(matchSettleEvent.getStatus()!=null&&matchSettleEvent.getStatus()==3){
            return;
        }
        matchSettleEvent.setStatus(1);
        matchSettleEvent.setT1(settleScoreSearchDto.getT1());
        matchSettleEvent.setT2(settleScoreSearchDto.getT2());
        matchSettleEvent.setModifyTime(System.currentTimeMillis());
        matchSettleEvent.setOperater(settleScoreSearchDto.getOperatorName());
        matchSettleEventRepository.updateById(matchSettleEvent);
    }
    public boolean isTeamFirstSettled(Long standardMatchId) {
        List<MatchSettleEventEntity> list =matchSettleEventRepository.getByMatchIdAndEventCodeAndSettleNumAndHomeAway(standardMatchId,null,"-1030",null);
        if(list.size()==0){
            return false;
        }else {
            if(list.get(0).getStatus()==SETTLED){
                return true;
            }
        }
        return false;
    }
    private void setRollBackStatusPenalty(PenaltyScoresVo penaltyScoresVo,Long stndardMatchId){
        List<MatchSettleRollBackInfoEntity> list =matchSettleRollBackInfoRepository.getByMatchId(stndardMatchId);
        Map<String,MatchSettleRollBackInfoEntity> map= new HashMap<>();
        for (MatchSettleRollBackInfoEntity info : list) {
            map.put(info.getId().toString(),info);
        }
        List<MatchSettleEventDto> homeEventList = penaltyScoresVo.getHomeEventList();
        List<MatchSettleEventDto> awayEventList = penaltyScoresVo.getAwayEventList();
        MatchSettleEventDto  homeAway5RoundEvent = penaltyScoresVo.getHomeAway5RoundEvent();
        MatchSettleEventDto  homeAwayAllRoundEvent = penaltyScoresVo.getHomeAwayAllRoundEvent();
        for (MatchSettleEventDto matchSettleEventDto : homeEventList) {
            MatchSettleRollBackInfoEntity info= map.get(matchSettleEventDto.getId());
            if(info!=null){
                matchSettleEventDto.setRollBackStatus(info.getRollBackStatus());
                matchSettleEventDto.setRollBackOrderCount(info.getRollBackOrderCount());
            }
        }
        for (MatchSettleEventDto matchSettleEventDto : awayEventList) {
            MatchSettleRollBackInfoEntity info= map.get(matchSettleEventDto.getId());
            if(info!=null){
                matchSettleEventDto.setRollBackStatus(info.getRollBackStatus());
                matchSettleEventDto.setRollBackOrderCount(info.getRollBackOrderCount());
            }
        }
        MatchSettleRollBackInfoEntity info5= map.get(homeAway5RoundEvent.getId());
        if(info5!=null){
            homeAway5RoundEvent.setRollBackStatus(info5.getRollBackStatus());
            homeAway5RoundEvent.setRollBackOrderCount(info5.getRollBackOrderCount());
        }
        MatchSettleRollBackInfoEntity infoAll= map.get(homeAwayAllRoundEvent.getId());
        if(infoAll!=null){
            homeAwayAllRoundEvent.setRollBackStatus(infoAll.getRollBackStatus());
            homeAwayAllRoundEvent.setRollBackOrderCount(infoAll.getRollBackOrderCount());
        }

    }
    private void addTeamFirstEvent(PenaltyScoresVo penaltyScoresVo, Long standardMatchId, List<MatchSettleEventDto> homeEventList, List<MatchSettleEventDto> awayEventList) {
        //先判断主客队
        String firstTeam="none";
        Integer status=0;
        for (MatchSettleEventDto matchSettleEventDto : homeEventList) {
            if(matchSettleEventDto.getStatus()==3&&matchSettleEventDto.getEventOrder()==1){
                firstTeam="home";
                status=3;
            }
        }
        for (MatchSettleEventDto matchSettleEventDto : awayEventList) {
            if(matchSettleEventDto.getStatus()==3&&matchSettleEventDto.getEventOrder()==1){
                firstTeam="away";
                status=3;
            }
        }
        MatchSettleEvent matchSettleEvent =FootballPenaltySettleEventUtils.initPenaltySettleEvent(standardMatchId);
        matchSettleEvent.setStatus(status);
        matchSettleEvent.setHomeAway(firstTeam);
        matchSettleEvent.setEventCode("goal");
        matchSettleEvent.setSettleNum("-1030");
        matchSettleEvent.setPeriodId(50l);
        matchSettleEvent.setFirstNum(0);
        matchSettleEvent.setEventOrder(0);
        //通过赛事ID 做数据并发冗余 防止重复生成
        matchSettleEvent.setId(standardMatchId);
        matchSettleEventRepository.save(matchSettleEvent);

        MatchSettleEvent event= matchSettleEvent;
        MatchSettleEventDto matchSettleEventDto =new MatchSettleEventDto();
        BeanUtils.copyProperties(event,matchSettleEventDto);
        matchSettleEventDto.setId(event.getId().toString());
        penaltyScoresVo.setTeamFirst(matchSettleEventDto);
    }
    String faCardProcessRest(String eventCode,String homeAway,Integer status){
        if (FaCardEnum.Method_5.getMsg().equals(homeAway) ) {
            if (status == 0) { return "-"; }
            else { return FaCardEnum.Method_5.getCode().toString();}
        }
        if ("home".equals(homeAway) && "yellow_card".equals(eventCode))  return FaCardEnum.Method_1.getCode().toString();
        if ("away".equals(homeAway) && "yellow_card".equals(eventCode))  return FaCardEnum.Method_2.getCode().toString();
        if ("home".equals(homeAway) && "red_card".equals(eventCode))  return FaCardEnum.Method_3.getCode().toString();
        if ("away".equals(homeAway) && "red_card".equals(eventCode))  return FaCardEnum.Method_4.getCode().toString();
        return homeAway;
    }
    String goalProcessRest(String homeAway,Integer status){
        if ("no goal".equals(homeAway) || "none".equals(homeAway)) {
            if (status == 0) {
                return "-";
            } else { return OperateLogTypeEnum.type_6.getCode().toString();}
        }
        if ("home".equals(homeAway) )  return OperateLogTypeEnum.SCORES_SETTLE_10027.getCode().toString();
        if ("away".equals(homeAway) )  return OperateLogTypeEnum.SCORES_SETTLE_10028.getCode().toString();

        return homeAway;
    }
    public void updateFaCardEventByInfo(MatchSettleEvent matchSettleEvent, String homeAway) {
        List<String> eventCodes =new ArrayList<>();eventCodes.add("yellow_card");
        eventCodes.add("red_card");eventCodes.add("fa_card");
        //罚牌类型   1  主队黄牌   2.客队黄牌  3.主队红牌  4.客队红牌  5.没有罚牌  null -
        if(homeAway.equals("1")){
            matchSettleEvent.setEventCode("yellow_card");
            matchSettleEvent.setHomeAway("home");
        }else if(homeAway.equals("2")){
            matchSettleEvent.setEventCode("yellow_card");
            matchSettleEvent.setHomeAway("away");
        }else if(homeAway.equals("3")){
            matchSettleEvent.setEventCode("red_card");
            matchSettleEvent.setHomeAway("home");
        }else if(homeAway.equals("4")){
            matchSettleEvent.setEventCode("red_card");
            matchSettleEvent.setHomeAway("away");
        }else if(homeAway.equals("5")){
            matchSettleEvent.setEventCode("fa_card");
            matchSettleEvent.setHomeAway("none");
        }else {
            matchSettleEvent.setHomeAway("none");
        }
        List<String> settleNumList=new ArrayList<>();
        List< MatchSettleEventEntity> list = new ArrayList<>();
        if (matchSettleEvent.getEventType() == 3) {
            if(matchSettleEvent.getSettleNum().equals("30195")||matchSettleEvent.getSettleNum().equals("30205")){
                settleNumList.add("30195"); settleNumList.add("30205");
            }else {
                settleNumList.add("30225"); settleNumList.add("30235");
            }
            //自动计算当前事件比分
            list =matchSettleEventRepository.getByStandardMatchIdAndSettleNumAndPeriodIdLessThanOrEqualAndIdNotAndEventTypeAndStatus(matchSettleEvent.getStandardMatchId(),settleNumList,matchSettleEvent.getPeriodId(),matchSettleEvent.getId(),3,3);
        } else {
            if(matchSettleEvent.getSettleNum().equals("3019")||matchSettleEvent.getSettleNum().equals("3020")){
                settleNumList.add("3019"); settleNumList.add("3020");
            }else {
                settleNumList.add("3022"); settleNumList.add("3023");
            }
            //自动计算当前事件比分
            list = matchSettleEventRepository.getByStandardMatchIdAndSettleNumAndPeriodIdLessThanOrEqualAndIdNotAndEventTypeAndStatus(matchSettleEvent.getStandardMatchId(),settleNumList,matchSettleEvent.getPeriodId(),matchSettleEvent.getId(),1,3);
        }


        MatchSettleEventEntity event = new MatchSettleEventEntity();
        BeanUtils.copyProperties(matchSettleEvent,event);
        list.add(event);
        countFaCardScoresByEvent(list,matchSettleEvent);
    }
    private void countFaCardScoresByEvent(List<MatchSettleEventEntity> list, MatchSettleEvent matchSettleEvent) {
        //1.计算黄牌数  红牌数  罚牌数
        Integer red_card_t1 =0; Integer yellow_card_t1=0; Integer fa_card_t1=0;
        Integer red_card_t2 =0; Integer yellow_card_t2=0; Integer fa_card_t2=0;
        for (MatchSettleEventEntity event : list) {
            if(matchSettleEvent.getPeriodId().equals(event.getPeriodId())){
                if( matchSettleEvent.getEventOrder()<event.getEventOrder()){
                    continue;
                }
            }
            if(StringUtils.isNotEmpty(event.getEventCode())){
                if(event.getEventCode().equals("yellow_card")){
                    if(event.getHomeAway().equals("home")){
                        yellow_card_t1++;fa_card_t1++;
                    }else {
                        yellow_card_t2++;fa_card_t2++;
                    }
                    continue;
                }
                if(event.getEventCode().equals("red_card")){
                    if(event.getHomeAway().equals("home")){
                        red_card_t1++;fa_card_t1+=2;
                    }else {
                        red_card_t2++;fa_card_t2+=2;
                    }
                    continue;
                }
            }
        }
        matchSettleEvent.setT1(fa_card_t1);
        matchSettleEvent.setT2(fa_card_t2);
        matchSettleEvent.setFirstT1(yellow_card_t1);
        matchSettleEvent.setFirstT2(yellow_card_t2);
        matchSettleEvent.setSecondT1(red_card_t1);
        matchSettleEvent.setSecondT2(red_card_t2);
    }

    public void updateGoalAndCornerEventByInfo(MatchSettleEvent matchSettleEvent, String homeAway,String eventCode) {
        log.info("updateGoalAndCornerEventByInfo param,matchSettleEvent: {},homeAway: {},eventCode: {}",matchSettleEvent,homeAway,eventCode);
        List< MatchSettleEventEntity> list =matchSettleEventRepository.getByStandardMatchIdAndEventCodeAndPeriodIdLessThanOrEqualAndIdNotAndEventType(matchSettleEvent.getStandardMatchId(),eventCode,matchSettleEvent.getPeriodId(),matchSettleEvent.getId(),matchSettleEvent.getEventType());
        Integer home=0;Integer away=0;
        matchSettleEvent.setHomeAway(homeAway);
        MatchSettleEventEntity event2 = new MatchSettleEventEntity();
        BeanUtils.copyProperties(matchSettleEvent,event2);
        list.add(event2);
        for (MatchSettleEventEntity event : list) {
            //1.事件次序过滤 如果是 同一个阶段 就判断 次序 如果不是 则 判断阶段大小
            if(matchSettleEvent.getPeriodId().equals(event.getPeriodId())){
                if(matchSettleEvent.getEventOrder()<event.getEventOrder()){
                    continue;
                }
            }
            //2.常规赛非常常规赛隔离比分
            if(matchSettleEvent.getPeriodId().equals(6l)||matchSettleEvent.getPeriodId().equals(7l)){
                if(event.getPeriodId().equals(41l)||event.getPeriodId().equals(42l)){
                    continue;
                }
            }else if(matchSettleEvent.getPeriodId().equals(41l)||matchSettleEvent.getPeriodId().equals(42l)){
                if(event.getPeriodId().equals(6l)||event.getPeriodId().equals(7l)){
                    continue;
                }
            }

            if(StringUtils.isEmpty(event.getHomeAway())){
                continue;
            }
            if(event.getHomeAway().equals("home")){
                home++;
            }else if(event.getHomeAway().equals("away")){
                away++;
            }else {
                //none
            }
        }
        //进球类型   home away  none
        matchSettleEvent.setT1(home);
        matchSettleEvent.setT2(away);
    }
    private Integer checkEventOrder(List<MatchSettleEvent> list) {
        Integer order =1;
        for (MatchSettleEvent matchSettleEvent : list) {
            if(matchSettleEvent.getEventOrder()!=null&&matchSettleEvent.getEventOrder()>order){
                order=matchSettleEvent.getEventOrder();
            }
        }
        return order;
    }

    public String processedScore(String forwScore, String settleNum, Integer extryInfo) {
        if (settleNum.equals("1021")) {
            forwScore= WinningMethodEnum.getWinningMethodByCode(extryInfo).getCode().toString();
            return forwScore;
        }

        if (settleNum.equals("1031")) {
            forwScore= YesNoEnum.getEnum(extryInfo).value.toString();
            return forwScore;
        }
        if (settleNum.equals("1032") || settleNum.equals("1033")) {
            //1表示走水
            if (extryInfo.equals(1)) {
                forwScore= WinningMethodEnum.Method_8.getCode().toString();
                return forwScore;
            }
        }
        return "-";
    }
    private void recordScore(UpdateMatchSettleScoreDto matchSettleScoreDto){
        MatchSettleInfoEntity   matchSettleInfo = matchSettleInfoRepository.getMatchSettleInfo(matchSettleScoreDto.getStandardMatchId());
        if (matchSettleInfo!=null) {

            matchSettleInfo.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            if (matchSettleScoreDto.getSettleNum().equals("105")) {
                matchSettleInfo.setH1T1(matchSettleScoreDto.getT1());
                matchSettleInfo.setH1T2(matchSettleScoreDto.getT2());
            }else if(matchSettleScoreDto.getSettleNum().equals("1010")){
                matchSettleInfo.setFtT1(matchSettleScoreDto.getT1());
                matchSettleInfo.setFtT2(matchSettleScoreDto.getT2());
            }
            //更新结算信息
            matchSettleInfoRepository.updateMatchSettleInfoToRedis(matchSettleInfo,false);
        }else {
            log.error("参数异常【matchSettleInfos为空! 】");
        }

    }

    @Override
    public Response querySettleType(Long StandardMatchId) {
        //1.查询结算信息
        MatchSettleInfoEntity matchSettleInfo = matchSettleInfoRepository.getMatchSettleInfo(StandardMatchId);
        if ( matchSettleInfo== null ||matchSettleInfo.getSettleType()==1) {
            return   Response.success(1);
        }else {
            return   Response.success(2);
        }
    }

    @Override
    public Response playCategoryFreezeAndReSettle(SettleQueryDTO settleQueryDTO) {
        log.info("playCategoryFreezeAndReSettle param,settleQueryDTO: {}",settleQueryDTO);

        //赛事级重跑
        if (settleQueryDTO.getLevel().equals(1)) {
            return matchReSettle(settleQueryDTO);
        }

        //玩法级重跑
        if (settleQueryDTO.getLevel().equals(2) && settleQueryDTO.getExInfo().equals(2)) {
            return categoryReSettle(settleQueryDTO);
        }

        //玩法级冻结
        if (settleQueryDTO.getLevel().equals(2)  &&
                (settleQueryDTO.getExInfo().equals(0)
                        || settleQueryDTO.getExInfo().equals(1))) {
            return categoryFreeze(settleQueryDTO);

        }
        return Response.failed();
    }

    @Override
    public List<MatchDelaySettleInfo> queryMatchDelaySettleInfoById(Long standardId) {
        List<MatchDelaySettleInfo> infos = matchDelaySettleInfoRepository.getModelByStandardMatchId(standardId);
        return infos;
    }


    //玩法级冻结/解冻
    private Response categoryFreeze(SettleQueryDTO settleQueryDTO){
        log.info("categoryFreeze param,settleQueryDTO: {}",settleQueryDTO);
        MatchSettleScoreSearchDto settleScoreSearchDto =new MatchSettleScoreSearchDto();
        settleScoreSearchDto.setStandardMatchId(settleQueryDTO.getMatchId());
        CategoryDto categoryDto =new  CategoryDto();
        if (settleQueryDTO.getPlayCategory().equals(1)) {
            categoryDto.setGoal(1);
            categoryDto.setCorner(0);
            categoryDto.setFaCard(0);
            settleScoreSearchDto.setEventCode("goal");
        }else  if (settleQueryDTO.getPlayCategory().equals(2)) {
            categoryDto.setGoal(0);
            categoryDto.setCorner(1);
            categoryDto.setFaCard(0);
            settleScoreSearchDto.setEventCode("corner");
        }else  if (settleQueryDTO.getPlayCategory().equals(3)) {
            categoryDto.setGoal(0);
            categoryDto.setCorner(0);
            categoryDto.setFaCard(1);
            settleScoreSearchDto.setEventCode("fa_card");
        }
        //查询比分
        List<MatchSettleScoreDto> matchSettleScoreDtos = matchSettleScoreAssemble.searchFootballMatchSettleScores(settleScoreSearchDto);
        //查询事件
        List<MatchSettleEventDto> matchSettleEventDtos = matchSettleEventAssemble.searchFootballMatchSettleEvent(settleScoreSearchDto);
        //更新比分
        List<MatchSettleScore> listScore = new ArrayList<>();
        List<MatchSettleEvent> listEvent = new ArrayList<>();
        for (MatchSettleScoreDto dto: matchSettleScoreDtos) {
            MatchSettleScore matchSettleScore = new MatchSettleScore();
            BeanUtils.copyProperties(dto,matchSettleScore);
            matchSettleScore.setModifyTime(System.currentTimeMillis());
            matchSettleScore.setId(Long.parseLong(dto.getId()));
            matchSettleScore.setSettleFreeze(settleQueryDTO.getExInfo());
            // matchSettleScoreMapper.updateByPrimaryKey(matchSettleScore);
            listScore.add(matchSettleScore);
        }

        //更新事件
        for (MatchSettleEventDto dto: matchSettleEventDtos) {
            MatchSettleEvent matchSettleEvent = new MatchSettleEvent();
            BeanUtils.copyProperties(dto,matchSettleEvent);
            matchSettleEvent.setModifyTime(System.currentTimeMillis());
            matchSettleEvent.setId(Long.parseLong(dto.getId()));
            matchSettleEvent.setSettleFreeze(settleQueryDTO.getExInfo());
            // matchSettleEventMapper.updateByPrimaryKey(matchSettleEvent);
            listEvent.add(matchSettleEvent);

            //进球球员和进球方式冻结
            if (settleQueryDTO.getPlayCategory().equals(1) && dto.getExtryEvent()!=null) {
                MatchSettleEventDto extryEvent = dto.getExtryEvent();
                MatchSettleEvent event = new MatchSettleEvent();
                BeanUtils.copyProperties(extryEvent,event);
                event.setModifyTime(System.currentTimeMillis());
                event.setId(Long.parseLong(extryEvent.getId()));
                event.setSettleFreeze(settleQueryDTO.getExInfo());
                listEvent.add(event);
            }

            //角球特殊次序中的比分冻结
            if(dto.getEventCode().equals("corner")
                    &&(dto.getSettleNum().equals("201")
                    ||dto.getSettleNum().equals("202")
                    ||dto.getSettleNum().equals("203")
                    ||dto.getSettleNum().equals("206")
                    ||dto.getSettleNum().equals("207")
                    ||dto.getSettleNum().equals("208"))){

                MatchSettleScore matchSettleScore = new MatchSettleScore();
                BeanUtils.copyProperties(dto,matchSettleScore);
                matchSettleScore.setModifyTime(System.currentTimeMillis());
                matchSettleScore.setId(Long.parseLong(dto.getId()));
                matchSettleScore.setSettleFreeze(settleQueryDTO.getExInfo());
                listScore.add(matchSettleScore);
                continue;
            }
        }


        if (listScore.size() != 0) {
            matchSettleScoreRepository.updateBatchById(listScore);
        }

        if (listEvent.size() != 0) {
            matchSettleEventRepository.saveOrUpdateBatch(listEvent);
        }


        //更新结算信息
        MatchSettleInfoEntity settleInfo = matchSettleInfoRepository.getMatchSettleInfo(settleQueryDTO.getMatchId());
        if (settleInfo == null) {
            return Response.failed("1031942");
        }

        String categoryFreezeStatus = settleInfo.getCategoryFreezeStatus();
        String forwText ="-";
        if (!StringUtils.isBlank(categoryFreezeStatus)) {
            CategoryDto category = JSON.parseObject(categoryFreezeStatus, CategoryDto.class);
            //更新玩法级的冻结状态
            if (settleQueryDTO.getPlayCategory().equals(1)) {
                //记录操作前的冻结状态
                if (category.getGoal() != null && category.getGoal()==1){
                    forwText = OperateLogTypeEnum.type_1.getCode().toString();
                }else{
                    forwText = OperateLogTypeEnum.type_2.getCode().toString();
                }
                //更新进球状态
                category.setGoal(settleQueryDTO.getExInfo());
            }else if (settleQueryDTO.getPlayCategory().equals(2)) {
                //记录操作前的冻结状态
                if (category.getCorner() != null && category.getCorner()==1){
                    forwText = OperateLogTypeEnum.type_1.getCode().toString();
                }else{
                    forwText = OperateLogTypeEnum.type_2.getCode().toString();
                }
                //更新角球状态
                category.setCorner(settleQueryDTO.getExInfo());
            }else if (settleQueryDTO.getPlayCategory().equals(3)) {
                //记录操作前的冻结状态
                if (category.getFaCard() != null && category.getFaCard()==1){
                    forwText = OperateLogTypeEnum.type_1.getCode().toString();
                }else{
                    forwText = OperateLogTypeEnum.type_2.getCode().toString();
                }
                //更新罚牌状态
                category.setFaCard(settleQueryDTO.getExInfo());
            }

            String categoryString = JSON.toJSONString(category);
            settleInfo.setCategoryFreezeStatus(categoryString);
            settleInfo.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        }else{
            String jsonString = JSON.toJSONString(categoryDto);
            settleInfo.setCategoryFreezeStatus(jsonString);
        }
        matchSettleInfoRepository.updateMatchSettleInfoToRedis(settleInfo,false);

        MatchFreezeMessage matchFreezeMessage = new MatchFreezeMessage();
        matchFreezeMessage.setMatchId(settleQueryDTO.getMatchId());
        matchFreezeMessage.setLevel(settleQueryDTO.getLevel());
        matchFreezeMessage.setPlayCategory(settleQueryDTO.getPlayCategory());
        matchFreezeMessage.setSportId(settleQueryDTO.getSportId());
        matchFreezeMessage.setFreezeSettleStatus(settleQueryDTO.getExInfo());
        matchFreezeMessage.setMins(settleQueryDTO.getMins());
        matchFreezeMessage.setFreezeTime(settleQueryDTO.getFreezeTime());
        matchFreezeMessage.setCreateTime(settleQueryDTO.getCreateTime());
        matchFreezeMessage.setOperatorId(settleQueryDTO.getOperatorId());
        matchFreezeMessage.setOperatorName(settleQueryDTO.getOperatorName());
        matchSettleCenterProducer.MatchFreeze(matchFreezeMessage,"玩法级冻结/解冻");
        //7.日志
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(settleQueryDTO.getMatchId());
        matchSettleOperateLogService.categoryReSettleAddLog(settleQueryDTO,forwText);

        matchSettleCenterProducer.doSendLogToRiskByType(standardMatchInfo,settleQueryDTO,forwText);
        return Response.success();
    }

    /**
     * 审核员赛事重推
     * @param settleQueryDTO
     * @return
     */
    private Response matchReSettle(SettleQueryDTO settleQueryDTO){
        log.info("matchReSettle param,settleQueryDTO: {}",settleQueryDTO);
        //1.查询比分
        MatchSettleScoreExample example =new MatchSettleScoreExample();
        example.createCriteria().andStandardMatchIdEqualTo(settleQueryDTO.getMatchId());
        example.setOrderByClause("settle_num desc");
        List<MatchSettleScore> list =matchSettleScoreRepository.getByStandardMatchIdAndEventCode(settleQueryDTO.getMatchId(),null);
        //2.更新比分
        for (MatchSettleScore matchSettleScore: list) {
            matchSettleScore.setModifyTime(System.currentTimeMillis());
            matchSettleScore.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.RE_SETTLE);
            matchSettleScore.setSettleTimes(matchSettleScore.getSettleTimes());
            matchSettleScore.setOperater(settleQueryDTO.getOperatorName());
            matchSettleScore.setUserid(settleQueryDTO.getOperatorId());
            //matchSettleScoreMapper.updateByPrimaryKey(matchSettleScore);
        }
        matchSettleScoreRepository.saveOrUpdateBatch(list);
        //3.批量查询事件
        List<Long> periods=new ArrayList<>();
        periods.add(6l);periods.add(7l);periods.add(41l);periods.add(42l);
        List<MatchSettleEventEntity> eventList =matchSettleEventRepository.getByEventCodeAndPeriodIdAndStatusAndStandardMatchIdAndHomeAway(null,periods,null,settleQueryDTO.getMatchId(),null);
        if(eventList.size()==0){
            return Response.failed("1031935");
        }
        //4.批量更新事件
        for (MatchSettleEventEntity event :eventList) {
            event.setModifyTime(System.currentTimeMillis());
            event.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.RE_SETTLE);
            event.setOperater(settleQueryDTO.getOperatorName());
            event.setUserid(settleQueryDTO.getOperatorId());
            //matchSettleEventMapper.updateByPrimaryKey(event);
        }
        //matchSettleEventRepository.saveBatch(eventList);
        matchSettleEventRepository.saveOrUpdateBatch(eventList);

        MatchSettleEventMessage matchSettleEvent =new MatchSettleEventMessage();
        matchSettleEvent.setStandardMatchId(settleQueryDTO.getMatchId());
        matchSettleEvent.setSportId(settleQueryDTO.getSportId());
        matchSettleEvent.setLevel(settleQueryDTO.getLevel());
        matchSettleEvent.setSettleNum("0");
        matchSettleEvent.setOperateType(3);
        //5.结算事件下发
        matchSettleScoresProducer.sendMatchSettleEvent(matchSettleEvent);


        //6.结算比分下发
        MatchSettleScoreMessage matchSettleScore=new MatchSettleScoreMessage();
        matchSettleScore.setStandardMatchId(settleQueryDTO.getMatchId());
        matchSettleScore.setSportId(settleQueryDTO.getSportId());
        matchSettleScore.setLevel(settleQueryDTO.getLevel());
        matchSettleScore.setSettleNum("0");
        matchSettleScore.setOperateType(3);
        matchSettleScoresProducer.sendMatchSettleScores(matchSettleScore);

        //7.日志
        matchSettleOperateLogService.matchReSettleAddLog(settleQueryDTO);

        return Response.success();
    }
    //玩法级重跑
    private Response categoryReSettle(SettleQueryDTO settleQueryDTO){
        log.info("categoryReSettle param,settleQueryDTO: {}",settleQueryDTO);

        MatchSettleScoreSearchDto settleScoreSearchDto =new MatchSettleScoreSearchDto();
        settleScoreSearchDto.setStandardMatchId(settleQueryDTO.getMatchId());
        if (settleQueryDTO.getPlayCategory().equals(1)) {
            settleScoreSearchDto.setEventCode("goal");
        }else  if (settleQueryDTO.getPlayCategory().equals(2)) {
            settleScoreSearchDto.setEventCode("corner");
        }else  if (settleQueryDTO.getPlayCategory().equals(3)) {
            settleScoreSearchDto.setEventCode("fa_card");
        }
        //查询比分
        List<MatchSettleScoreDto> matchSettleScoreDtos = matchSettleScoreAssemble.searchFootballMatchSettleScores(settleScoreSearchDto);
        //查询事件
        List<MatchSettleEventDto> matchSettleEventDtos = matchSettleEventAssemble.searchFootballMatchSettleEvent(settleScoreSearchDto);
        List<MatchSettleScore> listScore = new ArrayList<>();
        //更新比分
        for (MatchSettleScoreDto dto: matchSettleScoreDtos) {
            MatchSettleScore matchSettleScore = new MatchSettleScore();
            BeanUtils.copyProperties(dto,matchSettleScore);
            matchSettleScore.setModifyTime(System.currentTimeMillis());
            matchSettleScore.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.RE_SETTLE);
            matchSettleScore.setSettleTimes(matchSettleScore.getSettleTimes());
            matchSettleScore.setOperater(settleQueryDTO.getOperatorName());
            matchSettleScore.setUserid(settleQueryDTO.getOperatorId());
            matchSettleScore.setId(Long.parseLong(dto.getId()));
            listScore.add(matchSettleScore);

        }
        if (listScore.size() != 0) {
            matchSettleScoreRepository.saveOrUpdateBatch(listScore);
        }

        //更新事件
        List<MatchSettleEvent> listEvent = new ArrayList<>();
        for (MatchSettleEventDto dto: matchSettleEventDtos) {
            MatchSettleEvent matchSettleEvent = new MatchSettleEvent();
            BeanUtils.copyProperties(dto,matchSettleEvent);
            matchSettleEvent.setModifyTime(System.currentTimeMillis());
            matchSettleEvent.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.RE_SETTLE);
            matchSettleEvent.setSettleTimes(dto.getSettleTimes());
            matchSettleEvent.setOperater(settleQueryDTO.getOperatorName());
            matchSettleEvent.setUserid(settleQueryDTO.getOperatorId());
            matchSettleEvent.setId(Long.parseLong(dto.getId()));
            listEvent.add(matchSettleEvent);
        }

        if (listEvent.size() != 0) {
            matchSettleEventRepository.saveOrUpdateBatch(listEvent);
        }

        //6.结算比分下发
        MatchSettleScoreMessage matchSettleScore=new MatchSettleScoreMessage();
        matchSettleScore.setStandardMatchId(settleQueryDTO.getMatchId());
        matchSettleScore.setSportId(settleQueryDTO.getSportId());
        matchSettleScore.setLevel(settleQueryDTO.getLevel());
        matchSettleScore.setPlayCategory(settleQueryDTO.getPlayCategory());
        matchSettleScore.setSettleNum("0");
        matchSettleScore.setOperateType(3);
        matchSettleScoresProducer.sendMatchSettleScores(matchSettleScore);

        //7.结算事件下发
        MatchSettleEventMessage matchSettleEvent =new MatchSettleEventMessage();
        matchSettleEvent.setStandardMatchId(settleQueryDTO.getMatchId());
        matchSettleEvent.setSportId(settleQueryDTO.getSportId());
        matchSettleEvent.setLevel(settleQueryDTO.getLevel());
        matchSettleEvent.setPlayCategory(settleQueryDTO.getPlayCategory());
        matchSettleEvent.setSettleNum("0");
        matchSettleEvent.setOperateType(3);
        matchSettleScoresProducer.sendMatchSettleEvent(matchSettleEvent);

        //7.日志
        matchSettleOperateLogService.categoryReSettleAddLog(settleQueryDTO,"-");
        return Response.success();
    }

    /**
     * 编辑阶段比分
     * @param matchSettleScoreDto
     * @return
     */

    @Override
    public Response editMatchSettleScorev2(UpdateMatchSettleScoreDto matchSettleScoreDto) {
        log.info("[FootballNewMatchScoresSettleApiImpl] editMatchSettleScore-v2 with linkId:{} and param:{} start!",matchSettleScoreDto.getLinkedId(), matchSettleScoreDto);
        //赛事id
        Long standardMatchId = matchSettleScoreDto.getStandardMatchId();
        //赛事比分id
        Long matchScoreId = matchSettleScoreDto.getMatchScoreId();
        //审核员姓名
        String userName = matchSettleScoreDto.getOperatorName();

        //1.判断是否已超过结算时间
        if(matchServiceHelper.checkIfOverSettleTime(standardMatchId)){
            return Response.failed("1031930");
        }
        if(redisService.hasKey("SETTLE_ROLLBACK_MATCH_ID_"+matchSettleScoreDto.getMatchScoreId())){
            return Response.failed("1031960");
        }
        String key =CommonConstant.MATCH_PHASE_SCORE_SETTLE+ matchSettleScoreDto.getStandardMatchId();
        try{
            if(redisService.tryLock(key,key,2,5)) {
                MatchSettleCheckInfoEntity matchSettleCheckInfo=  matchSettleCheckInfoHelper.searchCheckInfoByUser(matchSettleScoreDto.getMatchScoreId(),matchSettleScoreDto.getStandardMatchId(),
                        matchSettleScoreDto.getOperatorName());

                if(matchSettleCheckInfo!=null && matchSettleCheckInfo.getCheckStatus()== MatchSettleCheckConstant.CheckStatus.CONFIRM){
                    //还没编辑 该用户已经确认比分
                    return Response.failed("1031933");
                }

                MatchSettleScore matchSettleScore =matchSettleScoreRepository.getById(matchSettleScoreDto.getMatchScoreId());
                Integer checkNumber = matchSettleScore.getCheckNumber();
                if(matchSettleScore==null){
                    return Response.failed("1031935");
                }

                if(!matchSettleCheckInfoHelper.isAllPeriodScoresBeforeSettled(matchSettleScore)&&
                        !matchSettleScoreDto.getSettleNum().equals(MatchPeriodEnum.GOAL_5.getCode().toString()) &&
                        !matchSettleScoreDto.getSettleNum().equals(MatchPeriodEnum.GOAL_9.getCode().toString())) {
                    // 请确保上一个比分已结算。
                    return Response.failed("1031946");
                }

                matchSettleScore.setExtryInfo(matchSettleScoreDto.getExtryInfo());
                matchSettleScore.setOperater(matchSettleScoreDto.getOperatorName());
                matchSettleScore.setT1(matchSettleScoreDto.getT1());
                matchSettleScore.setT2(matchSettleScoreDto.getT2());
                matchSettleScore.setGoWaterStatus(matchSettleScoreDto.getGoWaterStatus());
                MatchSettleCheckInfo checkInfo =new  MatchSettleCheckInfo();

                if(!matchSettleInfoHelper.checkSettleScoreAndAutoSettleNonEvent(matchSettleScore,null)){
                    return Response.failed("1031946");
                }
                //3.1无比分核对记录则初始化该用户的核对记录
                if(matchSettleCheckInfo==null){
                    //得到当前用户的次序
                    matchSettleCheckInfo=new MatchSettleCheckInfoEntity();
                    BeanUtils.copyProperties(matchSettleCheckInfo,checkInfo);
                    MatchSettleCheckInfo matchSettleCheckInfo2 =  SettleCheckUtils.initManualMatchSettleScores(matchSettleScore);
                    SettleCheckUtils.copyManualMatchSettleScore(matchSettleScoreDto,matchSettleCheckInfo2);
                    BeanUtils.copyProperties(matchSettleCheckInfo2,matchSettleCheckInfo);
                    matchSettleCheckInfo.setCheckNumber( SettleCheckUtils.getCheckNumber(matchSettleScore.getCheckNumber()));
                    matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
                    matchSettleCheckInfo.setDataSourceCode("PA");
                    matchSettleCheckInfoRepository.save(matchSettleCheckInfo);
                    log.info("[FootballNewMatchScoresSettleApiImpl] editMatchSettleScore linkId::{}:: 插入比分id为{}，审核员{}比分核对数据:{}",matchSettleScoreDto.getLinkedId(),matchScoreId,userName,matchSettleScoreDto);
                }else {
                    //3.2有比分核对记录则更新
                    BeanUtils.copyProperties(matchSettleCheckInfo,checkInfo);
                    SettleCheckUtils.copyManualMatchSettleScoreV2(matchSettleScoreDto,matchSettleCheckInfo);
                    matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
                    matchSettleCheckInfoRepository.updateById(matchSettleCheckInfo);
                    log.info("[FootballNewMatchScoresSettleApiImpl] editMatchSettleScore linkId::{}:: 更新比分id为{}，审核员{}比分核对数据:{}",matchSettleScoreDto.getLinkedId(),matchScoreId,userName,matchSettleScoreDto);
                }
                //TODO 记录日志
                MatchSettleCheckInfoEntity old = new MatchSettleCheckInfoEntity();
                BeanUtils.copyProperties(checkInfo,old);
                matchSettleOperateLogService.matchSettleCheckScoreAddLog(old,matchSettleCheckInfo,
                        matchSettleScoreDto,OperateLogTypeEnum.EDIT,matchSettleScore.getSettleNum(), checkNumber);
                log.info("[FootballNewMatchScoresSettleApiImpl] editMatchSettleScore with linkId:{} end!",matchSettleScoreDto.getLinkedId());
            }else {
                return Response.failed("1031933");
            }
        }catch (Exception e){
            log.error("[FootballNewMatchScoresSettleApiImpl] editMatchSettleScore with linkId:{} error",matchSettleScoreDto.getLinkedId(), e);
            return Response.failed();
        }finally {
            redisService.unLock(key,key);
        }
        return Response.success();
    }

    @Override
    public Response confirmMatchSettleScoreV2(ConfirmMatchSettleScoreDto matchSettleScoreDto) {
        log.info("[FootballNewMatchScoresSettleApiImpl] confirmMatchSettleScore-v2 with linkId:{} param: {}", matchSettleScoreDto.getLinkedId(), matchSettleScoreDto);
        //赛事id
        Long standardMatchId = matchSettleScoreDto.getStandardMatchId();
        //赛事比分id
        Long matchScoreId = matchSettleScoreDto.getMatchScoreId();
        //审核员姓名
        String userName = matchSettleScoreDto.getOperatorName();

        //1.判断是否已超过结算时间X
        if(matchServiceHelper.checkIfOverSettleTime(standardMatchId)){
            return Response.failed("1031930");
        }
        if(redisService.hasKey("SETTLE_ROLLBACK_MATCH_ID_"+matchSettleScoreDto.getMatchScoreId())){
            return Response.failed("1031960");
        }
        String key =CommonConstant.MATCH_PHASE_SCORE_SETTLE+ matchSettleScoreDto.getStandardMatchId();
        try{
            if(redisService.tryLock(key,key,2,5)) {
                //2.查询人工核对比分
                MatchSettleCheckInfoEntity matchSettleCheckInfo=  matchSettleCheckInfoHelper.searchCheckInfoByUser(matchSettleScoreDto.getMatchScoreId(),
                        matchSettleScoreDto.getStandardMatchId(),matchSettleScoreDto.getOperatorName());
                MatchSettleCheckInfoEntity checkInfo =new  MatchSettleCheckInfoEntity();
                BeanUtils.copyProperties(matchSettleCheckInfo,checkInfo);

                MatchSettleScore matchSettleScore =matchSettleScoreRepository.getById(matchSettleScoreDto.getMatchScoreId());
//                if (((matchSettleScore.getT1() == null || matchSettleScore.getT2() == null) && matchSettleScore.getExtryInfo() == null) && !"kick_off".equals(matchSettleScore.getEventCode())) {
//                    return Response.failed("该阶段比分为null，请重新编辑比分");
//                }
                Integer checkNumber = matchSettleScore.getCheckNumber();
                if(matchSettleScore==null){
                    return Response.failed("1031935");
                }
                if(matchSettleCheckInfo==null){
                    return Response.failed("1031934");
                }
                if(matchSettleCheckInfo.getCheckStatus()!= MatchSettleCheckConstant.CheckStatus.EDIT){
                    log.error("人工核对确认比分状态错误:{}",matchSettleCheckInfo);
                    return Response.failed("1031934");
                }

                //TODO 记录日志
                UpdateMatchSettleScoreDto dto =new UpdateMatchSettleScoreDto();
                BeanUtils.copyProperties(matchSettleScoreDto,dto);
                if (!StringUtil.isNullOrEmpty(matchSettleScore.getSettleNum())) {
                    dto.setSettleNum((matchSettleScore.getSettleNum()));
                }
                dto.setSportId(matchSettleScore.getSportId());
                matchSettleOperateLogService.matchSettleCheckScoreAddLog(checkInfo, matchSettleCheckInfo,
                        dto, OperateLogTypeEnum.CONFIRM_SCORE,matchSettleScore.getSettleNum(), checkNumber) ;

                //3.更新状态
                matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.CONFIRM);
                matchSettleCheckInfo.setModifyTime(System.currentTimeMillis());
                matchSettleCheckInfoRepository.updateById(matchSettleCheckInfo);
                //进入统一核对比分流程
                MatchSettleTemplate countDownTemplate = matchSettleTemplateHelper.getTemplateByStandardMatchId(standardMatchId, SettleTemplateTypeEnum.COUNT_DOWEN.code);
                MatchSettleCheckInfo checkInfo1 = matchSettleCheckInfoV2Converter.convertEntityToCheckInfo(matchSettleCheckInfo);
                Map<Long, Pair<Boolean, Boolean>> isScoreOrEventDiffMap = matchSettleBatchCheckService.batchCheckCommonMatchSettleScoreEvent(Arrays.asList(Pair.of(matchSettleScore,checkInfo1)),false,matchSettleScoreDto.getLinkedId(),countDownTemplate);
                Pair<Boolean, Boolean> isScoreOrEventDiff = isScoreOrEventDiffMap.get(matchSettleScore.getId());
                if (isScoreOrEventDiff == null) {
                    return Response.failed("接口调用-主流程赛事id没找到");
                }

                matchSettleInfoHelper.updateMatchGrayStatus(matchSettleCheckInfo.getStandardMatchId());
                matchSettleCheckInfoHelper.updateMatchFifteenMinGraySettleFactor(matchSettleScore.getStandardMatchId(),matchSettleScore.getSettleNum());
                if(isScoreOrEventDiff.getLeft()){
                    //1031947=比分不一致需要下个审核员审核
                    return Response.failed("1031947");
                }
                if(isScoreOrEventDiff.getRight()) {
                    // 同步比分
                    SettleMatchScoreDto scoreSearchDto = new SettleMatchScoreDto();
                    BeanUtils.copyProperties(matchSettleScoreDto, scoreSearchDto);
                    scoreSearchDto.setSettleNum(Integer.valueOf(matchSettleScoreDto.getSettleNum()));
                    scoreSearchDto.setT1(matchSettleCheckInfo.getT1());
                    scoreSearchDto.setT2(matchSettleCheckInfo.getT2());
                    scoreSearchDto.setOperatorName(scoreSearchDto.getOperatorName() + ",(第" + checkNumber + "人)");
                    syncScoreFactory.getProcessor(SettleSyncEnum.FOOTBALL_SYNC_SCORE).syncScore(scoreSearchDto);
                }
            }else {
                log.info("[FootballNewMatchScoresSettleApiImpl] confirmMatchSettleScore with linkId:{} obtain redis fail!",matchSettleScoreDto.getLinkedId());
                return Response.failed("1031933");
            }
        }catch (Exception e){
            log.error("[FootballNewMatchScoresSettleApiImpl] confirmMatchSettleScore with linkId:{} error",matchSettleScoreDto.getLinkedId(), e);
            return Response.failed();
        }finally {
            redisService.unLock(key,key);
        }
        log.info("[FootballNewMatchScoresSettleApiImpl] confirmMatchSettleScore with linkId:{} end!",matchSettleScoreDto.getLinkedId());
        return Response.success();
    }

    /**
     * 编辑次序比分
     * @param editMatchSettleEventDto
     * @return
     */
    @Override
    public Response editMatchSettleEventV2(EditMatchSettleEventDto editMatchSettleEventDto) {
        log.info("editMatchSettleEvent New param,editMatchSettleEventDto: {}",editMatchSettleEventDto);
        String key = CommonConstant.MATCH_SEQUENCE_SCORE_SETTLE + editMatchSettleEventDto.getStandardMatchId();
        //1.查询是否已经存在人工核对事件
        try {
            //3.更新或者新增
            if(redisService.tryLock(key,key,2,5)) {
                // 先获取事件（可能是eventType=1 次序事件，也可能是eventType=3 时段事件）
                MatchSettleEvent matchSettleEvent = matchSettleEventRepository.getById(editMatchSettleEventDto.getEventId());
                if(matchSettleEvent == null){
                    return Response.failed("1031935");
                }

                // 根据eventCode + eventType 分发：
                // - eventType=1：走原有 goal/corner/fa_card 逻辑（编辑次序比分）
                // - eventType=3：走统一的 editPeriodEvent，内部从对应次序的checkinfo复制比分，并校验次序已结算
                String eventCode = editMatchSettleEventDto.getEventCode();
                if("goal".equals(eventCode)){
                    if (matchSettleEvent.getEventType() != null && matchSettleEvent.getEventType() == 3) {
                        return this.editPeriodEvent(editMatchSettleEventDto, matchSettleEvent);
                    }
                    return this.editGoalEvent(editMatchSettleEventDto);
                }else if("corner".equals(eventCode)){
                    if (matchSettleEvent.getEventType() != null && matchSettleEvent.getEventType() == 3) {
                        return this.editPeriodEvent(editMatchSettleEventDto, matchSettleEvent);
                    }
                    return this.editCornerEvent(editMatchSettleEventDto);

                    //2.阶段比分
                }else if("fa_card".equals(eventCode)){
                    if (matchSettleEvent.getEventType() != null && matchSettleEvent.getEventType() == 3) {
                        return this.editPeriodEvent(editMatchSettleEventDto, matchSettleEvent);
                    }
                    return this.editFaCardEvent(editMatchSettleEventDto);
                }

                return Response.success();
            }else {
                return Response.failed("1031933");
            }
        }catch (Exception e){
            log.error("FootballNewMatchScoresSettleApiImpl-editMatchSettleEvent:",e);
            return Response.failed();
        }finally {
            redisService.unLock(key,key);
        }
        //4.返回成功
    }

    /**
     * 编辑发牌次序
     * @param editMatchSettleEventDto
     * @return
     */
    private Response editFaCardEvent(EditMatchSettleEventDto editMatchSettleEventDto) {
        log.info("editFaCardEvent New editMatchSettleEventDto: {}",editMatchSettleEventDto);
        //1.根据facard条件设置 主客队和 罚牌类型
        if(StringUtils.isEmpty(editMatchSettleEventDto.getHomeAway())){
            return Response.failed("1031939");
        }
        MatchSettleCheckInfoEntity matchSettleCheckInfo=  matchSettleCheckInfoHelper.searchCheckInfoByUser(editMatchSettleEventDto.getEventId(),editMatchSettleEventDto.getStandardMatchId(),
                editMatchSettleEventDto.getOperatorName());

        MatchSettleCheckInfoEntity checkInfo =new  MatchSettleCheckInfoEntity();

        if(matchSettleCheckInfo!=null){
            if(matchSettleCheckInfo.getCheckStatus()== MatchSettleCheckConstant.CheckStatus.CONFIRM){
                //错误编码还没编辑 该用户已经确认比分之类的
                return Response.failed("1031933");
            }
        }
        MatchSettleEvent matchSettleEvent =matchSettleEventRepository.getById(editMatchSettleEventDto.getEventId());
        Integer checkNumber = matchSettleEvent.getCheckNumber();
        if(matchSettleEvent==null){
            return Response.failed("1031935");
        }
        if(matchSettleEvent.getEventType() == 3 && StringUtils.isEmpty(editMatchSettleEventDto.getFiveMinSection())){
            return Response.failed("1031939");
        }
        // 审核员编辑时，不更新matchSettleEvent，只更新checkinfo
        // 使用临时对象计算比分，用于初始化checkinfo
        MatchSettleEvent tempMatchSettleEvent = new MatchSettleEvent();
        BeanUtils.copyProperties(matchSettleEvent, tempMatchSettleEvent);
        //1.自动计算进球比分（使用临时对象）
        updateFaCardEventByInfo(tempMatchSettleEvent, editMatchSettleEventDto.getHomeAway());
        //比分校验是否相同
        if(editMatchSettleEventDto.getGoWaterStatus()!=null&&editMatchSettleEventDto.getGoWaterStatus()==1){
            tempMatchSettleEvent.setGoWaterStatus(1);
        }else {
            tempMatchSettleEvent.setGoWaterStatus(0);
        }
        tempMatchSettleEvent.setExtryInfo(editMatchSettleEventDto.getExtryInfo());
        tempMatchSettleEvent.setFiveMinSection(editMatchSettleEventDto.getFiveMinSection());
        
        if(matchSettleCheckInfo==null){
            matchSettleCheckInfo =new MatchSettleCheckInfoEntity();
            BeanUtils.copyProperties(matchSettleCheckInfo,checkInfo);
            matchSettleCheckInfo.setId(IdWorker.getId());
            matchSettleCheckInfo.setModifyTime(System.currentTimeMillis());
            // 使用临时对象初始化checkinfo
            SettleCheckUtils.initCheckMatchSettleEventV2(tempMatchSettleEvent,matchSettleCheckInfo);
            //得到当前用户的次序
            matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
            matchSettleCheckInfo.setCheckNumber( SettleCheckUtils.getCheckNumber(matchSettleEvent.getCheckNumber()));
            matchSettleCheckInfo.setUserName(editMatchSettleEventDto.getOperatorName());
            matchSettleCheckInfo.setDataSourceCode("PA");
            // 如果eventType=1，前端入参fiveMinSection自动为null，直接设置即可
            matchSettleCheckInfo.setFiveMinSection(editMatchSettleEventDto.getFiveMinSection());
            matchSettleCheckInfoRepository.save(matchSettleCheckInfo);
        }else {
            BeanUtils.copyProperties(matchSettleCheckInfo,checkInfo);
            // 使用临时对象初始化checkinfo
            SettleCheckUtils.initCheckMatchSettleEventV2(tempMatchSettleEvent,matchSettleCheckInfo);
            matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
            // 如果eventType=1，前端入参fiveMinSection自动为null，直接设置即可
            matchSettleCheckInfo.setFiveMinSection(editMatchSettleEventDto.getFiveMinSection());
            matchSettleCheckInfoRepository.updateById(matchSettleCheckInfo);
        }
        //日志录入
        UpdateMatchSettleScoreDto dto =new UpdateMatchSettleScoreDto();
        BeanUtils.copyProperties(editMatchSettleEventDto,dto);
        dto.setSportId(matchSettleEvent.getSportId());
        // 如果eventType=1，前端入参fiveMinSection自动为null，直接设置即可
        matchSettleCheckInfo.setFiveMinSection(editMatchSettleEventDto.getFiveMinSection());
        matchSettleCheckInfo.setFifteenMinSection(editMatchSettleEventDto.getFifteenMinSection()!=null?editMatchSettleEventDto.getFifteenMinSection():editMatchSettleEventDto.getFiveMinSection());
        matchSettleOperateLogService.matchSettleCheckEventAddLog(checkInfo, matchSettleCheckInfo,
                dto, OperateLogTypeEnum.EDIT, matchSettleEvent.getSettleNum(), checkNumber, matchSettleEvent.getEventType());
        
        // 确保存在时段事件（eventType=3）：用于展示时段信息
//        ensurePeriodEventExists(matchSettleEvent, editMatchSettleEventDto);
        
        // 为审核员创建/更新时段事件的checkinfo（填充比分信息，不填充时段信息）
//        ensurePeriodEventCheckInfo(matchSettleEvent, editMatchSettleEventDto);

        return Response.success();

    }

    /**
     * 编辑角球次序
     * @param editMatchSettleEventDto
     * @return
     */
    private Response editCornerEvent(EditMatchSettleEventDto editMatchSettleEventDto) {
        log.info("editCornerEvent New editMatchSettleEventDto: {}",editMatchSettleEventDto);
        if(StringUtils.isEmpty(editMatchSettleEventDto.getHomeAway())){
            return Response.failed("1031939");
        }
        MatchSettleCheckInfoEntity matchSettleCheckInfo=  matchSettleCheckInfoHelper.searchCheckInfoByUser(editMatchSettleEventDto.getEventId(),editMatchSettleEventDto.getStandardMatchId(),
                editMatchSettleEventDto.getOperatorName());
        MatchSettleCheckInfoEntity checkInfo =new  MatchSettleCheckInfoEntity();

        if(matchSettleCheckInfo!=null){
            if(matchSettleCheckInfo.getCheckStatus()== MatchSettleCheckConstant.CheckStatus.CONFIRM){
                //错误编码还没编辑 该用户已经确认比分之类的 TODO
                return Response.failed("1031933");
            }
        }
        //1.事件只编辑比分
        MatchSettleEvent matchSettleEvent =matchSettleEventRepository.getById(editMatchSettleEventDto.getEventId());
        Integer checkNumber = matchSettleEvent.getCheckNumber();
        if(matchSettleEvent!=null){
            // 审核员编辑时，不更新matchSettleEvent，只更新checkinfo
            // 使用临时对象计算比分，用于初始化checkinfo
            MatchSettleEvent tempMatchSettleEvent = new MatchSettleEvent();
            BeanUtils.copyProperties(matchSettleEvent, tempMatchSettleEvent);
            //自动计算角球比分（使用临时对象）
            updateGoalAndCornerEventByInfo(tempMatchSettleEvent, editMatchSettleEventDto.getHomeAway(), "corner");
            tempMatchSettleEvent.setExtryInfo(editMatchSettleEventDto.getExtryInfo());
            tempMatchSettleEvent.setHomeAway(editMatchSettleEventDto.getHomeAway());
            if(editMatchSettleEventDto.getGoWaterStatus()!=null&&editMatchSettleEventDto.getGoWaterStatus()==1){
                tempMatchSettleEvent.setGoWaterStatus(1);
            }else {
                tempMatchSettleEvent.setGoWaterStatus(0);
            }
            tempMatchSettleEvent.setFiveMinSection(editMatchSettleEventDto.getFiveMinSection());
            
            if(matchSettleCheckInfo==null){
                matchSettleCheckInfo =new MatchSettleCheckInfoEntity();
                BeanUtils.copyProperties(matchSettleCheckInfo,checkInfo);
                matchSettleCheckInfo.setId(IdWorker.getId());
                matchSettleCheckInfo.setModifyTime(System.currentTimeMillis());
                // 使用临时对象初始化checkinfo
                SettleCheckUtils.initCheckMatchSettleEventV2(tempMatchSettleEvent,matchSettleCheckInfo);
                //得到当前用户的次序
                matchSettleCheckInfo.setCheckNumber( SettleCheckUtils.getCheckNumber(matchSettleEvent.getCheckNumber()));
                matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
                matchSettleCheckInfo.setUserName(editMatchSettleEventDto.getOperatorName());
                matchSettleCheckInfo.setDataSourceCode("PA");
                // 如果eventType=1，前端入参fiveMinSection自动为null，直接设置即可
                matchSettleCheckInfo.setFiveMinSection(editMatchSettleEventDto.getFiveMinSection());
                matchSettleCheckInfoRepository.save(matchSettleCheckInfo);
            }else {
                BeanUtils.copyProperties(matchSettleCheckInfo,checkInfo);
                // 使用临时对象初始化checkinfo
                SettleCheckUtils.initCheckMatchSettleEventV2(tempMatchSettleEvent,matchSettleCheckInfo);
                matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
                // 如果eventType=1，前端入参fiveMinSection自动为null，直接设置即可
                matchSettleCheckInfo.setFiveMinSection(editMatchSettleEventDto.getFiveMinSection());
                matchSettleCheckInfoRepository.updateById(matchSettleCheckInfo);
            }
            //TODO  日志录入
            UpdateMatchSettleScoreDto dto =new UpdateMatchSettleScoreDto();
            BeanUtils.copyProperties(editMatchSettleEventDto,dto);
            dto.setSportId(matchSettleEvent.getSportId());
            // 如果eventType=1，前端入参fiveMinSection自动为null，直接设置即可
            matchSettleCheckInfo.setFiveMinSection(editMatchSettleEventDto.getFiveMinSection());
            matchSettleCheckInfo.setFifteenMinSection(editMatchSettleEventDto.getFifteenMinSection()!=null?editMatchSettleEventDto.getFifteenMinSection():editMatchSettleEventDto.getFiveMinSection());
            matchSettleOperateLogService.matchSettleCheckEventAddLog(checkInfo, matchSettleCheckInfo,
                    dto, OperateLogTypeEnum.EDIT, matchSettleEvent.getSettleNum(), checkNumber, matchSettleEvent.getEventType());
//            ensurePeriodEventCheckInfo(matchSettleEvent, editMatchSettleEventDto);

            return Response.success();
        }else {
            MatchSettleScore matchSettleScore =matchSettleScoreRepository.getById(editMatchSettleEventDto.getEventId());
            MatchSettleScore matchSettleEventBefore =new MatchSettleScore();
            if(matchSettleScore!=null) {
                //角球阶段比分由人工录入
                //比分判断是否相同
//                if(MatchEventInfoSettleUtils.equileMatchSettleScores(matchSettleEventBefore,matchSettleScore)){
//                    return Response.failed("1031940");
//                }
                if(editMatchSettleEventDto.getGoWaterStatus()!=null&&editMatchSettleEventDto.getGoWaterStatus()==1){
                    matchSettleScore.setGoWaterStatus(1);
                }else {
                    matchSettleScore.setGoWaterStatus(0);
                }
                matchSettleScore.setExtryInfo(editMatchSettleEventDto.getExtryInfo());
                matchSettleScore.setT1(editMatchSettleEventDto.getT1());
                matchSettleScore.setT2(editMatchSettleEventDto.getT2());
                matchSettleScore.setModifyTime(System.currentTimeMillis());
                matchSettleScore.setOperater(editMatchSettleEventDto.getOperatorName());
                matchSettleScore.setStatus(1);
                if(matchSettleCheckInfo==null){
                    matchSettleCheckInfo =new MatchSettleCheckInfoEntity();
                    BeanUtils.copyProperties(matchSettleCheckInfo,checkInfo);
                    matchSettleCheckInfo.setId(IdWorker.getId());
                    matchSettleCheckInfo.setModifyTime(System.currentTimeMillis());
                    SettleCheckUtils.initCheckMatchSettleScoreV2(matchSettleScore,matchSettleCheckInfo);
                    //得到当前用户的次序
                    matchSettleCheckInfo.setCheckNumber( SettleCheckUtils.getCheckNumber(matchSettleEvent.getCheckNumber()));
                    matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
                    matchSettleCheckInfo.setUserName(editMatchSettleEventDto.getOperatorName());
                    matchSettleCheckInfo.setFiveMinSection(editMatchSettleEventDto.getFiveMinSection());
                    matchSettleCheckInfoRepository.save(matchSettleCheckInfo);
                }else {
                    BeanUtils.copyProperties(matchSettleCheckInfo,checkInfo);
                    SettleCheckUtils.initCheckMatchSettleScoreV2(matchSettleScore,matchSettleCheckInfo);
                    matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
                    matchSettleCheckInfo.setFiveMinSection(editMatchSettleEventDto.getFiveMinSection());
                    matchSettleCheckInfoRepository.updateById(matchSettleCheckInfo);
                }
                UpdateMatchSettleScoreDto dto =new UpdateMatchSettleScoreDto();
                BeanUtils.copyProperties(editMatchSettleEventDto,dto);
                dto.setSportId(matchSettleEvent.getSportId());
                matchSettleCheckInfo.setFifteenMinSection(editMatchSettleEventDto.getFifteenMinSection()!=null?editMatchSettleEventDto.getFifteenMinSection():editMatchSettleEventDto.getFiveMinSection());
                matchSettleOperateLogService.matchSettleCheckScoreAddLog(checkInfo, matchSettleCheckInfo,
                        dto, OperateLogTypeEnum.EDIT,matchSettleEvent.getSettleNum(),checkNumber) ;
            }else {
                return Response.failed("1031940");
            }
            //TODO  日志录入
            return Response.success();
        }
    }

    /**
     * 编辑进球次序
     * @param editMatchSettleEventDto
     * @return
     */
    private Response editGoalEvent(EditMatchSettleEventDto editMatchSettleEventDto) {
        log.info("editGoalEvent New param,editMatchSettleEventDto :{}",editMatchSettleEventDto);
        MatchSettleCheckInfoEntity matchSettleCheckInfo=  matchSettleCheckInfoHelper.searchCheckInfoByUser(editMatchSettleEventDto.getEventId(),editMatchSettleEventDto.getStandardMatchId(),
                editMatchSettleEventDto.getOperatorName());

        MatchSettleCheckInfoEntity checkInfo =new  MatchSettleCheckInfoEntity();


        if(matchSettleCheckInfo!=null){
            if(matchSettleCheckInfo.getCheckStatus()== MatchSettleCheckConstant.CheckStatus.CONFIRM){
                //错误编码还没编辑 该用户已经确认比分之类的
                return Response.failed("1031933");
            }
        }
        MatchSettleEvent matchSettleEvent =matchSettleEventRepository.getById(editMatchSettleEventDto.getEventId());
        Integer checkNumber = matchSettleEvent.getCheckNumber();
        if(matchSettleEvent==null){
            return Response.failed("1031935");
        }
        if(StringUtils.isEmpty(editMatchSettleEventDto.getHomeAway())){
            return Response.failed("1031939");
        }
        if(matchSettleEvent.getEventType() == 3 && StringUtils.isEmpty(editMatchSettleEventDto.getFiveMinSection())){
            return Response.failed("1031939");
        }
        if(!matchSettleCheckInfoHelper.isPeriodScoresBeforeSettledByEvent(matchSettleEvent)){
            return Response.failed("10138");
        }
        // 审核员编辑时，不更新matchSettleEvent，只更新checkinfo
        // 使用临时对象计算比分，用于初始化checkinfo
        MatchSettleEvent tempMatchSettleEvent = new MatchSettleEvent();
        BeanUtils.copyProperties(matchSettleEvent, tempMatchSettleEvent);
        //1.自动计算进球比分（使用临时对象）
        updateGoalAndCornerEventByInfo(tempMatchSettleEvent, editMatchSettleEventDto.getHomeAway(), "goal");
        //比分校验是否相同
        if(editMatchSettleEventDto.getGoWaterStatus()!=null&&editMatchSettleEventDto.getGoWaterStatus()==1){
            tempMatchSettleEvent.setGoWaterStatus(1);
        }else {
            tempMatchSettleEvent.setGoWaterStatus(0);
        }
        tempMatchSettleEvent.setExtryInfo(editMatchSettleEventDto.getExtryInfo());
        tempMatchSettleEvent.setHomeAway(editMatchSettleEventDto.getHomeAway());
        tempMatchSettleEvent.setFiveMinSection(editMatchSettleEventDto.getFiveMinSection());
        
        if(matchSettleCheckInfo==null){
            matchSettleCheckInfo =new MatchSettleCheckInfoEntity();
            BeanUtils.copyProperties(matchSettleCheckInfo,checkInfo);
            matchSettleCheckInfo.setId(IdWorker.getId());
            matchSettleCheckInfo.setModifyTime(System.currentTimeMillis());
            // 使用临时对象初始化checkinfo
            SettleCheckUtils.initCheckMatchSettleEventV2(tempMatchSettleEvent,matchSettleCheckInfo);
            //得到当前用户的次序
            matchSettleCheckInfo.setCheckNumber( SettleCheckUtils.getCheckNumber(matchSettleEvent.getCheckNumber()));
            matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
            matchSettleCheckInfo.setUserName(editMatchSettleEventDto.getOperatorName());
            matchSettleCheckInfo.setDataSourceCode("PA");
            // 如果eventType=1，前端入参fiveMinSection自动为null，直接设置即可
            matchSettleCheckInfo.setFiveMinSection(editMatchSettleEventDto.getFiveMinSection());
            matchSettleCheckInfoRepository.save(matchSettleCheckInfo);
        }else {
            BeanUtils.copyProperties(matchSettleCheckInfo,checkInfo);
            // 使用临时对象初始化checkinfo
            SettleCheckUtils.initCheckMatchSettleEventV2(tempMatchSettleEvent,matchSettleCheckInfo);
            matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
            // 如果eventType=1，前端入参fiveMinSection自动为null，直接设置即可
            matchSettleCheckInfo.setFiveMinSection(editMatchSettleEventDto.getFiveMinSection());
            matchSettleCheckInfoRepository.updateById(matchSettleCheckInfo);
        }
        //TODO  日志录入
        UpdateMatchSettleScoreDto dto =new UpdateMatchSettleScoreDto();
        BeanUtils.copyProperties(editMatchSettleEventDto,dto);
        dto.setSportId(matchSettleEvent.getSportId());
        // 如果eventType=1，前端入参fiveMinSection自动为null，直接设置即可
        matchSettleCheckInfo.setFiveMinSection(editMatchSettleEventDto.getFiveMinSection());
        matchSettleCheckInfo.setFifteenMinSection(editMatchSettleEventDto.getFifteenMinSection()!=null?editMatchSettleEventDto.getFifteenMinSection():editMatchSettleEventDto.getFiveMinSection());
        matchSettleOperateLogService.matchSettleCheckEventAddLog(checkInfo, matchSettleCheckInfo,
                dto, OperateLogTypeEnum.EDIT, matchSettleEvent.getSettleNum(), checkNumber, matchSettleEvent.getEventType());

        // 确保存在时段事件（eventType=3）：用于展示时段信息
//        ensurePeriodEventExists(matchSettleEvent, editMatchSettleEventDto);
        
        // 为审核员创建/更新时段事件的checkinfo（填充比分信息，不填充时段信息）
//        ensurePeriodEventCheckInfo(matchSettleEvent, editMatchSettleEventDto);

        return Response.success();
    }
    
    /**
     * 编辑eventType=3（时段事件）的checkinfo
     * 当审核员编辑时段部分时，需要创建/更新时段事件的checkinfo，包含时段信息
     * 必须先确保对应的eventType=1（比分事件）已结算（status=3）
     * @param editMatchSettleEventDto 编辑参数
     * @param matchSettleEvent 时段事件（eventType=3）
     */
    private Response editPeriodEvent(EditMatchSettleEventDto editMatchSettleEventDto, MatchSettleEvent matchSettleEvent) {
        try {
            if(matchSettleEvent.getEventType() == 3 && StringUtils.isEmpty(editMatchSettleEventDto.getFiveMinSection())){
                return Response.failed("1031939");
            }

            // 编辑时段(eventType=3)时：将“对应次序(eventType=1)的checkinfo比分”复制到时段事件/时段checkinfo中
            MatchSettleEvent scoreEvent = null;
            try {
                String scoreSettleNum = SettleNumUtils.getTypeEventSettleNum(
                        matchSettleEvent.getEventCode(), matchSettleEvent.getPeriodId(), 1);
                List<MatchSettleEvent> scoreEvents = matchSettleEventRepository.getByMatchIdAndSettleNumAndEventOrderAndPeriodId(
                        matchSettleEvent.getStandardMatchId(), scoreSettleNum, matchSettleEvent.getEventOrder(), matchSettleEvent.getPeriodId());
                scoreEvent = scoreEvents.stream()
                        .filter(e -> e.getEventType() != null && e.getEventType() == 1)
                        .findFirst()
                        .orElse(null);
            } catch (Exception e) {
                log.warn("editPeriodEvent::查找对应比分事件失败, periodEventId:{}, eventCode:{}", matchSettleEvent.getId(), matchSettleEvent.getEventCode(), e);
            }

            // 必须先确保对应的eventType=1（比分事件）存在且已结算（status=3）
            if (scoreEvent == null) {
                log.warn("editPeriodEvent::尝试编辑时段事件，但对应的比分事件不存在, periodEventId:{}, eventCode:{}", 
                        matchSettleEvent.getId(), matchSettleEvent.getEventCode());
                return Response.failed("1031941"); // 必须先编辑/结算比分
            }
            if (scoreEvent.getStatus() == null || scoreEvent.getStatus() != 3) {
                log.warn("editPeriodEvent::尝试编辑时段事件，但对应的比分事件未结算, periodEventId:{}, scoreEventId:{}, scoreEventStatus:{}, eventCode:{}", 
                        matchSettleEvent.getId(), scoreEvent.getId(), scoreEvent.getStatus(), matchSettleEvent.getEventCode());
                return Response.failed("1031941"); // 必须先结算比分事件
            }

            MatchSettleCheckInfoEntity scoreEventCheckInfo = null;
            if (scoreEvent != null) {
                scoreEventCheckInfo = matchSettleCheckInfoHelper.searchCheckInfoByUser(
                        scoreEvent.getId(), scoreEvent.getStandardMatchId(), editMatchSettleEventDto.getOperatorName());
            }

            // 查找当前用户是否已有时段事件的checkinfo
            MatchSettleCheckInfoEntity matchSettleCheckInfo = matchSettleCheckInfoHelper.searchCheckInfoByUser(
                    matchSettleEvent.getId(), matchSettleEvent.getStandardMatchId(), editMatchSettleEventDto.getOperatorName());
            
            if(matchSettleCheckInfo != null){
                if(matchSettleCheckInfo.getCheckStatus() == MatchSettleCheckConstant.CheckStatus.CONFIRM){
                    // 该用户已经确认，不能再次编辑
                    return Response.failed("1031933");
                }
            }
            
            MatchSettleCheckInfoEntity checkInfoBefore = new MatchSettleCheckInfoEntity();

            if(editMatchSettleEventDto.getGoWaterStatus()!=null&&editMatchSettleEventDto.getGoWaterStatus()==1){
                matchSettleEvent.setGoWaterStatus(1);
            }else {
                matchSettleEvent.setGoWaterStatus(0);
            }
            matchSettleEvent.setExtryInfo(editMatchSettleEventDto.getExtryInfo());
            matchSettleEvent.setFiveMinSection(editMatchSettleEventDto.getFiveMinSection());
            matchSettleEvent.setHomeAway(scoreEvent.getHomeAway());
            matchSettleEvent.setEventCode(scoreEvent.getEventCode());
            if(matchSettleCheckInfo == null){
                // 不存在，创建新的checkinfo
                matchSettleCheckInfo = new MatchSettleCheckInfoEntity();
                BeanUtils.copyProperties(matchSettleCheckInfo, checkInfoBefore);
                matchSettleCheckInfo.setId(IdWorker.getId());
                matchSettleCheckInfo.setModifyTime(System.currentTimeMillis());
                matchSettleCheckInfo.setCreateTime(System.currentTimeMillis());
                
                // 使用SettleCheckUtils初始化checkinfo
                SettleCheckUtils.initCheckMatchSettleEventV2(matchSettleEvent, matchSettleCheckInfo);
                // 确保时段checkinfo比分来自“对应次序(eventType=1)的checkinfo”
                matchSettleCheckInfo.setT1(scoreEvent.getT1());
                matchSettleCheckInfo.setT2(scoreEvent.getT2());
                matchSettleCheckInfo.setFirstT1(scoreEvent.getFirstT1());
                matchSettleCheckInfo.setFirstT2(scoreEvent.getFirstT2());
                matchSettleCheckInfo.setSecondT1(scoreEvent.getSecondT1());
                matchSettleCheckInfo.setSecondT2(scoreEvent.getSecondT2());
                // 得到当前用户的次序
                matchSettleCheckInfo.setCheckNumber(SettleCheckUtils.getCheckNumber(matchSettleEvent.getCheckNumber()));
                matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
                matchSettleCheckInfo.setUserName(editMatchSettleEventDto.getOperatorName());
                matchSettleCheckInfo.setDataSourceCode("PA");
                
                matchSettleCheckInfoRepository.save(matchSettleCheckInfo);
            }else {
                // 存在，更新checkinfo
                BeanUtils.copyProperties(matchSettleCheckInfo, checkInfoBefore);
                SettleCheckUtils.initCheckMatchSettleEventV2(matchSettleEvent, matchSettleCheckInfo);

                // 确保时段checkinfo比分来自“对应次序(eventType=1)的checkinfo”
//                if (scoreEventCheckInfo != null) {
//                    if (scoreEventCheckInfo.getT1() != null) {
//                        matchSettleCheckInfo.setT1(scoreEvent.getT1());
//                    }
//                    if (scoreEventCheckInfo.getT2() != null) {
//                        matchSettleCheckInfo.setT2(scoreEvent.getT2());
//                    }
//                    matchSettleCheckInfo.setFirstT1(scoreEventCheckInfo.getFirstT1());
//                    matchSettleCheckInfo.setFirstT2(scoreEventCheckInfo.getFirstT2());
//                    matchSettleCheckInfo.setSecondT1(scoreEventCheckInfo.getSecondT1());
//                    matchSettleCheckInfo.setSecondT2(scoreEventCheckInfo.getSecondT2());
//                }
                matchSettleCheckInfo.setT1(scoreEvent.getT1());
                matchSettleCheckInfo.setT2(scoreEvent.getT2());
                matchSettleCheckInfo.setFirstT1(scoreEvent.getFirstT1());
                matchSettleCheckInfo.setFirstT2(scoreEvent.getFirstT2());
                matchSettleCheckInfo.setSecondT1(scoreEvent.getSecondT1());
                matchSettleCheckInfo.setSecondT2(scoreEvent.getSecondT2());
                
                matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
                matchSettleCheckInfo.setModifyTime(System.currentTimeMillis());
                
                matchSettleCheckInfoRepository.updateById(matchSettleCheckInfo);
            }
            
            // 日志录入
            UpdateMatchSettleScoreDto dto = new UpdateMatchSettleScoreDto();
            BeanUtils.copyProperties(editMatchSettleEventDto, dto);
            dto.setSportId(matchSettleEvent.getSportId());
            matchSettleCheckInfo.setFifteenMinSection(editMatchSettleEventDto.getFifteenMinSection() != null 
                    ? editMatchSettleEventDto.getFifteenMinSection() 
                    : editMatchSettleEventDto.getFiveMinSection());
            matchSettleOperateLogService.matchSettleCheckEventAddLog(checkInfoBefore, matchSettleCheckInfo,
                    dto, OperateLogTypeEnum.EDIT, matchSettleEvent.getSettleNum(), matchSettleEvent.getCheckNumber(), matchSettleEvent.getEventType());
            
            log.info("editPeriodEvent::编辑时段事件checkinfo成功, eventId:{}, checkInfoId:{}, fiveMinSection:{}", 
                    matchSettleEvent.getId(), matchSettleCheckInfo.getId(), matchSettleCheckInfo.getFiveMinSection());
            
            return Response.success();
        } catch (Exception e) {
            log.error("editPeriodEvent::编辑时段事件checkinfo失败, eventId:{}, eventCode:{}", 
                    matchSettleEvent.getId(), matchSettleEvent.getEventCode(), e);
            return Response.failed();
        }
    }
    
    /**
     * 确保存在时段事件（eventType=3）：用于展示时段信息
     * 当编辑进球事件时，需要确保有两条数据：
     * 1. eventType=1 的事件（用于展示比分）
     * 2. eventType=3 的事件（用于展示时段）
     * @param matchSettleEvent 原事件（eventType=1）
     * @param editMatchSettleEventDto 编辑参数
     */
    private void ensurePeriodEventExists(MatchSettleEvent matchSettleEvent, EditMatchSettleEventDto editMatchSettleEventDto) {
        try {
            // 只处理eventType=1的事件（比分事件），如果当前事件不是eventType=1，则跳过
            if (matchSettleEvent.getEventType() == null || matchSettleEvent.getEventType() != 1) {
                return;
            }
            
            // eventType=3只在periodId=6L或7L时创建（加时赛41L, 42L没有eventType=3）
            Long periodId = matchSettleEvent.getPeriodId();
            if (periodId == null || (!periodId.equals(6L) && !periodId.equals(7L))) {
                return;
            }
            
            // 查找是否存在对应的时段事件（eventType=3）
            String periodSettleNum = SettleNumUtils.getTypeEventSettleNum(matchSettleEvent.getEventCode(), matchSettleEvent.getPeriodId(), 3);
            List<MatchSettleEvent> periodEvents = matchSettleEventRepository.getByMatchIdAndSettleNumAndEventOrderAndPeriodId(
                    matchSettleEvent.getStandardMatchId(), periodSettleNum, matchSettleEvent.getEventOrder(), matchSettleEvent.getPeriodId());
            
            // 过滤出eventType=3的事件
            MatchSettleEvent periodEvent = periodEvents.stream()
                    .filter(e -> e.getEventType() != null && e.getEventType() == 3)
                    .findFirst()
                    .orElse(null);
            
            if (periodEvent == null) {
                // 不存在，创建新的时段事件
                periodEvent = new MatchSettleEvent();
                BeanUtils.copyProperties(matchSettleEvent, periodEvent);
                periodEvent.setId(IdGenerator.nextId());
                periodEvent.setEventType(3);
                periodEvent.setSettleNum(periodSettleNum);
                periodEvent.setCreateTime(System.currentTimeMillis());
                periodEvent.setModifyTime(System.currentTimeMillis());
                periodEvent.setStatus(0); // 新建事件状态为0
                periodEvent.setDataSourceCode("PA");
                periodEvent.setCheckNumber(1);
                // 时段事件保留比分信息，与比分事件保持一致
                // 时段信息（fiveMinSection、fifteenMinSection）不在这里设置，等手动编辑时段部分时再设置
                matchSettleEventRepository.save(periodEvent);
                log.info("editGoalEvent::创建时段事件成功, eventId:{}, periodEventId:{}", matchSettleEvent.getId(), periodEvent.getId());
            } else {
                // 存在，更新比分信息（t1、t2、homeAway等），保持比分一致性
                // 不更新时段信息（fiveMinSection、fifteenMinSection），因为时段信息只有在手动编辑时段部分时才会有
                periodEvent.setT1(matchSettleEvent.getT1());
                periodEvent.setT2(matchSettleEvent.getT2());
                periodEvent.setHomeAway(matchSettleEvent.getHomeAway());
                periodEvent.setExtryInfo(matchSettleEvent.getExtryInfo());
                periodEvent.setGoWaterStatus(matchSettleEvent.getGoWaterStatus());
                periodEvent.setModifyTime(System.currentTimeMillis());
                periodEvent.setOperater(editMatchSettleEventDto.getOperatorName());
                matchSettleEventRepository.updateById(periodEvent);
                log.info("editGoalEvent::更新时段事件比分信息成功, periodEventId:{}, t1:{}, t2:{}", 
                        periodEvent.getId(), periodEvent.getT1(), periodEvent.getT2());
            }
        } catch (Exception e) {
            log.error("editGoalEvent::确保时段事件存在失败, eventId:{}", matchSettleEvent.getId(), e);
            // 不抛出异常，避免影响主流程
        }
    }

    /**
     * 为审核员创建/更新时段事件（eventType=3）的checkinfo
     * 当审核员编辑比分部分（eventType=1）时，需要同步创建/更新对应的时段事件的checkinfo
     * 填充比分信息（t1、t2、homeAway等），但不填充时段信息（fiveMinSection、fifteenMinSection）
     * 时段信息只有在手动编辑时段部分时才会有
     * 适用于goal、corner、fa_card三种事件类型
     * @param matchSettleEvent 比分事件（eventType=1）
     * @param editMatchSettleEventDto 编辑参数
     */
    private void ensurePeriodEventCheckInfo(MatchSettleEvent matchSettleEvent, EditMatchSettleEventDto editMatchSettleEventDto) {
        try {
            // 只处理eventType=1的事件（比分事件）
            if (matchSettleEvent.getEventType() == null || matchSettleEvent.getEventType() != 1) {
                return;
            }
            
            // eventType=3只在periodId=6L或7L时创建（加时赛41L, 42L没有eventType=3）
            Long periodId = matchSettleEvent.getPeriodId();
            if (periodId == null || (!periodId.equals(6L) && !periodId.equals(7L))) {
                return;
            }
            
            // 只处理goal、corner、fa_card事件
            String eventCode = matchSettleEvent.getEventCode();
            if (!"goal".equals(eventCode) && !"corner".equals(eventCode) && !"fa_card".equals(eventCode)) {
                return;
            }
            
            // 查找对应的时段事件（eventType=3）
            String periodSettleNum = SettleNumUtils.getTypeEventSettleNum(eventCode, matchSettleEvent.getPeriodId(), 3);
            List<MatchSettleEvent> periodEvents = matchSettleEventRepository.getByMatchIdAndSettleNumAndEventOrderAndPeriodId(
                    matchSettleEvent.getStandardMatchId(), periodSettleNum, matchSettleEvent.getEventOrder(), matchSettleEvent.getPeriodId());

            MatchSettleEvent periodEvent = periodEvents.stream()
                    .filter(e -> e.getEventType() != null && e.getEventType() == 3)
                    .findFirst()
                    .orElse(null);

            if (periodEvent == null) {
                log.warn("ensurePeriodEventCheckInfo::时段事件不存在, eventId:{}, eventCode:{}", matchSettleEvent.getId(), eventCode);
                return;
            }
            
            // 查找当前用户是否已有时段事件的checkinfo
            MatchSettleCheckInfoEntity periodCheckInfo = matchSettleCheckInfoHelper.searchCheckInfoByUser(
                    periodEvent.getId(), matchSettleEvent.getStandardMatchId(), editMatchSettleEventDto.getOperatorName());
            
            MatchSettleCheckInfoEntity periodCheckInfoBefore = new MatchSettleCheckInfoEntity();
            
            if (periodCheckInfo == null) {
                // 不存在，创建新的checkinfo
                periodCheckInfo = new MatchSettleCheckInfoEntity();
                periodCheckInfo.setId(IdWorker.getId());
                periodCheckInfo.setModifyTime(System.currentTimeMillis());
                periodCheckInfo.setCreateTime(System.currentTimeMillis());
                
                // 使用SettleCheckUtils初始化checkinfo，从时段事件复制基本信息
                SettleCheckUtils.initCheckMatchSettleEventV2(periodEvent, periodCheckInfo);
                
                // 填充比分信息（从比分事件获取）
                periodCheckInfo.setT1(matchSettleEvent.getT1());
                periodCheckInfo.setT2(matchSettleEvent.getT2());
                periodCheckInfo.setHomeAway(matchSettleEvent.getHomeAway());
                periodCheckInfo.setExtryInfo(matchSettleEvent.getExtryInfo());
                periodCheckInfo.setGoWaterStatus(matchSettleEvent.getGoWaterStatus());
                
                // 不设置时段信息（fiveMinSection、fifteenMinSection），因为时段信息只有在手动编辑时段部分时才会有
                
                periodCheckInfo.setCheckNumber(SettleCheckUtils.getCheckNumber(periodEvent.getCheckNumber()));
                periodCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
                periodCheckInfo.setUserName(editMatchSettleEventDto.getOperatorName());
                periodCheckInfo.setDataSourceCode("PA");
                
                matchSettleCheckInfoRepository.save(periodCheckInfo);
                log.info("ensurePeriodEventCheckInfo::创建时段事件checkinfo成功, periodEventId:{}, checkInfoId:{}", 
                        periodEvent.getId(), periodCheckInfo.getId());
            } else {
                // 存在，更新比分信息
                BeanUtils.copyProperties(periodCheckInfo, periodCheckInfoBefore);
                
                // 使用SettleCheckUtils初始化checkinfo，从时段事件复制基本信息
                SettleCheckUtils.initCheckMatchSettleEventV2(periodEvent, periodCheckInfo);
                
                // 填充比分信息（从比分事件获取）
                periodCheckInfo.setT1(matchSettleEvent.getT1());
                periodCheckInfo.setT2(matchSettleEvent.getT2());
                periodCheckInfo.setHomeAway(matchSettleEvent.getHomeAway());
                periodCheckInfo.setExtryInfo(matchSettleEvent.getExtryInfo());
                periodCheckInfo.setGoWaterStatus(matchSettleEvent.getGoWaterStatus());
                
                // 不更新时段信息（fiveMinSection、fifteenMinSection），因为时段信息只有在手动编辑时段部分时才会有
                
                periodCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
                periodCheckInfo.setModifyTime(System.currentTimeMillis());
                
                matchSettleCheckInfoRepository.updateById(periodCheckInfo);
                log.info("ensurePeriodEventCheckInfo::更新时段事件checkinfo成功, periodEventId:{}, checkInfoId:{}, t1:{}, t2:{}", 
                        periodEvent.getId(), periodCheckInfo.getId(), periodCheckInfo.getT1(), periodCheckInfo.getT2());
            }
        } catch (Exception e) {
            log.error("ensurePeriodEventCheckInfo::处理时段事件checkinfo失败, eventId:{}, eventCode:{}", 
                    matchSettleEvent.getId(), matchSettleEvent.getEventCode(), e);
            // 不抛出异常，避免影响主流程
        }
    }
    
    /**
     * 根据事件更新对应的时段比分
     * 当编辑进球比分时，需要同时更新对应的5/15分钟时段的比分，避免单独结算时段时比分不一致
     * @param matchSettleEvent 事件信息
     * @param editMatchSettleEventDto 编辑参数
     */
    private void updatePeriodScoresByEvent(MatchSettleEvent matchSettleEvent, EditMatchSettleEventDto editMatchSettleEventDto) {
        try {
            Long standardMatchId = matchSettleEvent.getStandardMatchId();
            String fiveMinSection = editMatchSettleEventDto.getFiveMinSection();
            String fifteenMinSection = editMatchSettleEventDto.getFifteenMinSection() != null 
                    ? editMatchSettleEventDto.getFifteenMinSection() 
                    : editMatchSettleEventDto.getFiveMinSection();
            
            List<String> settleNumsToUpdate = new ArrayList<>();
            
            // 1. 根据5分钟时段找到对应的settleNum
            if (StringUtils.isNotEmpty(fiveMinSection)) {
                try {
                    Integer fiveMin = Integer.parseInt(fiveMinSection);
                    List<String> fiveMinSettleNums = GrayIntervalService.fiveSettleNumMap.get(fiveMin);
                    if (fiveMinSettleNums != null && !fiveMinSettleNums.isEmpty()) {
                        settleNumsToUpdate.addAll(fiveMinSettleNums);
                    }
                } catch (NumberFormatException e) {
                    log.warn("editGoalEvent::无法解析5分钟时段:{}", fiveMinSection, e);
                }
            }
            
            // 2. 根据15分钟时段找到对应的settleNum
            if (StringUtils.isNotEmpty(fifteenMinSection)) {
                try {
                    // fifteenMinSection可能是字符串格式（如"60899"），需要转换为分钟数
                    Integer fifteenMin = null;
                    if (fifteenMinSection.length() == 5) {
                        // 格式如"60899"，前两位是分钟数
                        fifteenMin = Integer.parseInt(fifteenMinSection.substring(0, 2));
                    } else {
                        // 尝试直接解析为分钟数
                        fifteenMin = Integer.parseInt(fifteenMinSection);
                    }
                    
                    if (fifteenMin != null) {
                        List<String> fifteenMinSettleNums = GrayIntervalService.fifteenSettleNumMap.get(fifteenMin);
                        if (fifteenMinSettleNums != null && !fifteenMinSettleNums.isEmpty()) {
                            settleNumsToUpdate.addAll(fifteenMinSettleNums);
                        }
                    }
                } catch (NumberFormatException e) {
                    log.warn("editGoalEvent::无法解析15分钟时段:{}", fifteenMinSection, e);
                }
            }
            
            // 3. 更新对应的时段比分
            if (!settleNumsToUpdate.isEmpty()) {
                List<MatchSettleScore> periodScores = matchSettleScoreRepository.getModelBySettleNumAndMatchIdIdAndStatus(
                        settleNumsToUpdate, standardMatchId, null);
                
                if (periodScores != null && !periodScores.isEmpty()) {
                    for (MatchSettleScore periodScore : periodScores) {
                        // 重新计算该时段的所有进球事件，得到正确的比分
                        List<MatchSettleEvent> eventsInPeriod = matchSettleEventRepository.getModelByStandardMatchIdAndSettleNums(
                                standardMatchId, Arrays.asList(periodScore.getSettleNum()));
                        
                        // 过滤出进球事件
                        eventsInPeriod = eventsInPeriod.stream()
                                .filter(e -> "goal".equals(e.getEventCode()) && e.getStatus() != null && e.getStatus() != 3)
                                .collect(Collectors.toList());
                        
                        Integer homeScore = 0;
                        Integer awayScore = 0;
                        
                        for (MatchSettleEvent event : eventsInPeriod) {
                            if (StringUtils.isNotEmpty(event.getHomeAway())) {
                                if ("home".equals(event.getHomeAway())) {
                                    homeScore++;
                                } else if ("away".equals(event.getHomeAway())) {
                                    awayScore++;
                                }
                            }
                        }
                        
                        // 更新时段比分
                        if (periodScore.getT1() == null || !homeScore.equals(periodScore.getT1()) 
                                || periodScore.getT2() == null || !awayScore.equals(periodScore.getT2())) {
                            periodScore.setT1(homeScore);
                            periodScore.setT2(awayScore);
                            periodScore.setModifyTime(System.currentTimeMillis());
                            periodScore.setOperater(editMatchSettleEventDto.getOperatorName());
                            periodScore.setStatus(NOT_CONFIRM);
                            matchSettleScoreRepository.updateById(periodScore);
                            log.info("editGoalEvent::更新时段比分成功, settleNum:{}, t1:{}, t2:{}", 
                                    periodScore.getSettleNum(), homeScore, awayScore);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("editGoalEvent::更新时段比分失败, eventId:{}", matchSettleEvent.getId(), e);
            throw e;
        }
    }

    /**
     * 确认次序比分
     * @param matchSettleEventDto
     * @return
     */
    @Override
    public Response confirmMatchSettleEventV2(EditMatchSettleEventDto matchSettleEventDto) {
        log.info("confirmMatchSettleEvent New param,matchSettleEventDto: {}",matchSettleEventDto);
        //1.查询人工核对事件
        //2.检查当前的用户是否满足编辑比分的次序
        //3.更新状态
        //4.进入同一核对比分流程
        if(matchSettleEventDto.getLinkedId() == null){
            matchSettleEventDto.setLinkedId("confirmMatchSettleEventV2" + UUIdUtils.getId());
        }

        MatchSettleCheckInfoEntity matchSettleCheckInfo=  matchSettleCheckInfoHelper.searchCheckInfoByUser(matchSettleEventDto.getEventId(),
                matchSettleEventDto.getStandardMatchId(),matchSettleEventDto.getOperatorName());

        String key = CommonConstant.MATCH_SEQUENCE_SCORE_SETTLE + matchSettleEventDto.getStandardMatchId();
        if(matchServiceHelper.checkIfOverSettleTime(matchSettleEventDto.getStandardMatchId())){
            return Response.failed("1031930");
        }
        log.info("事件Id::{}:: 当前事件被确认参数:{} ",matchSettleEventDto.getEventId(),matchSettleEventDto);
        try {
            if(redisService.tryLock(key,key,2,5)) {
                MatchSettleEvent matchSettleEvent =matchSettleEventRepository.getById(matchSettleEventDto.getEventId());
                Integer checkNumber = matchSettleEvent.getCheckNumber();
                if(matchSettleEvent==null){
                    return Response.failed("1031935");
                }
                if(matchSettleCheckInfo==null){
                    return Response.failed("1031934");
                }
                MatchSettleCheckInfoEntity checkInfo =new  MatchSettleCheckInfoEntity();
                BeanUtils.copyProperties(matchSettleCheckInfo,checkInfo);
                if(matchSettleCheckInfo.getCheckStatus()!= MatchSettleCheckConstant.CheckStatus.EDIT){
                    return Response.failed("1031934");
                }

                matchSettleCheckInfo.setEventOrder(matchSettleEvent.getEventOrder());
                matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.CONFIRM);
                matchSettleCheckInfo.setModifyTime(System.currentTimeMillis());
                matchSettleCheckInfoRepository.updateById(matchSettleCheckInfo);
                //3.确认记录日志
                UpdateMatchSettleScoreDto dto =new UpdateMatchSettleScoreDto();
                BeanUtils.copyProperties(matchSettleEventDto,dto);
                dto.setSportId(matchSettleEvent.getSportId());
                matchSettleOperateLogService.matchSettleCheckEventAddLog(checkInfo, matchSettleCheckInfo,
                        dto, OperateLogTypeEnum.CONFIRM_SCORE, matchSettleEvent.getSettleNum(), checkNumber, matchSettleEvent.getEventType());
                MatchSettleTemplate countDownTemplate = matchSettleTemplateHelper.getTemplateByStandardMatchId(matchSettleEvent.getStandardMatchId(), SettleTemplateTypeEnum.COUNT_DOWEN.code);
                MatchSettleCheckInfo checkInfo1 = matchSettleCheckInfoV2Converter.convertEntityToCheckInfo(matchSettleCheckInfo);
                Map<Long, Pair<Boolean, Boolean>> isScoreOrEventDiffMap = matchSettleBatchCheckService.batchCheckCommonMatchSettleScoreEvent(Arrays.asList(Pair.of(matchSettleEvent,checkInfo1)),false,matchSettleEventDto.getLinkedId(),countDownTemplate);
                Pair<Boolean, Boolean> isScoreOrEventDiff = isScoreOrEventDiffMap.get(matchSettleEvent.getId());
                if (isScoreOrEventDiff == null) {
                    return Response.failed("接口调用-主流程赛事id没找到");
                }
                matchSettleInfoHelper.updateMatchGrayStatus(matchSettleCheckInfo.getStandardMatchId());

                if(matchSettleEvent.getStatus()!=3&& isScoreOrEventDiff.getLeft()){
                    //1031947=比分不一致需要下个审核员审核
                    return Response.failed("1031947");
                }
                return Response.success();
            }else {
                return Response.failed("1031933");
            }
        }catch (Exception e){
            log.error("FootballNewMatchScoresSettleApiImpl-confirmMatchSettleEvent:",e);
            return Response.failed();
        }finally {
            redisService.unLock(key,key);
        }
    }

    /**
     * 编辑点球大战比分
     * @param settleScoreSearchDto
     * @return
     */
    @Override
    public Response setPenaltyScoresV2(EditMatchSettleEventDto settleScoreSearchDto) {
        log.info("setPenaltyScores New param,settleScoreSearchDto: {}",settleScoreSearchDto);
        String key ="MATCH_SETTLE_INFO:"+ settleScoreSearchDto.getEventId();
        if(matchServiceHelper.checkIfOverSettleTime(settleScoreSearchDto.getStandardMatchId())){
            return Response.failed("1031930");
        }
        try {
            if(redisService.tryLock(key,key,2,5)) {
                //0.查询当前编辑用户的比分核对记录 如果没有则新增
                MatchSettleCheckInfoEntity matchSettleCheckInfo=  matchSettleCheckInfoHelper.searchCheckInfoByUser(settleScoreSearchDto.getEventId(),settleScoreSearchDto.getStandardMatchId(),
                        settleScoreSearchDto.getOperatorName());
                //0.01.判断谁先射门是否已经结算如果没结算则直接返回失败
                if(!isTeamFirstSettled(settleScoreSearchDto.getStandardMatchId())){
                    return Response.failed("1031952");
                }
                MatchSettleCheckInfoEntity checkInfo =new  MatchSettleCheckInfoEntity();
                //0.1 如果已经确认则无法更改
                if(matchSettleCheckInfo!=null){
                    BeanUtils.copyProperties(matchSettleCheckInfo,checkInfo);
                    if(matchSettleCheckInfo.getCheckStatus()== MatchSettleCheckConstant.CheckStatus.CONFIRM){
                        //错误编码还没编辑 该用户已经确认比分之类的
                        return Response.failed("1031933");
                    }
                }
                //0.2 查询当前的事件id是否存在不存在则返回失败
                MatchSettleEvent matchSettleEvent =matchSettleEventRepository.getById(settleScoreSearchDto.getEventId());
                if(matchSettleEvent==null){
                    return Response.failed("1031935");
                }
                //0.3 查询当前的事件id是否结算如果结算则返回失败
                if(matchSettleEvent.getStatus()==3){
                    return Response.failed("1031939");
                }
                Integer checkNumber = matchSettleEvent.getCheckNumber();
                //0.4 如果matchSettleCheckInfo is null 则新增
                if(matchSettleCheckInfo==null){
                    matchSettleCheckInfo =new MatchSettleCheckInfoEntity();
                    BeanUtils.copyProperties(matchSettleCheckInfo,checkInfo);
                    matchSettleCheckInfo =  SettleCheckUtils.initCheckPaniltyEventV2(matchSettleEvent,matchSettleCheckInfo);
                    matchSettleCheckInfo.setId(IdWorker.getId());
                    matchSettleCheckInfo.setCreateTime(System.currentTimeMillis());
                    matchSettleCheckInfo.setModifyTime(System.currentTimeMillis());
                    matchSettleCheckInfo.setCheckNumber( SettleCheckUtils.getCheckNumber(matchSettleEvent.getCheckNumber()));
                    matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
                    matchSettleCheckInfo.setUserName(settleScoreSearchDto.getOperatorName());
                    matchSettleCheckInfo.setGoWaterStatus(settleScoreSearchDto.getGoWaterStatus());
                    matchSettleCheckInfo.setDataSourceCode("PA");
                    matchSettleCheckInfoRepository.save(matchSettleCheckInfo);
                }
                //1.判断是否次序事件 如果是则计算次序事件比分
                if(matchSettleEvent.getSettleNum().equals("1030")){
                    boolean isCanCount= countPenaltyScores(settleScoreSearchDto,matchSettleEvent);
                    if(!isCanCount){
                        return Response.failed("1031937");
                    }
                    matchSettleCheckInfo.setT1(matchSettleEvent.getT1());
                    matchSettleCheckInfo.setT2(matchSettleEvent.getT2());
                    matchSettleCheckInfo.setExtryInfo(settleScoreSearchDto.getExtryInfo());
                }else {
                    //2.如果不是次序事件 则直接编辑比分
                    matchSettleCheckInfo.setT1(settleScoreSearchDto.getT1());
                    matchSettleCheckInfo.setT2(settleScoreSearchDto.getT2());
                    matchSettleCheckInfo.setExtryInfo(settleScoreSearchDto.getExtryInfo());
                }
                if(settleScoreSearchDto.getGoWaterStatus()!=null&&settleScoreSearchDto.getGoWaterStatus()==1){
                    matchSettleCheckInfo.setGoWaterStatus(1);
                }else {
                    matchSettleCheckInfo.setGoWaterStatus(0);
                }
                matchSettleCheckInfo.setHomeAway(matchSettleEvent.getHomeAway());
                matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
                matchSettleCheckInfoRepository.updateById(matchSettleCheckInfo);
                //  日志录入
                matchSettleCheckInfo.setEventOrder(matchSettleEvent.getEventOrder());
                UpdateMatchSettleScoreDto dto =new UpdateMatchSettleScoreDto();
                BeanUtils.copyProperties(settleScoreSearchDto,dto);
                dto.setSportId(matchSettleEvent.getSportId());
                matchSettleOperateLogService.matchSettleCheckEventAddLog(checkInfo, matchSettleCheckInfo,
                        dto, OperateLogTypeEnum.EDIT, matchSettleEvent.getSettleNum(), checkNumber, matchSettleEvent.getEventType());

                return Response.success();
            }else {
                return Response.failed("1031933");
            }
        }catch (Exception e){
            log.error("FootballNewMatchScoresSettleApiImpl-setPenaltyScores:",e);
            return Response.failed();
        }finally {
            redisService.unLock(key,key);
        }
    }

    /**
     * 确认点球大战比分
     * @param matchSettleEventDto
     * @return
     */
    @Override
    public Response confirmPenaltyScoresV2(EditMatchSettleEventDto matchSettleEventDto) {
        log.info("confirmPenaltyScores New param,matchSettleEventDto: {}",matchSettleEventDto);
        String key ="MATCH_SETTLE_INFO:"+ matchSettleEventDto.getEventId();
        if(matchServiceHelper.checkIfOverSettleTime(matchSettleEventDto.getStandardMatchId())){
            return Response.failed("1031930");
        }
        log.info("事件Id::{}:: 当前事件被确认参数:{} ",matchSettleEventDto.getEventId(),matchSettleEventDto);
        try {
            if(redisService.tryLock(key,key,2,5)) {
                MatchSettleEvent matchSettleEvent =matchSettleEventRepository.getById(matchSettleEventDto.getEventId());
                if(matchSettleEvent==null){
                    return Response.failed("1031935");
                }
                if(matchSettleEvent.getStatus()!=NOT_CONFIRM){
                    return Response.failed("1031934");
                }
                matchSettleEvent.setModifyTime(System.currentTimeMillis());
                matchSettleEvent.setStatus(CONFIRM);
                matchSettleEvent.setHomeAway(matchSettleEventDto.getHomeAway());
                matchSettleEvent.setT1(matchSettleEventDto.getT1());
                matchSettleEvent.setT2(matchSettleEventDto.getT2());
                matchSettleEvent.setPlayerNameCode(matchSettleEventDto.getPlayerNameCode());
                matchSettleEvent.setExtryInfo(matchSettleEventDto.getExtryInfo());
                matchSettleEventRepository.updateById(matchSettleEvent);
                //2.确认记录日志
                matchSettleOperateLogService.matchSettleEventAddLog(matchSettleEvent,matchSettleEventDto.getOperatorName(),
                        OperateLogTypeEnum.CONFIRM_SCORE.getCode().toString(),"",matchSettleEventDto.getIpAddress());

                //3.返回查询事件列表
                wsPushService.pushStandardSettleEvent(matchSettleEventDto.getStandardMatchId(),
                        matchSettleEventDto.getEventCode());
                return Response.success();
            }else {
                return Response.failed("1031933");
            }
        }catch (Exception e){
            log.error("FootballNewMatchScoresSettleApiImpl-confirmPenaltyScores:",e);
            return Response.failed();
        }finally {
            redisService.unLock(key,key);
        }
    }

    /**
     * 检查赛事锁定
     * @param standardMatchId
     * @param userName
     * @return
     */
    @Override
    public boolean isLockedByMatchSettleV2(Long standardMatchId, String userName) {
        MatchSettleInfoEntity matchSettleInfo =matchSettleInfoRepository.getMatchSettleInfo(standardMatchId);
        if (matchSettleInfo != null) {

            String arrayStr =matchSettleInfo.getLimitUserArray();
            if(StringUtils.isEmpty(arrayStr)){
                return false;
            }else {
                JSONArray array =JSONArray.parseArray(arrayStr);
                for (Object o : array) {
                    String userOne =o.toString();
                    if(userName.equals(userOne)){
                        return true;
                    }
                }
            }
            return false;
        }else {
            return false;
        }
    }

    @Override
    public Response confirmMatchSettlePlayerAndMethodV2(EditMatchSettleEventDto matchSettleEventDto) {
        log.info("confirmMatchSettlePlayerAndMethod New param ,matchSettleEventDto: {}",matchSettleEventDto);
        String key ="MATCH_SETTLE_INFO:"+ matchSettleEventDto.getEventId();
        if(matchServiceHelper.checkIfOverSettleTime(matchSettleEventDto.getStandardMatchId())){
            return Response.failed("1031930");
        }
        log.info("事件Id::{}:: 当前事件被确认参数:{} ",matchSettleEventDto.getEventId(),matchSettleEventDto);
        try {
            if(redisService.tryLock(key,key,2,5)) {
                MatchSettleEvent matchSettleEvent =matchSettleEventRepository.getById(matchSettleEventDto.getEventId());
                if(matchSettleEvent==null){
                    return Response.failed("1031935");
                }
                if(matchSettleEvent.getStatus()!=NOT_CONFIRM){
                    return Response.failed("1031934");
                }
                matchSettleEvent.setT2(matchSettleEventDto.getT2());
                matchSettleEvent.setT1(matchSettleEventDto.getT1());
                matchSettleEvent.setPlayerNameCode(matchSettleEventDto.getPlayerNameCode());
                matchSettleEvent.setExtryInfo(matchSettleEventDto.getExtryInfo());
                matchSettleEvent.setModifyTime(System.currentTimeMillis());
                matchSettleEvent.setStatus(CONFIRM);
                matchSettleEvent.setHomeAway(matchSettleEventDto.getHomeAway());
                matchSettleEventRepository.updateById(matchSettleEvent);
                //2.确认记录日志
                matchSettleOperateLogService.matchSettleEventAddLog(matchSettleEvent,matchSettleEventDto.getOperatorName(),
                        OperateLogTypeEnum.CONFIRM_SCORE.getCode().toString(),"",matchSettleEventDto.getIpAddress());


                //由于此时第X进球事件未核对，则结算事件表中无数据，原ws推送的数据缺失
                //若confirm的事件为进球方式和球员，则推送赛事列表的ws，以便前端能刷新最新数据
                if(matchSettleEvent.getEventType() == 2l && "goal".equals(matchSettleEventDto.getEventCode())){
                    wsPushService.pushSettleMatchList(new MatchListSettleDto(matchSettleEventDto.getStandardMatchId(), matchSettleEventDto.getEventCode(),null,null,4));
                }else {
                    wsPushService.pushStandardSettleEvent(matchSettleEventDto.getStandardMatchId(),
                            matchSettleEventDto.getEventCode());
                }
                return Response.success();
            }else {
                return Response.failed("1031933");
            }
        }catch (Exception e){
            log.error("FootballNewMatchScoresSettleApiImpl-confirmMatchSettlePlayerAndMethod:",e);
            return Response.failed();
        }finally {
            redisService.unLock(key,key);
        }
    }

    /**
     * 结算球员玩法
     * @param matchSettleScoreDto
     * @return
     */
    @Override
    public Response settleMatchSettlePlayerAndMethodV2(EditMatchSettleEventDto matchSettleScoreDto) {
        log.info("settleMatchSettlePlayerAndMethod New param,matchSettleScoreDto: {}",matchSettleScoreDto);
        String key ="MATCH_SETTLE_INFO:"+ matchSettleScoreDto.getEventId();
        if(matchServiceHelper.checkIfOverSettleTime(matchSettleScoreDto.getStandardMatchId())){
            return Response.failed("1031930");
        }
        try {
            if(redisService.tryLock(key,key,2,5)) {
                MatchSettleEvent matchSettleEvent =matchSettleEventRepository.getById(matchSettleScoreDto.getEventId());
                if(matchSettleEvent==null){
                    return Response.failed("1031935");
                }
                if(matchSettleEvent.getStatus()!=CONFIRM){
                    return Response.failed("1031936");
                }
                Integer settleTimes =matchSettleEvent.getSettleTimes();
                Integer settleCount = matchSettleEvent.getSettleCount();
                if(settleTimes==null){
                    settleTimes=0;
                }
                if (settleCount == null ) {
                    matchSettleEvent.setSettleCount(0);
                }
                settleTimes++;

                //二次结算,必须给出结算原因
                if ( matchSettleEvent.getSettleCount() >  0 &&
                        (matchSettleScoreDto.getSettleReason()==null  ||
                                matchSettleScoreDto.getSettleReason()== 0) ) {
                    return Response.failed("1031953");
                }

                String  before= "-";
                Integer settleReason = matchSettleEvent.getSettleReason();
                if (settleReason != null &&  settleReason != 0 ) {
                    before = settleReason.toString();
                    if (settleReason == 118) {
                        before += ": "+matchSettleEvent.getSettleReasonDetail();
                    }
                }

                matchSettleEvent.setStatus(SETTLED);
                matchSettleEvent.setSettleCount(matchSettleEvent.getSettleCount()+1);
                matchSettleEvent.setModifyTime(System.currentTimeMillis());
                matchSettleEvent.setSettleTimes(settleTimes);
                matchSettleEvent.setOperater(matchSettleScoreDto.getOperatorName());
                matchSettleEvent.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.SETTLE);
                matchSettleEvent.setUserid(matchSettleScoreDto.getOperatorId());
                matchSettleEvent.setSettleReason(matchSettleScoreDto.getSettleReason());
                matchSettleEvent.setSettleReasonDetail(matchSettleScoreDto.getSettleReasonDetail());
                matchSettleEventRepository.updateById(matchSettleEvent);
                log.info("比分Id::{}:: 当前事件被结算参数:{} ",matchSettleScoreDto.getEventId(),matchSettleEvent);
                //结算时把回滚订单数清零
                matchServiceHelper.settleRollBackSetNullOrderCount(matchSettleEvent.getId());
                //1.日志
                matchSettleOperateLogService.matchSettleEventAddLog(matchSettleEvent,
                        matchSettleScoreDto.getOperatorName(),OperateLogTypeEnum.SCORE_SETTLE.getCode().toString()
                        ,before,matchSettleScoreDto.getIpAddress());

                //2.MQ下发
                matchSettleScoresProducer.sendMatchSettleEvent(matchSettleEvent);
                wsPushService.pushStandardSettleEvent(matchSettleScoreDto.getStandardMatchId(),
                        matchSettleScoreDto.getEventCode());
                return Response.success();
            }else {
                return Response.failed("1031933");
            }
        }catch (Exception e){
            log.error("FootballNewMatchScoresSettleApiImpl-settleMatchSettlePlayerAndMethod:",e);
            return Response.failed();
        }finally {
            redisService.unLock(key,key);
        }
    }

    /**
     * 结算点球大战获胜队伍
     * @param matchSettleScoreDto
     * @return
     */
    @Override
    public Response setPenaltyTeamFirstV2(EditMatchSettleEventDto matchSettleScoreDto) {
        log.info("setPenaltyTeamFirst New param,matchSettleScoreDto: {}",matchSettleScoreDto);
        String key ="MATCH_SETTLE_INFO:"+ matchSettleScoreDto.getEventId();
        //1.查询是否已经存在人工核对事件
        try {
            //3.更新或者新增
            if(redisService.tryLock(key,key,2,5)) {
                MatchSettleCheckInfoEntity matchSettleCheckInfo=  matchSettleCheckInfoHelper.searchCheckInfoByUser(matchSettleScoreDto.getEventId(),matchSettleScoreDto.getStandardMatchId(),
                        matchSettleScoreDto.getOperatorName());

                MatchSettleCheckInfoEntity checkInfo =new  MatchSettleCheckInfoEntity();

                if(matchSettleCheckInfo!=null){
                    if(matchSettleCheckInfo.getCheckStatus()== MatchSettleCheckConstant.CheckStatus.CONFIRM){
                        //错误编码还没编辑 该用户已经确认比分之类的
                        return Response.failed("1031933");
                    }
                }
                MatchSettleEvent matchSettleEvent =matchSettleEventRepository.getById(matchSettleScoreDto.getEventId());
                Integer checkNumber = matchSettleEvent.getCheckNumber();
                if(matchSettleEvent==null){
                    return Response.failed("1031935");
                }
                if(StringUtils.isEmpty(matchSettleScoreDto.getHomeAway())){
                    return Response.failed("1031939");
                }
                //结算后不能编辑
                if(matchSettleEvent.getStatus()==3){
                    return Response.failed("1031939");
                }
                if(matchSettleCheckInfo==null){
                    matchSettleCheckInfo =new MatchSettleCheckInfoEntity();
                    BeanUtils.copyProperties(matchSettleCheckInfo,checkInfo);
                    matchSettleCheckInfo.setId(IdWorker.getId());
                    matchSettleCheckInfo.setModifyTime(System.currentTimeMillis());
                    SettleCheckUtils.initCheckMatchSettleEventV2(matchSettleEvent,matchSettleCheckInfo);
                    //得到当前用户的次序
                    matchSettleCheckInfo.setCheckNumber( SettleCheckUtils.getCheckNumber(matchSettleEvent.getCheckNumber()));
                    matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
                    if (!StringUtils.isAnyEmpty(matchSettleEvent.getOperater()) && (matchSettleEvent.getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || matchSettleEvent.getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode()))){
                        matchSettleCheckInfo.setUserName(matchSettleEvent.getOperater() + "(" + checkInfo.getDataSourceCode() +")");
                    }else {
                        matchSettleCheckInfo.setUserName(matchSettleScoreDto.getOperatorName());
                    }
                    matchSettleCheckInfo.setHomeAway(matchSettleScoreDto.getHomeAway());
                    matchSettleCheckInfo.setGoWaterStatus(0);
                    matchSettleCheckInfo.setDataSourceCode("PA");
                    matchSettleCheckInfoRepository.save(matchSettleCheckInfo);
                }else {
                    BeanUtils.copyProperties(matchSettleCheckInfo,checkInfo);
                    SettleCheckUtils.initCheckMatchSettleEventV2(matchSettleEvent,matchSettleCheckInfo);
                    if (!StringUtils.isAnyEmpty(matchSettleEvent.getOperater()) && (matchSettleEvent.getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || matchSettleEvent.getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode()))){
                        matchSettleCheckInfo.setUserName(matchSettleEvent.getOperater() + "(" + checkInfo.getDataSourceCode() +")");
                    }else {
                        matchSettleCheckInfo.setUserName(matchSettleScoreDto.getOperatorName());
                    }
                    matchSettleCheckInfo.setFirstT1(0);
                    matchSettleCheckInfo.setFirstT2(0);
                    matchSettleCheckInfo.setSecondT1(0);
                    matchSettleCheckInfo.setSecondT2(0);
                    matchSettleCheckInfo.setGoWaterStatus(0);
                    matchSettleCheckInfo.setCreateTime(System.currentTimeMillis());
                    matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
                    matchSettleCheckInfo.setHomeAway(matchSettleScoreDto.getHomeAway());
                    matchSettleCheckInfo.setModifyTime(System.currentTimeMillis());
                    matchSettleCheckInfoRepository.updateById(matchSettleCheckInfo);
                }
                //  日志录入
                UpdateMatchSettleScoreDto dto =new UpdateMatchSettleScoreDto();
                BeanUtils.copyProperties(matchSettleScoreDto,dto);
                dto.setSportId(matchSettleEvent.getSportId());
                matchSettleOperateLogService.matchSettleCheckEventAddLog(checkInfo, matchSettleCheckInfo,
                        dto, OperateLogTypeEnum.SCORES_SETTLE_10040,matchSettleEvent.getSettleNum(),checkNumber, matchSettleEvent.getEventType()) ;

                return Response.success();
            }else {
                return Response.failed("1031933");
            }
        }catch (Exception e){
            log.error("FootballNewMatchScoresSettleApiImpl-setPenaltyTeamFirst:",e);
            return Response.failed();
        }finally {
            redisService.unLock(key,key);
        }
    }

    @Override
    public Response settlePenaltyTeamFirstV2(EditMatchSettleEventDto matchSettleEventDto) {
        log.info("settlePenaltyTeamFirst New param,matchSettleEventDto: {}",matchSettleEventDto);
        String key ="MATCH_SETTLE_INFO:"+ matchSettleEventDto.getEventId();
        //1.查询是否已经存在人工核对事件
        try {
            //3.更新或者新增
            if(redisService.tryLock(key,key,2,5)) {
                MatchSettleEvent matchSettleEvent =matchSettleEventRepository.getById(matchSettleEventDto.getEventId());
                if(matchSettleEvent==null){
                    return Response.failed("1031935");
                }
                if(StringUtils.isEmpty(matchSettleEventDto.getHomeAway())){
                    return Response.failed("1031939");
                }
                //结算后不能编辑
                if(matchSettleEvent.getStatus()==3){
                    return Response.failed("1031939");
                }
                String  before= "-";
                Integer settleReason = matchSettleEvent.getSettleReason();
                if (settleReason != null &&  settleReason != 0 ) {
                    before = settleReason.toString();
                    if (settleReason == 118) {
                        before += ": "+matchSettleEvent.getSettleReasonDetail();
                    }
                }

                matchSettleEvent.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.SETTLE);
                matchSettleEvent.setSettleCount(matchSettleEvent.getSettleCount()== null ? 1 : matchSettleEvent.getSettleCount()+1);
                matchSettleEvent.setModifyTime(System.currentTimeMillis());
                matchSettleEvent.setStatus(SETTLED);
                matchSettleEventRepository.updateById(matchSettleEvent);
                //结算时把回滚订单数清零
                matchServiceHelper.settleRollBackSetNullOrderCount(matchSettleEvent.getId());
                //只有一次结算会走这里
                matchSettleEvent.setSettleTimes(1);
                matchSettleCheckInfoHelper.settlePenaltyTeamFirst(matchSettleEvent);
                //TODO  日志录入
                matchSettleOperateLogService.matchSettleEventAddLog(matchSettleEvent,matchSettleEventDto.getOperatorName(),
                        OperateLogTypeEnum.SCORES_SETTLE_10041.getCode().toString(),before,matchSettleEventDto.getIpAddress());


                wsPushService.pushStandardSettleScores(matchSettleEvent.getStandardMatchId(),"goal");
                wsPushService.pushSettleMatchList(new MatchListSettleDto(matchSettleEvent.getStandardMatchId()
                        , "goal",null,matchSettleEvent.getId(),2));
                return Response.success();
            }else {
                return Response.failed("1031933");
            }
        }catch (Exception e){
            log.error("FootballNewMatchScoresSettleApiImpl-settlePenaltyTeamFirst:",e);
            return Response.failed();
        }finally {
            redisService.unLock(key,key);
        }
    }

    @Override
    public Response setPenaltyTeamFirstHighLV2(EditMatchSettleEventDto matchSettleScoreDto) {
        log.info("setPenaltyTeamFirstHighL New param,matchSettleScoreDto: {}",matchSettleScoreDto);
        String key ="MATCH_SETTLE_INFO:"+ matchSettleScoreDto.getEventId();
        //1.查询是否已经存在人工核对事件
        try {
            //3.更新或者新增
            if(redisService.tryLock(key,key,2,5)) {
                MatchSettleEvent matchSettleEvent =matchSettleEventRepository.getById(matchSettleScoreDto.getEventId());
                MatchSettleEvent eventOid = new MatchSettleEvent();
                BeanUtils.copyProperties(matchSettleEvent,eventOid);
                if(matchSettleEvent==null){
                    return Response.failed("1031935");
                }
                if(StringUtils.isEmpty(matchSettleScoreDto.getHomeAway())){
                    return Response.failed("1031939");
                }
                String  before= "-";
                Integer settleReason = matchSettleEvent.getSettleReason();
                if (settleReason != null &&  settleReason != 0 ) {
                    before = settleReason.toString();
                    if (settleReason == 118) {
                        before += ": "+matchSettleEvent.getSettleReasonDetail();
                    }
                }


                matchSettleEvent.setStatus(1);
                matchSettleEvent.setHomeAway(matchSettleScoreDto.getHomeAway());
                matchSettleEvent.setModifyTime(System.currentTimeMillis());
                matchSettleEventRepository.updateById(matchSettleEvent);


                //  日志录入
                matchSettleOperateLogService.matchSettleEventAddLog(eventOid,matchSettleEvent,matchSettleScoreDto.getOperatorName(),
                        OperateLogTypeEnum.SCORES_SETTLE_10042,matchSettleScoreDto.getIpAddress());


                return Response.success();
            }else {
                return Response.failed("1031933");
            }
        }catch (Exception e){
            log.error("FootballNewMatchScoresSettleApiImpl-setPenaltyTeamFirstHighL:",e);
            return Response.failed();
        }finally {
            redisService.unLock(key,key);
        }
    }

    /**
     * 取消删除事件按钮
     * @param matchSettleSwitcherDto
     * @return
     */
    @Override
    public Response cancelDeleteStatusV2(MatchSettleSwitcherDto matchSettleSwitcherDto) {
        log.info("cancelDeleteStatus New param,matchSettleSwitcherDto: {}",matchSettleSwitcherDto);
        String key ="MATCH_SETTLE_INFO:"+ matchSettleSwitcherDto.getMatchId();
        //1.查询是否已经存在人工核对事件
        try {
            //3.更新或者新增
            if(redisService.tryLock(key,key,2,5)) {
                if(matchSettleSwitcherDto.getMatchScoreId()!=null && matchSettleSwitcherDto.getMatchScoreId()>0L){
                    MatchSettleScore settleScore = matchSettleScoreRepository.getById(matchSettleSwitcherDto.getMatchScoreId());
                    if(settleScore!=null){
                        settleScore.setHasDeleteEvent(0);
                        settleScore.setCurrentEventStatus(settleScore.getIsGrey());
                        matchSettleScoreRepository.updateById(settleScore);
                        MatchListSettleDto matchListSettleDto =new MatchListSettleDto();
                        matchListSettleDto.setStandardMatchId(matchSettleSwitcherDto.getMatchId());
                        matchListSettleDto.setEventCode(settleScore.getEventCode());
                        wsPushService.pushSettleMatchList(matchListSettleDto);
                        matchSettleOperateLogService.deleteSettleAlertLog(settleScore,matchSettleSwitcherDto);
                    }else {
                        MatchSettleEvent settleEvent = matchSettleEventRepository.getById(matchSettleSwitcherDto.getMatchScoreId());
                        if(settleEvent!=null){
                            settleEvent.setHasDeleteEvent(0);
                            settleEvent.setCurrentEventStatus(settleEvent.getIsGrey());
                            matchSettleEventRepository.updateById(settleEvent);
                            MatchListSettleDto matchListSettleDto =new MatchListSettleDto();
                            matchListSettleDto.setStandardMatchId(matchSettleSwitcherDto.getMatchId());
                            matchListSettleDto.setEventCode(settleEvent.getEventCode());
                            wsPushService.pushSettleMatchList(matchListSettleDto);
                            matchSettleOperateLogService.deleteSettleAlertLog(settleEvent,matchSettleSwitcherDto);
                        }
                    }
                }
                matchSettleInfoHelper.updateMatchCurrentEventStatus(matchSettleSwitcherDto.getMatchId());
                return Response.success();
            }else {
                return Response.failed("1031933");
            }
        }catch (Exception e){
            log.error("FootballNewMatchScoresSettleApiImpl-cancelDeleteStatus:",e);
            return Response.failed();
        }finally {
            redisService.unLock(key,key);
        }
    }

    @Override
    public Response getPlayerCancelDeleteStatusV2(Long standardMatchId) {
        log.info("getPlayerCancelDeleteStatus New param,standardMatchId: {}",standardMatchId);
        List<String> eventCodeFa= new ArrayList<>();
        eventCodeFa.add("fa_card"); eventCodeFa.add("yellow_card"); eventCodeFa.add("red_card");
        PlayerDeleteStatus playerDeleteStatus =new PlayerDeleteStatus();
        playerDeleteStatus.setStandardMatchId(standardMatchId);
        //进球查询
        int deleteGoal=0;
        int grayGoal=0;
        MatchSettleEventExample goalEvent=new MatchSettleEventExample();
        goalEvent.createCriteria().andStandardMatchIdEqualTo(standardMatchId).andEventCodeEqualTo("goal").andEventTypeEqualTo(1).andStatusNotEqualTo(SETTLED);
        List<MatchSettleEventEntity> goalEventList =matchSettleEventRepository.getByMatchIdAndEventCodeAndEventTypeAndNotStatus(standardMatchId,"goal",1,SETTLED);
        for (MatchSettleEventEntity matchSettleEvent : goalEventList) {
            if(matchSettleEvent.getIsGrey()!=null&&matchSettleEvent.getIsGrey()==1){
                grayGoal=1;
            }
            if(matchSettleEvent.getHasDeleteEvent()!=null&&matchSettleEvent.getHasDeleteEvent()==1){
                deleteGoal=1;
            }
        }
        MatchSettleScoreExample goalScoreExa=new MatchSettleScoreExample();
        goalScoreExa.createCriteria().andStandardMatchIdEqualTo(standardMatchId).andEventCodeEqualTo("goal").andStatusNotEqualTo(SETTLED);
        List< MatchSettleScore> goalScoreList = matchSettleScoreRepository.getByMatchIdAndEventCodeAndNotStatus(standardMatchId,Arrays.asList("goal"),SETTLED);
        for (MatchSettleScore matchSettleScore : goalScoreList) {
            if(matchSettleScore.getIsGrey()!=null&&matchSettleScore.getIsGrey()==1){
                grayGoal=1;
            }
            if(matchSettleScore.getHasDeleteEvent()!=null&&matchSettleScore.getHasDeleteEvent()==1){
                deleteGoal=1;
            }
        }
        if(deleteGoal!=0){
            //删除是2
            playerDeleteStatus.setGoalCurrentEventStatus(2);
        }else if(grayGoal!=0){
            playerDeleteStatus.setGoalCurrentEventStatus(1);
        }else {
            playerDeleteStatus.setGoalCurrentEventStatus(0);
        }
        //角球查询

        int grayCorner=0;
        int deleteCorner=0;
        List<MatchSettleEventEntity> cornerEventList =matchSettleEventRepository.getByMatchIdAndEventCodeAndEventTypeAndNotStatus(standardMatchId,"corner",1,SETTLED);
        for (MatchSettleEventEntity matchSettleEvent : cornerEventList) {
            if(matchSettleEvent.getIsGrey()!=null&&matchSettleEvent.getIsGrey()==1){
                grayCorner=1;
            }
            if(matchSettleEvent.getHasDeleteEvent()!=null&&matchSettleEvent.getHasDeleteEvent()==1){
                deleteCorner=1;
            }
        }

        MatchSettleScoreExample cornerScoreExa=new MatchSettleScoreExample();
        cornerScoreExa.createCriteria().andStandardMatchIdEqualTo(standardMatchId).andEventCodeEqualTo("corner").andStatusNotEqualTo(SETTLED);
        List< MatchSettleScore> cornerScoreList = matchSettleScoreRepository.getByMatchIdAndEventCodeAndNotStatus(standardMatchId,Arrays.asList("corner"),SETTLED);

        for (MatchSettleScore matchSettleScore : cornerScoreList) {
            if(matchSettleScore.getIsGrey()!=null&&matchSettleScore.getIsGrey()==1){
                grayCorner=1;
            }
            if(matchSettleScore.getHasDeleteEvent()!=null&&matchSettleScore.getHasDeleteEvent()==1){
                deleteCorner=1;
            }
        }

        if(deleteCorner!=0){
            //删除是2
            playerDeleteStatus.setCornerCurrentEventStatus(2);
        }else if(grayCorner!=0){
            playerDeleteStatus.setCornerCurrentEventStatus(1);
        }else {
            playerDeleteStatus.setCornerCurrentEventStatus(0);
        }
        //罚牌查询
        int grayFa=0;
        int deleteFa=0;
        List<MatchSettleEventEntity> faEventList =matchSettleEventRepository.getByMatchIdAndEventCodesAndEventTypeAndNotStatus(standardMatchId,eventCodeFa,1,SETTLED);
        for (MatchSettleEventEntity matchSettleEvent : faEventList) {
            if(matchSettleEvent.getIsGrey()!=null&&matchSettleEvent.getIsGrey()==1){
                grayFa=1;
            }
            if(matchSettleEvent.getHasDeleteEvent()!=null&&matchSettleEvent.getHasDeleteEvent()==1){
                deleteFa=1;
            }
        }
        List< MatchSettleScore> faScoreList = matchSettleScoreRepository.getByMatchIdAndEventCodeAndNotStatus(standardMatchId,eventCodeFa,SETTLED);

        for (MatchSettleScore matchSettleScore : faScoreList) {
            if(matchSettleScore.getIsGrey()!=null&&matchSettleScore.getIsGrey()==1){
                grayFa=1;
            }
            if(matchSettleScore.getHasDeleteEvent()!=null&&matchSettleScore.getHasDeleteEvent()==1){
                deleteFa=1;
            }
        }
        if(deleteFa!=0){
            //删除是2
            playerDeleteStatus.setFacardCurrentEventStatus(2);
        }else if(grayFa!=0){
            playerDeleteStatus.setFacardCurrentEventStatus(1);
        }else {
            playerDeleteStatus.setFacardCurrentEventStatus(0);
        }
        return Response.success(playerDeleteStatus) ;
    }

    /**
     * 获取赛事标记
     * @param mentionQueryRequest
     * @return
     */
    @Override
    public Response<AbstractMentionQueryDto> getSettleEventMentionStatusV2(MentionQueryRequest mentionQueryRequest) {
        try {
            if(mentionQueryRequest.getMentionType() == 0) {
                Map<String, AbstractMentionStatus> mentionStatusMap = matchServiceHelper.getAllMentionStatus(mentionQueryRequest);
                log.info("getSettleEventMentionStatusV2 matchIds:{} mentionStatusMap:{}", mentionQueryRequest.getMatchId(), mentionStatusMap);
                if (mentionQueryRequest.getSportId() == 1L) {
                    MentionQueryDto mentionQueryDto = new MentionQueryDto();
                    if (!MapUtils.isEmpty(mentionStatusMap)) {
                        for (String v : mentionStatusMap.keySet()) {
                            MentionQueryDto.FootballMentionStatus subMentionStatus = settleMentionConverter.convertFootballMentionStatus((FootballMentionStatus)mentionStatusMap.get(v));
                            log.info("getSettleEventMentionStatusV2 matchIds:{} subMentionStatus:{}", mentionQueryRequest.getMatchId(), subMentionStatus);
                            switch (v){
                                case "deleteStatus":
                                    mentionQueryDto.setDeleteStatus(subMentionStatus);
                                    break;
                                case "dataMismatchStatus":
                                    mentionQueryDto.setDataMismatchStatus(subMentionStatus);
                                    break;
                            }
                        }
                        if (mentionQueryRequest.getMentionDetail() == 0) {
                            mentionQueryDto.setDetailNull();
                        }
                    }
                    return Response.success(mentionQueryDto);
                } else {
                    BasketballMentionQueryDto response = new BasketballMentionQueryDto();
                    if (!MapUtils.isEmpty(mentionStatusMap)) {
                        for (String v : mentionStatusMap.keySet()) {
                            BasketballMentionQueryDto.BasketballMentionStatus mentionStatus = settleMentionConverter.convertBasketballMentionStatus((BasketballMentionStatus)mentionStatusMap.get(v));
                            switch (v){
                                case "dataMismatchStatus":
                                    response.setDataMismatchStatus(mentionStatus);
                                    break;
                            }
                        }
                        if (mentionQueryRequest.getMentionDetail() == 0) {
                            response.setDetailNull();
                        }
                    }
                    return Response.success(response);
                }
            } else {
                AbstractMentionStatus mentionDto = matchServiceHelper.getFootballMentionStatus(mentionQueryRequest);
                if(mentionQueryRequest.getSportId() == 1L) {
                    MentionQueryDto response= new MentionQueryDto();
                    FootballMentionStatus footballMentionStatus = (FootballMentionStatus) mentionDto;
                    MentionQueryDto.FootballMentionStatus subMentionStatus = settleMentionConverter.convertFootballMentionStatus(footballMentionStatus);
                    if (mentionQueryRequest.getMentionType() == 1) {
                        response.setDeleteStatus(subMentionStatus);
                    } else if (mentionQueryRequest.getMentionType() == 2) {
                        response.setDataMismatchStatus(subMentionStatus);
                    }
                    return Response.success(response);
                } else {
                    BasketballMentionQueryDto response= new BasketballMentionQueryDto();
                    BasketballMentionStatus mentionStatus = (BasketballMentionStatus) mentionDto;
                    BasketballMentionQueryDto.BasketballMentionStatus subMentionStatus = settleMentionConverter.convertBasketballMentionStatus(mentionStatus);
                    if (mentionQueryRequest.getMentionType() == 2) {
                        response.setDataMismatchStatus(subMentionStatus);
                    }
                    return Response.success(response);
                }
            }
        } catch (Exception e) {
            log.error("[FootballNewMatchScoresSettleApiImpl] getSettleEventMentionStatus error: ", e);
            return Response.failed(e.getMessage());
        }
    }

    @Override
    public Response<Map<Long, AbstractMentionQueryDto>> getSettleEventMentionStatusV2(List<Long> matchIds, Long sportId) {
        if(CollectionUtils.isEmpty(matchIds)) {
            log.info("getSettleEventMentionStatus matchIds: {}", matchIds);
            return Response.success();
        }
        Map<Long, AbstractMentionQueryDto> res = new HashMap<>();
        MentionQueryRequest request = new MentionQueryRequest();
        request.setMentionType(0);
        request.setMentionDetail(0);
        request.setSportId(sportId);
        for (Long matchId : matchIds) {
            request.setMatchId(matchId);
            Response<AbstractMentionQueryDto> response = this.getSettleEventMentionStatusV2(request);
            res.put(matchId, response.getData());
        }
        return Response.success(res);
    }

    @Override
    public Response<String> cancelSettleEventMentionV2(SettleEventDeleteRequest settleEventDeleteRequest) {
        try {
            log.info("[FootballNewMatchScoresSettleApiImpl] cancelSettleEventMention param with {}", settleEventDeleteRequest);
            matchServiceHelper.cancelSettleEventMention(settleEventDeleteRequest);
            return Response.success("操作成功");
        } catch (Exception e) {
            log.error("[FootballNewMatchScoresSettleApiImpl] cancelSettleEventMention error: ", e);
            return Response.failed(e.getMessage());
        }
    }

    @Override
    public Response updateMatchSettleScoreV3(UpdateMatchSettleScoreDto matchSettleScoreDto) {
        log.info("[IFootballMatchScoresSettleApiImpl] updateMatchSettleScore-v2 with linkId:{} and param:{} start!",matchSettleScoreDto.getLinkedId(), matchSettleScoreDto);
        String key = CommonConstant.MATCH_PHASE_SCORE_SETTLE+ matchSettleScoreDto.getStandardMatchId();
        if(matchServiceHelper.checkIfOverSettleTime(matchSettleScoreDto.getStandardMatchId())){
            return Response.failed("1031930");
        }
        try {

            if(redisService.tryLock(key,key,2,5)) {
                //0.加redis锁
                StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(matchSettleScoreDto.getStandardMatchId());
                if (standardMatchInfo == null) {
                    return Response.failed("1031931");
                }
                MatchSettleScore matchSettleScore = null;
                MatchSettleScore matchSettleBefore = new MatchSettleScore();
                String forwScore ="" ;
                if (matchSettleScoreDto.getMatchScoreId() != null && matchSettleScoreDto.getMatchScoreId() != 0) {
                    matchSettleScore = matchSettleScoreRepository.getById(matchSettleScoreDto.getMatchScoreId());
                    BeanUtils.copyProperties(matchSettleScore,matchSettleBefore);
                    if (matchSettleScore == null) {
                        return Response.failed("1031931");
                    }
                    if(!matchSettleCheckInfoHelper.isAllPeriodScoresBeforeSettled(matchSettleScore) &&
                            !matchSettleScoreDto.getSettleNum().equals(MatchPeriodEnum.GOAL_5.getCode().toString()) &&
                            !matchSettleScoreDto.getSettleNum().equals(MatchPeriodEnum.GOAL_9.getCode().toString())) {
                        // 请确保上一个比分已结算。
                        return Response.failed("1031946");
                    }
                    if(matchSettleScore.getEventCode().equals("kick_off")||matchSettleScore.getSettleNum().equals("1020")){
                        if(matchSettleScoreDto.getT1()!=null&&matchSettleScoreDto.getT2()!=null){
                            if(!((matchSettleScoreDto.getT1()==0&&matchSettleScoreDto.getT2()==1)||(matchSettleScoreDto.getT1()==1&&matchSettleScoreDto.getT2()==0))){
                                if(matchSettleScoreDto.getGoWaterStatus()==null||matchSettleScoreDto.getGoWaterStatus()==0){
                                    return Response.failed("1031939");
                                }
                            }
                        }else {
                            if(matchSettleScoreDto.getGoWaterStatus()==null||matchSettleScoreDto.getGoWaterStatus()==0){
                                return Response.failed("1031939");
                            }
                        }
                    }
                    //修改前比分
                    forwScore= matchSettleScore.getT1()+"-"+ matchSettleScore.getT2();
                    String t1 =matchSettleScore.getT1()==null ?"":matchSettleScore.getT1().toString();
                    String t2 =matchSettleScore.getT2()==null ?"":matchSettleScore.getT2().toString();
                    forwScore= t1+"-"+t2;

                    List<Integer> integers = Arrays.asList(1021,1031,1032,1033);
                    List<String> corner = Arrays.asList("206","207","208");
                    if (integers.contains(matchSettleScoreDto.getSettleNum())) {
                        String extryInfo = matchSettleScore.getExtryInfo();
                        Integer integer = null;
                        if (!StringUtils.isBlank(extryInfo) ) {
                            integer = Integer.valueOf(extryInfo);
                            forwScore = processedScore(forwScore, matchSettleScoreDto.getSettleNum(), integer);
                        }else if(matchSettleScore.getGoWaterStatus()!=null && "1".equals(matchSettleScore.getGoWaterStatus().toString())){
                            forwScore = WinningMethodEnum.Method_8.getCode().toString();
                        }
                    }
                    if (corner.contains(matchSettleScore.getSettleNum())) {
                        Integer goWaterStatus = matchSettleScore.getGoWaterStatus();
                        //角球走水 10031
                        if (goWaterStatus!=null && goWaterStatus.equals(1))  forwScore = OperateLogTypeEnum.SCORES_SETTLE_10031.getCode().toString();
                    }
                }else {
                    return Response.failed("1031931");
                }
                matchSettleScore.setT1(matchSettleScoreDto.getT1());
                matchSettleScore.setT2(matchSettleScoreDto.getT2());
                matchSettleScore.setStatus(NOT_CONFIRM);
                matchSettleScore.setOperater(matchSettleScoreDto.getOperatorName());
                if(matchSettleScoreDto.getGoWaterStatus()!=null&&matchSettleScoreDto.getGoWaterStatus()==1){
                    matchSettleScore.setGoWaterStatus(1);
                }else {
                    matchSettleScore.setGoWaterStatus(0);
                }
                matchSettleScore.setExtryInfo(matchSettleScoreDto.getExtryInfo());
                matchSettleScore.setModifyTime(System.currentTimeMillis());
                //比分判断是否相同
//                if(MatchEventInfoSettleUtils.equileMatchSettleScores(matchSettleBefore,matchSettleScore)){
//                    return Response.failed("1031940");
//                }
                //1 判断顺序 结算开关是否开启，如果没开启则返回 简单校验逻辑();
                if (!matchSettleInfoHelper.checkSettleScoreAndAutoSettleNonEvent(matchSettleScore,null)){
                    return Response.failed("1031946");
                }
                matchSettleScoreRepository.updateById(matchSettleScore);

                //2.判断更新上半场(5)和全场比分(10) 更新结算信息
                if (matchSettleScore.getSettleNum().equals("105") || matchSettleScore.getSettleNum().equals("1010")) {
                    recordScore(matchSettleScoreDto);
                }
                wsPushService.pushStandardSettleScores(matchSettleScoreDto.getStandardMatchId(),
                        matchSettleScoreDto.getEventCode());

                //3.操作日志记录
                matchSettleOperateLogService.updateMatchSettleScoreAddLog(matchSettleScoreDto,forwScore,matchSettleScore,standardMatchInfo,OperateLogTypeEnum.EDIT.getCode().toString());
                log.info("[IFootballMatchScoresSettleApiImpl] updateMatchSettleScore with linkId:{} end!",matchSettleScoreDto.getLinkedId());
                return Response.success();
            }else {
                return Response.failed("1031933");
            }
        }catch (Exception e){
            log.error("[IFootballMatchScoresSettleApiImpl] updateMatchSettleScore with linkId:{} error:",matchSettleScoreDto.getLinkedId(), e);
            return Response.failed();
        }finally {
            redisService.unLock(key,key);
        }
    }

    @Override
    public Response confirmMatchSettleScoreV3(ConfirmMatchSettleScoreDto matchSettleScoreDto) {
        log.info("[IFootballMatchScoresSettleApiImpl] confirmMatchSettleScore-v2 with linkId:{} and param:{} start!",matchSettleScoreDto.getLinkedId(), matchSettleScoreDto);
        //0.加redis锁
        String key =CommonConstant.MATCH_PHASE_SCORE_SETTLE+ matchSettleScoreDto.getStandardMatchId();
        if(matchServiceHelper.checkIfOverSettleTime(matchSettleScoreDto.getStandardMatchId())){
            return Response.failed("1031930");
        }
        try {
            if(redisService.tryLock(key,key,2,5)) {
                MatchSettleScore matchSettleScore =null;
                matchSettleScore=matchSettleScoreRepository.getById(matchSettleScoreDto.getMatchScoreId());
                if (((matchSettleScore.getT1() == null || matchSettleScore.getT2() == null) && matchSettleScore.getExtryInfo() == null) && !"kick_off".equals(matchSettleScore.getEventCode())) {
                    return Response.failed("该阶段比分为null，请重新编辑比分");
                }
                if(matchSettleScore==null){
                    return Response.failed("1031931");
                }
                if(matchSettleScore.getStatus()>=CONFIRM){
                    return Response.failed("1031934");
                }
                matchSettleScore.setStatus(CONFIRM);
                matchSettleScore.setModifyTime(System.currentTimeMillis());
                matchSettleScoreRepository.updateById(matchSettleScore);
                //2.记录日志
                //走水 将编码设置为8
                if (matchSettleScore.getGoWaterStatus()!=null&&matchSettleScore.getGoWaterStatus().equals(1))  matchSettleScore.setExtryInfo("8");
                matchSettleOperateLogService.matchSettleScoreAddLog(matchSettleScore,matchSettleScoreDto.getOperatorName(),
                        OperateLogTypeEnum.CONFIRM_SCORE,"",matchSettleScoreDto.getIpAddress());
                //推送比分WS
                wsPushService.pushStandardSettleScores(matchSettleScoreDto.getStandardMatchId(),
                        matchSettleScoreDto.getEventCode());
                log.info("[IFootballMatchScoresSettleApiImpl] confirmMatchSettleScore with linkId:{} end!",matchSettleScoreDto.getLinkedId());
                return Response.success();
            }else {
                return Response.failed("1031933");
            }
        }catch (Exception e){
            log.error("[IFootballMatchScoresSettleApiImpl] confirmMatchSettleScore with linkId:{} error:",matchSettleScoreDto.getLinkedId(), e);
            return Response.failed();
        }finally {
            redisService.unLock(key,key);
        }
    }

    @Override
    public Response settleMatchScoreV3(SettleMatchScoreDto matchSettleScoreDto) {
        log.info("[IFootballMatchScoresSettleApiImpl] settleMatchScore with linkId:{} and param:{} start!",matchSettleScoreDto.getLinkedId(), matchSettleScoreDto);
        String key =CommonConstant.MATCH_PHASE_SCORE_SETTLE+ matchSettleScoreDto.getStandardMatchId();
        if(matchServiceHelper.checkIfOverSettleTime(matchSettleScoreDto.getStandardMatchId())){
            return Response.failed("1031930");
        }
        log.info("读取SETTLE_ROLLBACK_MATCH_ID_"+matchSettleScoreDto.getMatchScoreId()+redisService.get("SETTLE_ROLLBACK_MATCH_ID_"+matchSettleScoreDto.getMatchScoreId()));
        if(redisService.hasKey("SETTLE_ROLLBACK_MATCH_ID_"+matchSettleScoreDto.getMatchScoreId())){
            return Response.failed("1031960");
        }
        try {
            if(redisService.tryLock(key,key,2,5)) {
                MatchSettleScore matchSettleScore =null;
                matchSettleScore=matchSettleScoreRepository.getById(matchSettleScoreDto.getMatchScoreId());
                if(matchSettleScore==null){
                    return Response.failed("1031931");
                }
                if(matchSettleScore.getStatus()!=CONFIRM){
                    return Response.failed("1031932");
                }
                Integer settleTimes =matchSettleScore.getSettleTimes();
                if(settleTimes==null){
                    settleTimes=0;
                }
                if (matchSettleScore.getSettleCount()== null ) {
                    matchSettleScore.setSettleCount(0);
                }
                settleTimes++;

                //二次结算,必须给出结算原因
                if (matchSettleScore.getSettleCount() >  0 &&
                        (matchSettleScoreDto.getSettleReason()==null  ||
                                matchSettleScoreDto.getSettleReason()== 0) ) {
                    return Response.failed("1031953");
                }

                String  before= "-";
                Integer settleReason = matchSettleScore.getSettleReason();
                if (settleReason != null &&  settleReason != 0 ) {
                    before = settleReason.toString();
                    if (settleReason == 118) {
                        before += ": "+matchSettleScore.getSettleReasonDetail();
                    }
                }
                //这是理论时间不对 应该先查数据商，如果没数据商再赋值当前
                if(matchSettleScore.getEventTime()==null||matchSettleScore.getEventTime().equals(0l)){
                    Long eventTime =matchSettleCheckInfoHelper.searchEventTimeByScores(matchSettleScore);
                    if(eventTime==0l){
                        eventTime=matchSettleScore.getModifyTime();
                    }
                    matchSettleScore.setEventTime(eventTime);
                }
                matchSettleScore.setStatus(SETTLED);
                matchSettleScore.setOperater(matchSettleScoreDto.getOperatorName());
                matchSettleScore.setSettleTimes(settleTimes);
                matchSettleScore.setSettleCount(matchSettleScore.getSettleCount()+1);
                matchSettleScore.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.SETTLE);
                matchSettleScore.setUserid(matchSettleScoreDto.getOperatorId());
                matchSettleScore.setModifyTime(System.currentTimeMillis());
                matchSettleScore.setSettleReason(matchSettleScoreDto.getSettleReason());
                matchSettleScore.setSettleReasonDetail(matchSettleScoreDto.getSettleReasonDetail());

                matchSettleScore.setIsGrey(0);
                matchSettleScore.setHasDeleteEvent(0);
                matchSettleScore.setCurrentEventStatus(0);
                matchSettleCheckInfoHelper.endEventSettleByScore(matchSettleScore);
                matchSettleScoreRepository.updateById(matchSettleScore);
                matchSettleInfoHelper.updateMatchGrayStatus(matchSettleScore.getStandardMatchId());
                matchSettleInfoHelper.updateMatchCurrentEventStatus(matchSettleScore.getStandardMatchId());
                matchSettleCheckInfoHelper.updateMatchFifteenMinGraySettleFactor(matchSettleScore.getStandardMatchId(),matchSettleScore.getSettleNum());
                //结算时把回滚订单数清零
                matchServiceHelper.settleRollBackSetNullOrderCount(matchSettleScore.getId());
                //2.MQ下发

                if (matchSettleScore.getPeriodId()==100 && (matchSettleScore.getSettleNum().equals(MatchPeriodEnum.GOAL_10.getCode().toString()) ||
                        matchSettleScore.getSettleNum().equals(MatchPeriodEnum.Corner_3.getCode().toString()) ||
                        matchSettleScore.getSettleNum().equals(MatchPeriodEnum.BOOKINGS_9.getCode().toString()))) {
                    matchSettleScoresProducer.sendMatchSettleScores(matchSettleScore, 2);
                } else {
                    matchSettleScoresProducer.sendMatchSettleScores(matchSettleScore);
                }

                wsPushService.pushStandardSettleScores(matchSettleScoreDto.getStandardMatchId(),
                        matchSettleScoreDto.getEventCode());
                if (matchSettleScoreDto.getSettleReason() != null) {
                    matchSettleEventService.secondSettleWarnMango(matchSettleScoreDto, 1);
                }
                //1.比分结算增加操作日志
                if (matchSettleScore.getGoWaterStatus()!=null&&matchSettleScore.getGoWaterStatus().equals(1))  matchSettleScore.setExtryInfo("8");
                matchSettleOperateLogService.matchSettleScoreAddLog(matchSettleScore,matchSettleScoreDto.getOperatorName(),OperateLogTypeEnum.SCORE_SETTLE,before,matchSettleScoreDto.getIpAddress());
                syncScoreFactory.getProcessor(SettleSyncEnum.FOOTBALL_SYNC_SCORE).syncScore(matchSettleScoreDto);
                log.info("[IFootballMatchScoresSettleApiImpl] settleMatchScore with linkId:{} end!",matchSettleScoreDto.getLinkedId());
                return Response.success();
            }else {
                log.info("[IFootballMatchScoresSettleApiImpl] settleMatchScore with linkId:{} obtain redis fail!",matchSettleScoreDto.getLinkedId());
                return Response.failed("1031933");
            }
        }catch (Exception e){
            log.error("[IFootballMatchScoresSettleApiImpl] settleMatchScore with linkId:{} error:",matchSettleScoreDto.getLinkedId(), e);
            return Response.failed();
        }finally {
            redisService.unLock(key,key);
        }
    }

    @Override
    public Response addMatchSettleEventV3(AddMatchSettleEventDto addMatchSettleEventDto) {
        log.info("addMatchSettleEvent param,addMatchSettleEventDto: {}",addMatchSettleEventDto);
        if(matchServiceHelper.checkIfOverSettleTime(addMatchSettleEventDto.getStandardMatchId())){
            return Response.failed("1031930");
        }
        String key ="MATCH_SETTLE_INFO:"+ addMatchSettleEventDto.getStandardMatchId();
        try {
            if(redisService.tryLock(key,key,2,5)) {
                StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(addMatchSettleEventDto.getStandardMatchId());
                //1.校验
                if (standardMatchInfo == null) {
                    return Response.failed("1031931");
                }
                List<String> settleNumbers = new ArrayList<>();
                settleNumbers.add(addMatchSettleEventDto.getSettleNum());
                List<MatchSettleEvent> list = matchSettleEventRepository.getModelByStandardMatchIdAndSettleNums(addMatchSettleEventDto.getStandardMatchId(),settleNumbers);
                //2.判断事件序号
                Integer eventOrder = checkEventOrder(list);
                if (eventOrder == 0) {
                    return Response.failed("1031931");
                }
                eventOrder++;
                //3.新增
                MatchSettleEvent matchSettleEvent = new MatchSettleEvent();
                //0：未编码（初始化对应事件编码的数据）
                matchSettleEvent.setStatus(0);
                matchSettleEvent.setStandardMatchId(addMatchSettleEventDto.getStandardMatchId());
                matchSettleEvent.setEventCode(addMatchSettleEventDto.getEventCode());
//        matchSettleEvent.setSettleNum(eventOrder.toString());
                matchSettleEvent.setSettleNum(SettleNumUtils.getEventSettleNum(addMatchSettleEventDto.getEventCode(), addMatchSettleEventDto.getPeriodId()));
                matchSettleEvent.setModifyTime(System.currentTimeMillis());
                matchSettleEvent.setCreateTime(System.currentTimeMillis());
                matchSettleEvent.setEventOrder(eventOrder);
                matchSettleEvent.setSportId(1l);
                matchSettleEvent.setId(IdGenerator.nextId());
                matchSettleEvent.setThirdEventSourceId(matchSettleEvent.getId());
                matchSettleEvent.setDataSourceCode("PA");
                matchSettleEvent.setPeriodId(addMatchSettleEventDto.getPeriodId());
                matchSettleEvent.setCheckNumber(1);
                matchSettleEvent.setEventType(1);
                matchSettleEvent.setSettleCount(0);
                matchSettleEvent.setSettleTimes(0);
                // eventType=1不应该设置时段信息，时段信息应该设置在对应的eventType=3事件中
                matchSettleEvent.setFiveMinSection(null);
                //2.2 获得上个事件比分 自动计算比分
                List<MatchSettleEvent> matchSettleEventList = new ArrayList<>();
                matchSettleEventList.add(matchSettleEvent);
                if (!(matchSettleEvent.getEventCode().equals("corner") || matchSettleEvent.getPeriodId().equals(50l))) {
                    MatchSettleEvent matchSettleEvent2 = new MatchSettleEvent();
                    BeanUtils.copyProperties(matchSettleEvent, matchSettleEvent2);
                    matchSettleEvent2.setEventType(2);
                    matchSettleEvent2.setId(IdGenerator.nextId());
                    matchSettleEvent2.setSportId(1l);
                    matchSettleEvent2.setDataSourceCode("PA");
                    matchSettleEvent2.setCheckNumber(1);
                    matchSettleEvent2.setSettleNum(SettleNumUtils.getTypeEventSettleNum(matchSettleEvent2.getEventCode(), matchSettleEvent2.getPeriodId(), 2));
                    matchSettleEventList.add(matchSettleEvent2);
                }
                // 创建eventType=3（时段事件）- 只在periodId=6L或7L时创建（加时赛41L, 42L没有eventType=3）
                Long periodId = matchSettleEvent.getPeriodId();
                if (periodId != null && (periodId.equals(6L) || periodId.equals(7L))) {
                    MatchSettleEvent timePhaseEvent = new MatchSettleEvent();
                    BeanUtils.copyProperties(matchSettleEvent, timePhaseEvent);
                    timePhaseEvent.setId(IdGenerator.nextId());
                    timePhaseEvent.setEventType(3);
                    timePhaseEvent.setSettleNum(SettleNumUtils.getTypeEventSettleNum(timePhaseEvent.getEventCode(), timePhaseEvent.getPeriodId(), 3));
                    // eventType=3才设置时段信息
                    timePhaseEvent.setFiveMinSection(addMatchSettleEventDto.getFiveMinSection());
                    matchSettleEventList.add(timePhaseEvent);
                }
                matchSettleEventRepository.saveOrUpdateBatch(matchSettleEventList);
                //4.查询事件列表返回
                if("goal".equals(addMatchSettleEventDto.getEventCode()) && StringUtils.isNotBlank(addMatchSettleEventDto.getOperatorName())){
                    wsPushService.pushSettleMatchList(new MatchListSettleDto(addMatchSettleEventDto.getStandardMatchId(),
                            addMatchSettleEventDto.getEventCode(),null,null,5));
                }else {
                    wsPushService.pushStandardSettleEvent(addMatchSettleEventDto.getStandardMatchId(),
                            addMatchSettleEventDto.getEventCode());
                }
                redisService.unLock(key,key);
                return Response.success();
            }else {
                return Response.failed();
            }
        }catch (Exception e){
            e.printStackTrace();
            return Response.failed();
        }
    }


    @Override
    public Response editMatchSettleEventV3(EditMatchSettleEventDto editMatchSettleEventDto) {
        log.info("editMatchSettleEvent param,editMatchSettleEvent-v2: {}",editMatchSettleEventDto);
        String key =CommonConstant.MATCH_SEQUENCE_SCORE_SETTLE + editMatchSettleEventDto.getStandardMatchId();
        MatchSettleEvent extryevent = null ;
        MatchSettleEvent matchSettleEvent = null;
        MatchSettleEvent matchSettleEventBefore = new MatchSettleEvent();
        try {
            if(redisService.tryLock(key,key,2,5)) {
                // 先获取事件，检查eventType（注意：corner事件可能不存在matchSettleEvent，而是MatchSettleScore）
                matchSettleEvent = matchSettleEventRepository.getById(editMatchSettleEventDto.getEventId());
                if(matchSettleEvent==null){
                    return Response.failed("1031935");
                }
                BeanUtils.copyProperties(matchSettleEvent,matchSettleEventBefore);
                // 如果编辑的是eventType=3（时段事件），必须先确保对应的eventType=1（比分事件）已结算（status=3）
                if (matchSettleEvent != null && matchSettleEvent.getEventType() != null && matchSettleEvent.getEventType() == 3) {
                    // 查找对应的eventType=1事件
                    String scoreSettleNum = SettleNumUtils.getTypeEventSettleNum(
                            matchSettleEvent.getEventCode(),
                            matchSettleEvent.getPeriodId(),
                            1);
                    List<MatchSettleEvent> scoreEvents = matchSettleEventRepository.getByMatchIdAndSettleNumAndEventOrderAndPeriodId(
                            matchSettleEvent.getStandardMatchId(),
                            scoreSettleNum,
                            matchSettleEvent.getEventOrder(),
                            matchSettleEvent.getPeriodId());
                    MatchSettleEvent scoreEvent = scoreEvents.stream()
                            .filter(e -> e.getEventType() != null && e.getEventType() == 1)
                            .findFirst()
                            .orElse(null);

                    // 检查eventType=1事件是否存在
                    if (scoreEvent == null) {
                        log.warn("editMatchSettleEvent::尝试编辑时段事件，但对应的比分事件不存在, eventId:{}, eventCode:{}",
                                matchSettleEvent.getId(), matchSettleEvent.getEventCode());
                        return Response.failed("1031941"); // 返回错误码：必须先编辑比分
                    }

                    // 检查eventType=1事件是否已结算（status=3）
                    if (scoreEvent.getStatus() == null || scoreEvent.getStatus() != 3) {
                        log.warn("editMatchSettleEvent::尝试编辑时段事件，但对应的比分事件未结算, eventId:{}, scoreEventId:{}, scoreEventStatus:{}, eventCode:{}",
                                matchSettleEvent.getId(), scoreEvent.getId(), scoreEvent.getStatus(), matchSettleEvent.getEventCode());
                        return Response.failed("1031941"); // 返回错误码：必须先结算比分事件
                    }
                    editMatchSettleEventDto.setT1(scoreEvent.getT1());
                    editMatchSettleEventDto.setT2(scoreEvent.getT2());
                    matchSettleEvent.setT1(scoreEvent.getT1());
                    matchSettleEvent.setT2(scoreEvent.getT2());
                    matchSettleEvent.setExtryInfo(scoreEvent.getExtryInfo());
                    matchSettleEvent.setGoWaterStatus(scoreEvent.getGoWaterStatus());
                    editMatchSettleEventDto.setHomeAway(scoreEvent.getHomeAway());
                    matchSettleEvent.setHomeAway(scoreEvent.getHomeAway());
                    if(editMatchSettleEventDto.getEventCode().equals("fa_card")) {
                        matchSettleEvent.setEventCode(scoreEvent.getEventCode());
                    }
                }
                if(StringUtils.isEmpty(editMatchSettleEventDto.getHomeAway())){
                    return Response.failed("1031939");
                }
                if(matchSettleEvent.getEventType() == 3 && StringUtils.isEmpty(editMatchSettleEventDto.getFiveMinSection())){
                    return Response.failed("1031939");
                }
                if(editMatchSettleEventDto.getEventCode().equals("goal")){
                    // matchSettleEvent已经在上面获取了，如果为null则重新获取

                    if(!matchSettleCheckInfoHelper.isPeriodScoresBeforeSettledByEvent(matchSettleEvent)){
                        return Response.failed("10138");
                    }

                    //1.自动计算进球比分
                    updateGoalAndCornerEventByInfo(matchSettleEvent,editMatchSettleEventDto.getHomeAway(),"goal");
                    //比分校验是否相同
                    if(editMatchSettleEventDto.getGoWaterStatus()!=null&&editMatchSettleEventDto.getGoWaterStatus()==1){
                        matchSettleEvent.setGoWaterStatus(1);
                    }else {
                        matchSettleEvent.setGoWaterStatus(0);
                    }
                    matchSettleEvent.setExtryInfo(editMatchSettleEventDto.getExtryInfo());
                    matchSettleEvent.setModifyTime(System.currentTimeMillis());
                    matchSettleEvent.setHomeAway(editMatchSettleEventDto.getHomeAway());
                    matchSettleEvent.setStatus(1);
                    matchSettleEvent.setOperater(editMatchSettleEventDto.getOperatorName());
                    // eventType=1不应该设置时段信息，时段信息应该设置在对应的eventType=3事件中
                    if (matchSettleEvent.getEventType() != null && matchSettleEvent.getEventType() == 3) {
                        matchSettleEvent.setFiveMinSection(editMatchSettleEventDto.getFiveMinSection());
                    }
//                    if(MatchEventInfoSettleUtils.equileMatchSettleEvent(matchSettleEventBefore,matchSettleEvent)){
//                        return Response.failed("1031940");
//                    }
//                    if(matchSettleEvent.getSettleCount() == 1 && matchSettleEvent.getT1().equals(matchSettleEventBefore.getT1()) && matchSettleEvent.getT2().equals(matchSettleEventBefore.getT2())
//                            && matchSettleEventBefore.getFiveMinSection() == null && matchSettleEvent.getFiveMinSection() != null) {
//                        redisService.set(CommonConstant.FIVE_MIN_SETTLE_TIMES+matchSettleEvent.getId(), 1, RedisConfig.REDIS_WEEK_TIME);
//                    }

                    matchSettleEventRepository.updateById(matchSettleEvent);
                    //编辑影子事件比分和homeAway
                    extryevent =matchServiceHelper.getExtryEvent(matchSettleEvent);
                    if(extryevent!=null){
                        MatchSettleEventDto matchSettleEventDto =new MatchSettleEventDto();
                        BeanUtils.copyProperties(matchSettleEvent,matchSettleEventDto);
                        extryevent.setExtryInfo(editMatchSettleEventDto.getExtryInfo());
                        extryevent.setT1(matchSettleEvent.getT1());
                        extryevent.setT2(matchSettleEvent.getT2());
                        extryevent.setModifyTime(System.currentTimeMillis());
                        extryevent.setHomeAway(editMatchSettleEventDto.getHomeAway());
//                        extryevent.setStatus(1);
                        extryevent.setOperater(editMatchSettleEventDto.getOperatorName());
                        matchSettleEventRepository.updateById(extryevent);
                    }

                }else if(editMatchSettleEventDto.getEventCode().equals("corner")){
                    //1.事件只编辑比分
                    // matchSettleEvent已经在上面获取了，如果为null则重新获取
                    if(matchSettleEvent!=null){
                        //自动计算角球比分
                        updateGoalAndCornerEventByInfo(matchSettleEvent,editMatchSettleEventDto.getHomeAway(),"corner");
                        matchSettleEvent.setExtryInfo(editMatchSettleEventDto.getExtryInfo());
                        matchSettleEvent.setModifyTime(System.currentTimeMillis());
                        matchSettleEvent.setHomeAway(editMatchSettleEventDto.getHomeAway());
                        matchSettleEvent.setStatus(1);
                        matchSettleEvent.setOperater(editMatchSettleEventDto.getOperatorName());
                        // eventType=1不应该设置时段信息，时段信息应该设置在对应的eventType=3事件中
                        if (matchSettleEvent.getEventType() != null && matchSettleEvent.getEventType() == 3) {
                            matchSettleEvent.setFiveMinSection(editMatchSettleEventDto.getFiveMinSection());
                        }
                        if(editMatchSettleEventDto.getGoWaterStatus()!=null&&editMatchSettleEventDto.getGoWaterStatus()==1){
                            matchSettleEvent.setGoWaterStatus(1);
                        }else {
                            matchSettleEvent.setGoWaterStatus(0);
                        }
//                        if(MatchEventInfoSettleUtils.equileMatchSettleEvent(matchSettleEventBefore,matchSettleEvent)){
//                            return Response.failed("1031940");
//                        }
                        matchSettleEvent.setModifyTime(System.currentTimeMillis());
//                        if(matchSettleEvent.getSettleCount() == 1 && matchSettleEvent.getT1().equals(matchSettleEventBefore.getT1()) && matchSettleEvent.getT2().equals(matchSettleEventBefore.getT2())
//                                && matchSettleEventBefore.getFiveMinSection() == null && matchSettleEvent.getFiveMinSection() != null) {
//                            redisService.set(CommonConstant.FIVE_MIN_SETTLE_TIMES+matchSettleEvent.getId(), 1, RedisConfig.REDIS_WEEK_TIME);
//                        }
                        matchSettleEventRepository.updateById(matchSettleEvent);
                    }else {
                        MatchSettleScore matchSettleScore =matchSettleScoreRepository.getById(editMatchSettleEventDto.getEventId());
                        if(matchSettleScore!=null) {
                            //角球阶段比分由人工录入
                            //比分判断是否相同
//                            if(MatchEventInfoSettleUtils.equileMatchSettleEvent(matchSettleEventBefore,matchSettleEvent)){
//                                return Response.failed("1031940");
//                            }
                            if(editMatchSettleEventDto.getGoWaterStatus()!=null&&editMatchSettleEventDto.getGoWaterStatus()==1){
                                matchSettleEvent.setGoWaterStatus(1);
                            }else {
                                matchSettleEvent.setGoWaterStatus(0);
                            }
                            matchSettleScore.setExtryInfo(editMatchSettleEventDto.getExtryInfo());
                            matchSettleScore.setT1(editMatchSettleEventDto.getT1());
                            matchSettleScore.setT2(editMatchSettleEventDto.getT2());
                            matchSettleScore.setModifyTime(System.currentTimeMillis());
                            matchSettleEvent.setOperater(editMatchSettleEventDto.getOperatorName());
                            matchSettleScore.setStatus(1);
                            matchSettleScoreRepository.updateById(matchSettleScore);
                        }
                    }
                    //2.阶段比分
                }else if(editMatchSettleEventDto.getEventCode().equals("fa_card")){
                    //1.根据facard条件设置 主客队和 罚牌类型
//                    if(StringUtils.isEmpty(editMatchSettleEventDto.getHomeAway())){
//                        return Response.failed("1031939");
//                    }
//
//                    if(matchSettleEvent.getEventType() == 3 && StringUtils.isEmpty(editMatchSettleEventDto.getFiveMinSection())){
//                        return Response.failed("1031939");
//                    }

                    //2.自动计算罚牌比分
                    if (matchSettleEvent.getEventType() == 1) {
                        updateFaCardEventByInfo(matchSettleEvent,editMatchSettleEventDto.getHomeAway());
                    }
                    matchSettleEvent.setModifyTime(System.currentTimeMillis());
                    matchSettleEvent.setStatus(1);
                    if(editMatchSettleEventDto.getGoWaterStatus()!=null&&editMatchSettleEventDto.getGoWaterStatus()==1){
                        matchSettleEvent.setGoWaterStatus(1);
                    }else {
                        matchSettleEvent.setGoWaterStatus(0);
                    }
                    matchSettleEvent.setOperater(editMatchSettleEventDto.getOperatorName());
                    // eventType=1不应该设置时段信息，时段信息应该设置在对应的eventType=3事件中
                    if (matchSettleEvent.getEventType() != null && matchSettleEvent.getEventType() == 3) {
                        matchSettleEvent.setFiveMinSection(editMatchSettleEventDto.getFiveMinSection());
                    }
//                    if(MatchEventInfoSettleUtils.equileMatchSettleEvent(matchSettleEventBefore,matchSettleEvent)){
//                        return Response.failed("1031940");
//                    }
//                    if(matchSettleEvent.getSettleCount() == 1 && matchSettleEvent.getT1().equals(matchSettleEventBefore.getT1()) && matchSettleEvent.getT2().equals(matchSettleEventBefore.getT2())
//                            && matchSettleEventBefore.getFiveMinSection() == null && matchSettleEvent.getFiveMinSection() != null) {
//                        redisService.set(CommonConstant.FIVE_MIN_SETTLE_TIMES+matchSettleEvent.getId(), 1, RedisConfig.REDIS_WEEK_TIME);
//                    }
                    matchSettleEventRepository.updateById(matchSettleEvent);
                    //3.设置到影子事件中比分 以及主客队 罚牌类型等
                    if (matchSettleEvent.getEventType()==1){
                        extryevent =matchServiceHelper.getExtryEvent(matchSettleEvent);
                        if(extryevent!=null){
                            extryevent.setHomeAway(matchSettleEvent.getHomeAway());
                            extryevent.setT1(matchSettleEvent.getT1());
                            extryevent.setT2(matchSettleEvent.getT2());
                            extryevent.setFirstT1(matchSettleEvent.getFirstT1());
                            extryevent.setFirstT2(matchSettleEvent.getFirstT2());
                            extryevent.setSecondT1(matchSettleEvent.getSecondT1());
                            extryevent.setSecondT2(matchSettleEvent.getSecondT2());
                            extryevent.setModifyTime(System.currentTimeMillis());
                            extryevent.setEventCode(matchSettleEvent.getEventCode());
//                        extryevent.setStatus(1);
                            extryevent.setOperater(editMatchSettleEventDto.getOperatorName());
                            matchSettleEventRepository.updateById(extryevent);
                        }
                    }
                }
                //2.事件编辑记录日志
                // 注意：corner事件中，如果matchSettleEvent为null（是MatchSettleScore的情况），则跳过后续的日志和eventType处理
                if (matchSettleEvent != null) {
                    log.info("{}--保存15分钟日志:event {}",editMatchSettleEventDto.getEventCode(),matchSettleEvent);
                    // 根据eventType决定处理逻辑
                    if (matchSettleEvent.getEventType() != null && matchSettleEvent.getEventType() == 1) {
                        // 编辑eventType=1（次序比分）时：
                        // 1. 确保存在时段事件（eventType=3）并同步比分信息
                        ensurePeriodEventExists(matchSettleEvent, editMatchSettleEventDto);
                    }
                    if (!(matchSettleEvent.getEventType()==3 && "fa_card".equals(editMatchSettleEventDto.getEventCode()))) {
                        matchSettleEvent.setEventCode(editMatchSettleEventDto.getEventCode());
                        matchSettleEvent.setHomeAway(editMatchSettleEventDto.getHomeAway());
                    }

                    String homeAway = matchSettleEventBefore.getHomeAway();
                    String homeAwayNew = matchSettleEvent.getHomeAway();
                    if ("goal".equals(editMatchSettleEventDto.getEventCode())) {
                        homeAway = goalProcessRest( matchSettleEventBefore.getHomeAway(),matchSettleEventBefore.getStatus());
                        homeAwayNew = goalProcessRest( matchSettleEvent.getHomeAway(),matchSettleEvent.getStatus());

                    };
                    if ("fa_card".equals(editMatchSettleEventDto.getEventCode())){
                        homeAway = faCardProcessRest(matchSettleEventBefore.getEventCode(), matchSettleEventBefore.getHomeAway(),matchSettleEventBefore.getStatus());
                        if (matchSettleEvent.getEventType() == 3) {
                            homeAwayNew = faCardProcessRest(matchSettleEvent.getEventCode(), matchSettleEvent.getHomeAway(),matchSettleEvent.getStatus());
                        }
                    }
                    matchSettleEventBefore.setHomeAway(homeAway);
                    matchSettleEvent.setHomeAway(homeAwayNew);
                    //事件编辑增加日志
                    // eventType=1不应该设置时段信息，只记录日志
                    // eventType=3的时段信息已经在上面更新了
                    if (matchSettleEvent.getEventType() != null && matchSettleEvent.getEventType() == 3 && (!matchSettleEvent.getEventCode().equals("goal"))) {
                        matchSettleEvent.setFifteenMinSection(editMatchSettleEventDto.getFiveMinSection());
                    }
                    log.info("{}--保存15分钟日志:修改前 {}---{}",editMatchSettleEventDto.getEventCode()
                            ,matchSettleEventBefore.getFiveMinSection(),
                            matchSettleEventBefore.getFifteenMinSection());
                    log.info("{}--保存15分钟日志:修改后 {}---{}",editMatchSettleEventDto.getEventCode()
                            ,matchSettleEvent.getFiveMinSection(),
                            matchSettleEvent.getFifteenMinSection());
                    matchSettleOperateLogService.matchSettleEventAddLog(matchSettleEventBefore,matchSettleEvent,editMatchSettleEventDto.getOperatorName(),
                            OperateLogTypeEnum.EDIT,editMatchSettleEventDto.getIpAddress());
                }

                //3.返回查询事件列表
                wsPushService.pushStandardSettleEvent(editMatchSettleEventDto.getStandardMatchId(),
                        editMatchSettleEventDto.getEventCode());
                return Response.success();
            }else {
                return Response.failed("1031933");
            }
        }catch (Exception e){
            log.error("IFootballMatchScoresSettleApiImpl-editMatchSettleEvent:",e);
            return Response.failed();
        }finally {
            redisService.unLock(key,key);
        }
    }
}
