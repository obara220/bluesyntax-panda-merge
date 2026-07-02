package com.panda.merge.advertise.dubbo;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.panda.merge.advertise.common.Constant;
import com.panda.merge.advertise.dto.FootballMatchEventStatusVo;
import com.panda.merge.advertise.dto.MatchScoreAndTimeVo;
import com.panda.merge.advertise.event.CommonEventService;
import com.panda.merge.advertise.mq.EventProducer;
import com.panda.merge.advertise.service.BasketBallAdvertiseService;
import com.panda.merge.advertise.service.CommonAdvertiseService;
import com.panda.merge.advertise.utils.MatchPeriodUtils;
import com.panda.merge.advertise.utils.RedisUtils;
import com.panda.merge.api.IBasketballMatchScoresSettleApi;
import com.panda.merge.api.IMatchBasketBallAdvertiseApi;
import com.panda.merge.common.enums.PDOperateLogEnum;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.ScoreEventCodeSourceEnum;
import com.panda.merge.constant.SourceTypeEnum;
import com.panda.merge.constant.SportPeriodConstant;
import com.panda.merge.constant.SportTypeEnum;
import com.panda.merge.dto.BasketballScores;
import com.panda.merge.dto.BasketballScoresExtra;
import com.panda.merge.dto.CommonItem;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.advertise.ChangeMatchLengthDto;
import com.panda.merge.dto.advertise.ChangeMatchPeriodDto;
import com.panda.merge.dto.advertise.ChangeMatchScoreDto;
import com.panda.merge.dto.advertise.ChangeMatchStartTimeDto;
import com.panda.merge.dto.advertise.ChangeMatchStatusDto;
import com.panda.merge.dto.advertise.ChangeMatchTimeDto;
import com.panda.merge.dto.advertise.CreatePDAdvertiseDto;
import com.panda.merge.dto.advertise.MatchAdvertiseQueryDto;
import com.panda.merge.dto.advertise.MatchEventInfoDto;
import com.panda.merge.mapper.MatchScoresEventInfoMapper;
import com.panda.merge.mapper.MatchScoresInfoMapper;
import com.panda.merge.mapper.MatchTimeInfoMapper;
import com.panda.merge.mapper.StandardMatchInfoMapper;
import com.panda.merge.mapper.StandardSportMarketSellMapper;
import com.panda.merge.mapper.ThirdMatchInfoMapper;
import com.panda.merge.model.MatchScoresEventInfo;
import com.panda.merge.model.MatchScoresEventInfoExample;
import com.panda.merge.model.MatchScoresInfo;
import com.panda.merge.model.MatchTimeInfo;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.StandardSportMarketSell;
import com.panda.merge.model.StandardSportMarketSellExample;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.mq.producer.ScoresProducer;
import com.panda.merge.repository.MatchScoreInfoRepository;
import com.panda.merge.repository.MatchTimeInfoRepository;
import com.panda.merge.repository.PdMatchInfoRepository;
import com.panda.merge.service.IMatchScorePdLogService;
import com.panda.merge.service.IScoresService;
import com.panda.merge.utils.JsonMapUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;

import static com.panda.merge.advertise.common.Constant.*;
import static com.panda.merge.constant.SportPeriodConstant.SportPeriod.WHOLE_MATCH;


/**
 * kb
 * PA篮球报球版服务
 * 不得写有关比分事件细节操作的功能，只能写主方法
 */
@Service
@Slf4j
@DubboService
public class MatchBasketBallAdvertiseApiImpl implements IMatchBasketBallAdvertiseApi {

    @Autowired
    private BasketBallAdvertiseService basketBallAdvertiseService;
//    @Autowired
//    ThirdMatchInfoMapper thirdMatchInfoMapper;
    @Autowired
    RedisService redisService;
//    @Autowired
//    MatchScoresInfoMapper matchScoresInfoMapper;
//    @Autowired
//    MatchTimeInfoMapper timeInfoMapper;
    @Autowired
    IScoresService scoresService;
//    @Autowired
//    StandardMatchInfoMapper standardMatchInfoMapper;
//    @Autowired
//    StandardSportMarketSellMapper standardSportMarketSellMapper;
    @Autowired
    CommonEventService commonEventService;
    @Autowired
    CommonAdvertiseService commonAdvertiseService;
    @Autowired
    RedisUtils redisUtils;
    @Autowired
    IMatchScorePdLogService matchScorePdLogService;
    @Autowired
    MatchScoresEventInfoMapper matchScoresEventInfoMapper;
    @Autowired
    ScoresProducer scoresProducer;
    @Autowired
    private PdMatchInfoRepository pdMatchInfoRepository;
    @Autowired
    private EventProducer eventProducer;
    @Autowired
    private MatchTimeInfoRepository matchTimeInfoRepository;
    @Autowired
    private MatchScoreInfoRepository matchScoreInfoRepository;

    @Override
    public Response createMatchAdvertise(CreatePDAdvertiseDto createPDAdvertiseDto) {
        try {
            //0.数据准备
//            ThirdMatchInfo thirdMatchInfo = thirdMatchInfoMapper.selectByPrimaryKey(createPDAdvertiseDto.getThirdMatchId());
            ThirdMatchInfo thirdMatchInfo = pdMatchInfoRepository.getThirdMatchInfo(createPDAdvertiseDto.getThirdMatchId(), null);
            if (thirdMatchInfo == null) {
                return Response.failed("PA赛事不存在");
            }
            String key = "PA_createMatchAdvertise:" + createPDAdvertiseDto.getThirdMatchId();
            try {
                if (redisService.tryLock(key, key, 4, 3)) {
                    basketBallAdvertiseService.createMatchScoresInfo(thirdMatchInfo, createPDAdvertiseDto.getDataSourceCode());
                    return Response.success();
                } else {
                    return Response.failed();
                }
            } catch (Exception e) {
                return Response.failed(e.getMessage());
            } finally {
                redisService.unLock(key, key);
            }
        } catch (Exception e) {
            log.error("::createMatchAdvertise异常::", e);
            return Response.failed(e.getMessage());
        }
    }

    @Override
    public Response changeMatchLenth(ChangeMatchLengthDto changeMatchLengthDto)
    {
        String linkId = changeMatchLengthDto.getLinkedId();
        log.info("::{}::篮球报球板赛制的变更入参:{}", linkId, JSON.toJSONString(changeMatchLengthDto));
        String key = "PA_createMatchAdvertise:" + changeMatchLengthDto.getThirdMatchId();
        try
        {
            if (redisService.tryLock(key, key, 2, 3))
            {

                //1.查询出当前赛事的赛制
                Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(changeMatchLengthDto.getThirdMatchId());
                if (!response.isSuccess()) {
                    return response;
                }
                ThirdMatchInfo thirdMatchInfo = response.getData().getThirdMatchInfo();
                MatchScoresInfo matchScoresInfo = response.getData().getMatchScoresInfo();
                MatchTimeInfo timeInfo = response.getData().getMatchTimeInfo();
                if (timeInfo.getPeriod() > 0)
                {
                    return Response.failed("开赛后不能修改赛制");
                }
                Integer newMinutes = changeMatchLengthDto.getMinutes();
                //3X3 判断
//                if (3 == response.getData().getStandardMatchInfo().getMatchType()) {
//                    thirdMatchInfo.setMatchLength(1);
//                }
                Integer oldMinutes = Constant.BasketBallConstant.matchLenthTimeMap.get(thirdMatchInfo.getMatchLength());

                Integer oldMatchLength = thirdMatchInfo.getMatchLength();

                Integer newMatchLength = Constant.BasketBallConstant.timeMatchLenthMap.get(changeMatchLengthDto.getMinutes());
                if ( 73 == newMatchLength )
                {
                    newMinutes = 10;
                }
                //赛制 3X3
//                if (3 == response.getData().getStandardMatchInfo().getMatchType()) {
//                    newMatchLength = 73;
//                }
                log.info("::{}::篮球报球板赛制的变更入参, oldMatchLength:{}, newMatchLength:{}, oldMinutes:{}, newMinutes:{}", linkId, oldMatchLength, newMatchLength, oldMinutes, newMinutes);
                if (newMatchLength == null) {
                    return Response.failed("时间输入错误必须是 10,12,20,5");
                }
                log.info("抱球版篮球赛制切换：----oldMinutes={}，newMinutes={}，oldMatchLength={}，newMatchLength={}",oldMinutes,newMinutes,oldMatchLength,newMatchLength);
//                if ( oldMinutes.equals(newMinutes) && oldMatchLength.equals(newMatchLength) ) {
//                    return Response.success();
//                }
                //1.2 如果 已经开赛 10-12 分钟可以切换 但不能切换到 20分钟。  20分钟也不能切换到 10- 12分钟，因为 节数和已经下发的阶段无法更改
                if (timeInfo.getPeriod() > 0) {
                    if (oldMinutes == 20) {
                        return Response.failed("PA赛事已经开始,上下半场分钟不能切换");
                    }
                    if (oldMinutes < 20 && newMinutes == 20) {
                        return Response.failed("PA赛事已经开始,上下半场分钟不能切换");
                    }
                }
                timeInfo.setTimeGo(0);
                timeInfo.setSecondFromStart(newMinutes * 60L);
                timeInfo.setRemainingTime(newMinutes * 60L);
                timeInfo.setPeriod(0L);

                matchScoresInfo.setSecondsMatchStart(newMinutes * 60L);
                matchScoresInfo.setRemainingTime(newMinutes * 60L);
                matchScoresInfo.setPeriod(0L);
                thirdMatchInfo.setMatchPeriod("0");
                //修改赛制 PA事件下发到比分服务只做时间修改,比分不做修改
                //2.更新赛制   修改 三方赛事 标准赛事的赛制 下发 下游同步标准赛事的赛制变更 kb-bug 1 可能会无法同步更新到业务风控,也可能会被覆盖需要 赛程的锁赛事功能
                thirdMatchInfo.setMatchLength(newMatchLength);
                timeInfo.setMatchLength(newMatchLength);
                matchScoresInfo.setMatchLength(newMatchLength);
//                StandardMatchInfo standardMatchInfo = standardMatchInfoMapper.selectByPrimaryKey(thirdMatchInfo.getReferenceId());
                StandardMatchInfo standardMatchInfo = pdMatchInfoRepository.getStandardMatchInfo(thirdMatchInfo.getReferenceId(), null);
                if (standardMatchInfo == null) {
                    log.error("changeMatchLenth_error:{} 标准赛事不存在 linkedId:{}", thirdMatchInfo.getReferenceId(), changeMatchLengthDto.getLinkedId());
                    return Response.failed("PA赛事的标准赛事不存在:" + thirdMatchInfo.getReferenceId());
                }
                StandardMatchInfo newStandardMatchInfo = new StandardMatchInfo();
                newStandardMatchInfo.setId(standardMatchInfo.getId());
                newStandardMatchInfo.setMatchLength(newMatchLength);
                newStandardMatchInfo.setModifyTime(System.currentTimeMillis());
                if(changeMatchLengthDto.getMinutes()==9){
                    newStandardMatchInfo.setMatchType(3);
                    thirdMatchInfo.setMatchType(3);
                }else{
                    newStandardMatchInfo.setMatchType(1);
                    thirdMatchInfo.setMatchType(1);
                }
//                standardMatchInfoMapper.updateByPrimaryKey(standardMatchInfo);
                pdMatchInfoRepository.setRedisAndStandardMatchInfo(newStandardMatchInfo, null);
                matchScoresInfo.setT1(0);
                matchScoresInfo.setT2(0);
                matchScoresInfo.setScoresJson(null);
                matchScoresInfo.setPeriodT1(0);
                matchScoresInfo.setPeriodT2(0);

//                matchScoresInfoMapper.updateByPrimaryKey(matchScoresInfo);
                pdMatchInfoRepository.setRedisAndMatchScoresInfo(matchScoresInfo, null);
//                timeInfoMapper.updateByPrimaryKey(timeInfo);
                pdMatchInfoRepository.setRedisAndMatchTimeInfo(timeInfo, null);
//                thirdMatchInfoMapper.updateByPrimaryKey(thirdMatchInfo);
                eventProducer.sendMatchStatusTopic(linkId, thirdMatchInfo, thirdMatchInfo.getMatchStatus());
                log.info("changeMatchLenth  更新赛制 :{}   linkedId:{}", newMatchLength, changeMatchLengthDto.getLinkedId());

                matchScorePdLogService.changeMatchLenthLog(response.getData(),changeMatchLengthDto);
                log.info("changeMatchLenth  结束 :{}   linkedId:{}", changeMatchLengthDto.getLinkedId());
                return Response.success();
            } else {
                throw new RuntimeException("redis锁失败:");
            }
        } catch (Exception e) {
            log.error("::changeMatchLenth::", e);
            return Response.failed(e.getMessage());
        } finally {
            redisService.unLock(key, key);
        }
    }

    @Override
    public Response changeMatchStartTime(ChangeMatchStartTimeDto changeMatchStartTimeDto)
    {
        String linkId = changeMatchStartTimeDto.getLinkedId();
        log.info("::{}::篮球报球板设置开赛时间入参:{}", linkId, JSON.toJSONString(changeMatchStartTimeDto) );
        String key = "PA_createMatchAdvertise:" + changeMatchStartTimeDto.getThirdMatchId();
        try {

            if (redisService.tryLock(key, key, 2, 3)) {
                //1.查询三方赛事-时间-比分
                Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(changeMatchStartTimeDto.getThirdMatchId());
                if (!response.isSuccess()) {
                    return response;
                }
                ThirdMatchInfo thirdMatchInfo = response.getData().getThirdMatchInfo();
                Long oldBeginTime = thirdMatchInfo.getBeginTime();
                MatchTimeInfo timeInfo = response.getData().getMatchTimeInfo();
                //2.如果已经开赛则无法修改
                if (timeInfo.getPeriod() > 0) {
                    return Response.failed("PA赛事已经开赛无法修改开赛时间");
                }
                //3.修改开赛时间
//                StandardMatchInfo standardMatchInfo = standardMatchInfoMapper.selectByPrimaryKey(thirdMatchInfo.getReferenceId());
                StandardMatchInfo standardMatchInfo = pdMatchInfoRepository.getStandardMatchInfo(thirdMatchInfo.getReferenceId(), null);
                if (standardMatchInfo == null) {
                    return Response.failed("PA赛事的标准赛事不存在");
                }
                thirdMatchInfo.setBeginTime(changeMatchStartTimeDto.getStartTime());
                standardMatchInfo.setBeginTime(changeMatchStartTimeDto.getStartTime());
                standardMatchInfo.setBeginTimeStatus(1); //1 为人工更新 0为 系统更新
                standardMatchInfo.setModifyTime(System.currentTimeMillis());
                StandardMatchInfo newStandardMatchInfo = new StandardMatchInfo();
                newStandardMatchInfo.setId(standardMatchInfo.getId());
                newStandardMatchInfo.setBeginTime(changeMatchStartTimeDto.getStartTime());
                newStandardMatchInfo.setBeginTimeStatus(1);
                newStandardMatchInfo.setModifyTime(System.currentTimeMillis());
//                standardMatchInfoMapper.updateByPrimaryKey(standardMatchInfo);
//                thirdMatchInfoMapper.updateByPrimaryKey(thirdMatchInfo);
                pdMatchInfoRepository.setRedisAndStandardMatchInfo(newStandardMatchInfo, null);
                eventProducer.sendMatchStatusTopic(linkId, thirdMatchInfo, thirdMatchInfo.getMatchStatus());
                //通过赛事id查询预开售表数据
                //修改开赛时间要同时修改开售表
//                StandardSportMarketSellExample standardSportMarketSellExample = new StandardSportMarketSellExample();
//                standardSportMarketSellExample.createCriteria().andMatchInfoIdEqualTo(standardMatchInfo.getId());
//                List<StandardSportMarketSell> standardSportMarketSellList = standardSportMarketSellMapper.selectByExample(standardSportMarketSellExample);
//                if (standardSportMarketSellList.size() != 0) {
//                    standardSportMarketSellList.forEach(it -> {
//                        it.setLiveOddTime(changeMatchStartTimeDto.getStartTime());
//                        it.setBeginTime(changeMatchStartTimeDto.getStartTime());
//                        it.setModifyTime(Calendar.getInstance().getTimeInMillis());
//                        standardSportMarketSellMapper.updateByPrimaryKey(it);
//                    });
//                }
                StandardSportMarketSell marketSell = pdMatchInfoRepository.getStandardSportMarketSell(standardMatchInfo.getId(), null);
                if(!ObjectUtils.isEmpty(marketSell)) {
                    marketSell.setLiveOddTime(changeMatchStartTimeDto.getStartTime());
                    marketSell.setBeginTime(changeMatchStartTimeDto.getStartTime());
                    marketSell.setModifyTime(Calendar.getInstance().getTimeInMillis());
                    pdMatchInfoRepository.setRedisAndStandardSportMarketSell(marketSell, null);
                }
                //4.修改标准赛事开赛时间同步下游 kb-bug 2 可能不同步要确认
                //5.返回新的开赛时间

                //6.打印报球版修改赛事开赛时间
                matchScorePdLogService.changeMatchStartTimeLog(changeMatchStartTimeDto, oldBeginTime, standardMatchInfo);
                return Response.success();
            } else {
                throw new RuntimeException("redis锁失败:");
            }
        } catch (Exception e) {
            log.error("::changeMatchStartTime异常::", e);
            return Response.failed(e.getMessage());
        } finally {
            redisService.unLock(key, key);
        }
    }

    @Override
    public Response changeMatchTime(ChangeMatchTimeDto changeMatchTimeDto) {
        log.info("足球报球板，修改时间，页面透传赛事时间={}", changeMatchTimeDto.getMatchTime());
        String key = "PA_createMatchAdvertise:" + changeMatchTimeDto.getThirdMatchId();
        try {
            ThirdMatchInfo thirdMatch = pdMatchInfoRepository.getThirdMatchInfo(changeMatchTimeDto.getThirdMatchId(), null);
            if (Objects.isNull(thirdMatch)) {
                log.error("linkId=::{}::changeMatchTime::三方赛事表里不存在，thirdMatchId:{}",changeMatchTimeDto.getLinkedId(),changeMatchTimeDto.getThirdMatchId());
                return Response.failed("三方赛事表里不存在");
            }
            // 存放事件时间，用于报球板事件监控
            long eventTime=System.currentTimeMillis();
            String actionMonitorKey =String.format(ACTION_MONITER_KEY,thirdMatch.getThirdMatchSourceId());
            if (thirdMatch.getSportId().compareTo(SportTypeEnum.FOOTBALL.getValue()) ==0 && ScoreEventCodeSourceEnum.getResult(thirdMatch.getDataSourceCode())) {
                log.info("::{}::changeMatchTime,key:{},eventTime:{}",changeMatchTimeDto.getLinkedId(),actionMonitorKey,eventTime);
                redisService.set(actionMonitorKey,eventTime);
            }
            if (redisService.tryLock(key, key, 2, 3)) {
                Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(changeMatchTimeDto.getThirdMatchId());
                if (!response.isSuccess()) {
                    return response;
                }
//                String changeKey ="PA_changeMatchTime:"+changeMatchTimeDto.getThirdMatchId();
//                if(!redisService.tryLock(key,key,1,2)){
//                    return Response.failed("后台正在响应,请稍后再试。");
//                }
//                redisService.expire(changeKey,3);
                ThirdMatchInfo thirdMatchInfo = response.getData().getThirdMatchInfo();
                MatchScoresInfo matchScoresInfo = response.getData().getMatchScoresInfo();
                MatchTimeInfo timeInfo = response.getData().getMatchTimeInfo();
                Long secondFromStart = timeInfo.getSecondFromStart();
                //1.先进行阶段判断非滚球开赛阶段无法下发时间修正
                if (timeInfo.getPeriod() <= 0) {
                    return Response.failed("赛事未开始，无法修改进行时间。");
                }
                //95648 【日常】【生产】PA报球板，足球赛事切换阶段，编辑阶段时间不能小于对应阶段的最小时间 只处理足球
                if(changeMatchTimeDto.getSportId()==1 && (null==response.getData().getStandardMatchInfo().getMatchLength() ||
                        0==response.getData().getStandardMatchInfo().getMatchLength()
                    || 46==response.getData().getStandardMatchInfo().getMatchLength())){
                    List<Long> periodList = Arrays.asList(31L,32L,33L,34L,50L,100L,110L,120L);
                    log.info("赛事进行时间校验：{}，{}",timeInfo.getPeriod(),changeMatchTimeDto.getMatchTime());
                    if(timeInfo.getPeriod()==6L && changeMatchTimeDto.getMatchTime()<=0){
                        return Response.failed("赛事进行时间错误，不在有效范围:上半场");
                    }else if(timeInfo.getPeriod()==7L && changeMatchTimeDto.getMatchTime()<=2700){
                        return Response.failed("赛事进行时间错误，不在有效范围:下半场");
                    }else if(timeInfo.getPeriod()==41L || timeInfo.getPeriod()==42L ){
                        if(changeMatchTimeDto.getMatchTime()<=5400){
                            return Response.failed("赛事进行时间错误，不在有效范围:加时赛");
                        }
                    }else if(periodList.contains(timeInfo.getPeriod())){
                        return Response.failed("该阶段不允许修改赛事时间");
                    }
                }
                //记录篮球报球版阶段 77076-bug, 加时赛在当前OT加时时
                if (changeMatchTimeDto.getSportId() != null && changeMatchTimeDto.getSportId() == 2 && changeMatchTimeDto.getMatchTime() != null && changeMatchTimeDto.getMatchTime() == 300) {
                    // 加时赛OT增加加时时间时，清空加时赛缓存暂停次数
                    redisService.del(MATCH_BASKETBALL_PERIOD_OT + "home:" + changeMatchTimeDto.getThirdMatchId());
                    redisService.del(MATCH_BASKETBALL_PERIOD_OT + "away:" + changeMatchTimeDto.getThirdMatchId());

                    Long period = matchScoresInfo.getPeriod();
                    JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
                    Map<Long, BasketballScores> allPeriodScores= JsonMapUtils.parseBasketballMap(periodFootballScores);
                    BasketballScores periodScores= allPeriodScores.get(period);
                    BasketballScores wholeScores= allPeriodScores.get(WHOLE_MATCH);
                    if (period == 40) {
                        BasketballScoresExtra scoresExtra = JSON.parseObject(matchScoresInfo.getScoresJsonExtra(),new TypeReference<BasketballScoresExtra>(){});
                        Map<Long, CommonItem> historyTimeout = scoresExtra.getHistoryTimeout();
                        historyTimeout.put(System.currentTimeMillis(), new CommonItem());
                        scoresExtra.setHistoryTimeout(historyTimeout);
                        scoresExtra.setCurrentTimeout(scoresExtra.getCurrentTimeout() + 1);
                        periodScores.setTimeout(new CommonItem());
                        allPeriodScores.put(period, periodScores);
                        wholeScores.setTimeout(new CommonItem());
                        allPeriodScores.put(WHOLE_MATCH,wholeScores);
                        matchScoresInfo.setScoresJsonExtra(JSONObject.toJSONString(scoresExtra));
                        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
                        matchScoresInfo.setModifyTime(System.currentTimeMillis());
                        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
                    }
                }

                // 下发赔率服务
                commonEventService.sendChangeMatchTimeInfo(changeMatchTimeDto, timeInfo, thirdMatchInfo);

                //2.针对时间做计算，得到修改后的时间
                long currentTime = System.currentTimeMillis();
                Long matchTime = changeMatchTimeDto.getMatchTime();
                timeInfo.setSecondFromStart(matchTime);
                timeInfo.setRemainingTime(matchTime);
                timeInfo.setEventTime(currentTime);
                timeInfo.setModifyTime(currentTime);
                matchScoresInfo.setEventTime(currentTime);
                matchScoresInfo.setSecondsMatchStart(matchTime);
                matchScoresInfo.setRemainingTime(matchTime);
                matchScoresInfo.setModifyTime(currentTime);
//                matchScoresInfoMapper.updateByPrimaryKey(matchScoresInfo);
//                timeInfoMapper.updateByPrimaryKey(timeInfo);

                //3.调用公共时间接口做时间下发
                commonEventService.updateBasketballMatchTimeEvent(response.getData(), timeInfo.getPeriod(), timeInfo.getSecondFromStart(), timeInfo.getSecondFromStart(), System.currentTimeMillis(), timeInfo.getTimeGo(), changeMatchTimeDto);
                if (thirdMatchInfo.getSportId().equals(1L)) {
                    redisUtils.pushFootBallScore(thirdMatchInfo.getId());
                    // 更新缓存当前eventCode
                    String eventCodeKey = MATCH_ADVERTIS_EVENT_STATUS + changeMatchTimeDto.getThirdMatchId();
                    Object cacheEventStatus = redisService.get(eventCodeKey);
                    if (cacheEventStatus != null) {
                        try {
                            FootballMatchEventStatusVo footballMatchEventStatusVo = JSONObject.toJavaObject(JSONObject.parseObject(cacheEventStatus.toString()), FootballMatchEventStatusVo.class);
                            footballMatchEventStatusVo.setCurrentEventCode("ball_safe");
                            redisService.set(MATCH_ADVERTIS_EVENT_STATUS + changeMatchTimeDto.getThirdMatchId(), JSON.toJSONString(footballMatchEventStatusVo));
                        } catch (Exception e) {
                            log.error("buildCacheMatchStatus error::", e);
                        }
                    }
                }
//                redisService.unLock(changeKey,changeKey);
                matchScorePdLogService.changeMatchTimeLog(response.getData(), secondFromStart, changeMatchTimeDto);
                return Response.success();
            } else {
                throw new RuntimeException("redis锁失败:");
            }
        } catch (Exception e) {
            log.error("::changeMatchTime异常::", e);

            return Response.failed(e.getMessage());
        } finally {
            redisService.unLock(key, key);
        }
    }

    @Override
    public Response changeMatchStatus(ChangeMatchStatusDto changeMatchStatus) {
        String linkId = changeMatchStatus.getLinkedId();
        log.info("::{}::changeMatchStatus的入参:{}", linkId, JSON.toJSONString(changeMatchStatus) );
        String key = "PA_createMatchAdvertise:" + changeMatchStatus.getThirdMatchId();
        try {
            if (redisUtils.checkRequestLinkId(changeMatchStatus.getLinkedId())) {
                return Response.failed("该linkId已被消费");
            }
            if (redisService.tryLock(key, key, 2, 3)) {
                Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(changeMatchStatus.getThirdMatchId());
                if (!response.isSuccess()) {
                    return response;
                }
                if (changeMatchStatus.getControlType() == null || changeMatchStatus.getControlType() > 4 || changeMatchStatus.getControlType() < 1) {
                    return Response.failed("操作不符合定义");
                }
                MatchTimeInfo matchTimeInfo = response.getData().getMatchTimeInfo();
                String changeKey = "PA_changeMatchStatus:" + changeMatchStatus.getThirdMatchId();
                if (!redisService.tryLock(changeKey, changeKey, 1, 2)) {
                    return Response.failed("当前赛事事件正在操作,请稍后再试。");
                }
                redisService.expire(changeKey, 1);
                if (ObjectUtils.isEmpty(matchTimeInfo.getMatchLength())) {
                    matchTimeInfo.setMatchLength(0);
                    matchTimeInfoRepository.updateByPrimaryKey(matchTimeInfo);
                    response.getData().setMatchTimeInfo(matchTimeInfo);
                    MatchScoresInfo matchScoresInfo = response.getData().getMatchScoresInfo();
                    matchScoresInfo.setMatchLength(0);
                    matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
                    response.getData().setMatchScoresInfo(matchScoresInfo);
                }
                //1.开始
                if (MATCH_START.equals(changeMatchStatus.getControlType()) ) {
                    if (BasketBallConstant.BASKETL_BALL_PERIODS.contains(matchTimeInfo.getPeriod())) {
                        return Response.failed("操作不符合定义");
                    }
                    changeMatchStatus.setPeriodId(matchTimeInfo.getPeriod());
                    // 篮球3*3则所有下发阶段改为21
                    if ( 73 == matchTimeInfo.getMatchLength() )
                    {
//                        matchTimeInfo.setPeriod(21L);
//                        matchTimeInfo.setModifyTime(System.currentTimeMillis());
//                        timeInfoMapper.updateByPrimaryKey(matchTimeInfo);
//                        response.getData().getMatchTimeInfo().setPeriod(21L);
//
//                        response.getData().getMatchScoresInfo().setPeriod(21L);
//                        matchScoresInfoMapper.updateByPrimaryKey(response.getData().getMatchScoresInfo());
//
//                        ThirdMatchInfo thirdMatchInfo = response.getData().getThirdMatchInfo();
//                        thirdMatchInfo.setMatchPeriod("21");
//                        thirdMatchInfo.setModifyTime(System.currentTimeMillis());
//                        thirdMatchInfoMapper.updateByPrimaryKey(thirdMatchInfo);
//                        response.getData().getThirdMatchInfo().setMatchPeriod("21");
//
//                        if ( null != thirdMatchInfo && null != thirdMatchInfo.getReferenceId() && thirdMatchInfo.getReferenceId() > 0 )
//                        {
//                            StandardMatchInfo standardMatchInfo = standardMatchInfoMapper.selectByPrimaryKey(thirdMatchInfo.getReferenceId());
//                            if ( null != standardMatchInfo )
//                            {
//                                standardMatchInfo.setMatchPeriodId(21L);
//                                standardMatchInfo.setModifyTime(System.currentTimeMillis());
//                                standardMatchInfoMapper.updateByPrimaryKey(standardMatchInfo);
//                            }
//                        }
                    }
                    //投递事件消息到实时服务：THIRD_MATCH_EVENT_INFO_API
                    Response r = basketBallAdvertiseService.matchStart(response.getData(), linkId);
                    // 每次切换阶段进入加时赛时
                    if (changeMatchStatus.getPeriodId() != null && changeMatchStatus.getPeriodId() == 40L && changeMatchStatus.getControlType() != null && changeMatchStatus.getControlType() == 1) {
                        MatchScoresInfo matchScoresInfo = matchScoreInfoRepository.selectByExample(changeMatchStatus.getThirdMatchId(), SourceTypeEnum.LIVE_DATA.getCode());
                        response.getData().setMatchScoresInfo(matchScoresInfo);
                    }
                    if (r.isSuccess()) {
                        redisUtils.cacheRequestLinkId(changeMatchStatus.getLinkedId());
                        if (null == changeMatchStatus.getIsJump() || changeMatchStatus.getIsJump() != 1) {
                            matchScorePdLogService.changeMatchStatusLog(response.getData(), changeMatchStatus);
                        }
                        scoresProducer.sendToMQ(response.getData().getThirdMatchInfo(),response.getData().getMatchScoresInfo(),changeMatchStatus.getLinkedId());
                        MatchScoresInfo matchScoresInfo = scoresService.checkBasketPeriodAndSixScore(response.getData() ,response.getData().getMatchTimeInfo().getPeriod(),response.getData().getMatchTimeInfo().getSecondFromStart());
                        response.getData().setMatchScoresInfo(matchScoresInfo);
                        return commonAdvertiseService.changeMatchStartStatus(response.getData().getThirdMatchInfo(),linkId);
                    }
                }

                //2.暂停
                if (MATCH_PAUSE.equals(changeMatchStatus.getControlType())) {
                    Response matchPauseResponse = basketBallAdvertiseService.matchPause(response.getData(), changeMatchStatus.getLinkedId());
                    matchScorePdLogService.changeMatchStatusLog(response.getData(), changeMatchStatus);
                    scoresProducer.sendToMQ(response.getData().getThirdMatchInfo(),response.getData().getMatchScoresInfo(),changeMatchStatus.getLinkedId());
                    redisUtils.cacheRequestLinkId(changeMatchStatus.getLinkedId());
                    MatchScoresInfo matchScoresInfo = scoresService.checkBasketPeriodAndSixScore(response.getData() ,response.getData().getMatchTimeInfo().getPeriod(),response.getData().getMatchTimeInfo().getSecondFromStart());
                    return matchPauseResponse;
                }
                //3.继续
                if (MATCH_CONTINUE.equals(changeMatchStatus.getControlType())) {
                    Response continueResponse = basketBallAdvertiseService.matchContinue(response.getData(), changeMatchStatus.getLinkedId());
                    matchScorePdLogService.changeMatchStatusLog(response.getData(), changeMatchStatus);
                    scoresProducer.sendToMQ(response.getData().getThirdMatchInfo(),response.getData().getMatchScoresInfo(),changeMatchStatus.getLinkedId());
                    redisUtils.cacheRequestLinkId(changeMatchStatus.getLinkedId());
                    return continueResponse;
                }
                //4.结束
                if (MATCH_END.equals(changeMatchStatus.getControlType())) {
                    Response matchEndResponse = basketBallAdvertiseService.matchEnd(response.getData(), changeMatchStatus.getLinkedId());
                    matchScorePdLogService.changeMatchStatusLog(response.getData(), changeMatchStatus);
                    scoresProducer.sendToMQ(response.getData().getThirdMatchInfo(),response.getData().getMatchScoresInfo(),changeMatchStatus.getLinkedId());
                    redisUtils.cacheRequestLinkId(changeMatchStatus.getLinkedId());
                    return matchEndResponse;
                }
                return Response.failed();
            } else {
                return Response.failed("当前赛事事件正在操作,请稍后再试:");
            }
        } catch (Exception e) {
            log.error(":处理数据发生异常,error：", e);

            return Response.failed(e.getMessage());
        } finally {
            redisService.unLock(key, key);
        }
    }

    @Override
    public Response changeMatchPeriod(ChangeMatchPeriodDto changeMatchPeriodDto) {
        String key = "PA_createMatchAdvertise:" + changeMatchPeriodDto.getThirdMatchId();
        try {
            if (redisUtils.checkRequestLinkId(changeMatchPeriodDto.getLinkedId())) {
                return Response.failed("该linkId已被消费");
            }
            if (redisService.tryLock(key, key, 2, 3)) {
                Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(changeMatchPeriodDto.getThirdMatchId());
                if (!response.isSuccess()) {
                    return response;
                }
                // 由加时赛切换阶段到非加时赛，清空加时赛缓存暂停次数
                if (response.getData().getMatchScoresInfo().getPeriod() == 40L && changeMatchPeriodDto.getPeriodId() != 40L && response.getData().getMatchScoresInfo().getSportId() == 2L) {
                    redisService.del(MATCH_BASKETBALL_PERIOD_OT + "home:" + changeMatchPeriodDto.getThirdMatchId());
                    redisService.del(MATCH_BASKETBALL_PERIOD_OT + "away:" + changeMatchPeriodDto.getThirdMatchId());
                }
                Long periodId = response.getData().getStandardMatchInfo().getMatchPeriodId();
                response.getData().getMatchTimeInfo().setRestTime(changeMatchPeriodDto.getRestTime());
                Response changeMatchPeriodResponse = basketBallAdvertiseService.changeMatchPeriod(response.getData(), changeMatchPeriodDto.getPeriodId(), changeMatchPeriodDto.getLinkedId());
                matchScorePdLogService.changeMatchPeriodLog(response.getData(),periodId, changeMatchPeriodDto);
                MatchScoresInfo matchScoresInfo = scoresService.checkBasketPeriodAndSixScore(response.getData() ,response.getData().getMatchTimeInfo().getPeriod(),response.getData().getMatchTimeInfo().getSecondFromStart());
                redisUtils.cacheRequestLinkId(changeMatchPeriodDto.getLinkedId());
                return changeMatchPeriodResponse;
            } else {
                throw new RuntimeException("redis锁失败:");
            }
        } catch (Exception e) {
            log.error(":处理数据发生异常,error：", e);

            return Response.failed(e.getMessage());
        } finally {
            redisService.unLock(key, key);
        }
    }

    @Override
    public Response setMatchEnd(ChangeMatchStatusDto changeMatchStatus) {
        String key = "PA_createMatchAdvertise:" + changeMatchStatus.getThirdMatchId();
        try {

            if (redisService.tryLock(key, key, 2, 3)) {
                Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(changeMatchStatus.getThirdMatchId());
                if (!response.isSuccess()) {
                    return response;
                }
                Response<MatchScoreAndTimeVo> matchEndResponse = basketBallAdvertiseService.match999End(response.getData(), changeMatchStatus);

                matchScorePdLogService.setMatchEndLog(response.getData(), changeMatchStatus);
                return matchEndResponse;
            } else {
                throw new RuntimeException("redis锁失败:");
            }
        } catch (Exception e) {
            log.error(":处理数据发生异常,error：", e);

            return Response.failed(e.getMessage());
        } finally {
            redisService.unLock(key, key);
        }
    }

    @Override
    public Response changeMatchScore(ChangeMatchScoreDto changeMatchScoreDto) {
        log.info("::{}::changeMatchScore的入参:{}", changeMatchScoreDto.getLinkedId(), JSON.toJSONString(changeMatchScoreDto) );
        String key = "PA_createMatchAdvertise:" + changeMatchScoreDto.getThirdMatchId();
        try {

            if (redisService.tryLock(key, key, 2, 3)) {
                Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(changeMatchScoreDto.getThirdMatchId());
                Long startTimeSecond = getMatchTime(response);
                changeMatchScoreDto.setMatchTime(startTimeSecond);
                if (!response.isSuccess()) {
                    return response;
                }
                if (!SportPeriodConstant.BasketballPeriod.contans(changeMatchScoreDto.getPeriod(), response.getData().getMatchTimeInfo().getMatchLength())) {
                    return Response.failed("比分变更阶段错误");
                }
                MatchScoreAndTimeVo oldMatchScoreAndTimeVo = new MatchScoreAndTimeVo();
                oldMatchScoreAndTimeVo = response.getData();
                //1.修改当前阶段比分必须1分 2分 3分的改
                Long periodNow = response.getData().getMatchTimeInfo().getPeriod();
                if (!MatchPeriodUtils.BascketBallPeriod.comparePeriodIndex(periodNow, changeMatchScoreDto.getPeriod(), response.getData().getMatchTimeInfo().getMatchLength())) {
                    return Response.failed("该节还未开打无法变更比分");
                }
//                if(changeMatchScoreDto.getPeriod().equals(periodNow)){
//                    return Response.failed("不能跨阶段改比分");
//                }
                Map<String,String> oldScoreMap = new LinkedHashMap<>();
                Integer periodT1Old = response.getData().getMatchScoresInfo().getPeriodT1();
                Integer periodT2Old = response.getData().getMatchScoresInfo().getPeriodT2();
                oldScoreMap.put("periodT1Old",periodT1Old.toString());
                oldScoreMap.put("periodT2Old",periodT2Old.toString());
                oldScoreMap.put("scoresJson",response.getData().getMatchScoresInfo().getScoresJson());
                if (periodNow.equals(changeMatchScoreDto.getPeriod())) {
                    if (changeMatchScoreDto.getPeriodT1().equals(periodT1Old) && changeMatchScoreDto.getPeriodT2().equals(periodT2Old)) {
                        return Response.failed("设置的比分和原来比分相等");
                    }
                }
                MatchScoresInfo matchScoresInfo = scoresService.checkBasketPeriodAndSixScore(response.getData() ,response.getData().getMatchTimeInfo().getPeriod(),response.getData().getMatchTimeInfo().getSecondFromStart());
                log.info("::{}::PD篮球报球板-初始化-比分计算时优先取缓存阶段比分,period={},入参阶段={}", matchScoresInfo.getThirdMatchId(), matchScoresInfo.getPeriod(),changeMatchScoreDto.getPeriod());
                response.getData().setMatchScoresInfo(matchScoresInfo);
                //2.修改当前阶段比分必须只能改一个球队比分
                Response matchScoreResponse = basketBallAdvertiseService.changeMatchScore(response.getData(), changeMatchScoreDto);
                matchScorePdLogService.changeMatchScoreLog(oldScoreMap,oldMatchScoreAndTimeVo, changeMatchScoreDto);
                return matchScoreResponse;
            } else {
                throw new RuntimeException("redis锁失败:");
            }
        } catch (Exception e) {
            log.error(":处理数据发生异常,error：", e);

            return Response.failed(e.getMessage());
        } finally {
            redisService.unLock(key, key);
        }
    }

    @Override
    public Response getMatchAdvertiseInfo(MatchAdvertiseQueryDto matchAdvertiseQueryDto) {
        try {
            Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(matchAdvertiseQueryDto.getThirdMatchId());
            if (!response.isSuccess()) {
                return response;
            }
            return basketBallAdvertiseService.buildBasketBallAdvertiseVo(response.getData());
        } catch (Exception e) {
            log.error("PA赛事报球版查询异常::{}::", e);

            return Response.failed(e.getMessage());
        }
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
     * PD报球版篮球事件消息查询
     * @param matchAdvertiseQueryDto
     * @return
     */
    @Override
    public Response getMatchEventMessage(MatchAdvertiseQueryDto matchAdvertiseQueryDto) {

        try {
            Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(matchAdvertiseQueryDto.getThirdMatchId());
            if (!response.isSuccess()) {
                return response;
            }
            MatchScoresEventInfoExample matchScoresEventInfoExample = new MatchScoresEventInfoExample();
            matchScoresEventInfoExample.createCriteria().andThirdMatchSourceIdEqualTo(response.getData().getThirdMatchInfo().getThirdMatchSourceId())
                    .andSportIdEqualTo(SportTypeEnum.BASKETBALL.getValue()).andEventCodeIsNotNull();
            matchScoresEventInfoExample.setOrderByClause("create_time");
            List<MatchScoresEventInfo> matchScoresEventInfoList = matchScoresEventInfoMapper.selectByExample(matchScoresEventInfoExample);
            List<MatchEventInfoDto> matchEventInfoDtoList = new LinkedList<>();
            for (MatchScoresEventInfo matchScoresEventInfo:matchScoresEventInfoList){
                String matchEventJson = JSONObject.toJSONString(matchScoresEventInfo);
                MatchEventInfoDto matchEventInfoDto = JSONObject.parseObject(matchEventJson,MatchEventInfoDto.class);
                String matchPeriodName = null;
                switch (matchAdvertiseQueryDto.getLanguage()){
                    case "zs":
                        matchPeriodName = PDOperateLogEnum.getCnNameByCode(matchScoresEventInfo.getSportId()+""+matchScoresEventInfo.getMatchPeriodId());
                        break;
                    case "en":
                        matchPeriodName = PDOperateLogEnum.getEnNameByCode(matchScoresEventInfo.getSportId()+""+matchScoresEventInfo.getMatchPeriodId());
                        break;
                    default:break;
                }
                matchEventInfoDto.setMatchPeriodName(matchPeriodName);
                if (matchScoresEventInfo.getSecondsFromStart()!=null){
                    Long minus = 0L;
                    Long seconds = 0L;
                    minus = (matchScoresEventInfo.getSecondsFromStart()/60)%60;
                    seconds = matchScoresEventInfo.getSecondsFromStart()%60;
                    matchEventInfoDto.setSecondsFromStart(minus + ":" + seconds);
                }
                matchEventInfoDtoList.add(matchEventInfoDto);
            }
            return Response.success(matchEventInfoDtoList);
        } catch (Exception e) {
            log.error("::{}::", e);
            e.printStackTrace();
            return Response.failed(e.getMessage());
        }
    }


}
